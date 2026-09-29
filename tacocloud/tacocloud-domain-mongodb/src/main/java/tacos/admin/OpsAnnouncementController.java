package tacos.admin;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/announcements")
public class OpsAnnouncementController {

    private final OpsAnnouncementRepository repo;
    private Clock clock; 

    public OpsAnnouncementController(OpsAnnouncementRepository repo) {
        this.repo = repo;
        this.clock = Clock.systemUTC();
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @GetMapping
    public Flux<OpsAnnouncement> getActiveAnnouncements() {
        return repo.findByActiveTrueAndExpiresAtAfter(clock.instant());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OpsAnnouncement> createAnnouncement(@RequestBody OpsAnnouncement request, Authentication auth) {

        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().contains("ADMIN"))) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "ONly admins can create this"));
        }

        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "the text cant be empty"));
        }
        if (request.getText().matches(".*\\p{Cntrl}.*")) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "control characters not permited"));
        }
        if (request.getText().length() > 255) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "the text need to be below 255"));
        }
        return repo.countByActiveTrueAndExpiresAtAfter(clock.instant())
            .flatMap(count -> {
                if (count >= 5) {
                    return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Max limit of 5 annuncment reached"));
                }

                OpsAnnouncement ann = new OpsAnnouncement();
                ann.setId(UUID.randomUUID().toString()); 
                ann.setText(request.getText());
                ann.setSeverity(request.getSeverity() != null ? request.getSeverity() : "INFO");
                ann.setCreatedAt(clock.instant());
                ann.setExpiresAt(request.getExpiresAt() != null ? request.getExpiresAt() : clock.instant().plus(24, ChronoUnit.HOURS));
                ann.setCreatedBy(auth.getName());
                ann.setActive(true);

                return repo.save(ann);
            });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAnnouncement(@PathVariable String id, Authentication auth) {
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().contains("ADMIN"))) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete annuncments"));
        }
        return repo.findById(id)
            .flatMap(ann -> {
                ann.setActive(false);
                return repo.save(ann);
            })
            .then();
    }
}