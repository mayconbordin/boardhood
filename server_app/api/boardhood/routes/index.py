from flask import Blueprint, current_app as app, request
from boardhood.helpers.response import json
from boardhood.helpers.auth import require_user_auth

module = Blueprint('index', __name__)


# GET /
@module.route('/')
@require_user_auth(app)
def main():
    return json({'url': '/'})

# GET /authenticate
@module.route('/authenticate')
@require_user_auth(app)
def authenticate():
    return json({'message': 'You are already authenticated.'})
