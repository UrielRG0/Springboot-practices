package tacos.web.api;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tacos.PaymentMethod;
import tacos.User;

@Service
public class FakePaymentGateway implements PaymentGateway {

    @Override
    public Mono<PaymentMethod> tokenize(String paymentToken, User user) {
        PaymentMethod safeMethod = new PaymentMethod(null, user, paymentToken, "VISA", "4242");
        
        return Mono.just(safeMethod);
    }
}