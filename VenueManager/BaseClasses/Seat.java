package VenueManager.BaseClasses;

public class Seat extends BaseEntity implements Bookable {

    private String posArea;

    private String displayName;

    private String eventId;
    private String bookingId;
    private Boolean isBookable;
    private SeatStatus status;

    public String toString() {
        return "Seat {id='" + id + "', posArea='" + posArea + "', displayName='" + displayName + "', eventId='"
                + eventId + "', bookingId='" + bookingId + "', isBookable='" + isBookable
                + "', status='" + status + "'}";
    }

    public Seat(String id, String posArea, String displayName, String bookingId, Boolean isBookable) {
        super(id);
        this.posArea = posArea;
        this.displayName = displayName;
        this.bookingId = bookingId;
        this.isBookable = isBookable;
        if (!Boolean.TRUE.equals(this.isBookable)) {
            this.status = SeatStatus.CHECKED_IN;
        } else if (this.bookingId == null || this.bookingId.isEmpty()) {
            this.status = SeatStatus.AVAILABLE;
        } else {
            this.status = SeatStatus.BOOKED;
        }

    }

    public String getPosArea() {
        return posArea;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEventId() {
        return eventId;
    }

    public String getBookingId() {
        return bookingId;
    }

    @Override
    public boolean isBookable() {
        return Boolean.TRUE.equals(isBookable);
    }

    public boolean isAvailable() {
        return isBookable() && (bookingId == null || bookingId.isEmpty());
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void assignBooking(String bookingId, String eventId) {
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.status = SeatStatus.BOOKED;
    }

    public void clearBooking() {
        this.bookingId = null;
        this.eventId = null;
        if (Boolean.TRUE.equals(this.isBookable)) {
            this.status = SeatStatus.AVAILABLE;
        } else {
            this.status = SeatStatus.CHECKED_IN;
        }
    }

    @Override
    public void setBookable(boolean isBookable) {
        this.isBookable = isBookable;
        if (!this.isBookable) {
            this.status = SeatStatus.CHECKED_IN;
        } else if (this.bookingId == null || this.bookingId.isEmpty()) {
            this.status = SeatStatus.AVAILABLE;
        } else {
            this.status = SeatStatus.BOOKED;
        }
    }

}
