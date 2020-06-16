<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
<style type="text/css">
    #ftpForm label.error{
        color: red;
    }
</style>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">

<!-- Content Wrapper. Contains page content -->
<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>ftp信息管理
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
    <div class="row" style="text-align: right">
        <div class="col-xs-offset-10 col-xs-1">
            <button class="btn btn-primary btn-info" id="addFtp" data-toggle="modal" onclick="addFtp()" data-target="#addFtpModal">添加FTP</button>
        </div>
        <div class="col-xs-1">
            <button class="btn btn-danger btn-info" id="deleteFtps">删除</button>
        </div>
    </div>
    <p></p>
    <div class="row">
        <div class="col-sm-12">
            <div class="box">
                <div class="box-body">
                    <table class="table table-striped table-bordered table-hover" id="ftpsTable" style="width: 100%">
                        <thead>
                            <tr>
                                <th style="text-align: center;padding-right: 8px">
                                    <input id="checkAll" class="checkAll" type="checkbox" value="">
                                </th>
                                <th>名称</th>
                                <th>IP地址</th>
                                <th>端口</th>
                                <th>用户名</th>
                                <th>密码</th>
                                <#--<th>更新时间</th>-->
                                <th>操作</th>
                            </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>

</section>
<!-- /.content -->

<!-- 添加FTP模态框 -->
<div class="modal fade" id="addFtpModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">添加FTP</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" id="ftpForm" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-md-offset-2 col-sm-2 control-label">名称：<font color="red">*</font></label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control" name="name" required lay-verify="required" placeholder="请输入名称" maxlength="100">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="modelName" class="col-md-offset-2 col-sm-2 control-label">IP地址：<font color="red">*</font></label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control ip" name="ipAddr" required lay-verify="required" placeholder="请输入IP地址">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="modelName" class="col-md-offset-2 col-sm-2 control-label">端口号：<font color="red">*</font></label>
                        <div class="col-sm-6">
                            <input type="number" class="form-control" name="port" required lay-verify="required" placeholder="请输入端口号">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="modelName" class="col-md-offset-2 col-sm-2 control-label">用户名：<font color="red">*</font></label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control" name="userName" required lay-verify="required" placeholder="请输入用户名">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="modelName" class="col-md-offset-2 col-sm-2 control-label">密码：<font color="red">*</font></label>
                        <div class="col-sm-6">
                            <input type="password" class="form-control" name="pwd" required lay-verify="required" placeholder="请输入密码">
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary" id="saveFtpSubmit" >保存</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <input type="hidden" name="id">
                        <input type="hidden" name="createTime">
                        <input type="hidden" name="version">
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>

<#--外置脚本引用-->
<script src="${request.contextPath}/static/js/systemConfig/ftpsConfig.js"></script>
</body>
</html>
