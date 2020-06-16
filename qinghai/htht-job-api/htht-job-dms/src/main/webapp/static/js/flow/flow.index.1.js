var flowTable;
$(function () {

    $('#searchBtn').on('click', function () {
        flowTable.fnDraw();
    });
    flowTable = $("#flowTable").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "aaSorting": [
            [1, "asc"]
        ],
        "ajax": {
            url: base_url + "/flow/pageList",
            type: "post",
            data: function (d) {
                var obj = {};
                var obj = {
                    "processCHName": $('#processCHName').val(),
                    "start": d.start,
                    "length": d.length
                }
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
            "data": 'processCHName',
            "visible": true
        }, {
            "data": 'processDescribe',
            "visible": true
        }
            , {
                "data": 'createTime',
                "visible": true,
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            }
            ,
            {
                "data": 'updateTime',
                "visible": true,
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            }
            , {
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

    $("#flowAdd").click(function() {
        window.open(base_url+"/designer_zh_CN.html","_self");
    });
    $("#flowTable").on('click', '.preview',function() {
        var id = $(this).parent('p').attr("id");
        var url=base_url+"/flow/preview/"+id;
        $("#previewImage").attr("src",url);
        $('#viewModal').modal('show');

    });

    $("#flowTable").on('click', '.update',function() {
        var id = $(this).parent('p').attr("id");
        window.open(base_url+"/designerUpdate_zh_CN.html?id="+id,"_self");

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
            url: base_url + "/flow/deleteFlow",
            success: function (data) {
                if (data.code == 200) {
                	layer.msg('成功', {
						icon: 1,
						time: 1000 //2秒关闭（如果不配置，默认是3秒）
					}, function(layero, index){
						flowTable.fnDraw();
					}); 
                } else {
                	layer.msg(data.msg || "操作失败", {
						icon: 2,
						time: 1000 //2秒关闭（如果不配置，默认是3秒）
					}); 
                }
            }
        });

    });


}

