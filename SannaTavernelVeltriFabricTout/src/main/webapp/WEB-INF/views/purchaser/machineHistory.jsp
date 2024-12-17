<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="be.fabrictout.javabeans.Machine" %>
<%@ page import="be.fabrictout.javabeans.Maintenance" %>
<%@ page import="be.fabrictout.javabeans.Zone" %>

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

        <%
            List<Zone> zones = machine.getZones();
            if (zones != null && !zones.isEmpty()) {
                String siteName = zones.get(0).getSite().getName();
        %>
                <p><strong>Site:</strong> <%= siteName %></p>
                <p><strong>Zones:</strong></p>
                <ul>
        <%
                for (Zone zone : zones) {
        %>
                    <li><%= zone.getLetter() + " (" + zone.getColor() + ")" %></li>
        <%
                }
        %>
                </ul>
        <%
            } else {
                out.println("<p>No zones available.</p>");
            }
        %>

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
            <form action="Purchaser" method="POST">
                <input type="hidden" name="action" value="submitOrder"/>
                <input type="hidden" name="machineId" value="<%= machine.getIdMachine() %>"/>

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
