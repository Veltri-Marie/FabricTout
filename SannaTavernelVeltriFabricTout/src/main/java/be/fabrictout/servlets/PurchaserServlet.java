package be.fabrictout.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.fabrictout.connection.FabricToutConnection;
import be.fabrictout.dao.MachineDAO;
import be.fabrictout.dao.SiteDAO;
import be.fabrictout.dao.ZoneDAO;
import be.fabrictout.pojo.Machine;
import be.fabrictout.pojo.Site;
import be.fabrictout.pojo.Type;
import be.fabrictout.pojo.State;
import be.fabrictout.pojo.Zone;

public class PurchaserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection conn;
    private MachineDAO machineDAO;
    private SiteDAO siteDAO;
    private ZoneDAO zoneDAO;
    private static final double MAX_MACHINE_SIZE = 25.0;

    @Override
    public void init() throws ServletException {
        super.init();
        conn = FabricToutConnection.getInstance();
        machineDAO = new MachineDAO(conn);
        siteDAO = new SiteDAO(conn);
        zoneDAO = new ZoneDAO(conn);
    }

    public PurchaserServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("viewMachineHistory".equals(action)) {
        	System.out.println("PurchaserServlet : viewMachineHistory");
            viewMachineHistory(request, response);
        } else if ("orderMachine".equals(action)) {
        	System.out.println("PurchaserServlet : orderMachine");
        	processMachineOrder(request, response);
        } else {
        	System.out.println("PurchaserServlet : loadAllMachines");
            loadAllMachines(request, response);
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("submitOrder".equals(action)) {
        	System.out.println("PurchaserServlet : processMachineOrder");
            processMachineOrder(request, response);  
        } else {
        	System.out.println("PurchaserServlet : doGet");
            doGet(request, response);
        }
    }


    private void loadAllMachines(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        try {
            List<Machine> machines = Machine.findAll(machineDAO);
            request.setAttribute("machines", machines);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineList.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading the machine list.");
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineList.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void viewMachineHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String machineIdParam = request.getParameter("machineId");

        if (machineIdParam != null) {
            try {
                Machine machine = Machine.find(machineDAO, Integer.parseInt(machineIdParam));

                request.setAttribute("machine", machine);
                request.setAttribute("maintenanceHistory", machine.getMaintenances());

                if (machine.getMaintenances() != null && machine.getMaintenances().size() > 6) {
                    request.setAttribute("showReorderButton", true);
                } else {
                    request.setAttribute("showReorderButton", false);
                }

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineHistory.jsp");
                dispatcher.forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "An error occurred while fetching machine maintenance history.");
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineHistory.jsp");
                dispatcher.forward(request, response);
            }
        }
    }    

    private void processMachineOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String machineIdParam = request.getParameter("machineId");

        if (machineIdParam != null) {
            try {
                Machine machine = Machine.find(machineDAO, Integer.parseInt(machineIdParam));
                if (machine == null) {
                    request.setAttribute("error", "Machine not found.");
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineHistory.jsp");
                    dispatcher.forward(request, response);
                    return;
                }

                if (machine.getMaintenances() != null && machine.getMaintenances().size() > 6) {
                    Machine newMachine = new Machine(Machine.getNextId(machineDAO), machine.getType(), machine.getSize(), State.OPERATIONAL, machine.getSite());

                    for (Zone zone : machine.getZones()) {
                        newMachine.addZone(zone);
                    }

                    boolean success = newMachine.create(machineDAO);

                    if (success) {
                    	machine.delete(machineDAO);

                        request.setAttribute("success", "Machine successfully re-ordered.");
                        response.sendRedirect("Purchaser");
                    } else {
                        request.setAttribute("error", "Error re-ordering the machine.");
                        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineHistory.jsp");
                        dispatcher.forward(request, response);
                    }
                } else {
                    request.setAttribute("error", "The machine does not have enough maintenance records to qualify for re-order.");
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineHistory.jsp");
                    dispatcher.forward(request, response);
                }

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "An error occurred while processing the order.");
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineHistory.jsp");
                dispatcher.forward(request, response);
            }
        } else {
            request.setAttribute("error", "Machine ID is missing.");
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineHistory.jsp");
            dispatcher.forward(request, response);
        }
    }
}
