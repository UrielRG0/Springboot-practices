package tacos;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor; // <-- El import mágico
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
// Quitamos el @RequiredArgsConstructor porque lo vamos a hacer manual
@Document
public class User implements UserDetails {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;
  
  @Indexed(unique = true)
  private final String username;
  
  private final String password;
  private final String fullname;
  private final String street;
  private final String city;
  private final String state;
  private final String zip;
  private final String phoneNumber;

  @Indexed(unique = true)
  private final String email;

  private final String role;
  
  // ¡EL TRUCO PARA MONGO! Le decimos explícitamente cómo inyectar los campos 'final'
  @PersistenceConstructor
  public User(String username, String password, String fullname, String street, 
              String city, String state, String zip, String phoneNumber, 
              String email, String role) {
    this.username = username;
    this.password = password;
    this.fullname = fullname;
    this.street = street;
    this.city = city;
    this.state = state;
    this.zip = zip;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.role = role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Arrays.asList(new SimpleGrantedAuthority(this.role != null ? this.role : "ROLE_USER"));
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}