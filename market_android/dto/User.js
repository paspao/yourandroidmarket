var dbconnection = require('../conf/postgresqlConnection.js');
var md5 = require('../util/md5');
var pg = require('pg');

/**
 * Constructor function for the User object..
 * 
 * @constructor
 */
exports.manageLogin = manageLogin;
var User = exports.User = function(id, email, password) {

	this.id=id;
	this.email = email;
	this.password = password;
};

User.prototype.list = function(callback) {

};

User.prototype.show = function(email, callback) {
	console.log('Called User.show with email = ' + email);
	var pg_client = new pg.Client(dbconnection.connectionString);
	pg_client.connect();
	var user = null;
	var newuser = null;
	var strQuery = 'SELECT id,email,password from user_ WHERE email = $1';
	var query = pg_client.query(strQuery, [ email ]);
	logger.info('Executing the query ' + JSON.stringify(query));
	query.on('error', function(err) {
		console.log('Connection error @ User.show ' + err);
		pg_client.end();
		// callback(decoded,err);
		callback('nouser', null);
	});
	query.on('row', function(row) {
		user = row;
		if (user) {
			newuser = new User(user.id, user.email, utc, user.password);

		} else
			callback('nouser', null);
	});

	query.on('end', function() {
		pg_client.end();
		if (newuser)
			callback(null, newuser);
	});
};

function manageLogin(email,pass, callback) {
	console.log('Called User manageLogin with email = ' + email);
	var pg_client = new pg.Client(dbconnection.connectionString);
	pg_client.connect();
	
	var newuser = null;
	var strQuery = 'SELECT id,email,password from user_ WHERE email = $1 AND password=$2;';
	var md5result = md5.hex_md5(pass);
	var query = pg_client.query(strQuery, [ email,md5result ]);
	console.log('Executing the query ' + JSON.stringify(query));
	query.on('error', function(err) {
		console.log('Connection error @ User manageLogin ' + err);
		pg_client.end();
		// callback(decoded,err);
		callback('nouser', null);
	});
	query.on('row', function(row) {
		console.log(row);
		if (row) {
			newuser = new User(row.id, row.email, row.password);
			
		} 
	});

	query.on('end', function() {
		
		pg_client.end();
		if (newuser)
			callback(null, newuser);
		else
			callback('nouser', null);
	});
};