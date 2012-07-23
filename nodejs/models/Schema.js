var mongoose = require('../node_modules/mongoose')
, Schema = mongoose.Schema
, ObjectId = Schema.ObjectId
, mongodb = require('../node_modules/mongodb');
var db = mongoose.connect('mongodb://localhost/sube');

var dataEntry = new Schema({
  physicalPerson: {firstName: {type: String, required: true, index: true},
		   lastName: {type: String, required: true, index: true},
		   idNumber: {type: Number, required: true, index: true},
		   documentType: {type: String, required: true}},
  password: {type: String, required: true}
});

mongoose.model('DataEntry', dataEntry);

var provider = new Schema({
  location: {type: String, required: true, index: true},
  providerType: {type: String, required: true},
  providerName: {type: String, required: true, index: true},
  legalPerson: {legalName: {type: String, required: true, index: true},
		fantasyName: {type: String, required: true, index: true},
		cuit: {type: Number, required: true, index:{unique: true}},
		legalLocation: {type: String, required: true}}
});

mongoose.model('Provider', provider);

var subeCard = new Schema({
  balance: {type: Number, required: true, default: 0},
  number: {type: Number, required: true, index: {unique: true}},
  user: {firstName: {type: String, required: true, index: true},
	 lastName: {type: String, required: true, index: true},
	 idNumber: {type: Number, required: true, index: true},
	 documentType: {type: String, required: true}},
  dataEntry: {type: Schema.ObjectId, required: true}
});

mongoose.model('SubeCard', subeCard);

var subeCardUsage = new Schema({
  subeCard: {type: Schema.ObjectId, required: true, index: true},
  provider: {type: Schema.ObjectId, required: true, index: true},
  datetime: {type: Date, required: true, index: true},
  money: {type: Number, required: true},
});

mongoose.model('SubeCardUsage', subeCardUsage);

exports.SubeCard = mongoose.model('SubeCard');
exports.SubeCardUsage = mongoose.model('SubeCardUsage');
exports.DataEntry = mongoose.model('DataEntry');
exports.Provider = mongoose.model('Provider');