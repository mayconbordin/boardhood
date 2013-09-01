import os
from application import create_app

os.environ['BOARDHOOD_ENV'] = 'testing'
app = create_app()
ctx = app.test_request_context()
ctx.push()
app.preprocess_request()
