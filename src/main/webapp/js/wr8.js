var _urlParams={};window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(str,key,value){_urlParams[key] = value;});

$(document).ready(function(){
    // have update button enabled on keydown
	 $('#info-form input').on('keydown',function() {
		 $('#update-button').attr('disabled', false);
	 })
	 
	 $('#info-form select').on('click',function() {
		 $('#update-button').attr('disabled', false);
	 })
	 

	 /** setup role based security */
	 // only show the Companies link if admin
	 if(readCookie('role') != 'admin') {
		 $('#company-bread').css('display','none');
		 
		 makeAllReadonly();
	 }
});



function makeAllReadonly() {
	$('#info-form input').attr('readonly', 'true');
	$('#info-form select').attr('readonly', 'true');
	$('#info-form textarea').attr('readonly', 'true');
	$('.panel-footer').html('');  // remove edit buttons
}



function readCookie(name) {
    var nameEQ = encodeURIComponent(name) + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) return decodeURIComponent(c.substring(nameEQ.length, c.length));
    }
    return null;
}

function doLogout() {
	document.location.href = '/logout';
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


function doUpdateRecord(data, restUrl, callback) {
	var dataJson = JSON.stringify(data);
	$.ajax({url: restUrl, type: "POST", data: dataJson, error: _errorHandler})
	.then(function(data) {
		callback();
	});
}



function _errorHandler(xObj, type, e) {
	bootbox.alert("There was a problem talking with the server.  Please relogin.");
	document.location.href='/';
}


function _showNetworkMapForCompany() {
	_netDataModel = null;
	_showNetworkMap('company',_dataModel.company);
}

function _showNetworkMap(type, company) {
	$.ajax({url: 'partials/network_map_list.html', type: "GET",  error: _errorHandler})
	.then(function(data) {
		_showNetworkMap_Aux(data, type, company);
	});
}

function _showNetworkMap_Aux(dialogHtml, type, company) {

	var title = 'Repeater Network for ' + company.companyName;
	var message = dialogHtml;
	
	bootbox.dialog({
        title: title ,
        message: message,
        buttons: {
        	cancel: {
        		label: "Close",
        		callback: function () {
        			_netDataModel = null;	
        		}
        	}
        }
    });
	
	var id = company.id;
	$.ajax({url: "/api/v1/" + type + "/network/" + id, error: _errorHandler})
	.then(function(data) {
		loadServerData(data);
	});
}


function getNetworkMapDataForSelectedSequence(sequence) {
	var dataJson = JSON.stringify(sequence);
	
	$('#network-graph').html('Loading sequence: ' + sequence.seq);

	$.ajax({url: "/api/v1/company/network/" + _companyId, type: "POST", data: dataJson, error: _errorHandler})
	.then(function(data) {
		loadServerData(data);
	});	
}


function getNodeMatching(graph, nodes, nodeIn) {
	for(var i=0;i<nodes.length;i++) {
		var n = nodes[i];
		if(n.key == nodeIn.key) {
			return n;
		}
	}
	
	var srcVal = nodeIn.src;
	var nodeToAdd = graph.newNode({label: nodeIn.label, src: srcVal });
	nodeToAdd.key = nodeIn.key
	nodeToAdd.subLabel = nodeIn.subLabel;
	nodes[nodes.length] = nodeToAdd;
	return nodeToAdd;
}


var _netDataModel;
var _sequenceNumbers;
var _allRepeaters;
var _currentSequence;


function _lookupRepeaterIdFromSrc(src) {
	for(var i=0;i<_allRepeaters.length;i++) {
		var r = _allRepeaters[i];
		if(r.sensor == src) {
			return r.id;
		}
	}
	return null;
}

function loadServerData(data) {
	
	resolveRefs(data);
	
	data.networkNodes = data.networkNodes['@items'];
	_sequenceNumbers = data.sequenceNumbers['@items'];
	_allRepeaters = data.allRepeaters['@items'];
	_currentSequence = data.currentSequence;
	
	
	if(_sequenceNumbers) {
		for(var x=0;x<_sequenceNumbers.length;x++) {
		    var sequence = _sequenceNumbers[x];
			sequence.devicesThatResponded = sequence.devicesThatResponded['@items'];
		}
		
		addRepeaterLabelForSequence(_allRepeaters,_sequenceNumbers, _currentSequence );
	}
	else {
		//
	}
	
	if(_netDataModel == null) {
	    function MyViewModel(data) {
			 this.sequenceNumbers = ko.observableArray(_sequenceNumbers);
			 this.currentSequence = ko.observable(data.currentSequence);
			 this.selectedSequence = _sequenceNumbers?_sequenceNumbers[0]:null;
			 
			 
			 this.allRepeaters = ko.observableArray(_allRepeaters);
			 
			 
			 this.rowClicked = function(x) {
				 alert('row clicked');
			 }
		 }
		 _netDataModel = new MyViewModel(data);
		 var node = document.getElementById('network-graph-wrapper');
		 ko.applyBindings(_netDataModel, node);
		
		 $(node).on('change', function(x) {
			 // alert('sequence changed: ' + _netDataModel.selectedSequence.seq);
			 getNetworkMapDataForSelectedSequence(_netDataModel.selectedSequence);
		 });
	}
	else {
		addRepeaterLabelForSequence(_allRepeaters,_sequenceNumbers, _currentSequence );
	}
	
	var graph = new Springy.Graph();

	var nn = data.networkNodes;
	
	buildNetworkGraph(graph, nn);
	
	jQuery(function(){
		   
		  var networkMapHtml="<canvas id='network-graph-canvas' width='540' height='480' />";
		  $('#network-graph').html(networkMapHtml);

		  $('#network-graph-canvas').on('dblclick', function(){
			  var repeaterId = _lookupRepeaterIdFromSrc(_selectedNode.data.src);
			  if(repeaterId) {
			      document.location.href = 'repeater-events.html?id=' + repeaterId;
			  }
			  else {
				  showNotify("Only repeater nodes are clickable");
			  }
	      });
			
		  var springy = window.springy = jQuery('#network-graph-canvas').springy({
		    graph: graph,
		    nodeSelected: function(node){
		    	_selectedNode = node;
		      console.log('Node selected: ' + node.id + ", " + JSON.stringify(node.data));
		      // document.location.href='/sensor-events.html?src=' + node.data.src;
		    }
		  });
		});	
	
	
	return;

}



function addRepeaterLabelForSequence(allRepeaters,sequenceNumbers, currentSequence ) {
	
	if(!sequenceNumbers) {
		return;
	}
	
	var sequence = null;
	for(var x=0;x<sequenceNumbers.length;x++) {
		if(sequenceNumbers[x].seq == currentSequence) {
			sequence = sequenceNumbers[x];
			break;
		}
	}
	
	if(sequence == null) {
		alert('could not find sequence: ' + currentSequence);
	}
	
	for(var i=0;i<allRepeaters.length;i++) {
		var r = allRepeaters[i];
		r.repeaterLabel = 'Did not respond';
		
		for(var j=0;j<sequence.devicesThatResponded.length;j++) {
			var s = sequence.devicesThatResponded[j];
			if(s.sensor.sensor == r.sensor) {
				r.repeaterLabel ='Responded: h:' + s.hopCnt + ",r:" + s.rssiRcv + ",%:" + r.respondPercent	;
				break;
			}
		}
	}
	
}

function buildNetworkGraph(graph, nn) {
	
	if(!nn) {
		return;
	}
	
	var nodes = [];
	for(var i=0;i<nn.length;i++) {
		var n = nn[i];

		var child = getNodeMatching(graph, nodes, n.deviceChild);
		var isRoot = n.deviceParent.label.toLowerCase() == 'root';
		var parent=null;
		if(!isRoot) {
			 parent = getNodeMatching(graph, nodes, n.deviceParent);
			 
			 var lineColor='#00A0B0';
			 switch(n.deviceChild.type.name) {
			     case 'COMPANY':
			    	 lineColor='black';
			    	 break;
			    	 
			     case 'COMPLEX':
			    	 lineColor='green';
			    	 break;
			    	 
			     case 'UNIT':
			    	 lineColor='blue';
			    	 break;
			    	 
			     case 'SENSOR':
			    	 lineColor='red';
			    	 break;
			    	 
			 }
			 graph.newEdge(parent, child, {color: lineColor});
		}
	}	
}


var _selectedNode;



function showNotify(msg) {
	 $('.notifications').notify({
		    message: { text: msg },
		    fadeOut: { enabled: true, delay: 3000 }
		  }).show();
}



function _gotoApp_Complex() {
	var loc = "&rand=" + new Date().getTime();
	var url = '/company-unit.html?id=' + _complexId + loc;
	document.location.href = url;
}

function _gotoApp_Unit() {
	var loc = "&rand=" + new Date().getTime();
	var url = '/unit-sensor.html?id=' + _unitId + loc;
	document.location.href = url;
}

function _gotoApp_Company() {
	var loc = "&rand=" + new Date().getTime();
	var url = '/company-complex.html?id=' + _companyId + loc;
	document.location.href = url;
}

function _gotoPreviousPage() {
	var backLocation = document.referrer;
	if (backLocation) {
	    if (backLocation.indexOf("?") > -1) {
	        backLocation += "&randomParam=" + new Date().getTime();
	    } else {
	        backLocation += "?randomParam=" + new Date().getTime();
	    }
	    window.location.assign(backLocation);	
	}
}


function ___showNetworkMap() {
	document.location.href='network_map.html?id=' + _companyId;
}