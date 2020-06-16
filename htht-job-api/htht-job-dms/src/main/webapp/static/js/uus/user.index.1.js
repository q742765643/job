var table;
var cityArray = [];
$(function () {
    table = $("#table").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "aaSorting": [
            [1, "asc"]
        ],
        "ajax": {
            url: base_url + "uus_user/pageList",
            type: "post",
            data: function (d) {
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
        "fnDrawCallback": function () {
            this.api().column(0).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },//分页
        "columns": [{
            "title": "序号",
            "data": null
        }, {
            "title": "名称",
            "data": 'userName',
            "visible": true
        },
            {
                "title": "所属角色",
                "data": "roles",
                "render": function (data, type, row) {
                    var r = "";
                    $(data).each(function (index, role) {
                        r = r + "【" + role.name + "】";
                    });
                    return r;
                }
            }, {
                "title": "昵称",
                "data": "nickName"
            },
            {
                "title": '操作',
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
    $('#searchBtn').on('click', function () {
        table.fnDraw();
    });
    $(".add").click(function () {
        $("#addModal .form")[0].reset();
        addModalValidate.resetForm();
        $("#addModal .form .form-group").removeClass("has-error");
        $('#addModal').modal({backdrop: false, keyboard: false}).modal('show');

        $.ajax({
            type: "POST",
            url: base_url + "uus_user/getAllRegionInfo",
            dataType: 'json',
            success:function (data) {
                cityArray = data
                $("#province－add").empty()
                $("#city－add").empty()
                $("#county－add").empty()
                console.log(data)
                for(var i=0;i<data.length;i++){
                    $("#province－add").append("<option value="+data[i].regionId+">"+data[i].areaName+"</option>")
                    if(data[i].subRegion){
                        for(var j=0;j<data[i].subRegion.length;j++){
                            $("#city－add").append("<option value="+data[i].subRegion[j].regionId+">"+data[i].subRegion[j].areaName+"</option>")
                            if(data[i].subRegion[j].subRegion){
                                for(var k=0;k<data[i].subRegion[j].subRegion.length;k++){
                                    $("#county－add").append("<option value="+data[i].subRegion[j].subRegion[k].regionId+">"+data[i].subRegion[j].subRegion[k].areaName+"</option>")
                                }
                            }

                        }
                    }

                }

                $("#province－add").change(function () {
                    $("#city－add").empty()
                    $("#county－add").empty()
                    for(var i=0;i<data.length;i++){
                        if(data[i].areaName ==  $("#province－add option:selected").text()){
                            if(data[i].subRegion){
                                for(var j=0;j<data[i].subRegion.length;j++){
                                    $("#city－add").append("<option value="+data[i].subRegion[j].regionId+">"+data[i].subRegion[j].areaName+"</option>")
                                    if(data[i].subRegion[j].subRegion){
                                        for(var k=0;k<data[i].subRegion[j].subRegion.length;k++){
                                            $("#county-add").append("<option value="+data[i].subRegion[j].subRegion[k].regionId+">"+data[i].subRegion[j].subRegion[k].areaName+"</option>")
                                        }
                                    }

                                }
                            }
                        }
                    }
                });
                $("#city－add").change(function () {
                    $("#county－add").empty()
                    for(var i=0;i<data.length;i++){
                            if(data[i].subRegion){
                                for(var j=0;j<data[i].subRegion.length;j++){
                                    if(data[i].subRegion[j].areaName == $("#city－add option:selected").text()){
                                        if(data[i].subRegion[j].subRegion){
                                            for(var k=0;k<data[i].subRegion[j].subRegion.length;k++){
                                                $("#county-add").append("<option value="+data[i].subRegion[j].subRegion[k].regionId+">"+data[i].subRegion[j].subRegion[k].areaName+"</option>")
                                            }
                                        }
                                    }
                                }
                            }
                    }
                });
            }
        });
    });
    var addModalValidate = $("#addModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            userName: {
                required: true
            },
            nickName: {
                required: true
            },
            password: {
                required: true,
            },
            ackPassword: {
                required: true,
                equalTo: "#password"    //新密码的id选择器
            }
        },
        messages: {
            userName: {
                required: "请输入用户名"
            },
            nickName: {
                required: "请输入用户昵称"
            },
            password: {
                required: "请输入新密码",
            },
            ackPassword: {
                required: "请确认新密码",
                equalTo: "两次密码输入不一致"
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
            $.post(base_url + "/uus_user/saveUser", $("#addModal .form").serialize(), function (data, status) {
                if (data.code == "200") {
                    $('#addModal').modal('hide');
                    layer.msg("新增用户成功", {
                        icon: 1,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function (layero, index) {
                        table.fnDraw();
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

    $("#table").on('click', '.update', function () {
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
        $("#updateModal .form input[name='id']").val(row.id);
        $("#updateModal .form input[name='userName']").val(row.userName);
        $("#updateModal .form input[name='nickName']").val(row.nickName);
        // $("#updateModal .form input[name='password']").val(row.password);
        $('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
        $.ajax({
            type: "POST",
            url: base_url + "uus_user/getAllRegionInfo",
            dataType: 'json',
            success:function (data) {
                cityArray = data
                $("#province-update").empty()
                $("#city-update").empty()
                $("#county-update").empty()
                console.log(data)
                for(var i=0;i<data.length;i++){
                    $("#province-update").append("<option value="+data[i].regionId+">"+data[i].areaName+"</option>")
                    if(data[i].subRegion){
                        for(var j=0;j<data[i].subRegion.length;j++){
                            $("#city-update").append("<option value="+data[i].subRegion[j].regionId+">"+data[i].subRegion[j].areaName+"</option>")
                            if(data[i].subRegion[j].subRegion){
                                for(var k=0;k<data[i].subRegion[j].subRegion.length;k++){
                                    $("#county-update").append("<option value="+data[i].subRegion[j].subRegion[k].regionId+">"+data[i].subRegion[j].subRegion[k].areaName+"</option>")
                                }
                            }

                        }
                    }

                }

                $("#province-update").change(function () {
                    $("#city-update").empty()
                    $("#county-update").empty()
                    for(var i=0;i<data.length;i++){
                        if(data[i].areaName ==  $("#province-update option:selected").text()){
                            if(data[i].subRegion){
                                for(var j=0;j<data[i].subRegion.length;j++){
                                    $("#city-update").append("<option value="+data[i].subRegion[j].regionId+">"+data[i].subRegion[j].areaName+"</option>")
                                    if(data[i].subRegion[j].subRegion){
                                        for(var k=0;k<data[i].subRegion[j].subRegion.length;k++){
                                            $("#county-update").append("<option value="+data[i].subRegion[j].subRegion[k].regionId+">"+data[i].subRegion[j].subRegion[k].areaName+"</option>")
                                        }
                                    }

                                }
                            }
                        }
                    }
                });
                $("#city-update").change(function () {
                    $("#county-update").empty()
                    for(var i=0;i<data.length;i++){
                            if(data[i].subRegion){
                                for(var j=0;j<data[i].subRegion.length;j++){
                                    if(data[i].subRegion[j].areaName == $("#city-update option:selected").text()){
                                        if(data[i].subRegion[j].subRegion){
                                            for(var k=0;k<data[i].subRegion[j].subRegion.length;k++){
                                                $("#county-update").append("<option value="+data[i].subRegion[j].subRegion[k].regionId+">"+data[i].subRegion[j].subRegion[k].areaName+"</option>")
                                            }
                                        }
                                    }
                                }
                            }
                    }
                });
            }
        });
    });
    var updateModalValidate = $("#updateModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            userName: {
                required: true
            },
            nickName: {
                required: true
            },
            ackPassword2: {
                required: true,
                equalTo: "#password2"    //新密码的id选择器
            }
        },
        messages: {
            userName: {
                required: "请输入用户名"
            },
            nickName: {
                required: "请输入用户昵称"
            },
            ackPassword2: {
                required: "请确认新密码",
                equalTo: "两次密码输入不一致"
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
            $.post(base_url + "uus_user/saveUser", $("#updateModal .form").serialize(), function (data, status) {
                if (data.code == "200") {
                    $('#updateModal').modal('hide');
                    layer.msg("更新成功", {
                        icon: 1,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function (layero, index) {
                        window.location.reload();
                    });
                } else {
                    layer.msg(data.msg || "更新失败213213213213", {
                        icon: 2,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    });
                }
            });
        }
    });

    $("#updateModal").on('hide.bs.modal', function () {
        $("#updateModal .form")[0].reset();
        updateModalValidate.resetForm();
        $("#updateModal .form .form-group").removeClass("has-error");
    });

    $("#table").on('click', '.grant',
        function () {
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
            $.ajax({
                type: "GET",
                url: base_url + "uus_user/find/" + id,
                dataType: 'json',
                success: function (msg) {
                    var roleIds = msg.roleIds;
                    var roles = msg.roles;
                    var html = "";
                    for (i = 0; i < roles.length; i++) {
                        if (roleIds.indexOf(roles[i].id) >= 0) {
                            html += "<input type='checkbox' value='" + roles[i].id + "' name='roleIds' checked='checked'> <i></i> " + roles[i].name + "";
                        } else {
                            html += "<input type='checkbox' value='" + roles[i].id + "' name='roleIds' > <i></i> " + roles[i].name + "";
                        }

                    }
                    $("#roleGrant").html(html);

                }
            });
            $("#grantModal .form input[name='id']").val(row.id);
            $('#grantModal').modal({
                backdrop: false,
                keyboard: false
            }).modal('show');
        });

    var grantModalValidate = $("#grantModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
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
            var id = $("#grantModal .form input[name='id']").val();
            $.ajax({
                type: "post",
                dataType: "json",
                url: base_url + "uus_user/grant/" + id,
                data: $("#grantModal .form").serialize(),
                success: function (data) {
                    if (data.code == 200) {
                        $('#grantModal').modal('hide');
                        layer.msg("操作成功", {
                            icon: 1,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function (layero, index) {
                            table.fnDraw();
                        });
                    } else {
                        layer.msg(data.msg || "操作失败", {
                            icon: 2,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }
                }
            });

        }
    });

});

function del(obj) {
    layer.confirm('确认删除?', {icon: 3, title: '系统提示'}, function (index) {
        layer.close(index);
        var id = $(obj).parent('p').attr("id");
        $.ajax({
            type: "post",
            dataType: "json",
            url: base_url + "uus_user/delUser/" + id,
            success: function (data) {
                if (data.code == 200) {
                    layer.msg("删除成功", {
                        icon: 1,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function (layero, index) {
                        table.fnDraw();
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
