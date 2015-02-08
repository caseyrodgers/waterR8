$(document).ready(
		getData()
);

function getData() {
	
	var id = _urlParams.id;
	if(!id) {
		bootbox.alert("No unit id found");
		return;
	}
	
	$.ajax({url: "/api/v1/sensor/" + id, error: _errorHandler})
	.then(function(data) {
		loadDataIntoModels(data);
	});
}

var _dataModel;
function loadDataIntoModels(dataIn) {
	 function MyViewModel(data) {
		 this.events = ko.observableArray(data.events['@items']);
		 this.sensor = data.sensor;
		 this.company = data.company;
		 this.complex = data.complex;
		 this.unit = data.unit;
		 this.networkStatus = data.networkStatus;
		 this.rowClicked = function(x) {
			 document.location.href='sensor-events.html?id=' + x.id;
		 }
		 this.deleteSensor = function() {
			 verifyDelete('Sensor', function() {
				 doDeleteRecord(_dataModel.sensor.id, "/api/v1/sensor/delete/" + _dataModel.sensor.id, function() {
					 history.go(-1);	
				 	});
			 });
		 }
		 this.availableRoles  = ['Sensor', 'Repeater'];
	 }
	 _dataModel = new MyViewModel(dataIn);
	 ko.applyBindings(_dataModel);
}


var _addFormHtml = null;
function createComplex() {

	if(_addFormHtml == null) {
		_addFormHtml = $('#add-form-div').html();
	    $('#add-form-div').html('');
	}
	
	bootbox.dialog({
        title: "Add Sensor",
        message: _addFormHtml,
        buttons: {
        	cancel: {
        		label: "Cancel"
        	},
            success: {
                label: "Save",
                className: "btn-success",
                callback: function () {
                	$('form').submit(function(x) {
                		
                		var e = $('#add-form');
                		
                		if($('#add-form').valid()) {
	                    	var data = {
	                    			role:$('[name=role]', e).val(),
	                    			sensor:$('[name=sensor]', e).val()
	                    	};
	                    	
	                    	_dataModel.sensors.push(data);
	                    	
	                    	bootbox.hideAll();
	                    	return false;
                		}
                		else {
                			return false;
                		}
                	});
                	$('#add-form').submit();
                	return false;
                }
            }
        }
    });
	
    $('#add-form').validate(
       {
    	   rules: {
       		   role: {
       			   required: true
       		   },
               sensor: {
            	   required: true
               }
           }
	  });
       
}
