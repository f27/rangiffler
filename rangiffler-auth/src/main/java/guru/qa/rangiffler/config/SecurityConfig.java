package guru.qa.rangiffler.config;

import guru.qa.rangiffler.config.cors.CorsCustomizer;
import guru.qa.rangiffler.config.filter.CookieCsrfFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CorsCustomizer corsCustomizer;

    @Autowired
    public SecurityConfig(CorsCustomizer corsCustomizer) {
        this.corsCustomizer = corsCustomizer;
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        corsCustomizer.corsCustomizer(http);

        return http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers(
                                antMatcher("/register"),
                                antMatcher("/images/**"),
                                antMatcher("/styles/**"),
                                antMatcher("/fonts/**"),
                                antMatcher("/actuator/health")
                        ).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // https://stackoverflow.com/a/74521360/65681
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .addFilterAfter(new CookieCsrfFilter(), BasicAuthenticationFilter.class)
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(antMatcher("/logout")) // https://github.com/spring-projects/spring-authorization-server/issues/266
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                )
                .sessionManagement(sm -> sm.invalidSessionUrl("/login"))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
