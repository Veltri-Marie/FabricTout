package be.fabrictout.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.fabrictout.pojo.Color;
import be.fabrictout.pojo.Letter;
import be.fabrictout.pojo.Machine;
import be.fabrictout.pojo.Site;
import be.fabrictout.pojo.State;
import be.fabrictout.pojo.Type;
import be.fabrictout.pojo.Zone;
import oracle.jdbc.OracleTypes;

public class SiteDAO extends DAO<Site> {

    public SiteDAO(Connection conn) {
        super(conn);
    }

    @Override
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

                        counter++;
                    } else {
                        zone = new Zone(
                            zoneId,
                            letter,
                            color, 
                            site);

                        if (site != null) {
                            site.addZone(zone); 
                        }
                    }
                }
            }
        }

        return site;
    }
    

}
