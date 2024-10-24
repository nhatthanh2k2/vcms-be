package vcms.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_POST_ENDPOINTS = {
            "/auth/token", "/auth/introspect", "/auth/logout",
            "/auth/refresh", "/api/customers/lookup",
            "/api/appointments/create", "/api/appointments/create-code",
            "/api/orders/create", "/api/orders/create-code",
            "/api/employees/reset-password",
            "/api/vaccination-record/getMyHistory"
    };

    private final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/vaccines/all", "/api/vaccines/detail/*",
            "/api/diseases/all", "/api/diseases/detail/*",
            "/api/employees/all", "/api/employees/detail/*",
            "/api/employees/getDoctorAndNurse",
            "/api/vaccine-batch/detail/*",
            "/api/vaccine-batch/all",
            "/api/vaccine-package/detail/*",
            "/api/vaccine-package/list-default",
            "/images/vaccines/*", "/images/avatars/*"
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final CustomJwtDecoder customJwtDecoder;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder) {
        this.customJwtDecoder = customJwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
                request -> request
                        .requestMatchers(HttpMethod.POST,
                                         PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                         PUBLIC_GET_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
        );

        httpSecurity.oauth2ResourceServer(
                oauth2 -> oauth2.jwt(
                                jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
                                        .jwtAuthenticationConverter(
                                                jwtAuthenticationConverter()))
                        .authenticationEntryPoint(
                                new JwtAuthenticationEntryPoint())
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",
                                                                  corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }


}
