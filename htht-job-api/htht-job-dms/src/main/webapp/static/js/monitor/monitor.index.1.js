var parameterTable;
var algoManagerTable;

$(function () {

    // remove
    $('.remove').on('click', function () {
        var id = $(this).attr('id');

        layer.confirm('确认删除节点?', {icon: 3, title: '系统提示'}, function (index) {
            layer.close(index);

            $.ajax({
                type: 'POST',
                url: base_url + '/monitor/remove',
                data: {"id": id},
                dataType: "json",
                success: function (data) {
                    if (data.code == 200) {
                        layer.msg("删除成功", {
                            icon: 1,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function (layero, index) {
                            window.location.reload();
                        });
                    } else {
                        layer.msg(data.msg || "删除失败", {
                            icon: 2,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }
                },
            });
        });

    });

    // jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”
    jQuery.validator.addMethod("myValid01", function (value, element) {
        var length = value.length;
        var valid = /^[a-z][a-zA-Z0-9-]*$/;
        return this.optional(element) || valid.test(value);
    }, "限制以小写字母开头，由小写字母、数字和下划线组成");

    $('.add').on('click', function () {
        $('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
    });

    jQuery.validator.addMethod("myregistryIp", function(value, element) {
        var ip = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\:([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/;
        return this.optional(element) || (ip.test(value) && (RegExp.$1 < 256 && RegExp.$2 < 256 && RegExp.$3 < 256 && RegExp.$4 < 256));
    }, "Ip地址格式错误");

    jQuery.validator.addMethod("concurrency", function(value, element) {
        var concurrency =/^[1-9]\d*$/;
        return this.optional(element) || (concurrency.test(value));
    });
    var addModalValidate = $("#addModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            registryKey: {
                required: true,
                rangelength: [4, 64]
                //myValid01 : true
            },
            registryIp: {
                required: true,
                rangelength: [7, 25],
                myregistryIp:true
            },
            concurrency:{
                required: true,
                concurrency :true
            },

        },
        messages: {
            registryKey: {
                required: "请输入“节点名称”",
                rangelength: "长度限制为4~64"
                //	myValid01: "限制以小写字母开头，由小写字母、数字和中划线组成"
            },
            registryIp: {
                required: "请输入“节点ip”",
                rangelength: "长度限制为7~25"
            },
            concurrency:{
                required: "请输入“节点总能力”",
                concurrency: "请输入正整数"
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
            $.post(base_url + "/monitor/save", $("#addModal .form").serialize(), function (data, status) {
                if (data.code == "200") {
                    $('#addModal').modal('hide');
                    layer.msg("新增成功", {
                        icon: 1,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function (layero, index) {
                        window.location.reload();
                    });
                } else {
                    layer.msg(data.msg || "新增失败", {
                        icon: 2,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    });
                }
            });
        }
    });
    $("#addModal").on('hide.bs.modal', function () {
        $("#addModal .form")[0].reset();
        addModalValidate.resetForm();
        $("#addModal .form .form-group").removeClass("has-error");
    });


    // update
    $('.update').on('click', function () {
        $("#updateModal .form input[name='id']").val($(this).attr("id"));
        $("#updateModal .form select[name='deploySystem']").val($(this).attr("deploySystem"));
        $("#updateModal .form input[name='registryKey']").val($(this).attr("registryKey"));
        $("#updateModal .form input[name='registryIp']").val($(this).attr("registryIp"));
        $("#updateModal .form input[name='concurrency']").val($(this).attr("zNum"));


        $('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
 	
    //algoManage
    $("#allBinding").on('click',function(){
    	if($(this).is(":checked")){
    	  var bindingList = $("table input[name = 'bingding']");	
    	  for(var a = 0;a<bindingList.length;a++){
    		  bindingList[a].checked =true;
    	  }
    	}else{
      	  var bindingList = $("table input[name = 'bingding']");	
    	  for(var a = 0;a<bindingList.length;a++){
    		  bindingList[a].checked =false;
    	  }
    	}
    });
    
    $("#allDownload").on('click',function(){
    	if($(this).is(":checked")){
      	  var downloadList = $("table input[name = 'download']");	
      	  for(var a = 0;downloadList.length;a++){
      		downloadList[a].checked =true;
      	  }
      	}else{
          var downloadList = $("table input[name = 'download']");	
      	  for(var a = 0;a<downloadList.length;a++){
      		downloadList[a].checked =false;
      	  }
      	}
    });
    
    
    $("#saveAlgoManage").on('click',function(){
    	var algoManageList = new Array();
    	//节点id
       var nodeId =	$("#ipId_find").val();
       //绑定和下载
       var bindingList = $("table input[name = 'bingding']");
       var downloadList = $("table input[name = 'download']");	
       if(bindingList.length == downloadList.length){
    	   for(var a = 0;a<bindingList.length;a++){
    		   var algoManage =  new Object();
    		   algoManage.algoId = bindingList[a].value;
    		   algoManage.registryId = nodeId;
    		   algoManage.isMapping = bindingList[a].checked;
    		   algoManage.isDownload = downloadList[a].checked;
    		   algoManageList.push(algoManage);
    	   }
    	   var list = JSON.stringify(algoManageList);
    	   //下载中,用户体验
    	   if(downloadList.length > 0){
    		   layer.msg( "节点下载算法中,请稍候...", {
    			   icon: 1,
    			   time: 1000000 //2秒关闭（如果不配置，默认是3秒）
    		   }, function(layero, index){
    			   
    		   });
    	   }
         	$.ajax({
          		type: "post",
          		data:list,
          		dataType: "json",
          		contentType:"application/json",
          		url: base_url+"/monitor/saveAlgoManageList",
          		success: function(data) {
                    if (data.code == "200") {
                        layer.msg(data.content, {
                            icon: 1,
                            time: 1000 //2秒关闭（如果不配置，默认是3秒）
                        }, function (layero, index) {
                        	algoManagerTable.fnDraw(false);
                        });
                    } else {
                        layer.msg(data.msg || "部署失败", {
                            icon: 2,
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        });
                    }
          		}
          	});
       }else{
    	   layer.msg("算法数据错误", {
               icon: 1,
               time: 2000 //2秒关闭（如果不配置，默认是3秒）
           }, function (layero, index) {
               window.location.reload();
           });
       }
    });
    
    $(".deploy").on('click',function(){
    	$("#deployIpName").text($(this).attr("registryIp"));
    	$("#ipId_find").val($(this).attr("id"));
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
    	  			$('#treeview1').treeview({
    	  				color: "#428bca",
    	  				emptyIcon: '',
    	  				// 没有子节点的节点图标
    	  				levels: 1,
    	  				data: defaultData,
    					onNodeSelected: function (event, data) {
    						$('#treeId_find').val(data.id);
    						algoManagerTable.fnDraw();
    					 }
    	  			});
    			    
    			    
    		    algoManagerTable = $("#algoMapping").dataTable({
    		    	  		"deferRender": true,
    		    	  		"processing": true,
    		    	  	//	 "retrieve": true,
    		    	  		"destroy":true,
    		    	  		"serverSide": true,
    					 /* "bStateSave":true,*/
    		    	  		"aaSorting": [
    		    	  			[1, "asc"]
    		    	  		],
    		    	  		"ajax": {
    		    	  			url: base_url+"/monitor/findAlgoManageList",
    		    	  			type: "post",
    		    	  			data: function(d) {
    		    	  				$("#allDownload").attr("checked",false);
    		    	  				$("#allBinding").attr("checked",false);
    		    	  				var obj = {};
    		    	  				obj.start = d.start;
    		    	  				obj.length = d.length;
    		    	                obj.ipId=$("#ipId_find").val();
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
    		    	  			"sClass": "left", "bSortable": false, "sWidth": "30",
    		    	  			"data": null
    		    	  		}, {
    		    	  			"data": 'algoId',
    		    	  			"visible": false 
    		    	  		},{
    	                    //    "sTitle": '',
    	                        "sClass": "left", "bSortable": false, "sWidth": "45",
    	                        "mRender": function (settings, rowIdx, rec, type) {
    	                        var btnBind;
    	                        if(rec.isMapping){
    	                        	 btnBind = "<label><input type='checkbox' name='bingding' value='"+rec.algoId+"' checked='checked'/><span class='lbl'></span></label>";
    	                        }else{
    	                        	btnBind = "<label><input type='checkbox' name='bingding' value='"+rec.algoId+"' /><span class='lbl'></span></label>";
    	                        }
	                            return btnBind;
    	                        }
    	                    },{
        	                    //    "sTitle": '',
        	                        "sClass": "left", "bSortable": false, "sWidth": "45",
        	                        "mRender": function (settings, rowIdx, rec, type) {
    	                            var date = rec.id + "/" + rec.cjr;
    	                            var download = "<label><input type='checkbox' name='download'/><span class='lbl'></span></label>";
    	                            return download;
        	                   }
        	                },{
        	                	"sClass": "left", "bSortable": false, "sWidth": "100",
    		    	  			"data": 'modelName',
    		    	  			"visible": true
    		    	  		}, {
    		    	  			"data": 'algoZipName',
    		    	  			"visible": true
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
    		    $('#algoDeploy').modal({backdrop: false, keyboard: false}).modal('show');
      		}
      	});
    	
    	
    });

    //info
    $('.info').on('click', function () {
        $("#jobInfoList").empty();
        $.post(base_url + "/monitor/info", {"id": $(this).attr("id")}, function (list) {
            if (0 == list.length) {
                $("#jobInfoList").append(
                    "<tr>"
                    + '<td>暂无</td>'
                    + '<td>暂无</td>'
                    + '<td>暂无</td>'
                    + "</tr>"
                );
            } else {
                for (var i = 0; i < list.length; i++) {
                    var jobDesc = list[i].xxlJobInfo.jobDesc;
                    var lineNumber = list[i].lineNumber;
                    var operateNumber = list[i].operateNumber;
                    if (null == lineNumber) {
                        lineNumber = 0;
                    }
                    if (null == operateNumber) {
                        operateNumber = 0;
                    }
                    $("#jobInfoList").append(
                        "<tr>"
                        + '<td>' + jobDesc + '</td>'
                        + '<td>' + lineNumber + '</td>'
                        + '<td>' + operateNumber + '</td>'
                        + "</tr>"
                    );
                }
            }
            $('#infoModal').modal({backdrop: false, keyboard: false}).modal('show');
        });
    });

    var updateModalValidate = $("#updateModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            registryKey: {
                required: true,
                rangelength: [4, 64]
                //myValid01 : true
            },
            registryIp: {
                required: true,
                rangelength: [7, 25]
            },
            concurrency:{
                required: true,
                concurrency :true
            }
        },
        messages: {
            registryKey: {
                required: "请输入“节点名称”",
                rangelength: "长度限制为4~64"
                //	myValid01: "限制以小写字母开头，由小写字母、数字和中划线组成"
            },
            registryIp: {
                required: "请输入“节点ip”",
                rangelength: "长度限制为7~25"
            },
            concurrency:{
                required: "请输入“节点总能力”",
                concurrency: "请输入正整数"
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
            $.post(base_url + "/monitor/update", $("#updateModal .form").serialize(), function (data, status) {
                if (data.code == "200") {
                    $('#addModal').modal('hide');
                    layer.msg("更新成功", {
                        icon: 1,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function (layero, index) {
                        window.location.reload();
                    });
                } else {
                    layer.msg(data.msg || "更新失败", {
                        icon: 2,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    });
                }
            });
        }
    });
    $("#updateModal").on('hide.bs.modal', function () {
        $("#updateModal .form")[0].reset();
        addModalValidate.resetForm();
        $("#updateModal .form .form-group").removeClass("has-error");
    });


});
