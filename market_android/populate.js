var sys = require('util');
var fs = require('fs');
var pg = require('pg');
var exec = require('child_process').exec;
var conf = require('./conf/confdirs.js');
var dbconnection = require('./conf/postgresqlConnection.js');
// executes `pwd`
// child = exec("pwd", function (error, stdout, stderr) {
// sys.print('stdout: ' + stdout);
// sys.print('stderr: ' + stderr);
// if (error !== null) {
// console.log('exec error: ' + error);
// }
// });
var escapeshell = function(cmd) {
	return '"' + cmd.replace(/(["\s'$`\\])/g, '\\$1') + '"';
};

var findLabelIcon = function(fileApk, callback) {
	exec(conf.aapt + " d badging " + fileApk + "  |grep application:", function(
			error, stdout, stderr) {
		if (stdout != null) {
			// Find app name
			var escapeChar = "\'";
			// if(stdout.indexOf(escapeChar)==-1);
			// escapeChar="\"";
			var label = 'label';
			var startTitle = stdout.indexOf(label) + label.lenght;
			startTitle = stdout.indexOf(escapeChar, startTitle);
			var endTitle = stdout.indexOf(escapeChar, startTitle + 1);
			var labelValue = stdout.substring(startTitle + 1, endTitle);
			// Find icon path

			// new stdout
			var stdout2 = stdout.substring(endTitle + 1, stdout.lenght);
			// sys.print(stdout2+'\n');
			var icon = 'icon';
			var startIcon = stdout2.indexOf(icon) + icon.lenght;
			startIcon = stdout2.indexOf(escapeChar, startIcon);
			var endIcon = stdout2.indexOf(escapeChar, startIcon + 1);
			var iconValue = stdout2.substring(startIcon + 1, endIcon);
		}
		if (stderr)
			sys.print('stderr: ' + stderr);
		else
			callback(labelValue, iconValue, fileApk);
	});
};
// package: name='it.eng.na' versionCode='16' versionName='1.0.1'
var findVersionsAndName = function(fileApk, callback) {
	exec(conf.aapt + " d badging " + fileApk + "  |grep package:",
			function(error, stdout, stderr) {
				if (stdout != null) {
					// Find app name
					var escapeChar = "\'";
					// if(stdout.indexOf(escapeChar)==-1);
					// escapeChar="\"";
					var packag = 'name';
					var startPackage = stdout.indexOf(packag) + packag.lenght;
					startPackage = stdout.indexOf(escapeChar, startPackage);
					var endPackage = stdout.indexOf(escapeChar,
							startPackage + 1);
					var packageValue = stdout.substring(startPackage + 1,
							endPackage);
					// Find icon path

					// new stdout
					var stdout2 = stdout.substring(endPackage + 1,
							stdout.lenght);
					// sys.print(stdout2+'\n');
					var versionCode = 'versionCode';
					var startVersionCode = stdout2.indexOf(versionCode)
							+ versionCode.lenght;
					startVersionCode = stdout2.indexOf(escapeChar,
							startVersionCode);
					var endVersionCode = stdout2.indexOf(escapeChar,
							startVersionCode + 1);
					var versionCodeValue = stdout2.substring(
							startVersionCode + 1, endVersionCode);

					var stdout3 = stdout2.substring(endVersionCode + 1,
							stdout2.lenght);
					// sys.print(stdout2+'\n');
					var versionName = 'versionName';
					var startVersionName = stdout3.indexOf(versionName)
							+ versionName.lenght;
					startVersionName = stdout3.indexOf(escapeChar,
							startVersionName);
					var endVersionName = stdout3.indexOf(escapeChar,
							startVersionName + 1);
					var versionName = stdout3.substring(startVersionName + 1,
							endVersionName);
				}
				if (stderr)
					sys.print('stderr: ' + stderr);
				else
					callback(packageValue, versionCodeValue, versionName,
							fileApk);
			});
};

var findMd5Sum = function(fileApk, callback) {
	exec(conf.md5sum + " " + fileApk, function(error, stdout, stderr) {
		if (stdout != null) {

			// sys.print('md5sum: ' + stdout.split(' ')[0] + '\n');
			var md5 = stdout.split(' ')[0];
			callback(md5, fileApk);
		}

	});
};

var listFilesApk = function(callBackFiles) {
	
	fs.readdir(conf.pathApk, function(err, files) {
		if (err)
			console.log(err);
		var result = new Array();

		for (x in files) {

			if (files[x].indexOf('.apk') > 0) {

				result.push(files[x]);
			}
		}
		callBackFiles(result);
	});
};

var extractIcon=function (fileApk,icon){
    console.log(conf.unzip+' ' +fileApk + ' -d '+conf.tmpDir);
    exec(conf.unzip+' -o ' +fileApk + ' -d '+conf.tmpDir,function(error, stdout, stderr){
    	console.log(stderr);
    	console.log(conf.mv+' '+conf.tmpDir+"/"+icon+' ' +conf.iconDir);
    	exec(conf.mv+' '+conf.tmpDir+"/"+icon+' ' +conf.iconDir+'/',function(error, stdout, stderr){
    		exec(conf.rm+' -rf '+conf.tmpDir+"/*");
    		
    	});	
    });
    
    
    
}

var insert = function(labelValue, iconValue, packageValue,
		versionCodeValue, versionName, md5,filename) {
	console.log("Insert "+labelValue + " " + iconValue + " " + packageValue + " "
			+ versionCodeValue + " " + versionName + " " + md5+" "+filename);
	var client = new pg.Client(dbconnection.connectionString);
	client.connect();
	var str = 'insert into applicazione values ($1,$2,$3,$4,$5,$6,$7);';
	var query = client.query(str, [ labelValue,iconValue, packageValue,
	                        		parseInt(versionCodeValue), versionName, md5,filename ]);
	query.on('error', function(err) {
		console.log('Connection error @ insert ' + err);
		
		
	});
	query.on('end', function() { 
		  client.end();
		});
};

var update = function(labelValue, iconValue, packageValue,
		versionCodeValue, versionName, md5,filename) {
	console.log("Update "+labelValue + " " + iconValue + " " + packageValue + " "
			+ versionCodeValue + " " + versionName + " " + md5+" "+filename);
	var client = new pg.Client(dbconnection.connectionString);
	client.connect();
	var str = 'update  applicazione SET  icon=$2, package_=$3, version_code=$4, version_name=$5, md5sum=$6, apkname=$7 where name=$1;';
	var query = client.query(str, [ labelValue,iconValue, packageValue,
	                                parseInt(versionCodeValue), versionName, md5, filename ]);
	
	query.on('error', function(err) {
		console.log('Connection error @ update ' + err);
		client.end();
		
	});
	query.on('end', function() { 
		  client.end();
		});
	console.log(JSON.stringify(query));
};

var select = function(labelValue,callback) {
	
	var client = new pg.Client(dbconnection.connectionString);
	client.connect();
	console.log("Select "+labelValue);
	var str = 'select name from  applicazione where name = $1;';
	var query = client.query(str, [ labelValue ]);
	var decoded = new Array();
	query.on('error', function(err) {
		console.log('Connection error @ select ' + err);
		client.end();
		callback();
	});
	
	query.on('row', function(row) {
        decoded.push(row.name);
    });

	query.on('end', function() {
		//logger.info('End EventPoint.list');
		client.end();
		callback(decoded);
	});
};

function start() {

	console.log('Start...');

	listFilesApk(function(files) {
		for (x in files) {
			var file = conf.pathApk + '/' + files[x];
			console.log(conf.pathApk);
			findMd5Sum(file, function(md5, f) {
				findVersionsAndName(f, function(packageValue, versionCodeValue,
						versionName, ff) {

					findLabelIcon(ff, function(labelValue, iconValue) {
						extractIcon(ff,iconValue);
						var startFile=ff.lastIndexOf('/');
						var filename=ff.substring(startFile+1,ff.lenght);
						
						select(labelValue,function (arrResult){
							if(arrResult&&arrResult.length>0)
								update(labelValue, iconValue, packageValue,
										versionCodeValue, versionName, md5,filename);
							else
								insert(labelValue, iconValue, packageValue,
										versionCodeValue, versionName, md5,filename);
						});
						

					});
				});
			});
		}
	});
};

// function puts(error, stdout, stderr) { sys.puts(stdout) }
// exec("ls -la", puts);
start();