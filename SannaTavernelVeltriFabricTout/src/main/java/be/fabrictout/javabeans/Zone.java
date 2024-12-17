package be.fabrictout.javabeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.fabrictout.dao.ZoneDAO;

public class Zone implements Serializable {

    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private int zoneId;
    private Letter letter;
    private Color color;
    private Site site;
    private List<Machine> machines;

    // CONSTRUCTORS
    public Zone() {
        if (machines == null) {
            machines = new ArrayList<>();
        }
    }
	
	public Zone(int zoneId, Letter letter, Color color) {
		this();
        this.zoneId = zoneId;
        this.letter = letter;
        this.color = color;
        site.addZone(this);
    }
	
	public Zone(int zoneId, Letter letter, Color color, Site site) {
		this();
        this.zoneId = zoneId;
        this.letter = letter;
        this.color = color;
        this.site = site;
        site.addZone(this);
    }
	
	public Zone(int zoneId, Letter letter, Color color, int idSite, String name, String city) {
		this();
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

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
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
