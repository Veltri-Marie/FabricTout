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

    <h1>Machine List</h1>

    <table class="table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Type</th>
                <th>Size</th>
                <th>Site</th> 
                <th>Zone(s)</th> 
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Machine> machines = (List<Machine>) request.getAttribute("machines");
                if (machines != null) {
                    for (Machine machine : machines) {
            %>
            <tr>
                <td><%= machine.getIdMachine() %></td>
                <td><%= machine.getType() %></td>
                <td><%= machine.getSize() %></td>
                
                <td>
                    <%
                        List<Zone> zones = machine.getZones();
                        if (zones != null && !zones.isEmpty()) {
                            Site site = zones.get(0).getSite();
                            out.print(site.getName());
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
                        <p><%= zone.getLetter().toString() %> - <%= zone.getColor() %></p>
                    <%
                            }
                        } else {
                    %>
                        <p>No zones available</p>
                    <%
                        }
                    %>
                </td>

                <td>
                    <form action="Purchaser" method="get" style="display:inline;">
                        <input type="hidden" name="action" value="viewMachineHistory" />
                        <input type="hidden" name="machineId" value="<%= machine.getIdMachine() %>" />
                        <button type="submit" class="btn btn-info">History</button>
                    </form>
                </td>
            </tr>
            <%
                    }
                }
            %>
        </tbody>
    </table>

</body>
</html>
