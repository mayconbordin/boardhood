import unittest
from test_endpoint import TestEndpoint, loads

class TestInterestsEndpoint(TestEndpoint):
    def testIndex(self):
        next = 1

        while next:
            r = self.res.get('/interests', page=next)
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

    def testIndexRecent(self):
        r = self.res.get('/interests', order='recent')
        self.assertEqual(200, r.status_int)
        data = loads(r.body_string())

        interests = data.get('interests')
        self.assertTrue(len(interests) > 0)

        for interest in interests:
            self.assertTrue(interest.get('id') is not None)
            self.assertTrue(interest.get('name') is not None)

    def testIndexDistance(self):
        r = self.res.get('/interests', order='distance', lat=-27.86403, lon=-54.4593889)
        self.assertEqual(200, r.status_int)
        data = loads(r.body_string())

        interests = data.get('interests')
        self.assertTrue(len(interests) > 0)

        for interest in interests:
            self.assertTrue(interest.get('id') is not None)
            self.assertTrue(interest.get('name') is not None)

    def testIndexAround(self):
        r = self.res.get('/interests', lat=-27.86403, lon=-54.4593889, radius=20000)
        self.assertEqual(200, r.status_int)
        data = loads(r.body_string())

        interests = data.get('interests')
        self.assertTrue(len(interests) > 0)

        for interest in interests:
            self.assertTrue(interest.get('id') is not None)
            self.assertTrue(interest.get('name') is not None)


    def testCreate(self):
        data = dict(name='Metallica')

        r = self.res.post('/interests', data)
        self.assertEqual(201, r.status_int)

    def testGetById(self):
        r = self.res.get('/interests/1e03ed55')
        self.assertEqual(200, r.status_int)

    def testSearch(self):
        r = self.res.get('/interests/search/m')
        self.assertEqual(200, r.status_int)

        data = loads(r.body_string())
        interests = data.get('interests')
        self.assertTrue(len(interests) > 0)

        for interest in interests:
            self.assertTrue(interest.get('id') is not None)
            self.assertTrue(interest.get('name') is not None)

    def testFollowers(self):
        r = self.res.get('/interests/1e03ed55/followers')
        self.assertEqual(200, r.status_int)

        data = loads(r.body_string())
        followers = data.get('followers')

        for follower in followers:
            self.assertTrue(follower.get('name') is not None)

    def testFollow(self):
        r = self.res.post('/interests/1e03ed55/followers/me')
        self.assertEqual(201, r.status_int)

    def testFollowing(self):
        r = self.res.get('/interests/1e03ed55/followers/me')
        self.assertEqual(204, r.status_int)

    def testUnfollow(self):
        r = self.res.delete('/interests/1e03ed55/followers/me')
        self.assertEqual(204, r.status_int)

if __name__ == '__main__':
    unittest.main()
