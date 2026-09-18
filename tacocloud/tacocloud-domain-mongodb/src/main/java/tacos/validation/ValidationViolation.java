package tacos.validation;

public class ValidationViolation {
    private String code;
    private String message;

    public ValidationViolation(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}