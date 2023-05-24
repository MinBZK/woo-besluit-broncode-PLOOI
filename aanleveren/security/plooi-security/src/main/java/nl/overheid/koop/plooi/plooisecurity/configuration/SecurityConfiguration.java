package nl.overheid.koop.plooi.plooisecurity.configuration;

import nl.overheid.koop.plooi.plooisecurity.domain.authorization.SecurityContextInitializer;
import nl.overheid.koop.plooi.plooisecurity.domain.authorization.TokenAuthorizationService;
import nl.overheid.koop.plooi.plooisecurity.web.filter.AuthorizationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.reactive.function.client.WebClient;

@ConditionalOnProperty(
        name = "plooi.security.enabled",
        matchIfMissing = true)
@Configuration
public class SecurityConfiguration {
    private AuthorizationFilter authorizationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(authorizationFilter, BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthorizationFilter authorizationFilter(final WebClient webClientIamService) {
        this.authorizationFilter =  new AuthorizationFilter(new TokenAuthorizationService(webClientIamService), new SecurityContextInitializer());
        return this.authorizationFilter;
    }
}
