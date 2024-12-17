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
import javax.servlet.http.HttpSession;

import be.fabrictout.connection.FabricToutConnection;
import be.fabrictout.dao.MachineDAO;
import be.fabrictout.dao.MaintenanceDAO;
import be.fabrictout.dao.WorkerDAO;
import be.fabrictout.javabeans.Maintenance;
import be.fabrictout.javabeans.Status;
import be.fabrictout.javabeans.Worker;

public class WorkerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection conn;
    private WorkerDAO workerDAO;
    private MachineDAO machineDAO;
    private MaintenanceDAO maintenanceDAO;
    private Worker currentWorker = null;
    ArrayList<String> errors = new ArrayList<String>();


    @Override
    public void init() throws ServletException {
        super.init();
        conn = FabricToutConnection.getInstance();
        workerDAO = new WorkerDAO(conn);
        machineDAO = new MachineDAO(conn);
        maintenanceDAO = new MaintenanceDAO(conn);
    }

    public WorkerServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	HttpSession session = request.getSession();
        Object idWorkerObj = session.getAttribute("idEmployee"); 

        errors.add("You must be logged in to access this page.");
        if (idWorkerObj == null) {
            request.setAttribute("errors", errors);
            forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
            return;
        }

        int idWorker = (int) idWorkerObj; 
        
        currentWorker = Worker.find(workerDAO, idWorker);
    			
        String action = request.getParameter("action");

        try {
            if ("reportCompletedMaintenance".equals(action)) {
                showForm(request, response);
			} else if ("report".equals(action)) {
				reportCompletedMaintenance(request, response);
			}
            else {
                loadAllMaintenances(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while processing the request.");
            forwardToPage(request, response, "/WEB-INF/views/worker/welcome.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void loadAllMaintenances(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Maintenance> maintenances = Maintenance.findAll(maintenanceDAO);
            List<Maintenance> workerMaintenances = new ArrayList<>();

            if (maintenances != null) {
                for (Maintenance maintenance : maintenances) {
                    for (Worker worker : maintenance.getWorkers()) {
                        if (worker.getIdPerson() == currentWorker.getIdPerson()) { 
                            workerMaintenances.add(maintenance);
                        }
                    }
                }
            }
            request.setAttribute("maintenances", workerMaintenances);
            forwardToPage(request, response, "/WEB-INF/views/worker/welcome.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading the maintenance list.");
            forwardToPage(request, response, "/WEB-INF/views/worker/welcome.jsp");
        }
    }
    
    private void showForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMaintenance = Integer.parseInt(request.getParameter("idMaintenance"));
            Maintenance maintenance = Maintenance.find(maintenanceDAO, idMaintenance);

            if (maintenance != null && "IN_PROGRESS".equals(maintenance.getStatus().toString())) {
                request.setAttribute("maintenance", maintenance); 
                forwardToPage(request, response, "/WEB-INF/views/worker/reportCompleteMaintenances.jsp");
            } else {
                request.setAttribute("error", "Only maintenances in 'IN_PROGRESS' status can be reported.");
                loadAllMaintenances(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error preparing maintenance completion form.");
            loadAllMaintenances(request, response);
        }
    }


    private void reportCompletedMaintenance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMaintenance = Integer.parseInt(request.getParameter("idMaintenance"));
            Maintenance maintenance = Maintenance.find(maintenanceDAO, idMaintenance);
            
            String report = request.getParameter("report");
            int duration = Integer.parseInt(request.getParameter("duration"));
            

            if ( "NEEDS_MAINTENANCE".equals(maintenance.getMachine().getState().toString())
                    && maintenance != null && "IN_PROGRESS".equals(maintenance.getStatus().toString())) {
                maintenance.setStatus(Status.WAITING); 
                maintenance.setDuration(duration);
                maintenance.setReport(report);
                
                maintenance.update(maintenanceDAO);


                request.setAttribute("success", "Maintenance successfully reported");
            } else {
                request.setAttribute("error", "Only machines in NEEDS_MAINTENANCE and maintenance in IN_PROGRESS can be reported.");
            }

            loadAllMaintenances(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error reporting machine maintenance.");
            loadAllMaintenances(request, response);
        }
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}
