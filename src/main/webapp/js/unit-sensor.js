$(document).ready(
		getData()
);

function getData() {
	
	var id = _urlParams.id;
	if(!id) {
		bootbox.alert("No unit id found");
		return;
	}
	
	$.ajax({url: "/api/v1/unit/" + id})
	.then(function(data) {
		loadDataIntoModels(data);
	});
}

var _dataModel;
function loadDataIntoModels(dataIn) {
	 function MyViewModel(data) {
		 this.sensors = ko.observableArray(data.sensors['@items']);
		 this.company = data.company;
		 this.complex = data.complex;
		 this.unit = data.unit;
		 this.rowClicked = function(x) {
			 document.location.href='sensor-events.html?id=' + x.id;
		 }
		 this.deleteUnit = function(x) {
			 verifyDelete('Unit', function() {
				 doDeleteRecord(x, "/api/v1/unit/delete/" + _dataModel.unit.id, 
						 function() { history.go(-1);});
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
	                    	var sensorData = {
	                    			unit: _dataModel.unit.id,
	                    			role:$('[name=role]', e).val(),
	                    			sensor:$('[name=sensor]', e).val()
	                    	};
	                    	var serverUrl = "/api/v1/sensor/add";
	                    	saveDataToServer(sensorData, serverUrl, function(pk) {
	                    		sensorData.id = pk;
	                    		_dataModel.sensors.push(sensorData);
		                    	bootbox.hideAll();
                    		}); 
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
