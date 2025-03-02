package ua.koziichuk.calendar.model;

import lombok.Data;

import java.util.List;

@Data
public class Course {
    private String name;
    private List<String> groups;
    private List<String> teachers;
    private int duration;
    private String frequency;
    private int recurrence;
    private List<ReminderSettings> reminders;
}
