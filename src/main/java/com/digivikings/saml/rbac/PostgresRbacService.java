package com.digivikings.saml.rbac;


import java.util.*;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostgresRbacService implements RbacService {

    private final NamedParameterJdbcTemplate jdbc;

    public PostgresRbacService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Set<String> resolveRoles(List<String> groupDns, String externalId) {
        if (groupDns == null || groupDns.isEmpty()) {
            return Set.of("VIEWER"); // дефолт, или пусто
        }

        String sql = """
      select distinct role_code
      from ad_group_role_mapping
      where enabled = true
        and group_dn in (:groups)
    """;

        Map<String, Object> params = Map.of("groups", groupDns);

        List<String> roles = jdbc.query(sql, params,
                (rs, rowNum) -> rs.getString("role_code"));

        // Здесь же можно применить user_role_override (GRANT/REVOKE), если нужно.
        return new LinkedHashSet<>(roles);
    }
}
