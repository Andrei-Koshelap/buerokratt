package com.digivikings.saml.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            RelyingPartyRegistrationRepository repo,
            SamlLoginSuccessHandler successHandler
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/error",
                                "/saml2/**",          // <- metadata + authenticate should be public
                                "/login/saml2/**"     // <- ACS endpoint also available (POST from IdP)
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .saml2Login(saml -> saml
                        .relyingPartyRegistrationRepository(repo)
                        .successHandler(successHandler)
                )
                .saml2Logout(saml -> saml
                        .relyingPartyRegistrationRepository(repo)
                );

        return http.build();
    }

}
