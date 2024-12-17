<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="be.fabrictout.javabeans.Maintenance" %>
<%@ page import="be.fabrictout.javabeans.Machine" %>

<html>
<head>
    <title>Report Completed Maintenance</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/bootstrap.css" />
</head>
<body>
    <div class="container mt-4">
        <h1 class="mb-4">Report Completed Maintenance</h1>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
            <div class="alert alert-danger"><%= error %></div>
        <%
            }
        %>

        <%
            int idMaintenance = Integer.parseInt(request.getParameter("idMaintenance"));
        %>
        <form action="Worker" method="post">
            <input type="hidden" name="action" value="report" />
            <input type="hidden" name="idMaintenance" value="<%= idMaintenance %>" />

            <div class="form-group">
                <label for="duration">Maintenance Duration (hours):</label>
                <input type="number" id="duration" name="duration" class="form-control" min="1" required />
            </div>

            <div class="form-group">
                <label for="report">Maintenance Report:</label>
                <textarea id="report" name="report" class="form-control" rows="4" placeholder="Enter details about the maintenance..." required></textarea>
            </div>

            <button type="submit" class="btn btn-success">Submit Report</button>
            <a href="Worker" class="btn btn-secondary">Back</a>
        </form>
    </div>
</body>
</html>
