package hotel;

import java.util.Date;

public interface IBookingRepository {
    public boolean book(Integer guestId, Integer roomId, Date startDate, Date endDate);
    public Booking unBook(Integer guestId, Integer roomId);
}
