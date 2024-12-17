package be.fabrictout.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import be.fabrictout.javabeans.Color;
import be.fabrictout.javabeans.Letter;
import be.fabrictout.javabeans.Machine;
import be.fabrictout.javabeans.Maintenance;
import be.fabrictout.javabeans.Manager;
import be.fabrictout.javabeans.Site;
import be.fabrictout.javabeans.State;
import be.fabrictout.javabeans.Status;
import be.fabrictout.javabeans.Type;
import be.fabrictout.javabeans.Worker;
import be.fabrictout.javabeans.Zone;
import oracle.jdbc.OracleTypes;

public class MaintenanceDAO extends DAO<Maintenance> {

    public MaintenanceDAO(Connection conn) {
        super(conn);
    }

    public int getNextIdDAO() {
    	System.out.println("MaintenanceDAO : getNextIdDAO");
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
        System.out.println("MaintenanceDAO : createDAO");
        String procedureCall = "{call add_maintenance(?, ?, ?, ?, ?, ?, ?, ?)}"; // 8 paramètres
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
          
            stmt.setInt(1, maintenance.getIdMaintenance());
            stmt.setDate(2, Date.valueOf(maintenance.getDate()));
            stmt.setInt(3, maintenance.getDuration());
            stmt.setString(4, maintenance.getReport());
            stmt.setString(5, maintenance.getStatus().toString());
            stmt.setInt(6, maintenance.getManager().getIdPerson());
            stmt.setInt(7, maintenance.getMachine().getIdMachine());

            String workerIds = String.join(",",
                    maintenance.getWorkers().stream()
                            .map(worker -> String.valueOf(worker.getIdPerson()))
                            .toArray(String[]::new));
            System.out.println("WorkerIds : " + workerIds);
            stmt.setString(8, workerIds); 

            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteDAO(Maintenance maintenance) {
    	System.out.println("MaintenanceDAO : deleteDAO");
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
        System.out.println("MaintenanceDAO : updateDAO");

        String procedureCall = "{call update_maintenance(?, ?, ?, ?, ?, ?, ?, ?)}"; // 8 paramètres
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {

            // Paramètres de la maintenance
            stmt.setInt(1, maintenance.getIdMaintenance());
            stmt.setDate(2, Date.valueOf(maintenance.getDate()));
            stmt.setInt(3, maintenance.getDuration());
            stmt.setString(4, maintenance.getReport());
            stmt.setString(5, maintenance.getStatus().toString());
            stmt.setInt(6, maintenance.getMachine().getIdMachine());
            stmt.setInt(7, maintenance.getManager().getIdPerson());

            String workerIds = String.join(",",
                    maintenance.getWorkers().stream()
                            .map(worker -> String.valueOf(worker.getIdPerson()))
                            .toArray(String[]::new));
            stmt.setString(8, workerIds); 

            stmt.execute();
            System.out.println("Maintenance mise à jour avec succès.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Maintenance findDAO(int id) {
    	System.out.println("MaintenanceDAO : findDAO");
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
    	System.out.println("MaintenanceDAO : findAllDAO");
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
        System.out.println("MaintenanceDAO : setMaintenanceDAO");
        Maintenance maintenance = null;

        int maintenanceId = rs.getInt("id_maintenance");
        LocalDate maintenanceDate = LocalDate.parse(rs.getString("maintenance_date"));
        int duration = rs.getInt("maintenance_duration");
        String report = rs.getString("maintenance_report");
        Status status = Status.valueOf(rs.getString("maintenance_status"));

        Machine machine = new Machine(
            rs.getInt("machine_id"), 
            Type.valueOf(rs.getString("machine_type")), 
            rs.getDouble("machine_size"), 
            State.valueOf(rs.getString("machine_state")), 
            new ArrayList<>() 
        );

        Manager manager = new Manager(
            rs.getInt("manager_id"), // id_person
            rs.getString("manager_firstName"), // firstName
            rs.getString("manager_lastName"), // lastName
            LocalDate.parse(rs.getString("manager_birthDate")), // birthDate
            rs.getString("manager_phoneNumber"), // phoneNumber
            rs.getString("manager_registrationCode"), // registration
            rs.getString("manager_password"), // password
            new Site() // site
        );

        List<Worker> workers = new ArrayList<>();
        String workerList = rs.getString("worker_list");
        if (workerList != null && !workerList.isEmpty()) {
            String[] workerDetails = workerList.split(",");
            for (String details : workerDetails) {
                String[] parts = details.split(":");
                Worker worker = new Worker(
                    Integer.parseInt(parts[0]),        // id_person
                    parts[1],                          // firstName
                    parts[2],                          // lastName
                    LocalDate.parse(parts[3]),         // birthDate
                    parts[4],                          // phoneNumber
                    parts[5],                          // registrationCode
                    parts[6],                          // password
            		new Site()						   // site
                );
                workers.add(worker);
            }
        }

        maintenance = new Maintenance(
            maintenanceId,
            maintenanceDate,
            duration,
            report,
            status,
            machine,
            manager,
            workers
        );
        return maintenance;
    }

}
