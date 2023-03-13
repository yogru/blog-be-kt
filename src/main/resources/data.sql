create table file_entity
(
    id           binary(16) not null primary key,
    created_at   datetime(6) not null,
    updated_at   datetime(6) not null,
    content_type varchar(255) not null,
    dir          varchar(255) not null,
    ext          varchar(255) not null,
    origin_name  varchar(255) not null,
    size         bigint       not null,
    status       varchar(255) not null
);

create table post_user_value
(
    id         binary(16) not null primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    account    varchar(255) not null,
    nick_name  varchar(255) not null
);

create table post
(
    id         binary(16) not null primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    body       tinytext null,
    deleted    bit null,
    title      varchar(255) not null,
    user_id    binary(16) not null,
    constraint FKcldb6ailu63x84lq3ir6a1hpe
        foreign key (user_id) references post_user_value (id)
);

create table role
(
    id         varchar(255) not null primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null
);

create table series
(
    id         binary(16) not null primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    body       varchar(255) not null,
    title      varchar(255) not null,
    user_id    binary(16) not null,
    constraint fk_series_user
        foreign key (user_id) references post_user_value (id)
);

create table series_post
(
    id           binary(16) not null primary key,
    created_at   datetime(6) not null,
    updated_at   datetime(6) not null,
    order_number int null,
    post_id      binary(16) null,
    series_id    binary(16) not null,
    constraint FKj3jb6322jo49bpjn6xyh0kytq
        foreign key (series_id) references series (id),
    constraint fk_series_post_post_id
        foreign key (post_id) references post (id)
);

create table tag
(
    id         varchar(255) not null primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null
);

create table post_tag
(
    id         binary(16) not null primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    post_id    binary(16) not null,
    tag_id     varchar(255) null,
    constraint FKc2auetuvsec0k566l0eyvr9cs
        foreign key (post_id) references post (id),
    constraint fk_post_tag_tag
        foreign key (tag_id) references tag (id)
);

create table user_entity
(
    id         binary(16) not null primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    account    varchar(255) not null,
    nick_name  varchar(255) null,
    password   varchar(255) null,
    status     varchar(255) not null,
    constraint UK_qpyntoml7p4g7nb8hggn6of20
        unique (account)
);

create table user_role
(
    id         binary(16) not null primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    role_id    varchar(255) null,
    user_id    binary(16) not null,
    constraint FK79ltvrbu1ni2ad7w7i9vers1k
        foreign key (user_id) references user_entity (id),
    constraint fk_user_role_role
        foreign key (role_id) references role (id)
);


INSERT INTO role (id, created_at, updated_at)
VALUES ('ADMIN', '2022-02-24T21:20', '2022-02-24T21:20');

INSERT INTO role (id, created_at, updated_at)
VALUES ('USER', '2022-02-24T21:20', '2022-02-24T21:20');

INSERT INTO tag(id, created_at, updated_at)
VALUES ('All', '2022-02-24T21:20', '2022-02-24T21:20');