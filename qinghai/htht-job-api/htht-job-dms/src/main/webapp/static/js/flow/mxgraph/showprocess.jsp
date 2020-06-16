<%@ page language="java" pageEncoding="utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/Translate.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><fmt:message key="information.display.of.the.flow.chart"/></title>

<script type="text/javascript">
	var  ed = null;
	function onInit(editor, isFirstTime)
	{
		ed = editor;
	}
	mxBasePath = '../flow/src';
</script>
<script type='text/javascript' src='<c:url value="/flow/js/mxClient.js"/>'></script>
<script type='text/javascript' src='<c:url value="/flow/js/mxApplication.js"/>'></script>

<%-- <script type='text/javascript' src='<c:url value="/dwr/interface/ProcessManager.js"/>'></script> --%>
<%-- <script type='text/javascript' src='<c:url value="/dwr/interface/InstanceManager.js"/>'></script> --%>
<%-- <script type='text/javascript' src='<c:url value="/dwr/interface/ServiceExcute.js"/>'></script> --%>
<%-- <script type='text/javascript' src='<c:url value="/dwr/interface/AirRemoteLogic.js"/>'></script> --%>

<script type="text/javascript">
var currentExcuteInstanceId;
var processOperate = {
	showProcessMap : function(){
		new mxApplication("src/config/diagrameditor.xml");
		/**读取静态xml文件显示流程图***/
		var _graph = ed.graph;
		_graph.setEnabled(false);
		_graph.tooltipHandler.setEnabled(false);

/****/
		var req = mxUtils.load("xml/HangYaoProcessChart.xml");
		var root = req.getDocumentElement();
		var dec = new mxCodec(root.ownerDocument);
		
		dec.decode(root, _graph.getModel());
		
		
		/***调用后台显示流程图****/
		/**
		var processId = "{com.huadi.HangYaoTest1}HangYaoTest1-4"; 
		ProcessManager.getResultChartByPId(processId,function(obj) {
			var responseXML = obj.str;
			var dom = mxUtils.createStrToXmlDom(responseXML);
			var xml=dom.getElementsByTagName("mxGraphModel")[0].xml; 
			
			var _graph = ed.graph;
			_graph.setEnabled(false);
			_graph.tooltipHandler.setEnabled(false);
			
			var doc = mxUtils.parseXml(xml);
			var dec = new mxCodec(doc);
			dec.decode(doc.documentElement,_graph.getModel());
		});**/
		
	},
	startExcute : function() {
		/**数据库中的serviceKey**/
		var currentExcuteServiceKey = "DABA03D0-BFAF-11DF-83D0-9FE0B66143BA";
		var userId = "userId";
		var currentExcuteServiceAccessURL = "http://172.16.17.59:8088/pe/processes/HangYaoTest1";
		var paramValue = "abc";

		var xmlStr = '';
		xmlStr += '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:com="http://com.huadi.HangYaoTest1">';
		xmlStr += '<soapenv:Header/>';
		xmlStr += '<soapenv:Body>';
		xmlStr += '<com:HangYaoTest1mt>';
		xmlStr += '<com:request type="string">'+ paramValue +'</com:request>';
		xmlStr += '</com:HangYaoTest1mt>';
		xmlStr += '</soapenv:Body>';
		xmlStr += '</soapenv:Envelope>';
		
		/**生成随即的InstanceId**/
		var curdate = new Date();
		var _curtime = curdate.getTime()+"";
		var _timeNum = _curtime.substring(_curtime.length-6,_curtime.length-1);
		var _randomnum = parseInt(Math.random()*(99999-10000)+10000);
		var currentInstanceId = "1"+_timeNum + _randomnum;
		currentExcuteInstanceId = currentInstanceId;
		
		ServiceExcute.excuteService(currentExcuteServiceKey,userId,currentExcuteServiceAccessURL,xmlStr,currentInstanceId,function(reponseTree) {
			if (reponseTree) {
				
			} else {
				alert(GetValueByKey("service.call.fails"));
			}
		});

		/**获取状态**/
		processOperate.start(ed.graph,currentInstanceId);
	},
	start : function(graph,currentInstanceId){
		document.getElementById("startExcute").disabled = true;
		document.getElementById("restart").disabled = true;
		document.getElementById("stop").disabled = false;

		//alert("currentExcuteInstanceId:"+currentExcuteInstanceId);
		processOperate.timeInterval = setInterval(function(){
			try{
				InstanceManager.getActivityStateByAppointedId(currentExcuteInstanceId,function(nodes){
					if(null != nodes && nodes.length>0){
						for (var z = 0; z < nodes.length; z++)
						{
							var node = nodes[z];
							var _id = node['id'];
							var _name = node['name'];
							var _state = node['state'];
							if(_name =="reply" && _state == "Completed"){
								clearInterval(processOperate.timeInterval);/**停止获取**/
							}else{
								processOperate.update(graph,nodes);
							}
						}
					}
				});
			}catch(e){
				
			}
		},1000);
	},
	pouse : function(){
		document.getElementById("stop").disabled = true;
		document.getElementById("restart").disabled = false;
		clearInterval(processOperate.timeInterval);
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
					//$alert("update的数据：****************\n");
					//$alert("id:"+id+"***name:"+node['name']+"****state:"+state);
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

var showRunStateDialog = {
		show : function(elem,node){
			this.excuteElement = document.getElementById("hidShowdiv");
			this.loadData(node);
			if (!this.dialog) { 
				this.dialog = new hdht.BasicDialog("showServiceInfoDialog2", {
					autoTabs :true,
					width :700,
					height :500,
					shadow :true,
					minWidth :500,
					minHeight :300,
					proxyDrag :true,
					modal :true
				});				
				this.dialog.addButton(GetValueByKey("Cancel"), this.dialog.hide, this.dialog);
			}
			this.dialog.show(this.excuteElement);
		},
		loadData : function(node){
			var taskId = 100021;
			var showMessageDivObj = document.getElementById("showServiceInfoDiv");
			showMessageDivObj.innerHTML = "";
			showMessageDivObj.innerHTML = '<div align="center"><img src="../../common/images/bigloading.gif" alt="" align="top" width="150px" height="150px"/><br />"<fmt:message key="loading.information"/>"</div>';
			AirRemoteLogic.getDataProcessByTaskId(taskId,function(dataList){
				if(null != dataList){
					var html = '';
					
					
					html += '<table width="100%">';
					html += '<tr>';
					html += '<td>' +GetValueByKey("number.of.mine")+ '</td>';
					html += '<td>123#124</td>';
					html += '<td>'+GetValueByKey("start.time=")+'</td>';
					html += '<td>2010-9-15 17:41:27</td>';
					html += '</tr>';

					html += '<tr>';
					html += '<td>'+GetValueByKey("total.progredd")+'</td>';
					html += '<td>53%</td>';
					html += '<td>'+GetValueByKey("remaining.time")+'</td>';
					html += '<td>00:15:55</td>';
					html += '</tr>';
					
					html += '</table>';
					
					
					html += '<table cellpadding="0" cellspacing="0" class="noborder_table" width="100%">';

					html += '<tr>';
					html += '<th nowrap><b>'+GetValueByKey("node.name.")+'</b></th>';
					html += '<th nowrap>'+GetValueByKey("IP")+'</th>';
					html += '<th nowrap>'+GetValueByKey("number.of.image")+'</th>';
					html += '<th nowrap>'+GetValueByKey("completion.progress")+'</th>';
					html += '</tr>';			
					
					
					
					for(var i=0;i<dataList.length;i++){
						var _currentObj = dataList[i];
						html += '<tr>';
						html += '<td>'+_currentObj.nodeName+'</td>';
						html += '<td>'+_currentObj.ip+'</td>';
						html += '<td>'+_currentObj.imageNumber+'</td>';
						//html += '<td nowrap><hr style="height: 14px;" color="#8DC8F7" width="'+parseInt(_currentObj.progress)+'%"/>'+_currentObj.progress+'%</td>';
						
						var processStr = '';
						processStr += '<div id="theMeter">';
						//<!-- 提示信息 -->
		                //<!-- 进度 -->
						processStr +='<span style="width:'+(parseInt(_currentObj.progress)*2.8)+'px;height: 20px; border-right: 1px solid #444; background: #9ACB34;"></span>';
						//processStr += '<span>完成进度:'+_currentObj.progress+'%</span>';
						processStr += '<span>'+GetValueByKey("completion.progress")+ _currentObj.progress+ '%</span>';
						processStr += '</div>';

						html += '<td nowrap>'
						html += processStr;
						html += '</td>';
						html += '</tr>';
					}

					html += '</table>';
					showMessageDivObj.innerHTML = html;
				}else{					
					showMessageDivObj.innerHTML = GetValueByKey("no.message");
				}
			});
		}
};

/**进度条控制***/
function showProcess(){
	var progressPercent = Math.ceil((uploadInfo.bytesRead / uploadInfo.totalSize) * 100);
	document.getElementById('progressBarText').innerHTML = GetValueByKey("completion.progress") + progressPercent + '%';
	document.getElementById('progressBarBoxContent').style.width = parseInt(progressPercent * 3.5) + 'px';
}
</script>
</head>

<body onload="processOperate.showProcessMap()">

	<div id="graph" style="height:768px; width: 1024px;"><br />
		<input type="button" value="<fmt:message key="Execute"/>" id="startExcute" onclick="processOperate.startExcute()"/>
		<input type="button" value="<fmt:message key="stop.access"/>" id="stop" onclick="processOperate.pouse()"/>
		<input type="button" value="<fmt:message key="continue.access"/>" id="reStart" onclick="processOperate.start()"/>
		<input type="hidden" id="hidShowdiv"/>
	</div>



<!-- 显示服务信息  -->
<div id="showServiceInfoDialog2"	style="visibility: hidden; position: absolute;">
<div class="x-dlg-hd"><fmt:message key="description.information"/></div>
<div class="x-dlg-bd">
<div class="x-dlg-tab" title="<fmt:message key="description.information"/>">
	<div id="showServiceInfoDiv" class="gngl2"><div align="center"><img src="../../common/images/bigloading.gif" alt="" align="top" width="150px" height="150px"/><br /><fmt:message key="loading.information"/></div>
	</div>
</div>
</div>
</div>
</body>
</html>