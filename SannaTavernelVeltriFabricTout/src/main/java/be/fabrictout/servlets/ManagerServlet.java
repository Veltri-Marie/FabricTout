package be.fabrictout.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.fabrictout.connection.FabricToutConnection;
import be.fabrictout.dao.MachineDAO;
import be.fabrictout.dao.MaintenanceDAO;
import be.fabrictout.dao.ManagerDAO;
import be.fabrictout.dao.WorkerDAO;
import be.fabrictout.javabeans.Machine;
import be.fabrictout.javabeans.Maintenance;
import be.fabrictout.javabeans.Manager;
import be.fabrictout.javabeans.State;
import be.fabrictout.javabeans.Status;
import be.fabrictout.javabeans.Worker;

public class ManagerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection conn;
    private MachineDAO machineDAO;
    private MaintenanceDAO maintenanceDAO;
    private WorkerDAO workerDAO;
    private ManagerDAO managerDAO;
    Manager currentManager = null;
    ArrayList<String> errors = new ArrayList<String>();

    @Override
    public void init() throws ServletException {
        super.init();
        conn = FabricToutConnection.getInstance();
        machineDAO = new MachineDAO(conn);
        maintenanceDAO = new MaintenanceDAO(conn);
        workerDAO = new WorkerDAO(conn);
        managerDAO = new ManagerDAO(conn);
    }

    public ManagerServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	HttpSession session = request.getSession();
        Object idManagerObj = session.getAttribute("idEmployee"); 

        errors.add("You must be logged in to access this page.");
        if (idManagerObj == null) {
            request.setAttribute("errors", errors);
            forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
            return;
        }

        int idManager = (int) idManagerObj; 
                 
        currentManager = Manager.find(managerDAO, idManager);
         
        String action = request.getParameter("action");

        try {
            if ("reportMachineMaintenance".equals(action)) {
                reportMachineMaintenance(request, response);
            } else if ("seeMaintenances".equals(action)) {
                seeMaintenances(request, response);
            } else if ("validate".equals(action)) {
                validCompletedMaintenance(request, response);
            } else {
                loadAllMachines(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while processing the request.");
            forwardToPage(request, response, "/WEB-INF/views/manager/welcome.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void loadAllMachines(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Machine> machines = Machine.findAll(machineDAO);
            List<Machine> machinesManagerSite = new ArrayList<>();
            
			for (Machine machine : machines) {
				System.out.println("****AllMachineZones"+machine.getZones().get(0).getSite().getIdSite());
				System.out.println("****CurrentManagerSite"+currentManager.getSite().getIdSite());
				if (machine.getZones().get(0).getSite().getIdSite()  == currentManager.getSite().getIdSite()) {
					machinesManagerSite.add(machine);
				}
			}
            request.setAttribute("machines", machinesManagerSite);
            forwardToPage(request, response, "/WEB-INF/views/manager/welcome.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading the machine list.");
            forwardToPage(request, response, "/WEB-INF/views/manager/welcome.jsp");
        }
    }

    private void reportMachineMaintenance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMachine = Integer.parseInt(request.getParameter("idMachine"));
            Machine machine = Machine.find(machineDAO, idMachine);
            List<Worker> workers = Worker.findAll(workerDAO);
            List<Worker> workersSite = new ArrayList<>();

            if (machine != null && machine.getZones() != null && !machine.getZones().isEmpty()) {
                int siteId = machine.getZones().get(0).getSite().getIdSite();

                for (Worker worker : workers) {
                    if (worker.getSite() != null && worker.getSite().getIdSite() == siteId) {
                        workersSite.add(worker);
                    }
                }


                if ("OPERATIONAL".equals(machine.getState().toString())) {
                    machine.setState(State.valueOf("NEEDS_MAINTENANCE"));
                    machine.update(machineDAO);

                    int idMaintenance = Maintenance.getNextId(maintenanceDAO);
                    Maintenance maintenance = new Maintenance(
                            idMaintenance, LocalDate.now(), 0, "", Status.IN_PROGRESS, machine, currentManager, workersSite);
                    maintenance.create(maintenanceDAO);

                    request.setAttribute("success", "Maintenance successfully reported for Machine ID: " + machine.getIdMachine());
                } else {
                    request.setAttribute("error", "Only machines in OPERATIONAL state can be reported for maintenance.");
                }
            } else {
                request.setAttribute("error", "Machine or associated site not found.");
            }
            loadAllMachines(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error reporting machine maintenance.");
            loadAllMachines(request, response);
        }
    }

    private void seeMaintenances(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMachine = Integer.parseInt(request.getParameter("idMachine"));
            Machine machine = Machine.find(machineDAO, idMachine);

            if (machine != null) {
                List<Maintenance> maintenances = machine.getMaintenances();
                request.setAttribute("machine", machine);
                request.setAttribute("maintenances", maintenances);
                forwardToPage(request, response, "/WEB-INF/views/manager/seeMaintenances.jsp");
            } else {
                request.setAttribute("error", "Machine not found.");
                loadAllMachines(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error retrieving maintenances.");
            loadAllMachines(request, response);
        }
    }

    private void validCompletedMaintenance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMachine = Integer.parseInt(request.getParameter("idMachine"));
            int idMaintenance = Integer.parseInt(request.getParameter("idMaintenance"));
            Machine machine = Machine.find(machineDAO, idMachine);
            Maintenance maintenance = Maintenance.find(maintenanceDAO, idMaintenance);

            if (machine != null && "NEEDS_MAINTENANCE".equals(machine.getState().toString())
                    && maintenance != null && "WAITING".equals(maintenance.getStatus().toString())) {
                
                maintenance.setStatus(Status.valueOf("COMPLETED"));
                maintenance.update(maintenanceDAO);

                machine.setState(State.valueOf("OPERATIONAL"));
                machine.update(machineDAO);

                request.setAttribute("success", "Maintenance validated successfully.");
            } else {
                request.setAttribute("error", "Only 'WAITING' maintenances for machines in 'NEEDS_MAINTENANCE' state can be validated.");
            }
            seeMaintenances(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error validating maintenance.");
            loadAllMachines(request, response);
        }
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}
