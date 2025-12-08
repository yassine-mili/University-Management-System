package main.java.com.universite.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse implements Serializable {
    private boolean success;
    private String message;
    private Object data;
    
    public static ServiceResponse success(String message, Object data) {
        return new ServiceResponse(true, message, data);
    }
    
    public static ServiceResponse success(String message) {
        return new ServiceResponse(true, message, null);
    }
    
    public static ServiceResponse error(String message) {
        return new ServiceResponse(false, message, null);
    }
}
