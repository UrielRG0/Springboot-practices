package tacos.web.api;

import reactor.core.publisher.Mono;
import tacos.PaymentMethod;
import tacos.User;

public interface PaymentGateway {
    Mono<PaymentMethod> tokenize(String paymentToken, User user);
}