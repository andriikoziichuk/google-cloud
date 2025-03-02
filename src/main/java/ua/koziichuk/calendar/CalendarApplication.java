package ua.koziichuk.calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.koziichuk.calendar.service.CalendarWatchService;

@SpringBootApplication
public class CalendarApplication implements CommandLineRunner {
    @Autowired
    private CalendarWatchService watchService;

    @Override
    public void run(String... args) throws Exception {
        watchService.startWatching("primary");
    }

    public static void main(String[] args) {
        SpringApplication.run(CalendarApplication.class, args);
    }
}
