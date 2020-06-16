<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progress/css/normalize.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progress/css/default.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progress/css/styles.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progressbar/css/default.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progressbar/css/jquery.classycountdown.css" />
<style>
.progress {
  border-radius: 0;
  -webkit-box-shadow: none;
  box-shadow: none;
  background: #dadada;
  height: 18px;
}
</style>
</head>

<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">

<!-- Content Wrapper. Contains page content -->
<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>磁盘信息管理
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
                    <input type="text" class="form-control" id="searchText" autocomplete="on" placeholder="按磁盘名称搜索...">
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp</button>
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-primary btn-success add" >磁盘添加</button>
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
                <h4 class="modal-title">添加新磁盘</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="userName" class="col-sm-2 control-label">磁盘名称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="diskdesc" maxlength="100">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">磁盘路径</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="loginurl">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="password" class="col-sm-2 control-label">登录名称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="loginname">
                        </div>
                        <label for="ackPassword" class="col-sm-2 control-label">登录密码</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control"  name="loginpwd">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">磁盘类型</label>
                        <div class="col-sm-4">
                            <select name="disktype" class="form-control">
                                <option value="0">扫描磁盘</option>
								<option value="1">归档磁盘</option>
								<option value="2">图片磁盘</option>
								<option value="3">工作磁盘</option>
								<option value="4">订单磁盘</option>
								<option value="5">近线磁盘</option>
								<option value="6">离线磁盘</option>
                            </select>
                        </div>
                        <label for="ackPassword" class="col-sm-2 control-label">磁盘最小<br>空闲大小</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control"  name="mindiskfreesize" style="width:70%;display:inline">
                            <select name="unitType" class="form-control" style="width:28%;display:inline">
                                <option value="MB">MB</option>
								<option value="GB">GB</option>
								<option value="TB">TB</option>
								<option value="PB">PB</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">磁盘状态</label>
                        <div class="col-sm-4">
                            <select name="diskstatus" class="form-control">
                                <option value="0">可用</option>
								<option value="1">禁用</option>
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
                <h4 class="modal-title">磁盘修改</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">磁盘名称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="diskdesc">
                            <input type="hidden" class="form-control" name="id">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">磁盘路径</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="loginurl">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="loginname" class="col-sm-2 control-label">登录名称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="loginname">
                        </div>
                        <label for="loginpwd" class="col-sm-2 control-label">登录密码</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="loginpwd">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">磁盘类型</label>
                        <div class="col-sm-4">
                            <select name="disktype" class="form-control">
                                <option value="0">扫描磁盘</option>
								<option value="1">归档磁盘</option>
								<option value="2">图片磁盘</option>
								<option value="3">工作磁盘</option>
								<option value="4">订单磁盘</option>
								<option value="5">近线磁盘</option>
								<option value="6">离线磁盘</option>
                            </select>
                        </div>
                        <label for="ackPassword" class="col-sm-2 control-label">磁盘最小<br>空闲大小</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control"  name="mindiskfreesize" style="width:70%;display:inline">
                            <select name="unitType" class="form-control" style="width:28%;display:inline">
                                <option value="MB">MB</option>
								<option value="GB">GB</option>
								<option value="TB">TB</option>
								<option value="PB">PB</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">磁盘状态</label>
                        <div class="col-sm-4">
                            <select name="diskstatus" class="form-control">
                                <option value="0">可用</option>
								<option value="1">禁用</option>
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
                '<button class="btn btn-warning btn-xs update" type="button">编辑</button>  ' +
                '<button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="del(this);">删除</button>' +
                '</p>';

        return html;
    }
</script>
<script src="${request.contextPath}/static/js/dms/disk_information.index.1.js"></script>


</body>
</html>
