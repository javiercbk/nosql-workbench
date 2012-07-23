var db = require('../models/subeMongoDB').db
, MongoDBSessionStore = require('connect-mongodb');

var mongoSessionStore = new MongoDBSessionStore({db: db});

exports.mongoSessionStore = mongoSessionStore;