package application.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityConfig {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth", "/auth/**",
                                "/login/**",
                                "/oauth2/**",
                                "/error", "/"
                        ).permitAll()

                        // Ваши REST API требуют JWT
                        .requestMatchers("/projects", "/projects/**").authenticated()

                        // Всё остальное — на ваше усмотрение
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth")  // опционально, если хотите кастомную страницу
                        .defaultSuccessUrl("/auth/success", true)
                )

                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(myConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter myConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("sub");
        return converter;
    }
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = jwtSecret.getBytes();
        SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
