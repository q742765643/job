<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台  </title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progress/css/normalize.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progress/css/default.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progress/css/styles.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progressbar/css/default.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/progressbar/css/jquery.classycountdown.css" />
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">


<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>节点管理
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
    <ul id="myTab" class="nav nav-pills">
        <li class="active">
            <a href="#home" data-toggle="tab">列表监控</a>
        </li>
        <li>
            <a href="#ios" data-toggle="tab">图表监控</a>
        </li>
    </ul>
    <div id="myTabContent" class="tab-content">
        <div class="tab-pane fade in active" id="home">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">列表</h3>&nbsp;&nbsp;
                        <@shiro.hasPermission name="jobgroupadd">
                            <button class="btn btn-info btn-xs pull-left2 add">+新增节点</button>
                        </@shiro.hasPermission>
                        </div>
                        <div class="box-body">
                            <table id="monitor_list" class="table table-bordered table-striped display" width="100%">
                                <thead>
                                <tr>
                                <#--<th name="id" >ID</th>-->
                                    <th name="deploySystem">部署系统</th>
                                    <th name="ip">ip</th>
                                    <th name="lineNum">排队任务</th>
                                    <th name="operationNum">运行任务</th>
                                    <th name="cpuUsage">cpu使用率</th>
                                    <th name="memoryUsage">占用内存</th>
                                    <th name="isRun">状态</th>
                                    <th name="zNum">总能力</th>
                                    <th name="operate">操作</th>
                                </tr>
                                </thead>
                                <tbody>
                                <#if list?exists && list?size gt 0>
                                    <#list list as monitor>
                                    <tr>
                                    <#--<td>${group.id}</td>-->
                                        <td>${monitor.deploySystem}</td>
                                        <td>${monitor.ip}</td>
                                        <td>${monitor.lineNum}</td>
                                        <td>${monitor.operationNum}</td>
                                        <td>${monitor.cpuUsage}%</td>
                                        <td>${monitor.memoryUsage}%</td>
                                        <td><#if monitor.isRun==0>正常</#if><#if monitor.isRun==1>异常</#if></td>
                                        <td>${monitor.zNum}</td>
                                        <td>
                                            <@shiro.hasPermission name="jobgroupinfo">
                                                <button class="btn btn-info btn-xs info"
                                                        id="${monitor.id}">详情
                                                </button>
                                            </@shiro.hasPermission>
                                            <@shiro.hasPermission name="jobgroupedit">
                                                <button class="btn btn-warning btn-xs update"
                                                        id="${monitor.id}"
                                                        deploySystem="${monitor.deploySystem}"
                                                        registryKey="${monitor.appName}"
                                                        registryIp="${monitor.ip}"
                                                        zNum="${monitor.zNum}">编辑
                                                </button>
                                            </@shiro.hasPermission>
                                            <@shiro.hasPermission name="jobgroupdel">
                                                <button class="btn btn-danger btn-xs remove" id="${monitor.id}">删除
                                                </button>
                                            </@shiro.hasPermission>
                                            <@shiro.hasPermission name="jobgroupdeploy">
                                                <button class="btn btn-primary btn-xs deploy" 
                                                id="${monitor.id}" 
                                                registryIp="${monitor.ip}">算法管理
                                                </button>
                                            </@shiro.hasPermission>
                                        </td>
                                    </tr>
                                    </#list>
                                </#if>
                                </tbody>
                            </table>

                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="tab-pane fade  active" id="ios">
            <div class="row">
                <div class="col-xs-12">

                    <div class="box">
                        <div class="box-body">

                            <table id="monitor_list1" class="table"  width="100%">
                                <thead>

                                <tr>
                                    <th name="ip" style="width:15%;text-align: center;" >ip</th>
                                    <th name="cpuUsage" style="width:35%;text-align: center;">cpu</th>

                                    <th name="memoryUsage" style="width:25%;text-align: center;">内存</th>
                                    <th name="hardDiskUsage" style="width:25%;text-align: center;">硬盘</th>

                                </tr>
                                </thead>
                                <tbody>

                                <#if list?exists && list?size gt 0>
                                    <#list list as monitor>
                                    <tr>
                                    <#--<td>${group.id}</td>-->
                                        <td  style="text-align: center;vertical-align:middle!important;">${monitor.ip}</td>
                                        <td  style="text-align: center;vertical-align:middle !important;">
                                            <div class="progress progress--active">
                                                <#if (monitor.cpuUsage<=50)>
                                                    <b class="progress__bar progress__bar--green" style="width:${monitor.cpuUsage}%">
                                                </#if>
                                                <#if (monitor.cpuUsage>50 && monitor.cpuUsage<=80)>
                                                    <b class="progress__bar progress__bar--yellow" style="width:${monitor.cpuUsage}%">
                                                </#if>
                                                <#if (monitor.cpuUsage>80)>
                                                    <b class="progress__bar progress__bar--orange" style="width:${monitor.cpuUsage}%">
                                                </#if>

                                                    <span class="progress__text">
                                                       <em>${monitor.cpuUsage}%</em>
                                                    </span>
                                                </b>
                                            </div>
                                        </td>
                                        <td  style="text-align: center;vertical-align:middle!important;">
                                            <#if (monitor.memoryUsage<=50)>
                                                <div class="countdown" color="#19ff37"  value="${monitor.memoryUsage}"></div>
                                            </#if>
                                            <#if (monitor.memoryUsage>50 && monitor.memoryUsage<=80)>
                                                <div class="countdown" color="#ffc74f"  value="${monitor.memoryUsage}"></div>
                                            </#if>
                                            <#if (monitor.memoryUsage>80)>
                                                <div class="countdown" color="#ff6030"  value="${monitor.memoryUsage}"></div>
                                            </#if>

                                        </td>
                                        <td  style="text-align: center;vertical-align:middle!important;">
                                            <#if (monitor.hardDiskUsage<=50)>
                                                <div class="countdown" color="#19ff37"  value="${monitor.hardDiskUsage}"></div>
                                            </#if>
                                            <#if (monitor.hardDiskUsage>50 && monitor.hardDiskUsage<=80)>
                                                <div class="countdown" color="#ffc74f"  value="${monitor.hardDiskUsage}"></div>
                                            </#if>
                                            <#if (monitor.hardDiskUsage>80)>
                                                <div class="countdown" color="#ff6030"  value="${monitor.hardDiskUsage}"></div>
                                            </#if>
                                        </td>


                                    </tr>

                                    </#list>
                                </#if>
                            </tbody>
                            </table>

                        </div>
                    </div>
                </div>
            </div>

        </div>

</section>

<!-- 新增.模态框 -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog ">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">新增节点</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">部署系统<font color="red">*</font></label>
                        <div class="col-sm-10"><select class="form-control" name="deploySystem">
                                                    <option value="windows">windows</option>
                                                    <option value="linux">linux</option>
                                                </select></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">节点名称<font color="red">*</font></label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="registryKey"
                                                      placeholder="请输入节点名称" maxlength="100"></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">节点IP<font color="red">*</font></label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="registryIp"
                                                      placeholder="请输入节点ip" maxlength="100"></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">总能力<font color="red">*</font></label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="concurrency"
                                                      placeholder="请输入节点总能力" maxlength="10"></div>
                    </div>
                    <hr>
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

<!-- 算法管理.模态框 -->
<div class="modal fade" id="algoDeploy" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog ">
        <div class="modal-content"  style="width:150%; height:50%;">
            <div class="modal-header">
                <h4 class="modal-title">算法管理</h4>
                <h4 class="modal-title" id = "deployIpName" ></h4>
            </div>
            
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                  <div class="col-sm-3" id="treeview1">
                 
                  </div>
                    <input type="hidden" class="form-control" id="ipId_find" >
                    <input type="hidden" class="form-control" id="treeId_find">
                     <div class="col-sm-9">
		            <div class="box">
		                <div class="box-body">
		                    <table id="algoMapping" class="table table-bordered table-striped"
		                           style="word-break:break-all;width: 100%">
		                        <thead>
		                        <tr>
			                            <th>序号</th>
			                            <th >id</th>
			                            <th ><input id='allBinding' type='checkbox'/>绑定</th>
			                            <th ><input id='allDownload' type='checkbox'/>下载</th>
			                            <th >算法名称</th>
			                            <th >算法包名</th>
		                        </tr>
		                        </thead>
		                        <tbody></tbody>
		                        <tfoot></tfoot>
		                    </table>
		                </div>
		            </div>
		            </div>

                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="button" class="btn btn-primary" id="saveAlgoManage">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- 更新.模态框 -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog ">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">修改节点</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">部署系统<font color="red">*</font></label>
                        <div class="col-sm-10"><select class="form-control" name="deploySystem">
                                <option value="windows">windows</option>
                                <option value="linux">linux</option>
                            </select></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">节点名称<font color="red">*</font></label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="registryKey"
                                                      placeholder="请输入节点名称" maxlength="100"></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">节点IP<font color="red">*</font></label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="registryIp"
                                                      placeholder="请输入节点ip" maxlength="100"></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">总能力<font color="red">*</font></label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="concurrency"
                                                      placeholder="" maxlength="100"></div>
                    </div>
                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <input type="hidden" name="id">
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- 详情.模态框 -->
<div class="modal fade" id="infoModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog ">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">节点详情</h4>
            </div>
            <div class="modal-body">
                <table id="info_list" class="table table-bordered table-striped display">
                    <thead>
                    <tr>
                        <th name="job_desc">任务名称</th>
                        <th>排队任务数量</th>
                        <th>执行任务数量</th>
                    </tr>
                    </thead>
                    <tbody id="jobInfoList">

                    </tbody>
                </table>
                <div class="col-sm-offset-5">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>
</div>






<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<#-- jquery.validate -->
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/plugins/progress/js/modernizr.js" type="text/javascript"></script>
<script src='${request.contextPath}/static/plugins/progress/js/stopExecutionOnTimeout.js?t=1'></script>
<script src="${request.contextPath}/static/plugins/progressbar/js/jquery.knob.js"></script>
<script src="${request.contextPath}/static/plugins/progressbar/js/jquery.throttle.js"></script>
<script src="${request.contextPath}/static/plugins/progressbar/js/jquery.classycountdown.js"></script>
<script src="${request.contextPath}/static/js/monitor/monitor.index.1.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-treeview/js/bootstrap-treeview.js"></script>
<script type="text/javascript">

    $(".countdown").each(function() {
        $(this).ClassyCountdown({
            theme: "flat-colors-black"
        });
    });

</script>

</body>
</html>
