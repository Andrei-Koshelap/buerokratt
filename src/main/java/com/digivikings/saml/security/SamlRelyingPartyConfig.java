package com.digivikings.saml.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml2.provider.service.registration.*;

@Configuration
public class SamlRelyingPartyConfig {

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {

        String idpMetadata = "http://localhost:8081/realms/demo/protocol/saml/descriptor";

        RelyingPartyRegistration rp = RelyingPartyRegistrations
                .fromMetadataLocation(idpMetadata)
                .registrationId("kc")
                .entityId("urn:sp:saml-sp")
                .assertionConsumerServiceLocation("http://localhost:8080/login/saml2/sso/kc")
                .build();

        return new InMemoryRelyingPartyRegistrationRepository(rp);
    }
}
