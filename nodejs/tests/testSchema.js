var schema = require('../models/Schema.js')
, should = require('../node_modules/should')
, validationHelper = require('./mongooseValidationHelper.js');


describe('with none of its required fields filled in', function() {
  var error = {};
  beforeEach(function() {
    var customer = new schema.SubeCard();
    customer.save(function(err) {
      error = err;
    });
  });
  it('should fail with validation errors for each required field', function() {
    should.exist(error);
    validationHelper.checkRequiredValidationErrorFor(error, 'balance');
    validationHelper.checkRequiredValidationErrorFor(error, 'number');
    validationHelper.checkRequiredValidationErrorFor(error, 'user');
    validationHelper.checkRequiredValidationErrorFor(error, 'dataEntry');
  });
});