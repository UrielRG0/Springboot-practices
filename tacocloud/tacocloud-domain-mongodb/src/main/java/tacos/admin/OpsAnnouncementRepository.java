package tacos.admin;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Instant;

public interface OpsAnnouncementRepository extends ReactiveMongoRepository<OpsAnnouncement, String> {

    Flux<OpsAnnouncement> findByActiveTrueAndExpiresAtAfter(Instant now);
    
    Mono<Long> countByActiveTrueAndExpiresAtAfter(Instant now);
}