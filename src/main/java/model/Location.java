package model;

public class Location {

    private int locationId;
    private String name;
    private String type;

    public Location(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}