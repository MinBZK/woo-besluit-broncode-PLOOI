package nl.overheid.koop.plooi.dcn.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class DCNSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationConfig authenticationConfig;
    private final TokenVerifierFilter tokenVerifierFilter;
    private final AuthorizationEntryPointJwt unauthorizedHandler;

    public DCNSecurityConfig(AuthenticationConfig authenticationConfig, TokenVerifierFilter tokenVerifierFilter,
            AuthorizationEntryPointJwt unauthorizedHandler) {
        this.authenticationConfig = authenticationConfig;
        this.tokenVerifierFilter = tokenVerifierFilter;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    /**
     * Allow every http request except requests to the admin api, they will be validated in TokenVerifierFilter.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling().authenticationEntryPoint(this.unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/admin").authenticated()
                .antMatchers("/**").permitAll();

        http.addFilterBefore(this.tokenVerifierFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(this.authenticationConfig.getUsername())
                .password(this.authenticationConfig.getPassword())
                .roles(this.authenticationConfig.getRole());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
