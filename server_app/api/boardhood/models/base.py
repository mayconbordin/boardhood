from psycopg2.extras import RealDictCursor

from dictshield.base import ShieldException
from dictshield.document import EmbeddedDocument
from dictshield.fields import IntField

from boardhood.helpers.opaque import OpaqueEncoder

class LoggingCursor(RealDictCursor):
    def execute(self, sql, args=None):
        BaseModel.logger.info(self.mogrify(sql, args))

        try:
            RealDictCursor.execute(self, sql, args)
        except Exception, e:
            BaseModel.logger.error("%s: %s" % (e.__class__.__name__, e))
            raise

class BaseModel(EmbeddedDocument):
    app = None
    module = None

    db = None
    logger = None
    s3 = None
    _key = None

    S3_URL = "https://s3.amazonaws.com/%s/%s"

    class Meta:
        id_field = IntField

    def to_sqldict(self):
        data = self.to_python()
        ndata = self.to_python()

        for attr in data:
            if isinstance(data[attr], dict):
                for attr2 in data[attr]:
                    ndata[attr + '_' + attr2] = data[attr][attr2]

        for field_name, field in self._fields.items():
            if field_name not in ndata:
                ndata[field_name] = None

        return ndata

    def to_dict(self):
        data = self.make_publicsafe(self)

        for attr in data:
            if isinstance(self._data[attr], BaseModel):
                data[attr] = self._data[attr].to_dict()

        if 'id' in self._data and 'ext_id' in self._data:
            self.ext_id = self.id
            data['id'] = self.ext_id

        return data

    def update_values(self, values):
        for attr_name, attr_value in values.items():
            setattr(self, attr_name, attr_value)
        return self

    @staticmethod
    def cursor():
        return BaseModel.db.cursor(cursor_factory=LoggingCursor)

    @staticmethod
    def report_error(e, cursor=None):
        message = ''

        if len(e.args) is 1:
            message = "Error: %s" % str(e.args[0])
        elif len(e.args) is 2:
            message = "Error %s: %s" % (str(e.args[0]), e.args[1])
        elif len(e.args) is 3:
            message = "Error %s: %s.\nDetails: %s" % (str(e.args[0]), e.args[1], e.args[2])

        if 'pgcode' in dir(e):
            message += "\nPostgreSQL Error: %s" % str(e.pgcode)
        if 'pgerror' in dir(e):
            message += "\n%s" % str(e.pgerror)

        if cursor:
            message += "\nSQL: %s" % str(cursor.query)

        if BaseModel.logger is not None:
            BaseModel.logger.error(message)

    @staticmethod
    def set_config(app):
        BaseModel.db = BaseModel.app.db.get_db()
        BaseModel.logger = BaseModel.app.logger
        BaseModel.s3 = BaseModel.app.s3

    @staticmethod
    def register_app(module, app=None):
        if app is None:
            app = module

        BaseModel.module = module
        BaseModel.app = app
        module.before_request(BaseModel.before_request)
        module.after_request(BaseModel.after_request)

    @staticmethod
    def before_request():
        BaseModel.db = BaseModel.app.db.get_db()
        BaseModel.logger = BaseModel.app.logger
        BaseModel.s3 = BaseModel.app.s3

    @staticmethod
    def after_request(request):
        return request

class ValidationException(ShieldException):
    def __init__(self, e):
        super(ValidationException, self).__init__(e.reason, e.field_name, e.field_value)

class UniqueViolationException(Exception):
    pass
