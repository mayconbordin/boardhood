import unittest
import random
from config import app
from datetime import datetime

from boardhood.models.base import ValidationException
from boardhood.models.users import User
from boardhood.helpers.validator import is_integer

class TestUserModel(unittest.TestCase):
    def setUp(self):
        User.db = app.db.get_db()
        User.logger = app.logger

        alpha = 'abcdefghijklmnopqrstuvxwyz1234567890'
        self.name = 'teste' + random.choice(alpha).join(random.sample(list(alpha), 5))

    def testCreate(self):
        obj = User.create(User(name=self.name, email=self.name + '@teste.com', password='teste'))
        self.assertEqual(obj.name, self.name)
        self.assertTrue(is_integer(obj.id), 'ID was supposed to be int. Value: %s' % str(obj.id))

        try:
            name = 'teste&%***(9 "" '
            obj = User.create(User(name=name, email=name + '@teste.com', password='teste'))
            self.fail("Create user should not succeed with invalid data")
        except ValidationException, e:
            self.assertTrue(True)


    def testExists(self):
        self.assertTrue(User.exists(1))

    def testFindByName(self):
        name = 'john'
        obj = User.findByName(name)
        self.assertTrue(obj.name == name)

    def testAuthenticate(self):
        user = User(name='john', password='teste')
        auth = User.authenticate(user)
        if auth is False:
            self.fail("User is not authenticated")

    def testNameExists(self):
        self.assertTrue(User.nameExists('john'))
        self.assertFalse(User.nameExists('dasdasdjskow3'))

    def emailExists(self):
        self.assertTrue(User.emailExists('john@example.com'))
        self.assertFalse(User.emailExists('dasdasdjskow3@teste.com'))

    def testUpdate(self):
        user = User(id=2, name='paul', avatar_url='https://s3.amazonaws.com/boardhood_user_avatars/sheldoncooper.png')
        user.email = "paul@example2.com"
        user.password = 'teste_2'

        self.assertTrue(user.update())

if __name__ == '__main__':
    unittest.main()
