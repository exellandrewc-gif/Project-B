package VenueManager.BaseClasses;

public class SeatingArea {

    public String id;
    public String name;

    public String[] seats;

    public String toString() {
        return "SeatingArea {id='" + id + "', name='" + name + "', seats='" + seats + "'}";
    }

    public SeatingArea(String id, String name, String[] seats) {
        this.id = id;
        this.name = name;
        this.seats = seats;
    }

}
