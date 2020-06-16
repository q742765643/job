var productfileinfoTable;
var menuId = "";
$(function () {
    getTreeNode();

    $('#searchBtn').on('click', function () {
        productfileinfoTable.fnDraw();
    });
    $(".col-sm-9").bind("keydown",function(e){
	    var theEvent = e || window.event;    
	    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;    
	    if (code == 13) {    
	        //回车执行查询
	        productfileinfoTable.fnDraw();
	        }    
	});

    productfileinfoTable = $("#productfileinfoTable").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "aaSorting": [
            [1, "asc"]
        ],
        //是否启用详细信息视图
        "detailView":true,
        "detailFormatter":"",
        "ajax": {
            url: base_url + "/productfileinfo/pageListProductInfo",
            type: "post",
            data: function (d) {
                console.log(d)
                var obj = {};
                var obj = {
                    "menuId": menuId,
                    "regionId": $('#regionId').val(),
                    "issue": $('#issue').val(),
                    "start": d.start,
                    "length": d.length
                }
                console.log(obj)
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        // "scrollX": true, // X轴滚动条，取消自适应
        /**"fnDrawCallback": function () {
            this.api().column(0).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },**/
        "columns": [
        {
                "data": null
            },
            {
        	"data": 'issue',
            "visible": true
        }, {
        	"data": 'regionId',
            "visible": true
        }, {
            "data": 'cycle',
            "visible": true
        }/*, {
            "data": 'region',
            "visible": true
        }*/, {
            "data": '操作',
            "render": function (data, type, row) {
                tableData['key' + row.id] = row;
                return function () {
                    var html = '<p id="' + row.id + '" >' + '<button class="btn btn-warning btn-xs update" type="button">查看</button>  ' + ' ' + '<button class="btn btn-danger btn-xs model_operate" _type="model_del" type="button" onclick="del(this);">删除</button>  ' + '</p>';
                    return html;
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
        },
        "fnCreatedRow": function(nRow, aData, iDataIndex) {
            $('td:eq(0)', nRow).html((iDataIndex+1));
            // $('td:eq(0)', nRow).html("<span class='row-details ' data_id='"+aData.id+"''></span>&nbsp;" +(iDataIndex+1));
        }
    });
    $('#productfileinfoTable').on('click', ' tbody td .row-details',
        function() {
            var nTr = $(this).parents('tr')[0];
            if (productfileinfoTable.fnIsOpen(nTr)) //判断是否已打开
            {
                /* This row is already open - close it */
                $(this).addClass("row-details-close").removeClass("row-details-open");
                productfileinfoTable.fnClose(nTr);
            } else {
                /* Open this row */
                $(this).addClass("row-details-open").removeClass("row-details-close");
                //  alert($(this).attr("data_id"));
                //oTable.fnOpen( nTr,
                // 调用方法显示详细信息 data_id为自定义属性 存放配置ID
                fnFormatDetails(nTr, $(this).attr("data_id"));
            }
        });
    var tableData = {};


    $("#productfileinfoTable").on('click', '.update', function () {
        var id = $(this).parent('p').attr("id");
        var row = tableData['key' + id];
        if (!row) {
            layer.open({
                title: '系统提示',
                content: ("模型信息加载失败，请刷新页面"),
                icon: '2'
            });
            return;
        }
        // base data
        /*$("#updateModal .form input[name='productType']").val(row.productType);
        $("#updateModal .form input[name='issue']").val(row.issue);
        $("#updateModal .form input[name='cycle']").val(row.cycle);
        $("#updateModal .form input[name='fileType']").val(row.fileType);
        $("#updateModal .form input[name='filePath']").val(row.filePath);*/
        initData(id);
        // show
        $('#updateModal').modal({
            backdrop: false,
            keyboard: false
        }).modal('show');
    });
    $("#updateModal").on('hide.bs.modal', function () {
        //$("#updateModal .form")[0].reset();
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
            url: base_url + "/productfileinfo/deleteProductInfo",
            success: function (data) {
                if (data.code == 200) {
                	layer.msg( "删除成功", {
    					icon: 1,
    					time: 2000 //2秒关闭（如果不配置，默认是3秒）
    				}, function(layero, index){
    					  productfileinfoTable.fnDraw();
    				});
                } else {
                	layer.msg(data.msg || "删除失败", {
                		icon: 2,
                		time: 2000 //2秒关闭（如果不配置，默认是3秒）
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

function initData(id){
    var dictCode = "";
    //获取下载URL
    $.ajax({
        type: "post",
        dataType: "json",
        url: base_url + "/productfileinfo/getFileUrls?dictName=fileUrl",
        success: function(res) {
            dictCode = res.dictCode;

            $("#ProductFileInfoTable").dataTable({
                "deferRender": true,
                "processing" : true,
                "serverSide": true,
                "ajax": {
                    url: base_url + "/productfileinfo/pageLists" ,
                    data : function ( d ) {
                        var obj = {};
                        obj.id = id;
                        obj.start = d.start;
                        obj.length = d.length;
                        return obj;
                    }
                },
                "searching": false,
                "ordering": false,
                //"scrollX": false,
                "destroy": true,
                "columns": [
                    {
                        "data": 'fileName',
                        "visible" : true
                    },
                    { "data": 'productType', "visible" : true,"width":'11%'},
                    { "data": 'filePath', "visible" : true},
                    { "data": 'fileType', "visible" : true,"width":'11%'},
                    {
                        "data": '',
                        "visible" : true,
                        "render": function ( data, type, row ) {
                            var url = res.dictCode+row.relativePath;
                            console.log(row)
                            var params = row.filePath.replace(/\\/g, '#')
                            var ckeditorUrl = base_url + '/static/ckeditor/ckeditor.html?params=' + params;
                            if("pic" === row.productType){
                                return '<a class="btn btn-success btn-xs" style="margin-left: 10px" href="'+url+'" download="'+row.fileName+'">下载</a>'+
                                '<a class="btn btn-info btn-xs" style="margin-left: 10px" href="'+url+'" target="_blank">预览</a>';
                            }else if("file" === row.productType){
                                return '<a class="btn btn-success btn-xs" style="margin-left: 10px" href="'+url+'" download="'+row.fileName+'">下载</a>';
                            }else if("doc" === row.productType){
                                return '<a class="btn btn-success btn-xs" style="margin-left: 10px" href="'+url+'" download="'+row.fileName+'">下载</a>'+
                                '<a class="btn btn-info btn-xs" style="margin-left: 10px" href="'+ckeditorUrl+'" target="_blank">编辑</a>'
                            }
                        }
                    }
                ],
                "language" : {
                    "sProcessing" : "处理中...",
                    "sLengthMenu" : "每页 _MENU_ 条记录",
                    "sZeroRecords" : "没有匹配结果",
                    "sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
                    "sInfoEmpty" : "无记录",
                    "sInfoPostFix" : "",
                    "sSearch" : "搜索:",
                    "sUrl" : "",
                    "sEmptyTable" : "表中数据为空",
                    "sLoadingRecords" : "载入中...",
                    "sInfoThousands" : ",",
                    "oPaginate" : {
                        "sFirst" : "首页",
                        "sPrevious" : "上页",
                        "sNext" : "下页",
                        "sLast" : "末页"
                    },
                    "oAria" : {
                        "sSortAscending" : ": 以升序排列此列",
                        "sSortDescending" : ": 以降序排列此列"
                    }
                }
            });

        },
        error : function (data) {
            console.log("字典中未配置名称为fileUrl，编码为ip的文件下载路径。默认为127.0.0.1");
            $("#ProductFileInfoTable").dataTable({
                "deferRender": true,
                "processing" : true,
                "serverSide": true,
                "ajax": {
                    url: base_url + "/productfileinfo/pageLists" ,
                    data : function ( d ) {
                        var obj = {};
                        obj.id = id;
                        obj.start = d.start;
                        obj.length = d.length;
                        return obj;
                    }
                },
                "searching": false,
                "ordering": false,
                //"scrollX": false,
                "destroy": true,
                "columns": [
                    {
                        "data": 'fileName',
                        "visible" : true
                    },
                    { "data": 'productType', "visible" : true,"width":'11%'},
                    { "data": 'filePath', "visible" : true},
                    { "data": 'fileType', "visible" : true,"width":'11%'},
                    {
                        "data": '',
                        "visible" : true,
                        "render": function ( data, type, row ) {
                            var url = "http://127.0.0.1/"+row.relativePath;
                            if("pic" === row.productType){
                                return '<a class="btn btn-info btn-xs" style="margin-left: 10px" href="'+url+'" target="_blank">预览</a>'+
                                    '<a class="btn btn-success btn-xs" style="margin-left: 10px" href="'+url+'" download="'+row.fileName+'">下载</a>';
                            }else if("file" === row.productType){
                                return '<a class="btn btn-success btn-xs" style="margin-left: 10px" href="'+url+'" download="'+row.fileName+'">下载</a>';
                            }else if("doc" === row.productType){
                                return '<a class="btn btn-success btn-xs" style="margin-left: 10px" href="'+url+'" download="'+row.fileName+'">下载</a>';
                            }
                        }
                    }
                ],
                "language" : {
                    "sProcessing" : "处理中...",
                    "sLengthMenu" : "每页 _MENU_ 条记录",
                    "sZeroRecords" : "没有匹配结果",
                    "sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
                    "sInfoEmpty" : "无记录",
                    "sInfoPostFix" : "",
                    "sSearch" : "搜索:",
                    "sUrl" : "",
                    "sEmptyTable" : "表中数据为空",
                    "sLoadingRecords" : "载入中...",
                    "sInfoThousands" : ",",
                    "oPaginate" : {
                        "sFirst" : "首页",
                        "sPrevious" : "上页",
                        "sNext" : "下页",
                        "sLast" : "末页"
                    },
                    "oAria" : {
                        "sSortAscending" : ": 以升序排列此列",
                        "sSortDescending" : ": 以降序排列此列"
                    }
                }
            });
        }
    });

}

function getTreeNode() {
    $.ajax({
        type: "post",
        data:{"treeKey":"product"},
        dataType: "json",
        url: base_url + "/product/getTreeNode",
        success: function (defaultData) {
            $('#treeview5').treeview({
                color: "#428bca",
                //icon: "glyphicon glyphicon-stop",
                //selectedIcon: "glyphicon glyphicon-stop",
                //nodeIcon: 'glyphicon glyphicon glyphicon-stop',

                levels: 1,
                data: defaultData,
                onNodeSelected: function (event, data) {
                    $('#id').val(data.id);
                    menuId = data.id;
                    productfileinfoTable.fnDraw();
                    $('#id').val("");
                }
            });
        }
    });

}

function fnFormatDetails(nTr, pdataId) {
    $.get(base_url + "/productfileinfo/findFileInfoByWhere/"+pdataId,
        function(json) {
           var sOut="";
           for(var i = 0; i < json.length; i++){
                sOut += '<p >'+json[i].filename+json[i].filetype+'</p>';
           }
            productfileinfoTable.fnOpen(nTr, sOut, 'details');
        });
    //根据配置Id 异步查询数据
}

