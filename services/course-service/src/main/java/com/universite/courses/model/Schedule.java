package com.universite.courses.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "Schedule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "courseId", "dayOfWeek", "startTime", "endTime", "room", "building", "createdAt"})
public class Schedule {
    private Long id;
    private Long courseId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String room;
    private String building;
    private String createdAt;
}
