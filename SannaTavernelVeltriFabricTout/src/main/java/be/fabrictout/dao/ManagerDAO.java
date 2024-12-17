package be.fabrictout.dao;

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

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManagerDAO extends DAO<Manager> {
    private Connection connection;

    public ManagerDAO(Connection connection) {
        super(connection);
        this.connection = connection;
        }
        
    @Override
    public boolean createDAO(Manager manager) {
    	System.out.println("ManagerDAO : createDAO");
        String sql = "{CALL create_manager(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, manager.getIdPerson()); 
            stmt.setString(2, manager.getFirstName());
            stmt.setString(3, manager.getLastName());
            stmt.setDate(4, Date.valueOf(manager.getBirthDate()));
            stmt.setString(5, manager.getPhoneNumber());
            stmt.setString(6, manager.getRegistrationCode());
            stmt.setString(7, manager.getPassword());
            stmt.setInt(8, manager.getSite().getIdSite());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean updateDAO(Manager manager) {
    	System.out.println("ManagerDAO : updateDAO");
        String sql = "{CALL update_manager(?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
        	stmt.setString(1, manager.getFirstName());
            stmt.setString(2, manager.getLastName());
            stmt.setDate(3, Date.valueOf(manager.getBirthDate()));
            stmt.setString(4, manager.getPhoneNumber());
            stmt.setString(5, manager.getRegistrationCode());
            stmt.setString(6, manager.getPassword());
            stmt.setInt(7, manager.getSite().getIdSite());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Manager manager) {
		System.out.println("ManagerDAO : deleteDAO");
        String sql = "{CALL delete_manager(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, manager.getIdPerson());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Manager findDAO(int id) {
        System.out.println("ManagerDAO : findDAO");
        String sql = "{CALL find_manager(?, ?)}"; 
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, id); 
            stmt.registerOutParameter(2, OracleTypes.CURSOR); 

            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) { 
                if (rs.next()) {
                    return setManager(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    


    @Override
    public List<Manager> findAllDAO() {
    	System.out.println("ManagerDAO : findAllDAO");
        String sql = "{CALL find_all_managers()}";
        List<Manager> managers = new ArrayList<>();
        try (CallableStatement stmt = connection.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Manager manager = setManager(rs);
                managers.add(manager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return managers;
    }
    
    public Manager setManager(ResultSet rs) throws SQLException {
    	System.out.println("ManagerDAO : setManager");
    	
    	Site site = null;

    	
        List<Zone> zones = new ArrayList<>();
        String zoneList = rs.getString("zone_list");
        if (zoneList != null && !zoneList.isEmpty()) {
            String[] zoneEntries = zoneList.split(",");
            for (String entry : zoneEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                	System.out.println("idZone :" + Integer.parseInt(parts[0].trim()));
                	System.out.println("letter :" + Letter.valueOf(parts[1].trim().toUpperCase()));
                	System.out.println("color :" + Color.valueOf(parts[2].trim().toUpperCase()));
                	System.out.println("site_id :" + rs.getInt("site_id"));
                	System.out.println("site_name :" + rs.getString("site_name"));
                	System.out.println("site_city :" + rs.getString("site_city"));
                	
                    Zone zone = new Zone(                           	
                            Integer.parseInt(parts[0].trim()), // idZone
                            Letter.valueOf(parts[1].trim().toUpperCase()), // letter
                            Color.valueOf(parts[2].trim().toUpperCase()), // color
                            rs.getInt("site_id"),
                            rs.getString("site_name"),
                            rs.getString("site_city")
                    );
                    zones.add(zone);
                    site = zone.getSite();
                    
                }
            }
        }
        
        System.out.println("id_person :" + rs.getInt("id_person"));
        System.out.println("firstName :" + rs.getString("firstName"));
        System.out.println("lastName :" + rs.getString("lastName"));
        System.out.println("birthDate :" + rs.getDate("birthDate").toLocalDate());
        System.out.println("phoneNumber :" + rs.getString("phoneNumber"));
        System.out.println("registrationCode :" + rs.getString("registrationCode"));
        System.out.println("password :" + rs.getString("password"));
        System.out.println("site :" + site);
    	
        Manager manager = new Manager(
        		rs.getInt("id_person"), 
        		rs.getString("firstName"),
        		rs.getString("lastName"),
        		rs.getDate("birthDate").toLocalDate(),
        		rs.getString("phoneNumber"),
        		rs.getString("registrationCode"),
        		rs.getString("password"),
        		site
        		);
    	      
        
        return manager;	        
    }
}