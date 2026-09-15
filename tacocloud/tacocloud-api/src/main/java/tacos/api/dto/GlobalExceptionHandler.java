package tacos.api.dto;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;

import tacos.api.dto.ApiProblem; 
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiProblem> handleValidationErrors(WebExchangeBindException ex, ServerHttpRequest request) {
        
        List<ApiProblem.Violation> violations = ex.getBindingResult().getFieldErrors().stream().map(error -> ApiProblem.Violation.builder().field(error.getField())
            .message(error.getDefaultMessage()).build()).collect(Collectors.toList());

        ApiProblem problem = ApiProblem.builder().type("https://tacocloud.com/errors/validation").title("Error data validation").status(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .detail("The request contains invalid or incomplete data.").instance(request.getPath().pathWithinApplication().value()).code("ERR_VALIDATION_422").violations(violations).build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ApiProblem> handleResponseStatusException(org.springframework.web.server.ResponseStatusException ex, ServerHttpRequest request) {
        
        ApiProblem problem = ApiProblem.builder().type("https://tacocloud.com/errors/http-error").title(ex.getStatus().getReasonPhrase()).status(ex.getStatus().value())
            .detail(ex.getReason() != null ? ex.getReason() : "Operation not allowed or resource not found").instance(request.getPath().pathWithinApplication().value())
            .code("ERR_HTTP_" + ex.getStatus().value()).build();

        return ResponseEntity.status(ex.getStatus()).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiProblem> handleGenericException(Exception ex, ServerHttpRequest request) {
        
        ApiProblem problem = ApiProblem.builder().type("https://tacocloud.com/errors/internal").title("Internal Server Error")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).detail("An unexpected error occurred on the server. Please try again later.")
            .instance(request.getPath().pathWithinApplication().value())
            .code("ERR_INTERNAL_500").build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

}