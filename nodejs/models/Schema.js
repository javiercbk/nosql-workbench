var mongoose = require('../node_modules/mongoose')
, Schema = mongoose.Schema
, ObjectId = Schema.ObjectId
, mongodb = require('../node_modules/mongodb');
var db = mongoose.connect('mongodb://localhost/sube');

var Counters = new Schema({
  _id:String, // the schema name
  count: Number
});

Counters.statics.findAndModify = function (query, sort, doc, options, callback) {
    return this.collection.findAndModify(query, sort, doc, options, callback);
};

var Counter = mongoose.model('Counter', Counters);

/**
 * Increments the counter associated with the given schema name.
 * @param {string} schemaName The name of the schema for which to
 *   increment the associated counter.
 * @param {function(err, count)} The callback called with the updated
 *   count (a Number).
 */
function incrementCounter(schemaName, callback){
  Counter.findAndModify({ _id: schemaName }, [],
    { $inc: { amount: 1 } }, {"new":true, upsert:true}, function (err, result) {
      if (err)
        callback(err);
      else
        callback(null, result.amount);
  });
}

var dataEntry = new Schema({
  physicalPerson: {firstName: {type: String, required: true, index: true},
		   lastName: {type: String, required: true, index: true},
		   idNumber: {type: Number, required: true, index: true},
		   documentType: {type: String, required: true}},
  password: {type: String, required: true}
});

dataEntry.methods.findByCredentials = function (callback) {
  return this.model('DataEntry').find({password: this.password, "physicalPerson.idNumber": this.physicalPerson.idNumber,  "physicalPerson.documentType": this.physicalPerson.documentType}, callback);
}

var provider = new Schema({
  location: {type: String, required: true, index: true},
  providerType: {type: String, required: true},
  providerName: {type: String, required: true, index: true},
  legalPerson: {legalName: {type: String, required: true, index: true},
		fantasyName: {type: String, required: true, index: true},
		cuit: {type: Number, required: true, index:{unique: true}},
		legalLocation: {type: String, required: true}}
});

var subeCard = new Schema({
  balance: {type: Number, required: true, default: 0},
  number: {type: Number, required: true, index: {unique: true}},
  user: {firstName: {type: String, required: true, index: true},
	 lastName: {type: String, required: true, index: true},
	 idNumber: {type: Number, required: true, index: true},
	 documentType: {type: String, required: true}},
  dataEntry: {type: Schema.ObjectId, ref: 'dataEntries', required: true}
});

subeCard.methods.create = function (callback) {
  var subeCard = this;
  var incrementCallback = function(err, newNumber) {
    if(!err){
      subeCard.number = newNumber;
      subeCard.save(callback);
    }
  };
  incrementCounter('subeCards', incrementCallback);
}

var subeCardUsage = new Schema({
  subeCard: {type: Schema.ObjectId, ref: 'subeCards', required: true, index: true},
  provider: {type: Schema.ObjectId, ref: 'providers', required: true, index: true},
  datetime: {type: Date, required: true, index: true},
  money: {type: Number, required: true},
});

exports.SubeCard = mongoose.model('SubeCard', subeCard, 'subeCards');
exports.SubeCardUsage = mongoose.model('SubeCardUsage', subeCardUsage, 'subeCardUsages');
exports.DataEntry = mongoose.model('DataEntry', dataEntry, 'dataEntries');
exports.Provider = mongoose.model('Provider', provider, 'providers');