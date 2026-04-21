package vuinfinitecart.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vuinfinitecart.web.auth.customer.OAuth2LoginSuccessHandler;
import vuinfinitecart.web.common.security.AdminJwtFilter;
import vuinfinitecart.web.common.security.CustomerJwtFilter;
import vuinfinitecart.web.common.security.JwtUtil;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final AppProperties appProperties;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final vuinfinitecart.web.customer.repository.CustomerRepository customerRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public: i18n translations
                .requestMatchers("/api/i18n/**").permitAll()
                // Public: customer auth (local login, register, OAuth2, refresh)
                .requestMatchers(
                        "/api/auth/customer/login",
                        "/api/auth/customer/register",
                        "/api/auth/customer/refresh",
                        "/api/auth/customer/oauth2/**"
                ).permitAll()
                // Public: admin auth (login + refresh)
                .requestMatchers("/api/auth/admin/login", "/api/auth/admin/refresh").permitAll()
                // Public: product browsing
                .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()
                // Protected: customer operations
                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                // Protected: admin operations
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            // Add JWT filters: admin filter runs first (path-specific), then customer
            .addFilterBefore(adminJwtFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(customerJwtFilter(), UsernamePasswordAuthenticationFilter.class)
            // OAuth2 Login config
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(auth -> auth.baseUri("/api/auth/customer/oauth2/authorize"))
                .redirectionEndpoint(redir -> redir.baseUri("/api/auth/customer/oauth2/callback/*"))
                .successHandler(oAuth2LoginSuccessHandler)
            )
            // Return 401 instead of redirecting to /login for unauthorized API calls
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(
                        new org.springframework.security.web.authentication.HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED),
                        new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/api/**")
                )
            );

        return http.build();
    }

    @Bean
    public CustomerJwtFilter customerJwtFilter() {
        return new CustomerJwtFilter(jwtUtil, customerRepository);
    }

    @Bean
    public AdminJwtFilter adminJwtFilter() {
        return new AdminJwtFilter(jwtUtil);
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(appProperties.getCors().getAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
