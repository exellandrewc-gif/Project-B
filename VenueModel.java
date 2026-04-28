import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import VenueManager.VenueManager;
import VenueManager.BaseClasses.Booking;
import VenueManager.BaseClasses.Event;
import VenueManager.BaseClasses.Seat;
import VenueManager.BaseClasses.SeatingArea;
import VenueManager.BaseClasses.Venue;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VenueModel {
    private VenueManager venueManager;
    private ObservableList<Event> events;
    private ObservableList<Booking> bookings;
    private ObservableList<SeatRecord> seats;
    private ObservableList<SeatRecord> availableSeats;
    private ObservableList<AreaRecord> seatingAreas;
    private ObservableList<AreaStatsRecord> areaStats;

    private SimpleStringProperty statusMessage;
    private SimpleIntegerProperty totalSeats;
    private SimpleIntegerProperty availableSeatCount;
    private SimpleIntegerProperty bookedSeatCount;
    private SimpleIntegerProperty checkedInSeatCount;
    private SimpleIntegerProperty bookingCount;

    public VenueModel() {
        Venue venue = new Venue(UUID.randomUUID().toString(), "Sample Venue",
                "Concert venue management system", null);
        this.venueManager = new VenueManager(venue);
        this.venueManager.putSampleSeatingData();

        this.events = FXCollections.observableArrayList();
        this.bookings = FXCollections.observableArrayList();
        this.seats = FXCollections.observableArrayList();
        this.availableSeats = FXCollections.observableArrayList();
        this.seatingAreas = FXCollections.observableArrayList();
        this.areaStats = FXCollections.observableArrayList();

        this.statusMessage = new SimpleStringProperty("Ready.");
        this.totalSeats = new SimpleIntegerProperty(0);
        this.availableSeatCount = new SimpleIntegerProperty(0);
        this.bookedSeatCount = new SimpleIntegerProperty(0);
        this.checkedInSeatCount = new SimpleIntegerProperty(0);
        this.bookingCount = new SimpleIntegerProperty(0);

        addSampleEvent("Opening Night");
        addSampleEvent("Acoustic Evening");
        refreshData();
    }

    public Venue getVenue() {
        return this.venueManager.venue;
    }

    public ObservableList<Event> eventsProperty() {
        return this.events;
    }

    public ObservableList<Booking> bookingsProperty() {
        return this.bookings;
    }

    public ObservableList<SeatRecord> seatsProperty() {
        return this.seats;
    }

    public ObservableList<SeatRecord> availableSeatsProperty() {
        return this.availableSeats;
    }

    public ObservableList<AreaRecord> seatingAreasProperty() {
        return this.seatingAreas;
    }

    public ObservableList<AreaStatsRecord> areaStatsProperty() {
        return this.areaStats;
    }

    public SimpleStringProperty statusMessageProperty() {
        return this.statusMessage;
    }

    public SimpleIntegerProperty totalSeatsProperty() {
        return this.totalSeats;
    }

    public SimpleIntegerProperty availableSeatCountProperty() {
        return this.availableSeatCount;
    }

    public SimpleIntegerProperty bookedSeatCountProperty() {
        return this.bookedSeatCount;
    }

    public SimpleIntegerProperty checkedInSeatCountProperty() {
        return this.checkedInSeatCount;
    }

    public SimpleIntegerProperty bookingCountProperty() {
        return this.bookingCount;
    }

    public void addEvent(String eventName) {
        String cleanName = cleanText(eventName);
        if (cleanName.isEmpty()) {
            setStatus("Event name cannot be empty.");
            return;
        }

        addSampleEvent(cleanName);
        refreshData();
        setStatus("Event created: " + cleanName);
    }

    public void deleteEvent(Event event) {
        if (event == null) {
            setStatus("Select an event first.");
            return;
        }

        if (hasBookingsForEvent(event.getId())) {
            setStatus("Cannot delete event because bookings already exist for it.");
            return;
        }

        getVenue().events.remove(event.getId());
        refreshData();
        setStatus("Event deleted: " + event.getName());
    }

    public void createBooking(String customerName, Event event, SeatRecord seatRecord) {
        String cleanName = cleanText(customerName);
        if (cleanName.isEmpty()) {
            setStatus("Customer name cannot be empty.");
            return;
        }
        if (event == null) {
            setStatus("Select an event for the booking.");
            return;
        }
        if (seatRecord == null) {
            setStatus("Select an available seat.");
            return;
        }

        Seat seat = getVenue().seats.get(seatRecord.getSeatId());
        if (seat == null || !seat.isAvailable()) {
            setStatus("Selected seat is no longer available.");
            refreshData();
            return;
        }

        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingId, event.getId(), new String[] { seatRecord.getSeatId() }, cleanName);

        // MVC step 3: the Model updates the domain objects and observable lists.
        getVenue().bookings.put(bookingId, booking);
        seat.assignBooking(bookingId, event.getId());

        refreshData();
        setStatus("Booking created for " + cleanName + " in seat " + seatRecord.getSeatId() + ".");
    }

    public void cancelBooking(Booking booking) {
        if (booking == null) {
            setStatus("Select a booking first.");
            return;
        }

        String[] seatIds = booking.getSeatIds();
        if (seatIds != null) {
            for (String seatId : seatIds) {
                Seat seat = getVenue().seats.get(seatId);
                if (seat != null && booking.getId().equals(seat.getBookingId())) {
                    seat.clearBooking();
                }
            }
        }

        getVenue().bookings.remove(booking.getId());
        refreshData();
        setStatus("Booking cancelled: " + shortId(booking.getId()));
    }

    public void checkInSeat(SeatRecord seatRecord) {
        if (seatRecord == null) {
            setStatus("Select a seat first.");
            return;
        }

        Seat seat = getVenue().seats.get(seatRecord.getSeatId());
        if (seat == null) {
            setStatus("Seat was not found.");
            return;
        }
        if (seat.getBookingId() == null || seat.getBookingId().trim().isEmpty()) {
            setStatus("Cannot check in a seat without a booking.");
            return;
        }
        if (!seat.isBookable()) {
            setStatus("Seat is already checked in.");
            return;
        }

        seat.setBookable(false);
        refreshData();
        setStatus("Seat checked in: " + seatRecord.getSeatId());
    }

    public void checkOutSeat(SeatRecord seatRecord) {
        if (seatRecord == null) {
            setStatus("Select a seat first.");
            return;
        }

        Seat seat = getVenue().seats.get(seatRecord.getSeatId());
        if (seat == null) {
            setStatus("Seat was not found.");
            return;
        }
        if (seat.isBookable()) {
            setStatus("Seat is already checked out.");
            return;
        }

        seat.setBookable(true);
        refreshData();
        setStatus("Seat checked out: " + seatRecord.getSeatId());
    }

    public void createSeatingArea(String areaId, String areaName, String seatCountText) {
        String cleanId = cleanText(areaId).toUpperCase();
        String cleanName = cleanText(areaName);
        int seatCount;

        if (cleanId.isEmpty() || cleanName.isEmpty()) {
            setStatus("Area ID and area name are required.");
            return;
        }
        if (findSeatingAreaById(cleanId) != null) {
            setStatus("A seating area with that ID already exists.");
            return;
        }

        try {
            seatCount = Integer.parseInt(cleanText(seatCountText));
        } catch (NumberFormatException e) {
            setStatus("Seat count must be a whole number.");
            return;
        }
        if (seatCount <= 0) {
            setStatus("Seat count must be greater than 0.");
            return;
        }

        if (getVenue().seats == null) {
            getVenue().seats = new HashMap<String, Seat>();
        }

        String[] areaSeatIds = new String[seatCount];
        for (int i = 0; i < seatCount; i++) {
            String seatKey = cleanId + "-" + (i + 1);
            areaSeatIds[i] = seatKey;
            getVenue().seats.put(seatKey,
                    new Seat(UUID.randomUUID().toString(), cleanId, "Seat " + cleanId + (i + 1), null, true));
        }

        SeatingArea newArea = new SeatingArea(cleanId, cleanName, areaSeatIds);
        addSeatingAreaToVenue(newArea);
        refreshData();
        setStatus("Seating area created: " + cleanId);
    }

    public void deleteSeatingArea(AreaRecord areaRecord) {
        if (areaRecord == null) {
            setStatus("Select a seating area first.");
            return;
        }

        String areaId = areaRecord.getAreaId();
        if (hasBookedSeatsInArea(areaId)) {
            setStatus("Cannot delete area because it has booked seats.");
            return;
        }

        removeSeatsForArea(areaId);
        removeSeatingArea(areaId);
        refreshData();
        setStatus("Seating area deleted: " + areaId);
    }

    public String formatBookingDetails(Booking booking) {
        if (booking == null) {
            return "No booking selected.";
        }

        String seatText = "";
        if (booking.getSeatIds() != null) {
            seatText = String.join(", ", booking.getSeatIds());
        }

        return "Booking ID: " + booking.getId()
                + "\nCustomer: " + booking.getCustomerName()
                + "\nEvent: " + getEventName(booking.getEventId())
                + "\nSeats: " + seatText;
    }

    public String getEventName(String eventId) {
        Event event = getVenue().events.get(eventId);
        if (event == null) {
            return eventId;
        }
        return event.getName();
    }

    private void refreshData() {
        refreshEvents();
        refreshBookings();
        refreshSeats();
        refreshSeatingAreas();
        refreshStatistics();
    }

    private void refreshEvents() {
        this.events.clear();
        List<String> eventIds = new ArrayList<String>(getVenue().events.keySet());
        Collections.sort(eventIds);
        for (String eventId : eventIds) {
            this.events.add(getVenue().events.get(eventId));
        }
    }

    private void refreshBookings() {
        this.bookings.clear();
        List<String> bookingIds = new ArrayList<String>(getVenue().bookings.keySet());
        Collections.sort(bookingIds);
        for (String bookingId : bookingIds) {
            this.bookings.add(getVenue().bookings.get(bookingId));
        }
        this.bookingCount.set(this.bookings.size());
    }

    private void refreshSeats() {
        this.seats.clear();
        this.availableSeats.clear();

        if (getVenue().seats == null) {
            return;
        }

        List<String> seatIds = new ArrayList<String>(getVenue().seats.keySet());
        Collections.sort(seatIds);
        for (String seatId : seatIds) {
            Seat seat = getVenue().seats.get(seatId);
            SeatRecord record = new SeatRecord(seatId, seat);
            this.seats.add(record);
            if (seat != null && seat.isAvailable()) {
                this.availableSeats.add(record);
            }
        }
    }

    private void refreshSeatingAreas() {
        this.seatingAreas.clear();
        if (getVenue().seatingAreas == null) {
            return;
        }

        for (SeatingArea area : getVenue().seatingAreas) {
            if (area != null) {
                this.seatingAreas.add(new AreaRecord(area.id, area.name, getVenue().countSeatsInArea(area.id)));
            }
        }
    }

    private void refreshStatistics() {
        this.areaStats.clear();

        int total = 0;
        int available = 0;
        int booked = 0;
        int checkedIn = 0;

        if (getVenue().seatingAreas != null) {
            for (SeatingArea area : getVenue().seatingAreas) {
                if (area == null) {
                    continue;
                }

                int areaTotal = 0;
                int areaAvailable = 0;
                int areaBooked = 0;
                int areaCheckedIn = 0;

                if (getVenue().seats != null) {
                    for (String seatId : getVenue().seats.keySet()) {
                        if (seatId.startsWith(area.id + "-")) {
                            Seat seat = getVenue().seats.get(seatId);
                            areaTotal++;
                            if (seat != null && !seat.isBookable()) {
                                areaCheckedIn++;
                            } else if (seat != null && seat.getBookingId() != null && !seat.getBookingId().isEmpty()) {
                                areaBooked++;
                            } else {
                                areaAvailable++;
                            }
                        }
                    }
                }

                this.areaStats.add(new AreaStatsRecord(area.id, area.name, areaTotal, areaAvailable, areaBooked,
                        areaCheckedIn));
                total += areaTotal;
                available += areaAvailable;
                booked += areaBooked;
                checkedIn += areaCheckedIn;
            }
        }

        this.totalSeats.set(total);
        this.availableSeatCount.set(available);
        this.bookedSeatCount.set(booked);
        this.checkedInSeatCount.set(checkedIn);
    }

    private void addSampleEvent(String eventName) {
        String eventId = UUID.randomUUID().toString();
        getVenue().events.put(eventId, new Event(eventId, eventName));
    }

    private boolean hasBookingsForEvent(String eventId) {
        for (Booking booking : getVenue().bookings.values()) {
            if (booking != null && eventId.equals(booking.getEventId())) {
                return true;
            }
        }
        return false;
    }

    private SeatingArea findSeatingAreaById(String areaId) {
        if (getVenue().seatingAreas == null) {
            return null;
        }
        for (SeatingArea area : getVenue().seatingAreas) {
            if (area != null && area.id.equals(areaId)) {
                return area;
            }
        }
        return null;
    }

    private boolean hasBookedSeatsInArea(String areaId) {
        if (getVenue().seats == null) {
            return false;
        }
        for (String seatId : getVenue().seats.keySet()) {
            if (seatId.startsWith(areaId + "-")) {
                Seat seat = getVenue().seats.get(seatId);
                if (seat != null && seat.getBookingId() != null && !seat.getBookingId().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addSeatingAreaToVenue(SeatingArea area) {
        if (getVenue().seatingAreas == null) {
            getVenue().seatingAreas = new SeatingArea[] { area };
            return;
        }

        SeatingArea[] next = new SeatingArea[getVenue().seatingAreas.length + 1];
        for (int i = 0; i < getVenue().seatingAreas.length; i++) {
            next[i] = getVenue().seatingAreas[i];
        }
        next[next.length - 1] = area;
        getVenue().seatingAreas = next;
    }

    private void removeSeatsForArea(String areaId) {
        List<String> toRemove = new ArrayList<String>();
        for (String seatId : getVenue().seats.keySet()) {
            if (seatId.startsWith(areaId + "-")) {
                toRemove.add(seatId);
            }
        }
        for (String seatId : toRemove) {
            getVenue().seats.remove(seatId);
        }
    }

    private void removeSeatingArea(String areaId) {
        List<SeatingArea> keptAreas = new ArrayList<SeatingArea>();
        for (SeatingArea area : getVenue().seatingAreas) {
            if (area != null && !area.id.equals(areaId)) {
                keptAreas.add(area);
            }
        }
        getVenue().seatingAreas = keptAreas.toArray(new SeatingArea[0]);
    }

    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        return text.trim();
    }

    private void setStatus(String message) {
        this.statusMessage.set(message);
    }

    private String shortId(String id) {
        if (id == null || id.length() <= 8) {
            return id;
        }
        return id.substring(0, 8);
    }
}

class SeatRecord {
    private final SimpleStringProperty seatId;
    private final SimpleStringProperty areaId;
    private final SimpleStringProperty displayName;
    private final SimpleStringProperty status;
    private final SimpleStringProperty bookingId;

    public SeatRecord(String seatId, Seat seat) {
        this.seatId = new SimpleStringProperty(seatId);
        if (seat == null) {
            this.areaId = new SimpleStringProperty("");
            this.displayName = new SimpleStringProperty("");
            this.status = new SimpleStringProperty("");
            this.bookingId = new SimpleStringProperty("");
        } else {
            this.areaId = new SimpleStringProperty(seat.getPosArea());
            this.displayName = new SimpleStringProperty(seat.getDisplayName());
            this.status = new SimpleStringProperty(seat.getStatus().toString());
            String bookingText = seat.getBookingId() == null ? "" : seat.getBookingId();
            this.bookingId = new SimpleStringProperty(bookingText);
        }
    }

    public String getSeatId() {
        return this.seatId.get();
    }

    public SimpleStringProperty seatIdProperty() {
        return this.seatId;
    }

    public SimpleStringProperty areaIdProperty() {
        return this.areaId;
    }

    public SimpleStringProperty displayNameProperty() {
        return this.displayName;
    }

    public SimpleStringProperty statusProperty() {
        return this.status;
    }

    public SimpleStringProperty bookingIdProperty() {
        return this.bookingId;
    }
}

class AreaRecord {
    private final SimpleStringProperty areaId;
    private final SimpleStringProperty areaName;
    private final SimpleIntegerProperty seatCount;

    public AreaRecord(String areaId, String areaName, int seatCount) {
        this.areaId = new SimpleStringProperty(areaId);
        this.areaName = new SimpleStringProperty(areaName);
        this.seatCount = new SimpleIntegerProperty(seatCount);
    }

    public String getAreaId() {
        return this.areaId.get();
    }

    public SimpleStringProperty areaIdProperty() {
        return this.areaId;
    }

    public SimpleStringProperty areaNameProperty() {
        return this.areaName;
    }

    public SimpleIntegerProperty seatCountProperty() {
        return this.seatCount;
    }
}

class AreaStatsRecord {
    private final SimpleStringProperty areaId;
    private final SimpleStringProperty areaName;
    private final SimpleIntegerProperty totalSeats;
    private final SimpleIntegerProperty availableSeats;
    private final SimpleIntegerProperty bookedSeats;
    private final SimpleIntegerProperty checkedInSeats;

    public AreaStatsRecord(String areaId, String areaName, int totalSeats, int availableSeats, int bookedSeats,
            int checkedInSeats) {
        this.areaId = new SimpleStringProperty(areaId);
        this.areaName = new SimpleStringProperty(areaName);
        this.totalSeats = new SimpleIntegerProperty(totalSeats);
        this.availableSeats = new SimpleIntegerProperty(availableSeats);
        this.bookedSeats = new SimpleIntegerProperty(bookedSeats);
        this.checkedInSeats = new SimpleIntegerProperty(checkedInSeats);
    }

    public SimpleStringProperty areaIdProperty() {
        return this.areaId;
    }

    public SimpleStringProperty areaNameProperty() {
        return this.areaName;
    }

    public SimpleIntegerProperty totalSeatsProperty() {
        return this.totalSeats;
    }

    public SimpleIntegerProperty availableSeatsProperty() {
        return this.availableSeats;
    }

    public SimpleIntegerProperty bookedSeatsProperty() {
        return this.bookedSeats;
    }

    public SimpleIntegerProperty checkedInSeatsProperty() {
        return this.checkedInSeats;
    }
}
