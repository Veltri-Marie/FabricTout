package be.fabrictout.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.fabrictout.pojo.Color;
import be.fabrictout.pojo.Employee;
import be.fabrictout.pojo.Letter;
import be.fabrictout.pojo.Machine;
import be.fabrictout.pojo.Maintenance;
import be.fabrictout.pojo.Site;
import be.fabrictout.pojo.State;
import be.fabrictout.pojo.Status;
import be.fabrictout.pojo.Type;
import be.fabrictout.pojo.Zone;
import oracle.jdbc.OracleTypes;

public class MachineDAO extends DAO<Machine> {

    public MachineDAO(Connection conn) {
        super(conn);
    }

    @Override
    public int getNextIdDAO() {
    	System.out.println("MachineDAO : getNextIdDAO");
        String procedureCall = "{call get_next_machine_id(?)}";
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
    public boolean createDAO(Machine machine) {
        System.out.println("MachineDAO : createDAO");
        String procedureCall = "{call add_machine(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, machine.getIdMachine());
            stmt.setString(2, machine.getType().toString());
            stmt.setDouble(3, machine.getSize());
            stmt.setString(4, machine.getState().toString());
            stmt.setInt(5, machine.getSite().getIdSite());

            String zoneIds = machine.getZones().stream()
                    .map(zone -> String.valueOf(zone.getZoneId()))
                    .collect(Collectors.joining(","));
            stmt.setString(6, zoneIds.isEmpty() ? null : zoneIds);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteDAO(Machine machine) {
    	System.out.println("MachineDAO : deleteDAO");
        String procedureCall = "{call delete_machine(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, machine.getIdMachine());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Machine machine) {
    	System.out.println("MachineDAO : updateDAO");
        String procedureCall = "{call update_machine(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, machine.getIdMachine());
            stmt.setString(2, machine.getType().toString());
            stmt.setDouble(3, machine.getSize());
            stmt.setString(4, machine.getState().toString());
            stmt.setInt(5, machine.getSite().getIdSite());
            stmt.registerOutParameter(6, OracleTypes.INTEGER);

            stmt.execute();
            int rowsUpdated = stmt.getInt(6);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Machine findDAO(int id) {
    	System.out.println("MachineDAO : findDAO");
        String procedureCall = "{call find_machine(?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs.next()) {
                    return setMachineDAO(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Machine> findAllDAO() {
    	System.out.println("MachineDAO : findAllDAO");
        String procedureCall = "{call find_all_machines(?)}";
        List<Machine> machines = new ArrayList<>();
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    machines.add(setMachineDAO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return machines;
    }
    
    public boolean addZoneDAO(Machine machine, Zone zone) {
        System.out.println("MachineDAO : addZoneDAO");
        String procedureCall = "{call add_zone_to_machine(?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, machine.getIdMachine());
            stmt.setInt(2, zone.getZoneId());

            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Machine setMachineDAO(ResultSet rs) throws SQLException {
        Machine machine = null;
        Site site = null;

        int machineId = rs.getInt("id_machine");
        Type machineType = Type.valueOf(rs.getString("machine_type"));
        double machineSize = rs.getDouble("machine_size");
        State machineState = State.valueOf(rs.getString("machine_state"));

        site = new Site(
                rs.getInt("site_id"),
                rs.getString("site_name"),
                rs.getString("site_city")
        );

        machine = new Machine(machineId, machineType, machineSize, machineState, site);

        String zoneList = rs.getString("zone_list");
        System.out.println(zoneList);
        if (zoneList != null && !zoneList.isEmpty()) {
            String[] zoneEntries = zoneList.split(",");
            for (String entry : zoneEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    Zone zone = new Zone(
                            Integer.parseInt(parts[0].trim()),
                            Letter.valueOf(parts[1].trim().toUpperCase()),
                            Color.valueOf(parts[2].trim().toUpperCase()),
                            site
                    );
                    machine.addZone(zone);
                    
                }
            }
        }

        String maintenanceList = rs.getString("maintenance_list");
        if (maintenanceList != null && !maintenanceList.isEmpty()) {
            String[] maintenanceEntries = maintenanceList.split(",");
            for (String entry : maintenanceEntries) {
                String[] parts = entry.split(";");
                if (parts.length == 5) {
                    try {
                        Maintenance maintenance = new Maintenance(
                                Integer.parseInt(parts[0].trim()),
                                LocalDate.parse(parts[1].trim()),
                                Integer.parseInt(parts[2].trim()),
                                parts[3].trim(),
                                Status.valueOf(parts[4].trim()),
                                machine,
                                new Employee()
                        );
                        machine.addMaintenance(maintenance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return machine;
    }
}
