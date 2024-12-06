package be.fabrictout.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Site {
    // ATTRIBUTES
    private int idSite;
    private String name;
    private String city;
    private List<Zone> zones;
    private List<Machine> machines;

    // CONSTRUCTORS
    public Site() {}
    
    public Site(int idSite, String name, String city, Zone zone) {
        this.idSite = idSite;
        this.name = name;
        this.city = city;
        addZone(zone);
    }
    
	public Site(int idSite, String name, String city, int zoneId, Letter letter, Color color) {
		this.idSite = idSite;
		this.name = name;
		this.city = city;
		Zone zone = new Zone(zoneId, letter, color, this);
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
  	public void addZone(Zone zone) {
        if (this.zones == null) {
            this.zones = new ArrayList<>();
		}
        if (zone != null && !zones.contains(zone)) {
        	zones.add(zone);
        	zone.setSite(this); 
        }
    }
	
	public void addMachine(Machine machine) {
		machines.add(machine);
	}

    @Override
    public String toString() {
        return "Site{" +
                "idSite=" + idSite +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", zones=" + zones +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return idSite == site.idSite &&
               Objects.equals(name, site.name) &&
               Objects.equals(city, site.city) &&
               Objects.equals(zones, site.zones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSite, name, city, zones);
    }
}
