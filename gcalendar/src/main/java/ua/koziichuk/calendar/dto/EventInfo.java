package ua.koziichuk.calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventInfo {
    private String id;
    private String summary;
    private String description;
    private String start;
    private String end;
}
