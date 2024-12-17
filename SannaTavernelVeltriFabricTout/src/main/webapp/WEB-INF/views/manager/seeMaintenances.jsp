<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="be.fabrictout.javabeans.Maintenance" %>
<%@ page import="be.fabrictout.javabeans.Machine" %>
<%@ page import="be.fabrictout.javabeans.Status" %>

<html>
<head>
    <title>Maintenance List</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/bootstrap.css" />
</head>
<body>
    <div class="container">
        <h1 class="mt-4 mb-4">Maintenance List</h1>

        <!-- Display success or error messages -->
        <%
            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");
            if (error != null) {
        %>
            <div class="alert alert-danger"><%= error %></div>
        <%
            } else if (success != null) {
        %>
            <div class="alert alert-success"><%= success %></div>
        <%
            }
        %>

        <!-- Display Machine details -->
        <%
            Machine machine = (Machine) request.getAttribute("machine");
            List<Maintenance> maintenances = (List<Maintenance>) request.getAttribute("maintenances");
            if (machine != null) {
        %>
            <h3>Machine: <%= machine.getType() %> (ID: <%= machine.getIdMachine() %>)</h3>
        <%
            }
        %>

        <!-- Table of maintenances -->
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Date</th>
                    <th>Duration</th>
                    <th>Report</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (maintenances != null && !maintenances.isEmpty()) {
                        for (Maintenance maintenance : maintenances) {
                %>
                <tr>
                    <td><%= maintenance.getIdMaintenance() %></td>
                    <td><%= maintenance.getDate() %></td>
                    <td><%= maintenance.getDuration() %> hours</td>
                    <td><%= (maintenance.getReport() != null) ? maintenance.getReport() : "No report available" %></td>
                    <td><%= maintenance.getStatus() %></td>
                    <td>
                        <% if ("WAITING".equals(maintenance.getStatus().toString())) { %>
                            <form action="Manager" method="get" style="display:inline;">
                                <input type="hidden" name="action" value="validate" />
                                <input type="hidden" name="idMachine" value="<%= machine.getIdMachine() %>" />
                                <input type="hidden" name="idMaintenance" value="<%= maintenance.getIdMaintenance() %>" />
                                <button type="submit" class="btn btn-success btn-sm">Validate</button>
                            </form>
                        <% } %>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="6" class="text-center">No maintenances found.</td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>

        <!-- Back button -->
        <a href="Manager" class="btn btn-primary mt-3">Back to Machine List</a>
    </div>
</body>
</html>
