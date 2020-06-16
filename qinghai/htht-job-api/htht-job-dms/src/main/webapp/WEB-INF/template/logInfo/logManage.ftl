<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/log.css">

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">

<!-- Content Wrapper. Contains page content -->
<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>日志管理
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
                <div class="col-xs-4" id="xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">日志类型</span>
                        <select class="form-control" id="searchText" onchange="searchLog()"  autocomplete="on">
                            <option value="SYSTEMLOG" select ="selected">系统日志</option>
                            <option value="OPERATELOG">操作日志</option>
                            <#--<option value="1" >异常日志</option>-->
                        </select>
                    </div>
                </div>
            </div>
            <div class="box" style="margin-top: 1%">
                <div class="box-body">
                        <table class="table table-striped table-bordered table-hover" id="logsTable" style="width: 100%">
                            <thead>
                            <tr>
                                <th>序号</th>
                                <th>操作用户</th>
                                <th>IP</th>
                                <th>日志</th>
                                <th>操作时间</th>
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


<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content" id="moContent">
            <div class="modal-body" id="moBody">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <div class="col-sm-4" id="deLog">
                           是否删除此条日志？
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6" id="mar">
                            <button type="submit" class="btn btn-primary" >删除</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
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
<script src="${request.contextPath}/static/js/logInfo/log.js"></script>


</body>
</html>
