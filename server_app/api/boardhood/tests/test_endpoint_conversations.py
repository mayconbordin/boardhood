import unittest
from test_endpoint import TestEndpoint, loads

class TestInterestsEndpoint(TestEndpoint):
    def testFeedList(self):
        next = 1

        while next:
            r = self.res.get('/conversations/feed', page=next)
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

    def testFeedListAfter(self):
        next = 1

        while next:
            r = self.res.get('/conversations/feed', page=next, after='2007-03-04T21:00:00')
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

    def testList(self):
        next = 1

        while next:
            r = self.res.get('/conversations', page=next)
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

    def testListAfter(self):
        next = 1

        while next:
            r = self.res.get('/conversations', page=next, after='2007-03-04T21:00:00')
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

    def testIndex(self):
        next = 1

        while next:
            r = self.res.get('/interests/1e03ed55/conversations', page=next)
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

    def testIndexWithLocationOrderByDistance(self):
        next = 1

        while next:
            r = self.res.get('/interests/1e03ed55/conversations', page=next, order='distance', lat=-27.8727, lon=-54.4951, radius=20000)
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

    def testIndexRecent(self):
        next = 1

        while next:
            r = self.res.get('/interests/1e03ed55/conversations', page=next, order='recent')
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

    def testCreate(self):
        data = dict(message='Just another silly test')
        r = self.res.post('/interests/1e03ed55/conversations', data)
        self.assertEqual(201, r.status_int)

        data = loads(r.body_string())
        self.assertTrue(data.get('id') is not None)

    def testShow(self):
        r = self.res.get('/interests/1e03ed55/conversations/bd7a5164')
        self.assertEqual(200, r.status_int)

        data = loads(r.body_string())
        self.assertTrue(data.get('id') is not None)
        self.assertTrue(data.get('message') is not None)

    def showReplies(self):
        r = self.res.get('/interests/1e03ed55/conversations/bd7a5164/replies')
        self.assertEqual(200, r.status_int)

        data = loads(r.body_string())
        replies = data.get('replies')
        self.assertTrue(len(replies) > 0)

    def createReply(self):
        data = dict(message='Just another silly test -- reply')
        r = self.res.post('/interests/1e03ed55/conversations/bd7a5164/replies', data)
        self.assertEqual(201, r.status_int)

        data = loads(r.body_string())
        self.assertTrue(data.get('id') is not None)

    def showReply(self):
        r = self.res.get('/interests/1e03ed55/conversations/bd7a5164/replies/46ed28b6')
        self.assertEqual(200, r.status_int)

        data = loads(r.body_string())
        self.assertTrue(data.get('id') is not None)
        self.assertTrue(data.get('message') is not None)
