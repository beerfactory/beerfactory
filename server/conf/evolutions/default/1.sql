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
    foreign key (login_info_fk) references login_info(login_info_id)

);

# --- !Downs

DROP TABLE login_info;
DROP TABLE users;

