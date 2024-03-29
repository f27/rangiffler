package guru.qa.rangiffler.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import guru.qa.rangiffler.config.cors.CorsCustomizer;
import guru.qa.rangiffler.config.key.KeyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Configuration
public class AuthServerConfig {
    private final KeyManager keyManager;
    private final CorsCustomizer corsCustomizer;
    private final String rangifflerFrontUri;
    private final String rangifflerAuthUri;
    private final String clientId;
    private final String clientSecret;
    private final String serverPort;
    private final String defaultHttpsPort = "443";

    public AuthServerConfig(KeyManager keyManager,
                            CorsCustomizer corsCustomizer,
                            @Value("${rangiffler-front.base-uri}") String rangifflerFrontUri,
                            @Value("${rangiffler-auth.base-uri}") String rangifflerAuthUri,
                            @Value("${oauth2.client-id}") String clientId,
                            @Value("${oauth2.client-secret}") String clientSecret,
                            @Value("${server.port}") String serverPort) {
        this.keyManager = keyManager;
        this.corsCustomizer = corsCustomizer;
        this.rangifflerFrontUri = rangifflerFrontUri;
        this.rangifflerAuthUri = rangifflerAuthUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.serverPort = serverPort;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      LoginUrlAuthenticationEntryPoint entryPoint) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());    // Enable OpenID Connect 1.0

        http.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(entryPoint))
                .oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()));

        corsCustomizer.corsCustomizer(http);
        return http.build();
    }

    @Bean
    @Profile({"staging", "prod"})
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPointHttps() {
        LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/login");
        PortMapperImpl portMapper = new PortMapperImpl();
        portMapper.setPortMappings(Map.of(
                serverPort, defaultHttpsPort,
                "80", defaultHttpsPort,
                "8080", "8443"
        ));
        PortResolverImpl portResolver = new PortResolverImpl();
        portResolver.setPortMapper(portMapper);
        entryPoint.setForceHttps(true);
        entryPoint.setPortMapper(portMapper);
        entryPoint.setPortResolver(portResolver);
        return entryPoint;
    }

    @Bean
    @Profile({"local", "docker"})
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPointHttp() {
        return new LoginUrlAuthenticationEntryPoint("/login");
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(rangifflerFrontUri + "/authorized")
                .scope(OidcScopes.OPENID)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true).build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .refreshTokenTimeToLive(Duration.ofHours(10))
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(rangifflerAuthUri)
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        JWKSet set = new JWKSet(keyManager.rsaKey());
        return (jwkSelector, securityContext) -> jwkSelector.select(set);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }
}
