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
        <h1>统计查询
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
    <div class="row" style='padding-left:15px'>
        <div class="col-xs-1" style='padding-left:0px'>
            <button class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp查询&nbsp&nbsp&nbsp&nbsp&nbsp</button>
        </div>
        <div class="col-xs-1" style='padding-left:0px'>
            <button class="btn btn-primary btn-success" id="searchBtn1">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp排位&nbsp&nbsp&nbsp&nbsp&nbsp</button>
        </div>
        <div class="col-xs-1" style='padding-left:0px'>
            <button class="btn btn-primary btn-info" id="searchBtn2">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp均值&nbsp&nbsp&nbsp&nbsp&nbsp</button>
        </div>
        <div class="col-xs-1" style='padding-left:0px'>
            <button class="btn btn-primary btn-success" id="searchBtn3">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp统计&nbsp&nbsp&nbsp&nbsp&nbsp</button>
        </div>
        <div class="col-xs-1" style='padding-left:0px'>
            <button class="btn btn-primary btn-info" id="searchBtn4">上期环比</button>
        </div>
        <div class="col-xs-1" style='padding-left:0px'>
            <button class="btn btn-primary btn-success" id="searchBtn5">去年同期</button>
        </div>
        <div class="col-xs-1" style='padding-left:0px'>
            <button class="btn btn-primary btn-info" id="searchBtn6">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp导出&nbsp&nbsp&nbsp&nbsp&nbsp</button>
        </div>
    </div>
    <p></p>
    <div class="row">
        <div class="col-xs-2">
            <div class="input-group">
                <span class="input-group-addon">山东</span>
                <select class="form-control" id="regionid" paramVal="<#if jobInfo?exists>${jobInfo.id}</#if>" >

                </select>
            </div>
        </div>
        <div class="col-xs-2">
            <div class="input-group">
                <span class="input-group-addon">类型</span>
                <select class="form-control" id="pType" paramVal="<#if jobInfo?exists>${jobInfo.id}</#if>" >

                </select>
            </div>
        </div>
        <div class="col-xs-2">
            <div class="input-group">
                <span class="input-group-addon">农田</span>
                <select class="form-control" id="farmType" >

                </select>
            </div>
        </div>

        <div class="col-xs-2">
            <div class="input-group">
                <span class="input-group-addon">查询周期</span>
                <select class="form-control" id="period" >
                    <option value='日' id="day_select" >日</option>
                    <option value='旬' id="search_cycle1">旬</option>
                    <option value='月' id="search_cycle2">月</option>
                    <option value='年' id="search_cycle3">年</option>
                    <option value='限1月' id="search_cycle4">限1月</option>
                    <option value='限2月' id="search_cycle5">限2月</option>
                    <option value='限3月' id="search_cycle6">限3月</option>
                    <option value='限4月' id="search_cycle7">限4月</option>
                    <option value='限5月' id="search_cycle8">限5月</option>
                    <option value='限6月' id="search_cycle9">限6月</option>
                    <option value='限7月' id="search_cycle10">限7月</option>
                    <option value='限8月' id="search_cycle11">限8月</option>
                    <option value='限9月' id="search_cycle12">限9月</option>
                    <option value='限10月' id="search_cycle13">限10月</option>
                    <option value='限11月' id="search_cycle14">限11月</option>
                    <option value='限12月' id="search_cycle15">限12月</option>
                    <option value='限上旬' id="search_cycle16">限上旬</option>
                    <option value='限中旬' id="search_cycle17">限中旬</option>
                    <option value='限下旬' id="search_cycle18">限下旬</option>
                </select>
            </div>
        </div>

        <div class="col-xs-4">
            <div class="input-group">
                <span class="input-group-addon">
                    查询时间
                </span>
                <input type="text" class="form-control" id="filterTime" readonly>
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
<script src="${request.contextPath}/static/js/rasterStatic/rasterStatic.index.1.js"></script>
</body>
</html>
