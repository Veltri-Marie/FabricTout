package be.fabrictout.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.fabrictout.javabeans.Color;
import be.fabrictout.javabeans.Letter;
import be.fabrictout.javabeans.Machine;
import be.fabrictout.javabeans.State;
import be.fabrictout.javabeans.Type;
import be.fabrictout.javabeans.Zone;
import oracle.jdbc.OracleTypes;

public class ZoneDAO extends DAO<Zone> {

    public ZoneDAO(Connection conn) {
        super(conn);
    }

    public int getNextIdDAO() {
    	System.out.println("ZoneDAO : getNextIdDAO");
        String procedureCall = "{call get_next_zone_id(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean createDAO(Zone zone) {
    	System.out.println("ZoneDAO : createDAO");
        String procedureCall = "{call add_zone(?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, zone.getZoneId());
            stmt.setString(2, zone.getLetter().name());
            stmt.setString(3, zone.getColor().name());
            stmt.setInt(4, zone.getSite().getIdSite());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Zone zone) {
    	System.out.println("ZoneDAO : deleteDAO");
        String procedureCall = "{call delete_zone(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, zone.getZoneId());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Zone zone) {
    	System.out.println("ZoneDAO : updateDAO");
        String procedureCall = "{call update_zone(?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, zone.getZoneId());
            stmt.setString(2, zone.getLetter().name());
            stmt.setString(3, zone.getColor().name());
            stmt.setInt(4, zone.getSite().getIdSite());
            stmt.registerOutParameter(5, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(5) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Zone findDAO(int id) {
    	System.out.println("ZoneDAO : findDAO");
        String procedureCall = "{call find_zone(?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs.next()) {
                    return setZone(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Zone> findAllDAO() {
    	System.out.println("ZoneDAO : findAllDAO");
        String procedureCall = "{call find_all_zones(?)}";
        List<Zone> zones = new ArrayList<>();
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    zones.add(setZone(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return zones;
    }
    
    public Zone setZone(ResultSet rs) throws SQLException {
    	System.out.println("ZoneDAO : setZone : ");
    	List<Zone> zones = new ArrayList<>();
    	
        Zone zone = new Zone(
            rs.getInt("zone_id"),
            Letter.valueOf(rs.getString("letter").toUpperCase()),
            Color.valueOf(rs.getString("color").toUpperCase()),
            rs.getInt("id_site"),
            rs.getString("site_name"),
            rs.getString("site_city")
        );
        zones.add(zone);

        String machineList = rs.getString("machine_list");
        if (machineList != null && !machineList.isEmpty()) {
            String[] machineEntries = machineList.split(",");
            for (String entry : machineEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 4) {
                    Machine machine = new Machine(
                        Integer.parseInt(parts[0].trim()),
                        Type.valueOf(parts[1].trim().toUpperCase()),
                        Double.parseDouble(parts[2].trim()),
                        State.valueOf(parts[3].trim().toUpperCase()),
                        zones
                    );
                    zone.addMachine(machine);
                }
            }
        }

        return zone;
    }

}
