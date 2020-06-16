$(function () {

    // 复选框
    $('input').iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%' // optional
    });

    // 登录.规则校验
    var loginFormValid = $("#loginForm").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            userName: {
                required: true,
                minlength: 5,
                maxlength: 18
            },
            password: {
                required: true,
                minlength: 5,
                maxlength: 18
            }
        },
        messages: {
            userName: {
                required: "请输入登录账号.",
                minlength: "登录账号不应低于5位",
                maxlength: "登录账号不应超过18位"
            },
            password: {
                required: "请输入登录密码.",
                minlength: "登录密码不应低于5位",
                maxlength: "登录密码不应超过18位"
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
            var isRmbPwd = $(".checkbox div").hasClass("checked");
            var username = $("#username").val();
            if (isRmbPwd == true) {
                $.cookie('This is username', username, {expires: 7});
                $.cookie(username, $("#password").val(), {expires: 7});
            } else {
                $.removeCookie('This is username');
                $.removeCookie('username');
            }
            var BACK_URL=getUrlParam("backurl");
            console.log(BACK_URL);

            $("#backurl").val(BACK_URL);
            $.post(base_url + "login", $("#loginForm").serialize(), function(data, status) {
               // window.location.href = base_url;
                if(null!=data){
                    if(data.code==1){
                        location.href = data.data;

                    }else{
                        layer.msg(data.data || "失败", {
                            icon: 2,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }
                }

            });
        }
    });


    (function () {

        var username = $.cookie("This is username");
        //如果用户名为空,则给表单元素赋空值
        if (username) {
            var password = $.cookie(username);

            document.getElementById("username").value = username;
            document.getElementById("password").value = password;
            $('#check').iCheck('check');
        }

        $('#myRemember,.remember').click(function (event) {
            event.preventDefault();
        })

    })();
});

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg); //匹配目标参数
    if (r != null) {
        return decodeURIComponent(r[2]);
    }
    return null;
}