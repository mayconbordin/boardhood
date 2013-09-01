import sys, os
from boardhood import create_app

def main():
    args = sys.argv[1:]
    envs = ['development', 'test', 'production']

    if len(args) == 1:
        if args[0] in envs:
            os.environ['BOARDHOOD_ENV'] = args[0]
            app = create_app()
            app.run()
        else:
            print "Environment variable not valid. Try: development, testing or production."

if __name__ == "__main__":
    main()
