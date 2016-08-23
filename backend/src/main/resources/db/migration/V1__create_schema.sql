CREATE TABLE "accounts" (
  "account_id" UUID NOT NULL PRIMARY KEY,
  "login" VARCHAR(100) NOT NULL,
  "passwordHash" VARCHAR(100) NOT NULL,
  "email" VARCHAR(100) NOT NULL,
  "createdOn" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "status" VARCHAR(100) NOT NULL);
