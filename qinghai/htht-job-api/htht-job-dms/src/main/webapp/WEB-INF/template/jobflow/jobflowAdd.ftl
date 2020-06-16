<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bootstrap/css/bootstrap-select.css">
    <link href="${request.contextPath}/static/plugins/table/bootstrap-table.min.css" rel="stylesheet"/>
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
    <h1>流程任务管理
        <small>并行支撑平台</small>
    </h1>
</section>

<!-- Main content -->
<section class="content">
    <form class="form-horizontal form" role="form">
        <div class="row">
            <div class="col-sm-12">
                <div class="box">
                    <div class="box-header with-border">
                        <h3 class="box-title">任务参数</h3>
                    </div>
                    <div class="box-body">
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">任务名称<font color="red">*</font></label>
                            <div class="col-sm-4"><input type="text" class="form-control" name="jobDesc"
                                                         placeholder="请输入“任务名称”" maxlength="50"></div>
                            <label for="lastname" class="col-sm-2 control-label">任务执行策略<font color="red">*</font></label>
                            <div class="col-sm-4">
	                            <select class="form-control selectpicker show-tick" data-live-search="true"  name="jobCron" >
										<#list executionStrategyList as eStrategy>
                                            <option value="${eStrategy.dictCode}" >${eStrategy.dictName}</option>
										</#list>
                                </select>
                                <input type="text" class="form-control" name="jobCronName"  style='display:none' value=''>
                            	<#--<input type="text" class="form-control" name="jobCron" placeholder="请输入“任务执行策略”" maxlength="20">-->
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">流程<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <select class="form-control selectpicker show-tick modelId" data-live-search="true"
                                        name="modelId">
                                    <option value="">请选择</option>
                                <#list flowModelList as item>
                                    <option value="${item.id}">${item.processCHName}</option>
                                </#list>
                                </select>
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">报警邮件</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="alarmEmail" id='inputChange' placeholder="请输入“报警邮件”，多个邮件地址逗号分隔" maxlength="100">
                                <span class="block-error" id='input-error'></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">优先级<font color="red">*</font></label>
                            <div class="col-sm-4">
                                        <select class="form-control" name="priority" >
                                            <option value="0" >1</option>
                                            <option value="1" >2</option>
                                            <option value="2" >3</option>
                                            <option value="3" selected>4</option>
                                            <option value="4" >5</option>
                                            <option value="5" >6</option>
                                            <option value="6" >7</option>
                                            <option value="7" >8</option>
                                            <option value="8" >9</option>
                                        </select>
                            </div>
                            <label for="firstname" class="col-sm-2 control-label">路由策略<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <select class="form-control" name="executorRouteStrategy" >
                                <#list ExecutorRouteStrategyEnum as item>
                                    <option value="${item}" >${item.title}</option>
                                </#list>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-12">
                                <table id="reportTable" class="table table-bordered table-striped text-nowrap"></table>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-5 col-sm-6">
                                <button type="submit" class="btn btn-primary"  id='save'>保存</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal" onclick="javascript :history.back(-1);">取消</button>
                            </div>
                        </div>
                        <input type="hidden"  name="dynamicParameter" id="dynamicParameter">
                         <input type="hidden"  name="tasktype" value="${tasktype}" id='tasktypevalue'>
                    </div>
                </div>
            </div>
        </div>
    </form>

</section>
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
                                    <div class="col-sm-4"><input type="text" class="form-control" name="prjName" placeholder="请输入“工程名称”" maxlength="50" ></div>
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

<!-- pan_xml_path.模态框 -->
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
                                        <input type="text" class="form-control " name="pan_xml_path" onclick="showtab5(this)"/>
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
                                        <input type="text" class="form-control " name="mss_xml_path" onclick="showtab5(this)"/>
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

<!-- ref_xml_path.模态框 -->
<div class="modal fade zdymodal" id="ref_xml_path" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >创建工程</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
                    <div id="rootwizard5">
                        <ul>
                            <li><a href="#tab10" data-toggle="tab"><span class="label"></span> 基准影像路径设置</a></li>
                        </ul>
                        <div class="tab-content">
                        	<div class="radio">
							  	<label class="radio-inline">
								  <input type="radio" name="ref_xml_path" onclick="showtabPie8(this)" id="inlineRadio1"> 选择文件
								</label>
								<label class="radio-inline">
								  <input type="radio" name="ref_xml_path" onclick="showtabPie6(this)" id="inlineRadio2" > 选择文件夹
								</label>
							</div>
                        </div>
                    </div>
					<hr>
					<div class="form-group">
						<div class="col-sm-offset-5 col-sm-6">
							<button type="button" class="btn btn-primary" onclick="ref_xml_pathSave1()" data-dismiss="modal">保存</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						</div>
					</div>
					<input type="hidden"  name="refxml_path" >
                </form>
         	</div>
         	<div class="modal-dialog modal-lg" id="divPie2"></div>
		</div>
	</div>
</div>

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
<script src="${request.contextPath}/static/js/jobflow/jobflowAdd.js"></script>
<script src="${request.contextPath}/static/adminlte/bootstrap/js/bootstrap-select.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bootstrap/js/bootstrap-select.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table-edit1.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-select.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js"></script>
<script>
    $('#rootwizard2').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard3').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard4').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard5').bootstrapWizard({'tabClass': 'bwizard-steps'});
</script>
</body>
</html>
