<%@ page language="java" pageEncoding="utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/Translate.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><fmt:message key="flow.designer"/></title>
<script type="text/javascript" src="<c:url value='/common/jsptaglibs.js'/>"></script>

<script type="text/javascript">
	var  ed = null;
	function onInit(editor, isFirstTime)
	{
		ed = editor;
	}
	mxBasePath = '../flow/src';
</script>
<script type='text/javascript' src='<c:url value="/mxgraph/js/mxClient.js"/>'></script>
<script type='text/javascript' src='<c:url value="/mxgraph/js/mxApplication.js"/>'></script>


<script type="text/javascript">
var currentExcuteInstanceId;
var processOperate = {
	showProcessMap : function(){
		new mxApplication("src/config/diagrameditor.xml");
		/**读取静态xml文件显示流程图***/
		var _graph = ed.graph;
		_graph.setEnabled(false);
		_graph.tooltipHandler.setEnabled(false);

		var req = mxUtils.load("xml/HangYaoProcessChart.xml");
		var root = req.getDocumentElement();
		var dec = new mxCodec(root.ownerDocument);
		
		dec.decode(root, _graph.getModel());
	},
	startExcute : function() {
	},
	start : function(graph,currentInstanceId){
		document.getElementById("startExcute").disabled = true;
		document.getElementById("restart").disabled = true;
		document.getElementById("stop").disabled = false;

	},
	pouse : function(){
		document.getElementById("stop").disabled = true;
		document.getElementById("restart").disabled = false;
	},		
	update : function(graph, nodes)
	{
		if (nodes != null && nodes.length>0)
		{
			var model = graph.getModel();
			model.beginUpdate();
			try
			{
				var state = 'Init';
				for (var i = 0; i < nodes.length; i++)
				{
					var node = nodes[i];
					var id = node['id'];
					state = node['state'];
					
					// Gets the cell for the given activity name from the model
					var cell = model.getCell(id);
					// Updates the cell color and adds some tooltip information
					if (cell != null)
					{
						// Resets the fillcolor and the overlay
						
						//graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, 'white', [cell]);
						//graph.removeCellOverlays(cell);
	
						// Changes the cell color for the known states
						if (state == 'Running')
						{
							graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, '#FFFF10', [cell]);
						}
						else if (state == 'Error')
						{
							graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, 'red', [cell]);
							//var resObj = document.getElementById("showParamResponseDiv2");
							//resObj.innerHTML='<div align="center">出错了!<div>';//返回结果显示的地方
							//resObj.style.display = "block";
						}
						else if (state == 'Completed')
						{
							graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, '#2DFF2C', [cell]);
						}
						else if (state == 'Unknown')
						{
							graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, '#FFAF1B', [cell]);
						}
						// Adds tooltip information using an overlay icon
						if (state != 'Init')
						{
							// Sets the overlay for the cell in the graph
							graph.addCellOverlay(cell, processOperate.createOverlay(node,graph.warningImage,state));
						}
					}
				}
			}
			finally
			{
				model.endUpdate();
			}
		}
	},
	createOverlay : function(node,image, tooltip)
	{
		var overlay = new mxCellOverlay(image, tooltip,"center","middle");

		// Installs a handler for clicks on the overlay
		overlay.addListener(mxEvent.CLICK, function(sender, evt)
		{
			alert("node.state *****"+node.state);
			if(node.state == "Running"){
				alert(GetValueByKey("popup.the.interactive.interface"));
			}
			else{
				showRunStateDialog.show(this,node);
			}
		});
		
		return overlay;
	}
}

/**进度条控制***/
function showProcess(){
	var progressPercent = Math.ceil((uploadInfo.bytesRead / uploadInfo.totalSize) * 100);
	document.getElementById('progressBarText').innerHTML = GetValueByKey("finish.the.progress") + progressPercent + '%';
	document.getElementById('progressBarBoxContent').style.width = parseInt(progressPercent * 3.5) + 'px';
}
</script>
</head>

<body onload="processOperate.showProcessMap()">

<div id="graph" style="height:768px; width:1024px;"><br />
	<input type="button" value="<fmt:message key="Execute"/>" id="startExcute" onclick="processOperate.startExcute()"/>
	<input type="button" value="<fmt:message key="stop.access"/>" id="stop" onclick="processOperate.pouse()"/>
	<input type="button" value="<fmt:message key="continue.access"/>" id="reStart" onclick="processOperate.start()"/>
	<input type="hidden" id="hidShowdiv"/>
</div>

</body>
</html>