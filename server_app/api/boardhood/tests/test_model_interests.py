import unittest
from config import app
from datetime import datetime

from boardhood.models.base import ValidationException
from boardhood.models.interests import Interest
from boardhood.helpers.validator import is_integer

class TestInterestModel(unittest.TestCase):
    def setUp(self):
        Interest.db = app.db.get_db()
        Interest.logger = app.logger

    def testCreate(self):
        name = 'teste_%s' % str(datetime.now())
        obj = Interest.create(Interest(name=name))
        self.assertEqual(obj.name, name)
        self.assertTrue(is_integer(obj.id), 'ID was supposed to be int. Value: %s' % str(obj.id))

        try:
            name = 't' * 255
            obj = Interest.create(Interest(name=name))
            self.fail("Create interest should not succeed with invalid data")
        except ValidationException, e:
            self.assertTrue(True)

    def testNameExists(self):
        self.assertTrue(Interest.nameExists('Photography'))

    def testExists(self):
        self.assertTrue(Interest.exists(1))

    def testFind(self):
        obj = Interest.find(1)
        self.assertEqual(obj.name, 'Photography')
        self.assertTrue(is_integer(obj.id))

    def testFollow(self):
        self.assertTrue(Interest.follow(1, 6))

    def testFollowing(self):
        self.assertTrue(Interest.following(1, 6))

    def testFollowers(self):
        followers = Interest.followers(1)
        following = False
        for follower in followers:
            if follower.id == 6:
                following = True
        self.assertTrue(following)

    def testUnfollow(self):
        self.assertTrue(Interest.unfollow(1, 6))

    def testCountAll(self):
        count = Interest.countAll()
        self.assertTrue(is_integer(count))

    def testCountAllAround(self):
        count = Interest.countAll(location=[-27.86403, -54.4593889], radius=20000)
        self.assertTrue(is_integer(count))

    def testFindAllRecent(self):
        objs = Interest.findAll()
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

    def testFindAllPopular(self):
        objs = Interest.findAll(order_by='popular')
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

    def testFindAllOrderByDistance(self):
        objs = Interest.findAll(order_by='distance', location=[-27.86403, -54.4593889])
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

    def testFindAllOrderByDistanceAround(self):
        objs = Interest.findAll(order_by='distance', location=[-27.86403, -54.4593889], radius=20000)
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

    def testFindAllRecentAround(self):
        objs = Interest.findAll(location=[-27.86403, -54.4593889], radius=20000)
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

    def testFindAllPopularAround(self):
        objs = Interest.findAll(order_by='popular', location=[-27.86403, -54.4593889], radius=20000)
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

    def testSearchByUser(self):
        objs = Interest.searchByUser('d', 1)
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

    def testFindAllByUser(self):
        objs = Interest.findAllByUser(1)
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

    def testCountAllByUser(self):
        count = Interest.countAllByUser(1)
        self.assertTrue(is_integer(count))

    def testFindAllByUserName(self):
        objs = Interest.findAllByUserName('john')
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

    def testCountAllByUserName(self):
        count = Interest.countAllByUserName('john')
        self.assertTrue(is_integer(count))

    def testSearch(self):
        objs = Interest.search('m')
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Interest))

if __name__ == '__main__':
    unittest.main()
