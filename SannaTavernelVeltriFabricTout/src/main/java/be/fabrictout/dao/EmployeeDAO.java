package be.fabrictout.dao;

import be.fabrictout.pojo.Employee;
import be.fabrictout.pojo.Role;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO extends DAO<Employee> {

    public EmployeeDAO(Connection conn) {
        super(conn);
    }

    @Override
    public int getNextIdDAO() {
    	System.out.println("EmployeeDAO : getNextIdDAO : ");
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
    	System.out.println("EmployeeDAO : createDAO : ");
        String procedureCall = "{call add_employee(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
        	
        	stmt.setInt(1, employee.getIdEmployee());
        	stmt.setString(2, employee.getRegistrationCode());
            stmt.setString(3, employee.getFirstName());
            stmt.setString(4, employee.getLastName());
            stmt.setDate(5, Date.valueOf(employee.getBirthdate()));
            stmt.setString(6, employee.getPhoneNumber());
            stmt.setString(7, employee.getPassword());
            stmt.setString(8, employee.getRole().toString());

            stmt.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int authenticateDAO(String registrationCode, String password) {
    	System.out.println("EmployeeDAO : authenticateDAO : ");
        String procedureCall = "{call authenticate_employee(?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setString(1, registrationCode);
            stmt.setString(2, password);

            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.execute();

            return stmt.getInt(3); 
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; 
        }
    }


   
    @Override
    public boolean deleteDAO(Employee employee) {
    	System.out.println("EmployeeDAO : deleteDAO : ");
        String procedureCall = "{call delete_employee(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, employee.getIdEmployee());
            stmt.execute();
            return true; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Employee employee) {
    	System.out.println("EmployeeDAO : updateDAO");
        String procedureCall = "{call update_employee(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, employee.getIdEmployee());
            stmt.setString(2, employee.getRegistrationCode());
            stmt.setString(3, employee.getFirstName());
            stmt.setString(4, employee.getLastName());
            stmt.setDate(5, Date.valueOf(employee.getBirthdate()));
            stmt.setString(6, employee.getPhoneNumber());
            stmt.setString(7, employee.getPassword());
            stmt.setString(8, employee.getRole().toString());

            stmt.registerOutParameter(9, java.sql.Types.INTEGER);

            stmt.execute();

            int rowsUpdated = stmt.getInt(9);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Employee findDAO(int id) {
    	System.out.println("EmployeeDAO : findDAO : ");
        String procedureCall = "{call find_employee(?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs.next()) {
                    return new Employee(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getDate("birthDate").toLocalDate(),
                        rs.getString("phoneNumber"),
                        rs.getInt("id_employee"),
                        rs.getString("registrationCode"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    @Override
    public List<Employee> findAllDAO() {
    	System.out.println("EmployeeDAO : findAllDAO : ");
        String procedureCall = "{call find_all_employee(?)}";
        List<Employee> employees = new ArrayList<>();
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    employees.add(new Employee(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getDate("birthDate").toLocalDate(),
                        rs.getString("phoneNumber"),
                        rs.getInt("id_employee"),
                        rs.getString("registrationCode"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
}
