import bcrypt
import tempfile

from datetime import datetime
from hashlib import sha256, sha1
from dictshield.base import ShieldException
from dictshield.fields import StringField, IntField, DateTimeField, EmailField, URLField
from psycopg2 import DatabaseError
from boto.s3.key import Key

from boardhood.models.base import BaseModel, ValidationException, UniqueViolationException
from boardhood.sql.users import *
from boardhood.helpers.image import resize, get_extension

class User(BaseModel):
    _public_fields = ['name', 'avatar_url', 'conversations_count', 'interests_count']

    STATUS_ACTIVE  = 1
    STATUS_PENDING = 2
    STATUS_BLOCKED = 3
    STATUS_REMOVED = 4

    BUCKET_NAME = 'boardhood_profile_images'
    ALLOWED_IMAGES = set(['png', 'jpg', 'jpeg', 'gif'])

    id = IntField()
    name = StringField(max_length=45)
    email = EmailField()
    password = StringField()
    status = IntField()
    created_at = DateTimeField()
    updated_at = DateTimeField()
    avatar_url = URLField()

    conversations_count = IntField()
    interests_count = IntField()

    ip = StringField()
    timestamp = DateTimeField()

    def encrypt_password(self):
        self.password = bcrypt.hashpw(self.password, bcrypt.gensalt())

    def to_self_dict(self):
        data = self.to_dict()
        data.update({'email': self.email})
        return data

    @staticmethod
    def parse(row):
        if not row:
            return None
        return User(**row)

    @staticmethod
    def parse_all(rows):
        records = []
        for row in rows:
            records.append(User.parse(row))
        return records

    @staticmethod
    def create(user):
        try:
            cursor = User.cursor()

            user.encrypt_password()
            user.status = User.STATUS_ACTIVE
            user.validate()

            cursor.execute(CREATE, user.to_sqldict())
            User.db.commit()
            user.id = cursor.fetchone()['id']
            return user
        except ShieldException, e:
            raise ValidationException(e)
        except DatabaseError, e:
            if e.pgcode == '23505':
                raise UniqueViolationException(e)
            else:
                User.report_error(e, cursor)
                return False
        except Exception, e:
            User.report_error(e, cursor)
            return False

    @staticmethod
    def exists(id):
        try:
            cursor = User.cursor()
            cursor.execute(EXISTS, [id])
            return cursor.fetchone() is not None
        except Exception, e:
            User.report_error(e, cursor)
            return False

    @staticmethod
    def authenticate(user):
        try:
            cursor = User.cursor()
            cursor.execute(AUTHENTICATE, user.to_sqldict())
            data = cursor.fetchone()
            if data is None or bcrypt.hashpw(user.password, data['password']) != data['password']:
                return False
            else:
                return user.update_values(data)
        except Exception, e:
            User.report_error(e, cursor)
            return False

    @staticmethod
    def findByName(name):
        try:
            cursor = User.cursor()
            cursor.execute(FIND_BY_NAME, [name])
            return User.parse(cursor.fetchone())
        except Exception, e:
            User.report_error(e, cursor)
            return None
            
    @staticmethod
    def findById(id):
        try:
            cursor = User.cursor()
            cursor.execute(FIND_BY_ID, [id])
            return User.parse(cursor.fetchone())
        except Exception, e:
            User.report_error(e, cursor)
            return None

    @staticmethod
    def nameExists(name):
        try:
            cursor = User.cursor()
            cursor.execute(NAME_EXISTS, [name])
            return cursor.fetchone() is not None
        except Exception, e:
            User.report_error(e, cursor)
            return False

    @staticmethod
    def emailExists(email):
        try:
            cursor = User.cursor()
            cursor.execute(EMAIL_EXISTS, [email])
            return cursor.fetchone() is not None
        except Exception, e:
            User.report_error(e, cursor)
            return False

    @staticmethod
    def updateAvatarUrl(id, avatar_url):
        try:
            cursor = User.cursor()
            cursor.execute(UPDATE_AVATAR, [avatar_url, id])
            User.db.commit()
            return True
        except Exception, e:
            User.report_error(e, cursor)
            return False

    def saveAvatar(self, file):
        avatar = create_avatar(file)

        if avatar:
            content_type = file.mimetype
            ext = get_extension(file.filename)
            name = sha1(self.name + str(datetime.utcnow())).hexdigest()
            filename = '%s.%s' % (name, ext)

            if not content_type:
                content_type = 'text/plain'

            bucket = User.s3.get_bucket(User.BUCKET_NAME)
            key = Key(bucket, filename)
            res = key.set_contents_from_file(avatar, policy='public-read')

            if res:
                self.avatar_url = User.S3_URL % (User.BUCKET_NAME, filename)
                User.updateAvatarUrl(self.id, self.avatar_url)
                return True
            else:
                return False
        else:
            return False

    def update(self):
        try:
            if len(self.password) != 60:
                self.encrypt_password()
            self.validate()

            cursor = User.cursor()
            cursor.execute(UPDATE, self.to_sqldict())
            User.db.commit()
            return True
        except ShieldException, e:
            raise ValidationException(e)
        except DatabaseError, e:
            if e.pgcode == '23505': # already exists
                raise UniqueViolationException(e)
            else:
                User.report_error(e, cursor)
                return False
        except Exception, e:
            User.report_error(e, cursor)
            return False


def create_avatar(file):
    try:
        img = resize(file)
        if img is False:
            return False

        tmp = tempfile.NamedTemporaryFile()

        ext = get_extension(file.filename)
        if ext == 'jpg' or ext == 'jpeg':
            img.save(tmp, "JPEG", quality=100)
        elif ext == 'png':
            img.save(tmp, "PNG", quality=100)
        elif ext == 'gif':
            img.save(tmp, "GIF", quality=100)

        tmp.seek(0)
        return tmp
    except IOError, e:
        User.report_error(e)
        return False
