$(document).ready(
		getData()
);

function getData() {
	var companyId = _urlParams.id;
	if(!companyId) {
		bootbox.alert("No company id found");
		return;
	}
	
	$.ajax({url: "/api/v1/company/" + companyId, error: _errorHandler})
	.then(function(data) {
		loadDataIntoModels(data);
	});
}

var _detailData;
function loadDataIntoModels(data) {
	
	 function MyViewModel() {
		 this.complexes = ko.observableArray(data.complexes['@items']);
		 this.company = data.company;
		 this.networkStatus = data.networkStatus;

		 this.rowClicked = function(x) {
			 document.location.href='company-unit.html?id=' + x.id;
		 };
		 this.deleteRecord = function(rec2Del) {
			 verifyDelete('Company', function() {
				 doDeleteRecord(rec2Del, "/api/v1/company/delete/" + data.company.id, function() {
					 history.go(-1);
				 });
			 })
		 };
		 
		 this.updateRecord = function(rec2Up) {
			 doUpdateRecord(rec2Up, "/api/v1/company/update", function() {
				 showNotify("server updated");
				 $('#update-button').attr('disabled',true);
			 });
		 }
		 
		 this.showNetworkMap = function() {
			 _showNetworkMap('company', _detailData.company.companyName, _detailData.company.id);
		 }
	 }
	 
	 _detailData = new MyViewModel();
	 ko.applyBindings(_detailData);
}



var _addComplexHtml = null;
function createComplex() {

	if(_addComplexHtml == null) {
		_addComplexHtml = $('#complex-add-form-div').html();
	    $('#complex-add-form-div').html('');
	}
	
	bootbox.dialog({
        title: "Add Complex",
        message: _addComplexHtml,
        buttons: {
        	cancel: {
        		label: "Cancel"
        	},
            success: {
                label: "Save",
                className: "btn-success",
                callback: function () {
                		
            		var e = $('#complex-add-form');
            		if($('#complex-add-form').valid()) {
                    	var record = {
                    			company: _detailData.company.id,
                    			complexName:$('[name=complexName]', e).val(),
                    			address: $('[name=address]', e).val(),
                    			city: $('[name=city]', e).val(),
                    			state: $('[name=state]', e).val(),
                    			zip: $('[name=zip]', e).val(),
                    			phone: $('[name=phone]', e).val(),
                    			email: $('[name=email]', e).val(),
                    			buildingCount: $('[name=buildingCount]', e).val(),
                    			constructionType: $('[name=constructionType]', e).val(),
                    			floorType: $('[name=floorType]', e).val(),
                    			lotSize: $('[name=lotSize]', e).val(),
                    			floors: $('[name=floors]', e).val(),
                    			notes: $('[name=notes]', e).val()
                    	};
                    	
                    	saveDataToServer(record, "/api/v1/complex/add", function(pk) {
                    		record.id = pk;
                    		_detailData.complexes.push(record);
                    		bootbox.hideAll();
                    	});
            		}
            		
            	    return false;
            }
        }
    }});
	
    $('#complex-add-form').validate(
       {
    	   rules: {
               complexName: {
            	   required: true
               },
               address: {
               },
               city: {
               },
               state: {
               },
               zip: {
            	   number: true
               },
               phone: {
               },
               email: {
            	   email: true
               },
               buildingCount: {
            	   number: true
               },   
               constructionType: {
               },
               floorType: {
               },
               lotSize: {
            	   number: true
               },
               floors: {
            	   number: true
               }               
               
           }
	  });
       
}
