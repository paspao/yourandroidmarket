YourAndroidMarket
=================

Introduction
------------

This is a really basic server to manage your apps like an android market (now play store). The main scope is to give the basic structure for building your own android store.

What you need
-------------
All the server side is written with [node.js][] on a REST architecture.


[node.js][] 0.6.x

[Android sdk][]

[PostgreSQL][] >= 8.x

Server Side
===========

Install
-------

	npm install pg
	npm install querystring
	npm install router
	psql [your options] < market_android/script_sql/postgres.sql

Configure
---------

Then configure properly market_android/conf/confdirs.js with your paths.

	exports.aapt = '/opt/android-sdk-mac_x86/platform-tools/aapt';//path to aapt binary file
	exports.md5sum = '/sw/bin/md5sum';//path to md5sum binary file
	exports.unzip='/sw/bin/unzip';//path to unzip binary file
	exports.mv='/bin/mv';//path to move binary file
	exports.rm='/bin/rm';//path to remove binary file
	exports.pathApk = '/Users/paspao/Desktop';//path to dir of apk files (with at last one apk)
	exports.iconDir='/Users/paspao/icon';//path to dir icon files (empty)
	exports.tmpDir='/Users/paspao/temp';//path to temporary dir path (empty)

And edit the file market_android/conf/postgresqlConnection.js

	exports.connectionString = "pg://market:market@127.0.0.1:5432/market";

Populate DB
-----------
If you have configured everything properly then launch:

	node populate.js

Well, the table Applicazione will contain your apps. Now you can add your users and groups (remember the password need md5sum). In a future release I'll add a script to manage users and groups.

Run
---

	node market.js <port>

API
---
* /
* /api/listapps
* /api/downladapp/{id}
* /api/getappicon/{appid}
* /api/appinfo/{appid}

Client Side
===========

Edit the file MarketAndroidClient/res/raw/conf.properties, below the content:

	rest.url=http://192.168.89.20:8000

	rest.user=email
	rest.password=password

	simplelogin.path=/

	listapps.path=/api/listapps

	downloadapp.path=/api/downladapp/

	getappicon.path=/api/getappicon/

	appinfo.path=/api/appinfo/

In a standard context, you must only edit the rest.url


[node.js]: http://nodejs.org/
[Android sdk]: http://developer.android.com/sdk/index.html
[PostgreSQL]: http://www.postgresql.org/download/