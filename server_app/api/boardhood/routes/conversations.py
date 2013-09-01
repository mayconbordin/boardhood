from flask import Blueprint, request, url_for, current_app as app
from boardhood.helpers.response import json
from boardhood.helpers.pagination import paginate
from boardhood.helpers.auth import require_user_auth
from boardhood.helpers.date import now
from boardhood.models.interests import Interest
from boardhood.models.conversations import Conversation
from boardhood.forms import (ConversationIndexForm    , ConversationShowForm,
                             ConversationCreateForm   , ConversationCreateReplyForm,
                             ConversationShowReplyForm, ConversationListForm,
                             ConversationRepliesForm)

module = Blueprint('conversations', __name__)
Conversation.register_app(module, app)


# GET /conversations/feed  -----------------------------------------------------
@module.route('/conversations/feed')
@require_user_auth(app)
def conversations_feed():
    form = ConversationListForm(request)
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    user = app.auth.user

    total = Conversation.countAll(order_by=form.order, location=form.location,
                                  radius=form.radius, user_id=user.id, after=form.after)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Conversation.findAll(order_by=form.order, location=form.location,
                                        radius=form.radius, offset=pg.offset,
                                        limit=pg.per_page, user_id=user.id, after=form.after)
        else:
            return json({'message': "Invalid interest or end of page list"}, 404)
    else:
        data = []

    return json(dict(conversations=data, **pg.info))


# GET /conversations  ----------------------------------------------------------
@module.route('/conversations')
@require_user_auth(app)
def conversations():
    form = ConversationListForm(request)
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    total = Conversation.countAll(order_by=form.order, location=form.location,
                                  radius=form.radius, after=form.after)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Conversation.findAll(order_by=form.order, location=form.location,
                                        radius=form.radius, offset=pg.offset,
                                        limit=pg.per_page, after=form.after)
        else:
            return json({'message': "Invalid interest or end of page list"}, 404)
    else:
        data = []

    return json(dict(conversations=data, **pg.info))


# GET /interests/:interest_id/conversations  -----------------------------------
@module.route('/interests/<interest_id>/conversations')
@require_user_auth(app)
def index(interest_id):
    form = ConversationIndexForm(request, url={'interest_id': interest_id})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    total = Conversation.countAll(form.interest_id, form.order, form.location,
                                  form.radius, None, form.after)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Conversation.findAll(form.interest_id, form.order, form.location,
                                        form.radius, pg.offset, pg.per_page, None, form.after)
        else:
            return json({'message': "Invalid interest or end of page list"}, 404)
    else:
        data = []

    return json(dict(conversations=data, **pg.info))


# POST /interests/:interest_id/conversations  ----------------------------------
@module.route('/interests/<interest_id>/conversations', methods=['POST'])
@require_user_auth(app)
def create(interest_id):
    form = ConversationCreateForm(request, url={'interest_id': interest_id})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    data = Conversation.create(Conversation.parse(form.data, user_id=app.auth.user.id))
    if data is False:
        return json({'message': 'An error ocurred while creating the conversation'}, 400)

    return json(data, 201)


# GET /interests/:id/conversations/:id  ----------------------------------------
@module.route('/interests/<interest_id>/conversations/<id>')
@require_user_auth(app)
def show(interest_id, id):
    form = ConversationShowForm(url={'interest_id':interest_id, 'id':id})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    data = Conversation.find(form.interest_id, form.id)
    if data is not False:
        return json(data)
    else:
        return json({'message': 'Conversation with given ID not found'}, 404)


# GET /interests/:id/conversations/:id/replies  --------------------------------
@module.route('/interests/<interest_id>/conversations/<id>/replies')
@require_user_auth(app)
def list_replies(interest_id, id):
    form = ConversationRepliesForm(request, url={'interest_id':interest_id, 'id':id})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    total = Conversation.countReplies(form.interest_id, form.id, form.after)
    pg = paginate(form, total)

    if total > 0:
        if pg.page_valid:
            data = Conversation.replies(form.interest_id, form.id, form.after,
                                        pg.offset, pg.per_page)
        else:
            return json({'message': "Invalid interest or end of page list"}, 404)
    else:
        data = []

    return json(dict(replies=data, **pg.info))

# POST /interests/:id/conversations/:id/replies  -------------------------------
@module.route('/interests/<interest_id>/conversations/<id>/replies', methods=['POST'])
@require_user_auth(app)
def create_reply(interest_id, id):
    form = ConversationCreateReplyForm(request, url={'interest_id':interest_id, 'parent_id':id})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    data = Conversation.create(Conversation.parse(form.data, user_id=app.auth.user.id))
    if data is False:
        return json({'message': 'An error ocurred while creating the reply'}, 400)

    return json(data, 201)


# GET /interests/:id/conversations/:id/replies/:id  ----------------------------
@module.route('/interests/<interest_id>/conversations/<parent_id>/replies/<id>')
@require_user_auth(app)
def show_reply(interest_id, parent_id, id):
    form = ConversationShowReplyForm(url={'interest_id': interest_id, 'parent_id': parent_id, 'id': id})
    if not form.validate():
        return json({'message': 'Validation Failed', 'errors': form.errors}, 422)

    data = Conversation.findReply(form.interest_id, form.parent_id, form.id)
    if data is False:
        return json({'message': 'Conversation with given ID not found'}, 404)

    return json(data)
