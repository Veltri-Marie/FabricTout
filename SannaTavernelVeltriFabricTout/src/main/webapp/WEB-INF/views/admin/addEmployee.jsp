<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="be.fabrictout.pojo.Employee" %>

<html>
<head>
    <title>Gestion des employés</title>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/bootstrap.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/responsive.css" />
    
    <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.1.3/assets/owl.carousel.min.css" />
    <link href="https://fonts.googleapis.com/css?family=Poppins:400,600,700&display=swap" rel="stylesheet">
</head>
<body>
    <h1>Ajouter un employé</h1>
    <form action="Admin" method="post">
        <label for="firstName">Prénom:</label>
        <input type="text" id="firstName" name="firstName" required/><br>
        
        <label for="lastName">Nom:</label>
        <input type="text" id="lastName" name="lastName" required/><br>
        
        <label for="birthDate">Date de naissance:</label>
        <input type="date" id="birthDate" name="birthDate" required/><br>
        
        <label for="phoneNumber">Numéro de téléphone:</label>
        <input type="text" id="phoneNumber" name="phoneNumber" required/><br>
        
        <label for="password">Mot de passe:</label>
        <input type="password" id="password" name="password" required/><br>

        <label for="role">Rôle:</label>
        <select name="role">
            <option value="ADMIN">Administrateur</option>
            <option value="MANAGER">Manager</option>
            <option value="WORKER">Ouvrier</option>
        </select><br>
        
        <button type="submit">Ajouter</button>
    </form>

    <h1>Liste des employés</h1>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Rôle</th>
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
            </tr>
        <%
                }
            }
        %>
    </table>

    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>
</body>
</html>
