<!DOCTYPE html>
<html>
	<#import "/common/common.macro.ftl" as netmacro>
	<#import "/common/common.js.ftl" as netCommon>
<@netCommon.commonStyle />
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if>">
	<section class="content-header">
			<h1>FTP信息<small>FT管理</small></h1>
	</section>
<div class="panel panel-default">  
    <div class="panel-body">  
        <div class="list-group" role="group" aria-label="好友列表">  
        <button type="button" class="btn btn-success" data-toggle="modal" data-target="#exampleModal" >新增FTP信息</button><hr/>
    	<input type="text" class="form-control" placeholder="输入FTP名称查询">
<table class="table table-hover">
  <thead>
    <tr>
      <th>名称</th>
      <th>账号</th>
      <th>密码</th>
      <th>操作</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>产品1</td>
      <td>23/11/2013</td>
      <td>待发货</td>
      <td><a href="#">修改 </a></td>
    </tr>
      <tr>
      <td>产品1</td>
      <td>23/11/2013</td>
      <td>待发货</td>
      <td><a href="#">修改 </a></td>
    </tr>
  </tbody>
</table>
        </div>  
    </div>
 <div class="container">
  <ul class="pager">
    <li><a href="#">上一页</a></li>
    <li><a href="#">下一页</a></li>
  </ul>
</div>  
</div>  
<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">  
    <div class="modal-dialog" role="document">  
        <div class="modal-content">  
            <div class="modal-header">  
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span  
                        aria-hidden="true">×</span></button>  
                <h4 class="modal-title" id="exampleModalLabel">New message</h4>  
            </div>  
            <div class="modal-body">  
                <form>  
                    <div class="form-group">  
                        <label for="recipient-name" class="control-label">名称:</label>  
                        <input type="text" class="form-control" id="recipient-name">  
                    </div>  
                    <div class="form-group">  
                        <label for="message-text" class="control-label">账号:</label>  
                           <input type="text" class="form-control" id="recipient-name">  
                    </div>  
                    <div class="form-group">  
                        <label for="message-text" class="control-label">密码:</label>  
                        <input type="text" class="form-control" id="recipient-name">  
                    </div>  
                </form>  
            </div>  
            <div class="modal-footer">  
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>  
                <button type="button" class="btn btn-primary">添加</button>  
            </div>  
        </div>  
    </div>  
</div>  

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<#-- jquery.validate -->
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/js/jobgroup.index.1.js"></script>
<script>
	  $('#exampleModal').on('show.bs.modal', function (event) {  
        var button = $(event.relatedTarget) // 触发事件的按钮  
        var recipient = button.data('whatever') // 解析出data-whatever内容  
        var modal = $(this)  
        modal.find('.modal-title').text('新增FTP信息')  
        modal.find('.modal-body input').val(recipient)  
      })  
</script>
</body>
</html>
