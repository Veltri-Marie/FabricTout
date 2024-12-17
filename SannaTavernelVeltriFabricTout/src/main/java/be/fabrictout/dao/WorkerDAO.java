package be.fabrictout.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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

public class WorkerDAO extends DAO<Worker> {
	
	private Connection connection;

	public WorkerDAO(Connection connection) {
		super(connection);
		this.connection = connection;
	}
	
    @Override
    public boolean createDAO(Worker worker) {
    	System.out.println("WorkerDAO : createDAO");
        String sql = "{CALL create_worker(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, worker.getFirstName());
            stmt.setString(2, worker.getLastName());
            stmt.setDate(3, Date.valueOf(worker.getBirthDate()));
            stmt.setString(4, worker.getPhoneNumber());
            stmt.setString(5, worker.getRegistrationCode());
            stmt.setString(6, worker.getPassword());
            stmt.setInt(7, worker.getSite().getIdSite());
            stmt.registerOutParameter(8, Types.INTEGER);
            stmt.execute();
            worker.setIdPerson(stmt.getInt(8));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Worker worker) {
    	System.out.println("WorkerDAO : updateDAO");
        String sql = "{CALL update_worker(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, worker.getIdPerson());
            stmt.setString(2, worker.getFirstName());
            stmt.setString(3, worker.getLastName());
            stmt.setDate(4, Date.valueOf(worker.getBirthDate()));
            stmt.setString(5, worker.getPhoneNumber());
            stmt.setString(6, worker.getRegistrationCode());
            stmt.setString(7, worker.getPassword());
            stmt.setInt(8, worker.getSite().getIdSite());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Worker worker) {
    	System.out.println("WorkerDAO : deleteDAO");
        String sql = "{CALL delete_worker(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, worker.getIdPerson());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Worker findDAO(int id) {
    	System.out.println("WorkerDAO : findDAO");
        String sql = "{CALL find_worker(?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs.next()) {
                    return setWorker(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Worker> findAllDAO() {
    	System.out.println("WorkerDAO : findAllDAO");
        String sql = "{CALL find_all_workers(?)}";
        List<Worker> workers = new ArrayList<>();
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    workers.add(setWorker(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workers;
    }
    
	public Worker setWorker(ResultSet rs) throws SQLException {
		System.out.println("WorkerDAO : setWorker");
		Site site = null;
		
        String zoneList = rs.getString("zone_list");
        if (zoneList != null && !zoneList.isEmpty()) {
        	System.out.println("ZoneList : " + zoneList);
            String[] zoneEntries = zoneList.split(",");
            for (String entry : zoneEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                	System.out.println("id : " + parts[0].trim());
                	System.out.println("letter : " + parts[1].trim());
                	System.out.println("color : " + parts[2].trim());
                	System.out.println("site_id : " + rs.getInt("site_id"));
                	System.out.println("site_name : " + rs.getString("site_name"));
                	System.out.println("site_city : " + rs.getString("site_city"));
                	
                    Zone zone = new Zone(
                            Integer.parseInt(parts[0].trim()), // idZone
                            Letter.valueOf(parts[1].trim().toUpperCase()), // letter
                            Color.valueOf(parts[2].trim().toUpperCase()), // color
                            rs.getInt("site_id"), // Alias explicit de id_site
		                    rs.getString("site_name"),
		                    rs.getString("site_city"));
                    
                    System.out.println("Zone : " + zone);
                    site = zone.getSite();
                    System.out.println("Site : " + site);
                    
                }
            }
        }
        
		Worker worker = new Worker(
				rs.getInt("id_person"),
				rs.getString("firstName"), 
				rs.getString("lastName"),
				rs.getDate("birthDate").toLocalDate(), 
				rs.getString("phoneNumber"), 
				rs.getString("registrationCode"),
				rs.getString("password"), 
				site
				);
		
		System.out.println("Worker : " + worker);
		
		return worker;
	}
}
