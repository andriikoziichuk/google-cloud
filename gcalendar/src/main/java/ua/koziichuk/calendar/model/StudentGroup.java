package ua.koziichuk.calendar.model;

import lombok.Data;

import java.util.List;

@Data
public class StudentGroup {
    private String group;
    private String calendarId;
    private List<TimeSlot> availability;
}
