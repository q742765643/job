$(function () {
    reportTableFlow("reportTable", null);
    $('.form select[name=modelId]').change(function () {
        var modelId = $(".form select[name='modelId']").val();
        reportTableFlow("reportTable", base_url + "/jobflow/getFlowParameter?id=" + modelId + "");
    });
//hyf
    var myVar = {
        submit:''
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

    $('#save').click(function () {
        selectionChanged();
    })

    var addModalValidate = $(".form").validate({
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
                required :"请输入任务名称."
            },
            jobCron : {
                required :"请输入任务执行策略."
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

            var dynamicParameter = $('#reportTable').bootstrapTable('getData');
            $("#dynamicParameter").val(JSON.stringify(dynamicParameter));
            if(myVar.submit == true){
                $.post(base_url + "/jobflow/addSave",  $(".form").serialize(), function(data, status) {
                    if (data.code == "0") {
                        layer.msg('新增任务成功', {
                            icon: 1,
                            time: 1000 //2秒关闭（如果不配置，默认是3秒）
                        }, function(){
                            //hyf
                            if($("#tasktypevalue").val()){
                                if($("#tasktypevalue").val() == 5){
                                    window.location.href = base_url + "/jobflow";
                                }else  if($("#tasktypevalue").val() == 7){
                                    window.location.href = base_url + "/preData/gfPreData";
                                }else  if($("#tasktypevalue").val() == 9){
                                	//跳转数管流程
                                    window.location.href = base_url + "/preData/dataJobFlow";
                                }
                            }
                        });
                    } else {
                        layer.msg(data.msg || "新增失败", {
                            icon: 2,
                            time: 1000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }
                });
            }
        }
    });

});


