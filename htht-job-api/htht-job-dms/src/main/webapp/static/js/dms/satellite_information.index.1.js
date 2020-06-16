  var meataImgAndInfo;
  $(function() {
	getTreeNode();
	
	// 过滤时间
	$('#filterTime').daterangepicker({
        autoApply:false,
        singleDatePicker:false,
        showDropdowns:false,        // 是否显示年月选择条件
		timePicker: true, 			// 是否显示小时和分钟选择条件
		timePickerIncrement: 10, 	// 时间的增量，单位为分钟
        timePicker24Hour : true,
        opens : 'left', //日期选择框的弹出位置
		ranges: {
			'最近1小时': [moment().subtract(1, 'hours'), moment()],
			'今日': [moment().startOf('day'), moment().endOf('day')],
			'昨日': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
			'最近7日': [moment().subtract(6, 'days'), moment()],
			'最近30日': [moment().subtract(29, 'days'), moment()],
			'本月': [moment().startOf('month'), moment().endOf('month')],
			'上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
		},
        locale : {
            format: 'YYYY-MM-DD HH:mm:ss',
            separator : ' - ',
        	customRangeLabel : '自定义',
            applyLabel : '确定',
            cancelLabel : '取消',
            fromLabel : '起始时间',
            toLabel : '结束时间',
            daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
            firstDay : 1,
            startDate: moment().startOf('day'),
            endDate: moment().endOf('day')
        }
	});
	
	//目录树增加
  	$("#treeAdd").click(function() {
        var parentList = new Array();

        var arr = $('#treeview5').treeview('getSelected');

        if (arr.length > 0){
            getparent(arr[0].nodeId, parentList);
			var text = "";
			//var menuId = "";
			for (var i = parentList.length - 1; i >= 0; i--) {
				text += parentList[i][0] + ">";
				//menuId += parentList[i][1] + ",";
			}
			$("#addModal .form input[name='pid']").val(arr[0].fid);
			$("#addModal .form input[name='catalogCode']").val(arr[0].catalogCode);
			$("#addModal .form input[name='text']").val(text + arr[0].text);
			//$("#addModal .form input[name='menuId']").val(menuId + arr[0].id);
        }else {
            $("#addModal .form input[name='pid']").val("0");

        }
        
        var yNodeType;
  		yNodeType=$("#addModal .form select[name='yNodeType']").find("option:selected").val();
  		if(yNodeType==0){
  			$("#mainTableNameD").show();
  		}else{
  			$("#mainTableNameD").hide();
  		}

  		// show
  		$('#addModal').modal({
  			backdrop: false,
  			keyboard: false
  		}).modal('show');



  	});
  	$("#addModal .form select[name=yNodeType]").change(function(){
  		var yNodeType;
  		yNodeType=$(this).children('option:selected').val();
  		if(yNodeType==0){
  			$("#mainTableNameD").show();
  		}else{
  			$("#mainTableNameD").hide();
  		}
  	});
  	//目录树修改
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
			var text = "";
			//var menuId = "";
			for (var i = parentList.length - 1; i >= 0; i--) {
				text += parentList[i][0] + ">";
				//menuId += parentList[i][1] + ",";
			}
          // base data
          $("#updateModal .form input[name='id']").val(arr[0].id);
          $("#updateModal .form input[name='fid']").val(arr[0].fid);
          $("#updateModal .form input[name='text']").val(text+arr[0].text);
          $("#updateModal .form input[name='catalogName']").val(arr[0].catalogName);
          //$("#updateModal .form input[name='mainTableName']").val(arr[0].mainTableName);
          
          $("#updateModal .form select[name='mainTableName']").find("option:selected").val();
          $("#updateModal .form input[name='subTableName']").val(arr[0].subTableName);
          $("#updateModal .form input[name='nodeDesc']").val(arr[0].nodeDesc);
          
          $('#updateModal .form select[name=yNodeType] option[value='+ arr[0].yNodeType +']').prop('selected', true);
          //$("#updateModal .form select[name=modelIdentify]").selectpicker('val',row.modelIdentify);
          
          //$("#updateModal .form select[name='nodeType']").find("option:selected").val();
          $("#updateModal .form input[name='xsDid']").val(arr[0].xsDid);
          $("#updateModal .form input[name='storingGrule']").val(arr[0].storingGrule);
          $("#updateModal .form input[name='method']").val(arr[0].method);
          $("#updateModal .form input[name='arcpathid']").val(arr[0].method);
          
          var yNodeType;
			yNodeType=$("#updateModal .form select[name='yNodeType']").find("option:selected").val();
			if(yNodeType==0){
				$("#mainTableNameU").show();
			}else{
				$("#mainTableNameU").hide();
			}
          
          // show
          $('#updateModal').modal({
              backdrop: false,
              keyboard: false
          }).modal('show');
      });
    $("#updateModal .form select[name=yNodeType]").change(function(){
  		var yNodeType;
  		yNodeType=$(this).children('option:selected').val();
  		if(yNodeType==0){
  			$("#mainTableNameU").show();
  		}else{
  			$("#mainTableNameU").hide();
  		}
  	});
    //目录树删除
    $("#treeDel").click(function() {
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
                url: base_url+"/archiveCatalog/deleteTreeNode",
                success: function(data) {
                    if (data.code == "200") {
    					layer.msg('操作成功', {
    						icon: 1,
    						time: 2000 //2秒关闭（如果不配置，默认是3秒）
    					}, function(){
    						//window.location.href = base_url + "/archiveCatalog";
    						 window.location.reload();
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



      });
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset();
		
	});
	$(".col-sm-9").bind("keydown",function(e){
	    var theEvent = e || window.event;    
	    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;    
	    if (code == 13) {    
	        //回车执行查询
	    	meataImgAndInfo.fnDraw();
	        }    
	});
	$('#searchBtn').on('click', function(){
		meataImgAndInfo.fnDraw();
	});
	//卫星信息展示
  	meataImgAndInfo = $("#meataImgAndInfo").dataTable({
  		"deferRender": true,
  		"processing": true,
  		"serverSide": true,
  		"aaSorting": [
  			[1, "asc"]
  		],
  		"ajax": {
  			url: base_url+"/SatelliteInformation/pageList",
  			type: "post",
  			data: function(d) {
  				var obj={};
  				var obj={
  				    "catalogcode":$('#catalogCode').val(),
  				    "fSatelliteid":$('#fSatelliteid').val(),
  				    "fProducetime" :$('#filterTime').val(),
  				    "fLevel":$('#fLevel').val(),
  				    "fCloudamount":$('#fCloudamount').val(),
  					"start":d.start,
  					"length":d.length
  				}
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
  		},{
  			"data": 'viewPath',
  			"visible": true,
  			"render": function (data, type, row) {
  				//console.log(data);
                return "<input type='image' style='width:50px;height=20px;' onclick='viewImage(this)' alt='暂无' src="+data+" />";
            }
  		},{
  			"data": 'metaImg.fSatelliteid',
  			"visible": true
  		},{
  			"data": 'metaImg.fSensorid',
  			"visible": true
  		}, {
  			"data": 'metaImg.fProducetime',
  			"visible": true,
  			"render": function (data, type, row) {
  				//console.log(data);
  				var fProducetime= timestampToTime(data);
                return fProducetime;
            }
  		}
  		,{
  			"data": '操作',
  			"render": function(data, type, row) {
  				tableData['key' + row.id] = row;
  				return function() {
  					return getDownloadButton(row)+getbutton(row);
//  					return getbutton(row);
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
  	var tableData = {};
  	//目录树新增保存
      var addModalValidate = $("#addModal .form").validate({
          errorElement : 'span',
          errorClass : 'help-block',
          focusInvalid : true,
          rules : {
        	  catalogName : {
                  required : true
              }
          },
          messages : {
        	  catalogName : {
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
        	  //alert($("#addModal .form").serialize());
              $.post(base_url + "/archiveCatalog/save",  $("#addModal .form").serialize(), function(data, status) {
                  if (data.code == "200") {
                      $('#addModal').modal('hide');
                      layer.msg('新增成功', {
  						icon: 1,
  						time: 1000 //2秒关闭（如果不配置，默认是3秒）
  					}, function(){
  						 meataImgAndInfo.fnDraw();
                         getTreeNode();
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

     //目录树修改保存
      var updateModalValidate = $("#updateModal .form").validate({
          errorElement : 'span',
          errorClass : 'help-block',
          focusInvalid : true,
          rules : {
        	  catalogName:{
        		  required: true
        	  }
          },
          messages : {
        	  catalogName : {
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
              $.post(base_url + "/archiveCatalog/save",  $("#updateModal .form").serialize(), function(data, status) {
                  if (data.code == "200") {
                      $('#updateModal').modal('hide');
                      layer.msg('更新成功', {
  						icon: 1,
  						time: 1000 //2秒关闭（如果不配置，默认是3秒）
  					}, function(){
  						meataImgAndInfo.fnDraw();
  						getTreeNode();
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
    //入库规则修改
  	$("#meataImgAndInfo").on('click', '.update', function() {
  		var id = $(this).parent('p').attr("id");
  		var row = tableData['key' + id];
  		if (!row) {
  			layer.msg(data.msg || "模型信息加载失败，请刷新页面", {
					icon: 2,
					time: 1000 //2秒关闭（如果不配置，默认是3秒）
				}); 
  			return;
  		}
  		
  		
  		reportTableA("reportTable2", base_url + "/archiveCatalog/getArchiveFiledMap?archiveRuleId=" + row.id);
  		var filetype1;
  		filetype1=row.filetype;
  		if(filetype1==0){
  			$("#finalstrD1").css('display','block'); 
  		}else{
  			$("#finalstrD1").css('display','none');
  		}
  		// base data
        $("#updateArchiveRulesModal .form input[name='id']").val(row.id);
        $("#updateArchiveRulesModal .form textarea[name='regexpstr']").val(row.regexpstr);
        $("#updateArchiveRulesModal .form input[name='catalogcode']").val(row.catalogcode);
        $("#updateArchiveRulesModal .form textarea[name='regexpxml']").val(row.regexpxml);
        $("#updateArchiveRulesModal .form textarea[name='regexpjpg']").val(row.regexpjpg);
        //$("#updateArchiveRulesModal .form input[name='archivdisk']").val(row.archivdisk);
        $("#updateArchiveRulesModal .form select[name=archivdisk]").selectpicker('val',row.archivdisk);
        $("#updateArchiveRulesModal .form input[name='finalstr']").val(row.finalstr);
        $("#updateArchiveRulesModal .form input[name='datalevel']").val(row.datalevel);
        $("#updateArchiveRulesModal .form input[name='wspFile']").val(row.wspFile);
        $("#updateArchiveRulesModal .form input[name='allFile']").val(row.allFile);
        
		$('#updateArchiveRulesModal .form select[name=rulestatus] option[value='+ row.rulestatus +']').prop('selected', true);
		
		$("#updateArchiveRulesModal .form select[name=flowid]").selectpicker('val',row.flowid);
		
		//$('#updateArchiveRulesModal .form select[name=flowid] option[value='+ row.flowid +']').prop('selected', true);
		$('#updateArchiveRulesModal .form select[name=filetype] option[value='+ row.filetype +']').prop('selected', true);
		
        //$("#updateArchiveRulesModal .form textarea[name='bz']").text(row.bz);


        // show
  		$('#updateArchiveRulesModal').modal({
  			backdrop: false,
  			keyboard: false
  		}).modal('show');
  		 
  	});
  	//动态加载文件标识、替换标识
  	$("#updateArchiveRulesModal .form select[name=filetype]").change(function(){
  		var filetype;
  		filetype=$(this).children('option:selected').val();
  		if(filetype==0){
  			$("#finalstrD1").show();
  		}else{
  			$("#finalstrD1").hide();
  		}
  	});
  	$("#updateArchiveRulesModal").on('hide.bs.modal', function () {
		  $("#updateArchiveRulesModal .form")[0].reset();

	  });
  	//入库规则新增
  	$("#archiveRulesAdd").click(function() {
  		
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
			  if (arr[0].yNodeType ==1){
				  layer.open({
					  title: '系统提示',
					  content: ("请选择数据节点"),
					  icon: '2'
				  });
				  return;
			  }
              getparent(arr[0].nodeId, parentList);
//              var menu = "";
//              var menuId = "";
//              for (var i = parentList.length - 1; i >= 0; i--) {
//                  menu += parentList[i][0] + ">";
//                  menuId += parentList[i][1] + ",";
//              }
              $("#addArchiveRulesModal .form input[name='catalogcode']").val(arr[0].catalogCode);
//              $("#addArchiveRulesModal .form input[name='menu']").val(menu+arr[0].text);
//              $("#addArchiveRulesModal .form input[name='menuId']").val(menuId+arr[0].id);


          // show
          $('#addArchiveRulesModal').modal({
              backdrop: false,
              keyboard: false
          }).modal('show');
          //reportTableA("reportTable3", "");
          reportTableB("reportTable3", base_url + "/archiveCatalog/findAllArchiveFiledManage");

      });
  	$("#addArchiveRulesModal .form select[name=filetype]").change(function(){
  		var filetype;
  		filetype=$(this).children('option:selected').val();
  		if(filetype==0){
  			$("#finalstrD").show();
  		}else{
  			$("#finalstrD").hide();
  		}
  	});
  		
  	$("#addArchiveRulesModal").on('hide.bs.modal', function () {
          $("#addArchiveRulesModal .form")[0].reset();

      });
  //入库规则新增保存
    var addArchiveRulesModalValidate = $("#addArchiveRulesModal .form").validate({
        errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
        	regexpstr : {
                required : true
            }
        },
        messages : {
        	regexpstr : {
                required :"请输入数据识别正则表达式！"
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
        	var filedMap = $('#reportTable3').bootstrapTable('getData');
      		var archiveRules={
      				"regexpstr":$("#addArchiveRulesModal .form textarea[name='regexpstr']").val(),
      				"catalogcode":$("#addArchiveRulesModal .form input[name='catalogcode']").val(),
      				"regexpxml":$("#addArchiveRulesModal .form textarea[name='regexpxml']").val(),
      				"regexpjpg":$("#addArchiveRulesModal .form textarea[name='regexpjpg']").val(),
      				"archivdisk":$("#addArchiveRulesModal .form input[name='archivdisk']").val(),
      				"finalstr":$("#addArchiveRulesModal .form input[name='finalstr']").val(),
      				"datalevel":$("#addArchiveRulesModal .form input[name='datalevel']").val(),
      				"wspFile":$("#addArchiveRulesModal .form input[name='wspFile']").val(),
      				"allFile":$("#addArchiveRulesModal .form input[name='allFile']").val(),
      				"rulestatus":$("#addArchiveRulesModal .form select[name=rulestatus] option:selected").val(),
      				"flowid":$('#addArchiveRulesModal .form select[name=flowid] option:selected').val(),
      				"filetype":$('#addArchiveRulesModal .form select[name=filetype] option:selected').val()
      				
      		};
      		$.post(base_url + "/archiveCatalog/saveArchiveRules",  {"archiveRules":JSON.stringify(archiveRules),"fileMapList":JSON.stringify(filedMap)}, function(data, status) {
                if (data.code == "200") {
                    $('#addArchiveRulesModal').modal('hide');
                    layer.msg('新增成功', {
                  	  icon: 1,
                  	  time: 1000 //2秒关闭（如果不配置，默认是3秒）
                    }, function(){
                  	  meataImgAndInfo.fnDraw();
                    }); 
                } else {
              	  layer.msg(data.msg || "新增失败", {
  						icon: 2,
  						time: 1000
              	  }); 
                }

            });
        }
    });
  	//入库规则修改保存
  	var updateArchiveRulesModalValidate = $("#updateArchiveRulesModal .form").validate({
        errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
        	regexpstr : {
                required : true
            }
        },
        messages : {
        	regexpstr : {
                required :"请输入数据识别正则表达式！"
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
      	  var filedMap = $('#reportTable2').bootstrapTable('getData');
      		var archiveRules={
      				"id":$("#updateArchiveRulesModal .form input[name='id']").val(),
      				"regexpstr":$("#updateArchiveRulesModal .form textarea[name='regexpstr']").val(),
      				"catalogcode":$("#updateArchiveRulesModal .form input[name='catalogcode']").val(),
      				"regexpxml":$("#updateArchiveRulesModal .form textarea[name='regexpxml']").val(),
      				"regexpjpg":$("#updateArchiveRulesModal .form textarea[name='regexpjpg']").val(),
      				"archivdisk":$('#updateArchiveRulesModal .form select[name=archivdisk] option:selected').val(),
      				"finalstr":$("#updateArchiveRulesModal .form input[name='finalstr']").val(),
      				"datalevel":$("#updateArchiveRulesModal .form input[name='datalevel']").val(),
      				"wspFile":$("#updateArchiveRulesModal .form input[name='wspFile']").val(),
      				"allFile":$("#updateArchiveRulesModal .form input[name='allFile']").val(),
      				"rulestatus":$("#updateArchiveRulesModal .form select[name=rulestatus] option:selected").val(),
      				"flowid":$('#updateArchiveRulesModal .form select[name=flowid] option:selected').val(),
      				"filetype":$('#updateArchiveRulesModal .form select[name=filetype] option:selected').val()
      				
      		};
      		$.post(base_url + "/archiveCatalog/saveArchiveRules",  {"archiveRules":JSON.stringify(archiveRules),"fileMapList":JSON.stringify(filedMap)}, function(data, status) {
                  if (data.code == "200") {
                      $('#updateArchiveRulesModal').modal('hide');
                      layer.msg('更新成功', {
                    	  icon: 1,
                    	  time: 1000 //2秒关闭（如果不配置，默认是3秒）
                      }, function(){
                    	  meataImgAndInfo.fnDraw();
                      }); 
                  } else {
                      layer.msg(data.msg || "更新失败", {
    						icon: 2,
    						time: 1000
                      }); 
                  }

              });
        }
    });

  });
  
  function downloadFile(obj) {
	  var id = $(obj).parent('p').attr("id");
	  $.ajax({
          type: "post",
          dataType: "json",
          url: base_url+"/SatelliteInformation/getDownloadPath/"+id,
          //data:JSON.stringify(data),
          success: function(data) {
              if(data.msg && data.msg != "") {
            	  window.location.href = data.msg;
              } else {
            	  alert("获取下载路径失败!");
              }
          }
      });
//	  $.post(base_url + "/SatelliteInformation/getDownloadPath/"+id,  id, function(data, status) {
//		  alert(data);
//      });
  }
  //更改回收状态
//  function del(obj,mark) {
//      layer.confirm('确认删除?', {icon: 3, title:'系统提示'}, function(index){
//          layer.close(index);
//          var id = $(obj).parent('p').attr("id");
//          /*var mark=0;
//          var data = {
//              "id": id,
//          };*/
//          
//          $.ajax({
//              type: "post",
//              dataType: "json",
//              url: base_url+"/SatelliteInformation/updateRecycleflag/"+mark+"/"+id,
//              //data:JSON.stringify(data),
//              success: function(data) {
//                  if (data.code == 200) {
//                	  layer.msg('删除成功', {
//  						icon: 1,
//  						time: 1000
//  					}, function(){
//  						 meataImgAndInfo.fnDraw();
//  					}); 
//                  } else {
//                	  layer.msg(data.msg || "删除失败", {
//  						icon: 2,
//  						time: 1000
//                    });
//                  }
//              }
//          });
//
//      });
//
//  }
  function del(obj,mark) {
	  layer.confirm('确认删除?', {icon: 3, title:'系统提示'}, function(index){
		  layer.close(index);
		  var id = $(obj).parent('p').attr("id");
		  $.ajax({
			  type: "post",
			  dataType: "json",
			  url: base_url+"/SatelliteInformation/deleteData/"+id,
			  //data:JSON.stringify(data),
			  success: function(data) {
				  if (data.code == 200) {
					  layer.msg('删除成功', {
						  icon: 1,
						  time: 1000
					  }, function(){
						  meataImgAndInfo.fnDraw();
					  }); 
				  } else {
					  layer.msg(data.msg || "删除失败", {
						  icon: 2,
						  time: 1000
					  });
				  }
			  }
		  });
		  
	  });
	  
  }
  
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
  function getTreeNode(){
	  	$.ajax({
	  		type: "post",
	  		dataType: "json",
	  		url: base_url+"/SatelliteInformation/getArchiveCatalog",
	  		success: function(defaultData) {
	  			$('#treeview5').treeview({
	  				color: "#428bca",
//	  			    icon: "glyphicon glyphicon-stop",
//	  			    selectedIcon: "glyphicon glyphicon-stop",
//	  				nodeIcon: 'glyphicon glyphicon glyphicon-stop',

	  				levels: 1,
	  				data: defaultData,
	  				onNodeSelected: function(event, data) {
	  					$('#catalogCode').val(data.catalogCode);
	  					//alert($('#catalogCode').val());
	  					meataImgAndInfo.fnDraw();
	  					//$('#catalogCode').val("");
	  				}
	  			});

	  		}
	  	});
	   }
  function viewImage(data){
	  var l=(screen.availWidth-500)/2;
	  var t=(screen.availHeight-300)/2;  
	  window.open(data.src,'newWin','width=720,height=600,top='+t+',left='+l+',toolbar=no,menubar=no,location=no,status=yes');
  }
//时间戳转时间
  function timestampToTime(timestamp) {
      var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
      var Y = date.getFullYear() + '-';
      var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
      var D = date.getDate() + ' ';
      var h = date.getHours() + ':';
      var m = date.getMinutes() + ':';
      var s = date.getSeconds();
      return Y+M+D+h+m+s;
  }
