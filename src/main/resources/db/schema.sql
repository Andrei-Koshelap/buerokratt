
-- 1) Users (local cache of profile)
create table if not exists app_user (
                                        id            bigserial primary key,
                                        external_id   text not null unique,
                                        email         text,
                                        first_name    text,
                                        last_name     text,
                                        last_login_at timestamptz null,
                                        created_at timestamptz not null default now(),
                                        updated_at timestamptz not null default now()
    );

-- 2) Roles
create table if not exists app_role (
                                        code        text primary key,
                                        description text
);

-- 3) AD group -> role mapping
create table if not exists ad_group_role_mapping (
                                                     id          bigserial primary key,
                                                     group_dn    text not null,
                                                     role_code   text not null references app_role(code),
    enabled     boolean not null default true,
    created_at  timestamptz not null default now(),
    unique (group_dn, role_code)
    );

create index if not exists idx_ad_group_role_mapping_group_dn
    on ad_group_role_mapping (group_dn);

-- 4)  Manual overrides
create table if not exists  user_role_override (
                                    user_id           bigint not null references app_user(id) on delete cascade,
                                    role_code         text not null references app_role(code),
                                    mode              text not null check (mode in ('GRANT','REVOKE')),
                                    created_at        timestamptz not null default now(),
                                    primary key (user_id, role_code, mode)
);

-- 5) Auth audit trail
create table if not exists auth_audit (
    id             bigserial primary key,
    ts             timestamptz not null default now(),
    external_id    text,
    email          text,
    event_type     text not null,
    idp_entity_id  text,
    session_id     text,
    ip             inet,
    user_agent     text,
    correlation_id text,
    details        jsonb
    );
