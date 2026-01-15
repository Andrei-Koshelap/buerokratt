package com.digivikings.saml.rbac;

import java.util.List;
import java.util.Set;

public interface RbacService {
    Set<String> resolveRoles(List<String> groupDns, String externalId);
}
