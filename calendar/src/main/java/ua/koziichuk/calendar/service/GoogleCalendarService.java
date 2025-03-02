package ua.koziichuk.calendar.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.koziichuk.calendar.dto.EventRequest;
import ua.koziichuk.calendar.dto.ScheduleData;
import ua.koziichuk.calendar.model.Course;
import ua.koziichuk.calendar.model.TimeSlot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoogleCalendarService {
    @Autowired
    private Calendar calendar;
    @Autowired
    private TimeSlotFinder timeSlotFinder;

    public List<Event> getFollowingItems() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = calendar.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }

    public Event insertEvent(EventRequest event) throws IOException, GeneralSecurityException {
        Event content = buildEvent(event);
        return createEvent(content);
    }

    public void scheduleEvents(ScheduleData scheduleData) throws Exception {
        for (Course course : scheduleData.getCourses()) {
            List<String> calendars = new ArrayList<>();
            // Додаємо календарі груп
            scheduleData.getStudents().stream()
                    .filter(s -> course.getGroups().contains(s.getGroup()))
                    .forEach(s -> calendars.add(s.getCalendarId()));
            // Додаємо календарі викладачів
            scheduleData.getTeachers().stream()
                    .filter(t -> course.getTeachers().contains(t.getId()))
                    .forEach(t -> calendars.add(t.getCalendarId()));

            TimeSlot slot = timeSlotFinder.findFreeTimeSlot(calendar, calendars, course.getDuration());
            if (slot != null) {
                createEvent(new Event()
                        .setSummary(course.getName())
                        .setStart(new EventDateTime().setDateTime(new DateTime(slot.getStart())))
                        .setEnd(new EventDateTime().setDateTime(new DateTime(slot.getEnd()))));
            }
        }
    }

    private Event createEvent(Event content) throws IOException, GeneralSecurityException {
        return calendar.events().insert("primary", content).execute();
    }

    private Event buildEvent(EventRequest eventRequest) {
        return new Event()
                .setSummary(eventRequest.getTitle())
                .setDescription(eventRequest.getDescription())
                .setLocation(eventRequest.getLocation())
                .setStart(new EventDateTime().setDateTime(new DateTime(eventRequest.getStart())))
                .setEnd(new EventDateTime().setDateTime(new DateTime(eventRequest.getEnd())));
    }

}