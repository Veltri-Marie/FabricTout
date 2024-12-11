package be.fabrictout.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.fabrictout.dao.EmployeeDAO;
import be.fabrictout.dao.MachineDAO;
import be.fabrictout.dao.SiteDAO;

public class Site {
    // ATTRIBUTES
    private int idSite;
    private String name;
    private String city;
    private List<Zone> zones;
    private List<Machine> machines;

    // CONSTRUCTORS
    public Site() 
    {
    	if (zones == null) {
    		zones = new ArrayList<>();
    	}
		if (machines == null) {
			machines = new ArrayList<>();
		}
    }
    
    
	public Site(int idSite, String name, String city) {
		this.idSite = idSite;
		this.name = name;
		this.city = city;		
	}
    
    public Site(int idSite, String name, String city, Zone zone) {
        this.idSite = idSite;
        this.name = name;
        this.city = city;
        addZone(zone);
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
    
    public List<Machine> getMachines() {
    	        return machines;
    }
    
	public void setMachines(List<Machine> machines) {
		this.machines = machines;
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
	
	public void addMachine(Machine machine) {
		if (this.machines == null) {
			this.machines = new ArrayList<>();
		}
		machines.add(machine);
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
