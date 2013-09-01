import sys, os
from boardhood import create_app

app = create_app()

if __name__ == "__main__":
    app.run()
