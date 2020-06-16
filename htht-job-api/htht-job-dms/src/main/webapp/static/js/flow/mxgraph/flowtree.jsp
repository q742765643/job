<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/common/Translate.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><fmt:message key="flow.management"/></title>
<link rel="stylesheet" type="text/css" href='<c:url value="/javascript/splitdiv/splitdiv.css" />'></css>
<script type='text/javascript' src='<c:url value="/javascript/splitdiv/splitdiv.js" />'></script>
<script type='text/javascript' src='<c:url value="/javascript/skytree/Script/SkyTree.js" />'></script>
<script type='text/javascript' src='<c:url value="/dwr/interface/InstanceManager.js"/>'></script>
<script type='text/javascript' src='<c:url value="/dwr/interface/ProcessManager.js"/>'></script>
<script type='text/javascript' src='<c:url value="/dwr/interface/InstanceManagerMonitorById.js"/>'></script>

<script type='text/javascript' src='js/flowtree.js'></script>
<script type='text/javascript' src='js/instancelist.js'></script>
<script type='text/javascript' src='js/mxApplication.js'></script>

<script type="text/javascript">
	var  ed = null;
	mxBasePath = 'src';
	function onInit(editor, isFirstTime)
	{
		ed = editor;
	}
	
</script>
<script type='text/javascript' src='js/mxClient.js'></script>

</head>

<body>
<div>
	<div id="leftTree" class="left_tree" style="WIDTH: 25%;height:600px;float: left;">
		<div class="gridLeft" id="ziyuanshu"><fmt:message key="structure.of.the.flow.tree"/></div>
		<div id="flow_tree" style="height: 568px; width: 100%; overflow: auto" nowrap><img src="../../common/images/loading.gif" alt="" /><fmt:message key="loading"/></div>
	</div>
	
	<!--滑块辅助-->
	<div id="split_hide" class="hidesplit"></div>
	<!--滑块-->
	<div id="split" class="split"></div>
	<div id="rigthDiv" class="rightdiv" style="height:598px;width: 712px;">
			<div class="gridLeft" id="xiangqing"><fmt:message key="graphic.display"/>   </div>
			<div id="graph" style="height: 368px; width: 100%; overflow: auto;"><br /></div>
			<!--滑块辅助-->
			<div id="split_h_hide" class="hidesplit_h"></div>
			<!--滑块-->
			<div id="split_h" class="split_h" style="display: none;"></div>
			<!--下侧表格-->
			<div id="methodDesc" class="downdiv" style="height: 200px;"></div>
	</div>
</div>
</body>
</html>