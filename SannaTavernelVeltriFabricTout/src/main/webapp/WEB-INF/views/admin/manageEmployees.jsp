<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="be.fabrictout.pojo.Employee" %>
<%@ page import="be.fabrictout.pojo.Role" %>

<html>
<head>
    <title>Employee Management</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/bootstrap.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/responsive.css" />
</head>
<body>
<h1><%= request.getAttribute("employee") != null ? "Edit Employee" : "Add Employee" %></h1>
    <form action="Admin" method="post">
        <input type="hidden" name="idEmployee" value="<%= request.getAttribute("employee") != null ? ((Employee) request.getAttribute("employee")).getIdEmployee() : "" %>" />
        <label for="firstName">First Name:</label>
        <input type="text" id="firstName" name="firstName" value="<%= request.getAttribute("employee") != null ? ((Employee) request.getAttribute("employee")).getFirstName() : "" %>" required/><br>
        
        <label for="lastName">Last Name:</label>
        <input type="text" id="lastName" name="lastName" value="<%= request.getAttribute("employee") != null ? ((Employee) request.getAttribute("employee")).getLastName() : "" %>" required/><br>
        
        <label for="birthDate">Birth Date:</label>
        <input type="date" id="birthDate" name="birthDate" value="<%= request.getAttribute("employee") != null ? ((Employee) request.getAttribute("employee")).getBirthdate() : "" %>" required/><br>
        
        <label for="phoneNumber">Phone Number:</label>
        <input type="text" id="phoneNumber" name="phoneNumber" value="<%= request.getAttribute("employee") != null ? ((Employee) request.getAttribute("employee")).getPhoneNumber() : "" %>" required/><br>
        
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" value="<%= request.getAttribute("employee") != null ? ((Employee) request.getAttribute("employee")).getPassword() : "" %>" required/><br>

        <label for="role">Role:</label>
        <select name="role">
            <option value="ADMIN" <%= request.getAttribute("employee") != null && ((Employee) request.getAttribute("employee")).getRole() == Role.ADMIN ? "selected" : "" %>>Administrator</option>
            <option value="MANAGER" <%= request.getAttribute("employee") != null && ((Employee) request.getAttribute("employee")).getRole() == Role.MANAGER ? "selected" : "" %>>Manager</option>
            <option value="WORKER" <%= request.getAttribute("employee") != null && ((Employee) request.getAttribute("employee")).getRole() == Role.WORKER ? "selected" : "" %>>Worker</option>
        </select><br>
        
        <button type="submit"><%= request.getAttribute("employee") != null ? "Save" : "Add" %></button>
    </form>

    <h1>Employee List</h1>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Last Name</th>
            <th>First Name</th>
            <th>Role</th>
            <th>Actions</th>
        </tr>
        <%
            List<Employee> employees = (List<Employee>) request.getAttribute("employees");
            if (employees != null) {
                for (Employee employee : employees) {
        %>
            <tr>
                <td><%= employee.getIdEmployee() %></td>
                <td><%= employee.getFirstName() %></td>
                <td><%= employee.getLastName() %></td>
                <td><%= employee.getRole() %></td>
                <td>
                    <form action="Admin" method="get" style="display:inline;">
                        <input type="hidden" name="action" value="edit" />
                        <input type="hidden" name="idEmployee" value="<%= employee.getIdEmployee() %>" />
                        <button type="submit">Edit</button>
                    </form>
                    <form action="Admin" method="get" style="display:inline;">
                        <input type="hidden" name="action" value="delete" />
                        <input type="hidden" name="idEmployee" value="<%= employee.getIdEmployee() %>" />
                        <button type="submit">Delete</button>
                    </form>
                </td>
            </tr>
        <%
                }
            }
        %>
    </table>

</body>
</html>
