package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import tacos.admin.OpsAnnouncementController;
import tacos.admin.OpsAnnouncementRepository;
import tacos.admin.OpsAnnouncement;

public class OpsAnnouncementControllerTest {

    private OpsAnnouncementRepository repoMock;
    private OpsAnnouncementController controller;
    private Clock fixedClock;

    private final Authentication adminAuth = new TestingAuthenticationToken("admin_user", "pass", "ROLE_ADMIN");
    private final Authentication userAuth = new TestingAuthenticationToken("normal_user", "pass", "ROLE_USER");

    @BeforeEach
    public void setup() {
        repoMock = mock(OpsAnnouncementRepository.class);
        controller = new OpsAnnouncementController(repoMock);

        fixedClock = Clock.fixed(Instant.parse("2026-09-28T12:00:00Z"), ZoneId.of("UTC"));
        controller.setClock(fixedClock);
    }

    @Test
    public void testSecurity_NonAdminCannotWriteOrDelete() {
        OpsAnnouncement req = new OpsAnnouncement();
        req.setText("Test");

        StepVerifier.create(controller.createAnnouncement(req, userAuth))
            .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                ((ResponseStatusException) throwable).getStatus() == HttpStatus.FORBIDDEN)
            .verify();

        StepVerifier.create(controller.deleteAnnouncement("123", userAuth))
            .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                ((ResponseStatusException) throwable).getStatus() == HttpStatus.FORBIDDEN)
            .verify();
    }

    @Test
    public void testValidation_RejectsEmptyAndControlCharacters() {
        OpsAnnouncement badReq1 = new OpsAnnouncement();
        badReq1.setText("   "); 

        OpsAnnouncement badReq2 = new OpsAnnouncement();
        badReq2.setText("Alerta con salto de linea\n");

        StepVerifier.create(controller.createAnnouncement(badReq1, adminAuth))
            .expectErrorMatches(t -> ((ResponseStatusException) t).getStatus() == HttpStatus.BAD_REQUEST)
            .verify();

        StepVerifier.create(controller.createAnnouncement(badReq2, adminAuth))
            .expectErrorMatches(t -> ((ResponseStatusException) t).getStatus() == HttpStatus.BAD_REQUEST)
            .verify();
    }

    @Test
    public void testExpiration_UsesClockAndQueriesRepo() {
        controller.getActiveAnnouncements();
        verify(repoMock, times(1)).findByActiveTrueAndExpiresAtAfter(fixedClock.instant());
    }

    @Test
    public void testLimits_CannotExceed5ActiveAnnouncements() {
        OpsAnnouncement req = new OpsAnnouncement();
        req.setText("Anuncio de prueba");

        when(repoMock.countByActiveTrueAndExpiresAtAfter(fixedClock.instant())).thenReturn(Mono.just(5L));

        StepVerifier.create(controller.createAnnouncement(req, adminAuth))
            .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                ((ResponseStatusException) throwable).getStatus() == HttpStatus.CONFLICT)
            .verify();
    }

    @Test
    public void testLogicalDelete_SetsActiveToFalse() {
        OpsAnnouncement existing = new OpsAnnouncement();
        existing.setId("uuid-123");
        existing.setActive(true);

        when(repoMock.findById("uuid-123")).thenReturn(Mono.just(existing));
        when(repoMock.save(any(OpsAnnouncement.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(controller.deleteAnnouncement("uuid-123", adminAuth))
            .verifyComplete();

        ArgumentCaptor<OpsAnnouncement> captor = ArgumentCaptor.forClass(OpsAnnouncement.class);
        verify(repoMock).save(captor.capture());
        
        assertThat(captor.getValue().isActive()).isFalse();
    }
}