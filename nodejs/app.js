var express = require('express')
, routes = require('./routes')
, config = require('yaml-config')
//gzip static responses
//, gzippo = require('gzippo')
//yaml config
, config = require('yaml-config')
//internationalization
, i18n = require('i18n')
, mongoSessionStore = require('./config/mongoSessionStore').mongoSessionStore;

//var settings = config.readConfig('config/app.yaml')

i18n.configure({
    // setup some locales - other locales default to en silently
    locales:['en', 'es'],
});

var app = module.exports = express.createServer();

// Configuration

app.configure(function(){
  app.set('views', __dirname + '/views');
  app.set('view engine', 'jade');
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(express.cookieParser());
  //FIXME very basic example
  app.use(express.session({store: mongoSessionStore, secret:'checreto'}));
  //i18n
  app.use(i18n.init);
  app.use(app.router);
  //Replace the default connect or express static provider with gzippo's
  app.use(express.static(__dirname + '/public'));
  //app.use(gzippo.staticGzip(__dirname + '/public'));
});

app.locals({
  __i: i18n.__,
  __n: i18n.__n
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
  app.set('view options', { pretty: true });
});

app.configure('production', function(){
  app.use(express.errorHandler());
});

//DataEntry Authorization check
function checkAuth(req, res, next) {
  if (!req.session.user) {
    res.send('You are not authorized to view this page');
  } else {
    next();
  }
}

//user Authorization check
function checkUserAuth(req, res, next) {
  if (!req.session.card) {
    res.send('You are not authorized to view this page');
  } else {
    next();
  }
}

// Routes

app.get('/', routes.index);

//testing 404
app.get('/404', routes.notFound);

//testing 500
app.get('/500', routes.internalServerError);

/* Data entry routes */ 
app.post('/login', routes.login);

app.get('/logout', routes.logout);

/* WARNING: ONLY FOR TESTING*/

app.get('/dataEntry', routes.getDataEntry);

app.get('/dataEntry/:idNumber', routes.getDataEntry);

app.post('/dataEntry', routes.postDataEntry);

app.post('/updateDataEntry', routes.updateDataEntry);

app.post('/deleteDataEntry', routes.deleteDataEntry);

/* END OF WARNING */

app.get('/home', checkAuth, routes.home);

app.get('/profile', checkAuth, routes.getProfile);

app.post('/createSubeCard', checkAuth, routes.postSubeCard);

app.post('/createProvider', checkAuth, routes.postProvider);

app.get('/getProvider/:idProv', checkAuth, routes.getProvider);

app.get('/subeCard/:numCard', checkAuth, routes.getSubeCard);

app.post('/profile', checkAuth, routes.updateProfile);

app.post('/picture', checkAuth, routes.uploadPicture);

app.get('/provider', checkAuth, routes.getProvider);

app.get('/subeCard', checkAuth, routes.getSubeCard);

/* User routes */
//TODO user everyauth to authenticate user
//app.post('/loginUser', routes.loginUser);

app.get('/newComplain', checkUserAuth, routes.newComplain);

app.post('/submitComplain', checkUserAuth, routes.submitComplain);


app.listen(3000, function(){
  console.log("Express server listening on port %d in %s mode", 3000, app.settings.env);
});
