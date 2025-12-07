package com.universite.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        
        // Customize error response
        errorAttributes.put("timestamp", LocalDateTime.now());
        errorAttributes.put("path", request.path());
        errorAttributes.put("service", "API Gateway");
        
        Throwable error = getError(request);
        if (error != null) {
            log.error("Error occurred: {}", error.getMessage());
            errorAttributes.put("message", error.getMessage());
        }
        
        return errorAttributes;
    }
}
