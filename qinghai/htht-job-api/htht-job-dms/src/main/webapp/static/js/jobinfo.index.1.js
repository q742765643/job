$(function () {
    //hyf
    var myVar = {
        alarmEmail:'',
        selectValue:'',
        flag:'',
        isMailbox:'',
        submit:'',
        updata:''
    };

    $("#executorFailStrategy option[value='FAIL_RETRY_FIVE']").remove();

    $('#executorFailStrategy').change(function () {
        $('#inputChange').val('');
        $('#input-error').html(' ');
        selectionChanged();
    });
    function selectionChanged() {
        console.log($('#executorFailStrategy').val())
        console.log($('#inputChange').val())

        myVar.selectValue = $('#executorFailStrategy option:selected').val()
        if(myVar.selectValue == 'FAIL_ALARM'){
            $('#labelChange').html('报警邮件');
            $('#inputChange').attr('placeholder','多个邮件逗号分隔');
            if($('#inputChange').val() !== ''){
                isMailbox($('#inputChange').val());
                if(myVar.isMailbox === false){
                    $('#input-error').html('请输入“正确的邮箱”');
                    myVar.submit = false
                }else {
                    $('#input-error').html(' ');
                    myVar.submit = true
                }
            }else {
                $('#input-error').html(' ');
                myVar.submit = true
            }
        }

        if(myVar.selectValue == 'FAIL_RETRY'){
            $('#labelChange').html('重试次数');
            $('#inputChange').attr('placeholder','只能输入数字');
            if($('#inputChange').val() !== ''){
                isPositiveInteger($('#inputChange').val());
                if(myVar.flag === false){
                    $('#input-error').html('请输入“正确的数字”');
                    myVar.submit = false
                }else {
                    $('#input-error').html(' ');
                    myVar.submit = true
                }
            }else {
                $('#input-error').html(' ');
                myVar.submit = true
            }
        }

    }

    function isPositiveInteger(s){//数字
         var re = /^[0-9]+$/ ;
         myVar.flag = re.test(s)
    }
    function isMailbox(s){//邮箱
        var re = new RegExp(/^((([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6}\,))*(([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})))$/);
        myVar.isMailbox = re.test(s);
    }
    $('#save').click(function () {
        selectionChanged();
    })


    var tasktype = 1;
    if ($("#tasktype").val()) {
        tasktype = $("#tasktype").val();
    }

    // init date tables
    var jobTable = $("#job_list").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
//        'bStateSave': true,
        "ajax": {
            url: base_url + "/jobinfo/pageList/" + tasktype,
            type: "post",
            data: function (d) {
                console.log(d)
                var obj = {};
                obj.jobGroup = $('#jobGroup').val();
                obj.executorHandler = $('#executorHandler').val();
                obj.start = d.start;
                obj.length = d.length;
                return obj;
            },
		    "dataSrc": function ( data ) {
		      	if(data.data){
		      		return data.data;
		      	}else{
		      		return [];
		      	}
		      }
        },
        "searching": false,
        "ordering": false,
        //"scrollX": true,	// X轴滚动条，取消自适应
        "columns": [
            {"data": 'id', "bSortable": true, "visible": true},
            {
                "data": 'jobGroup',
                "visible": false,
                "render": function (data, type, row) {
                    var groupMenu = $("#jobGroup").find("option");
                    for (var index in $("#jobGroup").find("option")) {
                        if ($(groupMenu[index]).attr('value') == data) {
                            return $(groupMenu[index]).html();
                        }
                    }
                    return data;
                }
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
            {"data": 'jobDesc', "visible": true, "width": '20%'},
            {
                "data": 'triggers.nextfireTime',
                "visible": true,
                "width": '15%',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {
                "data": 'triggers.prevfireTime',
                "visible": false,
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {
                "data": 'glueType',
                "visible": false,
                "render": function (data, type, row) {
                    if ('GLUE_GROOVY' == row.glueType) {
                        return "GLUE模式(Java)";
                    } else if ('GLUE_SHELL' == row.glueType) {
                        return "GLUE模式(Shell)";
                    } else if ('GLUE_PYTHON' == row.glueType) {
                        return "GLUE模式(Python)";
                    } else if ('GLUE_NODEJS' == row.glueType) {
                        return "GLUE模式(Nodejs)";
                    } else if ('BEAN' == row.glueType) {
                        return "BEAN模式：" + row.executorHandler;
                    }
                    return row.executorHandler;
                }
            },
            {"data": 'executorParam', "visible": false},
            {"data": 'jobCron', "visible": false, "width": '10%'},
            {"data": 'jobCronName',"visible": true,"width": '10%'},
            {
                "data": 'addTime',
                "visible": false,
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {
                "data": 'updateTime',
                "visible": false,
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {"data": 'author', "visible": false, "width": '10%'},
            {"data": 'alarmEmail', "visible": false},
            {"data": 'glueType', "visible": false},
            {
                "data": 'jobStatus',
                "width": '10%',
                "visible": true,
                "render": function (data, type, row) {
                    if ('NORMAL' == data) {
                        return '<small class="label label-success"    title="正常"><i class="fa fa-clock-o"></i>' + data + '</small>';
                    } else if ('PAUSED' == data) {
                        return '<small class="label label-default" title="暂停" ><i class="fa fa-clock-o"></i>' + data + '</small>';
                    } else if ('BLOCKED' == data) {
                        return '<small class="label label-default" title="阻塞[串行]" ><i class="fa fa-clock-o"></i>' + data + '</small>';
                    }
                    return data;
                }
            },
            {
                "data": '操作',
                "width": '20%',
                "render": function (data, type, row) {
                    return function () {
                        // status
                        var pause_resume = "";
                        if ('NORMAL' == row.jobStatus) {
                            pause_resume = '<button class="btn btn-primary btn-xs job_operate" _type="job_pause" type="button">暂停</button>  ';
                        } else if ('PAUSED' == row.jobStatus) {
                            pause_resume = '<button class="btn btn-primary btn-xs job_operate" _type="job_resume" type="button">运行</button>  ';
                        }
                        // log url
                        var logUrl = base_url + '/joblog?jobId=' + row.id;

                        // log url
                        var codeBtn = "";
                        if ('BEAN' != row.glueType) {
                            var codeUrl = base_url + '/jobcode?jobId=' + row.id;
                            codeBtn = '<button class="btn btn-warning btn-xs" type="button" onclick="javascript:window.open(\'' + codeUrl + '\')" >GLUE</button>  '
                        }

                        // html
                        tableData['key' + row.id] = row;


                        return getbutton(row, logUrl, codeBtn, pause_resume);
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

    // table data
    var tableData = {};

    // 搜索按钮
    $('#searchBtn').on('click', function () {
        jobTable.fnDraw(false);
    });

    // jobGroup change
    $('#jobGroup').on('change', function () {
        //reload
        var jobGroup = $('#jobGroup').val();
        window.location.href = base_url + "/jobinfo?jobGroup=" + jobGroup;
    });
    
    //复制
    var jobList = document.getElementById("job_list")
    var span = document.createElement('span'); //1、创建元素
    span.className = 'copy'
    span.innerHTML='<p>复制</p> '; //2.
    jobList.appendChild(span); //3、在末尾中添加元素
    var copy = document.querySelector('#job_list .copy')
    jobList.addEventListener(
        'contextmenu',
        function (e) {
            e.preventDefault()
             console.log('aaa')
            let id = e.target.parentNode.children[0].innerHTML
            if(e.target.parentNode.nodeName == 'TR'){
                copy.style.left = e.clientX + 'px'
                copy.style.top = e.clientY - 158 + 'px'
                copy.style.display = 'block'
                copy.onclick =
                    function (e) {
                       console.log(id)
                        $.ajax({
                            type: "POST",
                            url: base_url + "/jobinfo/copy",
                            data: {
                                id:id
                            },
                            dataType: "json",
                            success: function(data) {
                                if (data.code == 200) {
                                    jobTable.fnDraw(false);
                                    layer.msg(
                                        "复制成功",
                                        {
                                            icon: 1,
                                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                                        },

                                    );
                                }
                            }
                        });
                    }
            }

            //点击屏幕弹窗消失
            window.onclick = function (e) {
                copy.style.display = 'none'
            }
        });
    
    // job operate
    $("#job_list").on('click', '.job_operate', function () {
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
            layer.confirm('确认' + typeName + '?', {icon: 3, title: '系统提示'}, function (index) {
                layer.close(index);
                $.ajax({
                    type: 'POST',
                    url: url,
                    data: {
                        "id": id
                    },
                    dataType: "json",
                    success: function (data) {
                        if (data.code == 200) {
                            layer.msg(typeName + "成功", {
                                icon: 1,
                                time: 2000 //2秒关闭（如果不配置，默认是3秒）
                            }, function (layero, index) {
                                if (needFresh) {
                                    var start = $("#job_list").dataTable().fnSettings()._iDisplayStart;
                                    var total = $("#job_list").dataTable().fnSettings().fnRecordsDisplay();
                                    window.location.reload();

                                    if (total - start == 1) {
                                        if (start > 0) {
                                            $("#job_list").dataTable().fnPageChange('previous', true);
                                        }
                                    }
                                    window.location.reload();
                                    jobTable.fnDraw();
                                }
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

    // jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”
    jQuery.validator.addMethod("myValid01", function (value, element) {
        var length = value.length;
        var valid = /^[a-zA-Z][a-zA-Z0-9_]*$/;
        return this.optional(element) || valid.test(value);
    }, "只支持英文字母开头，只含有英文字母、数字和下划线");

    // 新增
    $(".add").click(function () {
        $("#addModal .form select[name=modelId]").trigger("change");
        $('#addModal').modal({backdrop: false, keyboard: false}).modal('show');

        $("#atomicAlgorithmSelectPicker").empty();
        $.ajax({
            type: "post",
            dataType: "json",
            url: base_url + "/jobinfo/getAtomicAlgorithms?productId=" + $("#productSelectPicker").val(),
            success: function (data) {
                var options = "";
                for (var i = 0; i < data.length; i++) {
                    options += "<option value='" + data[i].id + "'>" + data[i].modelName + "</option>";
                }
                $("#atomicAlgorithmSelectPicker").html(options); //为Select追加一个Option(下拉项)
                //    刷新
                $('#atomicAlgorithmSelectPicker').selectpicker('refresh');
            }
        });

    });
    var addModalValidate = $("#addModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            jobDesc: {
                required: true,
                maxlength: 50
            },
            jobCron: {
                required: true
            },
            author: {
                required: true
            },
        },
        messages: {
            jobDesc: {
                required: "请输入“任务名称”."
            },
            jobCron: {
                required: "请输入“Cron”."
            },
            author: {
                required: "请输入“负责人”."
            },
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
            var fixedParameter = $('#reportTable3').bootstrapTable('getData');
            var dynamicParameter = $('#reportTable4').bootstrapTable('getData');
            for (j = 0; j < dynamicParameter.length; j++) {
                if (dynamicParameter[j].dialogType == "prj_path" && $("#prj_path .form input[name='prj_Path']").val() != "") {
                    dynamicParameter[j].value = $("#prj_path .form input[name='prj_Path']").val();
                } else if (dynamicParameter[j].dialogType == "pan_xml_path" && $("#pan_xml_path .form input[name='panxml_path']").val() != "") {
                    dynamicParameter[j].value = $("#pan_xml_path .form input[name='panxml_path']").val();
                } else if (dynamicParameter[j].dialogType == "mss_xml_path" && $("#mss_xml_path .form input[name='mssxml_path']").val() != "") {
                    dynamicParameter[j].value = $("#mss_xml_path .form input[name='mssxml_path']").val();
                }
            }
            var executorHandler = $("#addModal .form select[name='modelId']").find("option:selected").text();
            $("#addModal .form input[name='fixedParameter']").val(JSON.stringify(fixedParameter));
            $("#addModal .form input[name='dynamicParameter']").val(JSON.stringify(dynamicParameter));
            $("#addModal .form input[name='executorHandler']").val(executorHandler);


            if ($("#addModal .form input:radio[name='timeType']:checked").val() == "now") {
                $("#addModal .form input[name='isopen']").val("true")
                $("#addModal .form input[name='times']").val($("#addModal .form input[name='downloadDays']").val())
            } else {
                $("#addModal .form input[name='isopen']").val("false")
                $("#addModal .form input[name='times']").val($("#addModal .form input[name='downloadDate']").val())
            }

            if ($("#addModal .form input:radio[name='reg']:checked").val() == "latLon") {
                $("#addModal .form input[name='maxLon']").val($("#addModal .form input[name='latAndLon']").val().split(",")[0])
                $("#addModal .form input[name='minLon']").val($("#addModal .form input[name='latAndLon']").val().split(",")[1])
                $("#addModal .form input[name='maxLat']").val($("#addModal .form input[name='latAndLon']").val().split(",")[2])
                $("#addModal .form input[name='minLat']").val($("#addModal .form input[name='latAndLon']").val().split(",")[3])
            } else {
                $("#addModal .form select[name='maxLon']").val("")
                $("#addModal .form select[name='minLon']").val("")
                $("#addModal .form select[name='maxLat']").val("")
                $("#addModal .form select[name='minLat']").val("")
            }

            //hyf
            var jobCronName = $("#addModal .form select[name='jobCron']").find("option:selected").text();
            $("#addModal .form input[name='jobCronName']").val(jobCronName);

            $("#addModal .form input[name='filename']").val($("#addModal .form input[name='filePrefix']").val() + "." + $("#addModal .form input[name='fileFormat']").val());
            var temSer = $("#reportTable3Div input,#reportTable3Div select,#reportTable3Div textarea").serializeJson();
            var temSerJson = JSON.stringify(temSer);
            $("#addModal .form input[name='modelParameters']").val(temSerJson);
            var temSer1 = $("#addModal input,#addModal select,#addModal textarea").serialize();
            console.log(temSer1)
            if(myVar.submit == true){
                $.post(base_url + "/jobinfo/add", temSer1, function (data, status) {
                    if (data.code == "200") {
                        $('#addModal').modal('hide');
                        layer.msg("新增任务成功", {
                            icon: 1,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function (layero, index) {
                            jobTable.fnDraw(false);
                        });
                    } else {
                        layer.msg(data.msg || typeName + "新增失败", {
                            icon: 2,
                            time: 1000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }
                });
            }
        }
    });
    $("#addModal").on('hide.bs.modal', function () {
        $("#addModal .form")[0].reset();
        addModalValidate.resetForm();
        $("#addModal .form .form-group").removeClass("has-error");
        $(".remote_panel").show();	// remote

        $("#addModal .form input[name='executorHandler']").removeAttr("readonly");
    });


    // 运行模式
    $(".glueType").change(function () {
        // executorHandler
        var $executorHandler = $(this).parents("form").find("input[name='executorHandler']");
        var glueType = $(this).val();
        if ('BEAN' != glueType) {
            $executorHandler.val("");
            $executorHandler.attr("readonly", "readonly");
        } else {
            $executorHandler.removeAttr("readonly");
        }
    });

    $("#addModal .glueType").change(function () {
        // glueSource
        var glueType = $(this).val();
        if ('GLUE_GROOVY' == glueType) {
            $("#addModal .form textarea[name='glueSource']").val($("#addModal .form .glueSource_java").val());
        } else if ('GLUE_SHELL' == glueType) {
            $("#addModal .form textarea[name='glueSource']").val($("#addModal .form .glueSource_shell").val());
        } else if ('GLUE_PYTHON' == glueType) {
            $("#addModal .form textarea[name='glueSource']").val($("#addModal .form .glueSource_python").val());
        } else if ('GLUE_NODEJS' == glueType) {
            $("#addModal .form textarea[name='glueSource']").val($("#addModal .form .glueSource_nodejs").val());
        }
    });

    // 更新
    $("#job_list").on('click', '.update', function () {
        if ($(this).parents('td').prev().find('small').attr('title') === '正常') {
            layer.open({
                title: '系统提示',
                content: ("运行状态下，无法进行编辑或删除操作"),
                icon: '2'
            });
            return;
        }

        $("#upDateExecutorFailStrategy option[value='FAIL_RETRY_FIVE']").remove();

        var id = $(this).parent('p').attr("id");
        var row = tableData['key' + id];
        if (!row) {
            layer.open({
                title: '系统提示',
                content: ("任务信息加载失败，请刷新页面"),
                icon: '2'
            });
            return;
        }
        reportTableinfo("reportTable1", base_url + "/jobinfo/getJobParameter?jobId=" + row.executorParam + "&parameterId=" + row.modelId + "&mark=1", row.executorParam);
        reportTableinfo("reportTable2", base_url + "/jobinfo/getJobParameter?jobId=" + row.executorParam + "&parameterId=" + row.modelId + "&mark=2", "");

        // base data
        $("#updateModal .form input[name='id']").val(row.id);
        $('#updateModal .form select[name=jobGroup] option[value=' + row.jobGroup + ']').prop('selected', true);
        $("#updateModal .form input[name='jobDesc']").val(row.jobDesc);
        // $("#updateModal .form input[name='jobCron']").val( row.jobCron );
    //     $('#updateModal .form select[name=jobCron] option[value=' + row.jobCron + ']').prop('selected', true);
        $("#updateModal .form input[name='author']").val(row.author);
        $("#updateModal .form input[name='alarmEmail']").val(row.alarmEmail);
        $('#updateModal .form select[name=executorRouteStrategy] option[value=' + row.executorRouteStrategy + ']').prop('selected', true);
        $('#updateModal .form select[name=modelId]').selectpicker('val', row.modelId);
        $('#updateModal .form select[name=productId]').selectpicker('val', row.productId);

        $('#updateModal .form select[name=jobCron]').selectpicker('val', row.jobCron);

        $("#updateModal .form input[name='executorParam']").val(row.executorParam);
        $("#updateModal .form input[name='childJobKey']").val(row.childJobKey);
        $('#updateModal .form select[name=executorBlockStrategy] option[value=' + row.executorBlockStrategy + ']').prop('selected', true);
        $('#updateModal .form select[name=executorFailStrategy] option[value=' + row.executorFailStrategy + ']').prop('selected', true);
        $("#updateModal .form select[name='priority']").val(row.priority);
        //$('#updateModal .form select[name=glueType] option[value='+ row.glueType +']').prop('selected', true);
        //$("#updateModal .form select[name=glueType]").change();

        //hyf
        //初始化设置值
        $('#upDate-error').html(' ');
        var defaultDate = row.executorFailStrategy;
        var defaultalarmEmail = row.alarmEmail;
        $('#upDateExecutorFailStrategy').change(function () {
            $('#upDatealarmEmail').val('');
            $('#upDatealarmEmail').attr('placeholder','');
            $('#upDate-error').html(' ');
            changeSelect();
        });
        //设置默认值
        function upDateSelection() {
            if(row.executorFailStrategy == 'FAIL_ALARM'){
                $('#upDateLabel').html('报警邮件');
                if(defaultDate == 'FAIL_ALARM'){
                    $("#updateModal .form input[name='alarmEmail']").val(row.alarmEmail);
                }
            }
            if(row.executorFailStrategy == 'FAIL_RETRY'){
                $('#upDateLabel').html('重试次数');
                if(defaultDate == 'FAIL_ALARM'){
                    $("#updateModal .form input[name='alarmEmail']").val(row.alarmEmail);
                }
            }
        }
        //改变值
        function changeSelect() {
            if($('#upDateExecutorFailStrategy option:selected').val() == 'FAIL_ALARM'){
                $('#upDateLabel').html('报警邮件');
                if(defaultDate == 'FAIL_ALARM'){
                    $('#upDatealarmEmail').attr('placeholder','');
                    if($('#upDatealarmEmail').val() == defaultalarmEmail){
                        $("#updateModal .form input[name='alarmEmail']").val(row.alarmEmail);
                    }else {
                        $("#updateModal .form input[name='alarmEmail']").val($('#upDatealarmEmail').val());
                    }
                }else {
                    $('#upDatealarmEmail').attr('placeholder','多个邮件逗号分隔');
                }

                if($('#upDatealarmEmail').val() !== ''){
                    isMailbox($('#upDatealarmEmail').val());
                    if(myVar.isMailbox === false){
                        $('#upDate-error').html('请输入“正确的邮箱”');
                        myVar.updata = false
                    }else {
                        $('#upDate-error').html(' ');
                        myVar.updata = true
                    }
                }else {
                    $('#upDate-error').html(' ');
                    myVar.updata = true
                }
            }
            if($('#upDateExecutorFailStrategy option:selected').val() == 'FAIL_RETRY'){
                $('#upDateLabel').html('重试次数');
                if(defaultDate == 'FAIL_RETRY'){
                    $('#upDatealarmEmail').attr('placeholder','');
                    console.log($('#upDatealarmEmail').val());
                    if($('#upDatealarmEmail').val() == defaultalarmEmail ){
                        $("#updateModal .form input[name='alarmEmail']").val(row.alarmEmail);
                    }else {
                        $("#updateModal .form input[name='alarmEmail']").val($('#upDatealarmEmail').val());
                    }
                }else {
                    $('#upDatealarmEmail').attr('placeholder','只能输入数字');
                }
                if($('#upDatealarmEmail').val() !== ''){
                    isPositiveInteger($('#upDatealarmEmail').val());
                    if(myVar.flag === false){
                        $('#upDate-error').html('请输入“正确的数字”');
                        myVar.updata = false
                    }else {
                        $('#upDate-error').html(' ');
                        myVar.updata = true
                    }
                }else {
                    $('#upDate-error').html(' ');
                    myVar.updata = true
                }
            }
        }
        upDateSelection();

        $('#upDateSave').click(function () {
            changeSelect();
        });


        //根据任务ID 获取关联的原子算法
        if ($("#modelProductSelectPicker").val()) {
            $("#modelAtomicAlgorithmSelectPicker").empty();
            $.ajax({
                type: "post",
                dataType: "json",
                url: base_url + "/jobinfo/getAtomicAlgorithms?productId=" + $("#modelProductSelectPicker").val(),
                success: function (data) {
                    var options = "";
                    for (var i = 0; i < data.length; i++) {
                        options += "<option value='" + data[i].id + "'>" + data[i].modelName + "</option>";
                    }
                    $("#modelAtomicAlgorithmSelectPicker").html(options); //为Select追加一个Option(下拉项)

                    $("#modelAtomicAlgorithmSelectPicker").selectpicker('val', row.modelId);

                    //    刷新
                    $('#modelAtomicAlgorithmSelectPicker').selectpicker('refresh');
                }
            });
        }
        // show
        $('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    var updateModalValidate = $("#updateModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,

        rules: {
            jobDesc: {
                required: true,
                maxlength: 50
            },
            jobCron: {
                required: true
            },
            author: {
                required: true
            }
        },
        messages: {
            jobDesc: {
                required: "请输入“任务名称”."
            },
            jobCron: {
                required: "请输入“Cron”."
            },
            author: {
                required: "请输入“负责人”."
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
            var fixedParameter = $('#reportTable1').bootstrapTable('getData');
            var dynamicParameter = $('#reportTable2').bootstrapTable('getData');
            for (j = 0; j < dynamicParameter.length; j++) {
                if (dynamicParameter[j].dialogType == "prj_path" && $("#prj_path .form input[name='prj_Path']").val() != "") {
                    dynamicParameter[j].value = $("#prj_path .form input[name='prj_Path']").val();
                } else if (dynamicParameter[j].dialogType == "pan_xml_path" && $("#pan_xml_path .form input[name='panxml_path']").val() != "") {
                    dynamicParameter[j].value = $("#pan_xml_path .form input[name='panxml_path']").val();
                } else if (dynamicParameter[j].dialogType == "mss_xml_path" && $("#mss_xml_path .form input[name='mssxml_path']").val() != "") {
                    dynamicParameter[j].value = $("#mss_xml_path .form input[name='mssxml_path']").val();
                }
            }

            //hyf
            var jobCronName = $("#updateModal .form select[name='jobCron']").find("option:selected").text();
            $("#updateModal .form input[name='jobCronName']").val(jobCronName);

            var executorHandler = $("#updateModal .form select[name='modelId']").find("option:selected").text();
            $("#updateModal .form input[name='fixedParameter']").val(JSON.stringify(fixedParameter));
            $("#updateModal .form input[name='dynamicParameter']").val(JSON.stringify(dynamicParameter));
            $("#updateModal .form input[name='executorHandler']").val(executorHandler);
            $("#updateModal .form input[name='filename']").val($("#updateModal .form input[name='filePrefix']").val() + "." + $("#updateModal .form input[name='fileFormat']").val());


            if ($("#updateModal .form input:radio[name='timeType']:checked").val() == "now") {
                $("#updateModal .form input[name='isopen']").val(true)
                $("#updateModal .form input[name='times']").val($("#updateModal .form input[name='downloadDays']").val())
            } else {
                $("#updateModal .form input[name='isopen']").val(false)
                $("#updateModal .form input[name='times']").val($("#updateModal .form input[name='downloadDate']").val())
            }
            if ($("#updateModal .form input:radio[name='reg']:checked").val() == "latLon") {
                $("#updateModal .form input[name='maxLon']").val($("#updateModal .form input[name='latAndLon']").val().split(",")[0])
                $("#updateModal .form input[name='minLon']").val($("#updateModal .form input[name='latAndLon']").val().split(",")[1])
                $("#updateModal .form input[name='maxLat']").val($("#updateModal .form input[name='latAndLon']").val().split(",")[2])
                $("#updateModal .form input[name='minLat']").val($("#updateModal .form input[name='latAndLon']").val().split(",")[3])
            } else {
                $("#updateModal .form select[name='maxLon']").val("")
                $("#updateModal .form select[name='minLon']").val("")
                $("#updateModal .form select[name='maxLat']").val("")
                $("#updateModal .form select[name='minLat']").val("")
            }
            var temSer = $("#reportTable1Div input,#reportTable1Div select,#reportTable1Div textarea").serializeJson();
            var temSerJson = JSON.stringify(temSer);
            $("#updateModal .form input[name='modelParameters']").val(temSerJson);
            var temSer2 = $("#updateModal input,#updateModal select,#updateModal textarea").serialize();
            //hyf
            if(myVar.updata == true){
                $.post(base_url + "/jobinfo/reschedule", temSer2, function (data, status) {
                    if (data.code == "200") {
                        $('#updateModal').modal('hide');
                        layer.msg('更新成功', {
                            icon: 1,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function () {
                            jobTable.fnDraw(false);
                        });
                    } else {
                        layer.msg(data.msg || "更新失败", {
                            icon: 2,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }
                });
            }
        }
    });
    $("#updateModal").on('hide.bs.modal', function () {
        $("#updateModal .form")[0].reset()
    });


    // 运行模式
    $('#updateModal .form select[name=modelId]').change(function () {
        var modelId = $("#updateModal .form select[name='modelId']").val();
        reportTableinfo("reportTable1", base_url + "/jobinfo/getJobParameter?jobId=''&parameterId=" + modelId + "&mark=1", "");
        reportTableinfo("reportTable2", base_url + "/jobinfo/getJobParameter?jobId=''&parameterId=" + modelId + "&mark=2", "");

    });

    $('#addModal .form select[name=modelId]').change(function () {
        var modelId = $("#addModal .form select[name='modelId']").val();
        reportTableinfo("reportTable3", base_url + "/jobinfo/getJobParameter?jobId=''&parameterId=" + modelId + "&mark=1", "");
        reportTableinfo("reportTable4", base_url + "/jobinfo/getJobParameter?jobId=''&parameterId=" + modelId + "&mark=2", "");

    });


});


