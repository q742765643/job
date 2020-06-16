var parameterTable;
var migrateId = "";
  $(function() {
	  //webuploader上传组件
	    var $ = jQuery,
        $list = $('.thelist111'),
        state = 'pending',
        uploader;
		 //赋值算法标签顺序
		  var aaa = $("span[name='add']")
		  for(var i =0;i<aaa.length;i++){
			  aaa[i].innerHTML=(i+1);
		  }
		  var bbb = $("span[name='update']")
		  for(var i = 0;i<bbb.length;i++){
			  bbb[i].innerHTML=(i+1);
		  }
	   	//初始化Web Uploader
	   uploader = WebUploader.create({
		    // 选完文件后，是否自动上传。
		    auto: true,
	        // 不压缩image
	        resize: false,

	        // swf文件路径
	        swf: '/static/plugins/webuploader-0.1.5/Uploader.swf',

	        // 文件接收服务端。
	        server: base_url+'/processmodel/uploadalgo',

	        // 选择文件的按钮。可选。
	        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
	        pick: '.picker111',
	        	
	        accept: {// 只允许选择zip文件格式
	        	 title: 'ZIP',
	        	    extensions: 'zip',
	        	    mimeTypes: 'application/zip'
            },
            //重复上传
            duplicate:true
	  });
	   
	   //当有文件被添加进队列的时候
	   uploader.on( 'fileQueued', function( file ) {
	       $("div[name='fileListdiv']").remove();
		   $list.append( '<div id="' + file.id + '" name="fileListdiv" class="item '+file.id+'">' +
	           '<h4 class="info">' + file.name + '</h4>' +
	           '<p class="state">等待上传...</p>' +
	       '</div>' );
	   });
	   
	    // 文件上传过程中创建进度条实时显示。
	    uploader.on( 'uploadProgress', function( file, percentage ) {
	        var $li = $( '.'+file.id ),
	            $percent = $li.find('.progress .progress-bar');

	        // 避免重复创建
	        if ( !$percent.length ) {
	            $percent = $('<div class="progress progress-striped active">' +
	              '<div class="progress-bar" role="progressbar" style="width: 0%">' +
	              '</div>' +
	            '</div>').appendTo( $li ).find('.progress-bar');
	        }

	        $li.find('p.state').html("<p style='color:green;' >上传中...</p>");

	        $percent.css( 'width', percentage * 100 + '%' );
	    });

	    uploader.on( 'uploadSuccess', function( file,response ) {
	    	//上传完成
	    	$( '.'+file.id ).find('p.state').html("<p style='color:green;' >正在校验算法包...</p>");
	    	if(response.code == 200){
	    		var idd = $("#updateModal .form input[name='id']").val();
	    		//进行压缩包校验
	    		$.get(base_url+'/processmodel/uploadAlgoCheck', { algoUploadPath : response.algoZipPath , algoZipName : response.algoZipName  },
	    				  function(data){
	    				    if(data.code == 200){
	    				    	//解析xml.入库操作
	    				    	$.get(base_url+"/processmodel/parseLoading",{algoUploadPath : response.algoZipPath,algoZipName : response.algoZipName},
	    				    	     function(data){
	    				    			$( '.'+file.id ).find('p.state').html("<p style='color:green;' >已上传</p>");
	    				    			if("" == idd){
	    				    				//新增模态框模态框同步显示模型参数
	    				    				$('#addModal .form select[name=algoType] option[value='+ data.algoType+']').prop('selected', true);
	    				    				//自动添加显示算法包中xml的算法标识
	    				    				if("" != data.modelIdentify && null != data.modelIdentify){
	    				    					//移出重复option
	    				    					$('#addModal .form select[name=modelIdentify] option').each(function(){
	    				    						if($(this).val() == data.modelIdentify){
	    				    							$(this).remove();
	    				    							}
	    				    					});
	    				    					$('#addModal .form select[name=modelIdentify]').append('<option value='+data.modelIdentify+'>'+data.modelIdentify+'</option>');
	    				    					//选中添加option
	    				    					$('.selectpicker').selectpicker('val',data.modelIdentify);
	    				    					$('.selectpicker').selectpicker('val',[data.modelIdentify,'relish']);
	    				    					//刷新option列表
	    				    					$('.selectpicker').selectpicker('refresh');
	    				    				}
	    				    				//新增模态框同步显示固定参数,输入参数
	    				    				var fixedParameter = JSON.parse(data.fixedParameter);
	    				    				var dynamicParameter = JSON.parse(data.dynamicParameter);
	    				    				reportTableFix1("reportTable1", fixedParameter);
	    				    				reportTableDyn("reportTable2", dynamicParameter);
	    				    				$("#addModal .form input[name='algoZipName']").val(response.algoZipName);
	    				    				$("#addModal .form input[name='algoUploadPath']").val(response.algoZipPath);
	    				    			}else{
	    				    				//修改模态框模态框同步显示模型参数
	    				    				$('#updateModal .form select[name=algoType] option[value='+ data.algoType+']').prop('selected', true);
	    				    				if("" != data.modelIdentify && null != data.modelIdentify){
	    				    					//移出重复option
	    				    					$('#updateModal .form select[name=modelIdentify] option').each(function(){
	    				    						if($(this).val() == data.modelIdentify){
	    				    							$(this).remove();
	    				    							}
	    				    					});
	    				    					$('#updateModal .form select[name=modelIdentify]').append('<option value='+data.modelIdentify+'>'+data.modelIdentify+'</option>');
	    				    					//选中添加option
	    				    					$('.selectpicker').selectpicker('val',data.modelIdentify);
	    				    					$('.selectpicker').selectpicker('val',[data.modelIdentify,'relish']);
	    				    					//刷新option列表
	    				    					$('.selectpicker').selectpicker('refresh');
	    				    				}
	    				    				//修改模态框同步显示固定参数,输入参数
	    				    				var fixedParameter = JSON.parse(data.fixedParameter);
	    				    				var dynamicParameter = JSON.parse(data.dynamicParameter);
	    				    				reportTableFix1("reportTable3", fixedParameter);
	    				    				reportTableDyn("reportTable4", dynamicParameter);
	    				    				$("#addModal .form input[name='algoZipName']").val(response.algoZipName);
	    				    				$("#addModal .form input[name='algoUploadPath']").val(response.algoZipPath);
	    				    			}
	    				    	});
	    				    }else{
	    				    	$( '.'+file.id ).find('p.state').html("<p style='color:red;' >"+data.msg+"</p>");
	    				    }
	    	    });
	    	}else{
	    		$( '.'+file.id ).find('p.state').html("<p style='color:red;' >上传成功,但系统内部异常</p>");
	    	}
	    });

	    uploader.on( 'uploadError', function( file ) {
	        $( '.'+file.id ).find('p.state').text('上传内部出错');
	    });

	    uploader.on( 'uploadComplete', function( file ) {
	        $( '.'+file.id ).find('.progress').fadeOut();
	    });	    
	  
  	$.ajax({
  		type: "post",
  		data:{"treeKey":"processmodel"},
  		dataType: "json",
  		url: base_url+"/product/getTreeNode",
  		success: function(defaultData) {
			defaultData[0].state = {
				checked:true,
				expanded:true,
				selected:true
			}
			$('#treeId_find').val(defaultData[0].id);
			parameterTable.fnDraw();
  			$('#treeview5').treeview({
  				color: "#428bca",
//  				expandIcon: 'glyphicon glyphicon-chevron-right',
//  				collapseIcon: 'glyphicon glyphicon-chevron-down',
//  				nodeIcon: 'glyphicon glyphicon-bookmark',
  				emptyIcon: '',
  				// 没有子节点的节点图标
  				levels: 1,
  				data: defaultData,
				onNodeSelected: function (event, data) {
					 $('#treeId_find').val(data.id);
                     parameterTable.fnDraw();
					 //$('#treeId_find').val("");
				 }
  			});
  		}
  	});


	  function getparent(nodeId,parentList){
		  var arr = $('#treeview5').treeview("getParent", nodeId);
		  var a=[arr.text,arr.id];
		  if(arr.nodeId!=undefined){
			  parentList.push(a);
			  return getparent(arr.nodeId,parentList);

		  }else{
			  return parentList;
		  }

	  }

	  $("#treeAdd").click(function() {

		  var parentList = new Array();
		  var arr = $('#treeview5').treeview('getSelected');
		  if(arr.length>0){
			  getparent(arr[0].nodeId, parentList);
			  var menu = "";
			  var menuId = "";
			  for (var i = parentList.length - 1; i >= 0; i--) {
				  menu += parentList[i][0] + ">";
				  menuId += parentList[i][1] + ",";
			  }
			  $("#addModal1 .form input[name='parentId']").val("0");
			  $("#addModal1 .form input[name='menu']").val(menu + arr[0].text);
			  $("#addModal1 .form input[name='menuId']").val(menuId + arr[0].id);
		  }else{
			  $("#addModal1 .form input[name='parentId']").val("0");
			  $("#addModal1 .form input[name='menu']").val("");
		  }


		  // show
		  $('#addModal1').modal({
			  backdrop: false,
			  keyboard: false
		  }).modal('show');



	  });
	  $("#treeUpdate").click(function() {
		  var parentList = new Array();

		  var arr = $('#treeview5').treeview('getSelected');
		  if (arr.length ==0){
			  layer.open({
				  title: '系统提示',
				  content: ("请选择目录"),
				  icon: '2'
			  });
			  return;
		  }
		  getparent(arr[0].nodeId, parentList);
		  var menu = "";
		  var menuId = "";
		  for (var i = parentList.length - 1; i >= 0; i--) {
			  menu += parentList[i][0] + ">";
			  menuId += parentList[i][1] + ",";
		  }
		  // base data
		  $("#updateModal1 .form input[name='id']").val(arr[0].id);
		  $("#updateModal1 .form input[name='text']").val(arr[0].text);
		  $("#updateModal1 .form input[name='parentId']").val("0");
		  $("#updateModal1 .form input[name='menu']").val(menu+arr[0].text);
		  $("#updateModal1 .form input[name='menuId']").val(menuId+arr[0].id);
		  /*var iconPath = "";
		  if("undefined" != menuId+arr[0].iconPath){
			  iconPath =  menuId+arr[0].iconPath;
		  }
		  $("#updateModal .form select[name='iconPath']").find("option:selected").val();*/
		  // show
		  $('#updateModal1').modal({
			  backdrop: false,
			  keyboard: false
		  }).modal('show');
	  });

	  $("#treeDel").click(function() {
		  if($('#treeview5').treeview('getSelected').length>0){
			  layer.confirm('确认删除?', {icon: 3, title:'系统提示'}, function(index){
				  layer.close(index);
				  var arr = $('#treeview5').treeview('getSelected');
				  var data = {
					  "id": arr[0].id
				  };
				  $.ajax({
					  type: "post",
					  data: data,
					  dataType: "json",
					  url: base_url+"/product/deleteTreeNode",
					  success: function(data) {
						  if (data.code == "200") {
							  layer.msg('操作成功', {
								  icon: 1,
								  time: 2000 //2秒关闭（如果不配置，默认是3秒）
							  }, function(){
								  window.location.href = base_url+ "/processmodel";
							  });
						  } else {
							  layer.msg(data.msg || "操作失败", {
								  icon: 2,
								  time: 2000 //2秒关闭（如果不配置，默认是3秒）
							  });

						  }
					  }
				  });

			  });
		  }else{
			  layer.msg("请选择将要删除节点", {
				  icon: 2,
				  time: 2000 //2秒关闭（如果不配置，默认是3秒）
			  });
		  }




	  });
	  /* $("#treeDel").click(function() {
		  var  selectedArr = $('#treeview5').treeview('getSelected');
		   if(selectedArr.length>0){
			   layer.confirm('确认删除?', {icon: 3, title:'系统提示'}, function(index){
				   layer.close(index);
				   var arr = $('#treeview5').treeview('getSelected');
				   var data = {
					   "id": arr[0].id
				   };
				    $.ajax({
						type: "post",
						data: data,
						dataType: "json",
						url: base_url+"/product/deleteTreeNode",
						success: function(data) {
							if (data.code == "200") {
								layer.msg('操作成功', {
								icon: 1,
								time: 2000 //2秒关闭（如果不配置，默认是3秒）
							}, function(){
								window.location.href = base_url + "/processmeta";
							});} else {
					layer.msg(data.msg || "操作失败", {
					icon: 2,
					time: 2000 //2秒关闭（如果不配置，默认是3秒）
					});
					}
					}
					});
			   });
		   }else{
			   layer.open({
				   title: '系统提示',
				   content: ("请选择目录"),
				   icon: '2'
			   });
			   return;
		   }
	  });*/
	  $("#saveAtomSubmit").click(function() {
	  });
	  $("#updateAtomSubmit").click(function() {
	  });

	  var addModalValidate = $("#addModal1 .form").validate({
		  errorElement : 'span',
		  errorClass : 'help-block',
		  focusInvalid : true,
		  rules : {
			  text : {
				  required : true
			  }
		  },
		  messages : {
			  text : {
				  required :"请输入目录名称！"
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
			  $.post(base_url + "/product/save",  $("#addModal1 .form").serialize(), function(data, status) {
				  if (data.code == "200") {
					  $('#addModal1').modal('hide');
					  layer.msg('新增成功', {
						  icon: 1,
						  time: 1000 //2秒关闭（如果不配置，默认是3秒）
					  }, function(){
						  $.ajax({
							  type: "post",
							  data:{"treeKey":"processmodel"},
							  dataType: "json",
							  url: base_url+"/product/getTreeNode",
							  success: function(defaultData) {
								  for(var i=0;i<defaultData.length;i++){
									  if(defaultData[i].text == $('#treeview5').treeview('getSelected')[0].text){
										  defaultData[i].state = {
											  checked:true,
											  expanded:true,
											  selected:true
										  }
									  }
								  }
								  $('#treeview5').treeview({
									  color: "#428bca",
									  emptyIcon: '',
									  levels: 1,
									  data: defaultData,
									  onNodeSelected: function (event, data) {
										  $('#treeId_find').val(data.id);
										  parameterTable.fnDraw();
									  }
								  })
							  }
						  });
					  });
				  } else {
					  layer.msg(data.msg || "新增失败", {
						  icon: 2,
						  time: 1000 //2秒关闭（如果不配置，默认是3秒）
					  });
				  }


			  });
		  }
	  });
	  var updateModalValidate = $("#updateModal1 .form").validate({
		  errorElement : 'span',
		  errorClass : 'help-block',
		  focusInvalid : true,
		  rules : {
			  text : {
				  required : true
			  }
		  },
		  messages : {
			  text : {
				  required :"请输入目录名称！"
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
			  $.post(base_url + "/product/save",  $("#updateModal1 .form").serialize(), function(data, status) {
				  if (data.code == "200") {
					  $('#updateModal1').modal('hide');
					  layer.msg('更新成功', {
						  icon: 1,
						  time: 1000 //2秒关闭（如果不配置，默认是3秒）
					  }, function(){
						  $.ajax({
							  type: "post",
							  data:{"treeKey":"processmodel"},
							  dataType: "json",
							  url: base_url+"/product/getTreeNode",
							  success: function(defaultData) {
								  for(var i=0;i<defaultData.length;i++){
									  if(defaultData[i].id== $('#treeview5').treeview('getSelected')[0].id){
										  defaultData[i].state = {
											  checked:true,
											  expanded:true,
											  selected:true
										  }
									  }
								  }
								  $('#treeview5').treeview({
									  color: "#428bca",
									  emptyIcon: '',
									  levels: 1,
									  data: defaultData,
									  onNodeSelected: function (event, data) {
										  $('#treeId_find').val(data.id);
										  parameterTable.fnDraw();
									  }
								  });

							  }
						  });
					  });
				  } else {
					  layer.msg(data.msg || "更新失败", {
						  icon: 2,
						  time: 1000 //2秒关闭（如果不配置，默认是3秒）
					  });
				  }


			  });
		  }
	  });


 $("#marginSure").click(function () {
	 $('#margin').modal('hide');
	 if($('#treeview3').treeview('getSelected').length>0){
		 $.ajax({
			 type: "post",
			 data:{"id":migrateId,"treeId":$('#treeview3').treeview('getSelected')[0].id},
			 dataType: "json",
			 url: base_url+"/processmodel/updateForTree",
			 success: function(defaultData) {
				 if (defaultData.code == 200) {

					 layer.msg( "迁移成功", {
						 icon: 1,
						 time: 2000 //2秒关闭（如果不配置，默认是3秒）
					 }, function(layero, index){
						 $.ajax({
							 type: "post",
							 data:{"treeKey":"processmodel"},
							 dataType: "json",
							 url: base_url+"/product/getTreeNode",
							 success: function(defaultData) {
								 for(var i=0;i<defaultData.length;i++){
									 if(defaultData[i].id== $('#treeview5').treeview('getSelected')[0].id){
										 defaultData[i].state = {
											 checked:true,
											 expanded:true,
											 selected:true
										 }
										 $('#treeId_find').val(defaultData[i].id);
									 }
								 }
								 parameterTable.fnDraw();
								 $('#treeview5').treeview({
									 color: "#428bca",
									 emptyIcon: '',
									 levels: 1,
									 data: defaultData,
									 onNodeSelected: function (event, data) {
										 $('#treeId_find').val(data.id);
										 parameterTable.fnDraw();
									 }
								 });
							 }
						 });
					 });
				 } else {
					 layer.msg("迁移失败", {
						 icon: 2,
						 time: 2000 //2秒关闭（如果不配置，默认是3秒）
					 });
				 }
			 }
		 });
	 }else{
		 layer.open({
			 title: '系统提示',
			 content: ("请选择迁移目录"),
			 icon: '2'
		 });
		 return;
	 }

 });

  		$("#add").click(function() {
  		//移出已上传div
  		$("div[name='fileListdiv']").remove();
		$("#addModal .form input[name='algoZipName']").val("");
		$("#addModal .form input[name='algoUploadPath']").val("");
  		$("#updateModal .form input[name='id']").val("");
  		
  		var arr = $('#treeview5').treeview('getSelected');
  		if (arr.length == 0) {
  			layer.alert('请选择算法分类', {icon: 2});
  			return;
  		}
        $("#addModal .form input[name='treeId']").val(arr[0].id);
  		// show
  		$('#addModal').modal({
  			backdrop: false,
  			keyboard: false
  		}).modal('show');
  		reportTableFix("reportTable1", "");
  		reportTable("reportTable2", "");
  	//	reportTable("add_reportTable3", "");


  	});


      if($("#productmodeluploadpath")[0]== null || $("#productmodeluploadpath")[0] == "undefined"){
    	  parameterTable = $("#parameterTable").dataTable({
    	  		"deferRender": true,
    	  		"processing": true,
    	  		"serverSide": true,
			  	/*"bStateSave":true,*/
    	  		"aaSorting": [
    	  			[1, "asc"]
    	  		],
    	  		"ajax": {
    	  			url: base_url+"/processmodel/pageList",
    	  			type: "post",
    	  			data: function(d) {
    	  				var obj = {};
    	  				obj.start = d.start;
    	  				obj.length = d.length;
    	                obj.modelName=$("#modelName").val();
    	                obj.modelIdentify=$("#modelIdentify").val();
    	                obj.treeId=$("#treeId_find").val();
    	                return obj;
    	  			}
    	  		},
    	  		"searching": false,
    	  		"ordering": false,
    	  		// "scrollX": true, // X轴滚动条，取消自适应
    	  		"fnDrawCallback": function() {
    	  			this.api().column(0).nodes().each(function(cell, i) {
    	  				cell.innerHTML = i + 1;
    	  			});
    	  		},
    	  		"columns": [{
    	  			"data": null
    	  		}, {
    	  			"data": 'id',
    	  			"visible": false 
    	  		}, {
    	  			"data": 'modelName',
    	  			"visible": true
    	  		}, {
    	  			"data": 'modelIdentify',
    	  			"visible": true
    	  		}, {
    	  			"data": '操作',
    	  			"render": function(data, type, row) {
    	  				tableData['key' + row.id] = row;
    	  				return function() {
    	  					return getbutton(row);
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
      }else{
    	  parameterTable = $("#parameterTable").dataTable({
    	  		"deferRender": true,
    	  		"processing": true,
    	  		"serverSide": true,
			 /* "bStateSave":true,*/
    	  		"aaSorting": [
    	  			[1, "asc"]
    	  		],
    	  		"ajax": {
    	  			url: base_url+"/processmodel/pageList",
    	  			type: "post",
    	  			data: function(d) {
    	  				var obj = {};
    	  				obj.start = d.start;
    	  				obj.length = d.length;
    	                obj.modelName=$("#modelName").val();
    	                obj.modelIdentify=$("#modelIdentify").val();
    	                obj.treeId=$("#treeId_find").val();
    	                return obj;
    	  			}
    	  		},
    	  		"searching": false,
    	  		"ordering": false,
    	  		// "scrollX": true, // X轴滚动条，取消自适应
    	  		"fnDrawCallback": function() {
    	  			this.api().column(0).nodes().each(function(cell, i) {
    	  				cell.innerHTML = i + 1;
    	  			});
    	  		},
    	  		"columns": [{
    	  			"data": null
    	  		}, {
    	  			"data": 'id',
    	  			"visible": false 
    	  		}, {
    	  			"data": 'modelName',
    	  			"visible": true
    	  		}, {
    	  			"data": 'modelIdentify',
    	  			"visible": true
    	  		}, {
    	  			"data": 'algoPath',
    	  			"visible": true
    	  		},{
    	  			"data": '操作',
    	  			"render": function(data, type, row) {
    	  				tableData['key' + row.id] = row;
    	  				return function() {
    	  					return getbutton(row);
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
      }


	  
 	var 	tableData = {};

  	$("#reportTable1Submit").click(function() {
  		var fixedParameter = $('#reportTable1').bootstrapTable('getData');
  		var dynamicParameter = $('#reportTable2').bootstrapTable('getData');
        //获取部署选择节点
    	var checkboxes = document.getElementsByName("deployNode");
    	var ids = new Array();
    	for ( var i = 0; i < checkboxes.length; i++) {
    		if (checkboxes[i].value != "" && checkboxes[i].checked) {
    			ids[ids.length] = checkboxes[i].value;
    		}
    	}
        
       var data = {
        		"modelName": $("#addModal .form input[name='modelName']").val(),
        		"treeId": $("#addModal .form input[name='treeId']").val(),
        		"modelIdentify": $("#addModal .form select[name=modelIdentify]").val(),
        		"url": $("#addModal .form input[name='url']").val(),
        		"type": $("#addModal .form select[name=type]").val(),
        		"algoType": $("#addModal .form select[name=algoType]").val(),
        		"executorBlockStrategy": $("#addModal .form select[name=executorBlockStrategy]").val(),
        		"fixedParameter": JSON.stringify(fixedParameter),
        		"dynamicParameter": JSON.stringify(dynamicParameter),
        	//	"outputParameters": JSON.stringify(outputParameters),
        		"nodeList":ids,
                "dealAmount":$("#addModal .form input[name='dealAmount']").val(),
                "algoPath":$("#addModal .form input[name='algoUploadPath']").val(),
                "algoZipName":$("#addModal .form input[name='algoZipName']").val()
       }
      data = JSON.stringify(data);
        var result_msg = "";
       //等待部署提示
		layer.msg( "正在注册部署中,请稍候...", {
			icon: 1,
			time: 1000000 //2秒关闭（如果不配置，默认是3秒）
		}, function(layero, index){
			
		});
	     	
     	$.ajax({
      		type: "post",
      		data:data,
      		dataType: "json",
      		contentType:"application/json",
      		url: base_url+"/processmodel/save",
      		success: function(data) {
				if (401 <= data.code) {
				//1(data.msg);
				layer.open({
					  title: '添加算法出错'
					  ,content: data.msg
				}); 
			} else {
				$('#addModal').modal('hide');
				layer.msg( "新增成功", {
					icon: 1,
					time: 2000 //2秒关闭（如果不配置，默认是3秒）
				}, function(layero, index){
					parameterTable.fnDraw();
				});
			}
      		
      		}
      	});
			
  	});

  	$("#reportTableUpdateSubmit").click(function() {
  		var fixedParameter = $('#reportTable3').bootstrapTable('getData');
  		var dynamicParameter = $('#reportTable4').bootstrapTable('getData');
  		//var outputParameters = $('#update_reportTable4').bootstrapTable('getData');
        //获取部署选择节点
    	var checkboxes = document.getElementsByName("uploadDeployNode");
    	var ids = new Array();
    	for ( var i = 0; i < checkboxes.length; i++) {
    		if (checkboxes[i].value != "" && checkboxes[i].checked) {
    			ids[ids.length] = checkboxes[i].value;
    		}
    	}

  		var data = {
  			"modelName": $("#updateModal .form input[name='modelName']").val(),
  			"id": $("#updateModal .form input[name='id']").val(),
  			"modelIdentify": $("#updateModal .form select[name=modelIdentify]").val(),
            "url": $("#updateModal .form input[name='url']").val(),
            "type": $("#updateModal .form select[name=type]").val(),
            "algoType": $("#updateModal .form select[name=algoType]").val(),
            "executorBlockStrategy": $("#updateModal .form select[name=executorBlockStrategy]").val(),
  			"fixedParameter": JSON.stringify(fixedParameter),
  			"dynamicParameter": JSON.stringify(dynamicParameter),
  			//"outputParameters": JSON.stringify(outputParameters),
  			"nodeList":ids,
            "dealAmount":$("#updateModal .form input[name='dealAmount']").val(),
            "algoPath":$("#addModal .form input[name='algoUploadPath']").val(),
            "algoZipName":$("#addModal .form input[name='algoZipName']").val()
  		}
  		
        //等待部署提示
 		layer.msg( "正在注册部署中,请稍候...", {
 			icon: 1,
 			time: 1000000 //2秒关闭（如果不配置，默认是3秒）
 		}, function(layero, index1){
 			
 		});
  		data = JSON.stringify(data);        
     	$.ajax({
      		type: "post",
      		data:data,
      		dataType: "json",
      		contentType:"application/json",
      		url: base_url+"/processmodel/update",
      		success: function(data) {
              if (401 <= data.code) {
				layer.open({
					  title: '修改算法出错'
					  ,content: data.msg
				}); 
            } else {
            	$('#updateModal').modal('hide');
            	layer.msg( "修改成功", {
					icon: 1,
					time: 2000 //2秒关闭（如果不配置，默认是3秒）
				}, function(layero, index){
					parameterTable.dataTable().fnDraw(false)
					//parameterTable.fnDraw();
				});
            }
      		}
      	});
  		
  	});


  	$("#parameterTable").on('click', '.update', function() {
  		//移出已上传div
  		$("div[name='fileListdiv']").remove();
		$("#addModal .form input[name='algoZipName']").val("");
		$("#addModal .form input[name='algoUploadPath']").val("");
  		
  		var id = $(this).parent('p').attr("id");
  		var row = tableData['key' + id];
  		if (!row) {
  			layer.open({
  				title: '系统提示',
  				content: ("模型信息加载失败，请刷新页面"),
  				icon: '2'
  			});
  			return;
  		}
  		reportTableFix("reportTable3", base_url + "/jobinfo/getJobParameter?jobId=&parameterId=" + row.id + "&mark=1");
  		reportTable("reportTable4", base_url + "/jobinfo/getJobParameter?jobId=&parameterId=" + row.id + "&mark=2");
  		//reportTable("update_reportTable4", base_url + "/jobinfo/getJobParameter?jobId=&parameterId=" + row.id + "&mark=3");

  		// base data
  		$("#updateModal .form input[name='id']").val(row.id);
  		$("#updateModal .form input[name='modelName']").val(row.modelName);
  		//$("#updateModal .form input[name='modelIdentify']").val(row.modelIdentify);
  		var boolean = true;
  		$("#updateModal .form select[name=modelIdentify] option").each(function(){
  			if($(this).val() == row.modelIdentify ){
  				boolean = false;
  			}
  		});
  		if(boolean){
  			$('#updateModal .form select[name=modelIdentify]').append('<option value='+row.modelIdentify+'>'+row.modelIdentify+'</option>');
			//选中添加option
			$('#updateModal .form select[name=modelIdentify]').selectpicker('val',row.modelIdentify);
			$('.selectpicker').selectpicker('val',[row.modelIdentify,'relish']);
			//刷新option列表
			$('.selectpicker').selectpicker('refresh');
  		}else{
  			$("#updateModal .form select[name=modelIdentify]").selectpicker('val',row.modelIdentify);
  		}
  		
        $("#updateModal .form input[name='url']").val(row.url);
        if("" != row.type){
        	$('#updateModal .form select[name=type] option[value='+ row.type +']').prop('selected', true);
        }
        if("" != row.algoType){
        	$('#updateModal .form select[name=algoType] option[value='+ row.algoType+']').prop('selected', true);
        }
        $("#updateModal .form input[name='dealAmount']").val(row.dealAmount);
        $('#updateModal .form select[name=executorBlockStrategy] option[value='+ row.executorBlockStrategy +']').prop('selected', true);
        
         $.ajax({
             type: "post",
             data: {"algoId":row.id},
             dataType: "json",
             url: base_url+"/processmodel/getRegistListByAlgoId",
             success: function(data) {
            	 //清空上传文件输入框
            	 $("#again_UploadFile").val("");
            	 //清空节点
            	 $("input[name='uploadDeployNode']").prop("checked",false);
                 if (data.length > 0) {
                	 // show
                	 $('#updateModal').modal({
                		 backdrop: false,
                		 keyboard: false
                	 }).modal('show');
                	 
                	 //将节点和算法对应页面绑定
                	 for(var i= 0;i<data.length;i++){  
                		 $("#"+data[i].registryId).prop("checked",true);
                	 }
                	 
                 } else {
                	 // show
                	 $('#updateModal').modal({
                		 backdrop: false,
                		 keyboard: false
                	 }).modal('show');
                	 //清空节点
                	 $("input[name='uploadDeployNode']").prop("checked",false);
                 }
             }
         });
  	});

      $('#searchBtn').on('click', function(){
          parameterTable.fnDraw();
      });
      
      $("input[name=againUpload]").click(function(){
    	  switch($("input[name=againUpload]:checked").attr("id")){
    	  case "againUpload1":
    	 //   alert("click camera1");
    	    $("#againUploadDiv").show();
    	  break;
    	  case "againUpload0":
    	  //  alert("click camera2");
    	    $("#againUploadDiv").hide();
    	    $("#again_UploadFile").val("");
    	  break;
    	    default:
    	  break;
    	  }
      });


  });

  function deleteParameter(obj) {
		  layer.confirm('确认删除?', {icon: 3, title:'系统提示'}, function(index){
			  layer.close(index);
			  var id = $(obj).parent('p').attr("id");
			  var data = {
				  "id": id
			  };
			  $.ajax({
				  type: "post",
				  data: data,
				  dataType: "json",
				  url: base_url+"/processmodel/deleteParameter",
				  success: function(data) {
					  if (data.code == 200) {

						  layer.msg( "删除成功", {
							  icon: 1,
							  time: 2000 //2秒关闭（如果不配置，默认是3秒）
						  }, function(layero, index){
							  parameterTable.fnDraw();
						  });
					  } else {
						  layer.msg(data.msg || "删除失败", {
							  icon: 2,
							  time: 2000 //2秒关闭（如果不配置，默认是3秒）
						  });
					  }
				  }
			  });

		  });
  }
  function migrateParameter(obj) {
	  if($('#treeview5').treeview('getSelected').length>0){
		  $('#margin').modal({
			  backdrop: false,
			  keyboard: false
		  }).modal('show');
		  migrateId = $(obj).parent('p').attr("id");
		  $.ajax({
			  type: "post",
			  data:{"treeKey":"processmodel"},
			  dataType: "json",
			  url: base_url+"/product/getTreeNode",
			  success: function(defaultData) {
				  for(var i=0;i<defaultData.length;i++){
					  if(defaultData[i].text == $('#treeview5').treeview('getSelected')[0].text){
						  defaultData.splice(i,1)
					  }
				  }
				  $('#treeview3').treeview({
					  color: "#428bca",
					  emptyIcon: '',
					  levels: 1,
					  data: defaultData,
					  onNodeSelected: function (event, data) {
						  $('#treeId_find').val(data.id);
					  }
				  });

			  }
		  });
	  }else{
		  layer.open({
			  title: '系统提示',
			  content: ("请选择初始存在目录"),
			  icon: '2'
		  });
		  return;
	  }

	 /* layer.confirm('确认迁徙?', {icon: 3, title:'系统提示'}, function(index){
		  layer.close(index);
		  var id = $(obj).parent('p').attr("id");
		  var data = {
			  "id": id
		  };
		  $.ajax({
			  type: "post",
			  data: data,
			  dataType: "json",
			  url: base_url+"/processmodel/deleteParameter",
			  success: function(data) {
				  if (data.code == 200) {

					  layer.msg( "删除成功", {
						  icon: 1,
						  time: 2000 //2秒关闭（如果不配置，默认是3秒）
					  }, function(layero, index){
						  parameterTable.fnDraw();
					  });
				  } else {
					  layer.msg(data.msg || "删除失败", {
						  icon: 2,
						  time: 2000 //2秒关闭（如果不配置，默认是3秒）
					  });
				  }
			  }
		  });

	  });*/
  }
