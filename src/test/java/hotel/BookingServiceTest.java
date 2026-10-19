package hotel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        IBookingRepository repository = new BookingRepository();
        bookingService = new BookingService(repository);
    }

    private Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Test
    @DisplayName("Тест 1: Успешное бронирование свободной комнаты (Успешный сценарий)")
    void testSuccessfulBooking() {
        Date startDate = createDate(2026, 10, 1);
        Date endDate = createDate(2026, 10, 5);

        boolean result = bookingService.book(1, 101, startDate, endDate);

        assertTrue(result, "Бронирование свободно свободной комнаты должно возвращать true");
    }

    @Test
    @DisplayName("Тест 2 (Индивидуальное задание): Ошибка при пересечении дат бронирования одного номера")
    void testBookingWithDateIntersectionFails() {
        Date start1 = createDate(2026, 10, 1);
        Date end1 = createDate(2026, 10, 10);
        bookingService.book(1, 101, start1, end1);

        Date start2 = createDate(2026, 10, 5);
        Date end2 = createDate(2026, 10, 12);

        boolean result = bookingService.book(2, 101, start2, end2);

        assertFalse(result, "Бронирование на пересекающиеся даты для одного номера должно отклоняться");
    }

    @Test
    @DisplayName("Тест 3: Бронирование смежных дат для одного номера должно проходить успешно")
    void testAdjacentBookingsAllowed() {
        Date start1 = createDate(2026, 10, 1);
        Date end1 = createDate(2026, 10, 5);
        bookingService.book(1, 101, start1, end1);

        Date start2 = createDate(2026, 10, 5);
        Date end2 = createDate(2026, 10, 10);

        boolean result = bookingService.book(2, 101, start2, end2);

        assertTrue(result, "Бронирование, начинающееся в день выезда предыдущего гостя, должно быть разрешено");
    }

    @Test
    @DisplayName("Тест 4: Успешная и некорректная отмена бронирования (unBook)")
    void testUnBookScenario() {
        Date startDate = createDate(2026, 10, 1);
        Date endDate = createDate(2026, 10, 5);
        bookingService.book(1, 101, startDate, endDate);

        boolean unbookNonExistent = bookingService.unBook(99, 101);
        assertFalse(unbookNonExistent, "Отмена несуществующего бронирования должна возвращать false");

        boolean unbookSuccess = bookingService.unBook(1, 101);
        assertTrue(unbookSuccess, "Успешная отмена бронирования должна возвращать true");
    }

    @Test
    @DisplayName("Тест 5: Невалидные даты (дата начала позже даты окончания или null)")
    void testInvalidDatesFails() {
        Date startDate = createDate(2026, 10, 10);
        Date endDate = createDate(2026, 10, 5);

        boolean resultInverted = bookingService.book(1, 101, startDate, endDate);
        boolean resultNull = bookingService.book(1, 101, null, endDate);

        assertFalse(resultInverted, "Бронирование с startDate > endDate должно возвращать false");
        assertFalse(resultNull, "Бронирование с null датой должно возвращать false");
    }
    @Test
    @DisplayName("Тест 6: Повторное успешное бронирование номера после отмены предыдущей брони")
    void testBookingAfterCancellation() {
        Date startDate = createDate(2026, 10, 1);
        Date endDate = createDate(2026, 10, 5);

        boolean firstBooking = bookingService.book(1, 101, startDate, endDate);
        assertTrue(firstBooking, "Первичное бронирование должно пройти успешно");

        boolean unbooked = bookingService.unBook(1, 101);
        assertTrue(unbooked, "Отмена бронирования должна пройти успешно");

        boolean secondBooking = bookingService.book(2, 101, startDate, endDate);
        assertTrue(secondBooking, "После отмены бронирования номер должен быть доступен для нового гостя");
    }
}
