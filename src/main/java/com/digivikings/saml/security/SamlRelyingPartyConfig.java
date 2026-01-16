package com.digivikings.saml.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Configuration
public class SamlRelyingPartyConfig {

    @Bean
    RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        String entraMetadata =
                "https://login.microsoftonline.com/48891f41-d13b-454a-89f3-32b87dbc1068/federationmetadata/2007-06/federationmetadata.xml?appid=26593149-6fc1-433e-a63e-ee9b1476c1f7";

        try {
            RelyingPartyRegistration.Builder builder = RelyingPartyRegistrations
                    .fromMetadataLocation(entraMetadata)
                    .registrationId("entra")
                    .entityId("urn:sp:saml-sp");

            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (InputStream is = new ClassPathResource("sp-keystore.p12").getInputStream()) {
                ks.load(is, "changeit".toCharArray());
            }

            PrivateKey privateKey = (PrivateKey) ks.getKey("sp-signing", "changeit".toCharArray());
            if (privateKey == null) {
                throw new IllegalStateException("Private key 'sp-signing' not found in sp-keystore.p12");
            }

            X509Certificate cert = (X509Certificate) ks.getCertificate("sp-signing");
            if (cert == null) {
                throw new IllegalStateException("Certificate 'sp-signing' not found in sp-keystore.p12");
            }

            Saml2X509Credential signing = Saml2X509Credential.signing(privateKey, cert);

            RelyingPartyRegistration entra = builder
                    .signingX509Credentials(c -> c.add(signing))
                    .build();

            return new InMemoryRelyingPartyRegistrationRepository(entra);

        } catch (Exception e) {
            // KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
            // UnrecoverableKeyException, InvalidKeyException, etc.
            throw new IllegalStateException("Failed to configure SAML relying party registrations (Entra metadata/keystore)", e);
        }
    }

}
