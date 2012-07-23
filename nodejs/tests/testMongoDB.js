var assert = require('assert')
, db = require('../models/subeMongoDB').db;

var SimpleProvider = function(db){
  this.db = db;
};

describe('Simple mongo test', function(){
  describe('showDatabases', function(){
    it('should show databases', function(done){
      var simpleProvider = new SimpleProvider(db);
      simpleProvider.db.open(function(err, pClient) {
	db.createCollection('test', function(err, collection){
	  assert.equal('test', collection.collectionName);
	  done();
	});
      });
    })
  })
});