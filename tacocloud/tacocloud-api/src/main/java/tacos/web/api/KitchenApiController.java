package tacos.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tacos.TacoOrder;
import tacos.User;
import tacos.api.dto.KitchenOrderDTO;
import tacos.KitchenQueueService;

@RestController
@RequestMapping(path = {"/api/v1/kitchen","/api/kitchen"}, produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class KitchenApiController {

    private final KitchenQueueService queueService;

    public KitchenApiController(KitchenQueueService queueService) {
        this.queueService = queueService;
    }

    private void verifyKitchenAccess(User user) {
        if (user == null || user.getRole() == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you must sign in");
        if (!user.getRole().contains("ADMIN") && !user.getRole().contains("KITCHEN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only personal can acces");
        }
    }

    private KitchenOrderDTO toKitchenDTO(TacoOrder order) {
        KitchenOrderDTO dto = new KitchenOrderDTO();
        dto.setId(order.getId());
        dto.setPlacedAt(order.getPlacedAt());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setCookId(order.getCookId());
        dto.setEstimatedPrepMinutes(order.getEstimatedPrepMinutes());
        dto.setItems(order.getItems());
        return dto;
    }

    @GetMapping("/queue")
    public Flux<KitchenOrderDTO> getQueue(@AuthenticationPrincipal User cook) {
        verifyKitchenAccess(cook);
        return queueService.getQueue().map(this::toKitchenDTO);
    }

    @PostMapping("/orders/claim")
    public Mono<ResponseEntity<KitchenOrderDTO>> claimNext(@AuthenticationPrincipal User cook) {
        verifyKitchenAccess(cook);
        
        return queueService.claimNextOrder(cook)
                .map(order -> ResponseEntity.ok(toKitchenDTO(order)))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ups apparently we dont have orders ")));
    }
}