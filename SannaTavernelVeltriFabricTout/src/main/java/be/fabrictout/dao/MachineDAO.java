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

public class MachineDAO extends DAO<Machine> {
    private Connection connection;

    public MachineDAO(Connection connection) {
		super(connection);
		this.connection = connection;
    }

	public int getNextIdDAO() {
		System.out.println("MachineDAO : getNextIdDAO");
		String sql = "{CALL get_next_machine_id(?)}";
		try (CallableStatement stmt = connection.prepareCall(sql)) {
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.execute();
			return stmt.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
    @Override
    public boolean createDAO(Machine machine) {
        System.out.println("MachineDAO : createDAO");
        String sql = "{CALL create_machine(?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, machine.getIdMachine());
            stmt.setString(2, machine.getType().toString());
            stmt.setDouble(3, machine.getSize());
            stmt.setString(4, machine.getState().toString());

            String zoneIds = String.join(",",
                    machine.getZones().stream()
                            .map(zone -> String.valueOf(zone.getZoneId()))
                            .toArray(String[]::new));
            stmt.setString(5, zoneIds);

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
        String sql = "{CALL update_machine(?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, machine.getIdMachine());
            stmt.setString(2, machine.getType().toString());
            stmt.setDouble(3, machine.getSize());
            stmt.setString(4, machine.getState().toString());

            String zoneIds = String.join(",",
                    machine.getZones().stream()
                            .map(zone -> String.valueOf(zone.getZoneId()))
                            .toArray(String[]::new));
            stmt.setString(5, zoneIds);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteDAO(Machine machine) {
        System.out.println("MachineDAO : deleteDAO");
        String sql = "{CALL delete_machine(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, machine.getIdMachine());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Machine findDAO(int id) {
    	System.out.println("MachineDAO : findDAO");
        String sql = "{CALL find_machine(?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
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
        String sql = "{CALL find_all_machines(?)}";
        List<Machine> machines = new ArrayList<>();
        try (CallableStatement stmt = connection.prepareCall(sql)) {
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
        System.out.println("MachineDAO : addZoneDAO");
        String procedureCall = "{call add_zone_to_machine(?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
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
        

        Manager manager = new Manager(
				rs.getInt("manager_id"), 
				rs.getString("manager_firstName"),
				rs.getString("manager_lastName"), 
				LocalDate.parse(rs.getString("manager_birthDate")),
				rs.getString("manager_phoneNumber"), 
				rs.getString("manager_registrationCode"),
				rs.getString("manager_password"), 
				new Site());
        
        List<Zone> zones = new ArrayList<>();
        String zoneList = rs.getString("zone_list");
        if (zoneList != null && !zoneList.isEmpty()) {
            String[] zoneEntries = zoneList.split(",");
            for (String entry : zoneEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    Zone zone = new Zone(
                            Integer.parseInt(parts[0].trim()),
                            Letter.valueOf(parts[1].trim().toUpperCase()),
                            Color.valueOf(parts[2].trim().toUpperCase()),
                            rs.getInt("site_id"),
                            rs.getString("site_name"),
                            rs.getString("site_city")
                    );
                    zones.add(zone);
                    site = zone.getSite();
                    site.setManager(manager);
                }
            }
        }
        
       
        machine = new Machine(machineId, machineType, machineSize, machineState,zones);        
        
		

        String maintenanceList = rs.getString("maintenance_list");
        if (maintenanceList != null && !maintenanceList.isEmpty()) {
            String[] maintenanceEntries = maintenanceList.split(",");
            for (String entry : maintenanceEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 5) {
                    Maintenance maintenance = new Maintenance(
                            Integer.parseInt(parts[0].trim()),
                            LocalDate.parse(parts[1].trim()),
                            Integer.parseInt(parts[2].trim()),
                            parts[3].trim(),
                            Status.valueOf(parts[4].trim()),
                            machine,
                            new Manager(),
                            List.of(new Worker())
                    );
                    machine.addMaintenance(maintenance);
                }
            }
        }
        return machine;
    }

}
