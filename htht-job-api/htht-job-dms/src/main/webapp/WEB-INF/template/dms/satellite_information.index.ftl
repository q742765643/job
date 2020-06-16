<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bootstrap/css/bootstrap-select.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/table/bootstrap-table.min.css" />
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap-wizard/wizard.css" />
    <!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">
	<style>
        .tab{
            table-layout: fixed;
        }
        .modal-content{
            width: 900px;
        }
        /*.modal-dialog {
            position: absolute;
            top: 20%;
            left: 20%;
        }*/
        #marginBox {
            background-color: #ffffff;
            position: absolute;
            top: 50%;
            left: 50%;
            width:500px;
            transform: translate(-50%,-50%);
            /*margin-top: -120px; !* negative half of the height *!*/
        }
        #addModal .modal-dialog,#addModal1 .modal-dialog,#updateModal .modal-dialog,#updateModal1 .modal-dialog{
            position:absolute;
            top: 0px;
            left: 0px;
        }
        #addModal  .modal-content{
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%,5%);
        }
        #addModal1  .modal-content{
            width: 600px;
            position: absolute;
            overflow: hidden;
            top: 50%;
            left: 50%;
            transform: translate(-40%,85%);
        }
        #updateModal  .modal-content{
            position: absolute;
            overflow: hidden;
            top: 50%;
            left: 50%;
            transform: translate(-42%,15%);
        }
        #updateModal1  .modal-content{
            width: 600px;
            position: absolute;
            overflow: hidden;
            top: 50%;
            left: 50%;
            transform: translate(-40%,85%);
        }
    </style>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">


        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>卫星信息展示
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
                <div class="col-sm-3">
                    <p>
                        <button style="display:none" class="btn btn-info btn-xs add" type="hidden" id="treeAdd">添加</button>
                        <button style="display:none" class="btn btn-warning btn-xs update" type="hidden" id="treeUpdate">编辑</button>
                        <button  style="display:none" class="btn btn-danger btn-xs delete" type="hidden" id="treeDel">删除</button>
                    </p>
                    <div id="treeview5" class=""></div>

                </div>
                <div class="col-sm-9">
                    <div class="row">
                        <div class="col-xs-2">
                                <input type="text" class="form-control" id="fSatelliteid" autocomplete="on" placeholder="按产品名称搜索...">
                                <input type="hidden" class="form-control" id="catalogCode" autocomplete="on" value="">
                        </div>
                        <div class="col-xs-2">
                                <input type="text" class="form-control" id="fCloudamount" autocomplete="on" placeholder="按云覆盖量搜索...">
                        </div>
                        <div class="col-xs-4">
		              		<div class="input-group">
		                		<span class="input-group-addon">
			                  		生产时间
			                	</span>
			                	<input type="text" class="form-control" id="filterTime" readonly >
			              	</div>
			            </div>
			            <div class="col-xs-2">
		                    <div class="input-group">
		                        <span class="input-group-addon">数据等级</span>
		                        <select class="form-control" name="fLevel" id="fLevel" style="width:140%;">
		                            <option value="L1" >L1</option>
		                            <option value="L2" >L2</option>
		                        </select>
		                    </div>
		                </div>
                        <div class="col-xs-2">
                            <button type="hidden" class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp</button>
                        </div>
                        <div class="col-xs-2">
                                <#--<input type="hidden" class="form-control" id="mark" autocomplete="on" placeholder="按产品标识搜索...">-->
                        </div>
                        <div class="col-xs-1">
                                <#--<button class="btn btn-primary btn-success add" id="archiveRulesAdd" type="button">添加入库规则</button>-->
                        </div>
                        
                    </div>
                    <p></p>
                    <div class="box">
                        <div class="box-body">
                            <table id="meataImgAndInfo" class="table table-bordered table-striped"
                                   style="word-break:break-all;">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th name="f_extractdatapath">快视图</th>
                                    <th name="fSatelliteid">卫星标识</th>
                                    <th name="fSensorid">传感器标识</th>
                                    <th name="fProducetime">产品时间</th>
                                    <th name="操作">操作</th>


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
        <!-- /.content -->


<!-- 卫星目录树增 -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">添加卫星目录</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">父級数据名称</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="text" maxlength="100" disabled>
                            <input type="hidden" class="form-control" name="pid">
                            <!-- 父及目录编码 -->
                            <input type="hidden" class="form-control" name="catalogCode">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">节点类型</font></label>
                        <div class="col-sm-4">
                            <select class="form-control" name="yNodeType" >
								<option value="1" >目录节点</option>
	                            <option value="0" >数据节点</option>
                            </select>
                        </div>
                        
                    </div>
                    <div class="form-group">
                    	<#--<label for="modelName" class="col-sm-2 control-label">新增数据名称</label>
                        <div class="col-sm-4">
                        	<input type="text" class="form-control" name="catalogName" maxlength="100">
                        </div>-->
                        <label for="lastname" class="col-sm-2 control-label">新增数据名称</label>
                        <div class="col-sm-4">
                        	<input type="text" class="form-control" name="catalogName" maxlength="100">
                        </div>
                        
                        <label for="lastname" class="col-sm-2 control-label">节点描述</label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="nodeDesc" maxlength="100"></div>
                        <#--<label for="lastname" class="col-sm-2 control-label">副表名称<font color="red">*</font></label>
                        <div class="col-sm-4">
                        	<input type="text" class="form-control" name="subTableName" maxlength="100">
                        </div>-->
                   </div>
                   
                   <div class="form-group" style="display:none" id="mainTableNameD">
                   		<label for="lastname" class="col-sm-2 control-label">数据归属</label>
                        <div class="col-sm-4">
                        	<select class="form-control" name="mainTableName" >
								<option value="HTHT_DMS_META_IMG" selected = "selected" >卫星影像库</option>
								<option value="htht_dms_meta_info" >卫星物理信息库</option>
                            </select>
                        </div>
                    </div>
                    
                    <#--<div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">元素质检表ID</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="xsDid" maxlength="100">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">存放规则<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="storingGrule" maxlength="100"></div>
                    </div>
                    
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">正则表达</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="method" maxlength="100">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">存放数据归档路径ID<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="arcpathid" maxlength="100"></div>
                    </div>-->
                   
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary" >保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <input type="hidden" name="id">
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<#-- 修改卫星目录 -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">修改卫星目录</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">父級数据名称</label>
                        <div class="col-sm-4">
                        	<input type="text" class="form-control" name="text" maxlength="100" disabled>
                            <input type="hidden" class="form-control" name="id">
                            <input type="hidden" class="form-control" name="fid">

                        </div>
                        <label for="modelName" class="col-sm-2 control-label">节点类型</label>
                        <div class="col-sm-4">
                        	<select class="form-control" name="yNodeType" >
								<option value="1" >目录节点</option>
	                            <option value="0" >数据节点</option>
                            </select>

                        </div>
                    </div>
                    <div class="form-group">

                        <label for="lastname" class="col-sm-2 control-label">新增数据名称</label>
                        <div class="col-sm-4">
                        	<input type="text" class="form-control" name="catalogName" >
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">节点描述</label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="nodeDesc" maxlength="100"></div>
                        
                    </div>
                    
                    <div class="form-group" style="display:none" id="mainTableNameU">
                        <label for="lastname" class="col-sm-2 control-label">主表名称</label>
                        <div class="col-sm-4">
                            <select class="form-control" name="mainTableName" >
								<option value="HTHT_DMS_META_IMG" selected = "selected" >卫星影像库</option>
								<option value="htht_dms_meta_info" >卫星物理信息库</option>
                            </select>
                        </div>
                        
                    </div>
                    
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

<!-- 添加入库规则 -->
<div class="modal fade"   id="addArchiveRulesModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width:90%">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">入库添加</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div id="rootwizard">
                        <ul>
                            <li><a href="#tab1" data-toggle="tab"><span name="add" class="label">1</span>入库规则参数</a></li>
                            <li><a href="#tab2" data-toggle="tab"><span name="add" class="label">2</span>字段映射参数</a></li>
                            <#--<li><a href="#tab3" data-toggle="tab"><span name="add" class="label">3</span>字段映射参数</a></li>-->
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="tab1">
                                <hr>
                                
                                <div class="form-group">
									<label for="modelName" class="col-sm-2 control-label">文件类型</label>
			                        <div class="col-sm-4">
			                        	 <select class="form-control" name="filetype" >
											<option value="0" select ="selected">单文件</option>
				                            <option value="1" >多文件</option>
			                            </select>
			                        </div>
			                        <label for="lastname" class="col-sm-2 control-label">是否启用</label>
			                        <div class="col-sm-4">
			                            <select class="form-control" name="rulestatus" >
											<option value="0" select ="selected">启用</option>
				                            <option value="1" >禁用</option>
			                            </select>
			                        </div>
			                    </div>
			                    <div class="form-group">
			                    	<label for="modelName" class="col-sm-2 control-label">数据识别正则表达<font color="red">*</font></label>
			                        <div class="col-sm-4">
			                        	<textarea class="form-control valid" rows="3" name="regexpstr" aria-required="true" aria-invalid="true"></textarea>
			                            <input type="hidden" class="form-control" name="catalogcode">
			                        </div>
			                        <label for="lastname" class="col-sm-2 control-label">xml正则表达<font color="red">*</font></label>
			                        <div class="col-sm-4">
			                        	<textarea class="form-control" rows="3" name="regexpxml"></textarea>
			                        </div>
			                    </div>
			                    <div class="form-group">
			                    	<label for="modelName" class="col-sm-2 control-label">快视图正则表达<font color="red">*</font></label>
			                        <div class="col-sm-4">
			                        	<textarea class="form-control" rows="3" name="regexpjpg"></textarea>
			                        </div>
			                        <label for="lastname" class="col-sm-2 control-label">解析文件标识</label>
			                        <div class="col-sm-4"><input type="text" class="form-control" name="wspFile" maxlength="100">
			                        </div>
			                    </div>
			                    <div class="form-group" id="finalstrD" style="display:none">
			                        <label for="modelName" class="col-sm-2 control-label">替换标识</label>
			                        <div class="col-sm-4">
			                        	<input type="text" class="form-control" name="finalstr" >
			                        </div>
			                        <label for="lastname" class="col-sm-2 control-label">归档文件标识</label>
			                        <div class="col-sm-4">
			                            <input type="text" class="form-control" name="allFile">
			                        </div>
			                    </div>
			                    <div class="form-group">
				                    <label for="modelName" class="col-sm-2 control-label">流程ID</label>
			                        <div class="col-sm-4">
			                        	<select class="form-control selectpicker show-tick" data-live-search="true"  name="flowid" >
											<#list dataFlowList as dataFlow>
			                                    <option value="${dataFlow.id}" >${dataFlow.jobDesc}</option>
											</#list>
			                            </select>
			                        </div>
			                    	<label for="lastname" class="col-sm-2 control-label">数据级别</label>
			                        <div class="col-sm-4">
			                            <input type="text" class="form-control" name="datalevel" maxlength="100">
			
			                        </div>
			                        
			                    </div>
			                    <div class="form-group">
			                        <label for="lastname" class="col-sm-2 control-label">归档磁盘<font color="red">*</font></label>
			                        <div class="col-sm-4">
			                        	<select class="form-control selectpicker show-tick" data-live-search="true"  name="archivdisk" >
											<#list fileDiskList as disk>
			                                    <option value="${disk.id}" >${disk.diskdesc}</option>
											</#list>
			                            </select>
			                        	<#--<input type="text" class="form-control" name="archivdisk" maxlength="100">-->
			                        </div>
			                    </div>
                                
                            </div>
                            <div class="tab-pane" id="tab2">
                                <div class="form-group">
                                    <#--<label for="firstname" class="col-sm-1 control-label">
                                        <a class="insert" href="javascript:addRow('reportTable3');" title="新增行">
                                            <i class="glyphicon glyphicon-plus" id="editTable_add_kjcg"></i>
                                            新增
                                        </a>
                                    </label>-->

                                    <div class="col-sm-12">

                                        <table id="reportTable3"
                                             class="table table-bordered table-striped text-nowrap"></table>
                                    </div>
                                </div>
                            </div>
                            
                            <#--<div class="tab-pane" id="tab3">
                                <div class="form-group">
		                             <table id="joblog_list1" class="table table-bordered table-striped display" width="80%" >
						                <thead>
							            	<tr>
		                                        <th>解析标识</th>
		                                        <th name="f_fieldmanageid" >目标名称</th>
		                                        <th name="f_archivefield" style="display:none"></th>
		                                    </tr>
						                </thead>
		                                <tbody>
										<#if filedManages?exists && filedManages?size gt 0>
										<#list filedManages as fm>
											<tr>
												<td ><input type="text" width="80%"></td>
		                                        <td>${fm.f_cname}</td>
		                                        <td style="display:none">${fm.id}</td>
		                                    </tr>
										</#list>
										</#if>
										</tbody>
									</table>
                                </div>

                            </div>-->
                            
                            <ul class="pager wizard">
                                <li class="previous first" style="display:none;"><a href="#">First</a></li>
                                <li class="previous"><a href="#">上一步</a></li>
                                <li class="next last" style="display:none;"><a href="#">Last</a></li>
                                <li class="next"><a href="#">下一步</a></li>
                            </ul>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary" id="saveArchiveRulesSubmit">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <input type="hidden" name="id">
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- 入库修改 -->
<div class="modal fade" id="updateArchiveRulesModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg"  style="width:90%">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">入库修改</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div id="rootwizard1">
                        <ul>
                            <li><a href="#tab3" data-toggle="tab"><span name="update" class="label">1</span> 模型参数</a></li>
                            <li><a href="#tab4" data-toggle="tab"><span name="update" class="label">2</span> 固定参数</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="tab3">
                                <hr>
                                <div class="form-group">
									<label for="modelName" class="col-sm-2 control-label">文件类型</label>
			                        <div class="col-sm-4">
			                        	<input type="hidden" class="form-control" name="id">
			                        	 <select class="form-control" name="filetype" >
											<option value="0" select ="selected">单文件</option>
				                            <option value="1" >多文件</option>
			                            </select>
			                        </div>
			                        <label for="lastname" class="col-sm-2 control-label">是否启用</label>
			                        <div class="col-sm-4">
			                            <select class="form-control" name="rulestatus" >
											<option value="0" select ="selected">启用</option>
				                            <option value="1" >禁用</option>
			                            </select>
			                        </div>
			                    </div>
			                    <div class="form-group">
			                    	<label for="modelName" class="col-sm-2 control-label">数据识别正则表达</label>
			                        <div class="col-sm-4">
			                        	<textarea class="form-control" rows="3" name="regexpstr"></textarea>
			                            <input type="hidden" class="form-control" name="catalogcode">
			                        </div>
			                        <label for="lastname" class="col-sm-2 control-label">xml正则表达</label>
			                        <div class="col-sm-4">
			                        	<textarea class="form-control" rows="3" name="regexpxml"></textarea>
			                        </div>
			                    </div>
			                    <div class="form-group">
			                    	<label for="modelName" class="col-sm-2 control-label">快视图正则表达</label>
			                        <div class="col-sm-4">
			                        	<textarea class="form-control" rows="3" name="regexpjpg"></textarea>
			                        </div>
			                        <label for="lastname" class="col-sm-2 control-label">解析文件标识</label>
			                        <div class="col-sm-4"><input type="text" class="form-control" name="wspFile" maxlength="100">
			                        </div>
			                    </div>
			                    <div class="form-group" id="finalstrD1" style="display:none">
			                        <label for="modelName" class="col-sm-2 control-label">替换标识</label>
			                        <div class="col-sm-4">
			                        	<input type="text" class="form-control" name="finalstr" >
			                        </div>
			                        <label for="lastname" class="col-sm-2 control-label">归档文件标识</label>
			                        <div class="col-sm-4">
			                            <input type="text" class="form-control" name="allFile">
			                        </div>
			                    </div>
			                    <div class="form-group">
				                    <label for="lastname" class="col-sm-2 control-label">流程ID</label>
				                        <div class="col-sm-4">
				                        	<select class="form-control selectpicker show-tick" data-live-search="true"  name="flowid" >
												<#list dataFlowList as dataFlow>
				                                    <option value="${dataFlow.id}" >${dataFlow.jobDesc}</option>
												</#list>
				                            </select>
			                        </div>
			                    	<label for="modelName" class="col-sm-2 control-label">数据级别</label>
			                        <div class="col-sm-4">
			                            <input type="text" class="form-control" name="datalevel" maxlength="100">
			
			                        </div>
			                        
			                    </div>
			                    <div class="form-group">
			                        <label for="lastname" class="col-sm-2 control-label">归档磁盘<font color="red">*</font></label>
			                        <div class="col-sm-4">
			                        	<#--<input type="text" class="form-control" name="archivdisk" maxlength="100">-->
			                        	<select class="form-control selectpicker show-tick" data-live-search="true"  name="archivdisk" >
											<#list fileDiskList as disk>
			                                    <option value="${disk.id}" >${disk.diskdesc}</option>
											</#list>
			                            </select>
			                        </div>
			                    </div>
                            </div>
                            <div class="tab-pane" id="tab4">
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-1 control-label">
                                        <a class="insert" href="javascript:addRow('reportTable2');" title="新增行">
                                            <i class="glyphicon glyphicon-plus" id="editTable_add_kjcg"></i>
                                            新增
                                        </a>
                                    </label>
                                    <div class="col-sm-12">
                                        <div class=table-responsive">
                                        <table id="reportTable2"
                                             class="table table-bordered table-striped text-nowrap"></table>
                                        </div>
                                    </div>
                                </div>

                            </div>
                            
                            <ul class="pager wizard col-sm-12">
                                <li class="previous first" style="display:none;"><a href="#">First</a></li>
                                <li class="previous"><a href="#">上一步</a></li>
                                <li class="next last" style="display:none;"><a href="#">Last</a></li>
                                <li class="next"><a href="#">下一步</a></li>
                            </ul>
                        </div>
                    </div>


                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary" id="updateArchiveRulesSubmit">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <input type="hidden" name="id">
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<@netCommon.commonScript />
<#--<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>-->
<script src="${request.contextPath}/static/plugins/bootstrap-treeview/js/bootstrap-treeview.js"></script>
<!--表格编辑-->
<script src="${request.contextPath}/static/plugins/table/bootstrap-table.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table-edit.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-select.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bootstrap/js/bootstrap-select.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script>
    function getbutton(row) {
        var html = '<p id="' + row.metaInfo.id + '" >' +
               '<button class="btn btn-warning btn-xs" type="button" onclick="del(this,1);">删除</button>' +
                //'<@shiro.hasPermission name="productdel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="del(this);">删除</button></@shiro.hasPermission>' +
                '</p>';
        return html;
    }
    function getDownloadButton(row) {
        var html = '<p id="' + row.metaInfo.id + '" >' +
               '<button class="btn btn-warning btn-xs" type="button" onclick="downloadFile(this);">下载</button>' +
                //'<@shiro.hasPermission name="productdel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="del(this);">下载</button></@shiro.hasPermission>' +
                '</p>';
        return html;
    }
</script>
<script src="${request.contextPath}/static/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js"></script>
<!-- 引入archiveCatalog JS -->
<script src="${request.contextPath}/static/js/dms/satellite_information.index.1.js"></script>
<script>
    $('#rootwizard').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard1').bootstrapWizard({'tabClass': 'bwizard-steps'});
</script>

</body>
</html>
