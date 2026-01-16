package com.digivikings.saml.user;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PostgresUserService implements UserService {

    private final NamedParameterJdbcTemplate jdbc;

    public PostgresUserService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void upsertUser(
            String externalId,
            String email,
            String firstName,
            String lastName,
            Instant loginTime
    ) {

        String sql = """
      insert into app_user (
        external_id,
        email,
        first_name,
        last_name,
        last_login_at,
        created_at,
        updated_at
      )
      values (
        :external_id,
        :email,
        :first_name,
        :last_name,
        :last_login_at,
        now(),
        now()
      )
      on conflict (external_id)
      do update set
        email = excluded.email,
        first_name = excluded.first_name,
        last_name = excluded.last_name,
        last_login_at = excluded.last_login_at,
        updated_at = now()
    """;

        var params = new java.util.HashMap<String, Object>();
        params.put("external_id", externalId);
        params.put("email", email);
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("last_login_at", loginTime == null ? null : java.sql.Timestamp.from(loginTime));
        jdbc.update(sql, params);


    }
}
