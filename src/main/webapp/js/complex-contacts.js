$(document).ready(
		getData()
);

function getData() {
	
	var id = _urlParams.id;
	if(!id) {
		bootbox.alert("No complex id found");
		return;
	}
	
	$.ajax({url: "/api/v1/complex/contacts/" + id})
	.then(function(data) {
		loadDataIntoModels(data);
	});
}

// for location services
var _complexId;
var _companyId;

var _dataModel;
function loadDataIntoModels(dataIn) {
	 function MyViewModel(data) {
		 _complexId = data.complexId;
		 _companyId = data.companyId;
		 
		 this.fullName = '';
		 this.emailAddress = '';
		 this.phone = '';
		 this.notifyOnError = '';
		 
		 this.complexName = data.complexName;
		 
		 var cons = dataIn.contacts['@items'];
		 cons = cons?cons:[];
		 this.contacts = ko.observableArray();
		 for(var i=0;i<cons.length;i++) {
			 var contact = cons[i];
			 
			 var c = {};
			 c.id = contact.id;
			 c.fullName = ko.observable(contact.fullName);
			 c.phone = ko.observable(contact.phone);
			 c.emailAddress = ko.observable(contact.emailAddress);
			 
			 c.notifyOnError = ko.observable(contact.notifyOnError);
			 
			 this.contacts.push(c);
		 }
		 
		 
		 this.rowClicked = function(x) {
			 editContact(x);
		 }
		 this.deleteRecord = function(x) {
			 verifyDelete('Contact', function() {
				 doDeleteRecord(x, "/api/v1/complex/contact/delete/" + _dataModel.unit.id, 
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
}


function editContact(c) {
	
var formHtml = _getFormHtml();
	bootbox.dialog({
        title: "Edit Contact",
        message: formHtml,
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
                    	var contactData = {
                    			complex: _complexId,
                    			id: c.id,
                    			fullName: c.fullName(),
                    			phone: c.phone(),
                    			emailAddress:c.emailAddress(),
                    			notifyOnError: c.notifyOnError()
                    	};
                    	var serverUrl = "/api/v1/complex/contacts/update";
                    	saveDataToServer(contactData, serverUrl, function(pk) {
                    		showNotify("Contact updated");
	                    	bootbox.hideAll();
                		}); 
            		}
                	return false;
                }
            }
        }
    });

	_setEditFormBindings(c);
}


function createContact() {

	
	var formHtml = _getFormHtml();
	
	function ViewModel() {
		this.fullName = ko.observable();
		this.phone = ko.observable();
		this.emailAddress = ko.observable();
		this.notifyOnError = ko.observable(true);
		
		this.rowClicked = function(x) {
			alert("x: " + x);
		}
	}
	var dataModel = new ViewModel();
	
	bootbox.dialog({
        title: "Add Contact",
        message: formHtml,
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
                    	var contactData = {
                    			complex: _complexId,
                    			fullName: dataModel.fullName(),
                    			phone: dataModel.phone(),
                    			emailAddress:dataModel.emailAddress(),
                    			notifyOnError: dataModel.notifyOnError()
                    	};
                    	var serverUrl = "/api/v1/complex/contacts/add";
                    	saveDataToServer(contactData, serverUrl, function(pk) {
                    		dataModel.id = pk;
                    		_dataModel.contacts.push(dataModel);
	                    	bootbox.hideAll();
                		}); 
            		}
                	return false;
                }
            }
        }
    });
	_setEditFormBindings(dataModel)
}




function _setEditFormBindings(dataModel) {
	ko.applyBindings(dataModel, document.getElementById('add-form'));
    $('#add-form').validate(
       {
    	   rules: {
       		   fullName: {
       			   required: true
       		   },
               phone: {
            	   required: false
               },
               emailAddress: {
            	   required: false,
            	   email: true
               }
           }
	  });	
}

var _addFormHtml = null;
function _getFormHtml() {
	if(_addFormHtml == null) {
		_addFormHtml = $('#add-form-div').html();
	    $('#add-form-div').html('');
	}	
	return _addFormHtml;
}