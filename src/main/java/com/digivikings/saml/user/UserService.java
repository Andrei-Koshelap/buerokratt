package com.digivikings.saml.user;

import java.time.Instant;

public interface UserService {

    void upsertUser(
            String externalId,
            String email,
            String firstName,
            String lastName,
            Instant loginTime
    );
}
