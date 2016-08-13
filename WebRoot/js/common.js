function postUrl(url, args) {
	var form = $("<form method='post'></form>"), input;
	form.attr({
		"action" : url
	});
	$.each(args, function(key, value) {
		input = $("<input type='hidden'>");
		input.attr({
			"name" : key
		});
		input.val(value);
		form.append(input);
	});
	form.submit();
}
function showToast(content) {
	var $toast = $('#toast');
	if ($toast.css('display') != 'none') {
		return;
	}
	$("#toastcontent").text(content);
	$toast.show();
	setTimeout(function() {
		$toast.hide();
	}, 1000);
}