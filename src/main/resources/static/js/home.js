$(function() {
	
	if ($(".selectable").length) {
		$(".selectable").selectable();
	}
	
	if ($("#modalAWSSettings").length) {
		$("#modalAWSSettings").modal();
	}

	//
	//$("formAWSSettings").on("submit", function(e) {
	//   e.preventDefault();
	//    $.post(this.action, $(this).serialize());
	//});
	
	

});
