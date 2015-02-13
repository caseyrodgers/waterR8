$(document).ready(
		getData()
);

function getData() {
	$.ajax({url: "/api/v1/companies", error: _errorHandler})
	.then(function(data) {
		loadDataIntoModels(data);
	});

	$('#registration-form').validate({
		
	    rules: {
	       name: {
	         required: true
 	       },
		  
		 username: {
	        minlength: 6,
	        required: true
	      },
		  
		  password: {
				required: true,
				minlength: 6
			},
			confirm_password: {
				required: true,
				minlength: 6,
				equalTo: "#password"
			},
		  
	        email: {
	          required: true,
	          email: true
	      },
		  
	     
		   address: {
	      	minlength: 10,
	        required: true
	      },
		  
		  agree: "required"
		  
	    },
			highlight: function(element) {
				$(element).closest('.control-group').removeClass('success').addClass('error');
			},
			success: function(element) {
				element
				.text('OK!').addClass('valid')
				.closest('.control-group').removeClass('error').addClass('success');
			}
	  });	
	
	
}


var _companyData;
function loadDataIntoModels(data) {
	 function MyViewModel() {
		 this.companies = ko.observableArray(data['@items']);
		 this.deleteCompany = function(x) {
			 alert('deleting company: ' + x);
		 }
		 this.companyClicked = function(x) {
			 document.location.href='company-complex.html?id=' + x.id;
		 }
	 }
	 _companyData = new MyViewModel();
	 ko.applyBindings(_companyData);
}



function deleteCompany(companyToDelete) {
	bootbox.confirm("Are you sure you want to delete this company?", function(result) {
	  if(result) {
		  _companyData.companies.remove(companyToDelete);
	  }
	}); 
}

var _addCompanyHtml = null;

function createCompany(companyToDelete) {

	if(_addCompanyHtml == null) {
	    _addCompanyHtml = $('#company-add-form-div').html();
	    $('#company-add-form-div').html('');
	}
	
	bootbox.dialog({
        title: "Add Company",
        show: true,
        message: _addCompanyHtml,
        buttons: {
        	cancel: {
        		label: "Cancel"
        	},
            submit: {
                label: "Save",
                className: "btn-success",
                callback: function () {
            		if($('#company-add-form').valid()) {
                		var e = $('#company-add-form');
                    	var company = {
                    			companyName:$('[name=company]', e).val(),
                    			owner:$('[name=owner]',e).val(),
                    			address:$('[name=address]', e).val(),
                    	        city:$('[name=city]',e).val(),
                    	        state: $('[name=state]', e).val(),
                    	        zip:$('[name=zip]', e).val()
                    	};
                    	saveDataToServer(company, function(pk) {
                    		company.id = pk;
                        	_companyData.companies.push(company);
                    		bootbox.hideAll();
                    	});
                    	return false;
            		}
            		else {
            			return false;
            		}                		
                	
                	return false;
                }
            }
        }
    });
	
    $('#company-add-form').validate(
       {
    	   rules: {
               company: {
                   
               },
               city: {
            	   
               },
               state: {
            	   
               },
               zip: {
            	   number: true
               }
           }
	  });
}


function saveDataToServer(record, callbackOnComplete) {
	var dataJson = JSON.stringify(record);
	$.ajax({url: "/api/v1/company/add", type: "POST", data: dataJson})
	.then(function(data) {
		var pk=data.primaryKey;
		callbackOnComplete(pk);
	});
}