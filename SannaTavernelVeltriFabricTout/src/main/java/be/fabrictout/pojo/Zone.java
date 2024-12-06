package be.fabrictout.pojo;

import java.util.Objects;

public class Zone {
    private int zoneId;
    private Letter letter;
    private Color color;
    private Site site;

	public Zone() {}
	
	public Zone(int zoneId, Letter letter, Color color, Site site) {
        this.zoneId = zoneId;
        this.letter = letter;
        this.color = color;
        this.site = site;
        site.addZone(this);
    }

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
        return zoneId == zone.zoneId &&
               letter == zone.letter &&
               color == zone.color &&
               Objects.equals(site, zone.site);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zoneId, letter, color, site);
    }
}

