package ua.koziichuk.calendar.model;

import lombok.Data;

@Data
public class ReminderSettings {
    private String method; // "email" або "popup"
    private int minutesBefore;
}
