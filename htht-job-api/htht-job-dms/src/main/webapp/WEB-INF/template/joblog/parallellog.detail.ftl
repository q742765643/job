<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
</head>
<body class="hold-transition skin-blue layout-top-nav">

<div class="wrapper">

    <header class="main-header">
        <nav class="navbar navbar-static-top">
            <div class="container">
            <input type="hidden" class="form-control" id="logFileName" value="${logFileName}" >
            <#-- icon -->
                <div class="navbar-header">
                    <a class="navbar-brand"><b>执行日志</b>Console</a>
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse">
                        <i class="fa fa-bars"></i>
                    </button>
                </div>

                <#-- left nav -->
                <div class="collapse navbar-collapse pull-left" id="navbar-collapse">
                    <ul class="nav navbar-nav">
                        <#--<li class="active" ><a href="javascript:;">任务：<span class="sr-only">(current)</span></a></li>-->
                    </ul>
                </div>

                <#-- right nav -->
                <div class="navbar-custom-menu">
                    <ul class="nav navbar-nav">
                        <li>
                            <a href="javascript:window.location.reload();" >
                                <i class="fa fa-fw fa-refresh" ></i>
                                刷新
                            </a>
                        </li>
                    </ul>
                </div>

            </div>
        </nav>
    </header>

    <div class="content-wrapper" >
        <section class="content">
            <pre style="font-size:12px;position:relative;" >
                <div id="logConsole"></div>
                <li class="fa fa-refresh fa-spin" style="font-size: 20px;float: left;" id="logConsoleRunning" ></li>
                <div><hr><hr></div>
            </pre>
        </section>
    </div>


</div>

<@netCommon.commonScript />
<script>
    var parallelLogId = '${parallelLogId}';
    var executorAddress='${executorAddress}';
    var triggerTime = '${triggerTime}';

</script>
<script src="${request.contextPath}/static/js/parallellog.detail.1.js"></script>

</body>
</html>