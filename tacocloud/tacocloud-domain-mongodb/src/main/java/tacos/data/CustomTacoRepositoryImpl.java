package tacos.data;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.Taco;
import tacos.TacoSearchCriteria;
import java.util.regex.Pattern;

public class CustomTacoRepositoryImpl implements CustomTacoRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public CustomTacoRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Taco> searchTacos(TacoSearchCriteria criteria) {
        Query query = buildQuery(criteria);
        String sortField = criteria.getSort().contains("name") ? "name" : "createdAt";
        Sort.Direction direction = criteria.getSort().contains("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), 
                Sort.by(direction, sortField).and(Sort.by(Sort.Direction.ASC, "_id")));
        
        query.with(pageable);
        return mongoTemplate.find(query, Taco.class);
    }

    @Override
    public Mono<Long> countTacos(TacoSearchCriteria criteria) {
        return mongoTemplate.count(buildQuery(criteria), Taco.class);
    }

    private Query buildQuery(TacoSearchCriteria criteria) {
        Query query = new Query();

        if (criteria.getName() != null && !criteria.getName().trim().isEmpty()) {
            query.addCriteria(Criteria.where("name").regex(Pattern.quote(criteria.getName()), "i"));
        }
        if (criteria.getIngredientId() != null) {
            query.addCriteria(Criteria.where("ingredients.id").is(criteria.getIngredientId()));
        }
        if (criteria.getDiet() != null) {
            query.addCriteria(Criteria.where("ingredients.dietaryTags").is(criteria.getDiet()));
        }
        if (criteria.getExcludeAllergen() != null) {
            query.addCriteria(Criteria.where("ingredients.allergens").ne(criteria.getExcludeAllergen()));
        }
        if (criteria.getSpice() != null) {
            query.addCriteria(Criteria.where("ingredients.spiceLevel").gte(criteria.getSpice()));
        }

        return query;
    }
}