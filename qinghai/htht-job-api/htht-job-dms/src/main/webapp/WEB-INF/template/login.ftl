<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1 user-scalable=0">
    <title>卫星遥感集群并行支撑平台</title>
    <link rel="stylesheet" href="${request.contextPath}/static/login/css/login.css">
    <style>
    </style>
</head>
<body>
<div class="container">
    <div class="main">
        <h3 class="title">
        <#--${Session.tileList}-->
        <#if Session.tileList?? && (Session.tileList?size > 0) >
            <#list Session.tileList as titleDic>
                <#if titleDic.dictCode== "bigTitle">
                    ${titleDic.dictName}
                </#if>
            </#list>
        <#else>
 			并行支撑平台
        </#if>
        </h3>
        <div class="inputbox">
            <form id="loginForm" method="post">
                <div class="userName input"><input id="username" type="text" name="userName" class="form-control"
                                                   placeholder="请输入登录账号" value=""></div>
                <div class="passWord input"><input id="password" type="password" name="password" class="form-control"
                                                   placeholder="请输入登录密码" value=""></div>
                <div class="remember"><p class="checkbox"><input type="checkbox" name="ifRemember" id="check">
                    <span></span></p><label for="check" id="myRemember">记住密码</label></div>
                <div class="submit"><input type="submit" value="登录"></div>
                <input type="hidden" name="backurl" id="backurl">
            </form>
        </div>
    </div>
</div>
<script src="${request.contextPath}/static/adminlte/plugins/jQuery/jquery-2.2.3.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/jQuery/jquery.cookie.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<script src="${request.contextPath}/static/js/login.1.js"></script>
<script src="${request.contextPath}/static/plugins/layer/layer.js"></script>
<script>var base_url = '${request.contextPath}';</script>
</body>
</html>
