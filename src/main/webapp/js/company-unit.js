$(document).ready(
		getData()
);

function getData() {
	
	var id = _urlParams.id;
	if(!id) {
		bootbox.alert("No complex id found");
		return;
	}
	
	$.ajax({url: "/api/v1/complex/" + id})
	.then(function(data) {
		loadDataIntoModels(data);
	});
}

var _dataModel;
function loadDataIntoModels(data) {
	 function MyViewModel() {
		 this.units = ko.observableArray(data.units['@items']);
		 this.companyName = data.company.companyName;
		 this.rowClicked = function(x) {
			 document.location.href='unit-sensor.html?id=' + x.id;
		 }
	 }
	 _dataModel = new MyViewModel();
	 ko.applyBindings(_dataModel);
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
                	$('form').submit(function(x) {
                		
                		var e = $('#add-form');
                		if($('#add-form').valid()) {
	                    	var data = {
	                    			unitNumber:$('[name=unitNumber]', e).val(),
	                    			type:$('[name=type]', e).val(),
	                    			beds:$('[name=beds]', e).val(),
	                    			tenants:$('[name=tenants]', e).val(),	                    			
	                    	};
	                    	
	                    	_dataModel.units.push(data);
	                    	
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
               unitNumber: {
                   required: true
               },
       		   type: {
       			   required: true
       		   },
               beds: {
            	   required: true,
            	   number: true
               },
               tenants: {
            	   required: true,
            	   number: true
               }
           }
	  });
       
}
