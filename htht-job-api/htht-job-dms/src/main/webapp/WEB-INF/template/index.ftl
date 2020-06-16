<!DOCTYPE html>
<html>
<head>
    <title>并行支撑平台</title>
<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />

    <style>
        html{
          overflow:hidden;
      }
        .sidebar-menu li>a {
            position: relative;
            background-color: #ffffff;
        }
        .skin-blue-light .treeview-menu > li > a.bgcolor{
            color: #fff;
            background-color: #3CA0EC;
            border-right: 1px solid #0000FF;
        }
        .skin-blue-light .sidebar-menu > li > a.bgcolor{
            color: #fff;
            background-color: #3CA0EC;
            border-right: 1px solid #0000FF;
        }
        .skin-blue-light .treeview-menu > li > a:hover{
            color: #353535;
            background-color: #eff1f3;
            border-right: 1px solid #0000FF;
        }
        .skin-blue-light .sidebar-menu > li > a:hover{
            color: #353535;
            background-color: #eff1f3;
            border-right: 1px solid #0000FF;
        }
    </style>
</head>
<body class="hold-transition skin-blue-light sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper" style="overflow: hidden">
    <header class="main-header" style="background: url(../../static/image/1_06.png) no-repeat left;)">
        <#--<a href="${request.contextPath}/main" target="view_frame" class="logo" disabled>-->
        <a href="${request.contextPath}/main"  target="view_frame" class="logo" disabled>
            <span class="logo-mini"><b>HTHT</b></span>
            <span class="logo-lg">
            	<b>
            		<#if Session.tileList?? && (Session.tileList?size > 0) >
			        	<#list Session.tileList as titleDic>
			        		<#if titleDic.dictCode== "bigTitle">
			        			${titleDic.dictName}
							</#if>
			        	</#list>
			 		<#else>
			 			并行支撑平台
			        </#if>
            	</b>
            </span>
        </a>
        <nav class="navbar navbar-static-top" role="navigation">
            <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button"><span
                    class="sr-only">切换导航</span></a>
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
                    <p>${userName}</p>
                    <p><i class="fa fa-circle text-success"></i> Online</p>
                </div>
            </div>

        <ul class="sidebar-menu" data-widget="tree">
            <li class="header">菜单</li>


         <@shiro.hasPermission name="datacollect">
            <li class=" treeview">
                <a href="#">
                    <i class="fa fa-database"></i> <span>数据汇集</span>
                    <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
                </a>
                <ul class="treeview-menu">
                    <@shiro.hasPermission name="remotedatacollect">
                        <li><a href="${request.contextPath}/dataCollect/remotedatacollect.index" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            遥感数据汇集</a></li>
                    </@shiro.hasPermission>
					<@shiro.hasPermission name="cimissdatacollect">
                    <li><a href="${request.contextPath}/dataCollect/cimissdatacollect.index" target="view_frame" onclick="loadIframe($(this))"><i
                            class="fa fa-circle-o"></i>
                        CIMISS数据汇集</a></li>
                    </@shiro.hasPermission>
                </ul>
            </li>
         </@shiro.hasPermission>

        <@shiro.hasPermission name="PreDat">
        <li class=" treeview">
            <a href="#">
            <i class="fa fa-repeat"></i> <span>数据预处理</span>
                <span class="pull-right-container">
          <i class="fa fa-angle-left pull-right"></i>
        </span>
            </a>
            <ul class="treeview-menu">

        <@shiro.hasPermission name="weatherPreData">
                <li><a href="${request.contextPath}/preData/weatherPreData" target="view_frame" onclick="loadIframe($(this))">
                    <i class="fa fa-cloud"></i>
                    气象卫星数据预处理</a></li>
        </@shiro.hasPermission>
        <@shiro.hasPermission name="gfPreData">
                <li><a href="${request.contextPath}/preData/gfPreData" target="view_frame" onclick="loadIframe($(this))">
                    <i class="fa fa-circle-o"></i>
                    高分数据预处理</a></li>
        </@shiro.hasPermission>
        <@shiro.hasPermission name="jobinfo">
                 <li><a href="${request.contextPath}/jobinfo/1" target="view_frame" onclick="loadIframe($(this))">
                    <i class="fa fa-circle-o"></i>
                    算法任务</a></li>
        </@shiro.hasPermission>
        <@shiro.hasPermission name="jobflow">
        <li><a href="${request.contextPath}/jobflow" target="view_frame" onclick="loadIframe($(this))">
                    <i class="fa fa-circle-o"></i>
                    流程任务</a></li>
        </@shiro.hasPermission>
        <@shiro.hasPermission name="joblog">
        <li><a href="${request.contextPath}/joblog" target="view_frame" onclick="loadIframe($(this))">
                    <i class="fa fa-circle-o"></i>
                    调度日志</a></li>
        </@shiro.hasPermission>
            </ul>
        </li>
        </@shiro.hasPermission>
        
        
        <@shiro.hasPermission name="dataSchedule">
        <li class=" treeview">
            <a href="#">
                <i class="fa fa-repeat"></i> <span>数管运维</span>
                <span class="pull-right-container">
                <i class="fa fa-angle-left pull-right"></i>
                </span>
            </a>
		    <ul class="treeview-menu">
			    <@shiro.hasPermission name="weatherPreData">
			            <li><a href="${request.contextPath}/preData/dataJobInfo" target="view_frame" onclick="loadIframe($(this))">
			                <i class="fa fa-cloud"></i>
			                数管调度任务</a></li>
			    </@shiro.hasPermission>
			    <@shiro.hasPermission name="gfPreData">
			            <li><a href="${request.contextPath}/preData/dataJobFlow" target="view_frame" onclick="loadIframe($(this))">
			                <i class="fa fa-circle-o"></i>
			                数管流程任务</a></li>
			    </@shiro.hasPermission>
		    </ul>
        </li>
        </@shiro.hasPermission>

        <@shiro.hasPermission name="product">
        <li class="treeview" >

            <a href="${request.contextPath}/jobinfo/product" target="view_frame" onclick="loadIframe($(this))">
                <i class="fa fa-list-ul"></i>
            <span>产品生产</span>
            <span class="pull-right-container"></span></a>
        </li>
        </@shiro.hasPermission>

		<@shiro.hasPermission name="flow">
           <li class="treeview">
               <a href="${request.contextPath}/flow" target="view_frame" onclick="loadIframe($(this))">
                   <i class="fa fa-th-list"></i>
                   <span>流程管理</span>
                   <span class="pull-right-container">
            </span>
               </a>
           </li>
        </@shiro.hasPermission>
        <@shiro.hasPermission name="productmanage">
            <li class=" treeview">
                <a href="#">
                    <i class="fa fa-repeat"></i> <span>产品管理</span>
                <span class="pull-right-container">
          <i class="fa fa-angle-left pull-right"></i>
        </span>
                </a>
                <ul class="treeview-menu">
                <@shiro.hasPermission name="productinfo">
                    <li class="treeview">
                        <a href="${request.contextPath}/product" target="view_frame" onclick="loadIframe($(this))">
                            <i class="fa fa-pie-chart"></i>
                            <span>产品分类</span>
                   <span class="pull-right-container">
            </span>
                        </a>
                    </li>
              </@shiro.hasPermission>
              <@shiro.hasPermission name="productfileinfo">      
                    <li class="treeview">
                        <a href="${request.contextPath}/productfileinfo" target="view_frame" onclick="loadIframe($(this))">
                            <i class="fa fa-pie-chart"></i>
                            <span>产品结果</span>
                   <span class="pull-right-container">
            </span>
                        </a>
                    </li>
             </@shiro.hasPermission>      
             <@shiro.hasPermission name="rasterStaticInfo">      
                    <li class="treeview">
                        <a href="${request.contextPath}/rasterStatic" target="view_frame" onclick="loadIframe($(this))">
                            <i class="fa fa-pie-chart"></i>
                            <span>统计查询</span>
                   <span class="pull-right-container">
            </span>
                        </a>
                    </li>
             </@shiro.hasPermission>  
                </ul>
            </li>
        </@shiro.hasPermission>
        

        <@shiro.hasPermission name="processmodel">
            <li class="treeview">
                <a href="${request.contextPath}/processmodel" target="view_frame" onclick="loadIframe($(this))">
                    <i class="fa fa-pie-chart"></i>
                    <span>算法管理</span>
                    <span class="pull-right-container">
            </span>
                </a>
            </li>
        </@shiro.hasPermission>


        <@shiro.hasPermission name="monitor">
            <li class="treeview">
                <a href="${request.contextPath}/monitor" target="view_frame" onclick="loadIframe($(this))">
                    <i class="fa fa-pie-chart"></i>
                    <span>节点管理</span>
                    <span class="pull-right-container">
            </span>
                </a>
            </li>
         </@shiro.hasPermission>   
        <@shiro.hasPermission name="system">
            <li class=" treeview">
                <a href="#">
                   <#--    <i class="fa fa-dashboard"></i> <span>支撑平台系统管理</span> -->
             <i class="fa fa-dashboard"></i> <span>系统管理</span> 
                    <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i></span>
                </a>
                <ul class="treeview-menu">
                    <@shiro.hasPermission name="user">

                        <li class="active"><a href="${request.contextPath}/user" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            用户管理</a></li>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="role">

                        <li class="active"><a href="${request.contextPath}/role" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            角色管理</a></li>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="resource">

                        <li class="active"><a href="${request.contextPath}/resource" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            资源管理</a></li>
                    </@shiro.hasPermission>


                </ul>
            </li>
          </@shiro.hasPermission>

<@shiro.hasPermission name="dbms_system">
            <li class=" treeview">
                <a href="#">
                    <i class="fa fa-dashboard"></i> <span>数管平台系统管理</span>
                    <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i></span>
                </a>
                <ul class="treeview-menu">

                    <@shiro.hasPermission name="user">

                        <li class="active"><a href="${request.contextPath}/dbms_user" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            用户管理</a></li>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="role">

                        <li class="active"><a href="${request.contextPath}/dbms_role" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            角色管理</a></li>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="resource">

                        <li class="active"><a href="${request.contextPath}/dbms_module" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            功能管理</a></li>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="resource">

                        <li class="active"><a href="${request.contextPath}/archiveCatalog" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            资源管理</a></li>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="resource">

                        <li class="active"><a href="${request.contextPath}/system_param" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            系统参数管理</a></li>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="resource">

                        <li class="active"><a href="${request.contextPath}/SatelliteInformation" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            卫星信息管理</a></li>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="resource">

                        <li class="active"><a href="${request.contextPath}/disk_information" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            磁盘信息管理</a></li>
                    </@shiro.hasPermission>
                	<!--
                	 <@shiro.hasPermission name="resource">

                        <li class="active"><a href="${request.contextPath}/dbms_module" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            数据管理</a></li>
                    </@shiro.hasPermission>
                	-->
                    

                </ul>
            </li>
        </@shiro.hasPermission>

        <@shiro.hasPermission name="uus_system">
            <li class=" treeview">
                <a href="#">
                    <i class="fa fa-dashboard"></i> <span>发布平台系统管理</span>
                    <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i></span>
                </a>
                <ul class="treeview-menu">

                    <@shiro.hasPermission name="uus_user">

                        <li class="active"><a href="${request.contextPath}/uus_user" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            用户管理</a></li>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="uus_role">

                        <li class="active"><a href="${request.contextPath}/uus_role" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            角色管理</a></li>
                    </@shiro.hasPermission>
                    <#--<@shiro.hasPermission name="uus_resource">

                        <li class="active"><a href="${request.contextPath}/uus_resource" target="view_frame" onclick="loadIframe($(this))"><i
                                class="fa fa-circle-o"></i>
                            资源管理</a></li>
                    </@shiro.hasPermission>-->
                </ul>
            </li>
        </@shiro.hasPermission>
         <@shiro.hasPermission name="dicCode">
            <li class="active" ><a href="${request.contextPath}/dicCode" target="view_frame" onclick="loadIframe($(this))">
                    <i class="fa fa-pie-chart"></i>
                    <span>字典管理</span>
                   <span class="pull-right-container"></span>
                </a>
            </li>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="logInfo">
            <li class="treeview">
                <a href="${request.contextPath}/logInfo/logInfo" target="view_frame" onclick="loadIframe($(this))">
                    <i class="fa fa-pie-chart"></i>
                    <span>系统日志</span>
                   <span class="pull-right-container">
            </span>
                </a>
            </li>
			</@shiro.hasPermission>	


            <@shiro.hasPermission name="systemConfig">
            <li class=" treeview">
                <a href="#">
                    <i class="fa fa-database"></i> <span>系统配置项</span>
                    <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
                </a>
                <ul class="treeview-menu">
                <@shiro.hasPermission name="ftpsConfig">
                    <li><a href="${request.contextPath}/systemConfig/ftpsConfig" target="view_frame" onclick="loadIframe($(this))"><i
                            class="fa fa-circle-o"></i>
                        ftp信息管理</a></li>
                 </@shiro.hasPermission>
                </ul>
            </li>
            </@shiro.hasPermission>



        </ul>

        </section>
        <!-- /.sidebar -->
    </aside>
    </ul>
    </li>
    <div class="content-wrapper">
        <iframe src="${request.contextPath}/main" frameborder="0" onload="changeFrameHeight()"
                id="view_frame" name="view_frame" width="100%" height="100%"></iframe>
    </div>
</div>
<script src="${request.contextPath}/static/adminlte/plugins/jQuery/jquery-2.2.3.min.js"></script>
<@netCommon.commonScript />
<script>
    var that;
    $(".main-sidebar a").click(function () {
        $(that).removeClass("bgcolor")
        $(this).addClass("bgcolor")
        that = this;
    });
    function changeFrameHeight() {
        var ifm = document.getElementById("view_frame");
        ifm.height = document.documentElement.clientHeight;

    }

    window.onresize = function () {
        changeFrameHeight();

    }
    function loadIframe(obj) {
        //获取url链接
        var url="${request.contextPath}"+obj.attr("href");
        var u = window.location.href;
        //因为每次获取的链接中都有之前的旧锚点，
        //所以需要把#之后的旧锚点去掉再来加新的锚点（即传入的url参数）
        var end = u.indexOf("#");
        var rurl = u.substring(0,end);
        //设置新的锚点
        window.location.href = rurl + "#" + url;
        onhashchange();
    }
    function onhashchange() {
        //location.hash取到的是url链接中#(包括自己)后面的内容
        var hash = location.hash;
        //去掉#号，得到的就是我们要设置到iframe中src上的地址
        var url = hash.substring(1,hash.length);
        $("#view_frame").attr("src", url);
    }
    document.addEventListener('DOMContentLoaded', function () {
        var hash = location.hash;
        var url = hash.substring(1,hash.length);
        if(url==""){
            url="/main";
        }
        $("#view_frame").attr("src", url);
    }, false);
</script>


</body>
</html>
