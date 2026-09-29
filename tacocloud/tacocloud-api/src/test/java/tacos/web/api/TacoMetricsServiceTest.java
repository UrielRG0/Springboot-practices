package tacos.web.api;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import tacos.metrics.TacoMetricsService;

public class TacoMetricsServiceTest {

    private MeterRegistry registry;
    private TacoMetricsService metricsService;

    @BeforeEach
    public void setUp() {
        registry = new SimpleMeterRegistry();
        metricsService = new TacoMetricsService(registry);
    }

    @Test
    public void testRecordOrderCreated_IncrementsCounter() {
        metricsService.recordOrderCreated();

        double count = registry.counter("tacocloud.orders", "result", "success").count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    public void testRecordOrderFailed_IncrementsCounter() {
        metricsService.recordOrderFailed();

        double count = registry.counter("tacocloud.orders", "result", "failed").count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    public void testRecordStockRejection_IncrementsCounter() {
        metricsService.recordStockRejection();

        double count = registry.counter("tacocloud.inventory.rejections").count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    public void testRecordDlqEvent_IncrementsCounter() {
        metricsService.recordDlqEvent();

        double count = registry.counter("tacocloud.dlq.events").count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    public void testRecordPlacementTime_RecordsTimer() {
        metricsService.recordPlacementTime(250); 

        long count = registry.timer("tacocloud.orders.placement.time").count();
        assertThat(count).isEqualTo(1L);
    }
}