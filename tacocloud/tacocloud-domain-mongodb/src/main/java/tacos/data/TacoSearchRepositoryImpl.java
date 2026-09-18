package tacos.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import tacos.Taco;
import tacos.DietaryTag;
import tacos.Allergen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class TacoSearchRepositoryImpl implements TacoSearchRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList("name", "createdAt", "id");
    private static final int MAX_PAGE_SIZE = 50; 

    public TacoSearchRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Taco> searchTacos(String name, String ingredientId, DietaryTag diet, Allergen excludeAllergen, Integer spice, int page, int size, String sortBy, String sortDir) {

        int safeSize = (size <= 0 || size > MAX_PAGE_SIZE) ? 20 : size;
        int safePage = Math.max(0, page);
        long skip = (long) safePage * safeSize;

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            String escapedRegex = Pattern.quote(name.trim());
            criteriaList.add(Criteria.where("name").regex(escapedRegex, "i"));
        }

        if (ingredientId != null && !ingredientId.trim().isEmpty()) {
            criteriaList.add(Criteria.where("ingredients.id").is(ingredientId.trim()));
        }

        if (diet != null) {
            criteriaList.add(Criteria.where("dietaryTags").is(diet));
        }

        if (excludeAllergen != null) {
            criteriaList.add(Criteria.where("allergens").ne(excludeAllergen));
        }

        if (spice != null) {
            criteriaList.add(Criteria.where("spiceLevel").is(spice));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        String safeSortField = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        query.with(Sort.by(direction, safeSortField).and(Sort.by(Sort.Direction.ASC, "id")));
        query.skip(skip).limit(safeSize);

        return mongoTemplate.find(query, Taco.class);
    }
}