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
  res.render('index', { title: 'Express' })
};

exports.login = function (req, res) {
  var post = req.body;
  //check in mongo
  var dataEntry = new schema.DataEntry();
  dataEntry.where('physicalPerson.idNumber', post.idNumber)
    .where('physicalPerson.documentType', post.documentType)
    .where('password', post.password)
    .limit(1)
    .callback(function(err, dataEntry){
      if(err){
	/** DB ERROR **/
	return res.end("WTF...Database Failed")
      }
      if(dataEntry){
	req.session.user_id = dataEntry._id;
	res.redirect('/home');
      }else {
	res.send('Bad user/pass');
      }
    });
};

exports.logout = function (req, res) {
  delete res.session.user_id;
  res.redirect('/index');
};

exports.notFound = function(req, res){
  throw new NotFound;
};

exports.internalServerError = function(req, res){
  throw new Error('keyboard cat!');
};