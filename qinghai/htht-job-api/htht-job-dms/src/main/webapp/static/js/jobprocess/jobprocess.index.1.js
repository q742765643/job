  var parameterTable;
  $(function() {

  	$("#add").click(function() {
  		/*var arr = $('#treeview5').treeview('getSelected');
  		if (arr.length == 0) {
  			alert("请选择节点");
  			return;
  		}
  		$("#treeId").val(arr[0].id);*/
  		// show
  		$('#addJobprocess').modal({
  			backdrop: false,
  			keyboard: false
  		}).modal('show');
  		reportTable("reportTable1", "");
  		reportTable("reportTable2", "");



  	});

  	parameterTable = $("#parameterTable").dataTable({
  		"deferRender": true,
  		"processing": true,
  		"serverSide": true,
  		"aaSorting": [
  			[1, "asc"]
  		],
  		"ajax": {
  			url: base_url+"/jobprocess/pageList",
  			type: "post",
  			data: function(d) {
  				var obj = {};
  				obj.start = d.start;
  				obj.length = d.length;
                obj.modelName=$("#modelName").val();
                obj.modelIdentify=$("#modelIdentify").val();

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
  	var tableData = {};

  	$("#reportTable1Submit").click(function() {
  		var fixedParameter = $('#reportTable1').bootstrapTable('getData');
  		var dynamicParameter = $('#reportTable2').bootstrapTable('getData');


  		var data = {
  			"modelName": $("#addJobprocess .form input[name='modelName']").val(),
  			"treeId": $("#addJobprocess .form input[name='treeId']").val(),
  			"modelIdentify": $("#addJobprocess .form input[name='modelIdentify']").val(),
            "url": $("#addJobprocess .form input[name='url']").val(),
            "type": $("#addJobprocess .form select[name=type]").val(),
  			"fixedParameter": JSON.stringify(fixedParameter),
  			"dynamicParameter": JSON.stringify(dynamicParameter)
  		}
  		$.ajax({
  			type: "post",
  			data: data,
  			dataType: "json",
  			url: base_url+"/jobprocess/save",
  			success: function(data) {
                if (data.code == "200") {
                    $('#addJobprocess').modal('hide');
                    layer.msg( "新增成功", {
    					icon: 1,
    					time: 2000 //2秒关闭（如果不配置，默认是3秒）
    				}, function(layero, index){
    					parameterTable.fnDraw();
    				});
                } else {
                    layer.msg(data.msg || "新增失败", {
    					icon: 2,
    					time: 2000 //2秒关闭（如果不配置，默认是3秒）
    				}); 
                }
  			}
  		});
  	});

  	$("#reportTableUpdateSubmit").click(function() {
  		var fixedParameter = $('#reportTable3').bootstrapTable('getData');
  		var dynamicParameter = $('#reportTable4').bootstrapTable('getData');


  		var data = {
  			"modelName": $("#updateModal .form input[name='modelName']").val(),
  			"id": $("#updateModal .form input[name='id']").val(),
  			"modelIdentify": $("#updateModal .form input[name='modelIdentify']").val(),
            "url": $("#updateModal .form input[name='url']").val(),
            "type": $("#updateModal .form select[name=type]").val(),
  			"fixedParameter": JSON.stringify(fixedParameter),
  			"dynamicParameter": JSON.stringify(dynamicParameter)
  		}
  		$.ajax({
  			type: "post",
  			data: data,
  			dataType: "json",
  			url: base_url+"/jobprocess/save",
  			success: function(data) {
                if (data.code == "200") {
                    $('#updateModal').modal('hide');
                    layer.msg( "修改成功", {
    					icon: 1,
    					time: 2000 //2秒关闭（如果不配置，默认是3秒）
    				}, function(layero, index){
    					parameterTable.fnDraw();
    				});
                } else {
                    layer.msg(data.msg || "修改失败", {
    					icon: 2,
    					time: 2000 //2秒关闭（如果不配置，默认是3秒）
    				}); 
                }
  			}
  		});
  	});


  	$("#parameterTable").on('click', '.update', function() {
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
  		reportTable("reportTable3", base_url + "/jobinfo/getJobParameter?jobId=&parameterId=" + row.id + "&mark=1");
  		reportTable("reportTable4", base_url + "/jobinfo/getJobParameter?jobId=&parameterId=" + row.id + "&mark=2");

  		// base data
  		$("#updateModal .form input[name='id']").val(row.id);
  		$("#updateModal .form input[name='modelName']").val(row.modelName);
  		$("#updateModal .form input[name='modelIdentify']").val(row.modelIdentify);
        $("#updateModal .form input[name='url']").val(row.url);
        $('#updateModal .form select[name=type] option[value='+ row.type +']').prop('selected', true);


        // show
  		$('#updateModal').modal({
  			backdrop: false,
  			keyboard: false
  		}).modal('show');
  	});

      $('#searchBtn').on('click', function(){
          parameterTable.fnDraw();
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
              url: base_url+"/jobprocess/deleteParameter",
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