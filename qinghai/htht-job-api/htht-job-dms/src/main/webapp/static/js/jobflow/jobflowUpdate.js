$(function () {
    var executorParam=$("#executorParam").val();
    var priority = $('#priority').val();
    var modelIdInput = $('#modelIdInput').val();
    $(".form select[name='priority']").val(priority);
    reportTableFlow("reportTable", base_url + "/jobflow/getUpdateParameters?id=" +executorParam+"&modelId="+modelIdInput);
    $('.form select[name=modelId]').change(function () {
        var modelId = $(".form select[name='modelId']").val();
        reportTableFlow("reportTable", base_url + "/jobflow/getFlowParameter?id=" + modelId + "");
    });

    //hyf
    var myVar = {
        updata:''
    };
    function isMailbox(s){//邮箱
        var re = new RegExp(/^((([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6}\,))*(([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})))$/);
        myVar.isMailbox = re.test(s);
        console.log( myVar.isMailbox)
    }

    function selectionChanged() {
        if($('#inputChange').val() !== ''){
            isMailbox($('#inputChange').val());
            if(myVar.isMailbox === false){
                $('#input-error').html('请输入“正确的邮箱”');
                myVar.updata = false
            }else {
                $('#input-error').html(' ');
                myVar.updata = true
            }
        }else {
            $('#input-error').html(' ');
            myVar.updata = true
        }
    }

    $('#upDateSave').click(function () {
        selectionChanged();
    })

    var updateModalValidate = $(".form").validate({
        errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
            jobDesc : {
                required : true,
                maxlength: 50
            },
            jobCron : {
                required : true
            }
        },
        messages : {
            jobDesc : {
                required :"请输入任务名称"
            },
            jobCron : {
                required :"请输入任务执行策略"
            }
        },
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
            //hyf
            var jobCronName = $(".form select[name='jobCron']").find("option:selected").text();
            $(".form input[name='jobCronName']").val(jobCronName);
            var tasktype=$("#tasktypevalue").val();
            var dynamicParameter = $('#reportTable').bootstrapTable('getData');
            $("#dynamicParameter").val(JSON.stringify(dynamicParameter));
            if(myVar.updata == true){
                $.post(base_url + "/jobflow/updateSave",  $(".form").serialize(), function(data, status) {
                    if (data.code == "0") {
                        layer.msg( "修改任务成功", {
                            icon: 1,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function(layero, index){
                            //hyf
                            if(tasktype==5) {
                                window.location.href = base_url + "/jobflow";
                            }else  if($("#tasktypevalue").val() == 9){
                            	//跳转数管流程
                                window.location.href = base_url + "/preData/dataJobFlow";
                            }else{
                                window.location.href = base_url + "/preData/gfPreData";

                            }
                            // window.location.href = base_url + "/jobflow";
                        });
                    } else {
                        layer.msg(data.msg || "修改失败", {
                            icon: 2,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }
                });
            }
        }
    });

});


