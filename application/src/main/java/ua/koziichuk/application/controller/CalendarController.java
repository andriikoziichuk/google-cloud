package ua.koziichuk.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.koziichuk.calendar.dto.EventRequest;
import ua.koziichuk.calendar.dto.ScheduleData;
import ua.koziichuk.calendar.service.GoogleCalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
public class CalendarController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @GetMapping("/events")
    public ResponseEntity<List<?>> getFollowingEvents() throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(googleCalendarService.getFollowingItems());
    }

    @PostMapping("/events/add")
    public ResponseEntity<?> addEvent(@RequestBody EventRequest event) throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(googleCalendarService.insertEvent(event));
    }

    @PostMapping("/events/schedule")
    public ResponseEntity<Void> scheduleEvents(@RequestBody ScheduleData scheduleData) throws Exception {
        googleCalendarService.scheduleEvents(scheduleData);
        return ResponseEntity.ok().build();
    }
}
