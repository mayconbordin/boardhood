from flask import Blueprint, current_app as app, request

from boardhood.helpers.response import json
from boardhood.helpers.image import resize
from boardhood.helpers.pagination import paginate
from boardhood.helpers.auth import require_user_auth
from boardhood.models.applications import Application, require_app_auth
from boardhood.models.users import User
from boardhood.models.interests import Interest
from boardhood.models.conversations import Conversation
from boardhood.models.base import ValidationException, UniqueViolationException
from boardhood.forms import (UserCreateForm, UserUpdateForm, UserListForm,
                             UserNameListForm, UserShowForm, InterestSearchForm,
                             UserActivityForm)

module = Blueprint('users', __name__)
User.register_app(module, app)
Interest.register_app(module, app)
Conversation.register_app(module, app)


# POST /users  -----------------------------------------------------------------
@module.route('/users', methods=['POST'])
@require_app_auth(app, Application.LEVEL_ALL)
def create():
    form = UserCreateForm(request)
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    try:
        user = User.create(User(**form.data))
        if user:
            if form.avatar:
                user.saveAvatar(form.avatar)
            return json(user, 201)
        else:
            return json({'message': 'An error ocurred while creating the user'}, 400)
    except ValidationException, e:
        return json({'message': 'Validation Failed',
                     'errors': [{'reason': e.reason, 'field': e.field_name}]}, 422)
    except UniqueViolationException, e:
        return json({'message': 'User name or email already exists'}, 409)


# GET  /users/:username  -------------------------------------------------------
@module.route('/users/<name>')
@require_user_auth(app)
def show_user(name):
    form = UserShowForm(url={'name': name})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    data = User.findByName(form.name)
    if data is not False:
        return json(data)
    else:
        return json({'message': 'User with given name not found'}, 404)


# GET  /users/:username/interests  ---------------------------------------------
@module.route('/users/<name>/interests')
@require_user_auth(app)
def show_user_interests(name):
    form = UserNameListForm(request, url={'name': name})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    if not User.nameExists(form.name):
        return json({'message': 'User with given name not found'}, 404)

    total = Interest.countAllByUserName(form.name)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Interest.findAllByUserName(form.name, pg.offset, pg.per_page)
        else:
            return json({'message': "This is the end of the list"}, 404)
    else:
        data = []

    return json(dict(interests=data, **pg.info))


# GET  /users/:username/conversations  -----------------------------------------
@module.route('/users/<name>/conversations')
@require_user_auth(app)
def show_user_conversations(name):
    form = UserNameListForm(request, url={'name': name})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    if not User.nameExists(form.name):
        return json({'message': 'User with given name not found'}, 404)

    total = Conversation.countAllByUserName(form.name)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Conversation.findAllByUserName(form.name, pg.offset, pg.per_page)
        else:
            return json({'message': "This is the end of the list"}, 404)
    else:
        data = []

    return json(dict(conversations=data, **pg.info))


# Authenticated user ===========================================================

# PUT /user  -------------------------------------------------------------------
@module.route('/user', methods=['PUT'])
@require_user_auth(app)
def update():
    form = UserUpdateForm(request)
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    user = app.auth.user

    if user.name != form.name and User.nameExists(form.name):
        return json({'message': 'Validation Failed', 'errors': [
                {'code': 'already_exists', 'field': 'name'}]}, 422)

    if user.email != form.email and User.emailExists(form.email):
        return json({'message': 'Validation Failed', 'errors': [
                {'code': 'already_exists', 'field': 'email'}]}, 422)

    user.update_values(form.data)

    try:
        if form.avatar:
            user.saveAvatar(form.avatar)

        if user.update():
            app.auth.user = user
            return json(user, 200)
        else:
            return json({'message': 'An error ocurred while creating the user'}, 400)
    except ValidationException, e:
        return json({'message': 'Validation Failed', 'errors': [
                                                        {'reason': e.reason, 'field': e.field_name}]}, 422)
    except UniqueViolationException, e:
        return json({'message': 'User name or email already exists'}, 409)


# GET /user  -------------------------------------------------------------------
@module.route('/user')
@require_user_auth(app)
def show():
    user = User.findById(app.auth.user.id)
    	
    if user is not None:
        return json(user.to_self_dict())
    else:
        return json({'message': 'There is no authenticated user'}, 404)


# GET  /user/interests  --------------------------------------------------------
@module.route('/user/interests')
@require_user_auth(app)
def show_interests():
    form = UserListForm(request)
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    user = app.auth.user
    total = Interest.countAllByUser(user.id)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Interest.findAllByUser(user.id, pg.offset, pg.per_page)
        else:
            return json({'message': "This is the end of the list"}, 404)
    else:
        data = []

    return json(dict(interests=data, **pg.info))


# GET /user/interests/search/:query  -------------------------------------------
@module.route('/user/interests/search/<query>')
@require_user_auth(app)
def search(query):
    form = InterestSearchForm(url={'query': query})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    data = Interest.searchByUser(form.query, user_id=app.auth.user.id)
    return json({'interests': data})


# GET  /user/conversations  ----------------------------------------------------
@module.route('/user/conversations')
@require_user_auth(app)
def show_conversations():
    form = UserListForm(request)
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    user = app.auth.user
    total = Conversation.countAllByUser(user.id)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Conversation.findAllByUser(user.id, pg.offset, pg.per_page)
        else:
            return json({'message': "This is the end of the list"}, 404)
    else:
        data = []

    return json(dict(conversations=data, **pg.info))


# GET  /user/activity  ---------------------------------------------------------
@module.route('/user/activity')
@require_user_auth(app)
def show_conversations():
    form = UserActivityForm(request)
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    user_id = app.auth.user.id
    total = Conversation.countAllBySubscription(user_id, form.after)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Conversation.findAllBySubscription(user_id, pg.offset, pg.per_page, form.after)
        else:
            return json({'message': "This is the end of the list"}, 404)
    else:
        data = []

    return json(dict(conversations=data, **pg.info))
