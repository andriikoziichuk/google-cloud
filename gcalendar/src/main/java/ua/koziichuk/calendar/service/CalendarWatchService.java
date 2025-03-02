package ua.koziichuk.calendar.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.koziichuk.calendar.dto.EventInfo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CalendarWatchService {

    @Value("${google.calendar.webhook.url}")
    private String webhookUrl;

    @Autowired
    private Calendar calendar;

    private String syncToken;

    public void startWatching(String calendarId) throws Exception {
        Channel channel = new Channel()
                .setId(UUID.randomUUID().toString())
                .setType("web_hook")
                .setAddress(webhookUrl)
                .setExpiration(System.currentTimeMillis() + 604800000L); // 7 днів

        calendar.events().watch(calendarId, channel).execute();
    }

    public Set<EventInfo> processCalendarChanges(String resourceId) {
        try {
            Set<Event> events = fetchEventDetails(resourceId);
            logChanges(events);
            return events.stream()
                    .map(this::eventConverter)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Error processing changes", e);
            return null;
        }
    }

    private EventInfo eventConverter(Event event) {
        return EventInfo.builder()
                .id(event.getId())
                .summary(event.getSummary())
                .description(event.getDescription())
                .start(event.getStart().getDateTime().toStringRfc3339())
                .end(event.getEnd().getDateTime().toStringRfc3339())
                .build();
    }

    private Set<Event> fetchEventDetails(String calendarId) throws Exception {
        DateTime oneMinuteAgo = new DateTime(System.currentTimeMillis() - 60000);

        Calendar.Events.List request = calendar.events().list("primary");

        if (syncToken != null) {
            request.setSyncToken(syncToken);
        } else {
            request.setOrderBy("updated")
                    .setShowDeleted(true)
                    .setUpdatedMin(oneMinuteAgo);
        }

        Events response = request.execute();

        this.syncToken = response.getNextSyncToken();
        return new HashSet<>(response.getItems());
    }

    private void logChanges(Set<Event> events) {
        events.forEach(event ->
                log.warn("""
                                [ЗМІНА В РОЗКЛАДІ]
                                Час: {}
                                Подія: {}
                                Статус: {}
                                Оновлено: {}
                                ------------------------
                                """,
                        new Date(),
                        event.getSummary(),
                        event.getStatus(),
                        event.getUpdated()
                )
        );
    }
}
