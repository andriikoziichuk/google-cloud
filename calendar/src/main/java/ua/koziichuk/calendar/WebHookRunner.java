package ua.koziichuk.calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ua.koziichuk.calendar.service.CalendarWatchService;

@Component
public class WebHookRunner implements CommandLineRunner {
    @Autowired
    private CalendarWatchService watchService;

    @Override
    public void run(String... args) throws Exception {
        watchService.startWatching("primary");
    }
}
