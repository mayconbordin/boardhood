import unittest
from config import app
from datetime import datetime
from boardhood.models.conversations import Conversation
from boardhood.helpers.validator import is_integer, is_array

class TestConversationModel(unittest.TestCase):
    def setUp(self):
        self.model = Conversation()
        Conversation.db = app.db.get_db()
        Conversation.logger = app.logger

    def testFind(self):
        obj = Conversation.find(1, 1)
        self.assertTrue(isinstance(obj, Conversation))
        self.assertTrue(is_integer(obj.id))

    def testFindReply(self):
        obj = Conversation.findReply(6, 2, 7)
        self.assertTrue(isinstance(obj, Conversation))
        self.assertTrue(is_integer(obj.id))

    def testReplies(self):
        replies = Conversation.replies(6, 2)
        self.assertTrue(is_array(replies))
        self.assertTrue(len(replies) > 0)

        for reply in replies:
            self.assertTrue(isinstance(reply, Conversation))

    def testCountAllByInterest(self):
        count = Conversation.countAll(interest_id=1)
        self.assertTrue(is_integer(count))
        self.assertTrue(count > 0)

    def testCountAllByUser(self):
        count = Conversation.countAll(user_id=2)
        self.assertTrue(is_integer(count))
        self.assertTrue(count > 0)

    def testCountAllAfter(self):
        date = datetime.strptime("21/11/06 16:30", "%d/%m/%y %H:%M")
        count = Conversation.countAll(after=date)
        self.assertTrue(is_integer(count))
        self.assertTrue(count > 0)

    def testCountAll(self):
        count = Conversation.countAll()
        self.assertTrue(is_integer(count))
        self.assertTrue(count > 0)

    def testCreate(self):
        # create without location data
        obj = Conversation.parse({
                'interest_id' : 1,
                'user_id'                       : 1,
                'message'                       : "Hello there, this is just a test, ok?"
        })
        obj = Conversation.create(obj)
        self.assertTrue(is_integer(obj.id))

        # create a reply to the conversation above
        obj2 = Conversation.parse({
                'interest_id'   : 1,
                'user_id'                       : 2,
                'message'                       : "And this is a reply.",
                'lat'                                   : -27.8738,
                'lon'                                   : -54.4761,
                'parent_id'             : obj.id
        })
        obj2 = Conversation.create(obj2)
        self.assertTrue(is_integer(obj2.id))

    def testFindAllRecent(self):
        objs = Conversation.findAll()
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

        if len(objs) > 2:
            self.assertTrue(objs[0].created_at >= objs[1].created_at, 'First object should be newer')

    def testFindAllAfter(self):
        date = datetime.strptime("21/11/06 16:30", "%d/%m/%y %H:%M")
        objs = Conversation.findAll(after=date)
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testFindAllPopular(self):
        objs = Conversation.findAll(order_by='popular')
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

        if len(objs) > 2:
            self.assertTrue(objs[0].score >= objs[1].score, 'First object should have a greater score')

    def testFindAllRecentByInterest(self):
        objs = Conversation.findAll(interest_id=6)
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testFindAllRecentByUser(self):
        objs = Conversation.findAll(user_id=2)
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testFindAllPopularByInterest(self):
        objs = Conversation.findAll(interest_id=6, order_by='popular')
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testFindAllPopularByUser(self):
        objs = Conversation.findAll(user_id=2, order_by='popular')
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testFindAllPopularAroundByInterest(self):
        interest_id = 6
        objs = Conversation.findAll(interest_id=interest_id, order_by='popular', location=[-27.8727, -54.4951])
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))
            self.assertTrue(obj.interest.id == interest_id)

    def testFindAllAround(self):
        objs = Conversation.findAll(location=[-27.8727, -54.4951])
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testFindAllAroundRadius(self):
        objs = Conversation.findAll(location=[-27.8727, -54.4951], radius=20000)
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testFindAllAroundRadiusByUser(self):
        objs = Conversation.findAll(location=[-27.8727, -54.4951], radius=20000, user_id=2)
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testFindAllAroundOrderByDistance(self):
        objs = Conversation.findAll(location=[-27.8727, -54.4951], order_by='distance')
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

        if len(objs) > 2:
            self.assertTrue(objs[0].distance <= objs[1].distance, 'First object should have a smaller distance')

    def testFindAllAroundRadiusOrderByDistance(self):
        radius = 20000
        objs = Conversation.findAll(location=[-27.8727, -54.4951], order_by='distance', radius=radius)
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))
            self.assertTrue(obj.distance <= radius)

        if len(objs) > 2:
            self.assertTrue(objs[0].distance <= objs[1].distance, 'First object should have a smaller distance')

    def testFindAllAroundByInterest(self):
        interest_id = 6
        objs = Conversation.findAll(interest_id=interest_id, location=[-27.8727, -54.4951])
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))
            self.assertTrue(obj.interest.id == interest_id)

    def testFindAllByUser(self):
        objs = Conversation.findAllByUser(1)
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testCountAllByUser(self):
        count = Conversation.countAllByUser(1)
        self.assertTrue(is_integer(count))

    def testFindAllByUserName(self):
        objs = Conversation.findAllByUserName('john')
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testCountAllByUserName(self):
        count = Conversation.countAllByUserName('john')
        self.assertTrue(is_integer(count))

    def testFindAllBySubscription(self):
        objs = Conversation.findAllBySubscription(1)
        self.assertTrue(is_array(objs))
        self.assertTrue(len(objs) > 0)

        for obj in objs:
            self.assertTrue(isinstance(obj, Conversation))

    def testCountAllBySubscription(self):
        count = Conversation.countAllBySubscription(1)
        self.assertTrue(is_integer(count))

if __name__ == '__main__':
    unittest.main()
