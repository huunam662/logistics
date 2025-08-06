package warehouse_management.com.warehouse_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF để dễ test Postman
                .cors(cors -> cors.configurationSource(config -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);

                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", corsConfiguration);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Cho phép toàn bộ request
                );
        return http.build();
    }
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        System.out.println("__________________>>>>>");
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/login").permitAll()
//                        .requestMatchers("/dev/**").permitAll()
//                        .anyRequest().permitAll()
//                )
////                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
//                .sessionManagement(sessionManagementCustomizer ->
//                        sessionManagementCustomizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        return http.build();
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

//    @Bean
//    public JwtAuthFilter jwtAuthFilter() {
//        return new JwtAuthFilter();
//    }
}