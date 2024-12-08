<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="be.fabrictout.pojo.Employee" %>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css" />
</head>
<body>
    <h1>Login</h1>

    <!-- Displaying errors if any -->
    <%
        ArrayList<String> errors = (ArrayList<String>) request.getAttribute("errors");
        if (errors != null && !errors.isEmpty()) {
    %>
        <div style="color: red;">
            <ul>
                <%
                    for (String error : errors) {
                        out.println("<li>" + error + "</li>");
                    }
                %>
            </ul>
        </div>
    <% } %>

    <!-- Login form -->
    <form action="Login" method="post">
        <label for="registrationCode">Registration code:</label>
        <input type="text" id="registrationCode" name="registrationCode" required /><br><br>

        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required /><br><br>

        <button type="submit">Log In</button>
    </form>

    <br/>
</body>
</html>
