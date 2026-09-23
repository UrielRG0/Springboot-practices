package tacos.web.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.TacoOrder;
import tacos.data.OrderRepository;
import tacos.messaging.OrderMessagingService; // Import que sí tienes

public class OrderApiControllerTestPost {

    private EmailOrderService emailOrderService;
    private OrderRepository repo;
    private OrderMessagingService orderMessages;
    
    private OrderApiController controller;

    @BeforeEach
    public void setUp() {
        emailOrderService = mock(EmailOrderService.class);
        repo = mock(OrderRepository.class);
        orderMessages = mock(OrderMessagingService.class);
        
        // ¡OJO AQUÍ!: Asegúrate de que tu constructor real del OrderApiController coincida con esto.
        // Si tu constructor pide más cosas (como InventoryService), agrégalas aquí como mocks.
        controller = new OrderApiController(repo, orderMessages, emailOrderService); 
    }

    @Test
    public void testColdPublisher_And_ExactlyOneInvocation() {
        EmailOrder emailOrder = new EmailOrder();
        TacoOrder mockOrder = new TacoOrder();
        
        when(emailOrderService.convertEmailOrderToDomainOrder(any(Mono.class))).thenReturn(Mono.just(mockOrder));
        when(repo.save(any())).thenReturn(Mono.just(mockOrder));

        Mono<TacoOrder> resultMono = controller.postOrderFromEmail(emailOrder);

        // Aún no hay suscripción, nada se ejecuta
        verify(repo, never()).save(any());
        verify(orderMessages, never()).sendOrder(any());

        StepVerifier.create(resultMono)
            .expectNextCount(1)
            .verifyComplete();

        // Exactamente una invocación
        verify(repo, times(1)).save(any());
        verify(orderMessages, times(1)).sendOrder(any());
    }

    @Test
    public void testConversionError_CancelsChain() {
        EmailOrder emailOrder = new EmailOrder();
        
        when(emailOrderService.convertEmailOrderToDomainOrder(any(Mono.class)))
            .thenReturn(Mono.error(new IllegalArgumentException("User not found")));

        Mono<TacoOrder> resultMono = controller.postOrderFromEmail(emailOrder);

        StepVerifier.create(resultMono)
            .expectErrorMatches(throwable -> 
                throwable instanceof ResponseStatusException && 
                ((ResponseStatusException) throwable).getStatus() == HttpStatus.BAD_REQUEST)
            .verify();

        verify(repo, never()).save(any());
        verify(orderMessages, never()).sendOrder(any());
    }

    @Test
    public void testPersistenceError_DoesNotPublish() {
        EmailOrder emailOrder = new EmailOrder();
        TacoOrder mockOrder = new TacoOrder();
        
        when(emailOrderService.convertEmailOrderToDomainOrder(any(Mono.class))).thenReturn(Mono.just(mockOrder));
        when(repo.save(any())).thenReturn(Mono.error(new RuntimeException("DB Down")));

        Mono<TacoOrder> resultMono = controller.postOrderFromEmail(emailOrder);

        StepVerifier.create(resultMono)
            .expectError(RuntimeException.class)
            .verify();

        verify(repo, times(1)).save(any());
        verify(orderMessages, never()).sendOrder(any()); // No se publica si falla el guardado
    }
}