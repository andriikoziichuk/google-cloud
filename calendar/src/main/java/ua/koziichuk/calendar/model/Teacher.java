package ua.koziichuk.calendar.model;

import lombok.Data;

import java.util.List;

@Data
public class Teacher {
    private String id;
    private String calendarId;
    private List<TimeSlot> availability;
}
