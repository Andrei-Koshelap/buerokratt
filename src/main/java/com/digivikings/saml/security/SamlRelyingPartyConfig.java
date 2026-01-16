package com.digivikings.saml.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.*;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Configuration
public class SamlRelyingPartyConfig {

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() throws Exception {

        // --- load SP signing keypair from PKCS12 ---
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (var is = new ClassPathResource("sp-keystore.p12").getInputStream()) {
            ks.load(is, "changeit".toCharArray());
        }

        PrivateKey privateKey = (PrivateKey) ks.getKey("sp-signing", "changeit".toCharArray());
        X509Certificate certificate = (X509Certificate) ks.getCertificate("sp-signing");

        Saml2X509Credential spSigning = Saml2X509Credential.signing(privateKey, certificate);

        String idpMetadata = "http://localhost:8081/realms/demo/protocol/saml/descriptor";

        RelyingPartyRegistration rp = RelyingPartyRegistrations
                .fromMetadataLocation(idpMetadata)
                .registrationId("kc")
                .entityId("urn:sp:saml-sp")
                .assertionConsumerServiceLocation("http://localhost:8080/login/saml2/sso/kc")
                .signingX509Credentials(c -> c.add(spSigning))
                .build();

        return new InMemoryRelyingPartyRegistrationRepository(rp);
    }
}
