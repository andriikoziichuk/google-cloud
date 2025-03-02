package ua.koziichuk.calendar.controller;

import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.koziichuk.calendar.service.CalendarWatchService;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/webhook")
public class CalendarWebhookController {

    @Autowired
    private CalendarWatchService watchService;

    @PostMapping("/calendar")
    public ResponseEntity<Set<Event>> handleCalendarUpdate(@RequestHeader("X-Goog-Resource-ID") String resourceId) {
        return ResponseEntity.ok(watchService.processCalendarChanges(resourceId));
    }
}
