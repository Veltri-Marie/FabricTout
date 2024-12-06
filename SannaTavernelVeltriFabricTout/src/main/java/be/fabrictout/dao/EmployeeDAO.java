package be.fabrictout.dao;

import be.fabrictout.pojo.Employee;
import be.fabrictout.pojo.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO extends DAO<Employee> {

    public EmployeeDAO(Connection conn) {
        super(conn);
    }

    @Override
    public int getNextIdDAO() {
        String procedureCall = "{call get_next_employee_id(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean createDAO(Employee employee) {
        String procedureCall = "{call add_employee(?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setDate(3, Date.valueOf(employee.getBirthdate()));
            stmt.setString(4, employee.getPhoneNumber());
            stmt.setString(5, employee.getPassword()); 
            stmt.setString(6, employee.getRole().toString());

            stmt.registerOutParameter(7, Types.INTEGER);

            stmt.execute();

            int idEmployee = stmt.getInt(7);
            employee.setIdEmployee(idEmployee);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Employee employee) {
        String procedureCall = "{call delete_employee(?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, employee.getIdEmployee());
            stmt.registerOutParameter(2, Types.INTEGER);
            stmt.execute();
            int rowsAffected = stmt.getInt(2);
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Employee employee) {
        String sql = "UPDATE Employee SET firstName = ?, lastName = ?, birthDate = ?, phoneNumber = ?, password = ?, role = ? WHERE id_employee = ?";
        try (PreparedStatement stmt = this.connect.prepareStatement(sql)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setDate(3, Date.valueOf(employee.getBirthdate()));
            stmt.setString(4, employee.getPhoneNumber());
            stmt.setString(5, employee.getPassword()); // Assurez-vous que le mot de passe est crypté dans la base
            stmt.setString(6, employee.getRole().toString());
            stmt.setInt(7, employee.getIdEmployee());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Retourne vrai si une ligne a été mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Employee findDAO(int id) {
        String sql = "SELECT * FROM Employee WHERE id_employee = ?";
        try (PreparedStatement stmt = this.connect.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Employee(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getDate("birthDate").toLocalDate(),
                    rs.getString("phoneNumber"),
                    rs.getInt("id_employee"),
                    rs.getString("password"),
                    Role.valueOf(rs.getString("role"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retourne null si l'employé n'est pas trouvé
    }

    @Override
    public List<Employee> findAllDAO() {
        
        return null;
    }

}
