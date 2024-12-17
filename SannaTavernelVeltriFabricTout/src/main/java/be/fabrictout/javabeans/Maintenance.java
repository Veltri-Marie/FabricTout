package be.fabrictout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.fabrictout.dao.MaintenanceDAO;

public class Maintenance implements Serializable {

    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private int idMaintenance;
    private LocalDate date;
    private int duration;
    private String report;
    private Status status;
    private Machine machine;
    private List<Worker> workers;
    private Manager manager;

    // CONSTRUCTORS
    public Maintenance() {
        if (workers == null) {
            workers = new ArrayList<>();
        }
    }

    public Maintenance(int idMaintenance, LocalDate date, int duration, String report, Status status, 
                       Machine machine, Manager manager, List<Worker> workers) {
        this(); 
        this.idMaintenance = idMaintenance;
        this.date = date;
        this.duration = duration;
        this.report = report;
        this.status = status;
        this.machine = machine;
        this.manager = manager;
        this.workers = workers;
        for (Worker worker : workers) {
            worker.addMaintenance(this);
        }
        machine.addMaintenance(this);
        manager.addMaintenance(this);
    }

    // PROPERTIES
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

    public List<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
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

    public void addWorker(Worker worker) {
        if (workers == null) {
            workers = new ArrayList<>();
        }
        if (!workers.contains(worker)) {
            workers.add(worker);
        }
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
