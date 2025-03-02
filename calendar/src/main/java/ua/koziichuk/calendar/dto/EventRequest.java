package ua.koziichuk.calendar.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventRequest {
    private String title;
    private String description;
    private String start;
    private String end;
    private String location;
}
