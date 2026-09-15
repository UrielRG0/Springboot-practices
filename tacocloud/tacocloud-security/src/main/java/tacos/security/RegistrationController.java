package tacos.security;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tacos.data.UserRepository;

@RestController // Lo cambiamos a RestController para devolver JSON
@RequestMapping(path = "/register", produces = "application/json")
public class RegistrationController {
  
  private UserRepository userRepo;
  private PasswordEncoder passwordEncoder;

  public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }
  
  // Eliminamos el GetMapping porque en las APIs Reactivas (Angular/React) el frontend maneja las vistas.

  @PostMapping(consumes = "application/json")
  public Mono<ResponseEntity<UserResponse>> processRegistration(@RequestBody RegistrationForm form) {
    
    // El form.toUser(passwordEncoder) ya hace la magia de encriptar tu contraseña
    
    // 1. RETORNAMOS la cadena reactiva (Solucionamos el "publisher descartado")
    return userRepo.save(form.toUser(passwordEncoder))
        .map(savedUser -> {
            // 2. Mapeamos al DTO seguro. Se genera el 201 SÓLO después de guardar.
            UserResponse response = new UserResponse(savedUser.getUsername(), savedUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        })
        .onErrorResume(DuplicateKeyException.class, e -> 
            // 3. Si MongoDB bota el error del @Indexed (Duplicado), devolvemos el 409
            Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()) 
        );
  }
}