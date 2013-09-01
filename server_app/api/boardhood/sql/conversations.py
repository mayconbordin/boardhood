from datetime import datetime

__all__ = ['FIND', 'FIND_REPLY', 'REPLIES', 'REPLIES_AFTER', 'CREATE', 'CREATE_NO_LOCATION',
                   'FIND_ALL_BY_USER', 'FIND_ALL_BY_USERNAME', 'COUNT_ALL_BY_USER',
                   'COUNT_ALL_BY_USERNAME', 'FIND_ALL_BY_SUBSCRIPTION', 'COUNT_ALL_BY_SUBSCRIPTION',
                   'FIND_ALL_BY_SUBSCRIPTION_AFTER', 'COUNT_ALL_BY_SUBSCRIPTION_AFTER',
                   'COUNT_REPLIES', 'COUNT_REPLIES_AFTER', 'build_find_all_query']

FIND = """
        SELECT c.id, c.created_at, c.message, u.username, u.avatar_url,
                i.id as interest_id, i.name as interest_name, cs.replies_count
        FROM conversations c
        INNER JOIN users u ON u.id = c.user_id
        INNER JOIN interests i ON i.id = c.interest_id
        LEFT JOIN conversations_summary cs ON c.id = cs.conversation_id
        WHERE c.id = %s
                AND c.interest_id = %s
                AND c.parent_id IS NULL
"""

FIND_REPLY = """
        SELECT c.id, c.created_at, c.message, u.username, u.avatar_url
        FROM conversations c, users u
        WHERE c.user_id = u.id
                AND c.id = %s
                AND c.interest_id = %s
                AND c.parent_id = %s
"""

REPLIES = """
        SELECT c.id, c.created_at, c.message, u.username, u.avatar_url
        FROM conversations c, users u
        WHERE c.user_id = u.id
                AND c.interest_id = %s
                AND c.parent_id = %s
        ORDER BY c.created_at
        LIMIT %s OFFSET %s
"""

REPLIES_AFTER = """
        SELECT c.id, c.created_at, c.message, u.username, u.avatar_url
        FROM conversations c, users u
        WHERE c.user_id = u.id
                AND c.interest_id = %s
                AND c.parent_id = %s
                AND c.created_at > %s
        ORDER BY c.created_at
        LIMIT %s OFFSET %s
"""

COUNT_REPLIES = """
        SELECT COUNT(*) AS count
        FROM conversations c
        WHERE c.interest_id = %s
                AND c.parent_id = %s
"""

COUNT_REPLIES_AFTER = """
        SELECT COUNT(*) AS count
        FROM conversations c
        WHERE c.interest_id = %s
                AND c.parent_id = %s
                AND c.created_at > %s
"""

CREATE = """
        INSERT INTO conversations(interest_id, user_id, message, latlng, location_wgs84, parent_id)
        VALUES(%(interest_id)s, %(author_id)s, %(message)s, POINT('%(location_lat)s, %(location_lon)s'), ST_GeomFromText('POINT(%(location_lon)s %(location_lat)s)', 4326), %(parent_id)s) RETURNING id, created_at
"""

CREATE_NO_LOCATION = """
        INSERT INTO conversations(interest_id, user_id, message, parent_id)
        VALUES(%(interest_id)s, %(author_id)s, %(message)s, %(parent_id)s) RETURNING id, created_at
"""

FIND_ALL_BY_USER = """
        SELECT c.id, c.created_at, c.message, cs.replies_count, u.username, u.avatar_url,
                i.id as interest_id, i.name as interest_name
        FROM conversations c
                INNER JOIN users u ON c.user_id = u.id
                INNER JOIN interests i ON c.interest_id = i.id
                LEFT JOIN conversations_summary cs ON c.id = cs.conversation_id
        WHERE c.user_id = %s
        ORDER BY c.created_at DESC
        LIMIT %s OFFSET %s
"""

FIND_ALL_BY_USERNAME = """
        SELECT c.id, c.created_at, c.message, cs.replies_count, u.username, u.avatar_url,
                i.id as interest_id, i.name as interest_name, c.parent_id
        FROM conversations c
                INNER JOIN users u ON c.user_id = u.id
                INNER JOIN interests i ON c.interest_id = i.id
                LEFT JOIN conversations_summary cs ON c.id = cs.conversation_id
        WHERE u.username = %s
        ORDER BY c.created_at DESC
        LIMIT %s OFFSET %s
"""

COUNT_ALL_BY_USER = """
        SELECT COUNT(*) AS count
        FROM conversations c
                INNER JOIN users u ON c.user_id = u.id
        WHERE c.user_id = %s
"""

COUNT_ALL_BY_USERNAME = """
        SELECT COUNT(*) AS count
        FROM conversations c
                INNER JOIN users u ON c.user_id = u.id
        WHERE u.username = %s
"""

FIND_ALL_BY_SUBSCRIPTION = """
SELECT c.id, c.created_at, c.message, cs.replies_count, u.username, u.avatar_url,
        i.id as interest_id, i.name as interest_name, c.parent_id
FROM conversations c
        INNER JOIN users u ON c.user_id = u.id
        INNER JOIN interests i ON c.interest_id = i.id
        LEFT JOIN conversations_summary cs ON c.id = cs.conversation_id
WHERE c.parent_id IN (SELECT cnu.conversation_id FROM conversations_users cnu WHERE cnu.user_id = %s)
        AND c.user_id <> %s
ORDER BY c.created_at DESC
LIMIT %s OFFSET %s
"""

FIND_ALL_BY_SUBSCRIPTION_AFTER = """
SELECT c.id, c.created_at, c.message, cs.replies_count, u.username, u.avatar_url,
        i.id as interest_id, i.name as interest_name, c.parent_id
FROM conversations c
        INNER JOIN users u ON c.user_id = u.id
        INNER JOIN interests i ON c.interest_id = i.id
        LEFT JOIN conversations_summary cs ON c.id = cs.conversation_id
WHERE c.parent_id IN (SELECT cnu.conversation_id FROM conversations_users cnu WHERE cnu.user_id = %s)
        AND c.user_id <> %s
        AND c.created_at > %s
ORDER BY c.created_at DESC
LIMIT %s OFFSET %s
"""

COUNT_ALL_BY_SUBSCRIPTION = """
SELECT COUNT(*) AS count
FROM conversations c
WHERE c.interest_id IN (SELECT ui.interest_id FROM users_interests ui WHERE ui.user_id = %s)
        AND c.user_id <> %s
"""

COUNT_ALL_BY_SUBSCRIPTION_AFTER = """
SELECT COUNT(*) AS count
FROM conversations c
WHERE c.interest_id IN (SELECT ui.interest_id FROM users_interests ui WHERE ui.user_id = %s)
        AND c.user_id <> %s
        AND c.created_at > %s
"""

FIND_ALL_ORDER_BY = ['recent', 'popular', 'distance']

def build_find_all_query(type='select', interest_id=None, order_by='recent',
                                                 location=None, radius=None, offset=0, limit=20,
                                                 user_id=None, after=None):
    if order_by not in FIND_ALL_ORDER_BY:
        order_by = 'recent'

    args = []
    sql = []

    if type == 'select':
        sql.append("""SELECT c.id, c.created_at, c.message, cs.replies_count, u.username, u.avatar_url, i.id as interest_id, i.name as interest_name """)
        if order_by == 'popular':
            sql.append(", score(cs.replies_count, c.created_at) ")

        if location:
            sql.append(", (ST_Distance_Sphere(c.location_wgs84, PointFromText('POINT(%s %s)', 4326))/1000) as distance ")
            args.extend([location[1], location[0]])
    elif type == 'count':
        sql.append("SELECT COUNT(*) as count ")

    sql.append("""
            FROM conversations c
            INNER JOIN users u ON c.user_id = u.id
            INNER JOIN interests i ON c.interest_id = i.id
            INNER JOIN conversations_summary cs ON cs.conversation_id = c.id
            WHERE c.parent_id IS NULL """)

    if interest_id:
        sql.append("AND c.interest_id = %s ")
        args.append(interest_id)

    if user_id:
        sql.append("AND c.interest_id IN (SELECT interest_id FROM users_interests WHERE user_id = %s) AND c.user_id <> %s ")
        args.extend([user_id, user_id])

    if radius and location:
        sql.append("AND ST_DWithin(ST_Transform(ST_GeomFromText('POINT(%s %s)', 4326), 26986), ST_Transform(c.location_wgs84, 26986), %s) ")
        args.extend([location[1], location[0], (radius * 1000)])

    if after and isinstance(after, datetime):
        sql.append("AND c.created_at > %s ")
        args.append(after.strftime("%Y-%m-%d %H:%M:%S"))

    if type == 'select':
        if order_by == 'distance' and location:
            sql.append("ORDER BY distance ")
        elif order_by == 'recent':
            sql.append("ORDER BY c.created_at DESC ")
        elif order_by == 'popular':
            sql.append("ORDER BY score DESC ")

        sql.append("LIMIT %s OFFSET %s")
        args.extend([limit, offset])

    return ''.join(sql), args
