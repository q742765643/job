<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bootstrap/css/bootstrap-select.css">
    <link href="${request.contextPath}/static/plugins/table/bootstrap-table.min.css" rel="stylesheet"/>
    <link href="${request.contextPath}/static/plugins/bootstrap-wizard/wizard.css" rel="stylesheet"/>
    <!--引入webuploader CSS-->
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/webuploader-0.1.5/webuploader.css">
    <style>
        .tab {
            table-layout: fixed;
        }

        .modal-content {
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
            width: 500px;
            transform: translate(-50%, -50%);
            /*margin-top: -120px; !* negative half of the height *!*/
        }

        #addModal .modal-dialog, #addModal1 .modal-dialog, #updateModal .modal-dialog, #updateModal1 .modal-dialog {
            position: absolute;
            top: 0px;
            left: 0px;
        }

        #addModal .modal-content {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, 5%);
        }

        #addModal1 .modal-content {
            width: 600px;
            position: absolute;
            overflow: hidden;
            top: 50%;
            left: 50%;
            transform: translate(-40%, 85%);
        }

        #updateModal .modal-content {
            position: absolute;
            overflow: hidden;
            top: 50%;
            left: 50%;
            transform: translate(-42%, 15%);
        }

        #updateModal1 .modal-content {
            width: 600px;
            position: absolute;
            overflow: hidden;
            top: 50%;
            left: 50%;
            transform: translate(-40%, 85%);
        }
    </style>
    <!--解决webuploader与模态框冲突无法渲染问题 -->
    <style>
        #picker div:nth-child(2) {
            width: 100% !important;
            height: 100% !important;
        }
    </style>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">

<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>原子算法
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
    <div class="row">
        <div class="col-sm-3">
            <p>
                <button class="btn btn-info btn-xs add" type="button" id="treeAdd">添加</button>
                <button class="btn btn-warning btn-xs update" type="button" id="treeUpdate">编辑</button>
                <button class="btn btn-danger btn-xs delete" type="button" id="treeDel">删除</button>

            </p>
            <div id="treeview5"></div>
        </div>
        <div class="col-sm-9">
            <div class="row">
                <div class="col-xs-2">
                    <input type="text" class="form-control" id="modelName" autocomplete="on" placeholder="算法名称搜索...">
                    <input type="hidden" class="form-control" id="treeId_find" autocomplete="on">
                </div>
                <div class="col-xs-2">
                    <input type="text" class="form-control" id="modelIdentify" autocomplete="on"
                           placeholder="算法标识搜索...">
                </div>
                <div class="col-xs-2">
                    <button class="btn btn-primary btn-info" id="searchBtn">
                        &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp
                    </button>
                </div>
                <div class="col-xs-1">
                    <@shiro.hasPermission name="productmodeladd">
                        <button class="btn btn-primary btn-success" id="add" data-toggle="button">添加算法</button>
                    </@shiro.hasPermission>
                </div>
            </div>
            <p></p>
            <div class="box">
                <div class="box-body">
                    <table id="parameterTable" class="table table-bordered table-striped"
                           style="word-break:break-all;width: 100%">
                        <thead>
                        <tr>
                            <th>序号</th>
                            <th name="id">id</th>
                            <th name="modelName">算法名称</th>
                            <th name="modelIdentify">算法标识</th>
                        <@shiro.hasPermission name="productmodeluploadpath">
                            <th id="productmodeluploadpath" name="algoPath">算法上传路径</th>
                        </@shiro.hasPermission>
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
<!-- /.content -->
<!-- /.content-wrapper -->
<div class="modal fade" id="margin" tabindex="-1" role="dialog" aria-hidden="true">
    <div id="marginBox">
        <div class="col-sm-12">
            <h2>目标目录</h2>
            <div id="treeview3"></div>
            <div class="form-group">
                <div class="col-sm-offset-5 col-sm-6" style="margin-bottom: 3px;">
                    <button type="button" class="btn btn-primary" id="marginSure">迁移</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal">取消</button>
                    <input type="hidden" name="id">
                </div>
            </div>
        </div>
    </div>

<#--<div id="treeview3" style="width: 300px;margin-left: 700px;margin-top: 200px;"></div>-->
</div>

<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width:100%">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">添加算法</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div id="rootwizard">
                        <ul>
                            <li><a href="#tab1" data-toggle="tab"><span name="add"
                                                                        class="label">1111111111</span>基本参数</a></li>
		                    <@shiro.hasPermission name="alogparametershow">
                          <!--  <li><a href="#add_tab4" data-toggle="tab"><span name="add" class="label">2</span> 上传模型</a></li> -->
                            <li><a href="#tab2" data-toggle="tab"><span name="add" class="label">3</span> 固定参数</a></li>
                            </@shiro.hasPermission>
                            <li><a href="#tab3" data-toggle="tab"><span name="add" class="label">4</span> 输入参数</a></li>
                            <li><a href="#add_tab5" data-toggle="tab"><span name="add" class="label">5</span> 部署节点</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="tab1">
                                <hr>
                                <div class="form-group">
                                    <label for="modelName" class="col-sm-2 control-label">算法名称</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="modelName" placeholder="请输入“模型名称”"
                                               maxlength="50">
                                        <input type="hidden" class="form-control" name="treeId">
                                    </div>
                                    <label for="modelName" class="col-sm-2 control-label">算法标识</label>
                                    <div class="col-sm-4">
                                    <#--  <input type="text" class="form-control" name="modelIdentify"
                                            placeholder="请输入“模型标识”" maxlength="50">-->
                                        <select class="form-control selectpicker show-tick" data-live-search="true"
                                                name="modelIdentify">
										<#list modelIdentificationyList as mi>
                                            <option value="${mi.dictCode}">${mi.dictName}</option>
                                        </#list>
                                        </select>
                                    </div>
                                </div>
                            <@shiro.hasPermission name="alogparametershow">
                                <div class="form-group">
                                    <label for="modelName" class="col-sm-2 control-label">算法加载形式</label>
                                    <div class="col-sm-4">
                                        <select class="form-control" name="type">
                                            <option value="0">列表(路径项为空)</option>
                                            <option value="1">页面(路径项不为空)</option>
                                        </select>
                                    </div>
                                    <label for="modelName" class="col-sm-2 control-label">路径</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="url" placeholder="请输入“路径”"
                                               maxlength="50">
                                    </div>
                                </div>
                            </@shiro.hasPermission>
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-2 control-label">阻塞处理策略<font
                                            color="red">*</font></label>
                                    <div class="col-sm-4">
                                        <select class="form-control" name="executorBlockStrategy">
										<#list ExecutorBlockStrategyEnum as item>
											<#if item.title == "单机并行">
												<option value="${item}" selected="selected">${item.title}</option>
                                            <#else>
                                            	<option value="${item}">${item.title}</option>
                                            </#if>
                                        </#list>
                                        </select>
                                    </div>
                                    <label for="modelName" class="col-sm-2 control-label">算法所需能力</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="dealAmount" value="1"
                                               maxlength="50">
                                    </div>
                                </div>
                                <div class="form-group">
	                               <@shiro.hasPermission name="alogparametershow">
                                    <label for="modelType" class="col-sm-2 control-label">算法类型</label>
                                    <div class="col-sm-4">
                                        <select class="form-control" name="algoType">
                                            <option value="exe">exe</option>
                                            <option value="jar">jar</option>
                                            <option value="cpp">c++</option>
                                            <option value="sh">sh</option>
                                            <option value="py">py</option>
                                        </select>
                                    </div>
                                   </@shiro.hasPermission>
                                    <label for="modelType" class="col-sm-2 control-label">算法上传</label>
                                    <div class="col-sm-4">
                                        <div class="wu-example">
                                            <!--用来存放文件信息-->
                                            <div id="thelist" class="uploader-list thelist111"></div>
                                            <div class="col-sm-6">
                                                <div id="picker" class="picker111">选择文件</div>
                                                <!--<button id="ctlBtn" style="width:90px;height:38px">开始上传</button> -->
                                            </div>
                                            <input type="hidden" name="algoUploadPath">
                                            <input type="hidden" name="algoZipName">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="tab2">
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-1 control-label">
                                        <a class="insert" href="javascript:addRow('reportTable1');" title="新增行">
                                            <i class="glyphicon glyphicon-plus" id="editTable_add_kjcg"></i>
                                            新增
                                        </a>
                                    </label>

                                    <div class="col-sm-12">

                                        <table id="reportTable1"
                                             class="table table-bordered table-striped text-nowrap"></table>
                                    </div>
                                </div>


                            </div>
                            <div class="tab-pane" id="tab3">
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
                                                 class="table table-bordered table-striped text-nowrap "></table>
                                        </div>
                                    </div>
                                </div>

                            </div>
                            <!--      <div class="tab-pane" id="add_tab4">
                                   <hr>
                                     <div class="form-group">
                                         <div class="col-sm-2"></div>

                                      </div>
                                  </div>   -->
                            <div class="tab-pane" id="add_tab5">
                                <div class="form-group">
                                    <table id="joblog_list" class="table table-bordered table-striped display"
                                           width="100%">
                                        <thead>
                                        <tr>
                                        <#--<th name="id" >ID</th>-->
                                            <th name="order">选择</th>
                                            <th name="title">名称</th>
                                            <th name="deploySystem">部署系统</th>
                                            <th name="registryIp">IP地址</th>
                                            <th name="concurrency">总能力</th>
                                        </tr>
                                        </thead>
                                        <tbody>
								<#if nodeList?exists && nodeList?size gt 0>
								<#list nodeList as group>
									<tr>
                                        <td><input type="checkbox" name="deployNode" value=${group.id}></td>
                                        <!--  <td>${group.id}</td> -->
                                        <!--  <td>${group.order}</td> -->
                                        <td>${group.registryKey}</td>
                                        <td>${group.deploySystem}</td>
                                        <td>${group.registryIp}</td>
                                        <td>${group.concurrency}</td>

                                    </tr>
                                </#list>
                                </#if>
                                        </tbody>
                                    </table>
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
                            <button type="button" class="btn btn-primary" id="reportTable1Submit">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <input type="hidden" name="id">
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="addModal1" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width:90%">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">添加原子算法分类</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="lastname" class="col-sm-3 control-label">新增算法分类名称<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="text" maxlength="20"></div>

                        <label for="lastname" class="col-sm-2 control-label" style="display: none">新增算法分类名称<font
                                color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="treeKey" maxlength="20"
                                                     style="display: none" value="processmodel"></div>
                    </div>
                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary" id="saveAtomSubmit">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <input type="hidden" name="id">
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="updateModal1" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width:90%">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">修改原子算法分类</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label" style="display: none">父級算法分类</label>
                        <div class="col-sm-4" style="display: none">
                            <input type="text" class="form-control" name="menu" maxlength="50" disabled>
                            <input type="hidden" class="form-control" name="parentId">
                            <input type="hidden" class="form-control" name="menuId">
                        </div>
                        <label for="lastname" class="col-sm-3 control-label">修改算法分类名称<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="text" maxlength="20"></div>

                        <label for="lastname" class="col-sm-2 control-label" style="display: none">修改算法分类名称<font
                                color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="treeKey" maxlength="20"
                                                     value="processmodel" style="display: none"></div>
                    </div>
                <#--<div class="form-group">
                    <label for="modelName" class="col-sm-2 control-label">备注</label>
                    <div class="col-sm-10">
                        <textarea class="form-control" name="bz"></textarea>
                    </div>
                </div>-->
                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary" id="updateAtomSubmit">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <input type="hidden" name="id">
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width:90%">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">修改算法</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div id="rootwizard1">
                        <ul>
                            <li><a href="#tab4" data-toggle="tab"><span name="update" class="label">1</span> 模型参数</a>
                            </li>
                            <@shiro.hasPermission name="alogparametershow">
                        <!--    <li><a href="#tab7" data-toggle="tab"><span name="update" class="label">2</span> 上传模型</a></li>  -->
                            <li><a href="#tab5" data-toggle="tab"><span name="update" class="label">3</span> 固定参数</a>
                            </li>
                            </@shiro.hasPermission>
                            <li><a href="#tab6" data-toggle="tab"><span name="update" class="label">4</span> 输入参数</a>
                            </li>
                            <li><a href="#tab8" data-toggle="tab"><span name="update" class="label">5</span> 部署节点</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="tab4">
                                <hr>
                                <div class="form-group col-sm-6">
                                    <label for="modelName" class="col-sm-4 control-label">模型名称</label>
                                    <div class="col-sm-8">
                                        <input type="text" class="form-control" name="modelName" placeholder="请输入“描述”"
                                               maxlength="50">
                                        <input type="hidden" class="form-control" name="id">
                                        <input type="hidden" class="form-control" name="fixedParameter">
                                        <input type="hidden" class="form-control" name="dynamicParameter">
                                    </div>
                                </div>
                                <div class="form-group col-sm-6">
                                    <label for="modelName" class="col-sm-5 control-label">模型标识</label>
                                    <div class="col-sm-7">
                                    <#-- <input type="text" class="form-control" name="modelIdentify"
                                           placeholder="请输入“描述”" maxlength="50">-->
                                        <select class="form-control selectpicker show-tick" data-live-search="true"
                                                name="modelIdentify">
										<#list modelIdentificationyList as mi>
                                            <option value="${mi.dictCode}">${mi.dictName}</option>
                                        </#list>
                                        </select>
                                    </div>
                                </div>
                                <@shiro.hasPermission name="alogparametershow">
                                <div class="form-group col-sm-6">
                                    <label for="modelName" class="col-sm-4 control-label">模型加载形式</label>
                                    <div class="col-sm-8">
                                        <select class="form-control" name="type">
                                            <option value="0">列表(路径项为空)</option>
                                            <option value="1">页面(路径项不为空)</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group col-sm-6">
                                    <label for="modelName" class="col-sm-5 control-label">路径</label>
                                    <div class="col-sm-7">
                                        <input type="text" class="form-control" name="url" placeholder="请输入“路径”"
                                               maxlength="50">
                                    </div>
                                </div>
                                </@shiro.hasPermission>
                                <div class="form-group col-sm-6">
                                    <label for="firstname" class="col-sm-4 control-label">阻塞处理策略<font
                                            color="red">*</font></label>
                                    <div class="col-sm-8">
                                        <select class="form-control" name="executorBlockStrategy">
											<#list ExecutorBlockStrategyEnum as item>
                                                <option value="${item}">${item.title}</option>
                                            </#list>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group col-sm-6">
                                    <label for="modelName" class="col-sm-5 control-label">算法所需能力</label>
                                    <div class="col-sm-7">
                                        <input type="text" class="form-control" name="dealAmount" value="1"
                                               maxlength="50">
                                    </div>
                                </div>
                                <@shiro.hasPermission name="alogparametershow">
                                <div class="form-group col-sm-6">
                                    <label for="modelType" class="col-sm-4 control-label">模型类型</label>
                                    <div class="col-sm-8">
                                        <select class="form-control" name="algoType">
                                            <option value="exe">exe</option>
                                            <option value="jar">jar</option>
                                            <option value="cpp">c++</option>
                                            <option value="sh">sh</option>
                                            <option value="py">py</option>
                                        </select>
                                    </div>
                                </div>
                                </@shiro.hasPermission>

                            <#--<div class="form-group col-sm-6">-->
                            <#--<label for="firstname" class="col-sm-4 control-label">算法上传路径</label>-->
                            <#--<div class="col-sm-8">-->
                            <#--&lt;#&ndash;<label for="firstname" class="form-group" name="algoPath"></label>&ndash;&gt;-->
                            <#--<input type="text" class="form-control" name="uploadPath" value="" maxlength="50">-->
                            <#--</div>-->
                            <#--</div>-->
                                <div class="form-group col-sm-6">
                                    <label for="firstname" class="col-sm-5 control-label">是否重新上传算法</label>
                                    <div class="col-sm-7">
                                        <label class="checkbox-inline">
                                            <input type="radio" name="againUpload" id="againUpload1"
                                                   value="1"> 是
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="radio" name="againUpload" id="againUpload0"
                                                   value="0" checked> 否
                                        </label>

                                    <#--<label for="firstname" class="form-group">-->
                                    <#--<input name="againUpload" id="againUpload1"  type="radio" value="1"  >是 </input>-->
                                    <#--<input name="againUpload" id="againUpload0" type="radio" value="0" checked="checked" >否 </input>-->
                                    <#--</label>-->
                                    </div>
                                </div>

                                <div class="form-group col-sm-6" style="display: none;" id="againUploadDiv">
                                    <label for="modelType" class="col-sm-5 control-label"></label>
                                    <div class="col-sm-7">
                                        <div class="wu-example">
                                            <!--用来存放文件信息-->
                                            <div id="thelist" class="uploader-list thelist111"></div>
                                            <div>
                                                <div id="picker" class="picker111">选择文件</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="tab5">
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-1 control-label">
                                        <a class="insert" href="javascript:addRow('reportTable3');" title="新增行">
                                            <i class="glyphicon glyphicon-plus" id="editTable_add_kjcg"></i>
                                            新增
                                        </a>
                                    </label>
                                    <div class="col-sm-12">
                                        <div class=table-responsive">
                                            <table id="reportTable3"
                                                 class="table table-bordered table-striped text-nowrap"></table>
                                        </div>
                                    </div>
                                </div>

                            </div>
                            <div class="tab-pane" id="tab6">
                                <div class="form-group">
                                    <label for="firstname" class="col-sm-1 control-label">
                                        <a class="insert" href="javascript:addRow('reportTable4');" title="新增行">
                                            <i class="glyphicon glyphicon-plus" id="editTable_add_kjcg"></i>
                                            新增
                                        </a>
                                    </label>
                                    <div class="col-sm-12">
                                        <table id="reportTable4"
                                             class="table table-bordered table-striped text-nowrap"></table>
                                    </div>
                                </div>

                            </div>
                            <!--    <div class="tab-pane" id="tab7">
                                 <hr>
                                   <div class="form-group">
                                        <label for="firstname" class="col-sm-2 control-label">算法路径</label>
                                        <label for="firstname" class="col-sm-8 control-label">是否重新上传算法</label>
                                        <div class="col-sm-2">
                                        <label for="firstname"> <input name="againUpload" id="againUpload1"  type="radio" value="1"  >是 </input></label>
                                        <label for="firstname"> <input name="againUpload" id="againUpload0" type="radio" value="0" checked="checked" >否 </input></label>
                                        </div>
                                    </div>
                                   <div class="form-group" style="display: none;" id="againUploadDiv">
                                       <div class="col-sm-2"></div>
                                       <div  class="wu-example col-sm-8">
                                            <div id="thelist" class="uploader-list thelist111"></div>
                                            <div class="col-sm-8">
                                                <div id="picker" class="col-sm-5 picker111">选择文件</div>
                                            </div>
                                       </div>
                                    </div>
                                </div>  -->

                            <div class="tab-pane" id="tab8">
                                <div class="form-group">
                                    <table id="updateNode_list" class="table table-bordered table-striped display"
                                           width="100%">
                                        <thead>
                                        <tr>
                                        <#--<th name="id" >ID</th>-->
                                            <th name="order">选择</th>
                                            <th name="title">名称</th>
                                            <th name="deploySystem">部署系统</th>
                                            <th name="registryIp">IP地址</th>
                                            <th name="concurrency">总能力</th>
                                        </tr>
                                        </thead>
                                        <tbody>
								<#if nodeList?exists && nodeList?size gt 0>
								<#list nodeList as group>
									<tr>
                                        <td><input type="checkbox" name="uploadDeployNode"
                                                   value=${group.id} id=${group.id}></td>
                                        <!--  <td>${group.id}</td> -->
                                        <!--  <td>${group.order}</td> -->
                                        <td>${group.registryKey}</td>
                                        <td>${group.deploySystem}</td>
                                        <td>${group.registryIp}</td>
                                        <td>${group.concurrency}</td>
                                    </tr>
                                </#list>
                                </#if>
                                        </tbody>
                                    </table>
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

                <#--<hr style="margin-top: 301px;">-->
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="button" class="btn btn-primary" id="reportTableUpdateSubmit">保存</button>
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
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script>
    function getbutton(row) {
        var html = '<p id="' + row.id + '" >' + '<@shiro.hasPermission name="productmodeledit"><button class="btn btn-warning btn-xs update" type="button">编辑</button></@shiro.hasPermission>  ' +
                '<@shiro.hasPermission name="productmodeldel"><button class="btn btn-danger btn-xs model_operate" _type="model_migrate" type="button" onclick="migrateParameter(this);">迁移</button></@shiro.hasPermission>  ' +
                '<@shiro.hasPermission name="productmodeldel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="deleteParameter(this);">删除</button></@shiro.hasPermission>  ' +
                '</p>';
        return html;
    }
</script>
<script src="${request.contextPath}/static/adminlte/bootstrap/js/bootstrap-select.min.js"></script>
<script src="${request.contextPath}/static/js/processmeta/processmeta.index.1.js"></script>
<script src="${request.contextPath}/static/plugins/ajaxFileUpload/ajaxFileUpload.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js"></script>
<!--引入webuploader JS-->
<script src="${request.contextPath}/static/plugins/webuploader-0.1.5/webuploader.js"></script>
<script>
    $('#rootwizard').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard1').bootstrapWizard({'tabClass': 'bwizard-steps'});
</script>
</body>
</html>
