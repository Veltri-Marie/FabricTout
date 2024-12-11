<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="be.fabrictout.pojo.Machine" %>
<%@ page import="be.fabrictout.pojo.Maintenance" %>
<%@ page import="be.fabrictout.pojo.Zone" %>

<html>
<head>
    <title>Machine Maintenance History</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/bootstrap.css" />
</head>
<body>
    <h1>Machine Maintenance History</h1>

    <%
        Machine machine = (Machine) request.getAttribute("machine");
        if (machine != null) {
    %>
    
        <p><strong>Machine ID:</strong> <%= machine.getIdMachine() %></p>
        <p><strong>Type:</strong> <%= machine.getType() %></p>
        <p><strong>Size:</strong> <%= machine.getSize() %></p>
        <p><strong>Site:</strong> <%= machine.getSite().getName() %></p>
        <p><strong>Zones:</strong>
            <%
                List<Zone> zones = machine.getZones();
                if (zones != null && !zones.isEmpty()) {
                    for (Zone zone : zones) {
            %>
                <%= zone.getLetter() + " (" + zone.getColor() + ") " %>
            <%
                    }
                }
            %>
        </p>

        <h2>Maintenance History</h2>
        <%
            List<Maintenance> maintenanceHistory = machine.getMaintenances();
            if (maintenanceHistory != null && !maintenanceHistory.isEmpty()) {
        %>
            <table class="table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Duration (hours)</th>
                        <th>Report</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (Maintenance maintenance : maintenanceHistory) {
                    %>
                    <tr>
                        <td><%= maintenance.getDate() %></td>
                        <td><%= maintenance.getDuration() %></td>
                        <td><%= maintenance.getReport() %></td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        <% 
            } else {
                out.println("<p>No maintenance history available.</p>");
            }
        %>

        <%
            Boolean showReorderButton = (Boolean) request.getAttribute("showReorderButton");
            if (showReorderButton != null && showReorderButton) {
        %>
            <form action="Purchaser" method="get">
                <input type="hidden" name="action" value="orderMachine" />
                <button type="submit" class="btn btn-warning">Re-order Machine</button>
            </form>
        <%
            }
        %>

    <% } else { %>
        <p>Machine not found.</p>
    <% } %>

    <br>
    <a href="Purchaser">Back to Machine List</a>

</body>
</html>
