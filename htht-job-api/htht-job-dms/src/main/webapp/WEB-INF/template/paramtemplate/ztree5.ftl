<!DOCTYPE html>
<html>
<head>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/zTree/zTreeStyle/zTreeStyle.css">

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="row">
    <div class="col-sm-2" style="width: 640px; height: 280px; overflow: auto;">
        <ul class="ztree" id="tree" >

        </ul>
    </div>
</div>
<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/plugins/zTree/jquery.ztree.all.min.js"></script>
<script>
    var setting = {
        check : {
            enable : true,
            chkboxType: { "Y": "", "N": "ps" }
        },
        data : {
            simpleData : {
                enable : true
            }
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            onClick: zTreeOnClick,
            beforeCheck:zTreeBeforeCheck,
            onCheck:onCheck
        }
    };
    $.ajax({
        type : "POST",
        url : base_url +  "/folderscan/getRootToJsonWithConfig",
        dataType : 'json',
        success : function(msg) {
            $.fn.zTree.init($("#tree"), setting, msg);
        }
    });
    
     function zTreeBeforeCheck(treeId, treeNode) {
        return !treeNode.isParent;<#--当是父节点 返回false 不让选取-->
   	}
     function onCheck(e,treeId,treeNode){
     		<#--alert(treeNode.id);-->
            var treeObj=$.fn.zTree.getZTreeObj("tree"),
            
            nodes=treeObj.getCheckedNodes(true),
            v="";
            for(var i=0;i<nodes.length;i++){
            	v+=nodes[i].id + ",";
            	<#--console.log("节点id:"+nodes[i].id+"节点名称"+v); //获取选中节点的值-->
            }
            v=v.substr(0, v.length - 1);
            <#--alert(v);-->
            <#--$(parent.inputId).val(v);-->
            
            window.parent.arrPie=v;
            window.parent.say2();
            
            $(parent.inputId).val(v);
        	$(parent.inputId).attr("checked","true");
        	
        	
     }

    function zTreeOnClick(event, treeId, treeNode) {
    	var treeObj = $.fn.zTree.getZTreeObj("tree");
        $(parent.inputId).val(treeNode.id);
        var sNodes = treeObj.getSelectedNodes();
        if (sNodes.length > 0) {
			var zAsync = sNodes[0].zAsync;
			if(zAsync){
				treeObj1.reAsyncChildNodes(treeNode, "refresh");
			}else{
				$.ajax({
	            url: base_url + "/folderscan/getChildrenFiles4",//请求的action路径
	            type:"post",
	            data:{"pid":treeNode.id,"dataSource":treeNode.dataSource},
	            error: function () {//请求失败处理函数
	                alert('请求失败');
	            },
	            success:function(data)
	            { //添加子节点到指定的父节点
	                if(data == null || data == ""){
	                }
	                else{
	                    treeNode.halfCheck = false;
	                    newNode = treeObj.addNodes(treeNode,data, false);
	                    sNodes[0].zAsync = true;
	                }
	            }
	       		});
			}
		}

    };
    
    
</script>


</body>
</html>
