<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/Translate.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
/* 左右分割条样式 */
.lrSeparator {
   background-color:#549FE3;
   cursor:e-resize;
   font-size : 1px;
}
/* 上下分割条样式 */
.udSeparator {
   background-color:#549FE3;
   cursor:s-resize;
   font-size : 1px;
}
</style>
<title><fmt:message key="flow.management"/></title>
<!--页面样式和弹出窗口样式 -->
<link rel="stylesheet" type="text/css" href='<c:url value="/css/css.css"/>'/>
<link rel="stylesheet" type="text/css" href='<c:url value="/css/tableList.css"/>'/>

<link rel="stylesheet" type="text/css" href='<c:url value="/javascript/splitdiv/splitdiv.css" />'></css>
<script type='text/javascript' src='<c:url value="/javascript/splitdiv/splitdiv.js" />'></script>
<script type='text/javascript' src='<c:url value="/dwr/interface/InstanceManager.js"/>'></script>
<script type='text/javascript' src='<c:url value="/dwr/interface/ProcessManager.js"/>'></script>
<script type='text/javascript' src='js/instancelist.js'></script>
</head>
<body onload="searchInitProcess()">
<form action="#" method="post" name="instanceListForm">
<div class="search"> 
		<div class="search_top"></div>
		<div class="search_middle">
			<div class="searchnav">
				<span><fmt:message key="instance.ID="/></span>
				<ul><input type="text" name="instanceSelectStr" id="instanceSelectStr" value="" class="txt" /></ul>
				<span><fmt:message key="flow.ID"/></span>
				<ul><input type="text" name="instancePidSelectStr" id="instancePidSelectStr" value="" class="txt" />
				</ul>
				<input name="" type="button"  onmouseover="this.className='btna'" onmouseout="this.className='btn'" class="btn" value="&nbsp;&nbsp;&nbsp;<fmt:message key="query"/>" onclick="javascript:displayAllInstance();"/>
			</div>
		</div>
		<div class="search_bottom"></div>
	</div>

<div class="content">
<div class="key"><fmt:message key="your.location"/><a href="#"><fmt:message key="home"/></a> > <a href="#"><fmt:message key="management.of.data.resource"/></a> ><fmt:message key="execution.log.of.flow"/> </div>
<div class="btngroup1">
	<ul>
		<li><img src="<c:url value='/common/images/position.gif'/>" alt="" /><a href="../service/serviceResultManage.jsp");"><font color="red"><B><fmt:message key="invoke.and.display.the.service"/></B></font></a></li>
		<li><img src="<c:url value='/common/images/position.gif'/>" alt="" /><font color="black"><fmt:message key="invoke.and.display.the.flow"/></font></li>
	</ul>
	<span></span>
</div>
<div id="gridCon" class="tabelBox">
	
</div>

<div class="btngroup2">
	<ul>
		<li><input id="all_sel"	onclick="javascript:checkbox.selectAll(this,'selectBox',true)" type="button"  onmouseover="this.className='inputbuttona1'" onmouseout="this.className='inputbutton1'" class="inputbutton1" value="<fmt:message key="select.all"/>"/></li>
		<li><input onclick="javascript:checkbox.convertSelect('selectBox','all_sel')" type="button"  onmouseover="this.className='inputbuttona2'" onmouseout="this.className='inputbutton2'" class="inputbutton2" value="<fmt:message key="inverse.select"/>"/></li>
		<li><input id="deleteCoredata" type="button" onmouseover="this.className='inputbuttona'" onmouseout="this.className='inputbutton'" class="inputbutton" value="<fmt:message key="delete"/>">
		</li>
	</ul>
	<div id="gridTools" class="gridbar" ></div> 
</div>
</div>

<div id="flowGraphShowDialog" style="visibility: hidden; position: absolute;">
<div class="x-dlg-hd" id="flowShowPid"><fmt:message key="display.of.the.flow.chart"/></div>
<div class="x-dlg-bd"  id="flowShowPid2">
<div class="x-dlg-tab" title="<fmt:message key="display.of.the.flow.chart"/>">


	
	<div id="graph" class="updiv"></div>
	<!--滑块-->
	<div id="split" class="split_h" style="display: none;"></div>

	<!--滑块辅助-->
	<div id="split_hide" class="hidesplit_h"></div>

	<!--右侧表格-->
	<div id="methodDesc" class="downdiv" style="height: 130px;"></div>
	



</div>
</div>
</div>

</form>
<script type="text/javascript">
	var ed;
	function onInit(editor, isFirstTime){
		ed = editor;
	}
</script>
<script type='text/javascript' src='js/mxApplication.js'></script>
<script type='text/javascript' src='js/mxClient.js'></script>
</body>
</html>