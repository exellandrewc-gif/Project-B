package VenueManager;

import java.util.HashMap;
import java.util.UUID;

import VenueManager.BaseClasses.SeatingArea;
import VenueManager.BaseClasses.Seat;
import VenueManager.BaseClasses.Venue;

public class VenueManager {
    public Venue venue;

    public VenueManager(Venue venue) {
        this.venue = venue;
    }

    public void putSampleSeatingData() {
        this.venue.seatingAreas = new SeatingArea[] {
                new SeatingArea("A", "Grandstand A", null),
                new SeatingArea("B", "Grandstand B", null)
        };

        for (SeatingArea sa : this.venue.seatingAreas) {
            for (int i = 0; i < 10; i++) {
                String seatId = sa.id + "-" + (i + 1);
                Seat seat = new Seat(UUID.randomUUID().toString(), sa.id, "Seat " + sa.id + (i + 1), null, true);
                if (venue.seats == null) {
                    venue.seats = new HashMap<String, Seat>();
                }
                this.venue.seats.put(seatId, seat);
            }
        }
    }
}
