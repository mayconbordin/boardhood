from boardhood.helpers.forms import (Form, StringField, IntField, FloatField,
                                     HexField, EmailField, FileField, DateField)
from boardhood.models.conversations import Conversation
from boardhood.models.interests import Interest

def conversation_opaque_to_id(value):
    return Conversation(id=value).id

def interest_opaque_to_id(value):
    return Interest(id=value).id


# Conversations ================================================================
class ConversationIndexForm(Form):
    interest_id = HexField(source='url', filter=interest_opaque_to_id)
    order = StringField(allowed=['recent', 'popular', 'distance'], default='popular')
    radius = IntField(required=False, min=1, max=100000)
    page = IntField(default=1, min=1)
    per_page = IntField(default=10, min=1, max=50)
    lat = FloatField(required=False, needs='lon')
    lon = FloatField(required=False, needs='lat')
    after = DateField(required=False, format='%Y-%m-%dT%H:%M:%S')

    @property
    def location(self):
        if self.lat and self.lon:
            return [self.lat, self.lon]
        else:
            return None

class ConversationListForm(ConversationIndexForm):
    interest_id = None

class ConversationCreateForm(Form):
    interest_id = HexField(source='url', filter=interest_opaque_to_id)
    message = StringField(minlength=1, maxlength=2000, source='form')
    lat = FloatField(required=False, source='form')
    lon = FloatField(required=False, source='form')

    @property
    def location(self):
        if self.lat and self.lon:
            return [self.lat, self.lon]
        else:
            return None

class ConversationCreateReplyForm(ConversationCreateForm):
    parent_id = HexField(source='url', filter=conversation_opaque_to_id)

class ConversationShowForm(Form):
    interest_id = HexField(source='url', filter=interest_opaque_to_id)
    id = HexField(source='url', filter=conversation_opaque_to_id)

class ConversationRepliesForm(ConversationShowForm):
    after = DateField(required=False, format='%Y-%m-%dT%H:%M:%S')
    page = IntField(default=1, min=1)
    per_page = IntField(default=20, min=1, max=50)

class ConversationShowReplyForm(Form):
    interest_id = HexField(source='url', filter=interest_opaque_to_id)
    parent_id = HexField(source='url', filter=conversation_opaque_to_id)
    id = HexField(source='url', filter=conversation_opaque_to_id)


# Users ========================================================================
class UserCreateForm(Form):
    name = StringField(regex=['^\w+$', 'accepts only alphanumerics'], minlength=1, maxlength=80, source='form')
    email = EmailField(minlength=1, maxlength=120, source='form')
    password = StringField(source='form', minlength=3)
    avatar = FileField(required=False, extensions=['png', 'jpg', 'jpeg', 'gif'])

class UserUpdateForm(Form):
    name = StringField(required=False, minlength=1, maxlength=80, source='form')
    email = EmailField(required=False, minlength=1, maxlength=120, source='form')
    password = StringField(required=False, source='form')
    avatar = FileField(required=False, extensions=['png', 'jpg', 'jpeg', 'gif'])

class UserListForm(Form):
    page = IntField(default=1, min=1)
    per_page = IntField(default=10, min=1, max=50)

class UserActivityForm(UserListForm):
    after = DateField(required=False, format='%Y-%m-%dT%H:%M:%S')

class UserShowForm(Form):
    name = StringField(regex=['^\w+$', 'accepts only alphanumerics'], minlength=1, maxlength=80, source='url')

class UserNameListForm(UserListForm, UserShowForm):
    pass


# Interests ====================================================================
class InterestIndexForm(Form):
    order = StringField(allowed=['recent', 'popular', 'distance'], default='popular')
    radius = IntField(required=False, min=1, max=100000)
    page = IntField(default=1, min=1)
    per_page = IntField(default=10, min=1, max=50)
    lat = FloatField(required=False, needs='lon')
    lon = FloatField(required=False, needs='lat')

    @property
    def location(self):
        if self.lat and self.lon:
            return [self.lat, self.lon]
        else:
            return None

class InterestCreateForm(Form):
    name = StringField(maxlength=45, source='form')

class InterestSearchForm(Form):
    query = StringField(minlength=1, maxlength=45, source='url')

class InterestIdForm(Form):
    id = HexField(length=8, source='url', filter=interest_opaque_to_id)

class InterestFollowersForm(Form):
    id = HexField(length=8, source='url', filter=interest_opaque_to_id)
    page = IntField(default=1, min=1)
    per_page = IntField(default=10, min=1, max=50)
