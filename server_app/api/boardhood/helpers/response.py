from flask import Response, jsonify, request
from werkzeug.exceptions import default_exceptions
from werkzeug.exceptions import HTTPException

from boardhood.models.base import BaseModel

try:
    from json import dumps
except ImportError:
    from simplejson import dumps


def handler(obj):
    if hasattr(obj, 'isoformat'):
        # ISO 8601 - without microseconds
        return obj.strftime("%Y-%m-%dT%H:%M:%SZ")
    elif isinstance(obj, BaseModel):
        return obj.to_dict()
    else:
        raise TypeError, 'Object of type %s with value of %s is not JSON serializable' % (type(obj), repr(obj))

def json(response='', code=200, headers=None):
    data = dumps(response, default=handler, sort_keys=True, separators=(',', ':'))
    return Response(data, code, mimetype='application/json', headers=headers)

def make_json_error(ex):
    response = jsonify(message=str(ex))
    response.status_code = (ex.code if isinstance(ex, HTTPException) else 500)
    return response

def json_error_handler(app):
    for code in default_exceptions.iterkeys():
        app.error_handler_spec[None][code] = make_json_error
