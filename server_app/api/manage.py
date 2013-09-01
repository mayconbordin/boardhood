#!/usr/bin/env python
#coding=utf-8

import pprint
import memcache
import sys, os, signal
import subprocess
import time

from subprocess import call, Popen, PIPE
from flask import Flask, current_app as app
from flask import Config
from flaskext.script import Server, Shell, Manager, Command, prompt_bool
from memcached_stats import MemcachedStats

from boardhood import create_app
from boardhood.helpers.os import children_pid

manager = Manager(create_app('development'))
manager.add_command("runserver", Server('127.0.0.1',port=5000))

sql_files_91 = [
        '/usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql',
        '/usr/share/postgresql/9.1/contrib/postgis-1.5/spatial_ref_sys.sql',
        '/usr/share/postgresql/9.1/contrib/postgis_comments.sql',
        'db/schema.sql',
        'db/extras.sql',
        'db/functions.sql',
        'db/sample.sql'
]

sql_files_84 = [
        '/usr/share/postgresql/8.4/contrib/postgis-1.5/postgis.sql',
        '/usr/share/postgresql/8.4/contrib/postgis-1.5/spatial_ref_sys.sql',
        '/usr/share/postgresql/8.4/contrib/postgis_comments.sql',
        'db/schema.sql',
        'db/extras.sql',
        'db/functions.sql',
        'db/sample.sql'
]

def _make_context():
    return dict(db=app.db)

def prepare_context():
    ctx = app.test_request_context()
    ctx.push()
    app.preprocess_request()

manager.add_command("shell", Shell(make_context=_make_context))

## MEMCACHED ##
@manager.command
def memcached(keys=False, stats=False, clear=False):
    """List memcached stored keys and server stats"""
    if 'CACHE_MEMCACHED_SERVERS' in app.config:
        servers = app.config['CACHE_MEMCACHED_SERVERS']
        pp = pprint.PrettyPrinter(indent=4)

        for server in servers:
            host, port = server.split(':')
            mem = MemcachedStats(host, port)

            print '%s' % '=' * 80
            print 'SERVER: %s:%s' % (host, port)

            if keys:
                print 'KEYS:'
                pp.pprint(mem.keys())

            if stats:
                print 'STATS:'
                pp.pprint(mem.stats())

        # clear keys
        if clear:
            cache = memcache.Client(servers, debug=0)
            if cache.flush_all():
                print 'Memcached data flushed'
            else:
                print 'Could not flush memcached'
    else:
        print 'There is no memcached servers in the config files'

@manager.command
def clearpyc():
    """Clear all the .pyc files"""
    call(["find . -name \*.pyc -delete"], shell=True)

@manager.command
@manager.option('-d', '--database', dest='database')
@manager.option('-s', '--showall', dest='showall')
@manager.option('-v', '--version', dest='version')
def db(cmd, database=None, showall=False, version='8.4'):
    """Manipulates databases. Commands: createall, dropall"""
    if database is None:
        database = app.config['POSTGRESQL_DATABASE_DB']

    if showall:
        out = dict(stdin=None, stdout=None, stderr=None)
    else:
        out = dict(stdin=PIPE, stdout=PIPE, stderr=PIPE)

    if cmd == 'createall':
        if prompt_bool("You really want to destroy %s?" % database):
            sys.stdout.write("  [DROP] database %s..." % database)
            print 'ok' if call(["dropdb", database], **out) == 0 else 'error'

            sys.stdout.write("[CREATE] database %s..." % database)
            print 'ok' if call(["createdb", database], **out) == 0 else 'error'

            sys.stdout.write("[CREATE] PL/pgSQL language...")
            print 'ok' if call(["createlang", "plpgsql", database], **out) == 0 else 'error'

            if version == '8.4':
                sqls = sql_files_84
            else:
                sqls = sql_files_91

            for sql_file in sqls:
                sys.stdout.write("  [EXEC] SQL file %s..." % sql_file)
                print 'ok' if call(["psql", "-d", database, "-f", sql_file], **out) == 0 else 'error'

    elif cmd == 'dropall':
        if prompt_bool("You really want to destroy %s?" % database):
            sys.stdout.write("  [DROP] database %s..." % database)
            print 'ok' if call(["dropdb", database], **out) == 0 else 'error'

@manager.command
def test():
    config = Config('application/config/')
    config.from_pyfile('testing.cfg')
    #db('createall', database=config['POSTGRESQL_DATABASE_DB'])

    print ' [START] Testing server'
    proc = subprocess.Popen(["python", "runserver.py", "testing"])
    time.sleep(5)

    call(["python", "application/tests/testsuite.py"])

    print 'KILLING THE TESTING SERVER...'
    time.sleep(5)
    os.kill(proc.pid, signal.SIGTERM)
    for pid in children_pid(proc.pid):
        os.kill(pid, signal.SIGTERM)

if __name__ == "__main__":
    manager.run()
