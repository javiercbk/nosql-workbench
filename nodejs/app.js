var express = require('express')
, routes = require('./routes')
, config = require('yaml-config')
//gzip static responses
, gzippo = require('gzippo')
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
  //app.use(express.static(__dirname + '/public'));
  app.use(gzippo.staticGzip(__dirname + '/public'));
  //    app.use(express.static(__dirname + '/public'));
});

app.helpers({
  __i: i18n.__,
  __n: i18n.__n
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
});

app.configure('production', function(){
  app.use(express.errorHandler());
});

//DataEntry Authorization check
function checkAuth(req, res, next) {
  if (!req.session.user_id) {
    res.send('You are not authorized to view this page');
  } else {
    next();
  }
}

//user Authorization check
function checkUserAuth(req, res, next) {
  if (!req.session.card_id) {
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

app.get('/home', checkAuth, routes.home);

app.get('/profile', checkAuth, routes.profile);

app.get('/newCard', checkAuth, routes.newSubeCard);

app.get('/newProvider', checkAuth, routes.newProvider);

app.get('/getProvider/:idProv', checkAuth, routes.getProvider);

app.get('/getSubeCard/:numCard', checkAuth, routes.getSubeCard);

app.post('/updateProfile', checkAuth, routes.updateProfile);

app.post('/uploadPicture', checkAuth, routes.uploadPicture);

app.post('/inputProvider', checkAuth, routes.inputProvider);

app.post('/inputSubeCard', checkAuth, routes.inputSubeCard);

/* User routes */
//TODO user everyauth to authenticate user
//app.post('/loginUser', routes.loginUser);

app.get('/newComplain', checkUserAuth, routes.newComplain);

app.post('/submitComplain', checkUserAuth, routes.submitComplain);


app.listen(3000, function(){
  console.log("Express server listening on port %d in %s mode", app.address().port, app.settings.env);
});
