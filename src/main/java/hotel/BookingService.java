package hotel;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private final IBookingRepository bookingRepository;
    private final Counter operationsTotal;
    private final Counter operationsSuccessTotal;
    private final Counter operationsErrorTotal;
    private final Counter bookingsTotal;
    private final Counter bookingsSuccessTotal;
    private final Counter bookingsOccupiedTotal;
    private final Counter bookingsErrorTotal;

    public BookingService(IBookingRepository bookingRepository) {
        this(bookingRepository, new SimpleMeterRegistry());
    }

    public BookingService(IBookingRepository bookingRepository, MeterRegistry meterRegistry) {
        this.bookingRepository = bookingRepository;
        this.operationsTotal = Counter.builder("application_operations_total")
                .description("Total number of application operations")
                .register(meterRegistry);
        this.operationsSuccessTotal = Counter.builder("application_operations_success_total")
                .description("Total number of successful operations")
                .register(meterRegistry);
        this.operationsErrorTotal = Counter.builder("application_operations_error_total")
                .description("Total number of failed operations")
                .register(meterRegistry);
        this.bookingsTotal = Counter.builder("hotel_bookings_total")
                .description("Total number of room booking attempts")
                .register(meterRegistry);
        this.bookingsSuccessTotal = Counter.builder("hotel_bookings_success_total")
                .description("Total number of successful room bookings")
                .register(meterRegistry);
        this.bookingsOccupiedTotal = Counter.builder("hotel_bookings_occupied_total")
                .description("Total number of booking attempts for occupied rooms")
                .register(meterRegistry);
        this.bookingsErrorTotal = Counter.builder("hotel_bookings_error_total")
                .description("Total number of booking errors")
                .register(meterRegistry);
    }

    public boolean book(Integer guestId, Integer roomId, Date startDate, Date endDate) {
        operationsTotal.increment();
        bookingsTotal.increment();
        LOGGER.info("Booking request received for guestId={} and roomId={} (from {} to {})",
                guestId, roomId, startDate, endDate);

        if (startDate == null || endDate == null || startDate.after(endDate)) {
            operationsErrorTotal.increment();
            bookingsErrorTotal.increment();
            LOGGER.warn("Invalid date range for booking: guestId={}, roomId={}, start={}, end={}",
                    guestId, roomId, startDate, endDate);
            return false;
        }

        try {
            boolean success = bookingRepository.book(guestId, roomId, startDate, endDate);
            if (!success) {
                operationsErrorTotal.increment();
                bookingsOccupiedTotal.increment();
                LOGGER.warn("Room {} is already booked or unavailable for guest {}", roomId, guestId);
                return false;
            }
            operationsSuccessTotal.increment();
            bookingsSuccessTotal.increment();
            LOGGER.info("Room {} successfully booked for guest {}", roomId, guestId);
            return true;
        } catch (Exception e) {
            operationsErrorTotal.increment();
            bookingsErrorTotal.increment();
            LOGGER.error("Booking operation failed unexpectedly for guestId={} and roomId={}",
                    guestId, roomId, e);
            return false;
        }
    }

    public boolean unBook(Integer guestId, Integer roomId) {
        operationsTotal.increment();
        LOGGER.info("Cancellation request received for guestId={} and roomId={}", guestId, roomId);

        try {
            Booking booking = bookingRepository.unBook(guestId, roomId);
            if (booking == null) {
                operationsErrorTotal.increment();
                LOGGER.warn("Cancellation rejected: active booking not found for guestId={} and roomId={}",
                        guestId, roomId);
                return false;
            }
            operationsSuccessTotal.increment();
            LOGGER.info("Booking {} successfully canceled for guest {}", booking.Id, guestId);
            return true;
        } catch (Exception e) {
            operationsErrorTotal.increment();
            LOGGER.error("Cancellation operation failed for guestId={} and roomId={}",
                    guestId, roomId, e);
            return false;
        }
    }
}