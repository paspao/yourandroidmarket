var sys = require('util');
var url = require('url');
var http = require('http');
var fs = require('fs');
var route = require('router');
var conf = require('./conf/confdirs.js');
var dbconnection = require('./conf/postgresqlConnection.js');
var User = require('./dto/User.js');
var Applicazione = require('./dto/Applicazione.js');
var router = route();

console.log('Start your market http://0.0.0.0:' + process.argv[2] + '\n');
http.createServer(router).listen(process.argv[2]);

// /api/simplelogin
router.get('/', function(request, response) {
	var queryString = url.parse(request.url, true).query;
	if (queryString && queryString.email && queryString.password) {
		User.manageLogin(queryString.email, queryString.password, function(msg,
				user) {
			if (user) {
				console.log("Login success: " + user.email);
				response.writeHead(200, {
					'content-type' : 'text/plain',
					'content-encoding' : 'utf-8',
					'Access-Control-Allow-Origin' : '*'
				});
				response.write('Login success!!!');
				response.end();
			}
			else {
				console.log("Login failed!!!");
				response.writeHead(401, {
					'content-type' : 'text/plain',
					'content-encoding' : 'utf-8',
					'Access-Control-Allow-Origin' : '*'
				});
				response.write('Login failed!!!');
				response.end();
			}
			});
	}else {
		response.writeHead(401, {
			'content-type' : 'text/plain',
			'content-encoding' : 'utf-8',
			'Access-Control-Allow-Origin' : '*'
		});
		response.write('Not authorized!');
		response.end();
	}
});
router.get('/api/listapps', function(request, response) {
	var queryString = url.parse(request.url, true).query;
	if (queryString && queryString.email && queryString.password) {

		User.manageLogin(queryString.email, queryString.password, function(msg,
				user) {
			if (user) {
				console.log("Login success: " + user.email);

				Applicazione.listByUserId(user.id, function(appArr) {
					response.writeHead(200, {
						'content-type' : 'application/json',
						'content-encoding' : 'utf-8',
						'Access-Control-Allow-Origin' : '*'
					});
					var str = JSON.stringify(appArr);
					console.log(str);
					response.write(str);

					response.end();
				});

			} else {
				console.log("Login failed!!!");
				response.writeHead(401, {
					'content-type' : 'text/plain',
					'content-encoding' : 'utf-8',
					'Access-Control-Allow-Origin' : '*'
				});
				response.write('Login failed!!!');
				response.end();
			}
		});
	} else {
		response.writeHead(401, {
			'content-type' : 'text/plain',
			'content-encoding' : 'utf-8',
			'Access-Control-Allow-Origin' : '*'
		});
		response.write('Not authorized!');
		response.end();
	}
});

router.get('/api/downladapp/{id}', function(request, response) {
	var appid = request.params.id;
	var queryString = url.parse(request.url, true).query;
	if (queryString && queryString.email && queryString.password) {

		User.manageLogin(queryString.email, queryString.password, function(msg,
				user) {

			if (user) {
				console.log("Login success: " + user.email);
				Applicazione.getByIdAndUserId(user.id, appid, function(appobj) {
					if (appobj) {
						var file = conf.pathApk + '/' + appobj.apkname;
						/*
						response.setHeader('Content-disposition',
								'attachment; filename=' + appobj.apkname);
						response.setHeader('Content-type',
								'application/vnd.android.package-archive');

						var filestream = fs.createReadStream(file);
						filestream.on('data', function(chunk) {
							response.write(chunk);
						});
						filestream.on('end', function() {
							response.end();
						});*/
						fs.readFile(file,function (err, data){							
							response.writeHead(200, {'Content-Type': 'application/vnd.android.package-archive',
					        	'Content-disposition':'attachment; filename=' + appobj.apkname,
					        	'Content-Length':data.length});
							response.write(data);
							response.end();
					    });
						
						
					} else {

						response.writeHead(404, {
							'content-type' : 'text/plain',
							'content-encoding' : 'utf-8',
							'Access-Control-Allow-Origin' : '*'
						});
						response.write('Not found!!!');
						response.end();

					}
				});

			} else {
				console.log("Login failed!!!");
				response.writeHead(401, {
					'content-type' : 'text/plain',
					'content-encoding' : 'utf-8',
					'Access-Control-Allow-Origin' : '*'
				});
				response.write('Login failed!!!');
				response.end();
			}

		});
	} else {
		response.writeHead(401, {
			'content-type' : 'text/plain',
			'content-encoding' : 'utf-8',
			'Access-Control-Allow-Origin' : '*'
		});
		response.write('Not authorized!');
		response.end();
	}
});

router.get('/api/getappicon/{appid}', function(request, response) {
	var appid = request.params.appid;
	var queryString = url.parse(request.url, true).query;
	if (queryString && queryString.email && queryString.password) {

		User.manageLogin(queryString.email, queryString.password, function(msg,
				user) {
			if (user) {
				console.log("Login success: " + user.email);
				Applicazione.getByIdAndUserId(user.id, appid, function(appobj) {
					if (appobj) {
						var startIconName=appobj.icon.lastIndexOf('/');
						var filename=appobj.icon.substring(startIconName+1,appobj.icon.length);
						var file = conf.iconDir + '/' + filename;

						response.setHeader('Content-disposition',
								'attachment; filename=' + filename);
						response.setHeader('Content-type',
								'image/png');

						var filestream = fs.createReadStream(file);
						filestream.on('data', function(chunk) {
							response.write(chunk);
						});
						filestream.on('end', function() {
							response.end();
						});
					} else {

						response.writeHead(404, {
							'content-type' : 'text/plain',
							'content-encoding' : 'utf-8',
							'Access-Control-Allow-Origin' : '*'
						});
						response.write('Not found!!!');
						response.end();

					}
				});

			} else {
				console.log("Login failed!!!");
				response.writeHead(401, {
					'content-type' : 'text/plain',
					'content-encoding' : 'utf-8',
					'Access-Control-Allow-Origin' : '*'
				});
				response.write('Login failed!!!');
				response.end();
			}

		});
	} else {
		response.writeHead(401, {
			'content-type' : 'text/plain',
			'content-encoding' : 'utf-8',
			'Access-Control-Allow-Origin' : '*'
		});
		response.write('Not authorized!');
		response.end();
	}
});

router.get('/api/appinfo/{appid}', function(request, response) {
	var appid = request.params.appid;
	var queryString = url.parse(request.url, true).query;
	if (queryString && queryString.email && queryString.password) {

		User.manageLogin(queryString.email, queryString.password, function(msg,
				user) {
			if (user) {
				console.log("Login success: " + user.email);
				Applicazione.getByIdAndUserId(user.id, appid, function(appobj) {
					if (appobj) {

						response.writeHead(200, {
							'content-type' : 'application/json',
							'content-encoding' : 'utf-8',
							'Access-Control-Allow-Origin' : '*'
						});
						response.write(JSON.stringify(appobj));
						response.end();
					} else {

						response.writeHead(404, {
							'content-type' : 'text/plain',
							'content-encoding' : 'utf-8',
							'Access-Control-Allow-Origin' : '*'
						});
						response.write('Not found!!!');
						response.end();

					}
				});

			} else {
				console.log("Login failed!!!");
				response.writeHead(401, {
					'content-type' : 'text/plain',
					'content-encoding' : 'utf-8',
					'Access-Control-Allow-Origin' : '*'
				});
				response.write('Login failed!!!');
				response.end();
			}

		});
	} else {
		response.writeHead(401, {
			'content-type' : 'text/plain',
			'content-encoding' : 'utf-8',
			'Access-Control-Allow-Origin' : '*'
		});
		response.write('Not authorized!');
		response.end();
	}
});

router.all(function(request, response) {
	response.writeHead(404, {
		'content-type' : 'text/plain',
		'content-encoding' : 'utf-8',
		'Access-Control-Allow-Origin' : '*'
	});
	response.write('Unknown resource.');
	response.end();
});
