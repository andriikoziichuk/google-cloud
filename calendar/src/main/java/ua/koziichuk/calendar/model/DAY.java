package ua.koziichuk.calendar.model;

import lombok.Getter;

@Getter
public enum DAY {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    private final int day;

    DAY(int day) {
        this.day = day;
    }
}
