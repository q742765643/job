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
  	
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bootstrap/css/bootstrap-select.css">
    <link href="${request.contextPath}/static/plugins/table/bootstrap-table.min.css" rel="stylesheet" />
    <link href="${request.contextPath}/static/plugins/bootstrap-wizard/wizard.css" rel="stylesheet" />
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>调度日志<small>并行支撑平台</small></h1>
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
			   <div class="col-xs-2">
                    <div class="input-group">
                        <span class="input-group-addon">状态</span>
                        <select class="form-control" id="logStatus" >
                            <option value="-1" >全部</option>
                            <option value="200" >成功</option>
                            <option value="500" >失败</option>
                            <option value="0" >进行中</option>
                        </select>
                    </div>
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp</button>
                </div>
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header hide"><h3 class="box-title">调度日志</h3></div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
					                	<th name="id" >id</th>
                                        <th name="label" >任务名称</th>
                                        <th name="code" >执行结果</th>
                                        <th name="createTime" >开始时间</th>
					                  	<th name="updateTime" >结束时间</th>
					                  	<th name="ip" >执行节点</th>
					                  	<th name="handleMsg" >执行信息</th>
					                  	<th name="handleMsg" >任务参数</th>
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
	</div>

	<!-- footer -->
</div>
	</div>
	
<!-- 子任务详情.模态框 -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
                    <div class="col-sm-12">
						<h4 class="modal-title" id="childAlgoName"></h4>
                    </div>
                    <div class="col-sm-12">
                        <div id="reportTable3Div" ></div>
                        <table id="reportTable3" class="table table-bordered table-striped text-nowrap" ></table>
                    </div>
					<hr>
					<div class="form-group">
						<div class="col-sm-offset-5 col-sm-6">
							<button type="button" class="btn btn-default" data-dismiss="modal">返回</button>
						</div>
					</div>
<input type="hidden" name="author" value="admin">
<input type="hidden"  name="glueType" value="BEAN">
<input type="hidden"  name="executorParam">
<input type="hidden"  name="fixedParameter">
<input type="hidden"  name="dynamicParameter" >
<input type="hidden"  name="executorHandler" >
<input type="hidden"  name="modelParameters" >
<input type="hidden"  id="tasktype" name="tasktype" value="${tasktype}" >
<input type="hidden" name="glueRemark" value="GLUE代码初始化" >
<textarea name="glueSource" style="display:none;" ></textarea>
<textarea class="glueSource_java" style="display:none;" >
package com.xxl.job.service.handler;

import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;

public class DemoGlueJobHandler extends IJobHandler {

	@Override
	public ReturnT<String> execute(String... params) throws Exception {
		XxlJobLogger.log("XXL-JOB, Hello World.");
		return ReturnT.SUCCESS;
	}

}
</textarea>
<textarea class="glueSource_shell" style="display:none;" >
#!/bin/bash
echo "xxl-job: hello shell"

echo "脚本位置：$0"
echo "参数数量：$#"
for param in $*
do
    echo "参数 : $param"
    sleep 1s
done

echo "Good bye!"
exit 0
</textarea>
<textarea class="glueSource_python" style="display:none;" >
#!/usr/bin/python
# -*- coding: UTF-8 -*-
import time
import sys

print "xxl-job: hello python"
print "脚本文件：", sys.argv[0]
for i in range(1, len(sys.argv)):
	time.sleep(1)
	print "参数", i, sys.argv[i]

print "Good bye!"
exit(0)<#--
import logging
logging.basicConfig(level=logging.DEBUG)
logging.info("脚本文件：" + sys.argv[0])
-->
</textarea>
<textarea class="glueSource_nodejs" style="display:none;" >
#!/usr/bin/env node
console.log("xxl-job: hello nodejs")

var arguments = process.argv

console.log("脚本文件: " + arguments[1])
for (var i = 2; i < arguments.length; i++){
	console.log("参数 %s = %s", (i-1), arguments[i]);
}

console.log("Good bye!")
process.exit(0)
</textarea>
				</form>
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
		<input style="display:none" id="jobInfoId" value=${jobInfo.id}></input>
<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/jobflowlog.detail.index.1.js"></script>

<script src="${request.contextPath}/static/adminlte/bootstrap/js/bootstrap-select.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table-edit1.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-select.js"></script>

<script src="${request.contextPath}/static/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js"></script>



<script src="${request.contextPath}/static/plugins/bootstrap-treeview/js/bootstrap-treeview.js"></script>
<!--表格编辑-->
<script src="${request.contextPath}/static/plugins/table/bootstrap-table-edit.js"></script>
<script>
    function getbutton(row) {
        var html = '<p id="' + row.id + '" >' + '<@shiro.hasPermission name="productmodeledit"><button class="btn btn-warning btn-xs update" type="button">编辑</button></@shiro.hasPermission>  ' +
                '<@shiro.hasPermission name="productmodeldel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="deleteParameter(this);">删除</button></@shiro.hasPermission>  ' + '</p>';
        return html;
    }
</script>
<script src="${request.contextPath}/static/js/processmeta/processmeta.index.1.js"></script>
<script src="${request.contextPath}/static/plugins/ajaxFileUpload/ajaxFileUpload.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js"></script>
</body>
</html>
