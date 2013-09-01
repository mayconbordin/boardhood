import bcrypt

from hashlib import sha256
from dictshield.base import ShieldException
from dictshield.fields import BaseField, JsonHashMixin, IntField

from boardhood.helpers.opaque import OpaqueEncoder

class IDField(IntField):
    def __init__(self, key, *args, **kwargs):
        self.encoder = OpaqueEncoder(key)
        super(IDField, self).__init__(*args, **kwargs)

    def __set__(self, instance, value):
        if isinstance(value, (basestring, str)):
            instance._data['ext_id'] = value
            value = self.encoder.decode_hex(value)
        elif isinstance(value, (int, long)):
            instance._data['ext_id'] = self.encoder.encode_hex(value)

        instance._data[self.field_name] = value

class OpaqueIdField(BaseField):
    def __init__(self, key, auto_fill=False, **kwargs):
        self.auto_fill = auto_fill
        self.encoder = OpaqueEncoder(key)

        super(OpaqueIdField, self).__init__(**kwargs)

    def __set__(self, instance, value):
        if isinstance(value, (int, long)):
            instance._data['id'] = value
            value = self.encoder.encode_hex(value)
        elif isinstance(value, (basestring, str)):
            instance._data['id'] = self.encoder.decode_hex(value)

        instance._data[self.field_name] = value

    def _jsonschema_type(self):
        return 'string'

    def for_python(self, value):
        return str(value)

    def for_json(self, value):
        return str(value)

    def validate(self, value):
        if not isinstance(value, (basestring, str)):
            try:
                value = self.encoder.encode_hex(value)
            except Exception, e:
                raise ShieldException('Invalid IntField', self.field_name, value)
        return value

class SHA256Field(BaseField, JsonHashMixin):
    """A field that validates input as resembling an MD5 hash.
    """
    hash_length = 64

    def __set__(self, instance, value):
        if value is not None and len(value) != SHA256Field.hash_length:
            value = sha256(value).hexdigest()

        try:
            int(value, 16)
        except:
            if value is not None:
                value = sha256(value).hexdigest()

        instance._data[self.field_name] = value

    def validate(self, value):
        if len(value) != SHA256Field.hash_length:
            raise ShieldException('SHA256 value is wrong length', self.field_name,
                                  value)
        try:
            int(value, 16)
        except:
            raise ShieldException('SHA256 value is not hex', self.field_name,
                                  value)
        return value

class BCryptField(BaseField, JsonHashMixin):
    hash_length = 60

    def __set__(self, instance, value):
        if value is not None and len(value) != BCryptField.hash_length:
            value = bcrypt.hashpw(value, bcrypt.gensalt())

        instance._data[self.field_name] = value

    def validate(self, value):
        if len(value) != BCryptField.hash_length:
            raise ShieldException('BCrypt value is wrong length', self.field_name, value)
        return value
