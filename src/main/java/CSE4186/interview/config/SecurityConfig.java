package CSE4186.interview.config;

import CSE4186.interview.jwt.JwtFilter;
import CSE4186.interview.jwt.TokenProvider;
import CSE4186.interview.login.CustomAuthenticationManager;
import CSE4186.interview.login.FilterExceptionHandler;
import CSE4186.interview.login.LoginFilter;
import CSE4186.interview.login.handler.CustomAccessDeniedHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CustomAuthenticationManager customAuthenticationManager;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web)->web.ignoring().requestMatchers(
                "/css/**","/js/**","/images/**","/favicon.ico","/error");
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/login/oauth2/**").permitAll()
                                .requestMatchers("/oauth2/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/join/**").permitAll()
                                .requestMatchers("/token/**").permitAll()
                                .requestMatchers("/question/**").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                .requestMatchers("/health-check").permitAll()
                                .anyRequest().hasAnyAuthority("USER")
                )

                .oauth2Login(oauth2 ->
                        oauth2
                                .authorizationEndpoint(a->{
                                    a.baseUri("/login/oauth2");
                                })
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(new FilterExceptionHandler(objectMapper),UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new LoginFilter(tokenProvider,customAuthenticationManager,objectMapper), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}