package application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/projects/**").permitAll() // Разрешаем доступ к projects без аутентификации
                        .requestMatchers("/auth/**").permitAll()     // Разрешаем доступ к auth endpoints
                        .requestMatchers("/").permitAll()            // Разрешаем доступ к корневому эндпоинту
                        .anyRequest().permitAll()                // Все остальные требуют аутентификации
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/auth/success", true)
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}