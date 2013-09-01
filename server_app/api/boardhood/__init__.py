import os

from flask import Flask, jsonify, request
from flaskext.cache import Cache
from boto.s3.connection import S3Connection

from boardhood.helpers.response import json_error_handler
from boardhood.helpers.auth import Auth
from boardhood.helpers.postgresql import PostgreSQL
from boardhood.helpers.response import json

MODULES = ['index', 'users', 'interests', 'conversations']
CONFIG_FILES = {'development': 'config/development.cfg',
                'test'       : 'config/test.cfg',
                'production' : 'config/production.cfg' }

def create_app(env=None):
    app = Flask(__name__)

    load_config(app, env)
    configure_extensions(app)
    json_error_handler(app)
    register_modules(app)
    register_filters(app)

    return app

def load_config(app, env):
    app.config.from_object(__name__)
    app.config.from_pyfile('config/default.cfg')
    print '[CONFIG] Loading default configuration file'

    var = "BOARDHOOD_ENV"
    if env is None and var in os.environ:
        env = os.environ[var]

    if env in CONFIG_FILES:
        app.config.from_pyfile(CONFIG_FILES[env])
        print '[CONFIG] Loading configuration file from "%s" environment' % env

def configure_extensions(app):
    print '  [LOAD] PostgreSQL extension'
    app.db = PostgreSQL(app)

    print '  [LOAD] Cache extension'
    app.cache = Cache(app)

    print '  [LOAD] Authentication extension'
    app.auth = Auth(app)

    print '  [LOAD] Amazon S3 extension'
    app.s3 = S3Connection(app.config['AWS_ACCESS_KEY_ID'], app.config['AWS_SECRET_ACCESS_KEY'])

def register_modules(app):
    for name in MODULES:
        module = __import__('boardhood.routes.'+name, fromlist=['module'])
        print '[MODULE] Registering module %s' % name
        app.register_blueprint(getattr(module, 'module'))

def register_filters(app):
    pass
