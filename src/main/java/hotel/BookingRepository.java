package hotel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class BookingRepository implements IBookingRepository {

    private final ArrayList<Booking> _bookings;

    public BookingRepository() {
        _bookings = new ArrayList<Booking>();
    }

    public List<Booking> getBookings() {
        return _bookings;
    }
    public boolean book(Integer guestId, Integer roomId, Date startDate, Date endDate) {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            return false;
        }
        boolean hasIntersection = _bookings.stream()
                .filter(b -> b.getRoomId().equals(roomId))
                .anyMatch(b -> startDate.before(b.getEndDate()) && endDate.after(b.getStartDate()));

        if (hasIntersection) {
            return false;
        }

        Booking booking = new Booking(guestId, roomId, startDate, endDate);
        _bookings.add(booking);
        return true;
    }
    public Booking unBook(Integer guestId, Integer roomId) {
        Optional<Booking> bookingToCancel = _bookings.stream()
                .filter(b -> b.getGuestId().equals(guestId) && b.getRoomId().equals(roomId))
                .findFirst();

        if (bookingToCancel.isPresent()) {
            Booking booking = bookingToCancel.get();
            _bookings.remove(booking);
            return booking;
        }

        return null;
    }
}