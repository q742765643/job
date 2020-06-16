<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link href="${request.contextPath}/static/plugins/table/bootstrap-table.min.css" rel="stylesheet"/>
    <style>
        img{
            width:auto;
            height:auto;
            max-width:100%;
            max-height:100%;
        }
    </style>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">


        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>流程管理
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
                <div class="col-sm-12">
                    <div class="row">
                        <div class="col-xs-2">
                                <input type="text" class="form-control" id="processCHName" autocomplete="on" placeholder="按流程名称搜索...">
                        </div>
                        <div class="col-xs-1">
                            <button class="btn btn-primary btn-info" id="searchBtn">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp搜索&nbsp&nbsp&nbsp&nbsp&nbsp</button>
                        </div>
                        <div class="col-xs-1">
                            <@shiro.hasPermission name="productadd">
                                <button class="btn btn-primary btn-success add" id="flowAdd" type="button">添加流程</button>
                            </@shiro.hasPermission>
                        </div>
                    </div>
                    <p></p>
                    <div class="box">
                        <div class="box-body">
                            <table id="flowTable" class="table table-bordered table-striped"
                                   style="word-break:break-all;">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th name="processCHName">流程名称</th>
                                    <th name="processDescribe">流程描述</th>
                                    <th name="createTime">创建时间</th>
                                    <th name="processCHName">修改时间</th>
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
        <div class="modal fade" id="viewModal" tabindex="-1" role="dialog"  aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" >查看流程</h4>
                    </div>
                    <div class="modal-body">
                              <img src="" id="previewImage">
                    </div>
                </div>
            </div>
        </div>








<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>

        <!--表格编辑-->
<script src="${request.contextPath}/static/plugins/table/bootstrap-table.min.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-table-edit.js"></script>
<script src="${request.contextPath}/static/plugins/table/bootstrap-select.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script>
    function getbutton(row) {
        var html = '<p id="' + row.id + '" >' +
                '<@shiro.hasPermission name="productedit"><button class="btn btn-warning btn-xs preview" type="button">查看</button></@shiro.hasPermission> ' +
                '<@shiro.hasPermission name="productedit"><button class="btn btn-warning btn-xs update" type="button">修改</button></@shiro.hasPermission> ' +
                '<@shiro.hasPermission name="productdel"><button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="del(this);">删除</button></@shiro.hasPermission>' +
                '</p>';
        return html;
    }
</script>
<script src="${request.contextPath}/static/js/flow/flow.index.1.js"></script>

</body>
</html>
