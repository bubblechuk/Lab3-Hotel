package hotel;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("=== Запуск системы бронирования ===");

        IBookingRepository repository = new BookingRepository();
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        BookingService bookingService = new BookingService(repository, registry);

        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/metrics", exchange -> {
            String response = registry.scrape();
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set(
                    "Content-Type", "text/plain; version=0.0.4; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        Date start = createDate(2026, 10, 1);
        Date end = createDate(2026, 10, 5);

        Date start2 = createDate(2026, 10, 3);
        Date end2 = createDate(2026, 10, 12);
        boolean success = bookingService.book(1, 103, start, end);
        boolean success2 = bookingService.book(2, 103, start2, end2);

        Date startDate = createDate(2026, 10, 10);
        Date endDate = createDate(2026, 10, 5);
        bookingService.book(1, 102, startDate, endDate);
        bookingService.book(1, 102, null, endDate);

        System.out.println("Результат бронирования 1: " + success);
        System.out.println("Результат бронирования 2: " + success2);

        runConsoleMenu(bookingService, server, start, end, start2, end2, startDate, endDate);
    }

    private static void runConsoleMenu(
            BookingService bookingService,
            HttpServer server,
            Date start,
            Date end,
            Date start2,
            Date end2,
            Date startDate,
            Date endDate) {
        int nextRoomId = 200;
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("\nВыберите действие: 1 - успешная бронь, "
                        + "2 - занятый номер, 3 - ошибка дат, 0 - выход:");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        boolean ok = bookingService.book(1, nextRoomId++, start, end);
                        System.out.println("Успешная бронь: " + ok);
                        break;
                    case "2":
                        boolean occupied = bookingService.book(2, 103, start2, end2);
                        System.out.println("Бронь занятого номера: " + occupied);
                        break;
                    case "3":
                        boolean err = bookingService.book(1, 102, startDate, endDate);
                        System.out.println("Бронь с ошибкой дат: " + err);
                        break;
                    case "0":
                        server.stop(0);
                        running = false;
                        System.out.println("Сервер остановлен.");
                        break;
                    default:
                        System.out.println("Неизвестная команда.");
                        break;
                }
            }
        }
    }

    private static Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}