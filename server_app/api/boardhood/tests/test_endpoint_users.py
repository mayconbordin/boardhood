import unittest
import random
import MultipartPostHandler, urllib2, cookielib
from test_endpoint import TestEndpoint, loads
from restkit.errors import ResourceNotFound

class TestUserEndpoint(TestEndpoint):
    def setUp(self):
        TestEndpoint.setUp(self)

        alpha = 'abcdefghijklmnopqrstuvxwyz1234567890'
        self.name = 'teste' + random.choice(alpha).join(random.sample(list(alpha), 5))

    def testCreate(self):
        cookies = cookielib.CookieJar()
        opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cookies),
                          MultipartPostHandler.MultipartPostHandler)

        data = dict(name=self.name, email=self.name+'@teste.com', password='teste',
                                                        avatar=open("application/resources/tests/sheldon.jpg", "rb"))
        r = opener.open("http://localhost:5000/users?api_key=9c7dc77314ca22b8eec94440fa528157f8b8be03", data)
        self.assertEqual(201, r.code)

    def testUpdate(self):
        data = dict(email=self.name+'@testeteste.com', password='teste')
        r = self.res.put('/user', data)
        self.assertEqual(200, r.status_int)

    def testShowAuthUser(self):
        r = self.res.get('/user')
        self.assertEqual(200, r.status_int)
        data = loads(r.body_string())

        self.assertTrue(data.get('name') is not None)
        self.assertTrue(data.get('email') is not None)

    def testShowUser(self):
        r = self.res.get('/users/john')
        self.assertEqual(200, r.status_int)
        data = loads(r.body_string())

        self.assertTrue(data.get('name') is not None)
        self.assertTrue(data.get('avatar_url') is not None)

    def testShowAuthUserInterests(self):
        next = 1

        while next:
            r = self.res.get('/user/interests', page=next)
            self.assertEqual(200, r.status_int)
            data = loads(r.body_string())

            if data.get('next_url'):
                next += 1
            else:
                next = None

            interests = data.get('interests')
            self.assertTrue(len(interests) > 0)

            for interest in interests:
                self.assertTrue(interest.get('id') is not None)
                self.assertTrue(interest.get('name') is not None)

    def testShowUserInterests(self):
        next = 1

        while next:
            r = self.res.get('/users/john/interests', page=next)
            self.assertEqual(200, r.status_int)
            data = loads(r.body_string())

            if data.get('next_url'):
                next += 1
            else:
                next = None

            interests = data.get('interests')
            self.assertTrue(len(interests) > 0)

            for interest in interests:
                self.assertTrue(interest.get('id') is not None)
                self.assertTrue(interest.get('name') is not None)

    def testShowAuthUserConversations(self):
        next = 1

        while next:
            r = self.res.get('/user/conversations', page=next)
            self.assertEqual(200, r.status_int)
            data = loads(r.body_string())

            if data.get('next_url'):
                next += 1
            else:
                next = None

            conversations = data.get('conversations')
            self.assertTrue(len(conversations) > 0)

            for conversation in conversations:
                self.assertTrue(conversation.get('id') is not None)
                self.assertTrue(conversation.get('message') is not None)

    def testShowUserConversations(self):
        next = 1

        while next:
            r = self.res.get('/users/john/conversations', page=next)
            self.assertEqual(200, r.status_int)
            data = loads(r.body_string())

            if data.get('next_url'):
                next += 1
            else:
                next = None

            conversations = data.get('conversations')
            self.assertTrue(len(conversations) > 0)

            for conversation in conversations:
                self.assertTrue(conversation.get('id') is not None)
                self.assertTrue(conversation.get('message') is not None)

    def testSearch(self):
        r = self.res.get('/user/interests/search/d')
        self.assertEqual(200, r.status_int)

        data = loads(r.body_string())
        interests = data.get('interests')
        self.assertTrue(len(interests) > 0)

        for interest in interests:
            self.assertTrue(interest.get('id') is not None)
            self.assertTrue(interest.get('name') is not None)

    def testAuthUserActivity(self):
        next = 1

        try:
            while next:
                r = self.res.get('/user/activity', page=next)
                self.assertEqual(200, r.status_int)

                next += 1

                data = loads(r.body_string())
                conversations = data.get('conversations')
                self.assertTrue(len(conversations) > 0)

                for conversation in conversations:
                    self.assertTrue(conversation.get('id') is not None)
                    self.assertTrue(conversation.get('message') is not None)
        except ResourceNotFound, e:
            pass

    def testAuthUserActivityAfter(self):
        next = 1

        try:
            while next:
                r = self.res.get('/user/activity', page=next, after='2007-03-04T21:00:00')
                self.assertEqual(200, r.status_int)

                next += 1

                data = loads(r.body_string())
                conversations = data.get('conversations')
                self.assertTrue(len(conversations) > 0)

                for conversation in conversations:
                    self.assertTrue(conversation.get('id') is not None)
                    self.assertTrue(conversation.get('message') is not None)
        except ResourceNotFound, e:
            pass
