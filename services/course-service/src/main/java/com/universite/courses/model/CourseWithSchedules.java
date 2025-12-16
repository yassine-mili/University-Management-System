package com.universite.courses.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "CourseWithSchedules")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"course", "schedules"})
public class CourseWithSchedules {
    private Course course;
    
    @XmlElementWrapper(name = "schedules")
    @XmlElement(name = "schedule")
    private List<Schedule> schedules = new ArrayList<>();
}
