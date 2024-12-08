package be.fabrictout.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.fabrictout.connection.FabricToutConnection;
import be.fabrictout.dao.EmployeeDAO;
import be.fabrictout.pojo.Employee;
import be.fabrictout.pojo.Role;

public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection conn;
    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        conn = FabricToutConnection.getInstance();
        employeeDAO = new EmployeeDAO(conn);
    }

    public AdminServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
        loadAllEmployees(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        addEmployee(request, response);
    }

    private void loadAllEmployees(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Employee> employees = Employee.findAll(employeeDAO); 
            request.setAttribute("employees", employees); 

            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/admin/addEmployee.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement de la liste des employés.");
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/admin/addEmployee.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void addEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String birthDate = request.getParameter("birthDate");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (firstName == null || firstName.isEmpty() ||
            lastName == null || lastName.isEmpty() ||
            birthDate == null || birthDate.isEmpty() ||
            phoneNumber == null || phoneNumber.isEmpty() ||
            password == null || password.isEmpty() ||
            role == null || role.isEmpty()) {

            request.setAttribute("error", "Tous les champs doivent être remplis.");
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/admin/addEmployee.jsp");
            dispatcher.forward(request, response);
            return;
        }

        try {
            String registrationCode = firstName.substring(0, 1).toUpperCase() + lastName.toUpperCase();
            LocalDate birthDateParsed = LocalDate.parse(birthDate);
            int employeeId = Employee.getNextId(employeeDAO);

            Employee employee = new Employee(firstName, lastName, birthDateParsed, phoneNumber, employeeId, registrationCode, password, Role.valueOf(role.toUpperCase()));
            boolean created = employee.create(employeeDAO); 

            if (created) {
                loadAllEmployees(request, response); 
            } else {
                request.setAttribute("error", "Erreur lors de la création de l'employé.");
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/admin/addEmployee.jsp");
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur interne lors de la création de l'employé.");
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/admin/addEmployee.jsp");
            dispatcher.forward(request, response);
        }
    }

}
