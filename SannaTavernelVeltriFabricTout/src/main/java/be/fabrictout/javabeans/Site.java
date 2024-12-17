package be.fabrictout.javabeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.fabrictout.dao.SiteDAO;

public class Site implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private int idSite;
    private String name;
    private String city;
    private List<Zone> zones;
    private List<Worker> workers;
    private Manager manager;

    // CONSTRUCTORS
    public Site() {
        if (zones == null) {
            zones = new ArrayList<>();
        }

		if (workers == null) {
			workers = new ArrayList<>();
		}
    }

    public Site(int idSite, String name, String city) {
    	this();
		this.idSite = idSite;
		this.name = name;
		this.city = city;		
	}
    
    public Site(int idSite, String name, String city, List<Zone> zones) {
    	this(idSite, name, city);
        if (zones == null) throw new IllegalArgumentException("zones cannot be null");
        this.zones = zones;
    }

    // PROPERTIES
    public int getIdSite() {
        return idSite;
    }

    public void setIdSite(int idSite) {
        this.idSite = idSite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }


    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
    
    public List<Worker> getWorkers() {
		return workers;
    }
    
	public void setWorkers(List<Worker> workers) {
        this.workers = workers;
	}
    	        		
    // METHODS
    public boolean create(SiteDAO siteDAO) {
        return siteDAO.createDAO(this);
    }

    public static int getNextId(SiteDAO siteDAO) {
        return siteDAO.getNextIdDAO();
    }

    public boolean delete(SiteDAO siteDAO) {
        return siteDAO.deleteDAO(this);
    }

    public boolean update(SiteDAO siteDAO) {
        return siteDAO.updateDAO(this);
    }

    public static Site find(SiteDAO siteDAO, int id) {
        return siteDAO.findDAO(id);
    }

    public static List<Site> findAll(SiteDAO siteDAO) {
        return siteDAO.findAllDAO();
    }

    public void addZone(Zone zone) {
        if (this.zones == null) {
            this.zones = new ArrayList<>();
        }
        if (zone != null && !this.zones.contains(zone)) {
            this.zones.add(zone);
        }
    }

    
    public void addWorker(Worker worker) {
		if (this.workers == null) {
			this.workers = new ArrayList<>();
		}
		if (worker != null && !this.workers.contains(worker)) {
			this.workers.add(worker);
		}
    }
       

    @Override
    public String toString() {
        return "Site{" +
                "idSite=" + idSite +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", zones.size=" + (zones != null ? zones.size() : 0) + 
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return idSite == site.idSite;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSite);
    }
}
