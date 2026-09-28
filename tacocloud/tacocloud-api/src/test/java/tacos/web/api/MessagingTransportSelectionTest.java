package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tacos.messaging.NoOpOrderMessagingService;
import tacos.messaging.contract.OrderMessagingService;

import static org.assertj.core.api.Assertions.assertThat;
import tacos.messaging.config.MessagingValidationConfig;

public class MessagingTransportSelectionTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(MessagingValidationConfig.class, NoOpOrderMessagingService.class);

    @Test
    public void testDefaultLoadsNoOpBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(OrderMessagingService.class);
            assertThat(context).getBean(OrderMessagingService.class).isInstanceOf(NoOpOrderMessagingService.class);
        });
    }

    @Test
    public void testExplicitNoOpLoadsProperly() {
        contextRunner.withPropertyValues("tacocloud.messaging.transport=noop")
            .run(context -> {
                assertThat(context).hasSingleBean(OrderMessagingService.class);
            });
    }

    @Test
    public void testInvalidTransportFailsFast() {
        contextRunner.withPropertyValues("tacocloud.messaging.transport=paloma_mensajera")
            .run(context -> {
                assertThat(context).hasFailed();
                assertThat(context.getStartupFailure().getMessage())
                    .contains("Transporte inválido");
            });
    }
}