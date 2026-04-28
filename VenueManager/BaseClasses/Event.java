package VenueManager.BaseClasses;

public class Event {

    public String id;
    public String name;

    public Event(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "Event {id='" + id + "', name='" + name + "'}";
    }

}
