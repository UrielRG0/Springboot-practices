package tacos.messaging.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingValidationConfig implements InitializingBean {

    @Value("${tacocloud.messaging.transport:noop}")
    private String transport;

    @Override
    public void afterPropertiesSet() {
        if (!transport.matches("noop|jms|rabbit|kafka")) {
            throw new IllegalStateException("Transporte inválido detectado: '" + transport + 
                                            "'. Valores permitidos: noop, jms, rabbit, kafka");
        }
    }
}