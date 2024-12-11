package be.fabrictout.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.fabrictout.dao.EmployeeDAO;
import be.fabrictout.dao.MaintenanceDAO;

public class Maintenance {
    // ATTRIBUTES
    private int idMaintenance;
    private LocalDate date;
    private int duration;
    private String report;
    private Status status;
    private Machine machine;
    private List<Employee> employees;

    // CONSTRUCTORS
	public Maintenance() {
		if (employees == null) {
			employees = new ArrayList<>();
		}
	}
	
    public Maintenance(int idMaintenance, LocalDate date, int duration, String report, Status status, Machine machine, Employee employee) {
    	
        this.idMaintenance = idMaintenance;
        this.date = date;
        this.duration = duration;
        this.report = report;
        this.status = status;
        this.machine = machine;
        addEmployee(employee);
        machine.addMaintenance(this);
        employee.addMaintenance(this);
    }

    public int getIdMaintenance() {
        return idMaintenance;
    }
    
    public void setIdMaintenance(int idMaintenance) {
    	this.idMaintenance = idMaintenance;
    }

    public LocalDate getDate() {
        return date;
    }
    
	public void setDate(LocalDate date) {
		this.date = date;
	}

    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
		this.duration = duration;
    }

    public String getReport() {
        return report;
    }
    
    public void setReport(String report) {
		this.report = report;
    }

    public Status getStatus() {
        return status;
    }
    
        public void setStatus(Status status) {
			this.status = status;
        }

    public Machine getMachine() {
        return machine;
    }
    
    public void setMachine(Machine machine) {
		this.machine = machine;
    }
    
    public List<Employee> getEmployees() {
		return employees;
    }

    public void setEmployees(List<Employee> employees) {
		this.employees = employees;
    }
    
    // METHODS
    public boolean create(MaintenanceDAO maintenanceDAO) {
        return maintenanceDAO.createDAO(this); 
    }

    public static int getNextId(MaintenanceDAO maintenanceDAO) {
        return maintenanceDAO.getNextIdDAO(); 
    }
    
    
	public boolean delete(MaintenanceDAO maintenanceDAO) {
		return maintenanceDAO.deleteDAO(this);
	}
	
	public boolean update(MaintenanceDAO maintenanceDAO) {
		return maintenanceDAO.updateDAO(this);
	}
	
	public static Maintenance find(MaintenanceDAO maintenanceDAO, int id) {
		return maintenanceDAO.findDAO(id);
	}
	
	public static List<Maintenance> findAll(MaintenanceDAO maintenanceDAO) {
		return maintenanceDAO.findAllDAO();
	}
	
	public void addEmployee(Employee employee) {
		if (employees == null) {
            employees = new ArrayList<>();
                    }
		employees.add(employee);
	}
	
    @Override
    public String toString() {
        return "Maintenance{" +
                "idMaintenance=" + idMaintenance +
                ", date=" + date +
                ", duration=" + duration +
                ", report='" + report + '\'' +
                ", status=" + status +
                ", machine=" + machine +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Maintenance that = (Maintenance) o;
        return idMaintenance == that.idMaintenance &&
               duration == that.duration &&
               Objects.equals(date, that.date) &&
               Objects.equals(report, that.report) &&
               status == that.status &&
               Objects.equals(machine, that.machine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMaintenance, date, duration, report, status, machine);
    }
}

