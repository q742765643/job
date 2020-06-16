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
    <style>
        .help-block,
        .block-error,
        .upDate-error{
        color: #a94442;
    }
    </style>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if>">
<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>产品生产
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
                    <@shiro.hasPermission name="taskadd"><button class="btn btn-primary btn-success add" type="button">新增任务</button></@shiro.hasPermission>
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

<!-- footer -->

<!-- job新增.模态框 -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" >新增任务</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div id="rootwizard">
                        <ul>
                            <li><a href="#tab1" data-toggle="tab"><span class="label">1</span> 任务参数</a></li>
                            <li><a href="#tab2" data-toggle="tab"><span class="label">2</span> 固定参数</a></li>
                            <li><a href="#tab3" data-toggle="tab"><span class="label">3</span> 输入参数</a></li>

                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="tab1">
                                <hr>
                                <div class="form-group">
                                    <label for="lastname" class="col-sm-2 control-label">任务名称<font color="red">*</font></label>
                                    <div class="col-sm-10"><input type="text" class="form-control" name="jobDesc" placeholder="请输入“任务名称”" maxlength="100" ></div>
                                </div>
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-2 control-label">任务调度策略<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <select class="form-control" name="executorRouteStrategy" >
										<#list ExecutorRouteStrategyEnum as item>
                                            <option value="${item}" >${item.title}</option>
                                        </#list>
                                        </select>
                                    </div>
                                    <label for="lastname" class="col-sm-2 control-label">任务执行策略<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <!--<select class="form-control" name="jobCron">
                                           <option value="0 0/5 * * * ?" >每隔5分运行</option>
                                           <option value="0 5 * * * ?" >每小时05分运行</option>
                                           <option value="0 0 7 * * ?" >每日7点运行</option>
                                           <option value="0 0 8 1,11,21 * ?" >每旬1日8点运行</option>
                                           <option value="0 0 8 2,12,22 * ?" selected >每旬2日8点运行</option>
                                           <option value="0 0 8 1 * ?" >每月1日8点运行</option>
                                           <option value="0 0 20 * * ?" >每日20点运行</option>
                                           <option value="0 00 20 L * ?" >每月最后一日20时运行</option>
                                       </select>-->
                                        <select class="form-control selectpicker show-tick" data-live-search="true"  name="jobCron" >
										<#list executionStrategyList as eStrategy>
                                            <option value="${eStrategy.dictCode}" >${eStrategy.dictName}</option>
                                        </#list>
                                        </select>
                                        <input type="text" class="form-control" name="jobCronName"  style='display:none' value=''>
                                        <!--<input type="text" class="form-control" name="jobCron" placeholder="请输入“任务执行策略”" maxlength="20" >-->
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-2 control-label">产品<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <select class="form-control selectpicker show-tick productId" onchange="initAtomicAlgorithm()" id="productSelectPicker"  data-live-search="true"  name="productId"  >
										<#list productList as item>
                                            <option value="${item.id}" >${item.name}</option>
                                        </#list>
                                        </select>
                                    </div>
                                    <label for="firstname" class="col-sm-2 control-label">选择算法<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <select class="form-control selectpicker show-tick modelId" id="atomicAlgorithmSelectPicker" data-live-search="true" name="modelId" >
										<#list executorHandlerlist as item>
                                            <option value="${item.id}" >${item.modelName}</option>
                                        </#list>
                                        </select>
                                    </div>
                                </div>
                            <#--<div class="form-group">
                                <label for="firstname" class="col-sm-2 control-label">执行参数<font color="black">*</font></label>
                                <div class="col-sm-4"><input type="text" class="form-control" name="executorParam" placeholder="请输入“执行参数”" maxlength="100" ></div>
                                <label for="lastname" class="col-sm-2 control-label">子任务Key<font color="black">*</font></label>
                                <div class="col-sm-4"><input type="text" class="form-control" name="childJobKey" placeholder="请输入子任务的任务Key,如存在多个逗号分隔" maxlength="100" ></div>
                            </div>-->
                                <div class="form-group">
                                    <label for="lastname" class="col-sm-2 control-label">优先级<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <select class="form-control" name="priority" >
                                            <option value="0" >1</option>
                                            <option value="1" >2</option>
                                            <option value="2" >3</option>
                                            <option value="3" selected >4</option>
                                            <option value="4" >5</option>
                                            <option value="5" >6</option>
                                            <option value="6" >7</option>
                                            <option value="7" >8</option>
                                            <option value="8" >9</option>
                                        </select>
                                    </div>
                                    <label for="lastname" class="col-sm-2 control-label">调度异常处理<font color="red">*</font></label>
                                    <div class="" style='width:85px;float:left;margin-left:14px'>
                                        <select class="form-control" name="executorFailStrategy" id='executorFailStrategy'  style='padding:6px 0px'>
                                            <#list ExecutorFailStrategyEnum as item>
                                                <option value="${item}" >${item.title}</option>
                                            </#list>
                                        </select>
                                    </div>
                                    <label for="lastname" class="control-label" style='width:60px;float:left;margin-right:2px' id='labelChange'>报警邮件</label>
                                    <div class="" style='width:124px;float:left'>
                                        <input style='padding:6px 0px' id='inputChange' type="text" class="form-control" name="alarmEmail" placeholder="多个邮件逗号分隔" maxlength="100" >
                                            <span class="block-error" id='input-error'></span>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="tab2">
                                <div class="form-group">
                                    <div class="col-sm-12">
                                        <div id="reportTable3Div" ></div>
                                        <table id="reportTable3" class="table table-bordered table-striped text-nowrap" ></table>
                                    </div>
                                </div>

                            </div>
                            <div class="tab-pane" id="tab3">
                                <div class="form-group">
                                    <div class="col-sm-12">
                                        <div id="reportTable4Div" ></div>
                                        <table id="reportTable4" class="table table-bordered table-striped text-nowrap"></table>
                                    </div>
                                </div>

                            </div>
                            <ul class="pager wizard">
                                <li class="previous first" style="display:none;"><a href="#">First</a></li>
                                <li class="previous"><a href="#">上一步</a></li>
                                <li class="next last" style="display:none;"><a href="#">Last</a></li>
                                <li class="next"><a href="#">下一步</a></li>
                            </ul>
                        </div>
                    </div>

                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary" id='save'>保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
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

<!-- 更新.模态框 -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" >更新任务</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div id="rootwizard1">
                        <ul>
                            <li><a href="#tab4" data-toggle="tab"><span class="label">1</span> 任务参数</a></li>
                            <li><a href="#tab5" data-toggle="tab"><span class="label">2</span> 固定参数</a></li>
                            <li><a href="#tab6" data-toggle="tab"><span class="label">3</span> 输入参数</a></li>

                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="tab4">
                                <hr>
                                <div class="form-group">
                                    <label for="lastname" class="col-sm-2 control-label">任务名称<font color="red">*</font></label>
                                    <div class="col-sm-10"><input type="text" class="form-control" name="jobDesc" placeholder="请输入“任务名称”" maxlength="50" ></div>
                                </div>
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-2 control-label">任务调度策略<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <select class="form-control" name="executorRouteStrategy" >
										<#list ExecutorRouteStrategyEnum as item>
                                            <option value="${item}" >${item.title}</option>
                                        </#list>
                                        </select>
                                    </div>
                                    <label for="lastname" class="col-sm-2 control-label">任务执行策略<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                    <#--<input type="text" class="form-control" name="jobCron" placeholder="请输入“任务执行策略”" maxlength="100" >-->
                                        <select class="form-control selectpicker show-tick" data-live-search="true"  name="jobCron" >
										<#list executionStrategyList as eStrategy>
                                            <option value="${eStrategy.dictCode}" >${eStrategy.dictName}</option>
                                        </#list>
                                        </select>
                                        <input type="text" class="form-control" name="jobCronName"  style='display:none' value=''>
                                    </div>
                                </div>
                                <div class="form-group">

                                <#--
                                 <label for="firstname" class="col-sm-2 control-label">运行模式<font color="red">*</font></label>
                                 <div class="col-sm-4">
                                     <select class="form-control glueType" name="glueType" disabled >
                                     <#list GlueTypeEnum as item>
                                         <option value="${item}" >${item.desc}</option>
                                     </#list>
                                     </select>
                                 </div>-->
                                    <label for="firstname" class="col-sm-2 control-label">产品<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <select class="form-control selectpicker show-tick productId" id="modelProductSelectPicker" onchange="initAtomicAlgorithm()"  data-live-search="true"  name="productId"  >
										<#list productList as item>
                                            <option value="${item.id}" >${item.name}</option>
                                        </#list>
                                        </select>
                                    </div>
                                    <label for="firstname" class="col-sm-2 control-label">选择算法<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <select class="form-control selectpicker show-tick modelId" id="modelAtomicAlgorithmSelectPicker" data-live-search="true" name="modelId" >
										<#list executorHandlerlist as item>
                                            <option value="${item.id}" >${item.modelName}</option>
                                        </#list>
                                        </select>
                                    </div>
                                </div>
                            <#--				<div class="form-group">
                                                <label for="firstname" class="col-sm-2 control-label">执行参数<font color="black">*</font></label>
                                                <div class="col-sm-4"><input type="hidden" class="form-control" name="executorParam" placeholder="请输入“执行参数”" id="executorParam1" maxlength="100" ></div>
                                                <label for="lastname" class="col-sm-2 control-label">子任务Key<font color="black">*</font></label>
                                                <div class="col-sm-4"><input type="text" class="form-control" name="childJobKey" placeholder="请输入子任务的任务Key,如存在多个逗号分隔" maxlength="100" ></div>
                                            </div>-->
                                <div class="form-group">
                                    <!--<label for="lastname" class="col-sm-2 control-label">子任务key</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="childJobKey" placeholder="请输入子任务的任务Key,如存在多个逗号分隔" maxlength="100" >
                                    </div>
                                    -->
                                    <label for="lastname" class="col-sm-2 control-label">优先级<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <select class="form-control" name="priority" >
                                            <option value="0" >1</option>
                                            <option value="1" >2</option>
                                            <option value="2" >3</option>
                                            <option value="3" >4</option>
                                            <option value="4" >5</option>
                                            <option value="5" >6</option>
                                            <option value="6" >7</option>
                                            <option value="7" >8</option>
                                            <option value="8" >9</option>
                                        </select>
                                    </div>
                                    <label for="lastname" class="col-sm-2 control-label">调度异常处理<font color="red">*</font></label>
                                    <div class="" style='width:85px;float:left;margin-left:14px'>
                                        <select class="form-control" name="executorFailStrategy" style='padding:6px 0' id='upDateExecutorFailStrategy'>
                                            <#list ExecutorFailStrategyEnum as item>
                                                <option value="${item}" >${item.title}</option>
                                            </#list>
                                        </select>
                                    </div>
                                    <label for="lastname" id='upDateLabel' class="control-label" style='width:60px;float:left;margin-right:2px'></label>
                                    <div class="" style='width:124px;float:left'>
                                        <input id='upDatealarmEmail' style='padding:6px 0' type="text" class="form-control" name="alarmEmail" placeholder="" maxlength="100" value ="">
                                            <span class="upDate-error" id='upDate-error'></span>
                                    </div>
                                </div>

                            </div>
                            <div class="tab-pane" id="tab5">
                                <div class="form-group">
                                    <div class="col-sm-12">
                                        <div id="reportTable1Div" ></div>
                                        <table id="reportTable1" class="table table-bordered table-striped text-nowrap" ></table>
                                    </div>
                                </div>

                            </div>
                            <div class="tab-pane" id="tab6">
                                <div class="form-group">
                                    <div class="col-sm-12" >
                                        <div id="reportTable2Div" ></div>
                                        <!--class="table table-bordered table-striped" style="word-break:break-all;" -->
                                        <table id="reportTable2" class="table table-bordered table-striped text-nowrap"  ></table>
                                    </div>
                                </div>

                            </div>
                            <ul class="pager wizard">
                                <li class="previous first" style="display:none;"><a href="#">First</a></li>
                                <li class="previous"><a href="#">上一步</a></li>
                                <li class="next last" style="display:none;"><a href="#">Last</a></li>
                                <li class="next"><a href="#">下一步</a></li>
                            </ul>
                        </div>
                    </div>


                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary"  id='upDateSave'>保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <input type="hidden" name="id" >
                        </div>
                    </div>
                    <input type="hidden" name="author" >

                    <input type="hidden"  name="glueType" value="BEAN">
                    <input type="hidden"  name="executorParam">
                    <input type="hidden"  name="fixedParameter">
                    <input type="hidden"  name="dynamicParameter" >
                    <input type="hidden"  name="executorHandler" >
                    <input type="hidden"  name="modelParameters" >

                </form>
            </div>
        </div>
    </div>
</div>

<!-- prj_path.模态框 -->
<div class="modal fade zdymodal" id="prj_path" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" >创建工程</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div id="rootwizard2">
                        <ul>
                            <li><a href="#tab7" data-toggle="tab"><span class="label"></span> 工程设置</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="tab7">
                                <hr>
                                <div class="form-group">
                                    <label for="lastname" class="col-sm-2 control-label">工程名称<font color="red">*</font></label>
                                    <div class="col-sm-4"><input type="text" class="form-control" name="prjName" placeholder="请输入“工程名称”" maxlength="100" ></div>
                                </div>
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-2 control-label">工程路径<font color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control " name="prjPath" onclick="showtab5(this)"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="button" class="btn btn-primary" onclick="prj_pathSave1()" data-dismiss="modal">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                    <input type="hidden"  name="prj_Path" >
                </form>
            </div>
        </div>
    </div>
</div>

<!-- pan_xml_path.全色影像 模态框 -->
<div class="modal fade zdymodal" id="pan_xml_path" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" >创建工程</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div id="rootwizard3">
                        <ul>
                            <li><a href="#tab8" data-toggle="tab"><span class="label"></span> 全色影像路径设置</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="radio">
                                <label class="radio-inline">
                                    <input type="radio" name="pan_xml_path" onclick="showtabPie5(this)" id="inlineRadio1"> 选择文件
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="pan_xml_path" onclick="showtabPie6(this)" id="inlineRadio2" > 扫描文件夹
                                </label>
                            </div>
                            <div class="tab-pane" id="tab8">
                            <#--<hr>
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-2 control-label">全色影像路径<font color="red">*</font></label>
                                    <div class="col-sm-4">
										<input type="text" class="form-control " name="pan_xml_path" onclick="showtabPie5(this)"/>
                                    </div>
                                </div>-->
                            </div>
                        </div>
                    </div>
                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="button" class="btn btn-primary" onclick="pan_xml_pathSave1()" data-dismiss="modal">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                    <input type="hidden"  name="panxml_path" >
                </form>
            </div>
            <div class="modal-dialog modal-lg" id="divPie"></div>
        </div>
    </div>
</div>

<!-- mss_xml_path.模态框 -->
<div class="modal fade zdymodal" id="mss_xml_path" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" >创建工程</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div id="rootwizard4">
                        <ul>
                            <li><a href="#tab9" data-toggle="tab"><span class="label"></span> 多光谱影像路径设置</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="radio">
                                <label class="radio-inline">
                                    <input type="radio" name="mss_xml_path" onclick="showtabPie7(this)" id="inlineRadio1"> 选择文件
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="mss_xml_path" onclick="showtabPie6(this)" id="inlineRadio2" > 扫描文件夹
                                </label>
                            </div>
                        <#--<div class="tab-pane" id="tab9">
                            <hr>
                            <div class="form-group">
                                <label for="firstname" class="col-sm-2 control-label">多光谱影像路径<font color="red">*</font></label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control " name="mss_xml_path" onclick="showtabPie5(this)"/>
                                </div>
                            </div>
                        </div>-->
                        </div>
                    </div>
                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="button" class="btn btn-primary" onclick="mss_xml_pathSave1()" data-dismiss="modal">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                    <input type="hidden"  name="mssxml_path" >
                </form>
            </div>
            <div class="modal-dialog modal-lg" id="divPie1"></div>
        </div>
    </div>
</div>



<ul class='ztree' id='tree' > </ul>
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
<script>
    function getbutton(row,logUrl,codeBtn,pause_resume) {
        var html = '<p id="'+ row.id +'" >'+
                '<@shiro.hasPermission name="executeonce"><button class="btn btn-primary btn-xs job_operate" _type="job_trigger" type="button">执行一次</button> </@shiro.hasPermission>'+
                '<@shiro.hasPermission name="executepause">'+pause_resume+'</@shiro.hasPermission>&nbsp&nbsp&nbsp&nbsp&nbsp'+
                '<@shiro.hasPermission name="taskedit"><button class="btn btn-warning btn-xs update" type="button">编辑</button></@shiro.hasPermission>  '+
                codeBtn +
                '<@shiro.hasPermission name="taskdel"><button class="btn btn-danger btn-xs job_operate" _type="job_del" type="button">删除</button></@shiro.hasPermission>&nbsp&nbsp&nbsp&nbsp&nbsp'+
                '<@shiro.hasPermission name="tasklog"><button class="btn btn-primary btn-xs" type="job_del" type="button" onclick="windowOpen(\'' + logUrl + '\')" >日志</button></@shiro.hasPermission>'+
                '</p>';
        return html;
    }
</script>
<script src="${request.contextPath}/static/js/jobinfo.index.1.js"></script>
<script src="${request.contextPath}/static/adminlte/bootstrap/js/bootstrap-select.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table-edit1.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-select.js"></script>

<script src="${request.contextPath}/static/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js"></script>
<script>


    $('#rootwizard').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard1').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard2').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard3').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard4').bootstrapWizard({'tabClass': 'bwizard-steps'});
    function windowOpen(url) {
        window.open(url,"_self");
    }

    function initAtomicAlgorithm(){
        if($("#productSelectPicker").val()){
            $("#atomicAlgorithmSelectPicker").empty();
            $.ajax({
                type: "post",
                dataType: "json",
                url: base_url + "/jobinfo/getAtomicAlgorithms?productId="+$("#productSelectPicker").val(),
                success: function(data) {
                    var options = "";
                    for (var i = 0; i < data.length; i++) {
                        options+="<option value='"+data[i].id+"'>"+data[i].modelName+"</option>";
                    }
                    $("#atomicAlgorithmSelectPicker").html(options); //为Select追加一个Option(下拉项)
                    //    刷新
                    $('#atomicAlgorithmSelectPicker').selectpicker('refresh');
                }
            });
        }

        if($("#modelProductSelectPicker").val()){
            $("#modelAtomicAlgorithmSelectPicker").empty();
            $.ajax({
                type: "post",
                dataType: "json",
                url: base_url + "/jobinfo/getAtomicAlgorithms?productId="+$("#modelProductSelectPicker").val(),
                success: function(data) {
                    var options = "";
                    for (var i = 0; i < data.length; i++) {
                        options+="<option value='"+data[i].id+"'>"+data[i].modelName+"</option>";
                    }
                    $("#modelAtomicAlgorithmSelectPicker").html(options); //为Select追加一个Option(下拉项)
                    //    刷新
                    $('#modelAtomicAlgorithmSelectPicker').selectpicker('refresh');
                }
            });
        }
    }
</script>


</body>
</html>
