<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link href="${request.contextPath}/static/plugins/table/bootstrap-table.min.css" rel="stylesheet"/>
    <link href="${request.contextPath}/static/plugins/bootstrap-wizard/wizard.css" rel="stylesheet"/>

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">

<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>流程管理
        <small>流程管理中心</small>
    </h1>
</section>

<!-- Main content -->
<section class="content">
    <div class="row">
        <div class="col-sm-12">
            <div class="row">
                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">名称</span>
                        <input type="text" class="form-control" id="modelName" autocomplete="on">
                    </div>
                </div>
                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">标识</span>
                        <input type="text" class="form-control" id="modelIdentify" autocomplete="on">
                    </div>
                </div>
                <div class="col-xs-2">
                    <button class="btn btn-block btn-info" id="searchBtn">搜索</button>
                </div>
                <div class="col-xs-2">
                    <@shiro.hasPermission name="jobprocessadd">
                    <button class="btn btn-primary" id="add" data-toggle="button">添加流程</button>
                    </@shiro.hasPermission>
                </div>

            </div>
            <div class="box">
                <div class="box-body">
                    <table id="parameterTable" class="table table-bordered table-striped" style="word-break:break-all;">
                        <thead>
                        <tr>
                            <th>序号</th>
                            <th name="id">id</th>
                            <th name="modelName">流程名称</th>
                            <th name="modelIdentify">流程标识</th>
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


<div class="modal fade" id="addJobprocess" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">添加模型</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div id="rootwizard">
                        <ul>
                            <li><a href="#tab1" data-toggle="tab"><span class="label">1</span> 模型参数</a></li>
                            <li><a href="#tab2" data-toggle="tab"><span class="label">2</span> 固定参数</a></li>
                            <li><a href="#tab3" data-toggle="tab"><span class="label">3</span> 输入参数</a></li>

							
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="tab1">
                                <hr>
                                <div class="form-group">
                                    <label for="modelName" class="col-sm-2 control-label">模型名称</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="modelName" placeholder="请输入“描述”"
                                               maxlength="100">
                                        <input type="hidden" class="form-control" name="treeId">
                                    </div>
                                    <label for="modelName" class="col-sm-2 control-label">模型标识</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="modelIdentify"
                                               placeholder="请输入“描述”" maxlength="100">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="modelName" class="col-sm-2 control-label">模型名称</label>
                                    <div class="col-sm-4">
                                        <select class="form-control" name="type">
                                            <option value="0">全动态</option>
                                            <option value="1">半动态</option>
                                        </select>
                                    </div>
                                    <label for="modelName" class="col-sm-2 control-label">路径</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="url" placeholder="请输入“路径”"
                                               maxlength="100">
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
                                        <table id="reportTable2"
                                             class="table table-bordered table-striped text-nowrap"></table>
                                    </div>
                                </div>

                            </div>
                            <div class="tab-pane" id="tab4">
                                <div class="form-group">
                                    <label for="uploadFile" class="col-sm-2 control-label">上传文件</label>

                                    <div class="col-sm-12">
                                        <table id="reportTable2"
                                             class="table table-bordered table-striped text-nowrap"></table>
                                    </div>
                                </div>

                            </div>
                            <div class="tab-pane" id="tab5">
                                <div class="form-group">
                                    <label for="deployNode" class="col-sm-2 control-label">部署节点</label>

                                    <div class="col-sm-12">
                                        <table id="reportTable2"
                                             class="table table-bordered table-striped text-nowrap"></table>
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


<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">修改模型</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div id="rootwizard1">
                        <ul>
                            <li><a href="#tab4" data-toggle="tab"><span class="label">1</span> 模型参数</a></li>
                            <li><a href="#tab5" data-toggle="tab"><span class="label">2</span> 固定参数</a></li>
                            <li><a href="#tab6" data-toggle="tab"><span class="label">3</span> 输入参数</a></li>

                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="tab4">
                                <hr>
                                <div class="form-group">
                                    <label for="modelName" class="col-sm-2 control-label">模型名称</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="modelName" placeholder="请输入“描述”"
                                               maxlength="100">
                                        <input type="hidden" class="form-control" name="id">
                                        <input type="hidden" class="form-control" name="fixedParameter">
                                        <input type="hidden" class="form-control" name="dynamicParameter">
                                    </div>
                                    <label for="modelName" class="col-sm-2 control-label">模型标识</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="modelIdentify"
                                               placeholder="请输入“描述”" maxlength="100">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="modelName" class="col-sm-2 control-label">模型名称</label>
                                    <div class="col-sm-4">
                                        <select class="form-control" name="type">
                                            <option value="0">全动态</option>
                                            <option value="1">半动态</option>
                                        </select>
                                    </div>
                                    <label for="modelName" class="col-sm-2 control-label">路径</label>
                                    <div class="col-sm-4">
                                        <input type="text" class="form-control" name="url" placeholder="请输入“路径”"
                                               maxlength="100">
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

                                        <table id="reportTable3"
                                             class="table table-bordered table-striped text-nowrap"></table>
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
<script>
    function getbutton(row) {
        var html = '<p id="' + row.id + '" >' + '<@shiro.hasPermission name="jobprocessedit"><button class="btn btn-warning btn-xs update" type="button">编辑</button></@shiro.hasPermission>  ' +
                '<@shiro.hasPermission name="jobprocessdel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="deleteParameter(this);">删除</button></@shiro.hasPermission>  ' + '</p>';
        return html;
    }
</script>
<script src="${request.contextPath}/static/js/jobprocess/jobprocess.index.1.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-wizard/jquery.bootstrap.wizard.min.js"></script>
<script>
    $('#rootwizard').bootstrapWizard({'tabClass': 'bwizard-steps'});
    $('#rootwizard1').bootstrapWizard({'tabClass': 'bwizard-steps'});

</script>
</body>
</html>
