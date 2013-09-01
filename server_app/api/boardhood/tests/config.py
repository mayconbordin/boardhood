import sys, os
sys.path.append('.')

from boardhood import create_app

app = create_app('testing')
ctx = app.test_request_context()
ctx.push()
app.preprocess_request()

server = 'http://localhost:5000'
