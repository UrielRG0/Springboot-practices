package tacos.web.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.TacoOrder;
import tacos.data.OrderRepository;
import tacos.messaging.OrderMessagingService;

public class OrderApiControllerTestPost {

    @Mock
    private EmailOrderService emailOrderService;
    
    @Mock
    private OrderRepository repo;
    
    @Mock
    private OrderMessagingService orderMessages;
    
    @InjectMocks
    private OrderApiController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testColdPublisher_And_ExactlyOneInvocation() {
        EmailOrder emailOrder = new EmailOrder();
        TacoOrder mockOrder = new TacoOrder();
        
        when(emailOrderService.convertEmailOrderToDomainOrder(any(Mono.class))).thenReturn(Mono.just(mockOrder));
        when(repo.save(any())).thenReturn(Mono.just(mockOrder));

        Mono<TacoOrder> resultMono = controller.postOrderFromEmail(emailOrder);
        verify(repo, never()).save(any());
        verify(orderMessages, never()).sendOrder(any());

        StepVerifier.create(resultMono)
            .expectNextCount(1)
            .verifyComplete();
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
        verify(orderMessages, never()).sendOrder(any());
    }
}