package tacos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService userDetailsService;
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().ignoringAntMatchers("/api/**", "/register")
      .and().cors() 
      .and().authorizeRequests()
        .antMatchers(HttpMethod.GET, "/api/ingredients").permitAll()
        .antMatchers("/register", "/login").permitAll()
        .antMatchers("/actuator/health").permitAll()
        .antMatchers(HttpMethod.POST, "/api/ingredients").hasRole("ADMIN")
        .antMatchers(HttpMethod.PATCH, "/api/ingredients").hasRole("ADMIN")
        .antMatchers(HttpMethod.DELETE, "/api/ingredients").hasRole("ADMIN")
        .antMatchers("/actuator/**").hasRole("ADMIN")
        .antMatchers("/api/kitchen/**").hasRole("KITCHEN")
        .antMatchers("/api/orders", "/api/orders/**").hasAnyRole("USER", "ADMIN")
        .antMatchers("/api/admin/ingredients/**").hasRole("ADMIN") // <-- En su posición correcta
        .antMatchers("/data-api/**").denyAll()
        .anyRequest().authenticated() // <-- El comodín cerrando la cadena
      .and()
        .httpBasic(); 
  }

  @Bean
  public PasswordEncoder encoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
  
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
        .passwordEncoder(encoder());
  }
}