package hotel;

import java.util.Date;

public class Booking {
    public static Integer counter = 0;
    public Integer Id;
    public Integer roomId;
    public Date startDate;
    public Date endDate;
    public Integer guestId;
    public Booking(Integer guestId, Integer roomId, Date startDate, Date endDate)
    {
        Id = counter++;
        this.guestId = guestId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }
}
