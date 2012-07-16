function(key, values) {
	var result = 0;
	values.forEach(function(value) {
		result += value.money;
	});
	return result;
};