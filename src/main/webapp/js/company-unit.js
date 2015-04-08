$(document).ready(getData());

function getData() {
	
	var id = _urlParams.id;
	if(!id) {
		bootbox.alert("No complex id found");
		return;
	}
	
	
	/** remove the header */
	$.ajax({url: "/api/v1/complex/" + id,error:_errorHandler})
	.then(function(data) {
		loadDataIntoModels(data);
	});
}



function handleNavigation(row) {
	document.location.href='unit-sensor.html?id=' + row.id;
}


var _dataModel;
var _companyId;
var _complexId;
var _availableGateways;

function loadDataIntoModels(data) {
	 function MyViewModel() {
		 
		 _companyId = data.company.id;
		 _complexId = data.complex.id;
		 
		 this.units = ko.observableArray(data.units['@items']);
		 this.complex = data.complex;
		 this.company = data.company;
		 this.networkStatus = data.networkStatus;
		 
		 
		 this.gateway = ko.observable(data.complex.gateway);  // {macAddress:'1234'};
		 // this.gateway = ko.observable();
		 this.selectGateway = function(x) {
			 showSelectGatewayDialog();
		 }
		 this.deleteRecord = function(x) {
			 verifyDelete('Complex', function() {
				 doDeleteRecord(x,"/api/v1/complex/delete/" + x.complex.id, function() {
				     history.go(-1);
				 });
			 });
		 }
		 
		 this.updateRecord = function(rec2Up) {
			 doUpdateRecord(rec2Up, "/api/v1/complex/update", function() {
				 showNotify("server updated");
				 $('#update-button').attr('disabled',true);
			 });
		 }		 
		 
		 this.showNetworkMap = function() {
			 _showNetworkMap('complex', 'Complex ' + _dataModel.complex.complexName, _dataModel.complex.id);
		 }

		 this.showNotifications = function() {
			 document.location.href = 'complex-contacts.html?id=' + _complexId;
		 }

	 }
	 _dataModel = new MyViewModel();
	 ko.applyBindings(_dataModel);
	 
	 _setupGrids();

}




function showSelectGatewayDialog() {
	$.ajax({url: "/api/v1/gateway/available/" + _complexId, error: _errorHandler})
	.then(function(data) {
		var gws = data['@items'];
		if(!gws) {
			gws = [];
		}
		gws[gws.length] = {id:0,sn:0,label: 'No Gateway'};
		
		$.ajax({url: "partials/select_gateway.html",error:_errorHandler})
		.then(function(html) {
			showSelectGateway(gws, html);
		});
		
	});
}
function showSelectGateway(gateways, html) {
	
    function MyViewModel() {
		 this.availableGateways = ko.observableArray(gateways);
		 this.selectedGateway = ko.observable();
	}
	var dataModel = new MyViewModel();
	 
	bootbox.dialog({
        title: 'Select Gateway' ,
        message: html,
        buttons: {
        	save: {
        		label: "Select",
        		callback: function(x) {
        			doSaveGateway(dataModel.selectedGateway(), function(x) {
        				
        				// add to existing model
        			    var newGateway = {id:x.id, sn:x.sn, macAddress: x.macAddress, ipAddress: x.ipAddress};
        			    _dataModel.gateway(newGateway);

        				bootbox.hideAll();	
        			});
        		}
        	},
        	cancel: {
        		label: "Cancel",
        		callback: function (x) {
        				return true;
        		}
        	}
        }
    });

	 ko.applyBindings(dataModel, document.getElementById('select-gateway'));
}


function doSaveGateway(x, callbackOnSuccess) {
	
	var update = {gateway: x, complex: _complexId};
	var dataJson = JSON.stringify(update);
	$.ajax({url: "/api/v1/complex/gateway/update", type: "POST", data: dataJson, error: _errorHandler})
	.then(function(data) {
		callbackOnSuccess(x);
	});
}


var _addFormHtml = null;
function createComplex() {

	if(_addFormHtml == null) {
		_addFormHtml = $('#add-form-div').html();
	    $('#add-form-div').html('');
	}
	
	bootbox.dialog({
        title: "Add Unit",
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
                    	var unitData = {
                    			complex: _dataModel.complex.id,
                    			unitNumber:$('[name=unitNumber]', e).val(),
                    			type:$('[name=type]', e).val(),
                    			beds:$('[name=beds]', e).val(),
                    			tenants:$('[name=tenants]', e).val(),	                    			
                    	};
                    	
                    	saveDataToServer(unitData, "/api/v1/unit/add", function(pk) {
                    		unitData.id = pk;
                    		_dataModel.units.push(unitData);
                    		bootbox.hideAll();
                    	});
            		}
            		
                	return false;
                }
            }
        }
    });
	
    $('#add-form').validate(
       {
    	   rules: {
               unitNumber: {
                   required: true
               },
       		   type: {
       			   
       		   },
               beds: {
            	   number: true
               },
               tenants: {
            	   number: true
               }
           }
	  });
       
}




function _showNotifications() {
	$.ajax({url: "/api/v1/complex/notifications/" + _complexId, error: _errorHandler})
	.then(function(data) {
		
		$.ajax({url: "partials/notifications.html",error:_errorHandler})
		.then(function(html) {
			showNotificationsDialog(data, html);
		});
		
	});
}
function showNotificationsDialog(notifications, html) {
	
    function MyViewModel() {
    	var data = {contacts: [{fullName:'test casey', phone:'123-123-1234', emailAddress:'abc west street'}]};
    	this.username = ko.observable('test');
    	this.contacts = ko.observableArray(data.contacts);
	}
	var dataModel = new MyViewModel();
	 
	bootbox.dialog({
        title: 'Notifications' ,
        message: html,
        buttons: {
        	close: {
        		label: "Close",
        		callback: function (x) {
        				return true;
        		}
        	}
        }
    });
	
	
	$('#complex-notifications td a').editable({
		type: 'text',
		title: 'Value',
		url: function(x) {alert('value set');}
	});
	
	 //make status editable
    $('#username').editable({
        type: 'select',
        title: 'Select status',
        placement: 'right',
        value: 2,
        url: function(x) {
        	alert('the value: ' + x);
        },
        source: [
            {value: 1, text: 'status 1'},
            {value: 2, text: 'status 2'},
            {value: 3, text: 'status 3'}
        ]
        /*
        //uncomment these lines to send data on server
        ,pk: 1
        ,url: '/post'
        */
    });
    
	 ko.applyBindings(dataModel, document.getElementById('complex-notifications'));
}


function doSaveNotify(x, callbackOnSuccess) {
	
	var update = {gateway: x, complex: _complexId};
	var dataJson = JSON.stringify(update);
	$.ajax({url: "/api/v1/complex/gateway/update", type: "POST", data: dataJson, error: _errorHandler})
	.then(function(data) {
		callbackOnSuccess(x);
	});
}



