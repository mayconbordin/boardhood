import os, unittest

# model tests
import test_model_interests
import test_model_users
import test_model_conversations
import test_model_applications

# endpoint tests
import test_endpoint_interests
import test_endpoint_conversations
import test_endpoint_users

def run_test_suite():
    loader = unittest.TestLoader()

    suite = loader.loadTestsFromModule(test_model_interests)
    suite.addTests(loader.loadTestsFromModule(test_model_users))
    suite.addTests(loader.loadTestsFromModule(test_model_conversations))
    suite.addTests(loader.loadTestsFromModule(test_model_applications))

    suite.addTests(loader.loadTestsFromModule(test_endpoint_interests))
    suite.addTests(loader.loadTestsFromModule(test_endpoint_conversations))
    suite.addTests(loader.loadTestsFromModule(test_endpoint_users))

    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)

if __name__ == "__main__":
    run_test_suite()
