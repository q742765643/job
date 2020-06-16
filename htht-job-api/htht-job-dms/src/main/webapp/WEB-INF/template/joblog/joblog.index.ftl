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
			<h1>调度日志
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
				<li><a><i class="fa fa-dashboard"></i>调度日志</a></li>
				<li class="active">调度管理</li>
			</ol>
			-->
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    	<div class="row">
 					<div class="input-group">
                		<select class="form-control" id="jobGroup" style="display: none"  paramVal="<#if jobInfo?exists>${jobInfo.jobGroup}</#if>" >
                            <option value="-1" >全部</option>
                			<#list JobGroupList as group>
                				<option value="${group.id}" >${group.title}</option>
                			</#list>
	                  	</select>
	              	</div>
	            <div class="col-xs-2">
	              	<div class="input-group">
	                	<span class="input-group-addon">任务</span>
                        <select class="form-control" id="jobId" paramVal="<#if jobInfo?exists>${jobInfo.id}</#if>" >
                            <option value="0" >全部</option>
						</select>
	              	</div>
	            </div>

                <div class="col-xs-2">
                    <div class="input-group">
                        <span class="input-group-addon">状态</span>
                        <select class="form-control" id="logStatus" >
                            <option value="-1" >全部</option>
                            <option value="1" >成功</option>
                            <option value="2" >失败</option>
                            <option value="3" >进行中</option>
                            <option value="4">异常</option>
                        </select>
                    </div>
                </div>

	            <div class="col-xs-4">
              		<div class="input-group">
                		<span class="input-group-addon">
	                  		调度时间
	                	</span>
	                	<input type="text" class="form-control" id="filterTime" readonly >
	              	</div>
	            </div>

                <div class="col-xs-1">
                    <button class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp</button>
                </div>

	            <div class="col-xs-1">
                      <@shiro.hasPermission name="clean"><button class="btn btn-primary btn-nomal" id="clearLog">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp清理&nbsp&nbsp&nbsp&nbsp&nbsp</button></@shiro.hasPermission>
	            </div>
          	</div>
			<p></p>
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header hide"><h3 class="box-title">调度日志</h3></div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
					                	<th name="id" >id</th>
                                        <th name="executorParam" hidden >executorParam</th>
                                        <th name="triggerTime" hidden >triggerTime</th>
                                        <th name="executorAddress" hidden >executorAddress</th>
                                        <th name="handleMsg" hidden >handleMsg</th>
                                        <th name="jobGroup" >执行器ID</th>
					                	<th name="jobId" >任务ID</th>
                                        <th name="executorHandler" >任务名称</th>
										<#--<th name="executorAddress" >执行器地址</th>
										<th name="glueType" >运行模式</th>
                                      	<th name="executorParam" >任务参数</th>-->
                                        <th name="triggerTime" >调度时间</th>
                                        <th name="triggerCode" >调度结果</th>
                                        <#--<th name="triggerMsg" >调度备注</th>-->
					                  	<th name="handleTime" >执行时间</th>
					                  	<th name="handleCode" >执行状态</th>
					                  	<th name="handleMsg" >执行备注</th>
					                  	<th name="handleMsg" >任务日志</th>
					                  	<th name="handleMsg" >操作</th>
					                </tr>
				                </thead>
				                <tbody></tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
	    </section>


		<!-- footer -->
        <!-- 日志模态框 -->
        <div class="modal fade" id="showLogModal" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-body" id="showLogModalBody">
                        <div class="box">
                            <div class="box-body">
                                <table id="logParamsTable" class="table table-bordered table-striped" style="word-break:break-all;width: 100%">
                                    <thead>
                                   <#-- <tr>
                                        <th>序号</th>
                                        <th name="parameterName">参数名称</th>
                                        <th name="parameterDesc">参数标识</th>
                                        <th name="parameterType">参数类型</th>
                                        <th name="value">参数值</th>
                                    </tr>-->
                                   <tr>
                                       <th>序号</th>
                                       <th name="parameterName">参数名称</th>
                                       <th name="parameterDesc">参数标识</th>
                                       <th name="parameterType">参数类型</th>
                                       <th name="value">参数值</th>
                                   </tr>
                                    </thead>
                                    <tbody></tbody>
                                    <tfoot></tfoot>
                                </table>
                            </div>
                        </div>
                        <div>

                                    <div id="logConsole"></div>
                                    <li class="fa fa-refresh fa-spin" style="font-size: 20px;float: left;" id="logConsoleRunning" ></li>
                                    <div><hr><hr></div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        </div>



<!-- 日志清理.模态框 -->
<div class="modal fade" id="clearLogModal" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" >日志清理</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div class="form-group">
                        <label class="col-sm-3 control-label"">执行器：</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control jobGroupText" readonly >
							<input type="hidden" name="jobGroup" >
						</div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label"">任务：</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control jobIdText" readonly >
                            <input type="hidden" name="jobId" >
						</div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label"">清理类型：</label>
                        <div class="col-sm-9">
                            <select class="form-control" name="type" >
                                <option value="1" >清理一个月之前日志数据</option>
                                <option value="2" >清理三个月之前日志数据</option>
                                <option value="3" >清理六个月之前日志数据</option>
                                <option value="4" >清理一年之前日志数据</option>
                                <option value="5" >清理一千条以前日志数据</option>
                                <option value="6" >清理一万条以前日志数据</option>
                                <option value="7" >清理三万条以前日志数据</option>
                                <option value="8" >清理十万条以前日志数据</option>
                                <option value="9" >清理所有日志数据</option>
                            </select>
                        </div>
                    </div>

                        <hr>
                        <div class="form-group">
                            <div class="col-sm-offset-5 col-sm-6">
                                <button type="button" class="btn btn-primary ok" >确定</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            </div>
                    </div>
                </form>
            </div>
        </div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/joblog.index.1.js"></script>
</body>
</html>
