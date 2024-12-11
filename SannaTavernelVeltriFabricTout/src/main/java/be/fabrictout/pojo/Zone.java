package be.fabrictout.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.fabrictout.dao.SiteDAO;
import be.fabrictout.dao.ZoneDAO;

public class Zone {
	// ATTRIBUTES
    private int zoneId;
    private Letter letter;
    private Color color;
    private Site site;
    private List<Machine> machines;
    
    // CONSTRUCTORS
	public Zone() {}
	
	public Zone(int zoneId, Letter letter, Color color) {
        this.zoneId = zoneId;
        this.letter = letter;
        this.color = color;
        site.addZone(this);
    }
	
	public Zone(int zoneId, Letter letter, Color color, Site site) {
        this.zoneId = zoneId;
        this.letter = letter;
        this.color = color;
        this.site = site;
        site.addZone(this);
    }
	
	public Zone(int zoneId, Letter letter, Color color, int idSite, String name, String city) {
	    this.zoneId = zoneId;
	    this.letter = letter;
	    this.color = color;
        this.site = new Site(idSite, name, city);
        this.site.addZone(this);
	}

	
	// PROPERTIES
    public int getZoneId() {
        return zoneId;
    }
    
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

    public Letter getLetter() {
        return letter;
    }
    
    public void setLetter(Letter letter) {
    	        this.letter = letter;
    }

    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
		this.color = color;
	}
    
    public Site getSite() {
        return site;      
    }
    
	public void setSite(Site site) {
		this.site = site;
	}
	
	// METHODS
	public boolean create(ZoneDAO zoneDAO) {
        return zoneDAO.createDAO(this); 
    }

    public static int getNextId(ZoneDAO zoneDAO) {
        return zoneDAO.getNextIdDAO(); 
    }
    
	public boolean delete(ZoneDAO zoneDAO) {
		return zoneDAO.deleteDAO(this);
	}
	
	public boolean update(ZoneDAO zoneDAO) {
		return zoneDAO.updateDAO(this);
	}
	
	public static Zone find(ZoneDAO zoneDAO, int id) {
		return zoneDAO.findDAO(id);
	}
	
	public static List<Zone> findAll(ZoneDAO zoneDAO) {
		return zoneDAO.findAllDAO();
	}
	
	public void addSite(Site site) {

    	this.site = site;

        if (site != null && !site.getZones().contains(site)) {
        	site.addZone(this); 
        }
    }
	
	public void addMachine(Machine machine) {
		if (machines == null) {
			machines = new ArrayList<>();
		}
		machines.add(machine);
	}
	

    @Override
    public String toString() {
        return "Zone{" +
                "zoneId=" + zoneId +
                ", letter=" + letter +
                ", color=" + color +
                ", site=" + site +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return zoneId == zone.zoneId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(zoneId);
    }
}

