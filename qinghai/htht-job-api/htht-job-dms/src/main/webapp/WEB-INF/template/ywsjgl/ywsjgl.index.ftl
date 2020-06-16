<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
    <#import "/common/common.js.ftl" as netCommon>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <!-- daterangepicker -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1>农气要素查询
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
        <div class="col-xs-2">
            <div class="input-group">
                <span class="input-group-addon">要素</span>
                <select class="form-control" id="element" paramVal="<#if jobInfo?exists>${jobInfo.id}</#if>" >

                </select>
            </div>
        </div>
        <div class="col-xs-2">
            <div class="input-group">
                <span class="input-group-addon">站号</span>
                <select class="form-control" id="station" paramVal="<#if jobInfo?exists>${jobInfo.id}</#if>" >

                </select>
            </div>
        </div>
        <div class="col-xs-4">
            <div class="col-xs-4" style='padding-left:0px'>
                <button class="btn btn-primary btn-success" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp查询&nbsp&nbsp&nbsp&nbsp&nbsp</button>
            </div>
            <div class="col-xs-4" style='padding-left:0px'>
                <button class="btn btn-primary btn-info" id="exportBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp导出&nbsp&nbsp&nbsp&nbsp&nbsp</button>
            </div>
        </div>
    </div>
    <p></p>
    <div class="row">
        <div class="col-xs-12">
            <div class="box">
                <div class="box-header hide"><h3 class="box-title"></h3></div>
                <div class="box-body" id='createTable'>
                    <table id="joblog_list" class="table table-bordered table-striped display" width="100%" >

                    </table>
                </div>
            </div>
        </div>
    </div>
</section>
<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/ywsjgl/ywsjgl.index.1.js"></script>
</body>
</html>
