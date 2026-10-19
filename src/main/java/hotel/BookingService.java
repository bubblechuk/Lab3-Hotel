package hotel;

import java.util.Date;

public class BookingService {

    private final IBookingRepository bookingRepository;

    public BookingService(IBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public boolean book(Integer guestId, Integer roomId, Date startDate, Date endDate) {
        return bookingRepository.book(guestId, roomId, startDate, endDate);
    }

    public boolean unBook(Integer guestId, Integer roomId) {
        Booking booking = bookingRepository.unBook(guestId, roomId);
        return booking != null;
    }
}