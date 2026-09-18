package tacos.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

// IMPORTANTE: Verifica si EmailOrder está en tacos o en tacos.email
import tacos.web.api.EmailOrder; // (O solo tacos.EmailOrder si está en la raíz)
import tacos.Ingredient;
import tacos.PaymentMethod;
import tacos.TacoOrder;
import tacos.User;
import tacos.data.IngredientRepository;
import tacos.data.PaymentMethodRepository;
import tacos.data.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailOrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private EmailOrderService emailOrderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConvertEmailOrder_HappyPath() {
        EmailOrder emailOrder = mock(EmailOrder.class);
        when(emailOrder.getEmail()).thenReturn("habuma@tacocloud.com");
        when(emailOrder.getTacos()).thenReturn(java.util.Collections.emptyList());

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn("user-123"); 

        PaymentMethod mockPayment = mock(PaymentMethod.class);

        Ingredient mockIngredient1 = new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP);
        Ingredient mockIngredient2 = new Ingredient("CARN", "Carnitas", Ingredient.Type.PROTEIN);

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(mockUser));
        when(paymentMethodRepository.findByUserId(anyString())).thenReturn(Mono.just(mockPayment));
        when(ingredientRepository.findById("FLTO")).thenReturn(Mono.just(mockIngredient1));
        when(ingredientRepository.findById("CARN")).thenReturn(Mono.just(mockIngredient2));

        Mono<TacoOrder> resultMono = emailOrderService.convertEmailOrderToDomainOrder(Mono.just(emailOrder));

        StepVerifier.create(resultMono)
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    public void testConvertEmailOrder_MissingUser() {
        EmailOrder emailOrder = mock(EmailOrder.class);
        when(emailOrder.getEmail()).thenReturn("noexiste@tacocloud.com");

        // Simulamos que el usuario no existe en la base de datos
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        Mono<TacoOrder> resultMono = emailOrderService.convertEmailOrderToDomainOrder(Mono.just(emailOrder));

        // Verificamos que emita un error controlado
        StepVerifier.create(resultMono)
            .expectError()
            .verify();
    }

    @Test
    public void testConvertEmailOrder_UnknownIngredient() {
        EmailOrder emailOrder = mock(EmailOrder.class);
        when(emailOrder.getEmail()).thenReturn("habuma@tacocloud.com");

        User mockUser = mock(User.class);

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(mockUser));
        // Simulamos que un ingrediente requerido no existe en inventario
        when(ingredientRepository.findById("UNKNOWN_ING")).thenReturn(Mono.empty());

        Mono<TacoOrder> resultMono = emailOrderService.convertEmailOrderToDomainOrder(Mono.just(emailOrder));

        // Verificamos que el flujo falle por el ingrediente desconocido
        StepVerifier.create(resultMono)
            .expectError()
            .verify();
    }
}