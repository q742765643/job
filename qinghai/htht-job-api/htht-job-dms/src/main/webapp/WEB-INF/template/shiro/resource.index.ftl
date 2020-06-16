<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/zTree/zTreeStyle/zTreeStyle.css">

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">

<!-- Content Wrapper. Contains page content -->
<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>资源管理
        <small>
    		<#if Session.tileList?? && (Session.tileList?size > 0) >
	        	<#list Session.tileList as titleDic>
                    <#if titleDic.dictCode== "bigTitle">
                        ${titleDic.dictName}
                    </#if>
                </#list>
            <#else>
	 			并行支撑平台
            </#if>
        </small>
    </h1>

</section>

<!-- Main content -->
<section class="content">
    <div class="row">
        <div class="col-sm-2">
            <ul class="ztree" id="tree" >

            </ul>
        </div>
        <div class="col-sm-10">
            <div class="row">
                <div class="col-xs-2">
                        <input type="text" class="form-control" id="searchText" autocomplete="on" placeholder="按资源名称搜索...">
                </div>
                <div class="col-xs-2">
                    <button class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp</button>
                </div>
                <div class="col-xs-1">
                    <@shiro.hasPermission name="resourceadd">
                    <button class="btn btn-primary btn-success add" >添加资源</button>
                    </@shiro.hasPermission>
                </div>
            </div>
            <p></p>
            <div class="box">
                <div class="box-body">
                    <table id="table" class="table table-bordered table-striped"
                           style="word-break:break-all;">

                    </table>
                </div>
            </div>
        </div>

    </div>


</section>
<!-- /.content -->

<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">添加资源</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">资源名称</label>
                        <div class="col-sm-4">
                            <input  name="name" class="form-control" type="text"  />
                            <input  name="parentId" class="form-control" type="hidden"  />

                        </div>
                        <label for="modelName" class="col-sm-2 control-label">资源key</label>
                        <div class="col-sm-4">
                            <input  name="sourceKey" class="form-control" type="text"  />

                        </div>
                    </div>
                    <div class="form-group">

                        <label for="lastname" class="col-sm-2 control-label">类型</label>
                        <div class="col-sm-4">
                            <select name="type" class="form-control">
                                <option value="0">目录</option>
                                <option value="1">菜单</option>
                                <option value="2">按钮</option>
                            </select>
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">层级</label>
                        <div class="col-sm-4">
                                <input id="level" name="level" class="form-control" >
                        </div>
                    </div>
                    <div class="form-group">
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">排序</label>
                        <div class="col-sm-4">
                            <input id="sort" name="sort" class="form-control" >
                        </div>
                        <label class="col-sm-2 control-label">资源路径</label>
                        <div class="col-sm-4">
                            <input id="sort" name="sourceUrl" class="form-control" >
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">修改资源</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">资源名称</label>
                        <div class="col-sm-4">
                            <input  name="name" class="form-control" type="text"  />
                            <input  name="id" class="form-control" type="hidden"  />

                        </div>
                        <label for="modelName" class="col-sm-2 control-label">资源key</label>
                        <div class="col-sm-4">
                            <input  name="sourceKey" class="form-control" type="text"  />

                        </div>
                    </div>
                    <div class="form-group">

                        <label for="lastname" class="col-sm-2 control-label">类型</label>
                        <div class="col-sm-4">
                            <select name="type" class="form-control">
                                <option value="0">目录</option>
                                <option value="1">菜单</option>
                                <option value="2">按钮</option>
                            </select>
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">层级</label>
                        <div class="col-sm-4">
                            <input id="level" name="level" class="form-control" >
                        </div>
                    </div>
                    <div class="form-group">
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">排序</label>
                        <div class="col-sm-4">
                            <input id="sort" name="sort" class="form-control" >
                        </div>
                        <label class="col-sm-2 control-label">资源路径</label>
                        <div class="col-sm-4">
                            <input id="sort" name="sourceUrl" class="form-control" >
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>








<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/plugins/zTree/jquery.ztree.all.min.js"></script>
<script>
    function getbutton(row) {
        var html = '<p id="' + row.id + '" >' +
                '<@shiro.hasPermission name="resourceedit"><button class="btn btn-warning btn-xs update" type="button">编辑</button></@shiro.hasPermission> ' +
                '<@shiro.hasPermission name="resourcedel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="del(this);">删除</button></@shiro.hasPermission>' +
                '</p>';

        return html;
    }
</script>
<script src="${request.contextPath}/static/js/shiro/resource.index.1.js"></script>


</body>
</html>
