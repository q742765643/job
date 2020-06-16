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
    <h1>角色管理
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
        <div class="col-sm-12">
            <div class="row">
                <div class="col-xs-2">
                        <input type="text" class="form-control" id="searchText" autocomplete="on" placeholder="按角色名称搜索...">
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp</button>
                </div>
                <div class="col-xs-1">
                    <@shiro.hasPermission name="roleadd">
                    <button class="btn btn-primary btn-success add" >添加角色</button>
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

<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">添加角色</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <div class="form-group">
                            <label for="modelName" class="col-sm-2 control-label">角色key</label>
                            <div class="col-sm-4">
                                <input  name="roleKey" class="form-control" type="text"  />
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">角色名称</label>
                            <div class="col-sm-4">
                                <input  name="name" class="form-control" type="text"  />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="modelName" class="col-sm-2 control-label">状态</label>
                            <div class="col-sm-4">
                                <select name="status" class="form-control">
                                    <option value="0" >正常</option>
                                    <option value="1" >禁用</option>
                                </select>
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-4">
                                <input name="description" class="form-control" >
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
                    <h4 class="modal-title">修改角色</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <div class="form-group">
                            <label for="modelName" class="col-sm-2 control-label">角色key</label>
                            <div class="col-sm-4">
                                <input  name="roleKey" class="form-control" type="text"  />
                                <input  name="id" class="form-control" type="hidden"  />

                            </div>
                            <label for="lastname" class="col-sm-2 control-label">角色名称</label>
                            <div class="col-sm-4">
                                <input  name="name" class="form-control" type="text"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="modelName" class="col-sm-2 control-label">状态</label>
                            <div class="col-sm-4">
                                <select name="status" class="form-control">
                                    <option value="0" >正常</option>
                                    <option value="1" >禁用</option>
                                </select>
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-4">
                                <input name="description" class="form-control" >
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

<!-- /.content -->
<div class="modal fade" id="grantModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">分配资源</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <input  name="id" class="form-control" type="hidden"  />

                    <ul id="tree" class="ztree"></ul>
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
                '<@shiro.hasPermission name="roleedit"><button class="btn btn-warning btn-xs update" type="button">编辑</button></@shiro.hasPermission>  ' +
                '<@shiro.hasPermission name="roledel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="del(this);">删除</button></@shiro.hasPermission>  ' +
                '<@shiro.hasPermission name="grantresource"><button class="btn btn-danger btn-xs grant" _type="model_del" type="button" >分配资源</button></@shiro.hasPermission>  ' +
                '</p>';

        return html;
    }
</script>
<script src="${request.contextPath}/static/js/dataAdmin/role.index.1.js"></script>


</body>
</html>
