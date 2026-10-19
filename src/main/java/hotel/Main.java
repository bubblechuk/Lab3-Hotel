package hotel;

import java.util.Calendar;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Запуск системы бронирования ===");

        IBookingRepository repository = new BookingRepository();
        BookingService bookingService = new BookingService(repository);

        Date start = createDate(2026, 10, 1);
        Date end = createDate(2026, 10, 5);

        Date start2 = createDate(2026, 10, 3);
        Date end2 = createDate(2026, 10, 12);
        boolean success = bookingService.book(1, 101, start, end);
        boolean success2 = bookingService.book(2, 101, start2, end2);

        System.out.println("Результат бронирования: " + success);
        System.out.println("Результат бронирования: " + success2);

    }

    private static Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}