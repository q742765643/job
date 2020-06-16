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

</#macro>

<#macro commonHeader>
<header class="main-header">
    <a href="${request.contextPath}/" target="view_frame" class="logo">
        <span class="logo-mini"><b>XXL</b></span>
        <span class="logo-lg"><b>并行支撑平台</b></span>
    </a>
    <nav class="navbar navbar-static-top" role="navigation">
        <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button"><span class="sr-only">切换导航</span></a>
        <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
                <li class="dropdown user user-menu">
                    <a href=";" id="logoutBtn" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                        <span class="hidden-xs">注销</span>
                    </a>
                </li>
            </ul>
        </div>
    </nav>
</header>
</#macro>

<#macro commonLeft pageName >
<!-- Left side column. contains the logo and sidebar -->
<#--
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less &ndash;&gt;
    <section class="sidebar">
        <!-- sidebar menu: : style can be found in sidebar.less &ndash;&gt;
        <ul class="sidebar-menu">
            <li class="header">常用模块</li>
            <li class="nav-click <#if pageName == "jobinfo">active</#if>"><a href="${request.contextPath}/jobinfo"><i
                    class="fa fa-circle-o text-aqua"></i><span>任务管理</span></a></li>
            <li class="nav-click <#if pageName == "joblog">active</#if>"><a href="${request.contextPath}/joblog"><i
                    class="fa fa-circle-o text-yellow"></i><span>调度日志</span></a></li>
            <li class="nav-click <#if pageName == "jobgroup">active</#if>"><a href="${request.contextPath}/jobgroup"><i
                    class="fa fa-circle-o text-green"></i><span>执行器管理</span></a></li>
          &lt;#&ndash;  <li class="nav-click <#if pageName == "help">active</#if>"><a href="${request.contextPath}/help"><i
                    class="fa fa-circle-o text-gray"></i><span>使用教程</span></a></li>&ndash;&gt;
            <li class="nav-click <#if pageName == "help">active</#if>"><a href="${request.contextPath}/processmodel"><i
                    class="fa fa-circle-o text-gray"></i><span>产品模型</span></a></li>
            <li class="nav-click <#if pageName == "help">active</#if>"><a href="${request.contextPath}/product"><i
                    class="fa fa-circle-o text-gray"></i><span>产品</span></a></li>
            <li class="nav-click <#if pageName == "help">active</#if>"><a href="${request.contextPath}/productfileinfo"><i
                    class="fa fa-circle-o text-gray"></i><span>产品结果</span></a></li>

        </ul>
    </section>
    <!-- /.sidebar &ndash;&gt;
</aside>
-->
<div class="wrapper">
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
        <!-- Sidebar user panel -->
        <div class="user-panel">
            <div class="pull-left image">
                <img src="${request.contextPath}/static/adminlte/dist/user2-160x160.jpg" class="img-circle"
                     alt="User Image">
            </div>
            <div class="pull-left info">
                <p>admin</p>
                <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
            </div>
        </div>

        <!-- sidebar menu: : style can be found in sidebar.less -->
        <ul class="sidebar-menu" data-widget="tree">
            <li class="header">菜单</li>
            <li class="active treeview">
                <a href="#">
                    <i class="fa fa-dashboard"></i> <span>任务管理</span>
                    <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
                </a>
                <ul class="treeview-menu">
                    <li class="active"><a href="${request.contextPath}/jobinfo/1" target="view_frame"><i class="fa fa-circle-o"></i>
                        算法任务管理</a></li>
                    <@shiro.hasPermission name="newPage.jhtml">
                    <li class="active"><a href="${request.contextPath}/jobinfo/2" target="view_frame"><i class="fa fa-circle-o"></i>
                        下载任务管理</a></li>
                    </@shiro.hasPermission>
                </ul>
            </li>
            <li class="treeview">
                <a href="${request.contextPath}/joblog" target="view_frame">
                    <i class="fa fa-files-o"></i>
                    <span>调度日志</span>
                    <span class="pull-right-container">
            </span>
                </a>
            </li>
            <li class="treeview">
                <a href="${request.contextPath}/jobgroup" target="view_frame">
                    <i class="fa fa-th"></i> <span>执行器管理</span>
                    <span class="pull-right-container">
            </span>
                </a>
            </li>
            <li class="treeview">
                <a href="${request.contextPath}/processmodel" target="view_frame">
                    <i class="fa fa-pie-chart"></i>
                    <span>产品模型</span>
                    <span class="pull-right-container">
            </span>
                </a>
            </li>
            <li class="treeview">
                <a href="${request.contextPath}/product" target="view_frame">
                    <i class="fa fa-laptop"></i>
                    <span>产品</span>
                    <span class="pull-right-container">
            </span>
                </a>
            </li>
            <li class="treeview">
                <a href="${request.contextPath}/productfileinfo" target="view_frame">
                    <i class="fa fa-edit"></i> <span>产品结果</span>
                    <span class="pull-right-container">
            </span>
                </a>
            </li>
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>
<div class="content-wrapper">
    <iframe src="" frameborder="0" scrolling="no" name="view_frame" width="100%" height="100%"></iframe>

</div>
    <footer class="main-footer">
        <b>HTHT-JOB</b> 1.0
        <div class="pull-right hidden-xs">
            <strong>Copyright &copy; 2017-${.now?string('yyyy')} &nbsp;
                <a href="#" target="_blank">北京航天宏图科技有限公司</a>
                &nbsp;
            </strong><!-- All rights reserved. -->
        </div>
    </footer>
</div>
</#macro>

<#macro commonControl >
<!-- Control Sidebar -->
<aside class="control-sidebar control-sidebar-dark">
    <!-- Create the tabs -->
    <ul class="nav nav-tabs nav-justified control-sidebar-tabs">
        <li class="active"><a href="#control-sidebar-home-tab" data-toggle="tab"><i class="fa fa-home"></i></a></li>
        <li><a href="#control-sidebar-settings-tab" data-toggle="tab"><i class="fa fa-gears"></i></a></li>
    </ul>
    <!-- Tab panes -->
    <div class="tab-content">
        <!-- Home tab content -->
        <div class="tab-pane active" id="control-sidebar-home-tab">
            <h3 class="control-sidebar-heading">近期活动</h3>
            <ul class="control-sidebar-menu">
                <li>
                    <a href="javascript::;">
                        <i class="menu-icon fa fa-birthday-cake bg-red"></i>
                        <div class="menu-info">
                            <h4 class="control-sidebar-subheading">张三今天过生日</h4>
                            <p>2015-09-10</p>
                        </div>
                    </a>
                </li>
                <li>
                    <a href="javascript::;">
                        <i class="menu-icon fa fa-user bg-yellow"></i>
                        <div class="menu-info">
                            <h4 class="control-sidebar-subheading">Frodo 更新了资料</h4>
                            <p>更新手机号码 +1(800)555-1234</p>
                        </div>
                    </a>
                </li>
                <li>
                    <a href="javascript::;">
                        <i class="menu-icon fa fa-envelope-o bg-light-blue"></i>
                        <div class="menu-info">
                            <h4 class="control-sidebar-subheading">Nora 加入邮件列表</h4>
                            <p>nora@example.com</p>
                        </div>
                    </a>
                </li>
                <li>
                    <a href="javascript::;">
                        <i class="menu-icon fa fa-file-code-o bg-green"></i>
                        <div class="menu-info">
                            <h4 class="control-sidebar-subheading">001号定时作业调度</h4>
                            <p>5秒前执行</p>
                        </div>
                    </a>
                </li>
            </ul>
            <!-- /.control-sidebar-menu -->
        </div>
        <!-- /.tab-pane -->

        <!-- Settings tab content -->
        <div class="tab-pane" id="control-sidebar-settings-tab">
            <form method="post">
                <h3 class="control-sidebar-heading">个人设置</h3>
                <div class="form-group">
                    <label class="control-sidebar-subheading"> 左侧菜单自适应
                        <input type="checkbox" class="pull-right" checked>
                    </label>
                    <p>左侧菜单栏样式自适应</p>
                </div>
                <!-- /.form-group -->

            </form>
        </div>
        <!-- /.tab-pane -->
    </div>
</aside>
<!-- /.control-sidebar -->
<!-- Add the sidebar's background. This div must be placed immediately after the control sidebar -->
<div class="control-sidebar-bg"></div>
</#macro>

<#macro commonFooter >
    <b>HTHT-JOB</b> 1.0
    <div class="pull-right hidden-xs">
        <strong>Copyright &copy; 2017-${.now?string('yyyy')} &nbsp;
            <a href="#" target="_blank">北京航天宏图科技有限公司</a>
            &nbsp;
        </strong><!-- All rights reserved. -->
    </div>
</#macro>