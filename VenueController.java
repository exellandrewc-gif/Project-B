import VenueManager.BaseClasses.Booking;
import VenueManager.BaseClasses.Event;

public class VenueController {
    private final VenueModel model;

    public VenueController(VenueModel model) {
        this.model = model;
    }

    public void addEvent(String eventName) {
        // MVC step 2: the Controller receives user input from the View.
        this.model.addEvent(eventName);
    }

    public void deleteEvent(Event event) {
        this.model.deleteEvent(event);
    }

    public void createBooking(String customerName, Event event, SeatRecord seatRecord) {
        this.model.createBooking(customerName, event, seatRecord);
    }

    public void cancelBooking(Booking booking) {
        this.model.cancelBooking(booking);
    }

    public void checkInSeat(SeatRecord seatRecord) {
        this.model.checkInSeat(seatRecord);
    }

    public void checkOutSeat(SeatRecord seatRecord) {
        this.model.checkOutSeat(seatRecord);
    }

    public void createSeatingArea(String areaId, String areaName, String seatCountText) {
        this.model.createSeatingArea(areaId, areaName, seatCountText);
    }

    public void deleteSeatingArea(AreaRecord areaRecord) {
        this.model.deleteSeatingArea(areaRecord);
    }
}
