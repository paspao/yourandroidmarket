var dbconnection = require('../conf/postgresqlConnection.js');

var pg = require('pg');

/**
 * Constructor function for the Applicazione object..
 * 
 * @constructor
 */
exports.listByUserId=listByUserId;
exports.getByIdAndUserId=getByIdAndUserId;

var Applicazione = exports.Applicazione = function(name,icon,package_,version_code,version_name,md5sum,apkname) {
	  this.name=name;
	  this.icon=icon;
	  this.package_=package_;
	  this.version_code=version_code;
	  this.version_name=version_name;
	  this.md5sum=md5sum;
	  this.apkname=apkname;
};

Applicazione.prototype.listAll = function(callback) {

};

function listByUserId (userid,callback) {
	console.log('Called Applicazione with userid = ' + userid);
	var pg_client = new pg.Client(dbconnection.connectionString);
	pg_client.connect();
	
	var strQuery = 'SELECT a.name, a.icon, a.package_, a.version_code, a.version_name, a.md5sum, a.apkname '+
	'FROM applicazione a,usergroup ug,group_ g, group_applicazione ga '+
	'WHERE ug.id_user=$1 AND '+
	'ug.id_group=g.id AND '+
	'ga.id_group_=g.id AND '+
	'ga.id_applicazione=a.name;';
	var query = pg_client.query(strQuery, [ userid ]);
	console.log('Executing the query ' + JSON.stringify(query));
	var decoded=new Array();
	query.on('error', function(err) {
		console.log('Connection error @ Applicazione.listByUserId ' + err);
		pg_client.end();
		// callback(decoded,err);
		callback(null);
	});
	query.on('row', function(row) {
		
		if (row) {
			decoded.push(new Applicazione(row.name,row.icon,row.package_,row.version_code,row.version_name,row.md5sum,row.apkname));

		} 
	});

	query.on('end', function() {
		pg_client.end();
		callback(decoded);
	});
};

function getByIdAndUserId (userid,appid,callback) {
	console.log('Called Applicazione with id = ' + appid+' and userid ='+userid);
	var pg_client = new pg.Client(dbconnection.connectionString);
	pg_client.connect();
	
	var strQuery = 'SELECT a.name, a.icon, a.package_, a.version_code, a.version_name, a.md5sum, a.apkname '+
	'FROM applicazione a,usergroup ug,group_ g, group_applicazione ga '+
	'WHERE ug.id_user=$1 AND '+
	'ug.id_group=g.id AND '+
	'ga.id_group_=g.id AND '+
	'ga.id_applicazione=a.name AND '+
	'a.name=$2 ;';
	var query = pg_client.query(strQuery, [ userid,appid ]);
	console.log('Executing the query ' + JSON.stringify(query));
	var decoded=null;
	query.on('error', function(err) {
		console.log('Connection error @ Applicazione.listByUserId ' + err);
		pg_client.end();
		// callback(decoded,err);
		callback(null);
	});
	query.on('row', function(row) {
		
		if (row) {
			decoded=new Applicazione(row.name,row.icon,row.package_,row.version_code,row.version_name,row.md5sum,row.apkname);

		} 
	});

	query.on('end', function() {
		pg_client.end();
		callback(decoded);
	});
};