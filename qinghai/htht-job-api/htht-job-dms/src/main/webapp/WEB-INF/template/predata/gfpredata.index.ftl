<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bootstrap/css/bootstrap-select.css">
    <link href="${request.contextPath}/static/plugins/table/bootstrap-table.min.css" rel="stylesheet" />
    <link href="${request.contextPath}/static/plugins/bootstrap-wizard/wizard.css" rel="stylesheet" />

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if>">
<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>高分数据预处理
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

        <select class="form-control" id="jobGroup" style="display: none" >
            <option value="-1">全部</option>

                <#list JobGroupList as group>
                    <option value="${group.id}" <#if jobGroup==group.id>selected</#if> >${group.title}</option>
                </#list>
        </select>

        <div class="col-xs-2">
            <input type="text" class="form-control" id="executorHandler" autocomplete="on" placeholder="按任务名称搜索...">
        </div>
        <div class="col-xs-1">
            <button class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp</button>
        </div>
        <div class="col-xs-1">
        <@shiro.hasPermission name="taskadd"><button class="btn btn-primary btn-success add" type="button" _tasktype="7">新增任务</button></@shiro.hasPermission>
        </div>
    </div>
    <p></p>
    <div class="row">
        <div class="col-xs-12">
            <div class="box">
                <div class="box-header hide">
                    <h3 class="box-title">调度列表</h3>
                </div>
                <div class="box-body" >
                    <table style="width: 100%" id="job_list" class="table table-bordered table-striped">
                        <thead>
                        <tr>
                            <th name="id" >id</th>
                            <th name="jobGroup" >jobGroup</th>
                            <!--<th name="childJobKey" >JobKey</th>-->
                            <th name="jobDesc" >任务名称</th>
                            <th name="nextfireTime" >下次执行时间</th>
                            <th name="prevfireTime" >准备</th>
                            <th name="glueType" >运行模式</th>
                            <th name="executorParam" >任务参数</th>
                            <th name="jobCron" >任务执行策略</th>
                            <th name="jobCronName" >任务执行策略</th>
                            <th name="addTime" >新增时间</th>
                            <th name="updateTime" >更新时间</th>
                            <th name="author" >负责人</th>
                            <th name="alarmEmail" >报警邮件</th>
                            <th name="glueType" >运行模式</th>
                            <th name="jobStatus" >状态</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                        <tfoot></tfoot>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<div class="modal fade" id="mappingModal" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-lg"  style="width:90%">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" >调整任务</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal mappingform" role="form" >
                    <input type="hidden" name="mappingJson"/>
                    <input type="hidden" name="jobId"/>
                    <div class="col-sm-12">
                        <table id="tableMapping" class="table table-bordered table-striped text-nowrap"></table>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary"  >保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<#--存放tasktype-->
<input type="hidden" value="${tasktype}" id="tasktypevalue">

<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/plugins/My97DatePicker/WdatePicker.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table-edit1.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-select.js"></script>

<script>
    function getbutton(row,logUrl,codeBtn,pause_resume) {
        var html = '<p id="'+ row.id +'" modelId="'+ row.modelId +'">'+
                '<@shiro.hasPermission name="executeonce"><button class="btn btn-primary btn-xs job_operate" _type="job_trigger" type="button">执行一次</button> </@shiro.hasPermission>'+
                '<@shiro.hasPermission name="executepause">'+pause_resume+'</@shiro.hasPermission>&nbsp&nbsp&nbsp&nbsp&nbsp'+
                '<button class="btn  btn-primary btn-xs job_mapping"  type="button">调整</button> '+
                '<@shiro.hasPermission name="taskedit"><button class="btn btn-warning btn-xs update" type="button">编辑</button></@shiro.hasPermission>  '+
                codeBtn +
                '<@shiro.hasPermission name="taskdel"><button class="btn btn-danger btn-xs job_operate" _type="job_del" type="button">删除</button></@shiro.hasPermission>&nbsp&nbsp&nbsp&nbsp&nbsp'+
                '<@shiro.hasPermission name="tasklog"><button class="btn btn-primary btn-xs" type="job_del" type="button" onclick="windowOpen(\'' + logUrl + '\')" >日志</button></@shiro.hasPermission>  '+
                '</p>';
        return html;
    }
    function windowOpen(url) {
        window.open(url,"_self");

    }
</script>
<script src="${request.contextPath}/static/js/jobflow/jobflow.index.1.js"></script>


</body>
</html>
