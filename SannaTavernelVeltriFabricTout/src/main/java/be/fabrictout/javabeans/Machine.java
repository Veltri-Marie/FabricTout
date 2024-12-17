package be.fabrictout.javabeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.fabrictout.dao.MachineDAO;

public class Machine implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private int idMachine;
    private Type type;
    private double size;
    private State state;
    private List<Maintenance> maintenances;
    private List<Zone> zones;

    // CONSTRUCTORS
    public Machine() {
        if (maintenances == null) {
            maintenances = new ArrayList<>();
        }
        if (zones == null) {
            zones = new ArrayList<>();
        }
    }

    public Machine(int idMachine, Type type, double size, State state, List<Zone> zones) {
        this();  
        this.idMachine = idMachine;
        this.type = type;
        this.size = size;
        this.state = state;
        if (zones == null) throw new IllegalArgumentException("zones cannot be null");
        this.zones = zones;
		for (Zone zone : zones) {
			zone.addMachine(this);
		}
    }

    // PROPERTIES
    public int getIdMachine() {
        return idMachine;
    }

    public void setIdMachine(int idMachine) {
        this.idMachine = idMachine;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<Maintenance> getMaintenances() {
        return maintenances;
    }

    public void setMaintenances(List<Maintenance> maintenances) {
        this.maintenances = maintenances;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    // METHODS
    public boolean create(MachineDAO machineDAO) {
        return machineDAO.createDAO(this);
    }

    public static int getNextId(MachineDAO machineDAO) {
        return machineDAO.getNextIdDAO();
    }

    public boolean delete(MachineDAO machineDAO) {
        return machineDAO.deleteDAO(this);
    }

    public boolean update(MachineDAO machineDAO) {
        return machineDAO.updateDAO(this);
    }

    public static Machine find(MachineDAO machineDAO, int id) {
        return machineDAO.findDAO(id);
    }

    public static List<Machine> findAll(MachineDAO machineDAO) {
        return machineDAO.findAllDAO();
    }

    public void addMaintenance(Maintenance maintenance) {
        if (maintenances == null) {
            maintenances = new ArrayList<>();
        }
        if (!maintenances.contains(maintenance)) {
            maintenances.add(maintenance);
        }
    }

    public void addZone(Zone zone) {
        if (zones == null) {
            zones = new ArrayList<>();
        }
        if (!zones.contains(zone)) {
            zones.add(zone);
            zone.addMachine(this);
        }
    }

    @Override
    public String toString() {
        return "Machine{" +
                "idMachine=" + idMachine +
                ", type=" + type +
                ", size=" + size +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Machine machine = (Machine) o;
        return idMachine == machine.idMachine &&
               Double.compare(machine.size, size) == 0 &&
               type == machine.type &&
               state == machine.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMachine, type, size, state);
    }
}
