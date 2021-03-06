var table;
$(function () {
    table=$("#table").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "detailView":true,
        "aaSorting": [
            [1, "asc"]
        ],
        "ajax": {
            url: base_url + "/disk_information/pageList",
            type: "post",
            data: function (d) {
                console.log(d)
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
            "title": "磁盘名称",
            "data": 'diskdesc',
            "visible": true
        },
        {
            "title": "磁盘地址",
            "data": "loginurl"
        },
        {
            "title": "创建时间",
            "data": "createTime",
            "render":function(data, type, row){
                 return timestampToTime(data);
            }
        },
        {
            "title": "修改时间",
            "data": "updateTime",
            "render":function(data, type, row){
                return timestampToTime(data);
            }
        },/*{
            "title": "登录名",
            "data": "loginname"
        }
        ,{
            "title": "密码",
            "data": "loginpwd"
        },*/
        {
            "title": "磁盘类型",
            "data": "disktype",
            "render": function (data, type, row) {
            	if(data==0)
            		return "扫描磁盘";
            	if(data==1)
            		return "归档磁盘";
            	if(data==2)
            		return "图片磁盘";
            	if(data==3)
            		return "工作磁盘";
            	if(data==4)
            		return "订单磁盘";
            	if(data==5)
            		return "近线磁盘";
            	if(data==6)
            		return "离线磁盘";
            }
        },
        {
            "title": "总量",
            "data": "disktotlesize",
            "render": function (data, type, row) {
            	data1=data/(1024*1024*1024*1024);
                return data1.toFixed(1)+"T";
            }
        },
        {
            "title": "可使用率",
            "data": "usagerate",
            "render": function (data, type, row) {
            	var barcolor = "";
                if(data > 50) {
                	barcolor = "progress-bar-success";
                } else if(data > 20) {
                	barcolor = "progress-bar-warning";
                } else {
                	barcolor = "progress-bar-danger";
                }
                var progress = "<div class='progress' style='margin-bottom:0px'>"+
									"<div class='progress-bar "+barcolor+" progress-bar-striped active' style='min-width:25px;width: "+data+"%;float:right'>"+
										"<div class='progress-value'>"+data+"%</div>"+
									"</div>"+
								"</div>";
                return progress;
            	  
            }
        },
        {
            "title": '操作',
            "render": function (data, type, row) {
               /* console.log(data)
                console.log(type)
                console.log(row)*/
                tableData['key' + row.id] = row;
                return function () {
                    return  getbutton(row);
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
    $(".add").click(function(){
        $("#addModal .form")[0].reset();
        addModalValidate.resetForm();
        $("#addModal .form .form-group").removeClass("has-error");
        $('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    var addModalValidate = $("#addModal .form").validate({
        errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
        	diskdesc : {
                required : true
            },
            loginurl : {
                required : true
            },
            loginname:{
                required:true,
            },
            loginpwd:{
                required:true,
                //equalTo:"#password"    //新密码的id选择器
            },
            mindiskfreesize:{
            	required:true,
            	digits:true
            }
        },
        messages : {
        	diskdesc : {
                required :"请输入磁盘名称"
            },
            loginurl : {
                required :"请输入磁盘路径"
            },
            loginname:{
                required: "请输入登录名称"
            },
            loginpwd:{
                required: "请输入密码"
            },
            mindiskfreesize:{
            	required:"请输入磁盘最小空闲大小",
            	digits:"请输入整数"
            }
        },
        highlight : function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success : function(label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement : function(error, element) {
            element.parent('div').append(error);
        },
        submitHandler : function(form) {
            $.post(base_url + "/disk_information/saveDisk",  $("#addModal .form").serialize(), function(data, status) {
                if (data.code == "200") {
                    $('#addModal').modal('hide');
                    layer.msg( "新增磁盘成功", {
    					icon: 1,
    					time: 2000 //2秒关闭（如果不配置，默认是3秒）
    				}, function(layero, index){
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

    $("#table").on('click', '.update', function(){
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
        var u= row.unitType;
        var a=0;
        if (u=="MB") {
        	a=row.mindiskfreesize/(1024 * 1024);
		}
		if (u=="GB") {
			a=row.mindiskfreesize/(1024 * 1024* 1024);
		}
		if (u=="TB") {
			a=row.mindiskfreesize/(1024 * 1024* 1024* 1024);
		}
		if (u=="PB") {
			a=row.mindiskfreesize/(1024 * 1024* 1024* 1024* 1024);
		}
		if(a!=0){
			
			$("#updateModal .form input[name='mindiskfreesize']").val(a);
		}
        $("#updateModal .form input[name='id']").val(row.id);
        $("#updateModal .form input[name='diskdesc']").val(row.diskdesc);
        $("#updateModal .form input[name='loginurl']").val(row.loginurl);
        $("#updateModal .form input[name='loginname']").val(row.loginname);
        $("#updateModal .form input[name='loginpwd']").val(row.loginpwd);
        //$("#updateModal .form input[name='disktype']").val(row.disktype);
        $('#updateModal .form select[name=disktype] option[value='+ row.disktype +']').prop('selected', true);
        $('#updateModal .form select[name=unitType] option[value='+ row.unitType +']').prop('selected', true);
        $('#updateModal .form select[name=diskstatus] option[value='+ row.diskstatus +']').prop('selected', true);
        // $("#updateModal .form input[name='updatePassword']").val(row.password);
        // $("#updateModal .form input[name='updateAckPassword']").val(row.password);
        $('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    var updateModalValidate = $("#updateModal .form").validate({
        errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
        	diskdesc : {
                required : true
            },
            loginurl : {
                required : true
            },
            loginname:{
                required:true
            },
            loginpwd:{
                required:true
            },
            mindiskfreesize:{
            	required:true,
            	digits:true
            }
        },
        messages : {
        	diskdesc : {
                required :"请输磁盘名称"
            },
            loginurl : {
                required :"请输入磁盘路径"
            },
            loginname:{
                required: "请输入登录名"
            },
            loginpwd:{
                required: "请输入登录密码"
            },
            mindiskfreesize:{
            	required:"请输入磁盘最小空闲大小",
            	digits:"请输入整数"
            }
        },
        highlight : function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success : function(label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement : function(error, element) {
            element.parent('div').append(error);
        },
        submitHandler : function(form) {
            $.post(base_url + "/disk_information/saveDisk",  $("#updateModal .form").serialize(), function(data, status) {
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

    $("#updateModal").on('hide.bs.modal', function () {
        $("#updateModal .form")[0].reset();
        updateModalValidate.resetForm();
        $("#updateModal .form .form-group").removeClass("has-error");
    });

    $("#table").on('click', '.grant',
        function() {
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
                type : "GET",
                url : base_url + "/dbms_user/find/"+id,
                dataType : 'json',
                success : function(msg) {
                    var roleIds=msg.roleIds;
                    var roles=msg.roles;
                    var html="";
                    for(i = 0; i < roles.length; i++) {
                        if(roleIds.indexOf(roles[i].id)>=0){
                            html+="<input type='checkbox' value='"+roles[i].id+"' name='roleIds' checked='checked'> <i></i> "+roles[i].name+"";
                        }else{
                            html+="<input type='checkbox' value='"+roles[i].id+"' name='roleIds' > <i></i> "+roles[i].name+"";
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
            var id=$("#grantModal .form input[name='id']").val();
            $.ajax({
                type: "post",
                dataType: "json",
                url: base_url + "/dbms_user/grant/" + id,
                data: $("#grantModal .form").serialize(),
                success: function(data) {
                    if (data.code == 200) {
                        $('#grantModal').modal('hide');
                        layer.msg( "操作成功", {
        					icon: 1,
        					time: 2000 //2秒关闭（如果不配置，默认是3秒）
        				}, function(layero, index){
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
    layer.confirm('确认删除?', {icon: 3, title:'系统提示'}, function(index){
        layer.close(index);
        var id = $(obj).parent('p').attr("id");
        $.ajax({
            type: "post",
            dataType: "json",
            url: base_url+"/disk_information/delDisk/"+id,
            success: function(data) {
                if (data.code == 200) {
                	 layer.msg( "删除成功", {
     					icon: 1,
     					time: 2000 //2秒关闭（如果不配置，默认是3秒）
     				}, function(layero, index){
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

function timestampToTime(timestamp) {
    //var date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
	var date = new Date(timestamp);
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    var D = date.getDate() + ' ';
    var h = date.getHours() + ':';
    var m = date.getMinutes() + ':';
    var s = date.getSeconds();
    return Y+M+D+h+m+s;
}
