$(document).ready(function() {
	getData();
});

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

function handleNavigation(row) {
	 var r = row.roleId;
	 if(_getRoleName(r) == 'Flow Timer') {
		 document.location.href='sensor-events.html?id=' + row.id;
	 }
	 else if(_getRoleName(r) == 'Repeater') {
		 document.location.href='repeater-events.html?id=' + row.id;
	 }
	 else {
		 showNotify('no such role: ' + r);
	 }
}

/** override global error handler aux function
 *  this will be caused during any server error
 *  allowing local routines to handle specific msgs.
 * @param ex
 */
_errorCallback = function(ex) {
    if(ex.toLowerCase().indexOf('duplicate') > -1) {
    	showNotify('Serial number is already in use.');
    }
    else {
    	showAlert('Could not save sensor.');
    }
};


// for location services
var _complexId;
var _companyId;

var _dataModel;
function loadDataIntoModels(dataIn) {
	 function MyViewModel(data) {
		 
		 _complexId = data.complex.id;
		 _companyId = data.company.id;
		 
		 this.availableSensors = ko.observableArray(dataIn.availableSensors['@items']); 
		 this.availableSensorsText = function(x) {
			 return x.sn + ' (' + _getRoleName(x.role) + ')';
		 } 
		 this.selectedSensor = ko.observable();
		 
		 this.sensors = ko.observableArray(data.sensors['@items']);
		 this.company = data.company;
		 this.complex = data.complex;
		 this.unit = data.unit;
		 this.networkStatus = data.networkStatus;
		 this.rowClicked = function(x) {
			 handleNavigation(x);
		 }
		 this.deleteRecord = function(x) {
			 verifyDelete('Unit', function() {
				 doDeleteRecord(x, "/api/v1/unit/delete/" + _dataModel.unit.id, 
						 function() { _gotoApp_Complex() });
			 });
		 }
		 
		 this.updateRecord = function(rec2Up) {
			 doUpdateRecord(rec2Up, "/api/v1/unit/update", function() {
				 showNotify("server updated");
				 $('#update-button').attr('disabled',true);
			 });
		 }
	 
		 this.showNetworkMap  = function() {
			 _showNetworkMap('unit', 'Unit ' + _dataModel.unit.unitNumber,  _dataModel.unit.id);
		 }
	 }
	 _dataModel = new MyViewModel(dataIn);
	 ko.applyBindings(_dataModel);
	 
	 _setupGrids();
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
                	
            		var e = $('#add-form');
            		if($('#add-form').valid()) {
                    	var sensorData = {
                    			unit: _dataModel.unit.id,
                    			role: _dataModel.selectedSensor().role,
                    			roleLabel: _getRoleName(_dataModel.selectedSensor().role),
                    			sensor:_dataModel.selectedSensor().sn
                    	};
                    	var serverUrl = "/api/v1/sensor/add";
                    	saveDataToServer(sensorData, serverUrl, function(pk) {
                    		sensorData.id = pk;
                    		_dataModel.sensors.push(sensorData);
	                    	bootbox.hideAll();
                		}); 
            		}
                	return false;
                	
                }
            }
        }
    });
	
	ko.applyBindings(_dataModel, document.getElementById('add-form'));
	
    $('#add-form').validate(
       {
    	   rules: {
       		   role: {
       			   required: true
       		   },
               sensor: {
            	   required: true,
            	   number: true
               }
           }
	  });
       
}
