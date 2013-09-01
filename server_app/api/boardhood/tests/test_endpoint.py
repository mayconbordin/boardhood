import unittest
import urllib

from urlparse import urlunparse
from restkit import Resource, request, BasicAuth
from restkit.errors import Unauthorized
from config import server

try:
    from json import loads
except ImportError:
    from simplejson import loads

try:
    from urlparse import parse_qsl
except ImportError:
    from cgi import parse_qsl

class AuthToken(object):
    def __init__(self, token):
        self.token = token

    def on_request(self, request):
        parsed_url = request.parsed_url
        params = {'auth_token': self.token}
        params.update(parse_qsl(parsed_url.query))
        request.url = urlunparse((parsed_url.scheme, parsed_url.netloc, parsed_url.path,
                                                                                                          parsed_url.params, urllib.urlencode(params, True),
                                                                                                          parsed_url.fragment))

class TestEndpoint(unittest.TestCase):
    def setUp(self):
        auth = BasicAuth('john', 'teste')
        res = Resource(server, filters=[auth, ])
        r = res.get('/authenticate')
        data = loads(r.body_string())

        self.auth = AuthToken(data.get('token'))
        self.res = Resource(server, filters=[self.auth, ])

    def testAuthentication(self):
        r = self.res.get('/')
        self.assertEqual(200, r.status_int)

        try:
            res = Resource(server)
            r = res.get('/')
            self.assertTrue(False)
        except Unauthorized, e:
            self.assertTrue(True)

if __name__ == '__main__':
    unittest.main()
