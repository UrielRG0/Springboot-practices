package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.TacoOrder;
import tacos.data.OrderRepository;
import tacos.outbox.OutboxEvent;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import tacos.core.OrderPlacementService;
import tacos.core.IdempotencyRecord;

import org.springframework.transaction.ReactiveTransaction;
import tacos.core.CanonicalHasher;

public class OrderPlacementServiceIdempotencyTest {

    private OrderRepository repoMock;
    private ReactiveMongoTemplate mongoMock;
    private ReactiveTransactionManager txManagerMock;
    private OrderPlacementService service;

    @BeforeEach
    public void setup() {
        repoMock = mock(OrderRepository.class);
        mongoMock = mock(ReactiveMongoTemplate.class);
        txManagerMock = mock(ReactiveTransactionManager.class);
        
        ReactiveTransaction mockTx = mock(ReactiveTransaction.class);
        when(txManagerMock.getReactiveTransaction(any())).thenReturn(Mono.just(mockTx));
        when(txManagerMock.commit(any())).thenReturn(Mono.empty());
        when(txManagerMock.rollback(any())).thenReturn(Mono.empty());

        service = new OrderPlacementService(repoMock, mongoMock, txManagerMock);
    }

    @Test
    public void testSequentialIdempotency_SameKeySameHash_ReturnsExistingOrder() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Peter Parker");
        order.setTotal(new BigDecimal("15.50"));
        order.setItems(new ArrayList<>());
        
        String idempotencyKey = "mi-llave-123";
        String userId = "user-abc";
        String recordId = userId + ":" + idempotencyKey;
        String hash = CanonicalHasher.hashOrder(order);

        IdempotencyRecord existingRecord = new IdempotencyRecord();
        existingRecord.setRequestHash(hash);
        existingRecord.setOrderId("order-999");

        TacoOrder existingOrder = new TacoOrder();
        existingOrder.setId("order-999");

        when(mongoMock.findById(recordId, IdempotencyRecord.class)).thenReturn(Mono.just(existingRecord));
        when(repoMock.findById("order-999")).thenReturn(Mono.just(existingOrder));

        StepVerifier.create(service.placeOrderTransactionally(order, idempotencyKey, userId))
            .assertNext(saved -> assertThat(saved.getId()).isEqualTo("order-999"))
            .verifyComplete();

        verify(repoMock, never()).save(any(TacoOrder.class));
        verify(mongoMock, never()).save(any(OutboxEvent.class));
    }

    @Test
    public void testPayloadConflict_SameKeyDifferentHash_Throws409() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Tony Stark"); 
        
        String idempotencyKey = "llave-usada";
        String userId = "user-abc";
        
        IdempotencyRecord existingRecord = new IdempotencyRecord();
        existingRecord.setRequestHash("un-hash-totalmente-distinto"); 

        when(mongoMock.findById(eq(userId + ":" + idempotencyKey), eq(IdempotencyRecord.class)))
            .thenReturn(Mono.just(existingRecord));

        StepVerifier.create(service.placeOrderTransactionally(order, idempotencyKey, userId))
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatus() == HttpStatus.CONFLICT)
            .verify();
    }

    @Test
    public void testConcurrentRequests_ThrowsDuplicateKey_RetriesAndSucceeds() {
        TacoOrder order = new TacoOrder();
        order.setId("order-777");
        order.setDeliveryName("Bruce Wayne");

        String idempotencyKey = "llave-concurrente";
        String userId = "user-abc";
        String recordId = userId + ":" + idempotencyKey;
        String hash = CanonicalHasher.hashOrder(order);

        java.util.concurrent.atomic.AtomicInteger intentos = new java.util.concurrent.atomic.AtomicInteger(0);
        
        when(mongoMock.findById(recordId, IdempotencyRecord.class))
            .thenReturn(Mono.defer(() -> {
                if (intentos.incrementAndGet() == 1) {
                    return Mono.empty(); 
                } else {
                    IdempotencyRecord r = new IdempotencyRecord();
                    r.setRequestHash(hash);
                    r.setOrderId("order-777");
                    return Mono.just(r);
                }
            }));

        when(repoMock.save(any(TacoOrder.class))).thenReturn(Mono.just(order));
        when(mongoMock.save(any(OutboxEvent.class))).thenReturn(Mono.empty());
        when(mongoMock.insert(any(IdempotencyRecord.class)))
            .thenReturn(Mono.error(new DuplicateKeyException("Llave duplicada")));
            
        when(repoMock.findById("order-777")).thenReturn(Mono.just(order));

        StepVerifier.create(service.placeOrderTransactionally(order, idempotencyKey, userId)).assertNext(result -> assertThat(result.getId()).isEqualTo("order-777")).verifyComplete();
            
        assertThat(intentos.get()).isEqualTo(2);
    }
}