package com.digivikings.saml.security;

import java.util.*;
import java.util.stream.Collectors;

import com.digivikings.saml.rbac.RbacService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

@Component
public class SamlAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private final RbacService rbacService;

    public SamlAuthoritiesMapper(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(
            Collection<? extends GrantedAuthority> authorities) {

        // Authorities usually contain only basic roles, while the principal is stored in Authentication.
        // Therefore, it is more correct to handle this via an AuthenticationSuccessHandler.
        // However, Spring invokes the mapper on the principal if it is available in the context.
        return authorities;
    }

    public Collection<GrantedAuthority> mapFromPrincipal(Saml2AuthenticatedPrincipal principal) {
        String externalId = principal.getName(); // часто NameID

        List<String> groups = principal.getAttribute("groups");
        if (groups == null) groups = List.of();

        Set<String> roleCodes = rbacService.resolveRoles(groups, externalId);

        return roleCodes.stream()
                .map(code -> "ROLE_" + code)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
