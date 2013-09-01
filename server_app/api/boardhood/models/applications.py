from datetime import datetime
from hashlib import sha1
from dictshield.base import ShieldException
from dictshield.fields import StringField, IntField, DateTimeField, EmailField, URLField
from psycopg2 import DatabaseError

from functools import wraps
from flask import request

from boardhood.helpers.response import json
from boardhood.sql.applications import *
from boardhood.models.base import BaseModel

class Application(BaseModel):
    _public_fields = []

    LEVEL_ALL = 10
    LEVEL_3RD_PARTY = 1

    id = IntField()
    name = StringField(max_length=40)
    key = StringField(max_length=40)
    level = IntField()

    def generate_key(self):
        key_str = '%s:%s:%s' % (str(self.id), self.name, str(datetime.utcnow()))
        self.key = sha1(key_str).hexdigest()

    @staticmethod
    def parse(row):
        if not row:
            return None
        return Application(**row)

    @staticmethod
    def findByKey(key):
        try:
            cursor = Application.cursor()
            cursor.execute(FIND_BY_KEY, [key])
            return Application.parse(cursor.fetchone())
        except Exception, e:
            Application.report_error(e, cursor)
            return None


def require_app_auth(app, level):
    def wrapped(f):
        @wraps(f)
        def decorated(*args, **kwargs):
            api_key = request.args.get('api_key')
            if not api_key:
                return json({"message": "You need an API Key to do this"}, 401)

            Application.set_config(app)

            application = Application.findByKey(api_key)
            if not application:
                return json({"message": "Your API Key is not valid"}, 401)

            if application.level < level:
                return json({"message": "Your application doesn't have enough rights to do this"}, 401)
            return f(*args, **kwargs)
        return decorated
    return wrapped
