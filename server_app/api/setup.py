from setuptools import setup

setup(
    name='BoardHood-API',
    version='1.0',
    url='http://boardhood.com',
    author='Maycon Bordin',
    author_email='mayconbordin@gmail.com',
    description='BoardHood API',
    long_description=__doc__,
    packages=[
        'boardhood',
        'boardhood.helpers',
        'boardhood.models',
        'boardhood.routes',
        'boardhood.sql',
        'boardhood.tests'
    ],
    #namespace_packages=['boardhood'],
    include_package_data=True,
    zip_safe=False,
    platforms='any',
    install_requires=[
        'Flask==0.9',
        'Flask-Cache==0.4.0',
        'Flask-Script==0.3.3',
        'Jinja2==2.6',
        'MultipartPostHandler==0.1.0',
        'Werkzeug==0.8.3',
        'argparse==1.2.1',
        'dictshield==0.4.4',
        'http-parser==0.7.5',
        'psycopg2==2.4.5',
        'py-bcrypt==0.2',
        'pycrypto==2.6',
        'python-memcached==1.48',
        'restkit==4.1.3',
        'socketpool==0.4.1',
        'ssh==1.7.14',
        'wsgiref==0.1.2',
        'boto==2.5.2',
        'PIL==1.1.7',
        'python-memcached-stats==0.1'
    ],
    dependency_links=[
        'https://github.com/dlrust/python-memcached-stats/tarball/master#egg=python-memcached-stats-0.1'
    ]
)
