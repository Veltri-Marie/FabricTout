package be.fabrictout.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import be.fabrictout.pojo.Employee;
import be.fabrictout.pojo.Machine;
import be.fabrictout.pojo.Maintenance;
import be.fabrictout.pojo.Role;
import be.fabrictout.pojo.Site;
import be.fabrictout.pojo.State;
import be.fabrictout.pojo.Status;
import be.fabrictout.pojo.Type;
import oracle.jdbc.OracleTypes;

public class MaintenanceDAO extends DAO<Maintenance> {

    public MaintenanceDAO(Connection conn) {
        super(conn);
    }

    @Override
    public int getNextIdDAO() {
    	System.out.println("MaintenanceDAO : getNextIdDAO : ");
        String procedureCall = "{call get_next_maintenance_id(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean createDAO(Maintenance maintenance) {
    	System.out.println("MaintenanceDAO : createDAO : ");
        String procedureCall = "{call add_maintenance(?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, maintenance.getIdMaintenance());
            stmt.setDate(2, Date.valueOf(maintenance.getDate()));
            stmt.setString(3, String.format("0 %02d:00:00", maintenance.getDuration()));
            stmt.setString(4, maintenance.getReport());
            stmt.setString(5, maintenance.getStatus().toString());
            stmt.setInt(6, maintenance.getMachine().getIdMachine());

            String employeeIds = String.join(",",
                    maintenance.getEmployees().stream()
                            .map(emp -> String.valueOf(emp.getIdEmployee()))
                            .toArray(String[]::new));
            stmt.setString(7, employeeIds);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Maintenance maintenance) {
    	System.out.println("MaintenanceDAO : deleteDAO : ");
        String procedureCall = "{call delete_maintenance(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, maintenance.getIdMaintenance());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Maintenance maintenance) {
    	System.out.println("MaintenanceDAO : updateDAO : ");
        String procedureCall = "{call update_maintenance(?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, maintenance.getIdMaintenance());
            stmt.setDate(2, Date.valueOf(maintenance.getDate()));
            stmt.setString(3, String.format("0 %02d:00:00", maintenance.getDuration()));
            stmt.setString(4, maintenance.getReport());
            stmt.setString(5, maintenance.getStatus().toString());
            stmt.setInt(6, maintenance.getMachine().getIdMachine());

            String employeeIds = String.join(",",
                    maintenance.getEmployees().stream()
                            .map(emp -> String.valueOf(emp.getIdEmployee()))
                            .toArray(String[]::new));
            stmt.setString(7, employeeIds);

            stmt.registerOutParameter(8, OracleTypes.INTEGER);
            stmt.execute();

            int rowsUpdated = stmt.getInt(8);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Maintenance findDAO(int id) {
    	System.out.println("MaintenanceDAO : findDAO : ");
        String procedureCall = "{call find_maintenance(?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs.next()) {
                    return setMaintenanceDAO(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Maintenance> findAllDAO() {
    	System.out.println("MaintenanceDAO : findAllDAO : ");
        String procedureCall = "{call find_all_maintenances(?)}";
        List<Maintenance> maintenances = new ArrayList<>();
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    maintenances.add(setMaintenanceDAO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maintenances;
    }

	private Maintenance setMaintenanceDAO(ResultSet rs) throws SQLException {
		System.out.println("MaintenanceDAO : setMaintenanceDAO : ");
		Maintenance maintenance = null;
	    int maintenanceId = rs.getInt("id_maintenance");
	    LocalDate maintenanceDate = LocalDate.parse(rs.getString("maintenance_date"));
	    int duration = rs.getInt("maintenance_duration");
	    String report = rs.getString("maintenance_report");
	    Status status = Status.valueOf(rs.getString("maintenance_status"));

	    Machine machine = new Machine(
	        rs.getInt("id_machine"),
	        Type.valueOf(rs.getString("machine_type")),
	        rs.getDouble("machine_size"),
	        State.valueOf(rs.getString("machine_state")),
	        new Site()
	    );

	    List<Employee> employees = new ArrayList<>();
	    String employeeList = rs.getString("employee_list");

	    if (employeeList != null && !employeeList.isEmpty()) {
	        String[] employeeEntries = employeeList.split(",");
	        for (int i = 0; i < employeeEntries.length; i++) {
	            String[] parts = employeeEntries[i].split(":");
	            if (parts.length == 8) {
	                Employee employee = new Employee(
	                    parts[0].trim(),                
	                    parts[1].trim(),             
	                    LocalDate.parse(parts[2].trim()), 
	                    parts[3].trim(),               
	                    Integer.parseInt(parts[4].trim()), 
	                    parts[5].trim(),                
	                    parts[6].trim(),                
	                    Role.valueOf(parts[7].trim())   
	                );

	                if (i == 0) {
	            	    maintenance = new Maintenance(maintenanceId, maintenanceDate, duration, report, status, machine, employee);
	                    
	                } else {
	                    employees.add(employee);
	                    maintenance.addEmployee(employee);
	                }
	            }
	        }
	    }
        return maintenance;	    
	}


}
