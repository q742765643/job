var table;
$(function() {
    table = $("#dicCodeTable").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "aaSorting": [[1, "asc"]],
        "ajax": {
            url: base_url + "dicCode/pageList",
            type: "post",
            data: function(d) {
                var obj = {};
                var obj = {
                    "searchText": $('#searchText').val(),
                    "start": d.start,
                    "length": d.length
                }
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        // "scrollX": true, // X轴滚动条，取消自适应
        "fnDrawCallback": function() {
            this.api().column(0).nodes().each(function(cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        "columns": [{
            "title": "序号",
            "data": null
        },
            {
                "title": "字典编码",
                "data": 'dictCode',
                "visible": true
            },
            {
                "title": "字典名称",
                "data": 'dictName',
                "visible": true
            },
            /*{
                "title": "资源路径",
                "data": 'sourceUrl',
                "visible": true
            },
            {
                "title": "资源类型",
                "data": "type",
                "render": function(data, type, row) {
                    if(data == 0)
                        return '<span class="label label-info">目录</span>';
                    else if(data == 1)
                        return '<span class="label label-primary">菜单</span>';
                    else if(data == 2)
                        return '<span class="label label-warning">按钮</span>';
                }
            },*/
            {
                "title": '操作',
                "render": function(data, type, row) {
                    tableData['key' + row.id] = row;
                    return function() {
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
    $('#searchBtn').on('click',
        function() {
            table.fnDraw();
        });
    $(".add").click(function() {
        var selectedNodes = $.fn.zTree.getZTreeObj("tree").getSelectedNodes();
        if(selectedNodes.length<=0){
            layer.open({
                title: '系统提示',
                content: ("请选择目录"),
                icon: '2'
            });
            return;
        }
        $("#addModal .form input[name='parentId']").val(selectedNodes[0].id);
        $('#addModal').modal({
            backdrop: false,
            keyboard: false
        }).modal('show');
    });
    var addModalValidate = $("#addModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
        	dictCode: {
                required: true
            },
            dictName: {
                required: true
            },
            sortOrder: {
                required: false,
                digits:false
            },
            memo: {
                required: false
            }
        },
        messages: {
        	dictCode: {
                required: "请输入字典编码"
            },
            dictName: {
                required: "请输入字典名称"
            },
            sortOrder: {
                required: "请输入字典顺序",
                digits:"必须输入整数"
            },
            memo: {
                required: "请输入字典用途"
            }
        },
        highlight: function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function(label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function(error, element) {
            element.parent('div').append(error);
        },
        submitHandler: function(form) {
            $.post(base_url + "/dicCode/saveDicCode", $("#addModal .form").serialize(),
                function(data, status) {
                    if (data.code == "200") {
                        $('#addModal').modal('hide');
                        layer.msg( "新增字典成功", {
        					icon: 1,
        					time: 2000 //2秒关闭（如果不配置，默认是3秒）
        				}, function(layero, index){
        					 window.location.reload();
        				});
                    } else {
                        layer.msg(data.msg || "新增失败", {
        					icon: 2,
        					time: 2000 //2秒关闭（如果不配置，默认是3秒）
        				});
                    }
                });
        }
    });
    $("#addModal").on('hide.bs.modal',
        function() {
            $("#addModal .form")[0].reset();
            addModalValidate.resetForm();
            $("#addModal .form .form-group").removeClass("has-error");
        });

    $("#dicCodeTable").on('click', '.update',
        function() {
            var id = $(this).parent('p').attr("id");
            //alert(id);
            var row = tableData['key' + id];
            if (!row) {
                /*layer.open({
                    title: '系统提示',
                    content: ("模型信息加载失败，请刷新页面"),
                    icon: '2'
                });*/
                return;
            }
            $("#updateModal .form input[name='id']").val(row.id);
            $("#updateModal .form input[name='dictCode']").val(row.dictCode);
            $("#updateModal .form input[name='dictName']").val(row.dictName);
            $("#updateModal .form textarea[name='memo']").val(row.memo);
            $("#updateModal .form input[name='sortOrder']").val(row.sortOrder);
            /*$('#updateModal .form select[name=parentId] option[value=' + row.parentId + ']').prop('selected', true);
            $('#updateModal .form select[name=type] option[value=' + row.type + ']').prop('selected', true);*/
            $('#updateModal').modal({
                backdrop: false,
                keyboard: false
            }).modal('show');
        });
    var updateModalValidate = $("#updateModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
        	dictCode: {
                required: true
            },
            dictName: {
                required: true
            },
            sortOrder: {
                required: false,
                digits:true
            },
            memo: {
                required: false
            }
        },
        messages: {
        	dictCode: {
                required: "请输入字典编码"
            },
            dictName: {
                required: "请输入字典名称"
            },
            sortOrder: {
                required: "请输入字典顺序",
                digits:"必须输入整数"
            },
            memo: {
                required: "请输入字典用途"
            }
        },
        highlight: function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function(label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function(error, element) {
            element.parent('div').append(error);
        },
        submitHandler: function(form) {
            $.post(base_url + "/dicCode/saveDicCode", $("#updateModal .form").serialize(),
                function(data, status) {
                    if (data.code == "200") {
                        $('#updateModal').modal('hide');
                        layer.msg( "更新成功", {
        					icon: 1,
        					time: 2000 //2秒关闭（如果不配置，默认是3秒）
        				}, function(layero, index){
        					 window.location.reload();
        				});
                    } else {
                        layer.msg(data.msg || "更新失败", {
        					icon: 2,
        					time: 2000 //2秒关闭（如果不配置，默认是3秒）
        				});
                    }
                });
        }
    });

    $("#updateModal").on('hide.bs.modal',
        function() {
            updateModalValidate.resetForm();
            $("#updateModal .form")[0].reset();
            $("#updateModal .form .form-group").removeClass("has-error");
        });
    var setting = {
        check : {
            enable : false
        },
        data : {
            simpleData : {
                enable : true
            }
        },
        callback : {
        	onClick: zTreeOnClick
        }
    };
    $.ajax({
        type : "POST",
        url : base_url + "/dicCode/allTree",
        dataType : 'json',
        success : function(msg) {
            $.fn.zTree.init($("#tree"), setting, msg);
        }
    });

});

function zTreeOnClick(event, treeId, treeNode) {
	//alert(treeNode);
	//alert(treeNode.tId + ", " + treeNode.name+ ", " + treeNode.id+ treeNode.children);
	//清空原表
	//table.destroy();
	$("#dicCodeTable").dataTable().fnDestroy();
	//更新表中数据
	//var table;
	var tableData1 = {};
	var treeNodeId = treeNode.id;//?id="+treeNodeId
	//alert(id);
	/**---------------------------table开始------------------------------------------**/
	table = $("#dicCodeTable").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "aaSorting": [[1, "asc"]],
        "ajax": {
            url: base_url + "dicCode/pageList?id="+treeNodeId,
            type: "post",
            data: function(d) {
                var obj = {};
                var obj = {
                    "searchText": $('#searchText').val(),
                    "start": d.start,
                    "length": d.length
                }
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        // "scrollX": true, // X轴滚动条，取消自适应
        "fnDrawCallback": function() {
            this.api().column(0).nodes().each(function(cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        "columns": [{
            "title": "序号",
            "data": null
        },
            {
                "title": "字典编码",
                "data": 'dictCode',
                "visible": true
            },
            {
                "title": "字典名称",
                "data": 'dictName',
                "visible": true
            },
            /*{
                "title": "资源路径",
                "data": 'sourceUrl',
                "visible": true
            },
            {
                "title": "资源类型",
                "data": "type",
                "render": function(data, type, row) {
                    if(data == 0)
                        return '<span class="label label-info">目录</span>';
                    else if(data == 1)
                        return '<span class="label label-primary">菜单</span>';
                    else if(data == 2)
                        return '<span class="label label-warning">按钮</span>';
                }
            },*/
            {
                "title": '操作',
                "render": function(data, type, row) {
                    tableData1['key' + row.id] = row;
                    return function() {
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
	
	/**---------------------------table结束------------------------------------------**/
	
	/**------------------------------二次修改start--------------------------------------**/
	
	$("#dicCodeTable").on('click', '.update',
	        function() {
	            var id = $(this).parent('p').attr("id");
	            //alert(id);
	            var row = tableData1['key' + id];
	            if (!row) {
	                /*layer.open({
	                    title: '系统提示',
	                    content: ("模型信息加载失败，请刷新页面"),
	                    icon: '2'
	                });*/
	                return;
	            }
	            $("#updateModal .form input[name='id']").val(row.id);
	            $("#updateModal .form input[name='dictCode']").val(row.dictCode);
	            $("#updateModal .form input[name='dictName']").val(row.dictName);
	            $("#updateModal .form textarea[name='memo']").val(row.memo);
	            $("#updateModal .form input[name='sortOrder']").val(row.sortOrder);
	            /*$('#updateModal .form select[name=parentId] option[value=' + row.parentId + ']').prop('selected', true);
	            $('#updateModal .form select[name=type] option[value=' + row.type + ']').prop('selected', true);*/
	            $('#updateModal').modal({
	                backdrop: false,
	                keyboard: false
	            }).modal('show');
	        });
	    var updateModalValidate = $("#updateModal .form").validate({
	        errorElement: 'span',
	        errorClass: 'help-block',
	        focusInvalid: true,
	        rules: {
	        	dictCode: {
	                required: true
	            },
	            dictName: {
	                required: true
	            },
	            sortOrder: {
	                required: false,
	                digits:true
	            },
	            memo: {
	                required: false
	            }
	        },
	        messages: {
	        	dictCode: {
	                required: "请输入字典编码"
	            },
	            dictName: {
	                required: "请输入字典名称"
	            },
	            sortOrder: {
	                required: "请输入字典顺序",
	                digits:"必须输入整数"
	            },
	            memo: {
	                required: "请输入字典用途"
	            }
	        },
	        highlight: function(element) {
	            $(element).closest('.form-group').addClass('has-error');
	        },
	        success: function(label) {
	            label.closest('.form-group').removeClass('has-error');
	            label.remove();
	        },
	        errorPlacement: function(error, element) {
	            element.parent('div').append(error);
	        },
	        submitHandler: function(form) {
	            $.post(base_url + "/dicCode/saveDicCode", $("#updateModal .form").serialize(),
	                function(data, status) {
	                    if (data.code == "200") {
	                        $('#updateModal').modal('hide');
	                        layer.msg( "更新成功", {
	        					icon: 1,
	        					time: 2000 //2秒关闭（如果不配置，默认是3秒）
	        				}, function(layero, index){
	        					 window.location.reload();
	        				});
	                    } else {
	                        layer.msg(data.msg || "更新失败", {
	        					icon: 2,
	        					time: 2000 //2秒关闭（如果不配置，默认是3秒）
	        				});
	                    }
	                });
	        }
	    });

	    $("#updateModal").on('hide.bs.modal',
	        function() {
	            updateModalValidate.resetForm();
	            $("#updateModal .form")[0].reset();
	            $("#updateModal .form .form-group").removeClass("has-error");
	        });
	
	/**------------------------------二次修改end----------------------------------------**/
	
}

function del(obj) {
    layer.confirm('确认删除?', {
            icon: 3,
            title: '系统提示'
        },
        function(index) {
            layer.close(index);
            var id = $(obj).parent('p').attr("id");
            $.ajax({
                type: "post",
                dataType: "json",
                url: base_url + "/dicCode/delDicCode/" + id,
                success: function(data) {
                    if (data.code == 200) {
                        layer.msg( "删除成功", {
        					icon: 1,
        					time: 2000 //2秒关闭（如果不配置，默认是3秒）
        				}, function(layero, index){
        					 window.location.reload();
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

