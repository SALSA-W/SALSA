/**
 * 
 */

$(document).ready(function() {
	
	(function poll() {
		$.ajax({
			url : 'ProcessChecker?' + getUrlParameter('id'),
			type: "GET",
			success : function(responseText) {				
				// TODO - redirect to correct page
				
			},
			error: function(){
				// TODO - manage errors
	            alert("Processes doesn't exists");
	        },
			
	        // Polling: http://stackoverflow.com/questions/6835835/jquery-simple-polling-example
			complete: setTimeout(function() {poll()}, 5000),
	        timeout: 2000,
		});
	})();
	
});

function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
}  