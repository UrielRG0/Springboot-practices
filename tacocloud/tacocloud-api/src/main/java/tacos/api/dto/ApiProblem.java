package tacos.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiProblem {
    
    private String type;
    private String title;
    private Integer status;
    private String detail;
    private String instance; 
    private String code;     
    
    private List<Violation> violations; 

    @Data
    @Builder
    public static class Violation {
        private String field;
        private String message;
    }
}