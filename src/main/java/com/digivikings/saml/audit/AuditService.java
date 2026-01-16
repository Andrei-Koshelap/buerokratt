package com.digivikings.saml.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuditService {

    private final NamedParameterJdbcTemplate jdbc;

    public AuditService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void loginSuccess(HttpServletRequest req, String externalId, String email,
                             String relyingPartyRegistrationId,
                             Collection<? extends GrantedAuthority> roles) throws JsonProcessingException {

        String sql = """
      insert into auth_audit(external_id, email, event_type, idp_entity_id, session_id, ip, user_agent, correlation_id, details)
      values (:external_id, :email, 'LOGIN_SUCCESS', :idp_entity_id, :session_id, :ip, :user_agent, :correlation_id, cast(:details as jsonb))
    """;

        String corr = Optional.ofNullable(req.getHeader("X-Correlation-Id")).orElse(null);
        String detailsJson = "{\"roles\":" + new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(roles) + "}";

        Map<String, Object> params = new HashMap<>();
        params.put("external_id", externalId);
        params.put("email", email);
        params.put("event_type", "LOGIN_SUCCESS");
        params.put("idp_entity_id", relyingPartyRegistrationId);
        params.put("session_id", req.getSession(false) != null ? req.getSession(false).getId() : null);
        params.put("ip", req.getRemoteAddr());                   // inet —
        params.put("user_agent", req.getHeader("User-Agent"));
        params.put("correlation_id", corr);
        params.put("details", detailsJson);


        jdbc.update(sql, params);
    }
}
