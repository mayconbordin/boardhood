from boardhood.models.base import BaseModel, ValidationException
from boardhood.models.users import User
from boardhood.sql.interests import *
from boardhood.helpers.fields import OpaqueIdField, IDField

from dictshield.base import ShieldException
from dictshield.fields import StringField, IntField, BooleanField
from psycopg2 import DatabaseError, IntegrityError

class Interest(BaseModel):
    _public_fields = ['name', 'followers_count', 'conversations_count', 'logged_user_follows']
    _key = 0x7f74cd5c

    id = IDField(_key)
    ext_id = OpaqueIdField(_key)
    name = StringField(max_length=45)
    followers_count = IntField()
    conversations_count = IntField()
    logged_user_follows = BooleanField()

    @staticmethod
    def parse(row):
        if not row:
            return None

        return Interest(**row)

    @staticmethod
    def parse_all(rows):
        records = []
        for row in rows:
            records.append(Interest.parse(row))
        return records

    @staticmethod
    def find(id, user_id=None):
        try:
            cursor = Interest.cursor()

            if user_id:
                cursor.execute(FIND_WUSER, [user_id, id, id])
            else:
                cursor.execute(FIND, [id])

            return Interest.parse(cursor.fetchone())
        except Exception, e:
            Interest.report_error(e, cursor)
            return None

    @staticmethod
    def create(i):
        try:
            cursor = Interest.cursor()
            i.validate()

            cursor.execute(CREATE, [i.name])
            Interest.db.commit()
            i.id = cursor.fetchone()['id']
            return i
        except ShieldException, e:
            raise ValidationException(e)
        except DatabaseError, e:
            Interest.db.rollback()
            if e.pgcode == '23505': # already exists
                return i
            else:
                Interest.report_error(e, cursor)
                return False
        except Exception, e:
            Interest.report_error(e, cursor)
            return False

    @staticmethod
    def nameExists(name):
        try:
            cursor = Interest.cursor()
            cursor.execute(NAME_EXISTS, [name])
            if cursor.fetchone() is None:
                return False
            else:
                return True
        except Exception, e:
            Interest.report_error(e, cursor)
            return False

    @staticmethod
    def exists(id):
        try:
            cursor = Interest.cursor()
            cursor.execute(EXISTS, [str(id)])
            if cursor.fetchone() is None:
                return False
            else:
                return True
        except Exception, e:
            Interest.report_error(e, cursor)
            return False

    @staticmethod
    def follow(id, user_id):
        try:
            cursor = Interest.cursor()
            cursor.execute(FOLLOW, [str(user_id), str(id)])
            Interest.db.commit()
            return True
        except DatabaseError, e:
            Interest.db.rollback()
            if e.pgcode == '23505': # already exists
                return True
            else:
                #Interest.report_error(e, cursor)
                return False
        except Exception, e:
            #Interest.report_error(e, cursor)
            return False

    @staticmethod
    def unfollow(id, user_id):
        try:
            cursor = Interest.cursor()
            cursor.execute(UNFOLLOW, [str(user_id), str(id)])
            Interest.db.commit()
            return True
        except DatabaseError, e:
            Interest.db.rollback()
            return False
        except Exception, e:
            Interest.report_error(e, cursor)
            return False

    @staticmethod
    def following(id, user_id):
        try:
            cursor = Interest.cursor()
            cursor.execute(FOLLOWING, [str(user_id), str(id)])
            if not cursor.fetchone():
                return False
            else:
                return True
        except Exception, e:
            Interest.report_error(e, cursor)
            return False

    @staticmethod
    def followers(id, offset=0, limit=20):
        try:
            cursor = Interest.cursor()
            cursor.execute(FOLLOWERS, [str(id), limit, offset])
            return User.parse_all(cursor.fetchall())
        except Exception, e:
            Interest.report_error(e, cursor)
            return False

    @staticmethod
    def countFollowers(id):
        try:
            cursor = Interest.cursor()
            cursor.execute(COUNT_FOLLOWERS, [str(id)])
            return cursor.fetchone()['count']
        except Exception, e:
            Interest.report_error(e, cursor)
            return 0

    @staticmethod
    def countAll(location=None, radius=None):
        try:
            cursor = Interest.cursor()
            sql, args = build_count_all_query(location, radius)
            cursor.execute(sql, args)
            return cursor.fetchone()['count']
        except Exception, e:
            Interest.report_error(e, cursor)
            return 0

    @staticmethod
    def findAll(order_by='recent', location=None, radius=None, offset=0, limit=20, user_id=None):
        try:
            cursor = Interest.cursor()
            sql, args = build_find_all_query(order_by, location, radius, offset, limit, user_id)
            cursor.execute(sql, args)
            return Interest.parse_all(cursor.fetchall())
        except Exception, e:
            Interest.report_error(e, cursor)
            return []

    @staticmethod
    def searchByUser(query, user_id, limit=20):
        try:
            cursor = Interest.cursor()
            cursor.execute(SEARCH_BY_USER, [user_id, query + '%', limit])
            return Interest.parse_all(cursor.fetchall())
        except Exception, e:
            Interest.report_error(e, cursor)
            return []

    @staticmethod
    def findAllByUser(user_id, offset=0, limit=20):
        try:
            cursor = Interest.cursor()
            cursor.execute(FIND_ALL_BY_USER, [user_id, limit, offset])
            return Interest.parse_all(cursor.fetchall())
        except Exception, e:
            Interest.report_error(e, cursor)
            return []

    @staticmethod
    def countAllByUser(user_id):
        try:
            cursor = Interest.cursor()
            cursor.execute(COUNT_ALL_BY_USER, [user_id])
            return cursor.fetchone()['count']
        except Exception, e:
            Interest.report_error(e, cursor)
            return 0

    @staticmethod
    def findAllByUserName(user_name, offset=0, limit=20):
        try:
            cursor = Interest.cursor()
            cursor.execute(FIND_ALL_BY_USERNAME, [user_name, limit, offset])
            return Interest.parse_all(cursor.fetchall())
        except Exception, e:
            Interest.report_error(e, cursor)
            return []

    @staticmethod
    def countAllByUserName(user_id):
        try:
            cursor = Interest.cursor()
            cursor.execute(COUNT_ALL_BY_USERNAME, [user_id])
            return cursor.fetchone()['count']
        except Exception, e:
            Interest.report_error(e, cursor)
            return 0

    @staticmethod
    def search(query, limit=10, user_id=None):
        try:
            cursor = Interest.cursor()
            if user_id:
                cursor.execute(SEARCH_BEGINNING_WITH_WUSER, [user_id, query + '%', limit])
            else:
                cursor.execute(SEARCH_BEGINNING_WITH, [query + '%', limit])
            return Interest.parse_all(cursor.fetchall())
        except Exception, e:
            Interest.report_error(e, cursor)
            return []
