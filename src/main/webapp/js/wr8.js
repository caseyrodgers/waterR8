var _urlParams={};window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(str,key,value){_urlParams[key] = value;});

$(document).ready(function(){
    var _myModel;
    var _cnt=0;
	function doIt() {
		var HelloWorldModel = function() {
			this.hello = new ko.observable('test test');
			_cnt++;
			this.cnt = _cnt;
		}

		var MyBindings = function(items, testItems) {
		    this.items = ko.observableArray(items);
		    this.testItems = ko.observableArray(testItems);
		    this.itemToAdd = ko.observable("");
		    
		    this.helloView = new HelloWorldModel();
		    this.addItem = function() {
		        if (this.itemToAdd() != "") {
		            this.items.push(this.itemToAdd()); // Adds the item. Writing to the "items" observableArray causes any associated UI to update.
		            this.itemToAdd(""); // Clears the text box, because it's bound to the "itemToAdd" observable
		        }
		        this.testItems.push(new HelloWorldModel());
		        
		    }.bind(this);  // Ensure that "this" is always this view model
		};
		ko.applyBindings(new MyBindings(["Alpha", "Beta", "Gamma"]));
	}
});

function doLogout() {
	document.location.href = 'index.html';
}

function checkLogin() {
	document.location.href='companies.html';
	return false;
}



function verifyDelete(thing, callback) {
	bootbox.confirm('Are you sure you want to delete this ' + thing + '?', function(x) {
		if(x) {
			callback();
		}
	});
}


function saveDataToServer(record, serverUrl, callbackOnComplete) {
	var dataJson = JSON.stringify(record);
	$.ajax({url: serverUrl, type: "POST", data: dataJson, error: _errorHandler})
	.then(function(data) {
		var pk=data.primaryKey;
		callbackOnComplete(pk);
	});
}


function doDeleteRecord(data, deleteUrl, callback) {
	$.ajax({url: deleteUrl, error: _errorHandler})
	.then(function(data) {
		callback();
	});
}




function _errorHandler(xObj, type, e) {
	bootbox.alert("There was a problem talking with the server.  Your can try again or visit the Help page.");
}