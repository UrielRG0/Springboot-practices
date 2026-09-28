package tacos.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class TacoMetricsService {
    private final MeterRegistry registry;

    public TacoMetricsService(MeterRegistry registry) {
        this.registry = registry;
    }

    public void recordOrderCreated() {
        registry.counter("tacocloud.orders", "result", "success").increment();
    }

    public void recordOrderFailed() {
        registry.counter("tacocloud.orders", "result", "failed").increment();
    }

    public void recordStockRejection() {
        registry.counter("tacocloud.inventory.rejections").increment();
    }

    public void recordDlqEvent() {
        registry.counter("tacocloud.dlq.events").increment();
    }

    public void recordPlacementTime(long milliseconds) {
        registry.timer("tacocloud.orders.placement.time").record(milliseconds, TimeUnit.MILLISECONDS);
    }
}