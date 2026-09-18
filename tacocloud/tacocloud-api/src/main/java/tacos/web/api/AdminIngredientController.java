package tacos.web.api;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tacos.Ingredient;
import tacos.data.IngredientRepository;
import tacos.api.dto.IngredientCatalogPatch;
import tacos.api.dto.StockAdjustmentRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/admin/ingredients", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminIngredientController {

    private final IngredientRepository repo;

    public AdminIngredientController(IngredientRepository repo) {
        this.repo = repo;
    }

    // PATCH /api/admin/ingredients/{id}/catalog
    @PatchMapping(path = "/{id}/catalog", consumes = "application/json")
    public Mono<ResponseEntity<Ingredient>> patchCatalog(@PathVariable String id, @Valid @RequestBody IngredientCatalogPatch patch) {
        
        return repo.findById(id).flatMap(ingredient -> {
            if (patch.getUnitPrice() != null) ingredient.setUnitPrice(patch.getUnitPrice());
            if (patch.getAvailable() != null) ingredient.setAvailable(patch.getAvailable());
            
            return repo.save(ingredient).map(ResponseEntity::ok);
        }).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // POST /api/admin/ingredients/{id}/stock-adjustments
    @PostMapping(path = "/{id}/stock-adjustments", consumes = "application/json")
    public Mono<ResponseEntity<?>> adjustStock(@PathVariable String id, @RequestBody StockAdjustmentRequest adjustment) {
        
        return repo.findById(id).flatMap(ingredient -> {
            int newStock = ingredient.getStockOnHand() + adjustment.getQuantity();
            
            if (newStock < 0) {
                return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Error: El ajuste produciría inventario negativo."));
            }
            
            ingredient.setStockOnHand(newStock);
            return repo.save(ingredient).map(ResponseEntity::ok);
        }).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Manejador del colisiones (Optimistic Locking) -> 409 Conflict
    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConcurrencyError() {
        return "Conflicto de versión: El ingrediente fue modificado por otro usuario. Recargue los datos e intente de nuevo.";
    }
}