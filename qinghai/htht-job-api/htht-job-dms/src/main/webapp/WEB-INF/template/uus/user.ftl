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
    <h1>用户管理
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
                    <input type="text" class="form-control" id="searchText" autocomplete="on" placeholder="按昵称搜索...">
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-primary btn-info" id="searchBtn">
                        &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp
                    </button>
                </div>
                <div class="col-xs-1">
                <@shiro.hasPermission name="useradd">
                    <button class="btn btn-primary btn-success add" id="aaa">添加用户</button>
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
                <h4 class="modal-title">添加发布平台用户</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">用户名称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" id="userName" name="userName" maxlength="100">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">用户昵称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" id="nickName" name="nickName" maxlength="100">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="password" class="col-sm-2 control-label">密码</label>
                        <div class="col-sm-4">
                            <input type="password" class="form-control" id="password" name="password">
                        </div>
                        <label for="ackPassword" class="col-sm-2 control-label">确认密码</label>
                        <div class="col-sm-4">
                            <input type="password" class="form-control" id="ackPassword" name="ackPassword" maxlength="100">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">省 ：</label>
                        <div class="col-sm-4">
                            <select name="province" class="form-control" id="province">
                            </select>
                        </div>
                         <label for="modelName" class="col-sm-2 control-label">市 ：</label>
                        <div class="col-sm-4">
                            <select name="city" class="form-control"  id="city">
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">县 ：</label>
                        <div class="col-sm-4">
                            <select name="region" class="form-control" id="region" >
                            </select>
                        </div>
                        <label for="modelName" class="col-sm-2 control-label">状态</label>
                        <div class="col-sm-4">
                            <select name="locked" class="form-control" id="locked">
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
                <h4 class="modal-title">更新发布平台用户</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">用户名称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="userName" maxlength="100" readonly>
                            <input type="hidden" class="form-control" name="id" maxlength="100">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">用户昵称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="nickName" maxlength="100">
                        </div>
                    </div>
                    <div class="form-group">

                        <label for="password" class="col-sm-2 control-label">新密码</label>
                        <div class="col-sm-4">
                            <input type="password" class="form-control" id="password2" name="password">
                        </div>
                        <label for="ackPassword2" class="col-sm-2 control-label">确认新密码</label>
                        <div class="col-sm-4">
                            <input type="password" class="form-control" id="ackPassword2" name="ackPassword2"
                                   maxlength="100">
                        </div>
                    </div>
                   <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">省 ：</label>
                        <div class="col-sm-4">
                            <select name="province" class="form-control">
                            </select>
                        </div>
                         <label for="modelName" class="col-sm-2 control-label">市 ：</label>
                        <div class="col-sm-4">
                            <select name="city" class="form-control">
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                       
                        <label for="modelName" class="col-sm-2 control-label">县 ：</label>
                        <div class="col-sm-4">
                            <select name="region" class="form-control">
                            </select>
                        </div>
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
                <h4 class="modal-title">分配发布平台角色</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <input name="id" class="form-control" type="hidden"/>
                    <div class="form-group">
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
<script src="${request.contextPath}/static/js/uus/user.index.1.js"></script>


</body>
</html>
