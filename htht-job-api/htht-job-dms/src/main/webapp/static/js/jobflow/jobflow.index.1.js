$(function() {
    var tasktype = 5;
    if($("#tasktypevalue").val()){
        tasktype=$("#tasktypevalue").val();
    }
    console.log(tasktype)
    var jobTable = $("#job_list").dataTable({
        "deferRender": true,
        "processing" : true,
        "serverSide": true,
        "ajax": {
            url: base_url + "/jobinfo/pageList/"+tasktype,
            type:"post",
            data : function ( d ) {
                var obj = {};
                obj.jobGroup = $('#jobGroup').val();
                obj.executorHandler = $('#executorHandler').val();
                obj.start = d.start;
                obj.length = d.length;
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        //"scrollX": true,	// X轴滚动条，取消自适应
        "columns": [
            { "data": 'id', "bSortable": false, "visible" : false},
            {
                "data": 'jobGroup',
                "visible" : false

            },
//					{
//						"data": 'childJobKey',
//						"width":'10%',
//						"visible" : true,
//						"render": function ( data, type, row ) {
//							var jobKey = row.jobGroup + "_" + row.id;
//							return jobKey;
//						}
//					},
            { "data": 'jobDesc', "visible" : true,"width":'20%'},
            {
                "data": 'triggers.nextfireTime',
                "visible" : true,
                "width":'15%',
                "render": function ( data, type, row ) {
                    return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                }
            },
            {
                "data": 'triggers.prevfireTime',
                "visible" : false,
                "render": function ( data, type, row ) {
                    return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                }
            },
            {
                "data": 'glueType',
                "visible" : false,
                "render": function ( data, type, row ) {
                    if ('GLUE_GROOVY'==row.glueType) {
                        return "GLUE模式(Java)";
                    } else if ('GLUE_SHELL'==row.glueType) {
                        return "GLUE模式(Shell)";
                    } else if ('GLUE_PYTHON'==row.glueType) {
                        return "GLUE模式(Python)";
                    }else if  ('GLUE_NODEJS'==row.glueType){
                        return "GLUE模式(Nodejs)";
                    } else if ('BEAN'==row.glueType) {
                        return "BEAN模式：" + row.executorHandler;
                    }
                    return row.executorHandler;
                }
            },
            { "data": 'executorParam', "visible" : false},
            { "data": 'jobCron', "visible" : false,"width":'10%'},
            { "data": 'jobCronName', "visible" : true,"width":'10%'},
            {
                "data": 'addTime',
                "visible" : false,
                "render": function ( data, type, row ) {
                    return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                }
            },
            {
                "data": 'updateTime',
                "visible" : false,
                "render": function ( data, type, row ) {
                    return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                }
            },
            { "data": 'author', "visible" : false, "width":'10%'},
            { "data": 'alarmEmail', "visible" : false},
            { "data": 'glueType', "visible" : false},
            {
                "data": 'jobStatus',
                "width":'10%',
                "visible" : true,
                "render": function ( data, type, row ) {
                    if ('NORMAL' == data) {
                        return '<small class="label label-success" ><i class="fa fa-clock-o"></i>'+ data +'</small>';
                    } else if ('PAUSED' == data){
                        return '<small class="label label-default" title="暂停" ><i class="fa fa-clock-o"></i>'+ data +'</small>';
                    } else if ('BLOCKED' == data){
                        return '<small class="label label-default" title="阻塞[串行]" ><i class="fa fa-clock-o"></i>'+ data +'</small>';
                    }
                    return data;
                }
            },
            {
                "data": '操作' ,
                "width":'20%',
                "render": function ( data, type, row ) {
                    return function(){
                        // status
                        var pause_resume = "";
                        if ('NORMAL' == row.jobStatus) {
                            pause_resume = '<button class="btn btn-primary btn-xs job_operate" _type="job_pause" type="button">暂停</button>  ';
                        } else if ('PAUSED' == row.jobStatus){
                            pause_resume = '<button class="btn btn-primary btn-xs job_operate" _type="job_resume" type="button">运行</button>  ';
                        }
                        // log url
                        var logUrl = base_url +'/joblog?jobId='+ row.id;

                        // log url
                        var codeBtn = "";
                        if ('BEAN' != row.glueType) {
                            var codeUrl = base_url +'/jobcode?jobId='+ row.id;
                            codeBtn = '<button class="btn btn-warning btn-xs" type="button" onclick="javascript:window.open(\'' + codeUrl + '\')" >GLUE</button>  '
                        }

                        // html
                        tableData['key'+row.id] = row;


                        return getbutton(row,logUrl,codeBtn,pause_resume);
                    };
                }
            }
        ],
        "language" : {
            "sProcessing" : "处理中...",
            "sLengthMenu" : "每页 _MENU_ 条记录",
            "sZeroRecords" : "没有匹配结果",
            "sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
            "sInfoEmpty" : "无记录",
            "sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
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
    var tableData = {};

    $(".add").click(function(){
        window.location.href = base_url + "/jobflow/add?tasktype=" + $('.add').attr('_tasktype');
    });
    $("#job_list").on('click', '.update',function() {
        var id = $(this).parent('p').attr("id");
        if ($(this).parent('p').find("button")[1].innerHTML === '暂停') {
            layer.open({
                title: '系统提示',
                content: ("运行状态下，无法进行编辑或删除操作"),
                icon: '2'
            });
            return;
        }
        window.location.href = base_url + "/jobflow/update?id="+id;
    });
    $("#job_list").on('click', '.job_mapping',function() {
        var  modelId=$(this).parent('p').attr("modelId");
        var  jobId=$(this).parent('p').attr("id");
        $("#mappingModal .mappingform input[name='jobId']").val(jobId);
        $.ajax({
            type : 'POST',
            url : base_url+"/jobflow/mapping",
            data : {
                "id" : modelId
            },
            dataType : "json",
            success : function(data){
                tableMapping("tableMapping",data);
            },
        });

        $('#mappingModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    var mappingModalValidate = $("#mappingModal .mappingform").validate({
        errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        highlight : function(element) {
            $(element).closest('.col-sm-4').addClass('has-error');
        },
        success : function(label) {
            label.closest('.col-sm-4').removeClass('has-error');
            label.remove();
        },
        errorPlacement : function(error, element) {
            element.parent('div').append(error);
        },
        submitHandler : function(form) {
            var mappingJson = $('#tableMapping').bootstrapTable('getData');
            $("#mappingModal .mappingform input[name='mappingJson']").val( JSON.stringify(mappingJson) );
            $.post(base_url + "/jobflow/saveMatchRelation",  $("#mappingModal .mappingform").serialize(), function(data, status) {
                if (data.code == 0) {
                    $('#mappingModal').modal('hide');
                    layer.msg("调整成功", {
                        icon: 1,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function(layero, index){
                        jobTable.fnDraw();
                    });
                } else {
                    layer.msg(data.msg ||"调整失败", {
                        icon: 2,
                        time: 1000 //2秒关闭（如果不配置，默认是3秒）
                    });
                }
            });
        }
    });

    $("#job_list").on('click', '.job_operate',function() {
        if ($(this).html()==='删除'&&$(this).parents('td').prev().find('small').attr('title') === '正常') {
            layer.open({
                title: '系统提示',
                content: ("运行状态下，无法进行编辑或删除操作"),
                icon: '2'
            });
            return;
        }

        var typeName;
        var url;
        var needFresh = false;

        var type = $(this).attr("_type");
        if ("job_pause" == type) {
            typeName = "暂停";
            url = base_url + "/jobinfo/pause";
            needFresh = true;
        } else if ("job_resume" == type) {
            typeName = "恢复";
            url = base_url + "/jobinfo/resume";
            needFresh = true;
        } else if ("job_del" == type) {
            typeName = "删除";
            url = base_url + "/jobinfo/remove";
            needFresh = true;
        } else if ("job_trigger" == type) {
            typeName = "执行";
            url = base_url + "/jobinfo/trigger";
        } else {
            return;
        }

        var id = $(this).parent('p').attr("id");
        if (typeName == "删除" && $(this).parent('p').find("button")[1].innerHTML == "暂停") {
            layer.open({
                title: '系统提示',
                content: ("请暂停任务"),
                icon: '2'
            });
        } else {
            layer.confirm('确认' + typeName + '?', {icon: 3, title:'系统提示'}, function(index){
                layer.close(index);

                $.ajax({
                    type : 'POST',
                    url : url,
                    data : {
                        "id" : id
                    },
                    dataType : "json",
                    success : function(data){
                        if (data.code == 200) {
                            layer.msg(typeName + "成功", {
                                icon: 1,
                                time: 2000 //2秒关闭（如果不配置，默认是3秒）
                            }, function(layero, index){
                                jobTable.fnDraw();
                            });
                        } else {
                            layer.msg(data.msg || typeName + "失败", {
                                icon: 2,
                                time: 2000 //2秒关闭（如果不配置，默认是3秒）
                            });
                        }
                    },
                });
            });
        }

    });
    $('#searchBtn').on('click', function(){
        jobTable.fnDraw();
    });

});


