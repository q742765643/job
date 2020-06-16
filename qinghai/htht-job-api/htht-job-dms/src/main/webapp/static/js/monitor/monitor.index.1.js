$(function () {

    // remove
    $('.remove').on('click', function () {
        var id = $(this).attr('id');

        layer.confirm('确认删除节点?', {icon: 3, title: '系统提示'}, function (index) {
            layer.close(index);

            $.ajax({
                type: 'POST',
                url: base_url + '/monitor/remove',
                data: {"id": id},
                dataType: "json",
                success: function (data) {
                    if (data.code == 200) {
                        layer.msg("删除成功", {
                            icon: 1,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function (layero, index) {
                            window.location.reload();
                        });
                    } else {
                        layer.msg(data.msg || "删除失败", {
                            icon: 2,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }
                },
            });
        });

    });

    // jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”
    jQuery.validator.addMethod("myValid01", function (value, element) {
        var length = value.length;
        var valid = /^[a-z][a-zA-Z0-9-]*$/;
        return this.optional(element) || valid.test(value);
    }, "限制以小写字母开头，由小写字母、数字和下划线组成");

    $('.add').on('click', function () {
        $('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
    });

    jQuery.validator.addMethod("myregistryIp", function(value, element) {
        var ip = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
        return this.optional(element) || (ip.test(value) && (RegExp.$1 < 256 && RegExp.$2 < 256 && RegExp.$3 < 256 && RegExp.$4 < 256));
    }, "Ip地址格式错误");


    var addModalValidate = $("#addModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            registryKey: {
                required: true,
                rangelength: [4, 64]
                //myValid01 : true
            },
            registryIp: {
                required: true,
                rangelength: [7, 25],
                myregistryIp:true
            }
        },
        messages: {
            registryKey: {
                required: "请输入“节点名称”",
                rangelength: "长度限制为4~64"
                //	myValid01: "限制以小写字母开头，由小写字母、数字和中划线组成"
            },
            registryIp: {
                required: "请输入“节点ip”",
                rangelength: "长度限制为7~25"
            }
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            element.parent('div').append(error);
        },
        submitHandler: function (form) {
            $.post(base_url + "/monitor/save", $("#addModal .form").serialize(), function (data, status) {
                if (data.code == "200") {
                    $('#addModal').modal('hide');
                    layer.msg("新增成功", {
                        icon: 1,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function (layero, index) {
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
    $("#addModal").on('hide.bs.modal', function () {
        $("#addModal .form")[0].reset();
        addModalValidate.resetForm();
        $("#addModal .form .form-group").removeClass("has-error");
    });


    // update
    $('.update').on('click', function () {
        $("#updateModal .form input[name='id']").val($(this).attr("id"));
        $("#updateModal .form input[name='registryKey']").val($(this).attr("registryKey"));
        $("#updateModal .form input[name='registryIp']").val($(this).attr("registryIp"));
        $("#updateModal .form input[name='concurrency']").val($(this).attr("zNum"));


        $('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    
    //deploy
    $(".deploy").on('click',function(){
    	$("#deployId").text($(this).attr("registryIp"));
    	
      	$.ajax({
      		type: "post",
      		data:{"treeKey":"processmodel"},
      		dataType: "json",
      		url: base_url+"/product/getTreeNode",
      		success: function(defaultData) {
    			defaultData[0].state = {
    				checked:true,
    				expanded:true,
    				selected:true
    			}
    			$('#treeId_find').val(defaultData[0].id);
    			parameterTable.fnDraw();
      			$('#treeview5').treeview({
      				color: "#428bca",
//      				expandIcon: 'glyphicon glyphicon-chevron-right',
//      				collapseIcon: 'glyphicon glyphicon-chevron-down',
//      				nodeIcon: 'glyphicon glyphicon-bookmark',
      				emptyIcon: '',
      				// 没有子节点的节点图标
      				levels: 1,
      				data: defaultData,
    				onNodeSelected: function (event, data) {
    					 $('#treeId_find').val(data.id);
                         parameterTable.fnDraw();
    					 //$('#treeId_find').val("");
    				 }
      			});
      		}
      	});
    	
    	$('#algoDeploy').modal({backdrop: false, keyboard: false}).modal('show');
    });

    //info
    $('.info').on('click', function () {
        $("#jobInfoList").empty();
        $.post(base_url + "/monitor/info", {"id": $(this).attr("id")}, function (list) {
            if (0 == list.length) {
                $("#jobInfoList").append(
                    "<tr>"
                    + '<td>暂无</td>'
                    + '<td>暂无</td>'
                    + '<td>暂无</td>'
                    + "</tr>"
                );
            } else {
                for (var i = 0; i < list.length; i++) {
                    var jobDesc = list[i].xxlJobInfo.jobDesc;
                    var lineNumber = list[i].lineNumber;
                    var operateNumber = list[i].operateNumber;
                    if (null == lineNumber) {
                        lineNumber = 0;
                    }
                    if (null == operateNumber) {
                        operateNumber = 0;
                    }
                    $("#jobInfoList").append(
                        "<tr>"
                        + '<td>' + jobDesc + '</td>'
                        + '<td>' + lineNumber + '</td>'
                        + '<td>' + operateNumber + '</td>'
                        + "</tr>"
                    );
                }
            }
            $('#infoModal').modal({backdrop: false, keyboard: false}).modal('show');
        });
    });

    var updateModalValidate = $("#updateModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            registryKey: {
                required: true,
                rangelength: [4, 64]
                //myValid01 : true
            },
            registryIp: {
                required: true,
                rangelength: [7, 25]
            }
        },
        messages: {
            registryKey: {
                required: "请输入“节点名称”",
                rangelength: "长度限制为4~64"
                //	myValid01: "限制以小写字母开头，由小写字母、数字和中划线组成"
            },
            registryIp: {
                required: "请输入“节点ip”",
                rangelength: "长度限制为7~25"
            }
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            element.parent('div').append(error);
        },
        submitHandler: function (form) {
            $.post(base_url + "/monitor/update", $("#updateModal .form").serialize(), function (data, status) {
                if (data.code == "200") {
                    $('#addModal').modal('hide');
                    layer.msg("更新成功", {
                        icon: 1,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function (layero, index) {
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
    $("#updateModal").on('hide.bs.modal', function () {
        $("#updateModal .form")[0].reset();
        addModalValidate.resetForm();
        $("#updateModal .form .form-group").removeClass("has-error");
    });


});
