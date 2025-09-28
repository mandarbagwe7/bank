create table roles
(
    id         bigint auto_increment
        primary key,
    name       varchar(32)                  not null,
    created_at datetime default (curdate()) not null,
    updated_at datetime default (curdate()) not null,
    constraint roles_unique_name
        unique (name)
);

create table users
(
    id            bigint auto_increment
        primary key,
    email         varchar(255)                   not null,
    password      varchar(255)                   not null,
    is_active     tinyint(1) default (1)         not null,
    last_login_at datetime   default (null)      null,
    created_at    datetime   default (curdate()) not null,
    updated_at    datetime   default (curdate()) not null,
    constraint users_unique_email
        unique (email)
);

create table user_roles
(
    user_id     bigint                       not null,
    role_id     bigint                       not null,
    assigned_at datetime default (curdate()) not null,
    constraint user_roles_pk
        primary key (user_id, role_id),
    constraint user_roles_roles_id_fk
        foreign key (role_id) references roles (id)
            on delete cascade,
    constraint user_roles_users_id_fk
        foreign key (user_id) references users (id)
            on delete cascade
);

create table customers
(
    id            bigint                           not null
        primary key,
    user_id       bigint                           not null,
    kyc_status    varchar(16)  default ('PENDING') not null,
    full_name     varchar(128)                     not null,
    dob           date                             not null,
    phone         varchar(32)                      null,
    email_copy    varchar(254)                     not null,
    address_line1 varchar(128)                     not null,
    address_line2 varchar(128) default (null)      null,
    city          varchar(64)                      not null,
    state         varchar(64)  default (null)      null,
    postal_code   varchar(16)                      not null,
    country       varchar(64)                      null,
    created_at    datetime     default (curdate()) not null,
    updated_at    datetime     default (curdate()) not null,
    constraint customers_unique_user_id
        unique (user_id),
    constraint customers_users_id_fk
        foreign key (user_id) references users (id)
);

create table accounts
(
    id             bigint                             not null
        primary key,
    customer_id    bigint                             not null,
    account_number varchar(20)                        not null,
    type           varchar(16)                        not null,
    currency       char(3)        default ('EUR')     not null,
    status         varchar(16)    default ('ACTIVE')  not null,
    balance        decimal(19, 4) default (0.0000)    not null,
    version        int            default (0)         not null,
    opened_at      datetime       default (curdate()) not null,
    closed_at      datetime       default (null)      null,
    created_at     datetime       default (curdate()) not null,
    updated_at     datetime       default (curdate()) not null,
    constraint accounts_unique_account_number
        unique (account_number),
    constraint accounts_customers_id_fk
        foreign key (customer_id) references customers (id)
            on delete cascade
);

create table transfers
(
    id                   bigint                            not null
        primary key,
    source_account_id    bigint                            not null,
    target_account_id    bigint                            not null,
    amount               decimal(19, 4)                    not null,
    currency             char(3)      default ('EUR')      not null,
    status               varchar(16)  default ('ACCEPTED') not null,
    idempotency_key      varchar(128)                      not null,
    requested_by_user_id bigint                            not null,
    failure_reason       varchar(255) default (null)       null,
    correlation_id       varchar(64)  default (null)       null,
    created_at           datetime     default (curdate())  not null,
    completed_at         datetime     default (null)       null,
    constraint transfers_pk_source_id_key
        unique (source_account_id, idempotency_key),
    constraint transfers_accounts_source_id_fk
        foreign key (source_account_id) references accounts (id),
    constraint transfers_accounts_target_id_fk
        foreign key (target_account_id) references accounts (id),
    constraint transfers_users_id_fk
        foreign key (requested_by_user_id) references users (id)
);

create table transactions
(
    id             bigint                           not null
        primary key,
    account_id     bigint                           not null,
    direction      varchar(8)                       not null,
    amount         decimal(19, 4)                   not null,
    currency       char(3)      default ('EUR')     not null,
    narrative      varchar(255) default (null)      null,
    correlation_id varchar(64)  default (null)      null,
    balance_after  decimal(19, 4)                   not null,
    created_at     datetime     default (curdate()) null,
    constraint transactions_accounts_id_fk
        foreign key (account_id) references accounts (id)
);

create table audit_logs
(
    id            bigint                           not null
        primary key,
    actor_user_id bigint       default (null)      null,
    action        varchar(64)                      not null,
    entity_type   varchar(64)                      not null,
    entity_id     varchar(64)                      not null,
    details       JSON         default (null)      null,
    source_ip     varchar(45)  default (null)      null,
    user_agent    varchar(255) default (null)      null,
    created_at    datetime     default (curdate()) not null,
    constraint audit_logs_users_id_fk
        foreign key (actor_user_id) references users (id)
);

