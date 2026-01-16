package com.digivikings.saml.security;

import com.digivikings.saml.audit.AuditService;
import com.digivikings.saml.rbac.RbacService;
import com.digivikings.saml.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SamlLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final RbacService rbacService;
    private final UserService userService;
    private final AuditService auditService;

    public SamlLoginSuccessHandler(RbacService rbacService, UserService userService, AuditService auditService) {
        this.rbacService = rbacService;
        this.userService = userService;
        this.auditService = auditService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principalObj = authentication.getPrincipal();
        if (!(principalObj instanceof Saml2AuthenticatedPrincipal principal)) {
            // on any case
            response.sendRedirect("/private");
            return;
        }

        String externalId = principal.getName();
        String email = principal.getFirstAttribute("email");
        String firstName = Optional.ofNullable(principal.getFirstAttribute("givenName")).orElse("").toString();
        String lastName  = Optional.ofNullable(principal.getFirstAttribute("sn")).orElse("").toString();


        System.out.println("SAML name=" + principal.getName());
        System.out.println("email=" + principal.getFirstAttribute("email"));
        System.out.println("givenName=" + principal.getFirstAttribute("givenName"));
        System.out.println("sn=" + principal.getFirstAttribute("sn"));
        System.out.println("groups=" + principal.getAttribute("groups"));

        userService.upsertUser(externalId, email, firstName, lastName, Instant.now());

        List<String> groups = principal.getAttribute("groups");
        if (groups == null) groups = List.of();

        Set<String> roleCodes = rbacService.resolveRoles(groups, externalId);

        Collection<GrantedAuthority> mappedAuthorities = roleCodes.stream()
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                .collect(Collectors.toSet());

        String registrationId = extractRegistrationId(request);

        auditService.loginSuccess(request, externalId, email, registrationId, mappedAuthorities);

        response.sendRedirect("/private");
    }

    private static String extractRegistrationId(HttpServletRequest request) {
        // /login/saml2/sso/kc -> kc
        String uri = request.getRequestURI();
        int lastSlash = uri.lastIndexOf('/');
        return (lastSlash >= 0 && lastSlash < uri.length() - 1) ? uri.substring(lastSlash + 1) : null;
    }
}
