<#macro commonStyle>

<#-- favicon -->
<link rel="icon" href="favicon.ico"/>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!-- Tell the browser to be responsive to screen width -->
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<!-- Bootstrap 3.3.5 -->
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<!-- <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css"> -->
<link rel="stylesheet" href="${request.contextPath}/static/plugins/font-awesome-4.5.0/css/font-awesome.min.css">
<!-- Ionicons -->
<!-- <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css"> -->
<link rel="stylesheet" href="${request.contextPath}/static/plugins/ionicons-2.0.1/css/ionicons.min.css">
<!-- Theme style -->
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/AdminLTE-local.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/skins/_all-skins.min.css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->

<!-- pace -->
<link rel="stylesheet" href="${request.contextPath}/static/plugins/pace/themes/pace-theme-flash.css">
</#macro>

<#macro commonScript>
<!-- jQuery 2.1.4 -->
<script src="${request.contextPath}/static/adminlte/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.5 -->
<script src="${request.contextPath}/static/adminlte/bootstrap/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="${request.contextPath}/static/adminlte/plugins/fastclick/fastclick.min.js"></script>
<!-- AdminLTE App -->
<script src="${request.contextPath}/static/adminlte/dist/js/app.min.js"></script>
<#-- jquery.slimscroll -->
<script src="${request.contextPath}/static/adminlte/plugins/slimScroll/jquery.slimscroll.min.js"></script>

<!-- pace -->
<script src="${request.contextPath}/static/plugins/pace/pace.min.js"></script>
<#-- jquery cookie -->
<script src="${request.contextPath}/static/plugins/jquery/jquery.cookie.js"></script>

<#-- layer -->
<script src="${request.contextPath}/static/plugins/layer/layer.js"></script>

<#-- common -->
<script src="${request.contextPath}/static/js/common.1.js"></script>
<script>var base_url = '${request.contextPath}';</script>
<style>
    body {
        background-color: #ecf0f5;
    }

    /*wsf*/
    /*一級標題顏色*/
    .skin-blue-light .sidebar a {
        color: #0E4A70;
    }

    /*二级标题文字*/
    .skin-blue-light .treeview-menu > li > a {
        color: #666666;
    }

    /*二级选中文字背景颜色*/
    .skin-blue-light .treeview-menu > li > a.bgcolor {
        background-color: #C8D6DE;
    }

    /*一级选中文字背景颜色*/
    .skin-blue-light .sidebar-menu > li > a.bgcolor {
        background-color: #C8D6DE;
    }

    /*一级选中状态文字颜色*/
    .skin-blue-light .sidebar-menu > li > a.bgcolor {
        color: #0E4A70;
    }

    /*二级选中状态文字颜色*/
    .skin-blue-light .treeview-menu > li > a.bgcolor {
        color: #0E4A70;
    }

</style>
</#macro>

