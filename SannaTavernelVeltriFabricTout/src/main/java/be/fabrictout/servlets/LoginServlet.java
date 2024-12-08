package be.fabrictout.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.fabrictout.connection.FabricToutConnection;
import be.fabrictout.dao.EmployeeDAO;
import be.fabrictout.pojo.Employee;
import be.fabrictout.pojo.Role;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection conn;
    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        conn = FabricToutConnection.getInstance();
        employeeDAO = new EmployeeDAO(conn);
    }

    public LoginServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            logout(request, response);
        } else {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/user/index.jsp");
            dispatcher.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String registrationCodeParam = request.getParameter("registrationCode");
        String passwordParam = request.getParameter("password");

        ArrayList<String> errors = new ArrayList<String>();

        if (registrationCodeParam == null || registrationCodeParam.equals("")) {
            errors.add("The [registrationCode] parameter is empty.");
        } else if (!registrationCodeParam.matches("^[0-9A-Za-z]{5,}$")) {
            errors.add("The [registrationCode] must be at least 5 characters.");
        }

        if (passwordParam == null || passwordParam.equals("")) {
            errors.add("The [password] parameter is empty.");
        } else if (!passwordParam.matches("^[0-9A-Za-z]{4,}$")) {
            errors.add("The [password] must contain at least 4 characters.");
        }

        if (errors.size() > 0) {
            request.setAttribute("errors", errors);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/user/index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        try {
            int idEmployee = Employee.authenticate(employeeDAO, registrationCodeParam, passwordParam); 

            if (idEmployee > 0) {
                Cookie cookie = new Cookie("idEmployee", String.valueOf(idEmployee));
                cookie.setMaxAge(86400);  
                response.addCookie(cookie);

                HttpSession session = request.getSession();
                session.setAttribute("idEmployee", idEmployee); 

                Employee employee = Employee.find(employeeDAO, idEmployee);


                if (employee.getRole() == Role.ADMIN) {
                    response.sendRedirect(request.getContextPath() + "/Admin");  
                } else if (employee.getRole() == Role.WORKER) {
                    response.sendRedirect(request.getContextPath() + "/Worker"); 
                } else {
                    response.sendRedirect(request.getContextPath() + "/Manager");  
                }

            } else {
                errors.add("Incorrect username or password.");
                request.setAttribute("errors", errors);
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/user/index.jsp");
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("An error occurred during authentication.");
            request.setAttribute("errors", errors);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/user/index.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("idEmployee", "");
        cookie.setMaxAge(0); 
        response.addCookie(cookie);

        response.sendRedirect(request.getContextPath() + "/Login");
    }
}
