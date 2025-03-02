package ua.koziichuk.calendar.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ua.koziichuk.calendar.model.Course;
import ua.koziichuk.calendar.model.StudentGroup;
import ua.koziichuk.calendar.model.Teacher;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleData {
    private List<StudentGroup> students;
    private List<Teacher> teachers;
    private List<Course> courses;
}
