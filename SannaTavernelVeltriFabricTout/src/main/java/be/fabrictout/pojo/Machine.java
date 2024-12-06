package be.fabrictout.pojo;

import java.util.List;
import java.util.Objects;

public class Machine {
    // ATTRIBUTES
    private int idMachine;
    private Type type;
    private double size;
    private Status status;
    private List<Site> sites;
    private List<Maintenance> maintenances;
    

    // CONSTRUCTORS
	public Machine() {}
	
    public Machine(int idMachine, Type type, double size, Status status, Site site) {
        this.idMachine = idMachine;
        this.type = type;
        this.size = size;
        this.status = status;
        addSite(site);
        site.addMachine(this);
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

    public Status getStatus() {
        return status;
    }
    
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public List<Site> getSites() {
		return sites;
	}
	
	public void setSites(List<Site> sites) {
		this.sites = sites;
	}
	
	public List<Maintenance> getMaintenances() {
		return maintenances;
	}

	public void setMaintenances(List<Maintenance> maintenances) {
		this.maintenances = maintenances;
	}
	
	// METHODS
	public void addMaintenance(Maintenance maintenance) {
		maintenances.add(maintenance);
	}
	
	public void addSite(Site site) {
		sites.add(site);
	}
	
    @Override
    public String toString() {
        return "Machine{" +
                "idMachine=" + idMachine +
                ", type=" + type +
                ", size=" + size +
                ", status=" + status +
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
               status == machine.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMachine, type, size, status);
    }
}

