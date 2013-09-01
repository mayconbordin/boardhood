__all__ = ['FIND', 'FIND_WUSER', 'CREATE', 'NAME_EXISTS', 'EXISTS', 'FOLLOW', 'UNFOLLOW',
                   'FOLLOWING', 'FOLLOWERS', 'FIND_ALL_BY_USER', 'SEARCH_BEGINNING_WITH',
                   'FIND_ALL_BY_USERNAME', 'COUNT_ALL_BY_USER', 'COUNT_ALL_BY_USERNAME',
                   'SEARCH_BEGINNING_WITH_WUSER', 'SEARCH_BY_USER', 'build_find_all_query',
                   'COUNT_FOLLOWERS', 'build_count_all_query']

# followers_count, conversations_count, following (user is)
FIND = "SELECT id, name FROM interests WHERE id = %s"

FIND_WUSER = """
SELECT i.id, i.name, ins.conversations_count, ins.followers_count,
        EXISTS(SELECT * FROM users_interests WHERE user_id = %s AND interest_id = %s) as logged_user_follows
FROM interests i
INNER JOIN interests_summary ins ON ins.interest_id = i.id
WHERE id = %s
"""

CREATE = "INSERT INTO interests(name) VALUES(%s) RETURNING id"

NAME_EXISTS = "SELECT id FROM interests WHERE name = %s"

EXISTS = "SELECT id FROM interests WHERE id = %s"

FOLLOW = "INSERT INTO users_interests(user_id, interest_id) VALUES(%s, %s)"

UNFOLLOW = "DELETE FROM users_interests WHERE user_id = %s AND interest_id = %s"

FOLLOWING = "SELECT user_id FROM users_interests WHERE user_id = %s AND interest_id = %s"

FOLLOWERS = """
        SELECT u.id, u.username as name, u.avatar_url
        FROM users_interests ui
        INNER JOIN users u ON u.id = ui.user_id
        WHERE ui.interest_id = %s
        ORDER BY u.username
        LIMIT %s OFFSET %s
"""

COUNT_FOLLOWERS = """
        SELECT followers_count as count
        FROM interests_summary
        WHERE interest_id = %s
"""

FIND_ALL_BY_USER = """
        SELECT i.id, i.name FROM interests i
        INNER JOIN users_interests ui ON ui.interest_id = i.id
        WHERE ui.user_id = %s
        ORDER BY i.name
        LIMIT %s OFFSET %s
"""

SEARCH_BY_USER = """
        SELECT i.id, i.name
        FROM interests i
        INNER JOIN users_interests ui ON ui.interest_id = i.id
        WHERE ui.user_id = %s AND LOWER(i.name) LIKE LOWER(%s)
        ORDER BY i.name
        LIMIT %s
"""

FIND_ALL_BY_USERNAME = """
        SELECT i.id, i.name FROM interests i
        INNER JOIN users_interests ui ON ui.interest_id = i.id
        INNER JOIN users u ON u.id = ui.user_id
        WHERE u.username = %s
        ORDER BY i.name
        LIMIT %s OFFSET %s
"""

COUNT_ALL_BY_USER = """
        SELECT COUNT(*) AS count FROM interests i
        INNER JOIN users_interests ui ON ui.interest_id = i.id
        WHERE ui.user_id = %s
"""

COUNT_ALL_BY_USERNAME = """
        SELECT COUNT(*) AS count FROM interests i
        INNER JOIN users_interests ui ON ui.interest_id = i.id
        INNER JOIN users u ON u.id = ui.user_id
        WHERE u.username = %s
"""

SEARCH_BEGINNING_WITH = """
        SELECT id, name
        FROM interests
        WHERE LOWER(name) LIKE LOWER(%s)
        ORDER BY name
        LIMIT %s
"""

SEARCH_BEGINNING_WITH_WUSER = """
        SELECT i.id, i.name,
                EXISTS(SELECT * FROM users_interests ui WHERE ui.user_id = %s AND ui.interest_id = i.id) as logged_user_follows
        FROM interests i
        WHERE LOWER(i.name) LIKE LOWER(%s)
        ORDER BY i.name
        LIMIT %s
"""


FIND_ALL_ORDER_BY = ['recent', 'popular', 'distance']

def build_find_all_query(order_by='recent', location=None, radius=None, offset=0, limit=20, user_id=None):
    if order_by not in FIND_ALL_ORDER_BY:
        order_by = 'recent'

    if order_by is 'distance' and not location:
        return []

    args = []
    sql = ["SELECT * FROM (SELECT DISTINCT ON (i.id) i.id, i.name "]

    if order_by == 'recent':
        sql.append(", c.created_at ")
    elif order_by == 'popular':
        sql.append(", ins.conversations_count ")
    elif order_by == 'distance':
        sql.append(", ST_Distance(c.location, transform(PointFromText('POINT(%s %s)', 4269), 32661)) as distance ")
        args.extend([location[1], location[0]])
    if user_id:
        sql.append(", EXISTS(SELECT * FROM users_interests ui WHERE ui.user_id = %s AND ui.interest_id = i.id) as logged_user_follows ")
        args.append(user_id)

    sql.append("FROM interests i LEFT JOIN conversations c ON i.id = c.interest_id ")

    if order_by == 'popular':
        sql.append("LEFT JOIN interests_summary ins ON ins.interest_id = i.id ")

    if location and radius:
        sql.append("WHERE ST_DWithin(c.location, transform(PointFromText('POINT(%s %s)', 4269), 32661), %s) ")
        args.extend([location[1], location[0], radius])

    sql.append(") as interests ")

    if order_by == 'recent':
        sql.append("ORDER BY created_at DESC ")
    elif order_by == 'popular':
        sql.append("ORDER BY conversations_count DESC ")
    elif order_by == 'distance' and location:
        sql.append("ORDER BY distance ")

    sql.append("LIMIT %s OFFSET %s")
    args.extend([limit, offset])

    return ''.join(sql), args

def build_count_all_query(location=None, radius=None,):
    args = []

    sql = ["""
            SELECT COUNT(*) as count FROM (
                    SELECT DISTINCT ON (i.id) i.id
                    FROM interests i
                    LEFT JOIN conversations c ON i.id = c.interest_id
    """]

    if location and radius:
        sql.append("WHERE ST_DWithin(c.location, transform(PointFromText('POINT(%s %s)', 4269), 32661), %s) ")
        args.extend([location[1], location[0], radius])

    sql.append(") as interests ")

    return ''.join(sql), args
