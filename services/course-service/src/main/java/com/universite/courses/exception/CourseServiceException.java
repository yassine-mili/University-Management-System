package com.universite.courses.exception;
import lombok.Getter;

@Getter
public class CourseServiceException extends Exception {
    private final String code;
    private final String details;

    public CourseServiceException(String message) {
        super(message);
        this.code = "UNKNOWN_ERROR";
        this.details = "";
    }

    public CourseServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = "UNKNOWN_ERROR";
        this.details = cause == null ? "" : cause.getMessage();
    }

    public CourseServiceException(String code, String message, String details) {
        super(message);
        this.code = code == null ? "UNKNOWN_ERROR" : code;
        this.details = details == null ? "" : details;
    }
}
