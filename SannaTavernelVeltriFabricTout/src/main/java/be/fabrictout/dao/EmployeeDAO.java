package be.fabrictout.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class EmployeeDAO {
	protected Connection connect = null;

    public EmployeeDAO(Connection conn) {
        this.connect = conn;
    }
    
    public int authenticateDAO(String registrationCode, String password) {
    	System.out.println("EmployeeDAO : authenticateDAO");
        String procedureCall = "{call authenticate_employee(?, ?, ?)}";
        try (CallableStatement stmt = connect.prepareCall(procedureCall)) {
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
    
    public String findTypeByIdDAO(int id) {
        String sql = "{call find_employee_type(?, ?)}"; 
        try (CallableStatement stmt = this.connect.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.execute();
            return stmt.getString(2); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    

}
