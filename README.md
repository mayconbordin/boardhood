# BoardHood #

BoardHood is an application developed as a final year project (FYP) in Information Systems, the documentation and presentation can be found [here](https://github.com/mayconbordin/tcc).

Basically, it is a mobile application for people to discuss about topics of interest. More basically yet, it's a forum, but you can choose to follow only those topics that you are interested in.

The client has been developed in Android and published in the [Google Play](https://play.google.com/store/apps/details?id=com.boardhood.mobile&hl=en) marketplace.

## Technology ##

* Deployed in the AWS as a two-tier application.
* Developed in Python with Flask Framework.
* The web server is nginx and the application server is uWSGI.
* The database is PostgreSQL with PostGIS extension.
* memcached is used for storing authentication tokens.
* Amazon S3 was used for storing user avatars.
* The server side is a JSON RESTful API.
* Munin was used for monitoring the server.
* The installation and configuration of all software stack above was done with a Fabric script.
* The Python application was packaged with Distribute and deployed with Fabric.
* The client application was developed for Android.

## Client Screenshots ##

<img src="https://raw.github.com/mayconbordin/boardhood/master/resources/screenshots/screenshots.jpg" alt="Screenshot #1"/>

## License ##

Copyright (c) 2012 Maycon Bordin

Licensed under the MIT License (MIT)
