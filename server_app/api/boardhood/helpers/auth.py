from datetime import datetime
from hashlib import sha1
from functools import wraps
from flask import request

from boardhood.helpers.response import json
from boardhood.models.users import User

# 1. user tries to request some data
# 2. provider asks for authentication
# 3. user sends username:password to provider
# 4. provider check if the credentials are valid
# 5. If valid, generate a toke (hash), store in memcached with a timeout
#    with the token as key and as value the username, user_id, ip addres and time of log in
# 6. User receive a token in the response body: {"token": ...}
# 7. User uses it in the url: api.example.com/resource?auth_token=...

class Auth:
    salt = 'f!nG%r+.Nvw[9I;WQd)FjCj2IC1UF3zsFgW~1-Vw9a/L1?h64]c4Xk6J(@chZkIL'
    timeout = 3600 * 1000

    def __init__(self, app=None):
        if app is not None:
            self.app = app
            self.init_app(app)
        else:
            self.app = None

    def init_app(self, app):
        self.app = app
        self.store = app.cache

        User.register_app(app)

        #app.before_request(self.before_request)

    def before_request(self):
        auth  = request.authorization
        token = request.args.get('auth_token')

        if token:
            if not self.check_token(token):
                return self.authenticate("Invalid token")
        elif auth:
            if not self.check_auth(auth):
                return self.authenticate()
            else:
                return json({"token": self.create_token(auth)}, 200)
        else:
            return self.authenticate()

    def authenticate(self, message="Bad credentials"):
        return json({"message":message}, 401, {'WWW-Authenticate': 'Basic realm="Login Required"'})

    def check_auth(self, auth):
        user = User(name=auth.username, password=auth.password, ip=request.remote_addr, timestamp=datetime.utcnow())
        self.user = User.authenticate(user)
        return self.user is not False

    def create_token(self, credentials):
        token = AuthToken(credentials, Auth.salt, self.user.timestamp).generate()
        self.store.set(token, self.user)
        return token

    def check_token(self, token):
        self.user = self.store.get(str(token))
        if not self.user or self.user.ip != request.remote_addr:
            return False
        return True


class AuthToken:
    def __init__(self, credentials, salt, timestamp=None):
        self.credentials = credentials
        self.salt = salt
        self.timestamp = timestamp or datetime.utcnow()

    def generate(self):
        hash = sha1("%s:%s:%s:%s" % (self.credentials.username, self.credentials.password, self.timestamp, self.salt))
        return hash.hexdigest()

def require_user_auth(app):
    def wrapped(f):
        @wraps(f)
        def decorated(*args, **kwargs):
            response = app.auth.before_request()
            if response is not None:
                return response
            return f(*args, **kwargs)
        return decorated
    return wrapped
