package tacos;

import lombok.Data;

@Data
public class TacoRankingDTO {
    private String tacoId;
    private double averageScore;
    private int totalVotes;

    public double getAverageScore() {
        return Math.round(this.averageScore * 10.0) / 10.0;
    }
}