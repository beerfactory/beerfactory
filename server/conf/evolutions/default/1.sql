# --- !Ups

create table login_info(
    login_info_id varchar primary key,
    provider_id varchar,
    provider_key varchar
);

create table "user" (
    user_id varchar not null primary key,
    login_info_fk VARCHAR unique,
    activated boolean,
    email varchar,
    firstname varchar,
    lastname varchar,
    fullname varchar,
    locales varchar,
    avatar_url varchar,
    foreign key (login_info_fk) references login_info(login_info_id)
);

create table auth_token(
    token_id varchar not null primary key,
    user_fk varchar not null,
    expiry timestamp,
    foreign key (user_fk) references "user"(user_id)
);

# --- !Downs

DROP TABLE login_info;
DROP table auth_token;
DROP TABLE "user";

