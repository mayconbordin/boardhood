-- Create tables section -------------------------------------------------
-- Table users

CREATE TABLE users(
 id Serial NOT NULL,
 username Character varying(80) NOT NULL,
 email Character varying(120) NOT NULL,
 password Character(60) NOT NULL,
 status Numeric(1,0) NOT NULL,
 avatar_url Character varying(500),
 created_at Timestamp NOT NULL,
 updated_at Timestamp NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table users

ALTER TABLE users ADD CONSTRAINT pk_users PRIMARY KEY (id)
;

ALTER TABLE users ADD CONSTRAINT un_username UNIQUE (username)
;

ALTER TABLE users ADD CONSTRAINT un_email UNIQUE (email)
;

-- Table users_changes

CREATE TABLE users_changes(
 id Serial NOT NULL,
 user_id Integer NOT NULL,
 created_at Timestamp NOT NULL,
 column_name Character varying(15) NOT NULL,
 value Character varying(500) NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table users_changes

ALTER TABLE users_changes ADD CONSTRAINT pk_users_changes PRIMARY KEY (id)
;

-- Table interests

CREATE TABLE interests(
 id Serial NOT NULL,
 name Character varying(45) NOT NULL,
 created_at Timestamp NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table interests

ALTER TABLE interests ADD CONSTRAINT pk_interests PRIMARY KEY (id)
;

ALTER TABLE interests ADD CONSTRAINT un_name UNIQUE (name)
;

-- Table users_interests

CREATE TABLE users_interests(
 user_id Integer NOT NULL,
 interest_id Integer NOT NULL,
 created_at Timestamp NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table users_interests

ALTER TABLE users_interests ADD CONSTRAINT pk_users_interests PRIMARY KEY (user_id,interest_id)
;

-- Table users_old_interests

CREATE TABLE users_old_interests(
 user_id Integer NOT NULL,
 interest_id Integer NOT NULL,
 started Timestamp NOT NULL,
 ended Timestamp NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table users_old_interests

ALTER TABLE users_old_interests ADD CONSTRAINT pk_users_old_interests PRIMARY KEY (user_id,interest_id)
;

-- Table conversations

CREATE TABLE conversations(
 id Serial NOT NULL,
 created_at Timestamp NOT NULL,
 latlng Point,
 message Text NOT NULL,
 user_id Integer NOT NULL,
 interest_id Integer NOT NULL,
 parent_id Integer
)
WITH (OIDS=FALSE)
;

-- Add keys for table conversations

ALTER TABLE conversations ADD CONSTRAINT pk_conversations PRIMARY KEY (id)
;

-- Table interests_summary

CREATE TABLE interests_summary(
 interest_id Integer NOT NULL,
 conversations_count Integer DEFAULT 0 NOT NULL,
 conversations_total_count Integer DEFAULT 0 NOT NULL,
 followers_count Integer DEFAULT 0 NOT NULL,
 updated_at Timestamp NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table interests_summary

ALTER TABLE interests_summary ADD CONSTRAINT pk_interests_summary PRIMARY KEY (interest_id)
;

-- Table conversations_summary

CREATE TABLE conversations_summary(
 conversation_id Integer NOT NULL,
 replies_count Integer DEFAULT 0 NOT NULL,
 updated_at Timestamp NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table conversations_summary

ALTER TABLE conversations_summary ADD CONSTRAINT pk_conversations_summary PRIMARY KEY (conversation_id)
;

-- Table conversations_users

CREATE TABLE conversations_users(
 user_id Integer NOT NULL,
 conversation_id Integer NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table conversations_users

ALTER TABLE conversations_users ADD CONSTRAINT pk_conversations_users PRIMARY KEY (user_id,conversation_id)
;

-- Table users_summary

CREATE TABLE users_summary(
 user_id Integer NOT NULL,
 conversations_count Integer DEFAULT 0 NOT NULL,
 replies_count Integer DEFAULT 0 NOT NULL,
 interests_count Integer DEFAULT 0 NOT NULL,
 updated_at Timestamp NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table users_summary

ALTER TABLE users_summary ADD CONSTRAINT pk_users_summary PRIMARY KEY (user_id)
;

-- Table applications

CREATE TABLE applications(
 id Serial NOT NULL,
 name Character varying(40) NOT NULL,
 key Character(40) NOT NULL,
 level Smallint NOT NULL
)
WITH (OIDS=FALSE)
;

-- Add keys for table applications

ALTER TABLE applications ADD CONSTRAINT pk_applications PRIMARY KEY (id)
;

-- Create relationships section ------------------------------------------------- 

ALTER TABLE users_changes ADD CONSTRAINT fk_users_userschanges FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE users_interests ADD CONSTRAINT fk_users_usersinterests FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE users_interests ADD CONSTRAINT fk_interests_usersinterests FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE users_old_interests ADD CONSTRAINT fk_users_usersoldinterests FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE users_old_interests ADD CONSTRAINT fk_interests_usersoldinterests FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE conversations ADD CONSTRAINT fk_users_conversations FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE conversations ADD CONSTRAINT fk_interests_conversations FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE conversations ADD CONSTRAINT fk_conversations_conversations FOREIGN KEY (parent_id) REFERENCES conversations (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE interests_summary ADD CONSTRAINT fk_interests_interestssummary FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE conversations_summary ADD CONSTRAINT fk_conversations_convsummary FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE conversations_users ADD CONSTRAINT fk_users_convusers FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE conversations_users ADD CONSTRAINT fk_conversations_convusers FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE users_summary ADD CONSTRAINT fk_users_userssummary FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION
;
