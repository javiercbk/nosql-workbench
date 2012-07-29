var schema = require('../models/Schema.js');
/*
  Error handling
*/
function NotFound(msg){
  this.name = 'NotFound';
  Error.call(this, msg);
  Error.captureStackTrace(this, arguments.callee);
}

NotFound.prototype.__proto__ = Error.prototype;

/*
 * GET home page.
 */
exports.index = function(req, res){
  res.render('index', { title: 'SUBE' , error: false});
};

/*
 * GET home page.
 */
exports.home = function(req, res){
  res.render('home', { title: 'SUBE - HOME', dataEntry: req.session.user});
};

exports.getSubeCard = function(req, res){
  res.render('subeCard', { title: ''});
};

exports.getProvider = function(req, res){
	res.render('provider', { title: ''});
}

exports.postSubeCard = function(req, res){
  var post = req.body;
  var physicalPersonTemplate = {firstName: post.firstName, lastName: post.lastName, idNumber: post.idNumber, documentType: post.documentType};
  var subeCardTemplate = {user: physicalPersonTemplate, balance: post.balance};
  subeCardTemplate.dataEntry = req.session.user._id;
  var subeCard = new schema.SubeCard(subeCardTemplate);
  subeCard.create(function(err){
    if(!err){
      res.render('subeCardNumber', {number: subeCard.number});
    }else{
      res.send(err);
    }
  });
};

exports.postProvider = function(req, res) {
	var post = req.body;
	var legalPersonTemplate = {legalName: post.legalName, legalLocation: post.legalLocation, fantasyName: post.fantasyName, cuit: post.cuit};
	var providerTemplate = {providerType: post.providerType, providerName: post.providerName, location: post.location, legalPerson: legalPersonTemplate};
	var provider = new schema.Provider(providerTemplate);
	provider.save(function(err){
		if(!err){
			res.json({success: true})
    }else{
      res.json({success: false, error: err});
    }
	});
}

exports.login = function (req, res) {
  var post = req.body;
  var dataEntry = new schema.DataEntry({physicalPerson : {idNumber : post.idNumber, documentType : post.documentType}, password : post.password});
  dataEntry.findByCredentials(function(err, dataEntries){
    if(err){
      /** DB ERROR **/
      return res.end("WTF...Database Failed " + err)
    }

    if(dataEntries.length == 1){
      var dataEntry = dataEntries[0]
      req.session.user = dataEntry;
      res.redirect('/home');
    }else {
      res.render('index', {title: 'SUBE', 'error' : true});
    }
  });
};

exports.logout = function (req, res) {
  delete res.session.user;
  res.redirect('/index');
};

/* WARNING: ONLY FOR TESTING*/
exports.postDataEntry = function(req, res) {
  var post = req.body;
  var dataEntry = new schema.DataEntry({physicalPerson : {idNumber : post.idNumber, documentType : post.documentType, firstName: post.firstName, lastName: post.lastName}, password : post.password});
  dataEntry.save(function(err){
    if(err){
      res.send('Bad user/pass');
    }else{
      res.send('Bad user/pass');
    }
  });
};

exports.notFound = function(req, res){
  throw new NotFound;
};

exports.internalServerError = function(req, res){
  throw new Error('keyboard cat!');
};

exports.getDataEntry = function(req, res) {
  var idNumber = req.param.idNumber;
  if(idNumber) {
    var dataEntry = new schema.DataEntry({physicalPerson: {idNumber: idNumber}});
    dataEntry.findByidNumber(function(error, dataEntry) {
      if(error) {
	return res.end("WTF...Database Failed " + err);
      }
      res.render('dataEntry', { dataEntry: dataEntry, title: 'SUBE: Data Entry'});
    });
  }
  res.render('dataEntry', {dataEntry: null, title: 'SUBE: Data Entry'});
};

var dataEntryAction = function(req, res, callback) {
  var post = req.body;
  var parsedPhysPer = {firstName: post.firstName, lastName: post.lastName, idNumber: post.idNumber, documentType: post.documentType};
  var parsedDataEntry = {password: post.password, physicalPerson : parsedPhysPer};
  var dataEntry = new schema.DataEntry(parsedDataEntry);
  dataEntry.save(function(err){
    if(err){
      return res.end("WTF...Database Failed " + err);
    }
    callback(res, this.emitted.complete[0]);
  });
}

exports.postDataEntry = function(req, res) {
  dataEntryAction(req, res, function(response, dataEntry){
    response.contentType('application/json');
    response.send(JSON.stringify(dataEntry));
  });
};

exports.updateDataEntry = function(req, res) {
  dataEntryAction(req, res, function(response, dataEntry){
    response.contentType('application/json');
    response.send(JSON.stringify(dataEntry));
  });
};
exports.deleteDataEntry = function(req, res) {
  var post = req.body;
  var dataEntry = new schema.DataEntry({_id: post.id});
  dataEntry.findOne(function(err, dataEntry){
    if(err){
      return res.end("WTF...Database Failed " + err);
    }
    dataEntry.remove(function(err){
      if(err){
	return res.end("WTF...Database Failed " + err);
      }
      res.contentType('application/json');
      res.send(JSON.stringify(this));
    });
  });
};