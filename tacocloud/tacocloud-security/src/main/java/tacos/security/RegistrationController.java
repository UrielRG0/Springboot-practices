package tacos.security;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tacos.data.UserRepository;

@RestController
@RequestMapping(path = "/register", produces = "application/json")
public class RegistrationController {
  
  private UserRepository userRepo;
  private PasswordEncoder passwordEncoder;

  public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }


  @PostMapping(consumes = "application/json")
  public Mono<ResponseEntity<UserResponse>> processRegistration(@RequestBody RegistrationForm form) {
    return userRepo.save(form.toUser(passwordEncoder)).map(savedUser -> {
            UserResponse response = new UserResponse(savedUser.getUsername(), savedUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }).onErrorResume(DuplicateKeyException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()) );
  }
}