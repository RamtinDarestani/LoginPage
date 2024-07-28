package com.example.loginpage.Config;

import com.example.loginpage.Controller.OAuth2Controller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final OAuth2Controller oAuth2Controller;

    public SecurityConfig(OAuth2Controller oAuth2Controller) {
        this.oAuth2Controller = oAuth2Controller;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain");
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/sign", "/login/oauth2/code/google", "/oauth2/**", "/api/users", "/api/test-db", "/user-profile", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/sign")
                        .successHandler(oauth2AuthenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/sign")
                        .permitAll());

        logger.info("SecurityFilterChain configuration completed");
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                logger.info("OAuth2 login successful, calling handleOAuth2LoginSuccess");
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                oAuth2Controller.handleOAuth2LoginSuccess(oauth2User);
                response.sendRedirect("/user-profile");
            }
        };
    }
}