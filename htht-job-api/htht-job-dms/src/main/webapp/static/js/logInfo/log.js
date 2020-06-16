
    function searchLog(){
        initData();
    }

    function initData(){
        $("#logsTable").dataTable({
            "deferRender": true,
            "processing" : true,
            "serverSide": true,
            "ajax": {
                url: base_url + "/logInfo/logs?type="+$("#searchText").val() ,
                data : function ( d ) {
                    var obj = {};
                    obj.start = d.start;
                    obj.length = d.length;
                    return obj;
                }
            },
            "searching": false,
            "ordering": false,
            //"scrollX": false,
            "destroy": true,
            "fnDrawCallback": function () {
                this.api().column(0).nodes().each(function (cell, i) {
                    cell.innerHTML = i + 1;
                });
            },
            "columns": [
                {
                    "data": null,
                    "width":'7%'
                },
                { "data": 'username', "visible" : true,"width":'15%'},
                { "data": 'ip', "visible" : true,"width":'12%'},
                { "data": 'content', "visible" : true,"width":'38%'},
                {
                    "data": 'createTime',
                    "visible" : true,
                    "width":'18%',
                    "render": function ( data, type, row ) {
                        return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                    }
                },
                {
                    "data": '',
                    "visible" : true,
                    "render": function ( data, type, row ) {
                        return '<input type="button" class="btn btn-danger btn-xs job_operate" style="margin-left: 10px" onclick=deleteLog("'+row.id+'") value="删除" />';
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
    initData();


    function deleteLog(id) {
        layer.confirm('确认删除?', {
                icon: 3,
                title: '系统提示'
            },
            function(index) {
                layer.close(index);
                $.ajax({
                    type: "post",
                    dataType: "json",
                    url: base_url + "/logInfo/delete",
                    data:{id: id},
                    success: function(data) {
                        if (data === 0) {
                            layer.msg(data.msg || "删除失败", {
                                icon: 2,
                                time: 2000 //2秒关闭（如果不配置，默认是3秒）
                            });
                        } else {
                            layer.msg("删除成功", {
                                icon: 1,
                                time: 2000 //2秒关闭（如果不配置，默认是3秒）
                            }, function (layero, index) {
                                initData();
                            });
                        }
                    }
                });
            });
    }



