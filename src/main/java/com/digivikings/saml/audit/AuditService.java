package com.digivikings.saml.audit;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final NamedParameterJdbcTemplate jdbc;

    public AuditService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void loginSuccess(HttpServletRequest req, String externalId, String email,
                             String relyingPartyRegistrationId,
                             Collection<? extends GrantedAuthority> roles) {

        String sql = """
      insert into auth_audit(external_id, email, event_type, idp_entity_id, session_id, ip, user_agent, correlation_id, details)
      values (:external_id, :email, 'LOGIN_SUCCESS', :idp, :session_id, :ip, :ua, :corr, cast(:details as jsonb))
    """;

        String corr = Optional.ofNullable(req.getHeader("X-Correlation-Id")).orElse(null);

        Map<String, Object> params = Map.of(
                "external_id", externalId,
                "email", email,
                "idp", relyingPartyRegistrationId,
                "session_id", req.getSession(false) != null ? req.getSession(false).getId() : null,
                "ip", req.getRemoteAddr(),
                "ua", req.getHeader("User-Agent"),
                "corr", corr,
                "details", "{\"roles\":\"" + roles.toString().replace("\"","\\\"") + "\"}"
        );

        jdbc.update(sql, params);
    }
}
