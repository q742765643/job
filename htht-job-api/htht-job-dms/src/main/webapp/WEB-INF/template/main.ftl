<!DOCTYPE html>
<html>
<head>
  	<title>并行支撑平台</title>
  	<#import "/common/common.js.ftl" as netCommon>
	<@netCommon.commonStyle />
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
<ul id="myTab" class="nav nav-pills">
    <li class="active ">
        <a href="#home" data-toggle="tab">服务器监控图表</a>
    </li>
    <li>
        <a href="#ios" data-toggle="tab">运行图表</a>
    </li>
</ul>
<div id="myTabContent" class="tab-content">
    <div class="tab-pane fade in active" id="home">

        <div class="row animated fadeInRight">
            <div class="col-sm-12">
                <div class="panel panel-primary">
                    <div class="form-group">
                        <div  class="col-sm-2"><i class="fa fa-rss-square"></i> 实时监控（秒)ip:</div>
                        <div class="col-sm-3"><input id="ipselect" onchange="selectOnchang(this)"></div>
                    </div>

                    <div class="panel-body">
                        <table style="width: 100%;">
                            <tr>
                                <td width="33.3%"><div id="main_one" style="height: 240px;"></div></td>
                                <td width="33.3%"><div id="main_two" style="height: 240px;"></div></td>
                                <td width="33.3%"><div id="main_three"
                                                       style="height: 240px;"></div></td>
                            </tr>
                        </table>
                        <br>
                        <div id="main" class="col-sm-6" style="height: 400px;"></div>
                        <div id="mainSecond" class="col-sm-6" style="height: 400px;"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row animated fadeInRight">
            <div class="col-sm-12">
                <div class="panel panel-danger">
                    <div class="">
                        <i class="fa fa-fire"></i> 实时监控（分/时）
                    </div>

                    <div class="panel-body">
                        <div id="mainMin" class="col-sm-6" style="height: 400px;"></div>
                        <div id="mainHour" class="col-sm-6" style="height: 400px;"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="tab-pane fade  active" id="ios">

        <section class="content-header">
            <h1>运行报表
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
            <!--
            <ol class="breadcrumb">
                <li><a><i class="fa fa-dashboard"></i>调度中心</a></li>
                <li class="active">使用教程</li>
            </ol>
            -->
        </section>

        <!-- Main content -->
        <section class="content">

            <!-- 任务信息 -->
            <div class="row">

            <#-- 任务信息 -->
                <div class="col-md-4 col-sm-6 col-xs-12">
                    <div class="info-box bg-aqua">
                        <span class="info-box-icon"><i class="fa fa-flag-o"></i></span>

                        <div class="info-box-content">
                            <span class="info-box-text">任务数量</span>
                            <span class="info-box-number">${jobInfoCount}</span>

                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">系统中配置的任务数量</span>
                        </div>
                    </div>
                </div>

            <#-- 调度信息 -->
                <div class="col-md-4 col-sm-6 col-xs-12" >
                    <div class="info-box bg-yellow">
                        <span class="info-box-icon"><i class="fa fa-calendar"></i></span>

                        <div class="info-box-content">
                            <span class="info-box-text">调度次数</span>
                            <span class="info-box-number">${jobLogCount}</span>

                            <div class="progress">
                                <div class="progress-bar" style="width: 100%" ></div>
                            </div>
                            <span class="progress-description">
                                调度中心触发的调度次数
                            <#--<#if jobLogCount gt 0>
                                调度成功率：${(jobLogSuccessCount*100/jobLogCount)?string("0.00")}<small>%</small>
                            </#if>-->
                            </span>
                        </div>
                    </div>
                </div>

            <#-- 执行器 -->
                <div class="col-md-4 col-sm-6 col-xs-12">
                    <div class="info-box bg-green">
                        <span class="info-box-icon"><i class="fa ion-ios-settings-strong"></i></span>

                        <div class="info-box-content">
                            <span class="info-box-text">执行器数量</span>
                            <span class="info-box-number">${executorCount}</span>

                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">心跳检测成功的执行器机器数量</span>
                        </div>
                    </div>
                </div>

            </div>

        <#-- 调度报表：时间区间筛选，左侧折线图 + 右侧饼图 -->
            <div class="row">
                <div class="col-md-12">
                    <div class="box">
                        <div class="box-header with-border">
                            <h3 class="box-title">调度报表（一月之内）</h3>
                        <#--<input type="text" class="form-control" id="filterTime" readonly >-->
                        </div>
                        <div class="box-body">
                            <div class="row">
                            <#-- 左侧折线图 -->
                                <div class="col-md-8">
                                    <div id="lineChart" style="height: 350px;"></div>
                                </div>
                            <#-- 右侧饼图 -->
                                <div class="col-md-4">
                                    <div id="pieChart" style="height: 350px;"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>


        </section>

    </div>
</div>	<!-- footer -->
<@netCommon.commonScript />
<#--<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>-->
<script src="${request.contextPath}/static/plugins/echarts/echarts.min.js"></script>
<script src="${request.contextPath}/static/plugins/echarts/roma.js"></script>
<script src="${request.contextPath}/static/adminlte/bootstrap/js/bootstrap-select.min.js"></script>
<script src="${request.contextPath}/static/js/index.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-select.js"></script>
<script src="${request.contextPath}/static/js/monitor/systemInfo.js"></script>

</body>
</html>
