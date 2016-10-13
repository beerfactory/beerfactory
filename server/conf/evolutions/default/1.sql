# --- !Ups
CREATE TABLE users (
    id varchar NOT NULL,
    PRIMARY KEY(id)
);

# --- !Downs
DROP TABLE users;