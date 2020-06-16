$(document).ready(function () {
    // IP地址验证
    jQuery.validator.addMethod("ip", function(value, element) {
        return this.optional(element) || /^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))$/.test(value);
    }, "请填写正确的IP地址");

    var form = $("#ftpForm").validate({
        rules: {
            name: "required",
            ipAddr: {
                required : true,
                ip : true
            },
            port: "required",
            userName: "required",
            pwd: "required"
        },
        messages: {
            name: "名称是必填项目",
            ipAddr: {
                required: "IP地址是必填项目"
            },
            port: "端口是必填项目",
            userName: "用户名是必填项目",
            pwd: "密码是必填项目"
        }
    });

    //Modal验证销毁重构
    $('#addFtpModal').on('hidden.bs.modal', function() {
        form.resetForm();
    });
});

/*
* 检测删除按钮状态
* */
function checkDeleteBtnState(){
    var deleteBtnState = true;
    $("input[name='deleteFtpId']").each(function () {
        if(this.checked){
            deleteBtnState = false;
        }
    });

    $('#deleteFtps').attr("disabled",deleteBtnState);
}
checkDeleteBtnState();

/*
*
* 选中全部
* */
$('#checkAll').on('click', function () {
    if (this.checked) {
        $(this).attr('checked','checked');
        $("input[name='deleteFtpId']").each(function () {
            this.checked = true;
        });
    } else {
        $(this).removeAttr('checked');
        $("input[name='deleteFtpId']").each(function () {
            this.checked = false;
        });
    }
    checkDeleteBtnState();
});

/*
* 获取删除的选项
* */
function selectDeleteItem(item){
    if ($(this).is(":checked") === false) {
        $("#checkAll").prop("checked", false);
    }

    var selectedAll = true;
    $("input[name='deleteFtpId']").each(function () {
        if(!this.checked){
            selectedAll = false;
        }
    });

    if(selectedAll){
        $('#checkAll').click();
    }

    checkDeleteBtnState();
}

/*内置脚本区域*/
// init date tables
function initData(){
    $("#ftpsTable").dataTable({
        "deferRender": true,
        "processing" : true,
        "serverSide": true,
        "ajax": {
            url: base_url + "/ftp/getFtpsByPage" ,
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
        "columns": [
            {
                "data": 'id',
                "visible" : true,
                "render": function (data, type) {
                    return "<div align='center'><input type='checkbox' onclick = selectDeleteItem('"+data+"') name='deleteFtpId' value=" + data + "></div>" ;
                }
            },
            { "data": 'name', "visible" : true,"width":'14%'},
            { "data": 'ipAddr', "visible" : true,"width":'16%'},
            { "data": 'port', "visible" : true,"width":'10%'},
            { "data": 'userName', "visible" : true,"width":'16%'},
            {
                "data": 'pwd',
                "visible" : true,
                "width":'16%',
                "render": function (data, type) {
                    var pd = "";
                    for (var i = 0; i < 6; i++) {
                        pd+="●";
                    }
                    return pd;
                }
            }/*,
                {
                    "data": 'updateTime',
                    "visible" : true,
                    "width":'15%',
                    "render": function ( data, type, row ) {
                        return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                    }
                }*/,
            {
                "data": '',
                "visible" : true,
                "render": function ( data, type, row ) {
                    return '<input type="button" class="btn btn-primary btn-xs job_operate" onclick=modifyFtp("'+row.id+'") value="修改" />'+
                        '<input type="button" class="btn btn-danger btn-xs job_operate" style="margin-left: 10px" onclick=deleteFtp("'+row.id+'") value="删除" />'+
                        '<input id="ftpTest'+row.id+'" type="button" class="btn btn-success btn-xs job_operate" style="margin-left: 10px" onclick=testConnectFtp("'+row.id+'","'+row.ipAddr+'","'+row.port+'","'+row.userName+'","'+row.pwd+'") value="连接测试" />' +
                        '<span title="连接中..." id="ftp'+row.id+'" class="fa fa-circle-o-notch fa-spin fa-fw" style="display: none;margin-left: 5%"></span>';
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


$("#saveFtpSubmit").click(function() {
    if($('#ftpForm').valid()){
        $.post(base_url + "/ftp/addFtp",  $("#addFtpModal .form").serialize(), function(data, status) {
            if (data.code === "200") {
                $('#addFtpModal').modal('hide');
                layer.msg('新增FTP成功', {
                    icon: 1,
                    time: 1000 //2秒关闭（如果不配置，默认是3秒）
                }, function(){
                    initData();
                });
            } else {
                layer.msg(data.msg || "新增FTP失败", {
                    icon: 2,
                    time: 1000
                });
            }

        });
    }
});

$("#deleteFtps").click(function (data) {
    var selectedDeleteItems = new Array();
    layer.confirm('确认删除?', {
            icon: 3,
            title: '系统提示'
        },
        function(index) {
            layer.close(index);

            //获取删除的IDS
            $("input[name='deleteFtpId']").each(function () {
                if(this.checked ){
                    selectedDeleteItems.push(this.value)
                }
            });

            if(selectedDeleteItems.length > 0){
                $.ajax({
                    type: "post",
                    dataType: "json",
                    url: base_url + "/ftp/deleteFtps",
                    data:{data: JSON.stringify(selectedDeleteItems)},
                    success: function(data) {
                        if (data.code === 200) {
                            layer.msg( "删除成功", {
                                icon: 1,
                                time: 2000 //2秒关闭（如果不配置，默认是3秒）
                            }, function(layero, index){
                                initData();
                            });
                        } else {
                            layer.msg(data.msg || "删除失败", {
                                icon: 2,
                                time: 2000 //2秒关闭（如果不配置，默认是3秒）
                            });
                        }
                    }
                });
            }else {
                layer.msg("未选择删除项", {
                    icon: 2,
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                });
            }
        });
});


/*
* 添加FTP
* */
function addFtp() {
    $("#addFtpModal .modal-title").html("添加Ftp");
    $("#addFtpModal input[name='name']").val("");
    $("#addFtpModal input[name='ipAddr']").val("");
    $("#addFtpModal input[name='port']").val(21);
    $("#addFtpModal input[name='userName']").val("");
    $("#addFtpModal input[name='pwd']").val("");
    $("#addFtpModal input[name='id']").val("");
    $("#addFtpModal input[name='createTime']").val(new Date());
    $("#addFtpModal input[name='version']").val("1");
}

/*
* 修改FTP
* */
function modifyFtp(id) {
    $.ajax({
        type: "post",
        dataType: "json",
        url: base_url + "/ftp/getById",
        data:{
            "id": id
        },
        success: function(data) {
            if (data) {
                $("#addFtpModal .modal-title").html("修改Ftp");
                $("#addFtpModal input[name='name']").val(data.name);
                $("#addFtpModal input[name='ipAddr']").val(data.ipAddr);
                $("#addFtpModal input[name='port']").val(data.port);
                $("#addFtpModal input[name='userName']").val(data.userName);
                $("#addFtpModal input[name='pwd']").val(data.pwd);
                $("#addFtpModal input[name='id']").val(data.id);
                $("#addFtpModal input[name='createTime']").val(new Date(data.createTime));
                $("#addFtpModal input[name='version']").val(data.version);
                $("#addFtpModal").modal('show');
            } else {
                layer.msg("未获取到数据", {
                    icon: 2,
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                });
            }
        }
    });
}

function deleteFtp(id) {
    layer.confirm('确认删除?', {
            icon: 3,
            title: '系统提示'
        },
        function(index) {
            layer.close(index);
            $.ajax({
                type: "post",
                dataType: "json",
                url: base_url + "/ftp/del",
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

function testConnectFtp(id, ip, port, userName, pwd) {
    $("#ftpTest"+id).hide();
    $("#ftp"+id).show();
    $.ajax({
        type: "post",
        dataType: "json",
        url: base_url + "/ftp/testConnect",
        data:{
            "ip": ip,
            "port": port,
            "userName": userName,
            "pwd": pwd
        },
        success: function(data) {
            if (data) {
                layer.msg(data.msg || "连接成功", {
                    icon: 1,
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                });
                $("#ftpTest"+id).show();
                $("#ftp"+id).hide();
            } else {
                layer.msg("连接失败", {
                    icon: 2,
                    time: 2000 //2秒关闭（如果不配置，默认是3秒）
                });
                $("#ftpTest"+id).show();
                $("#ftp"+id).hide();
            }
        }
    });

}