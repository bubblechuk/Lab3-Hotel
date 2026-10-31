package hotel;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private final IBookingRepository bookingRepository;

    public BookingService(IBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public boolean book(Integer guestId, Integer roomId, Date startDate, Date endDate) {
        LOGGER.info("Booking request received for guestId={} and roomId={} (from {} to {})",
                guestId, roomId, startDate, endDate);

        if (startDate == null || endDate == null || startDate.after(endDate)) {
            LOGGER.warn("Invalid date range for booking: guestId={}, roomId={}, start={}, end={}",
                    guestId, roomId, startDate, endDate);
            return false;
        }

        try {
            boolean success = bookingRepository.book(guestId, roomId, startDate, endDate);
            if (!success) {
                LOGGER.warn("Room {} is already booked or unavailable for guest {}", roomId, guestId);
                return false;
            }
            LOGGER.info("Room {} successfully booked for guest {}", roomId, guestId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Booking operation failed unexpectedly for guestId={} and roomId={}",
                    guestId, roomId, e);
            return false;
        }
    }

    public boolean unBook(Integer guestId, Integer roomId) {
        LOGGER.info("Cancellation request received for guestId={} and roomId={}", guestId, roomId);

        try {
            Booking booking = bookingRepository.unBook(guestId, roomId);
            if (booking == null) {
                LOGGER.warn("Cancellation rejected: active booking not found for guestId={} and roomId={}",
                        guestId, roomId);
                return false;
            }
            LOGGER.info("Booking {} successfully canceled for guest {}", booking.Id, guestId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Cancellation operation failed for guestId={} and roomId={}",
                    guestId, roomId, e);
            return false;
        }
    }
}