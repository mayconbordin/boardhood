from flask import Blueprint, request, current_app as app

from boardhood.helpers.response import json
from boardhood.helpers.pagination import paginate
from boardhood.helpers.auth import require_user_auth
from boardhood.models.interests import Interest
from boardhood.forms import (InterestIndexForm, InterestCreateForm,
                             InterestIdForm, InterestSearchForm,
                             InterestFollowersForm)

module = Blueprint('interests', __name__)
Interest.register_app(module, app)


# GET /interests  --------------------------------------------------------------
@module.route('/interests')
@require_user_auth(app)
def index():
    form = InterestIndexForm(request)
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    total = Interest.countAll(form.location, form.radius)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Interest.findAll(form.order, form.location, form.radius, pg.offset,
                                    pg.per_page, app.auth.user.id)
        else:
            return json({'message': "This is the end of the list"}, 404)
    else:
        data = []

    return json(dict(interests=data, **pg.info))


# POST /interests  -------------------------------------------------------------
@module.route('/interests', methods=['POST'])
@require_user_auth(app)
def create():
    form = InterestCreateForm(request)
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    data = Interest.create(Interest(name=form.name))
    if data.id == 0:
        return json({'message': 'Interest already exists'}, 409)

    return json(data, 201)


# GET /interests/search/:query  ------------------------------------------------
@module.route('/interests/search/<query>')
@require_user_auth(app)
def search(query):
    form = InterestSearchForm(url={'query': query})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    data = Interest.search(form.query, user_id=app.auth.user.id)
    return json({'interests': data})


# GET /interests/:id  ----------------------------------------------------------
@module.route('/interests/<id>')
@require_user_auth(app)
def show(id):
    form = InterestIdForm(url={'id': id})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    data = Interest.find(form.id, app.auth.user.id)
    if data is False:
        return json({'message': 'Interest with given ID not found'}, 404)

    return json(data)


# GET /interests/:id/followers  ------------------------------------------------
@module.route('/interests/<id>/followers')
@require_user_auth(app)
def followers(id):
    form = InterestFollowersForm(request, url={'id': id})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    total = Interest.countFollowers(form.id)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Interest.followers(form.id, pg.offset, pg.per_page)
        else:
            return json({'message': "This is the end of the list"}, 404)
    else:
        data = []

    return json(dict(followers=data, **pg.info))


# GET    /interests/:id/followers/me  ------------------------------------------
# POST   /interests/:id/followers/me
# DELETE /interests/:id/followers/me
@module.route('/interests/<id>/followers/me', methods=['GET', 'POST', 'DELETE'])
@require_user_auth(app)
def following(id):
    form = InterestIdForm(url={'id': id})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    user_id = app.auth.user.id

    if request.method == 'GET':
        if Interest.following(form.id, user_id):
            return json(code=204)
        else:
            return json(code=404)

    elif request.method == 'POST':
        if Interest.follow(form.id, user_id):
            return json(code=201)
        else:
            return json({'message': 'Interest with given ID not found'}, 400)

    elif request.method == 'DELETE':
        if Interest.unfollow(form.id, user_id):
            return json(code=204)
        else:
            return json({'message': 'Interest with given ID not found'}, 400)
