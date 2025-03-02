package ua.koziichuk.calendar.model;

import lombok.Data;

@Data
public class TimeSlot {
    private int day; // 1 = Monday, 7 = Sunday
    private String start;
    private String end;
}
