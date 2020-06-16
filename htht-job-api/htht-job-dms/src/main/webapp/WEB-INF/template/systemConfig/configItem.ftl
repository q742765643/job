<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">

<!-- Content Wrapper. Contains page content -->
<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>配置项管理
        <small>配置项管理</small>
    </h1>
    <!--
    <ol class="breadcrumb">
        <li><a><i class="fa fa-dashboard"></i>调度中心</a></li>
        <li class="active">使用教程</li>
    </ol>
    -->
</section>

<!-- Main content -->
<section class="content">
    <div class="row">
        <div class="col-sm-12">
            <div class="row">
                <div class="col-xs-4">
                </div>
                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">昵称</span>
                        <input type="text" class="form-control" id="searchText" autocomplete="on">
                    </div>
                </div>

                <div class="col-xs-2">
                    <button class="btn btn-block btn-info" id="searchBtn">搜索</button>
                </div>
                <div class="col-xs-2">
                <@shiro.hasPermission name="useradd">
                    <button class="btn btn-block btn-success add" >添加</button>
                </@shiro.hasPermission>
                </div>
            </div>
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
                <h4 class="modal-title">添加用户</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">用户名称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="userName" maxlength="100">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">用户昵称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="nickName" maxlength="100">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">状态</label>
                        <div class="col-sm-4">
                            <select name="locked" class="form-control">
                                <option value="0">未锁定</option>
                                <option value="1">锁定</option>
                            </select>
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
                <h4 class="modal-title">修改用户</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">用户名称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="userName" maxlength="100" disabled>
                            <input type="hidden" class="form-control" name="id" maxlength="100">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">用户昵称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="nickName" maxlength="100">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">状态</label>
                        <div class="col-sm-4">
                            <select name="locked" class="form-control">
                                <option value="0">未锁定</option>
                                <option value="1">锁定</option>
                            </select>
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

<div class="modal fade" id="grantModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">分配角色</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <input  name="id" class="form-control" type="hidden"  />
                    <div class="form-group" >
                        <div class="col-sm-offset-2 col-sm-6" id="roleGrant">

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
<script>
    function getbutton(row) {
        var html = '<p id="' + row.id + '" >' +
                '<@shiro.hasPermission name="useredit"><button class="btn btn-warning btn-xs update" type="button">编辑</button></@shiro.hasPermission>  ' +
                '<@shiro.hasPermission name="userdel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="del(this);">删除</button></@shiro.hasPermission> ' +
                '<@shiro.hasPermission name="grantrole"><button class="btn btn-danger btn-xs grant" _type="model_del" type="button" >分配角色</button></@shiro.hasPermission>  ' +
                '</p>';

        return html;
    }
</script>
<script src="${request.contextPath}/static/js/shiro/user.index.1.js"></script>


</body>
</html>
