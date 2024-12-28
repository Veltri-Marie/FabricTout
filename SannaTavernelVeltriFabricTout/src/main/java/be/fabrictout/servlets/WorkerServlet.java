package be.fabrictout.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.fabrictout.connection.FabricToutConnection;
import be.fabrictout.dao.EmployeeDAO;
import be.fabrictout.dao.MachineDAO;
import be.fabrictout.dao.MaintenanceDAO;
import be.fabrictout.dao.WorkerDAO;
import be.fabrictout.javabeans.Employee;
import be.fabrictout.javabeans.Maintenance;
import be.fabrictout.javabeans.Purchaser;
import be.fabrictout.javabeans.Status;
import be.fabrictout.javabeans.Type;
import be.fabrictout.javabeans.Worker;


public class WorkerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection conn;
    private WorkerDAO workerDAO;
    private MachineDAO machineDAO;
    private MaintenanceDAO maintenanceDAO;
    private EmployeeDAO employeeDAO = null;
    private Worker currentWorker = null;
    List<String> errors = new ArrayList<String>();
    List<String> successes = new ArrayList<String>();


    @Override
    public void init() throws ServletException {
        super.init();
        conn = FabricToutConnection.getInstance();
        workerDAO = new WorkerDAO(conn);
        machineDAO = new MachineDAO(conn);
        maintenanceDAO = new MaintenanceDAO(conn);
        employeeDAO = new EmployeeDAO(conn);
    }

    public WorkerServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	errors.clear();
        successes.clear();
    	
    	HttpSession session = request.getSession();
        Object idWorkerObj = session.getAttribute("idEmployee"); 

        errors.add("You must be logged in to access this page.");
        if (idWorkerObj == null) {
            request.setAttribute("errors", errors);
            forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
            return;
        }
        else if (!"Worker".equals(Employee.findTypeById(employeeDAO, (int) idWorkerObj))) {
			errors.add("You must be a worker to access this page.");
			request.setAttribute("errors", errors);
			forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
			return;
		}

        int idWorker = (int) idWorkerObj; 
        
        currentWorker = Worker.find(workerDAO, idWorker);
        
        if (currentWorker != null) {
            session.setAttribute("firstName", currentWorker.getFirstName());
        }
    			
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
            errors.add("An error occurred while processing the request.");
            request.setAttribute("errors", errors);
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
            List<Maintenance> sortedMaintenances = workerMaintenances.stream()
                    .sorted(Comparator
                            .comparing((Maintenance m) -> !(m.getStatus() == Status.IN_PROGRESS || m.getStatus() == Status.REJECTED))
                            .thenComparing((Maintenance m) -> m.getStatus() != Status.WAITING)
                            .thenComparing(Maintenance::getIdMaintenance))
                    .collect(Collectors.toList());

            request.setAttribute("maintenances", sortedMaintenances);
            forwardToPage(request, response, "/WEB-INF/views/worker/welcome.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("An error occurred while loading the maintenance list.");
            request.setAttribute("errors", errors);
            forwardToPage(request, response, "/WEB-INF/views/worker/welcome.jsp");
        }
    }
    
    private void showForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMaintenance = Integer.parseInt(request.getParameter("idMaintenance"));
            Maintenance maintenance = Maintenance.find(maintenanceDAO, idMaintenance);

            if (maintenance != null && ("IN_PROGRESS".equals(maintenance.getStatus().toString()) ||
            		"REJECTED".equals(maintenance.getStatus().toString()))) {
                request.setAttribute("maintenance", maintenance); 
                forwardToPage(request, response, "/WEB-INF/views/worker/reportCompleteMaintenances.jsp");
            } else {
            	errors.add("Only maintenances in 'IN_PROGRESS' OR 'REJECTED' status can be reported.");
            	request.setAttribute("errors", errors);
                loadAllMaintenances(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("Error preparing maintenance completion form.");
            request.setAttribute("errors", errors);
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
                    && maintenance != null && ("IN_PROGRESS".equals(maintenance.getStatus().toString()) ||
                    		"REJECTED".equals(maintenance.getStatus().toString()))) {
                maintenance.setStatus(Status.WAITING); 
                maintenance.setDuration(duration);
                maintenance.setReport(report);
                
                maintenance.update(maintenanceDAO);

                successes.add("Maintenance successfully reported");
                request.setAttribute("successes", successes);
            } else {
            	errors.add("Only maintenances in 'IN_PROGRESS' OR 'REJECTED' status can be reported.");
            	request.setAttribute("errors", errors);
            }

            loadAllMaintenances(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("Error reporting machine maintenance.");
            request.setAttribute("errors", errors);
            loadAllMaintenances(request, response);
        }
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}