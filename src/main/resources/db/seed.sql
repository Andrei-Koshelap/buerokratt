insert into app_role(code, description) values
                                            ('ADMIN', 'Full access'),
                                            ('OPERATOR', 'Operational access'),
                                            ('VIEWER', 'Read-only access')
on conflict do nothing;


insert into ad_group_role_mapping(group_dn, role_code, enabled) values
                                                                    ('CN=Burokratt-Admins,OU=Groups,DC=company,DC=ee', 'ADMIN', true),
                                                                    ('CN=Burokratt-Operators,OU=Groups,DC=company,DC=ee', 'OPERATOR', true),
                                                                    ('CN=Burokratt-Viewers,OU=Groups,DC=company,DC=ee', 'VIEWER', true)
on conflict do nothing;
