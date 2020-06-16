var productTable;
var parameterTable;
var productId;
$(function () {
    getTreeNode();

    $("#treeAdd").click(function () {
        var parentList = new Array();
        var arr = [];
        var arr = $('#treeview5').treeview('getSelected');

        if (arr.length > 0) {
            // console.log(arr)
            getparent(arr[0].nodeId, parentList);
            var menu = "";
            var menuId = "";
            for (var i = parentList.length - 1; i >= 0; i--) {
                menu += parentList[i][0] + ">";
                menuId += parentList[i][1] + ",";
            }
            if (arr[0].id == "treeview5") {
                $("#addModal .form input[name='parentId']").val("0");
                $("#addModal .form input[name='menu']").val("");
            } else {
                $("#addModal .form input[name='parentId']").val(arr[0].id);
                $("#addModal .form input[name='menu']").val(menu + arr[0].text);
            }


            $("#addModal .form input[name='menuId']").val("");
        } else {
            $("#addModal .form input[name='parentId']").val("0");

        }

        //添加产品时初始化产品图标
        $.ajax({
            type: "post",
            dataType: "json",
            url: base_url + "/product/getProductIconName",
            success: function (data) {
                // console.log(data);
                $("#addIconPath").empty();
                var options = "";
                if (data.length != 0) {
                    for (var i = 0; i < data.length; i++) {
                        options += '<option value="' + data[i].dictCode + '" >' + data[i].dictName + '</option>';
                    }
                } else {
                    options += '<option value="none" >无</option>';
                }
                $("#addIconPath").append(options);
            }
        });

        // show
        $('#addModal').modal({
            backdrop: false,
            keyboard: false
        }).modal('show');


    });
    $("#treeDel").click(function () {
        layer.confirm('确认删除?', {icon: 3, title: '系统提示'}, function (index) {
            layer.close(index);
            var arr = $('#treeview5').treeview('getSelected');
            var data = {
                "id": arr[0].id
            };
            $.ajax({
                type: "post",
                data: data,
                dataType: "json",
                url: base_url + "/product/deleteTreeNode",
                success: function (data) {
                    if (data.code == "200") {
                        layer.msg('操作成功', {
                            icon: 1,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function () {
                            window.location.href = base_url + "/product";
                        });
                    } else {
                        layer.msg(data.msg || "操作失败", {
                            icon: 2,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        });

                    }
                }
            });

        });
    });
    $("#addModal").on('hide.bs.modal', function () {
        $("#addModal .form")[0].reset();

    });
    $(".col-sm-9").bind("keydown", function (e) {
        var theEvent = e || window.event;
        var code = theEvent.keyCode || theEvent.which || theEvent.charCode;
        if (code == 13) {
            //回车执行查询
            // productTable.fnDraw();
        }
    });
    parameterTable = $("#parameterTable").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "aaSorting": [
            [1, "asc"]
        ],
        "ajax": {
            url: base_url + "/product/queryAlgoByTreeId",
            type: "post",
            data: function (d) {
                var obj = {};
                obj.treeId = $("#moalToggle input[name=id]").val();
                obj.start = 0;
                obj.length = 10;
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        // "scrollX": true, // X轴滚动条，取消自适应
        "fnDrawCallback": function () {
            this.api().column(0).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        "columns": [{
            "data": null
        }, {
            "data": 'id',
            "visible": false
        }, {
            "data": 'modelName',
            "visible": true
        }, {
            "data": 'modelIdentify',
            "visible": true
        }, {
            "data": '操作',
            "render": function (data, type, row) {
                tableData['key' + row.id] = row;
                return function () {
                    return getbutton(row);
                };
            }
        }

        ],
        "language": {
            "sProcessing": "处理中...",
            "sLengthMenu": "每页 _MENU_ 条记录",
            "sZeroRecords": "没有匹配结果",
            "sInfo": "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
            "sInfoEmpty": "无记录",
            "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
            "sInfoPostFix": "",
            "sSearch": "搜索:",
            "sUrl": "",
            "sEmptyTable": "表中数据为空",
            "sLoadingRecords": "载入中...",
            "sInfoThousands": ",",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "上页",
                "sNext": "下页",
                "sLast": "末页"
            },
            "oAria": {
                "sSortAscending": ": 以升序排列此列",
                "sSortDescending": ": 以降序排列此列"
            }
        }
    });
    var tableData = {};
    var addModalValidate = $("#addModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            text: {
                required: true
            }
        },
        messages: {
            text: {
                required: "请输入目录名称！"
            }
        },
        highlight: function (element) {
            $(element).closest('.col-sm-4').addClass('has-error');
        },
        success: function (label) {
            label.closest('.col-sm-4').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            element.parent('div').append(error);
        },
        submitHandler: function (form) {
            $("#newProductName").val($("#newProductType").val());
            if ($("#newProductName").val() == "" || $("#addModal .form input[name='mark']").val() == "" || $("#addModal .form input[name='productPath']").val() == "" || $("#addModal .form input[name='cycle']").val() == "") {
                layer.open({
                    title: '系统提示',
                    content: ("请补全提交信息"),
                    icon: '2'
                });
                return;
            } else {
                $.post(base_url + "/product/saveNewProduct", $("#addModal .form").serialize(), function (data, status) {
                    if (data.code == "200") {
                        $('#addModal').modal('hide');
                        layer.msg('新增成功', {
                            icon: 1,
                            time: 1000 //2秒关闭（如果不配置，默认是3秒）
                        }, function () {
                            // productTable.fnDraw();
                            getTreeNode();
                        });
                    } else {
                        layer.msg(data.msg || "新增失败", {
                            icon: 2,
                            time: 1000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }


                });
            }

        }
    });
    var updateModalValidate = $("#updateModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            text: {
                required: true
            }
        },
        messages: {
            text: {
                required: "请输入目录名称！"
            }
        },
        highlight: function (element) {
            $(element).closest('.col-sm-4').addClass('has-error');
        },
        success: function (label) {
            label.closest('.col-sm-4').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            element.parent('div').append(error);
        },
        submitHandler: function (form) {
            $.post(base_url + "/product/save", $("#updateModal .form").serialize(), function (data, status) {
                if (data.code == "200") {
                    $('#updateModal').modal('hide');
                    layer.msg('更新成功', {
                        icon: 1,
                        time: 1000 //2秒关闭（如果不配置，默认是3秒）
                    }, function () {
                        // productTable.fnDraw();
                        getTreeNode();
                    });
                } else {
                    layer.msg(data.msg || "更新失败", {
                        icon: 2,
                        time: 1000 //2秒关闭（如果不配置，默认是3秒）
                    });
                }


            });
        }
    });

    $("#productTable").on('click', '.update', function () {
        var id = $(this).parent('p').attr("id");
        var row = tableData['key' + id];
        if (!row) {
            layer.msg(data.msg || "模型信息加载失败，请刷新页面", {
                icon: 2,
                time: 1000 //2秒关闭（如果不配置，默认是3秒）
            });
            return;
        }
        // base data
        $("#updateProductModal .form input[name='id']").val(row.id);
        $("#updateProductModal .form input[name='treeId']").val(row.treeId);
        $("#updateProductModal .form input[name='menu']").val(row.menu);
        $("#updateProductModal .form input[name='menuId']").val(row.menuId);
        $("#updateProductModal .form input[name='name']").val(row.name);
        $("#updateProductModal .form input[name='mark']").val(row.mark);
        $("#updateProductModal .form input[name='cycle']").val(row.cycle);
        $("#updateProductModal .form input[name='mapUrl']").val(row.mapUrl);
        $("#updateProductModal .form input[name='featureName']").val(row.featureName);
        $("#updateProductModal .form input[name='productPath']").val(row.productPath);
        $("#updateProductModal .form input[name='gdbPath']").val(row.gdbpath);
        $('#updateProductModal .form select[name=isRelease] option[value=' + row.isRelease + ']').prop('selected', true);

        $("#updateProductModal .form textarea[name='bz']").text(row.bz);


        // show
        $('#updateProductModal').modal({
            backdrop: false,
            keyboard: false
        }).modal('show');
    });
    $("#updateProductModal").on('hide.bs.modal', function () {
        $("#updateProductModal .form")[0].reset();

    });
    $("#productAdd").click(function () {
        var parentList = new Array();

        var arr = $('#treeview5').treeview('getSelected');

        if (arr.length == 0) {
            layer.open({
                title: '系统提示',
                content: ("请选择目录"),
                icon: '2'
            });
            return;
        }
        getparent(arr[0].nodeId, parentList);
        var menu = "";
        var menuId = "";
        for (var i = parentList.length - 1; i >= 0; i--) {
            menu += parentList[i][0] + ">";
            menuId += parentList[i][1] + ",";
        }
        $("#addProductModal .form input[name='treeId']").val(arr[0].id);
        $("#addProductModal .form input[name='menu']").val(menu + arr[0].text);
        $("#addProductModal .form input[name='menuId']").val(menuId + arr[0].id);


        // show
        $('#addProductModal').modal({
            backdrop: false,
            keyboard: false
        }).modal('show');


    });
    $("#addProductModal").on('hide.bs.modal', function () {
        $("#addProductModal .form")[0].reset();
    });
    $("#updateProductSubmit").click(function () {
        $.post(base_url + "/product/saveProduct", $("#updateProductModal .form").serialize(), function (data, status) {
            if (data.code == "200") {
                $('#updateProductModal').modal('hide');
                layer.msg('更新成功', {
                    icon: 1,
                    time: 1000 //2秒关闭（如果不配置，默认是3秒）
                }, function () {
                     // productTable.fnDraw();
                });
            } else {
                layer.msg(data.msg || "更新失败", {
                    icon: 2,
                    time: 1000
                });
            }

        });

    });

    $("#reset").click(function () {
        if ($(this).text() == "更新") {
            $("#moalToggle input[name = 'productPath']").attr("readonly", false);
            $("#moalToggle input[name = 'cycle']").attr("readonly", false);
            $("#moalToggle input[name = 'mapUrl']").attr("readonly", false);
            $("#moalToggle input[name = 'featureName']").attr("readonly", false);
            $("#moalToggle input[name = 'gdbPath']").attr("readonly", false);
            $("#moalToggle select[name = 'isRelease']").attr("readonly", false);
            //$("#moalToggle select[name = 'iconPath']").removeAttr("disabled");
            $("#moalToggle textarea[name = 'bz']").attr("readonly", false);
            $("#moalToggle input[name = 'sortNo']").attr("readonly", false);
            $(this).text("保存");
        } else {
            $("#moalToggle input[name=text]").val($("#moalToggle input[name = 'name']").val());
           // $("#moalToggle input[name=iconPath]").val($('#treeview5').treeview('getSelected')[0].iconPath);
            


            $.post(base_url + "/product/saveProduct", $("#moalToggle .form").serialize(), function (data, status) {
                if (data.code == "200") {
                    $('#moalToggle').modal('hide');
                    layer.msg('更新成功', {
                        icon: 1,
                        time: 1000 //2秒关闭（如果不配置，默认是3秒）
                    }, function () {
                        getTreeNode();
                    });
                } else {
                    layer.msg(data.msg || "更新失败", {
                        icon: 2,
                        time: 1000
                    });
                }

            });

            $("#moalToggle input[name = 'productPath']").attr("readonly", true);
            $("#moalToggle input[name = 'cycle']").attr("readonly", true);
            $("#moalToggle input[name = 'mapUrl']").attr("readonly", true);
            $("#moalToggle input[name = 'featureName']").attr("readonly", true);
            $("#moalToggle input[name = 'gdbPath']").attr("readonly", true);
            $("#moalToggle select[name = 'isRelease']").attr("readonly", true);
            //$("#moalToggle select[name = 'iconPath']").attr("disabled", disabled);
            $("#moalToggle textarea[name = 'bz']").attr("readonly", true);
            $("#moalToggle input[name = 'sortNo']").attr("readonly", true);
            $(this).text("更新");
        }

    });

    $("#addRelation").click(function () {
        $('#margin').modal({
            backdrop: false,
            keyboard: false
        }).modal('show');
        $.ajax({
            type: "post",
            data: {"treeKey": "processmodel"},
            dataType: "json",
            url: base_url + "/product/getTreeNode",
            success: function (defaultData) {
                console.log(defaultData)
                defaultData[0].state = {
                    checked: true,
                    expanded: true,
                    selected: true
                }
                $('#treeview3').treeview({
                    color: "#428bca",
                    emptyIcon: '',
                    levels: 1,
                    data: defaultData,
                    onNodeSelected: function (event, data) {
                        var processmodelArr = [];
                        $.ajax({
                            type: "post",
                            data: {
                                start: 0,
                                length: 1000,
                                modelName: "",
                                modelIdentify: "",
                                treeId: data.id
                            },
                            dataType: "json",
                            url: base_url + "/processmodel/pageList",
                            success: function (data) {
                                for (var i = 0; i < data.data.length; i++) {
                                    processmodelArr.push({
                                        id: data.data[i].id,
                                        menuId: data.data[i].id,
                                        parentId: "0",
                                        text: data.data[i].modelName,
                                    });
                                }

                                $('#treeview4').treeview({
                                    color: "#428bca",
                                    emptyIcon: '',
                                    levels: 1,
                                    data: processmodelArr,
                                    onNodeSelected: function (event, data) {
                                    }
                                });
                            }
                        })
                    }
                });
                var processmodelArr = [];
                $.ajax({
                    type: "post",
                    data: {
                        start: 0,
                        length: 1000,
                        modelName: "",
                        modelIdentify: "",
                        treeId: defaultData[0].id
                    },
                    dataType: "json",
                    url: base_url + "/processmodel/pageList",
                    success: function (data) {
                        for (var i = 0; i < data.data.length; i++) {
                            processmodelArr.push({
                                id: data.data[i].id,
                                menuId: data.data[i].id,
                                parentId: "0",
                                text: data.data[i].modelName,
                            });
                        }

                        $('#treeview4').treeview({
                            color: "#428bca",
                            emptyIcon: '',
                            levels: 1,
                            data: processmodelArr,
                            onNodeSelected: function (event, data) {
                            }
                        });
                    }
                })
            }
        });
    });

    $("#addSure").click(function () {
        console.log($("#moalToggle input[name=id]").val())
        console.log($('#treeview4').treeview('getSelected'))
        console.log($('#treeview4').treeview('getSelected').length)
        if ($("#moalToggle input[name=id]").val() == "" || $('#treeview4').treeview('getSelected').length <= 0) {
            layer.open({
                title: '系统提示',
                content: ("请选择关联双方"),
                icon: '2'
            });
            return;
        } else {
            $.ajax({
                type: "post",
                data: {
                    treeId: $("#moalToggle input[name=id]").val(),
                    algoId: $('#treeview4').treeview('getSelected')[0].id,
                },
                dataType: "json",
                url: base_url + "/product/buildAlgoRelation",
                success: function (data) {
                    if (data.code == 200) {
                        layer.msg("添加成功", {
                            icon: 1,
                            time: 1000 //2秒关闭（如果不配置，默认是3秒）
                        });
                        $('#margin').modal('hide');
                        parameterTable.fnDraw();
                    }
                }
            })
        }

    });
});

function del(obj) {
    layer.confirm('确认删除?', {icon: 3, title: '系统提示'}, function (index) {
        layer.close(index);
        var id = $(obj).parent('p').attr("id");
        var data = {
            "id": id
        };
        $.ajax({
            type: "post",
            data: data,
            dataType: "json",
            url: base_url + "/product/deleteProduct",
            success: function (data) {
                if (data.code == 200) {
                    layer.msg('注销成功', {
                        icon: 1,
                        time: 1000
                    }, function () {
                        // productTable.fnDraw();
                    });
                } else {
                    layer.msg(data.msg || "删除失败", {
                        icon: 2,
                        time: 1000
                    });
                }
            }
        });

    });

}

function getparent(nodeId, parentList) {
    var arr = $('#treeview5').treeview("getParent", nodeId);
    var a = [arr.text, arr.id];
    if (arr.nodeId != undefined) {
        parentList.push(a);
        return getparent(arr.nodeId, parentList);

    } else {
        return parentList;
    }

}

function getTreeNode() {
    $.ajax({
        type: "post",
        data: {"treeKey": "product"},
        dataType: "json",
        url: base_url + "/product/getTreeNode",
        success: function (defaultData) {
            // console.log(defaultData)
            //修改部分
            // var newData = [];
            // var nodes = [];
            // var nodes =
            // for (var i = 0; i < defaultData.length; i++) {
            //     nodes = [];
            //     if (defaultData[i].hasOwnProperty("nodes")) {
            //         for (var j = 0; j < defaultData[i].nodes.length; j++) {
            //             nodes.push({
            //                 "createTime": defaultData[i].nodes[j].createTime,
            //                 "iconPath": defaultData[i].nodes[j].iconPath,
            //                 "id": defaultData[i].nodes[j].id,
            //                 "menu": defaultData[i].nodes[j].menu,
            //                 "menuId": defaultData[i].nodes[j].menuId,
            //                 "parentId": defaultData[i].nodes[j].parentId,
            //                 "text": defaultData[i].nodes[j].text,
            //                 "treeKey": defaultData[i].nodes[j].treeKey,
            //                 "version": defaultData[i].nodes[j].version
            //             })
            //         }
            //     }
            //     if (nodes.length > 0) {
            //         newData.push({
            //             "createTime": defaultData[i].createTime,
            //             "iconPath": defaultData[i].iconPath,
            //             "id": defaultData[i].id
            //             ,
            //             "menu": defaultData[i].menu,
            //             "menuId": defaultData[i].menuId,
            //             "parentId": defaultData[i].parentId,
            //             "text": defaultData[i].text,
            //             "treeKey": defaultData[i].treeKey,
            //             "version": defaultData[i].version,
            //             "nodes": nodes
            //         })
            //     } else {
            //         newData.push({
            //             "createTime": defaultData[i].createTime,
            //             "iconPath": defaultData[i].iconPath,
            //             "id": defaultData[i].id
            //             ,
            //             "menu": defaultData[i].menu,
            //             "menuId": defaultData[i].menuId,
            //             "parentId": defaultData[i].parentId,
            //             "text": defaultData[i].text,
            //             "treeKey": defaultData[i].treeKey,
            //             "version": defaultData[i].version
            //         })
            //     }
            // }
            defaultData[0].state = {
                checked: true,
                expanded: true,
                selected: true
            }
            // console.log(newData)
            $('#id').val(defaultData[0].id);
            // $('#id').val("");
            $('#treeview5').treeview({
                color: "#428bca",
                levels: 1,
                data: defaultData,
                onNodeSelected: function (event, data) {
                    // console.log(data)
                    // $('#id').val(data.id);
                    // productTable.fnDraw();
                    // $('#id').val("");
                    $.ajax({
                        type: "post",
                        data: {
                            treeId: $('#treeview5').treeview('getSelected')[0].id,
                            menuId: $('#treeview5').treeview('getSelected')[0].menuId,
                            name: "",
                            mark: "",
                            start: 0,
                            length: 10
                        },
                        dataType: "json",
                        url: base_url + "/product/pageList",
                        success: function (data) {
                            var iconNameF = "";
                            if (data.data.length > 0) {
                                for (var i = 0; i < data.data.length; i++) {
                                    if (data.data[i].name == $('#treeview5').treeview('getSelected')[0].text) {
                                        if (data.data[i].isRelease == 0) {
                                            $($("#moalToggle select[name=isRelease]")[1]).selected = true;
                                        } else {
                                            $($("#moalToggle select[name=isRelease]")[0]).selected = true;
                                        }
                                        if(data.data[i].sortNo == null){
                                            $("#moalToggle input[name=sortNo]").val("");
                                        }else{
                                            $("#moalToggle input[name=sortNo]").val(data.data[i].sortNo);
                                        }
                                        iconNameF = data.data[i].iconPath;
                                        $("#moalToggle input[name=name]").val(data.data[i].name);
                                        $("#moalToggle input[name=menu]").val(data.data[i].menu);
                                        $("#moalToggle input[name=treeId]").val(data.data[i].treeId);
                                        $("#moalToggle input[name=menuId]").val(data.data[i].menuId);
                                        $("#moalToggle input[name=id]").val(data.data[i].id);
                                        $("#moalToggle input[name=productPath]").val(data.data[i].productPath);
                                        $("#moalToggle input[name=cycle]").val(data.data[i].cycle);
                                        $("#moalToggle input[name=mapUrl]").val(data.data[i].mapUrl);
                                        $("#moalToggle input[name=featureName]").val(data.data[i].featureName);
                                        $("#moalToggle input[name=gdbPath]").val(data.data[i].gdbPath);
                                        $("#moalToggle input[name=mark]").val(data.data[i].mark);
                                        $("#moalToggle textarea[name=bz]").val(data.data[i].bz);
                                        $("#moalToggle input[name=sortNo]").val("");
                                    }

                                }
                            } else {
                                $("#moalToggle input[name=name]").val("");
                                $("#moalToggle input[name=menu]").val("");
                                $("#moalToggle input[name=treeId]").val("");
                                $("#moalToggle input[name=menuId]").val("");
                                $("#moalToggle input[name=id]").val("");
                                $("#moalToggle input[name=productPath]").val("");
                                $("#moalToggle input[name=cycle]").val("");
                                $("#moalToggle input[name=mapUrl]").val("");
                                $("#moalToggle input[name=featureName]").val("");
                                $("#moalToggle input[name=gdbPath]").val("");
                                $("#moalToggle input[name=isRelease]").val("");
                                $("#moalToggle input[name=mark]").val("");
                                $("#moalToggle textarea[name=bz]").val("");
                            }
                            $.ajax({
                                type: "post",
                                url: base_url + "/product/getProductIconName",
                                success:function (data) {
                                    $("#moalToggle select[name=iconPath]").empty();
                                    for(var i=0;i<data.length;i++){
                                        if(data[i].dictCode == iconNameF){
                                            $("#moalToggle select[name=iconPath]").append("<option value="+data[i].dictCode+"  selected>"+data[i].dictName+"</option>");
                                        }else{
                                            $("#moalToggle select[name=iconPath]").append("<option value="+data[i].dictCode+" >"+data[i].dictName+"</option>");
                                        }

                                    }
                                }
                            })

                            //原子算法
                            if ($("#moalToggle input[name=id]").val() == "") {
                                layer.open({
                                    title: '系统提示',
                                    content: ("此节点无产品"),
                                    icon: '2'
                                });
                            } else {
                                parameterTable.fnDraw()
                            }
                        }
                    })
                }
            });
            //产品
            $.ajax({
                type: "post",
                data: {
                    treeId: $('#treeview5').treeview('getSelected')[0].id,
                    menuId: $('#treeview5').treeview('getSelected')[0].menuId,
                    name: "",
                    mark: "",
                    start: 0,
                    length: 10
                },
                dataType: "json",
                url: base_url + "/product/pageList",
                success: function (data) {
                    // console.log(data)
                    var iconName = "";
                    if (data.data.length > 0) {
                        for (var i = 0; i < data.data.length; i++) {
                            if (data.data[i].name == $('#treeview5').treeview('getSelected')[0].text) {
                                if (data.data[i].isRelease == 0) {
                                    $($("#moalToggle select[name=isRelease]")[1]).selected = true;
                                } else {
                                    $($("#moalToggle select[name=isRelease]")[0]).selected = true;
                                }
                                iconName = data.data[i].iconPath

                                if(data.data[i].sortNo == null){
                                    $("#moalToggle input[name=sortNo]").val("");
                                }else{
                                    $("#moalToggle input[name=sortNo]").val(data.data[i].sortNo);
                                }
                                $("#moalToggle input[name=name]").val(data.data[i].name);
                                $("#moalToggle input[name=menu]").val(data.data[i].menu);
                                $("#moalToggle input[name=treeId]").val(data.data[i].treeId);
                                $("#moalToggle input[name=menuId]").val(data.data[i].menuId);
                                $("#moalToggle input[name=id]").val(data.data[i].id);
                                $("#moalToggle input[name=productPath]").val(data.data[i].productPath);
                                $("#moalToggle input[name=cycle]").val(data.data[i].cycle);
                                $("#moalToggle input[name=mapUrl]").val(data.data[i].mapUrl);
                                $("#moalToggle input[name=featureName]").val(data.data[i].featureName);
                                $("#moalToggle input[name=gdbPath]").val(data.data[i].gdbpath);
                                $("#moalToggle input[name=mark]").val(data.data[i].mark);
                                $("#moalToggle textarea[name=bz]").val(data.data[i].bz);
                            }
                        }
                    } else {
                        $("#moalToggle input[name=name]").val("");
                        $("#moalToggle input[name=menu]").val("");
                        $("#moalToggle input[name=treeId]").val("");
                        $("#moalToggle input[name=menuId]").val("");
                        $("#moalToggle input[name=id]").val("");
                        $("#moalToggle input[name=productPath]").val("");
                        $("#moalToggle input[name=cycle]").val("");
                        $("#moalToggle input[name=mapUrl]").val("");
                        $("#moalToggle input[name=featureName]").val("");
                        $("#moalToggle input[name=gdbPath]").val("");
                        $("#moalToggle input[name=isRelease]").val("");
                        $("#moalToggle input[name=mark]").val("");
                        $("#moalToggle textarea[name=bz]").val("");
                        $("#moalToggle input[name=sortNo]").val("");
                    }


                    $.ajax({
                        type: "post",
                        url: base_url + "/product/getProductIconName",
                        success:function (data) {
                            $("#moalToggle select[name=iconPath]").empty();
                            for(var i=0;i<data.length;i++){
                                if(data[i].dictCode == iconName){
                                    $("#moalToggle select[name=iconPath]").append("<option value="+data[i].dictCode+"  selected>"+data[i].dictName+"</option>");
                                }else{
                                    $("#moalToggle select[name=iconPath]").append("<option value="+data[i].dictCode+" >"+data[i].dictName+"</option>");
                                }

                            }
                        }
                    })

                }
            })
        }
    });
}


function reavet(obj) {
    layer.confirm('确认解除关联?', {icon: 3, title: '系统提示'}, function (index) {
        layer.close(index);
        $.ajax({
            type: "post",
            data: {
                treeId: $("#moalToggle input[name=id]").val(),
                algoId: $(obj).parent('p').attr("id"),
            },
            dataType: "json",
            url: base_url + "/product/deleteRelation",
            success: function (data) {
                if (data.code == 200) {
                    layer.msg("解绑成功", {
                        icon: 1,
                        time: 1000
                    });
                    parameterTable.fnDraw();
                }
            }
        })

    });
}