package nl.overheid.koop.plooi.plooisecurity.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@ConditionalOnProperty(
        name = "plooi.security.enabled",
        havingValue = "false")
@Configuration
public class LocalSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
