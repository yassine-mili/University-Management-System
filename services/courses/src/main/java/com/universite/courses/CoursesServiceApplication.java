package main.java.com.universite.courses;

import com.universite.courses.service.CourseServiceImpl;
import com.universite.courses.util.DatabaseManager;
import jakarta.persistence.EntityManager;
import jakarta.xml.ws.Endpoint;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.xml.ws.transport.http.servlet.WSServlet;
import com.sun.xml.ws.transport.http.servlet.WSServletContextListener;

@Slf4j
public class CoursesServiceApplication {
    
    private static final int PORT = getPort();
    private static final String SERVICE_PATH = "/CourseService";
    
    public static void main(String[] args) {
        try {
            log.info("========================================");
            log.info("Starting Courses Service");
            log.info("========================================");
            
            // Initialize database
            DatabaseManager.initialize();
            
            // Create entity manager
            EntityManager entityManager = DatabaseManager.getEntityManager();
            
            // Create service implementation
            CourseServiceImpl courseService = new CourseServiceImpl(entityManager);
            
            // Start embedded server
            startJettyServer(courseService);
            
        } catch (Exception e) {
            log.error("Failed to start Courses Service: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    private static void startJettyServer(CourseServiceImpl courseService) throws Exception {
        Server server = new Server(PORT);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        
        // Add WSServletContextListener
        context.addEventListener(new WSServletContextListener());
        
        // Add WSServlet
        ServletHolder servletHolder = new ServletHolder(new WSServlet());
        context.addServlet(servletHolder, SERVICE_PATH);
        
        // Publish SOAP endpoint
        String address = "http://0.0.0.0:" + PORT + SERVICE_PATH;
        Endpoint endpoint = Endpoint.publish(address, courseService);
        
        server.start();
        
        log.info("========================================");
        log.info("✓ Courses Service started successfully");
        log.info("Port: {}", PORT);
        log.info("SOAP Endpoint: {}", address);
        log.info("WSDL: {}?wsdl", address);
        log.info("========================================");
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down Courses Service...");
            try {
                endpoint.stop();
                server.stop();
                DatabaseManager.close();
                log.info("Courses Service stopped successfully");
            } catch (Exception e) {
                log.error("Error during shutdown: {}", e.getMessage());
            }
        }));
        
        server.join();
    }
    
    private static int getPort() {
        String portStr = System.getenv("PORT");
        if (portStr != null && !portStr.trim().isEmpty()) {
            try {
                return Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid PORT environment variable: {}, using default 8083", portStr);
            }
        }
        return 8083;
    }
}
