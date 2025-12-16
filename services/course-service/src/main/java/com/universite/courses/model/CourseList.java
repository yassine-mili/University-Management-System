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
@XmlRootElement(name = "CourseList")
@XmlAccessorType(XmlAccessType.FIELD)
public class CourseList {
    @XmlElement(name = "course")
    private List<Course> courses = new ArrayList<>();
}
