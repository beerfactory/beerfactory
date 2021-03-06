# --- !Ups

create table auth_login_info(
    login_info_id varchar primary key,
    provider_id varchar,
    provider_key varchar
);

create table auth_password_info (
    password_info_id varchar primary key,
    login_info_fk varchar unique,
    hasher varchar,
    password varchar,
    salt varchar,
    foreign key (login_info_fk) references auth_login_info(login_info_id)
);

create table auth_user (
    user_id varchar not null primary key,
    login_info_fk VARCHAR unique,
    email_verified boolean,
    email varchar,
    username varchar,
    firstname varchar,
    lastname varchar,
    nickname varchar,
    locale varchar,
    avatar_url varchar,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    foreign key (login_info_fk) references auth_login_info(login_info_id)
);

create table auth_token (
    token_id varchar not null primary key,
    user_fk varchar not null,
    expiry timestamp,
    foreign key (user_fk) references auth_user(user_id)
);

# --- !Downs

drop table auth_password_info;
drop table auth_login_info;
drop table auth_token;
drop table auth_user;

