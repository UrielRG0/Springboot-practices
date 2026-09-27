package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tacos.User;
import tacos.data.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import tacos.security.RegistrationController;
import tacos.security.RegistrationForm;
import tacos.security.UserResponse;

public class RegistrationTest {

    // Test encoder and adaptative hash
    @Test
    public void testPasswordEncoder_HashesCorrectly() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String plainPassword = "supersecreto";
        
        String hashedPassword = encoder.encode(plainPassword);
        
        assertNotEquals(plainPassword, hashedPassword, "The password must not be equals with the input text"); 
        assertTrue(hashedPassword.startsWith("{"), "The hash must have the identificator"); 
        assertTrue(encoder.matches(plainPassword, hashedPassword), "The match must return true with the correct password");
    }

    // PPersistent test
    @Test
    public void testRegistration_SavesReactively() {
        UserRepository repoMock = mock(UserRepository.class);
        PasswordEncoder encoderMock = mock(PasswordEncoder.class);
        
        RegistrationController controller = new RegistrationController(repoMock, encoderMock);

        RegistrationForm form = new RegistrationForm();
        form.setUsername("craig");
        form.setPassword("password");

        User savedUser = new User("craig", "{bcrypt}hashed123", "Craig Walls", 
                                  "123 St", "Dallas", "TX", "12345", 
                                  "555-1234", "craig@taco.com", "ROLE_USER");

        when(encoderMock.encode(anyString())).thenReturn("{bcrypt}hashed123");
        when(repoMock.save(any(User.class))).thenReturn(Mono.just(savedUser));

        Mono<ResponseEntity<UserResponse>> result = controller.processRegistration(form);

        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("craig", response.getBody().getUsername());
            })
            .verifyComplete();
            
        verify(repoMock).save(any(User.class));
    }

    //DuplicateKeyException 409
    @Test
    public void testRegistration_DuplicateKeyException_Returns409() {
        UserRepository repoMock = mock(UserRepository.class);
        PasswordEncoder encoderMock = mock(PasswordEncoder.class);
        RegistrationController controller = new RegistrationController(repoMock, encoderMock);

        RegistrationForm form = new RegistrationForm();
        form.setUsername("hacker");
        form.setPassword("password");

        when(encoderMock.encode(anyString())).thenReturn("{bcrypt}hashed123");
        
        when(repoMock.save(any(User.class))).thenReturn(Mono.error(new DuplicateKeyException("index violated")));

        Mono<ResponseEntity<UserResponse>> result = controller.processRegistration(form);
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            })
            .verifyComplete();
    }
}