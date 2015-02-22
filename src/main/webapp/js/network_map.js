$(document).ready(
		getData()
);

function getData() {
	
	var id = _urlParams.id;
	if(!id) {
		bootbox.alert("No company id found");
		return;
	}
	
	$.ajax({url: "/api/v1/company/network/" + id, error: _errorHandler})
	.then(function(data) {
		loadNetworkGraph(data);
	});
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
			   
			  var networkMapHtml="<canvas id='network-graph-canvas' width='600px' height='680px' />";
			  $('#network-graph').html(networkMapHtml);
			  
				
			  var springy = window.springy = jQuery('#network-graph-canvas').springy({
			    graph: graph,
			    nodeSelected: function(node){
			      console.log('Node selected: ' + JSON.stringify(node.data));
			    }
			  });
			});	
			
}
