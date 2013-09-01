-- /usr/lib/postgresql/8.4/bin$ sudo createlang -U postgres -W -d dbname plpgsql -h localhost


-- update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at() RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP AT TIME ZONE 'UTC';
   RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';
    

-- create user summary
CREATE OR REPLACE FUNCTION create_user_summary() RETURNS trigger AS $$
BEGIN
	INSERT INTO users_summary(user_id) VALUES(NEW.id);
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

    
-- report user changes
CREATE OR REPLACE FUNCTION report_user_changes() RETURNS trigger AS $$
BEGIN
	IF NEW.username != OLD.username THEN
		INSERT INTO users_changes(user_id, created_at, column_name, value) VALUES(OLD.id, NEW.updated_at, 'username', OLD.username);
	END IF;
	
	IF NEW.email != OLD.email THEN
		INSERT INTO users_changes(user_id, created_at, column_name, value) VALUES(OLD.id, NEW.updated_at, 'email', OLD.email);
	END IF;
	
	IF NEW.status != OLD.status THEN
		INSERT INTO users_changes(user_id, created_at, column_name, value) VALUES(OLD.id, NEW.updated_at, 'status', OLD.status);
	END IF;
	
	IF NEW.avatar_url != OLD.avatar_url THEN
		INSERT INTO users_changes(user_id, created_at, column_name, value) VALUES(OLD.id, NEW.updated_at, 'avatar_url', OLD.avatar_url);
	END IF;
	
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- create interest summary
CREATE OR REPLACE FUNCTION create_interest_summary() RETURNS trigger AS $$
BEGIN
	INSERT INTO interests_summary(interest_id) VALUES(NEW.id);
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- report user interest created
CREATE OR REPLACE FUNCTION report_user_interest_created() RETURNS trigger AS $$
BEGIN
	UPDATE users_summary SET interests_count = interests_count + 1 WHERE user_id = NEW.user_id;
	UPDATE interests_summary SET followers_count = followers_count + 1 WHERE interest_id = NEW.interest_id;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- report user interest removed
CREATE OR REPLACE FUNCTION report_user_interest_removed() RETURNS trigger AS $$
BEGIN
	BEGIN
		INSERT INTO users_old_interests(user_id, interest_id, started, ended) VALUES(OLD.user_id, OLD.interest_id, OLD.created_at, CURRENT_TIMESTAMP AT TIME ZONE 'UTC');
	EXCEPTION WHEN unique_violation THEN
		-- do nothing
	END;
	
	UPDATE users_summary SET interests_count = interests_count - 1 WHERE user_id = OLD.user_id;
	UPDATE interests_summary SET followers_count = followers_count - 1 WHERE interest_id = OLD.interest_id;
	
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- update all summaries (users, conversations and interests)
CREATE OR REPLACE FUNCTION update_summaries() RETURNS trigger AS $$
BEGIN
	-- new conversation
	IF NEW.parent_id IS NULL THEN
		UPDATE interests_summary SET conversations_count = conversations_count + 1, conversations_total_count = conversations_total_count + 1 WHERE interest_id = NEW.interest_id;
		INSERT INTO conversations_summary(conversation_id) VALUES(NEW.id);
		INSERT INTO conversations_users(conversation_id, user_id) VALUES(NEW.id, NEW.user_id);
		UPDATE users_summary SET conversations_count = conversations_count + 1 WHERE user_id = NEW.user_id;
		
	-- new reply
	ELSE
		UPDATE interests_summary SET conversations_total_count = conversations_total_count + 1 WHERE interest_id = NEW.interest_id;
		UPDATE conversations_summary SET replies_count = replies_count + 1 WHERE conversation_id = NEW.parent_id;
		INSERT INTO conversations_users(conversation_id, user_id) VALUES(NEW.parent_id, NEW.user_id);
		UPDATE users_summary SET conversations_count = conversations_count + 1, replies_count = replies_count + 1 WHERE user_id = NEW.user_id;
	END IF;
	
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- add created_at date
CREATE OR REPLACE FUNCTION add_created_at() RETURNS trigger AS $$
BEGIN
	NEW.created_at := CURRENT_TIMESTAMP AT TIME ZONE 'UTC';
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;



-- SCORE FUNCTION
CREATE OR REPLACE FUNCTION score(replies integer, created_at timestamp) RETURNS float AS $$
DECLARE
	diff integer;
	val float;
BEGIN
	diff := ABS(EXTRACT(EPOCH FROM current_timestamp - created_at))::Integer;
	val := (replies + 1)/diff^1.5;
	RETURN val;
END;
$$ LANGUAGE plpgsql;
    
    
-- TRIGGERS
-- -----------------------------------------------------------------------------

-- users
DROP TRIGGER IF EXISTS before_insert_user ON users;
CREATE TRIGGER before_insert_user BEFORE INSERT ON users
    FOR EACH ROW EXECUTE PROCEDURE add_created_at();
    
DROP TRIGGER IF EXISTS before_insert_user_updated ON users;
CREATE TRIGGER before_insert_user_updated BEFORE INSERT ON users
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();
    
DROP TRIGGER IF EXISTS after_insert_user ON users;
CREATE TRIGGER after_insert_user AFTER INSERT ON users
	FOR EACH ROW EXECUTE PROCEDURE create_user_summary();
	
DROP TRIGGER IF EXISTS before_update_user ON users;
CREATE TRIGGER before_update_user BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();
	
DROP TRIGGER IF EXISTS after_update_user ON users;
CREATE TRIGGER after_update_user AFTER UPDATE ON users
    FOR EACH ROW EXECUTE PROCEDURE report_user_changes();
    
-- users_changes
DROP TRIGGER IF EXISTS before_insert_user_change ON users_changes;
CREATE TRIGGER before_insert_user_change BEFORE INSERT ON users_changes
    FOR EACH ROW EXECUTE PROCEDURE add_created_at();
    
-- users_summary
DROP TRIGGER IF EXISTS before_update_user_summary ON users_summary;
CREATE TRIGGER before_update_user_summary BEFORE UPDATE ON users_summary
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();
    
DROP TRIGGER IF EXISTS before_insert_user_summary ON users_summary;
CREATE TRIGGER before_insert_user_summary BEFORE INSERT ON users_summary
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();
    
-- interests_summary
DROP TRIGGER IF EXISTS before_update_interest_summary ON interests_summary;
CREATE TRIGGER before_update_interest_summary BEFORE UPDATE ON interests_summary
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();
    
DROP TRIGGER IF EXISTS before_insert_interest_summary ON interests_summary;
CREATE TRIGGER before_insert_interest_summary BEFORE INSERT ON interests_summary
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();
    
-- conversations_summary
DROP TRIGGER IF EXISTS before_update_conversation_summary ON conversations_summary;
CREATE TRIGGER before_update_conversation_summary BEFORE UPDATE ON conversations_summary
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();
    
DROP TRIGGER IF EXISTS before_insert_conversation_summary ON conversations_summary;
CREATE TRIGGER before_insert_conversation_summary BEFORE INSERT ON conversations_summary
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at();
    
-- interests
DROP TRIGGER IF EXISTS before_insert_interest ON interests;
CREATE TRIGGER before_insert_interest BEFORE INSERT ON interests
    FOR EACH ROW EXECUTE PROCEDURE add_created_at();
    
DROP TRIGGER IF EXISTS after_insert_interest ON interests;
CREATE TRIGGER after_insert_interest AFTER INSERT ON interests
    FOR EACH ROW EXECUTE PROCEDURE create_interest_summary();
    
-- users_interests
DROP TRIGGER IF EXISTS before_insert_user_interest ON users_interests;
CREATE TRIGGER before_insert_user_interest BEFORE INSERT ON users_interests
    FOR EACH ROW EXECUTE PROCEDURE add_created_at();
   
DROP TRIGGER IF EXISTS after_insert_user_interest ON users_interests;
CREATE TRIGGER after_insert_user_interest AFTER INSERT ON users_interests
    FOR EACH ROW EXECUTE PROCEDURE report_user_interest_created();
    
DROP TRIGGER IF EXISTS after_delete_user_interest ON users_interests;
CREATE TRIGGER after_delete_user_interest AFTER DELETE ON users_interests
    FOR EACH ROW EXECUTE PROCEDURE report_user_interest_removed();
    
-- conversations
DROP TRIGGER IF EXISTS after_insert_conversation ON conversations;
CREATE TRIGGER after_insert_conversation AFTER INSERT ON conversations
    FOR EACH ROW EXECUTE PROCEDURE update_summaries();
    
DROP TRIGGER IF EXISTS before_insert_conversation ON conversations;
CREATE TRIGGER before_insert_conversation BEFORE INSERT ON conversations
    FOR EACH ROW EXECUTE PROCEDURE add_created_at();
