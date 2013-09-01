__all__ = ['CREATE', 'EXISTS', 'AUTHENTICATE', 'FIND_BY_NAME', 'NAME_EXISTS',
           'EMAIL_EXISTS', 'UPDATE', 'UPDATE_AVATAR', 'FIND_BY_ID']

CREATE = "INSERT INTO users(username, email, password, status, avatar_url) VALUES(%(name)s, %(email)s, %(password)s, %(status)s, %(avatar_url)s) RETURNING id"

EXISTS = "SELECT id FROM users WHERE id = %s"

AUTHENTICATE = """
        SELECT u.id, u.username as name, u.email, u.avatar_url, u.password,
                us.conversations_count, us.interests_count
        FROM users u
        INNER JOIN users_summary us ON u.id = us.user_id
        WHERE username = %(name)s
"""

FIND_BY_NAME = """
        SELECT u.username as name, u.avatar_url, us.conversations_count, us.interests_count
        FROM users u
        INNER JOIN users_summary us ON u.id = us.user_id
        WHERE username = %s
"""

FIND_BY_ID = """
        SELECT u.username as name, u.email, u.avatar_url, us.conversations_count, us.interests_count
        FROM users u
        INNER JOIN users_summary us ON u.id = us.user_id
        WHERE u.id = %s
"""

NAME_EXISTS = "SELECT id FROM users WHERE username = %s"

EMAIL_EXISTS = "SELECT id FROM users WHERE email = %s"

UPDATE = """
        UPDATE users SET username = %(name)s, email = %(email)s,
                avatar_url = %(avatar_url)s, password = %(password)s
        WHERE id = %(id)s
"""

UPDATE_AVATAR = "UPDATE users SET avatar_url = %s WHERE id = %s"
