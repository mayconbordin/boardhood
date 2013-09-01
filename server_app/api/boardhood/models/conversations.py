from datetime import datetime

from boardhood.models.base import BaseModel, ValidationException
from boardhood.models.users import User
from boardhood.models.interests import Interest
from boardhood.sql.conversations import *
from boardhood.helpers.fields import OpaqueIdField, IDField

from dictshield.base import ShieldException
from dictshield.fields import StringField, IntField, DateTimeField, GeoPointField, FloatField
from dictshield.fields.compound import EmbeddedDocumentField
from psycopg2 import DatabaseError

class Conversation(BaseModel):
    _public_fields = ['created_at', 'location', 'message', 'interest', 'author',
                      'replies_count', 'distance', 'parent']
    _key = 0x9c12cf6b

    id = IDField(_key)
    ext_id = OpaqueIdField(_key)
    created_at = DateTimeField()
    location = GeoPointField()
    message = StringField(max_length=2000)
    interest = EmbeddedDocumentField(Interest)
    author = EmbeddedDocumentField(User)
    replies_count = IntField()
    score = FloatField()
    distance = FloatField()

    #def __new__(cls, *args, **kwargs):
    #       cls.parent = EmbeddedDocumentField(cls)
    #       return object.__new__(cls, *args, **kwargs)

    def to_sqldict(self):
        data = BaseModel.to_sqldict(self)
        data['parent_id'] = self.parent.id if self.parent else None
        return data

    def to_dict(self):
        data = BaseModel.to_dict(self)
        if self.parent:
            data['parent'] = self.parent.to_dict()
        return data

    @staticmethod
    def parse(row, **kwargs):
        if not row:
            return None

        if len(kwargs) > 0:
            row.update(kwargs)

        obj = Conversation(**row)
        obj.author = User(id=row.get('user_id'), name=row.get('username'), avatar_url=row.get('avatar_url'))

        if row.get('lat') and row.get('lon'):
            obj.location = {'lat': row.get('lat'), 'lon': row.get('lon')}

        if row.get('interest_id') or row.get('interest_name'):
            obj.interest = Interest(id=row.get('interest_id'), name=row.get('interest_name'))

        if row.get('parent_id') or row.get('parent_name'):
            obj.parent = Conversation(id=row.get('parent_id'), name=row.get('parent_name'))

        return obj

    @staticmethod
    def parse_all(rows):
        records = []
        for row in rows:
            records.append(Conversation.parse(row))
        return records

    @staticmethod
    def find(interest_id, id):
        try:
            cursor = Conversation.cursor()
            cursor.execute(FIND, [id, interest_id])
            return Conversation.parse(cursor.fetchone())
        except Exception, e:
            Conversation.report_error(e, cursor)
            return None

    @staticmethod
    def findReply(interest_id, parent_id, id):
        try:
            cursor = Conversation.cursor()
            cursor.execute(FIND_REPLY, [id, interest_id, parent_id])
            return Conversation.parse(cursor.fetchone())
        except Exception, e:
            Conversation.report_error(e, cursor)
            return None

    @staticmethod
    def findAllByUser(user_id, offset=0, limit=20):
        try:
            cursor = Conversation.cursor()
            cursor.execute(FIND_ALL_BY_USER, [user_id, limit, offset])
            return Conversation.parse_all(cursor.fetchall())
        except Exception, e:
            Conversation.report_error(e, cursor)
            return None

    @staticmethod
    def countAllByUser(user_id):
        try:
            cursor = Conversation.cursor()
            cursor.execute(COUNT_ALL_BY_USER, [user_id])
            return cursor.fetchone()['count']
        except Exception, e:
            Conversation.report_error(e, cursor)
            return 0

    @staticmethod
    def findAllByUserName(user_name, offset=0, limit=20):
        try:
            cursor = Conversation.cursor()
            cursor.execute(FIND_ALL_BY_USERNAME, [user_name, limit, offset])
            return Conversation.parse_all(cursor.fetchall())
        except Exception, e:
            Conversation.report_error(e, cursor)
            return None

    @staticmethod
    def countAllByUserName(user_name):
        try:
            cursor = Conversation.cursor()
            cursor.execute(COUNT_ALL_BY_USERNAME, [user_name])
            return cursor.fetchone()['count']
        except Exception, e:
            Conversation.report_error(e, cursor)
            return 0

    @staticmethod
    def replies(interest_id, id, after=None, offset=0, limit=20):
        try:
            cursor = Conversation.cursor()

            if after:
                cursor.execute(REPLIES_AFTER, [interest_id, id, after, limit, offset])
            else:
                cursor.execute(REPLIES, [interest_id, id, limit, offset])

            return Conversation.parse_all(cursor.fetchall())
        except Exception, e:
            Conversation.report_error(e, cursor)
            return []

    @staticmethod
    def countReplies(interest_id, id, after=None):
        try:
            cursor = Conversation.cursor()

            if after:
                cursor.execute(COUNT_REPLIES_AFTER, [interest_id, id, after])
            else:
                cursor.execute(COUNT_REPLIES, [interest_id, id])

            return cursor.fetchone()['count']
        except Exception, e:
            Conversation.report_error(e, cursor)
            return []

    @staticmethod
    def create(c):
        try:
            cursor = Conversation.cursor()
            c.validate()

            if not c.location:
                cursor.execute(CREATE_NO_LOCATION, c.to_sqldict())
            else:
                cursor.execute(CREATE, c.to_sqldict())

            Conversation.db.commit()
            data = cursor.fetchone()
            c.id = data['id']
            c.created_at = data['created_at']
            return c
        except ShieldException, e:
            raise ValidationException(e)
        except DatabaseError, e:
            if e.pgcode == '23505': # already exists
                return c
            else:
                Interest.report_error(e, cursor)
                return False
        except Exception, e:
            print e
            Conversation.report_error(e, cursor)
            return False

    @staticmethod
    def countAll(interest_id=None, order_by='recent', location=None, radius=None,
                             user_id=None, after=None):
        try:
            cursor = Conversation.cursor()
            sql, args = build_find_all_query('count', interest_id=interest_id,
                                                                             order_by=order_by, location=location,
                                                                             radius=radius, user_id=user_id,
                                                                             after=after)
            cursor.execute(sql, args)
            return cursor.fetchone()['count']
        except Exception, e:
            Conversation.report_error(e, cursor)
            return 0

    @staticmethod
    def findAll(interest_id=None, order_by='recent', location=None, radius=None,
                            offset=0, limit=20, user_id=None, after=None):
        try:
            cursor = Conversation.cursor()
            sql, args = build_find_all_query('select', interest_id, order_by,
                                                                             location, radius, offset, limit,
                                                                             user_id, after)
            cursor.execute(sql, args)
            return Conversation.parse_all(cursor.fetchall())
        except Exception, e:
            Conversation.report_error(e, cursor)
            return []

    @staticmethod
    def findAllBySubscription(user_id, offset=0, limit=20, after=None):
        try:
            cursor = Conversation.cursor()
            if after:
                cursor.execute(FIND_ALL_BY_SUBSCRIPTION_AFTER, [user_id, user_id, after, limit, offset])
            else:
                cursor.execute(FIND_ALL_BY_SUBSCRIPTION, [user_id, user_id, limit, offset])

            return Conversation.parse_all(cursor.fetchall())
        except Exception, e:
            Conversation.report_error(e, cursor)
            return None

    @staticmethod
    def countAllBySubscription(user_id, after=None):
        try:
            cursor = Conversation.cursor()
            if after:
                cursor.execute(COUNT_ALL_BY_SUBSCRIPTION_AFTER, [user_id, user_id, after])
            else:
                cursor.execute(COUNT_ALL_BY_SUBSCRIPTION, [user_id, user_id])
            return cursor.fetchone()['count']
        except Exception, e:
            Conversation.report_error(e, cursor)
            return 0

Conversation.parent = EmbeddedDocumentField(Conversation)
