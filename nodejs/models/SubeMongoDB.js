var Db = require('../node_modules/mongodb').Db;
//var config = require('../lib/yaml-config')
var Server = require('../node_modules/mongodb').Server;

//var settings = config.readConfig('../config/app.yaml', 'test');
var serverConfig = new Server('localhost', 27017, {auto_reconnect: true, native_parser: false});
var mongoDatabase = new Db('sube', serverConfig, {});

exports.db = mongoDatabase;