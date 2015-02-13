var _urlParams={};window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(str,key,value){_urlParams[key] = value;});

$(document).ready(function(){
    // have update button enabled on keydown
	 $('#info-form input').on('keydown',function() {
		 $('#update-button').attr('disabled', false);
	 })
	 
	 $('#info-form select').on('click',function() {
		 $('#update-button').attr('disabled', false);
	 })
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


function doUpdateRecord(data, restUrl, callback) {
	var dataJson = JSON.stringify(data);
	$.ajax({url: restUrl, type: "POST", data: dataJson, error: _errorHandler})
	.then(function(data) {
		callback();
	});
}



function _errorHandler(xObj, type, e) {
	bootbox.alert("There was a problem talking with the server.  Your can try again or visit the Help page.");
}



function _showNetworkMap(type, title, id) {

	bootbox.dialog({
        title: "Network Map for " + title ,
        message: "<div id='network-graph'><h3>Loading Network Graph ...</h3></div>",
        buttons: {
        	cancel: {
        		label: "Close"
        	}
        }
    });

	$.ajax({url: "/api/v1/" + type + "/network/" + id, error: _errorHandler})
	.then(function(data) {
		loadNetworkGraph(data);
	});
}


function getNodeMatching(graph, nodes, nodeIn) {
	for(var i=0;i<nodes.length;i++) {
		var n = nodes[i];
		if(n.key == nodeIn.key) {
			return n;
		}
	}
	
	var nodeToAdd = graph.newNode({label: nodeIn.label });
	nodeToAdd.key = nodeIn.key
	nodeToAdd.subLabel = nodeIn.subLabel;
	nodes[nodes.length] = nodeToAdd;
	return nodeToAdd;
}

function loadNetworkGraph(data) {
	
	resolveRefs(data);
	
	var graph = new Springy.Graph();

	var nn = data.networkNodes['@items'];
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
	
	   
	jQuery(function(){
		   
		  var networkMapHtml="<canvas id='network-graph-canvas' width='540' height='680' />";
		  $('#network-graph').html(networkMapHtml);
		  
			
		  var springy = window.springy = jQuery('#network-graph-canvas').springy({
		    graph: graph,
		    nodeSelected: function(node){
		      console.log('Node selected: ' + JSON.stringify(node.data));
		    }
		  });
		});	
	
	
	return;

}





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