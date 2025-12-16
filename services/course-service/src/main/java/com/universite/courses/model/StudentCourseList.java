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
@XmlRootElement(name = "StudentCourseList")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudentCourseList {
    @XmlElement(name = "studentCourse")
    private List<StudentCourse> studentCourses = new ArrayList<>();
}
