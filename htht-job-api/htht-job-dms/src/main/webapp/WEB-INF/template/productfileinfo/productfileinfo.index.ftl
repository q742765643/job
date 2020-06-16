<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link href="${request.contextPath}/static/plugins/table/bootstrap-table.min.css" rel="stylesheet"/>
    <style>
        .dataTable .row-details.row-details-close {
            background: url("${request.contextPath}/static/adminlte/plugins/datatables/images/datatable-row-openclose.jpg") no-repeat 0 0;
        }

        .dataTable .row-details.row-details-open {
            background: url("${request.contextPath}/static/adminlte/plugins/datatables/images/datatable-row-openclose.jpg") no-repeat 0 -23px !important;
        }
        .dataTable .row-details {
            margin-top: 3px;
            display: inline-block;
            cursor: pointer;
            width: 14px;
            height: 14px;
        }
    </style>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">

    <!-- Content Wrapper. Contains page content -->
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>产品结果
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

                    </p>
                    <div id="treeview5" class=""></div>

                </div>
                <div class="col-sm-9">
                    <#--<div class="row">-->
                        <#--<div class="col-xs-2">-->
                                <#--<input type="hidden" class="form-control" id="id" autocomplete="on">-->
                                <#--<input type="text" class="form-control" id="issue" autocomplete="on" placeholder="按产品期次搜索...">-->
                        <#--</div>-->
                        <#--<div class="col-xs-2">-->
                                <#--<input type="text" class="form-control" id="productType" autocomplete="on" placeholder="按产品标识搜索...">-->
                        <#--</div>-->
                        <#--<div class="col-xs-1">-->
                            <#--<button class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp</button>-->
                        <#--</div>-->
                    <#--</div>-->
                    <#--<p></p>-->
                    <div class="box">
                        <div class="box-body">
                            <table id="productfileinfoTable" class="table table-bordered table-striped"
                                   style="word-break:break-all;">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th name="issue">产品期次</th>
                                    <th name="regionId">行政区域编码</th>
                                    <th name="cycle">产品周期</th>
                                    <#--<th name="region">区域</th>-->
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




<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">产品详情</h4>
            </div>
            <div class="modal-body">
                <#--<form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">产品类型</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="productType"  >
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">产品期次</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="issue" >
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">产品周期</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="cycle"  >
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">文件类型</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="fileType" >
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">文件路径</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" name="filePath" >
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-6">
                            <button type="button" class="btn btn-default" data-dismiss="modal">返回</button>
                            <input type="hidden" name="id">
                        </div>
                    </div>
                </form>-->

                    <table id="ProductFileInfoTable" class="table table-hover table-striped" style="word-break:break-all;">
                        <caption></caption>
                        <thead>
                        <tr>
                            <th name="fileName">名称</th>
                            <th name="productType" width="12%">产品类型</th>
                            <th name="filePath">文件路径</th>
                            <th name="fileType" width="12%">文件格式</th>
                            <th width="18%">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">返回</button>
            </div>
        </div>
    </div>
</div>





<@netCommon.commonScript />
<#--<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>-->
<script src="${request.contextPath}/static/plugins/bootstrap-treeview/js/bootstrap-treeview.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script>
    function getbutton(row) {
        var html = '<p id="' + row.id + '" >' +
                '<@shiro.hasPermission name="productfileinfosearch"><button class="btn btn-warning btn-xs update" type="button">查看</button></@shiro.hasPermission> ' +
                '<@shiro.hasPermission name="productfileinfodel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="del(this);">删除</button></@shiro.hasPermission>   ' +
                '</p>';

        return html;
    }
</script>
<script src="${request.contextPath}/static/js/productfileinfo/productfileinfo.index.1.js"></script>

</body>
</html>
