package tacos.api.dto;

import lombok.Data;

@Data
public class ReorderRequest {
    private String paymentToken; 
    private boolean confirmPriceChange; 
}