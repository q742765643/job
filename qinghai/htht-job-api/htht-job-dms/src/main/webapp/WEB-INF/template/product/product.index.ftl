<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link href="${request.contextPath}/static/plugins/table/bootstrap-table.min.css" rel="stylesheet"/>
    <style>
        #myTab {
            float: left;
            margin-left: 15px;
            margin-bottom: 0px;
        }

        #marginBox {
            background-color: #ffffff;
            position: absolute;
            height: 500px;
            overflow: auto;
            top: 50%;
            left: 50%;
            width: 780px;
            transform: translate(-50%, -50%);
        }

    </style>
</head>
<body style="overflow: hidden"
      class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">


<!-- Content Header (Page header) -->
<section class="content-header">
    <h1>产品分类
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
                <button class="btn btn-info btn-xs add" type="button" id="treeAdd">添加</button>
            <#-- <button class="btn btn-warning btn-xs update" type="button" id="treeUpdate">编辑</button>-->
                <button class="btn btn-danger btn-xs delete" type="button" id="treeDel">删除</button>

            </p>
            <div id="treeview5" class=""></div>

        </div>
        <div id="myTab">
            <ul class="nav nav-tabs">
                <li onclick="changeTab1()" class="active">
                    <a href="#home" data-toggle="tab" style="color: white;background-color: #428BCA" id="a1">
                        产品详情
                    </a>
                </li>
                <li onclick="changeTab2()">
                    <a href="#ios" data-toggle="tab"
                       style="    color: rgb(60, 141, 188);background-color: rgb(247, 247, 247);" id="a2">
                        关联原子算法
                    </a>
                </li>
            </ul>
        </div>
        <div class="col-sm-9" id="tabA">
            <p></p>
            <div class="modal-content" id="moalToggle">
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <div class="form-group">

                            <label for="lastname" class="col-sm-2 control-label">产品名称</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="name" maxlength="20" readonly>
                                <input type="hidden" class="form-control" name="menu" maxlength="50">
                                <input type="hidden" class="form-control" name="treeId">
                                <input type="hidden" class="form-control" name="menuId">
                                <input type="hidden" class="form-control" name="id">
                                <input type="hidden" class="form-control" name="parentId">
                                <input type="text" class="form-control" name="text" maxlength="20"
                                       style="display: none">
                            </div>
                            <label for="modelName" class="col-sm-2 control-label">产品标识</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="mark" maxlength="50" readonly>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="modelName" class="col-sm-2 control-label">产品路径</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="productPath" maxlength="50" readonly>

                            </div>
                            <label for="lastname" class="col-sm-2 control-label">产品周期</label>
                            <div class="col-sm-4"><input type="text" class="form-control" name="cycle" maxlength="20"
                                                         readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="modelName" class="col-sm-2 control-label">地图路径</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="mapUrl" maxlength="1000" readonly>

                            </div>
                            <label for="lastname" class="col-sm-2 control-label">图层名称</label>
                            <div class="col-sm-4"><input type="text" class="form-control" name="featureName"
                                                         maxlength="20" readonly>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">gdb路径</label>
                            <div class="col-sm-4"><input type="text" class="form-control" name="gdbPath" maxlength="20"
                                                         readonly>
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">产品审核</label>
                            <div class="col-sm-4">
                                <select class="form-control" name="isRelease" readonly>
                                    <option value="0">人工审核</option>
                                    <option value="1">自动审核</option>
                                </select>
                            </div>
                        <#--<label for="lastname" class="col-sm-2 control-label">产品审核</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="isRelease" maxlength="50" value="人工审核" disabled>
                        </div>-->
                        </div>
                        <div class="form-group">
                            <label for="iconPath" class="col-sm-2 control-label">产品图标</label>
                            <div class="col-sm-4">
                                <select class="form-control" name="iconPath" id="addIconPath2"  >

                                </select>
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">序号</label>
                            <div class="col-sm-4"><input type="text" class="form-control" name="sortNo"
                                                         maxlength="20" readonly>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="modelName" class="col-sm-2 control-label">备注</label>
                            <div class="col-sm-10">
                                <textarea class="form-control" name="bz" readonly></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="button" class="btn btn-primary" id="reset" style="float: right">更新</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>


        <div class="col-sm-9" style="display: none" id="tabB">
            <div class="form-group" style="height: 30px">
                <div class="col-sm-offset-3 col-sm-9">
                    <button type="button" class="btn btn-primary" id="addRelation" style="float: right">添加关联</button>
                </div>
            </div>
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
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                        <tfoot></tfoot>
                    </table>
                </div>
            </div>
            <div class="modal fade" id="margin" tabindex="-1" role="dialog" aria-hidden="true">
                <div id="marginBox">
                    <div class="col-sm-12">
                        <h3 style="height: 36px">关联算法
                            <div style="float: right;margin-bottom: ;: 2px">
                                <button type="button" class="btn btn-primary" id="addSure">添加</button>
                                <button type="button" class="btn btn-primary" data-dismiss="modal">取消</button>
                                <input type="hidden" name="id">
                            </div>
                        </h3>

                        <div id="treeview3" style="width: 350px;float: left"></div>
                        <div id="treeview4" style="width: 350px;float: left;margin-left: 10px"></div>

                    </div>
                </div>

            <#--<div id="treeview3" style="width: 300px;margin-left: 700px;margin-top: 200px;"></div>-->
            </div>

        </div>
    </div>


</section>
<!-- /.content -->


<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">添加产品分类</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">父級产品</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="menu" maxlength="50" disabled>
                            <input type="hidden" class="form-control" name="parentId">
                            <input type="hidden" class="form-control" name="menuId">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">新增产品名称<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="name" maxlength="20" id="newProductType">
                            <input type="hidden" class="form-control" name="text" id="newProductName">
                            <input type="hidden" class="form-control" name="menu" maxlength="50">
                            <input type="hidden" class="form-control" name="treeId">
                            <input type="hidden" class="form-control" name="menuId">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">产品标识<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="mark" maxlength="50">

                        </div>
                        <label for="modelName" class="col-sm-2 control-label" style="display: none">treeKey<font
                                color="red">*</font></label>
                        <div class="col-sm-4" style="display: none">
                            <input type="text" class="form-control" name="treeKey" maxlength="50" value="product">
                        </div>
                        <label for="modelName" class="col-sm-2 control-label">产品路径<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="productPath" maxlength="50"
                                   lay-verify="required" placeholder="请输入存放路径">

                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">地图路径</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="mapUrl" maxlength="1000">

                        </div>
                        <label for="lastname" class="col-sm-2 control-label">产品周期<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="cycle" maxlength="20"></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">图层名称</label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="featureName" maxlength="20">
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">产品图标</label>
                        <div class="col-sm-4">
                            <select class="form-control" name="iconPath" id="addIconPath">
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">gdb路径</label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="gdbPath" maxlength="20">
                        </div>

                        <label for="lastname" class="col-sm-2 control-label">产品审核</label>
                        <div class="col-sm-4">
                            <select class="form-control" name="isRelease">
                                <option value="0" select="selected">人工审核</option>
                                <option value="1">自动审核</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">备注</label>
                        <div class="col-sm-10">
                            <textarea class="form-control" name="bz"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="submit" class="btn btn-primary" id="saveProductSubmit">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <input type="hidden" name="id">
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="addProductModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">添加产品</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">

                        <label for="lastname" class="col-sm-2 control-label">产品名称<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="name" maxlength="20">
                            <input type="hidden" class="form-control" name="menu" maxlength="50">
                            <input type="hidden" class="form-control" name="treeId">
                            <input type="hidden" class="form-control" name="menuId">
                        </div>

                        <label for="modelName" class="col-sm-2 control-label">产品路径<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="productPath" maxlength="50"
                                   lay-verify="required" placeholder="请输入存放路径">

                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">产品标识<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="mark" maxlength="50">

                        </div>
                        <label for="lastname" class="col-sm-2 control-label">产品周期<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="cycle" maxlength="20"></div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">地图路径</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="mapUrl" maxlength="1000">

                        </div>
                        <label for="lastname" class="col-sm-2 control-label">图层名称</label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="featureName" maxlength="20">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">gdb路径</label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="gdbPath" maxlength="20">
                        </div>

                        <label for="lastname" class="col-sm-2 control-label">产品审核</label>
                        <div class="col-sm-4">
                            <select class="form-control" name="isRelease">
                                <option value="0" select="selected">人工审核</option>
                                <option value="1">自动审核</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">备注</label>
                        <div class="col-sm-10">
                            <textarea class="form-control" name="bz"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="button" class="btn btn-primary" id="saveProductSubmit">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="updateProductModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">修改产品</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form">
                    <div class="form-group">

                        <label for="lastname" class="col-sm-2 control-label">产品名称<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="name" maxlength="20">
                            <input type="hidden" class="form-control" name="menu" maxlength="50">
                            <input type="hidden" class="form-control" name="treeId">
                            <input type="hidden" class="form-control" name="menuId">
                            <input type="hidden" class="form-control" name="id">
                        </div>
                        <label for="modelName" class="col-sm-2 control-label">产品路径<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="productPath" maxlength="50">

                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">产品标识<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="mark" maxlength="50">

                        </div>
                        <label for="lastname" class="col-sm-2 control-label">产品周期<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="cycle" maxlength="20"></div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">地图路径</label>
                        <div class="col-sm-4">
                            <input type="text" class="form-control" name="mapUrl" maxlength="1000">

                        </div>
                        <label for="lastname" class="col-sm-2 control-label">图层名称</label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="featureName" maxlength="20">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">gdb路径</label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="gdbPath" maxlength="20">
                        </div>

                        <label for="lastname" class="col-sm-2 control-label">产品审核</label>
                        <div class="col-sm-4">
                            <select class="form-control" name="isRelease">
                                <option value="0">人工审核</option>
                                <option value="1">自动审核</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="modelName" class="col-sm-2 control-label">备注</label>
                        <div class="col-sm-10">
                            <textarea class="form-control" name="bz"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-6">
                            <button type="button" class="btn btn-primary" id="updateProductSubmit">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
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
        var html = '<p id="' + row.id + '" >' +
                '<@shiro.hasPermission name="productedit"><button class="btn btn-warning btn-xs update" type="button" onclick="reavet(this)">解除关联</button></@shiro.hasPermission> ' +
                '</p>';
        return html;
    }

    function changeTab1() {
        $("#tabA").show()
        $("#tabB").hide()
        $("#a1").css("background-color", "#428BCA");
        $("#a1").css("color", "white");
        $("#a2").css("color", "#3C8DBC");
        $("#a2").css("background-color", "#F7F7F7");
    }

    function changeTab2() {
        parameterTable.fnDraw();
        $("#tabA").hide()
        $("#tabB").show()
        $("#a2").css("background-color", "#428BCA");
        $("#a2").css("color", "white");
        $("#a1").css("color", "#3C8DBC");
        $("#a1").css("background-color", "#F7F7F7");
    }
</script>
<script src="${request.contextPath}/static/js/product/product.index.1.js"></script>

</body>
</html>
