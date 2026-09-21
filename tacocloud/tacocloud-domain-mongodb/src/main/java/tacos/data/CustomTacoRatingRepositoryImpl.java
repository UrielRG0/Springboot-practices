package tacos.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import reactor.core.publisher.Flux;
import tacos.TacoRankingDTO;
import tacos.TacoRating;

public class CustomTacoRatingRepositoryImpl implements CustomTacoRatingRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public CustomTacoRatingRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<TacoRankingDTO> getTopTacos(int limit, int minVotes) {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.group("tacoId")
                .avg("score").as("averageScore")
                .count().as("totalVotes"),
            Aggregation.match(Criteria.where("totalVotes").gte(minVotes)),
            Aggregation.sort(Sort.Direction.DESC, "averageScore").and(Sort.Direction.DESC, "totalVotes"),
            Aggregation.limit(limit),
            Aggregation.project("averageScore", "totalVotes").and("_id").as("tacoId")
        );

        return mongoTemplate.aggregate(agg, TacoRating.class, TacoRankingDTO.class);
    }
}