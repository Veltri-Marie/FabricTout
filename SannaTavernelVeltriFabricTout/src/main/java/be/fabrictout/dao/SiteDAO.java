package be.fabrictout.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import be.fabrictout.javabeans.Color;
import be.fabrictout.javabeans.Letter;
import be.fabrictout.javabeans.Manager;
import be.fabrictout.javabeans.Site;
import be.fabrictout.javabeans.Worker;
import be.fabrictout.javabeans.Zone;
import oracle.jdbc.OracleTypes;

public class SiteDAO extends DAO<Site> {

    public SiteDAO(Connection conn) {
        super(conn);
    }

    public int getNextIdDAO() {
        String procedureCall = "{call get_next_site_id(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, Types.VARCHAR); 
            stmt.execute();
            return stmt.getInt(1); 
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; 
        }
    }

    @Override
    public boolean createDAO(Site site) {
    	System.out.println("SiteDAO : CreateDAO");
    	String procedureCall = "{call add_site(?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, site.getIdSite());
            stmt.setString(2, site.getName());
            stmt.setString(3, site.getCity());

            stmt.execute();
            return true; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }

    @Override
    public boolean deleteDAO(Site site) {
    	System.out.println("SiteDAO : deleteDAO");
    	String procedureCall = "{call delete_site(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, site.getIdSite()); 
            stmt.execute();
            return true; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }


    @Override
    public boolean updateDAO(Site site) {
    	System.out.println("SiteDAO : updateDAO");
    	String procedureCall = "{call update_site(?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, site.getIdSite());
            stmt.setString(2, site.getName());
            stmt.setString(3, site.getCity());
            stmt.registerOutParameter(4, Types.INTEGER);

            stmt.execute();
            int rowsUpdated = stmt.getInt(4);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Site findDAO(int id) {
    	System.out.println("SiteDAO : findDAO");
    	String procedureCall = "{call find_site(?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs.next()) {  
                    return setSiteDAO(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    @Override
    public List<Site> findAllDAO() {
    	System.out.println("SiteDAO : findAllDAO");
    	String procedureCall = "{call find_all_sites(?)}";
        List<Site> sites = new ArrayList<>();
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    sites.add(setSiteDAO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sites; 
    }


    private Site setSiteDAO(ResultSet rs) throws SQLException {
    	System.out.println("SiteDAO : setSiteDAO");
    	Site site = null; 
        Zone zone = null;
        List<Zone> zones = new ArrayList<>();
                    
        String zoneList = rs.getString("zone_list");
        if (zoneList != null && !zoneList.isEmpty()) {
            int counter = 0;
            String[] zoneEntries = zoneList.split(",");
            for (String entry : zoneEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    int zoneId = Integer.parseInt(parts[0].trim());
                    Letter letter = Letter.valueOf(parts[1].trim().toUpperCase());
                    Color color = Color.valueOf(parts[2].trim().toUpperCase());

                    if (counter == 0) {
                        Zone firstZone = new Zone(
                            zoneId,
                            letter,
                            color,
                            rs.getInt("id_site"),
                            rs.getString("site_name"),
                            rs.getString("site_city")
                        );
                        site = firstZone.getSite();
                        zones.add(firstZone);
                        counter++;
                        
                    } else {
                        zone = new Zone(
                            zoneId,
                            letter,
                            color, 
                            site);
                        zones.add(zone);

                        if (site != null) {
                            site.addZone(zone); 
                        }
                    }
                }
            }
        }
        
            
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
	    site.setManager(manager);
        
	    List<Worker> workers = new ArrayList<>();
	    String workerList = rs.getString("worker_list");
        if (workerList != null && !workerList.isEmpty()) {
            String[] workereEntries = workerList.split(",");
            for (String entry : workereEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 5) {
                    try {
                        Worker worker = new Worker(
                            Integer.parseInt(parts[0].trim()), // idPerson
                            parts[1].trim(), // firstName
                            parts[2].trim(),	// lastName
                            LocalDate.parse(parts[3].trim()), // birthDate
                            parts[4].trim(), // phoneNumber, 
                            parts[5].trim(), // registrationCode
                            parts[6].trim(), // password
                            site
                        );
                        site.addWorker(worker);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return site;
    }
    

}
