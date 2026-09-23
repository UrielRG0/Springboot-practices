package tacos.web.api;

import tacos.Taco;
import java.time.LocalDate;

public class TacoOfTheDayResponse {
    private Taco taco;
    private LocalDate date;
    private String reason;

    public TacoOfTheDayResponse(Taco taco, LocalDate date, String reason) {
        this.taco = taco;
        this.date = date;
        this.reason = reason;
    }

    public Taco getTaco() { return taco; }
    public LocalDate getDate() { return date; }
    public String getReason() { return reason; }
}