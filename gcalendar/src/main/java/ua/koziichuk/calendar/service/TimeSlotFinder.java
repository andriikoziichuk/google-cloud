package ua.koziichuk.calendar.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.stereotype.Service;
import ua.koziichuk.calendar.model.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeSlotFinder {
    public TimeSlot findFreeTimeSlot(Calendar service, List<String> calendars, int durationMinutes)
            throws Exception {

        // 1. Запит до FreeBusy API
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime weekLater = new DateTime(System.currentTimeMillis() + 604800000L); // +7 днів
        List<FreeBusyRequestItem> items = calendars.stream()
                .map(id -> new FreeBusyRequestItem().setId(id))
                .collect(Collectors.toList());

        FreeBusyRequest request = new FreeBusyRequest()
                .setTimeMin(now)
                .setTimeMax(weekLater)
                .setItems(items);

        FreeBusyResponse response = service.freebusy().query(request).execute();

        // 2. Збір та об'єднання зайнятих періодів
        List<TimePeriod> allBusy = new ArrayList<>();
        for (FreeBusyCalendar calendar : response.getCalendars().values()) {
            allBusy.addAll(calendar.getBusy());
        }

        // Сортування за часом початку
        allBusy.sort(Comparator.comparing(t -> t.getStart().getValue()));

        // Об'єднання перетинаючихся періодів
        List<TimePeriod> mergedBusy = new ArrayList<>();
        if (!allBusy.isEmpty()) {
            TimePeriod current = allBusy.get(0);
            for (TimePeriod period : allBusy) {
                if (period.getStart().getValue() <= current.getEnd().getValue()) {
                    if (period.getEnd().getValue() > current.getEnd().getValue()) {
                        current.setEnd(period.getEnd());
                    }
                } else {
                    mergedBusy.add(current);
                    current = period;
                }
            }
            mergedBusy.add(current);
        }

        // 3. Пошук вільного слоту
        ZoneId zone = ZoneId.of("Europe/Kiev");
        long durationMillis = durationMinutes * 60 * 1000L;

        // Перебір днів у наступному тижні
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i);

            // Фільтрація за днями тижня з JSON-availability (приклад для понеділка)
            if (date.getDayOfWeek() != DayOfWeek.MONDAY) continue;

            // Генерація слотів 9:00-17:00
            LocalDateTime dayStart = date.atTime(9, 0);
            LocalDateTime dayEnd = date.atTime(17, 0);

            long startMillis = dayStart.atZone(zone).toInstant().toEpochMilli();
            long endMillis = dayEnd.atZone(zone).toInstant().toEpochMilli();

            // Перевірка кожних 15 хвилин
            for (long slotStart = startMillis;
                 slotStart + durationMillis <= endMillis;
                 slotStart += 15 * 60 * 1000) {

                long slotEnd = slotStart + durationMillis;
                boolean isFree = true;

                // Перевірка конфліктів з зайнятими періодами
                for (TimePeriod busy : mergedBusy) {
                    long busyStart = busy.getStart().getValue();
                    long busyEnd = busy.getEnd().getValue();

                    if (slotStart < busyEnd && slotEnd > busyStart) {
                        isFree = false;
                        break;
                    }
                }

                if (isFree) {
                    TimeSlot slot = new TimeSlot();
                    slot.setStart(new DateTime(slotStart).toStringRfc3339());
                    slot.setEnd(new DateTime(slotEnd).toStringRfc3339());
                    return slot;
                }
            }
        }
        return null;
    }
}
