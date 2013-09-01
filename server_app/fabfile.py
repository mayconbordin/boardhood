
from fabric.api import *

# the user to use for the remote commands
env.user = 'ubuntu'

# amazon key file
env.key_filename = ['key.pem']
env.disable_known_hosts = True

# the servers where the commands are executed
env.hosts = [
    '127.0.0.1'
]

sql_files = {
    'postgis': [
	    '/usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql',
	    '/usr/share/postgresql/9.1/contrib/postgis-1.5/spatial_ref_sys.sql',
	    '/usr/share/postgresql/9.1/contrib/postgis_comments.sql'
	],
	'boardhood': [
	    'schema.sql',
	    'extras.sql',
	    'functions.sql',
	    'sample.sql'
	]
}


def apt_get(*packages):
    sudo('apt-get -y --no-upgrade install %s' % ' '.join(packages), shell=False)


def install_postgresql():
    # db config
    user   = 'postgres'
    passwd = 'postgres'
    # install all needed packages
    apt_get('postgresql-9.1', 'postgresql-client-9.1', 'postgresql-server-dev-9.1',
            'postgresql-contrib-9.1', 'libpq5', 'libpq-dev', 'postgis', 'postgresql-9.1-postgis')
    # change postgres UNIX password      
    with hide('running', 'stdout', 'stderr'):
        run('echo -e "%s\n%s\n" | sudo passwd %s' % (passwd, passwd, user))
    # change postgres db password
    sudo('echo "ALTER USER %s WITH PASSWORD \'%s\'" | sudo su %s -c psql' % (user, passwd, user))


def install_python():
    apt_get('build-essential', 'python-dev', 'python-setuptools', 'python-pip',
            'libjpeg8', 'libjpeg8-dev', 'libpng12-0', 'libpng12-dev', 'libfreetype6',
            'libfreetype6-dev', 'zlib1g', 'zlib1g-dev')
    sudo('pip install psycopg2')
    sudo('pip install virtualenv')
    
    # create links to build PIL
    # http://www.jayzawrotny.com/blog/django-pil-and-libjpeg-on-ubuntu-1110
    sudo('ln -s /usr/lib/x86_64-linux-gnu/libjpeg.so /usr/lib')
    sudo('ln -s /usr/lib/x86_64-linux-gnu/libfreetype.so /usr/lib')
    sudo('ln -s /usr/lib/x86_64-linux-gnu/libz.so /usr/lib')


def install_memcached():
    apt_get('memcached')
    

def install_uwsgi():
    apt_get('uwsgi', 'uwsgi-plugin-python')
    

def install_nginx():
    apt_get('nginx')
    sudo('rm -f /etc/nginx/sites-available/default /etc/nginx/sites-enabled/default')


def install_munin():
    apt_get('munin libio-all-lwp-perl libdbd-pg-perl libcache-memcached-perl')
    

def setup_instance():
    sudo('apt-get update')
    install_postgresql()
    install_python()
    install_memcached()
    install_uwsgi()
    install_nginx()


def create_folders():
    sudo('mkdir -p /var/www/boardhood')
    sudo('mkdir -p /var/www/boardhood/site')
    sudo('mkdir -p /var/www/boardhood/api')
    sudo('mkdir -p /var/www/boardhood/api/resources')
    sudo('mkdir -p /var/www/boardhood/logs')
    sudo('touch /var/www/boardhood/logs/uwsgi.log')
    
    with cd('/var/www/boardhood'):
        sudo('virtualenv ./env')

  
def set_app_permissions():
    sudo('chown -R www-data:www-data /var/www/boardhood')
    sudo('chmod -R g+w /var/www/boardhood')
    

def tmpdir():
    sudo('rm -rf /tmp/boardhood')
    run('mkdir /tmp/boardhood')
    return cd('/tmp/boardhood')
    
def clear_tempdir():
    sudo('rm -rf /tmp/boardhood')


def run_sql(sql_file, dbname):
    user = 'postgres'
     
    with tmpdir():
        put('db/%s' % sql_file, '/tmp/boardhood/%s' % sql_file)
        sudo('sudo su %s -c "psql -d %s -f /tmp/boardhood/%s"' % (user, dbname, sql_file))
    
    # delete temp folder
    clear_tempdir()


# create_db:boardhood_test,yes
# create_db:dbname=boardhood_test,sample=yes
def create_db(dbname='boardhood_test', sample='yes'):
    user = 'postgres'
    
    # drop db if exists
    with settings(warn_only=True):
        sudo('sudo su %s -c "dropdb %s"' % (user, dbname))
    
    # create db
    sudo('sudo su %s -c "createdb %s"' % (user, dbname))
    
    # execute postgis sql files
    for sql_file in sql_files['postgis']:
        sudo('sudo su %s -c "psql -d %s -f %s"' % (user, dbname, sql_file))
        
    # execute app sql files
    with tmpdir():
        for sql_file in sql_files['boardhood']:
            if (sample == 'yes' and sql_file == 'sample.sql') or sql_file != 'sample.sql':
                put('api/db/%s' % sql_file, '/tmp/boardhood/%s' % sql_file)
                sudo('sudo su %s -c "psql -d %s -f /tmp/boardhood/%s"' % (user, dbname, sql_file))
    
    # delete temp folder
    clear_tempdir()
    

def pack():
    # create a new source distribution as tarball
    with lcd('api/'):
        local('python setup.py sdist --formats=gztar', capture=False)


def deploy_site():
    local('tar czf site.tar.gz -C site/publish/ .')
    with tmpdir():
        put('site.tar.gz', '.')
        sudo('tar xvfz ./site.tar.gz -C /var/www/boardhood/site/')
        
    # delete package and temp folder
    local('rm site.tar.gz')
    clear_tempdir()
    

def deploy_resources():
    local('tar czf resources.tar.gz -C api/boardhood/resources/ .')
    with tmpdir():
        put('resources.tar.gz', '.')
        sudo('tar xvfz ./resources.tar.gz -C /var/www/boardhood/api/resources/')
        
    local('rm resources.tar.gz')
    clear_tempdir()

        
def deploy():
    with lcd('api/'):
        # figure out the release name and version
        dist = local('python setup.py --fullname', capture=True).strip()

        # upload the source tarball to the temporary folder on the server
        put('dist/%s.tar.gz' % dist, '/tmp/boardhood.tar.gz')

    # create a place where we can unzip the tarball, then enter
    # that directory and unzip it
    with tmpdir():
        run('tar xzf /tmp/boardhood.tar.gz')
        # now setup the package with our virtual environment's
        # python interpreter
        with cd('%s' % dist):
            sudo('/var/www/boardhood/env/bin/python setup.py install')
            
        # upload the file that will execute the application
        put('api/run.py', './run.py')
        sudo('mv ./run.py /var/www/boardhood/api/run.py')
        set_app_permissions()

        # upload nginx config file
        put('config/nginx.cfg', './nginx.cfg')
        sudo('mv /tmp/boardhood/nginx.cfg /etc/nginx/sites-available/boardhood')
        sudo('ln -s -f /etc/nginx/sites-available/boardhood /etc/nginx/sites-enabled/boardhood')
    
        # upload uwsgi config file
        put('config/uwsgi.cfg', './uwsgi.cfg')
        sudo('mv ./uwsgi.cfg /etc/uwsgi/apps-available/boardhood.ini')
        sudo('ln -s -f /etc/uwsgi/apps-available/boardhood.ini /etc/uwsgi/apps-enabled/boardhood.ini')

    # restart both services
    sudo('service nginx restart', pty=False)
    sudo('service uwsgi restart', pty=False)

    # now that all is set up, delete the folder
    clear_tempdir()
