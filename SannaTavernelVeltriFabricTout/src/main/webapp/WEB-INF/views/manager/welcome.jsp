<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="be.fabrictout.javabeans.Machine" %>
<%@ page import="be.fabrictout.javabeans.Site" %>
<%@ page import="be.fabrictout.javabeans.Zone" %>

<html>
<head>
    <title>Machine List</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/bootstrap.css" />
</head>
<body>
    <div class="container mt-4">
        <h1 class="mb-4">Machine List</h1>

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

        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Size</th>
                    <th>Site</th>
                    <th>Zone(s)</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Machine> machines = (List<Machine>) request.getAttribute("machines");
                    if (machines != null && !machines.isEmpty()) {
                        for (Machine machine : machines) {
                %>
                <tr>
                    <td><%= machine.getIdMachine() %></td>
                    <td><%= machine.getType() %></td>
                    <td><%= machine.getState() %></td>
                    <td><%= machine.getSize() %></td>

                    <td>
                        <%
                            List<Zone> zones = machine.getZones();
                            if (zones != null && !zones.isEmpty()) {
                                Site site = zones.get(0).getSite();
                                if (site != null) {
                                    out.print(site.getName());
                                } else {
                                    out.print("No site available");
                                }
                            } else {
                                out.print("No site available");
                            }
                        %>
                    </td>

                    <td>
                        <%
                            if (zones != null && !zones.isEmpty()) {
                                for (Zone zone : zones) {
                        %>
                            <span><%= zone.getLetter() %> (<%= zone.getColor() %>)</span><br>
                        <%
                                }
                            } else {
                        %>
                            <span>No zones available</span>
                        <%
                            }
                        %>
                    </td>

                    <td>
                        <% if ("OPERATIONAL".equals(machine.getState().toString())) { %>
                            <form action="Manager" method="get" style="display:inline;">
                                <input type="hidden" name="action" value="reportMachineMaintenance" />
                                <input type="hidden" name="idMachine" value="<%= machine.getIdMachine() %>" />
                                <button type="submit" class="btn btn-info btn-sm">Report Maintenance</button>
                            </form>
                        <% } %>

                        <form action="Manager" method="get" style="display:inline; margin-left:5px;">
                            <input type="hidden" name="action" value="seeMaintenances" />
                            <input type="hidden" name="idMachine" value="<%= machine.getIdMachine() %>" />
                            <button type="submit" class="btn btn-success btn-sm">See Maintenances</button>
                        </form>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="7" class="text-center">No machines found.</td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>

        <a href="Manager" class="btn btn-primary mt-3">Back to Home</a>
    </div>
</body>
</html>
