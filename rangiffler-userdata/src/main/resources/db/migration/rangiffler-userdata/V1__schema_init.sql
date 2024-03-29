create table if not exists `user`
(
    id           binary(16) unique  not null default (UUID_TO_BIN(UUID(), true)),
    username     varchar(50) unique not null,
    firstname    varchar(50),
    lastname     varchar(50),
    avatar       longblob,
    country_code varchar(50)        not null,
    primary key (id)
);

create table if not exists `friendship`
(
    requester_id binary(16)  not null,
    addressee_id binary(16)  not null,
    created_date datetime    not null default CURRENT_TIMESTAMP,
    status       varchar(50) not null,
    primary key (requester_id, addressee_id),
    constraint friend_are_distinct_ck check (requester_id <> addressee_id),
    constraint fk_requester_id foreign key (requester_id) references `user` (id),
    constraint fk_addressee_id foreign key (addressee_id) references `user` (id)
);