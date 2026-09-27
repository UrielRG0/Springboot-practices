package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import tacos.api.dto.ApiProblem;
import tacos.api.dto.GlobalExceptionHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiProblemExceptionHandlerTest {

    // 409 
    @Test
    public void testBusinessException_Returns409Conflict() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/orders");
        
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.CONFLICT, "INSUFFICIENT_STOCK");
        ResponseEntity<ApiProblem> response = handler.handleResponseStatusException(ex, request);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertTrue(response.getBody().getDetail().contains("INSUFFICIENT_STOCK"));
    }

    //500
    @Test
    public void testInternalError_DoesNotLeakStackTrace() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/orders");
        
        Exception ex = new RuntimeException("Timeout of the driver db.mongodb.net");
        
        ResponseEntity<ApiProblem> response = handler.handleGenericException(ex, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        
        String errorDetail = response.getBody().getDetail();
        
        assertFalse(errorDetail.contains("MongoSocketException"), "implementation error");
        assertFalse(errorDetail.contains("java.lang"), "stacktrace error");
        assertEquals("An unexpected error occurred on the server. Please try again later.", errorDetail);
    }
}