import unittest
from config import app

from boardhood.models.applications import Application
from boardhood.helpers.validator import is_integer

class TestUserModel(unittest.TestCase):
    def setUp(self):
        Application.db = app.db.get_db()
        Application.logger = app.logger

    def testFindByKey(self):
        app = Application.findByKey('9c7dc77314ca22b8eec94440fa528157f8b8be03')
        self.assertTrue(app is not None)
        self.assertTrue(is_integer(app.id))
