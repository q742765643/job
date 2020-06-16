var dynamicParameterData = "";

$(function() {


//	if ($("#jobGroup").attr("paramVal")){
//		$("#jobGroup").find("option[value='" + $("#jobGroup").attr("paramVal") + "']").attr("selected",true);
//        $("#jobGroup").change();
//	}

	var flowId = $("#jobInfoId").val();
	// init date tables
	var logTable = $("#joblog_list").dataTable({
		
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
	        url: base_url + "/joblog/detailPageList",
	        data : function ( d ) {
	        	var obj = {};
	        	obj.logId = flowId;
	        	obj.start = d.start;
	        	obj.length = d.length;
	        	obj.logStatus = $('#logStatus').val();
                return obj;
            }
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": false,
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : false},
	                { "data": 'label', "visible" : true,"width":'20%'},
					{
						"data": 'code',
						"render": function ( data, type, row ) {
							return (data==200)?'<span style="color: green">成功</span>':(data==500)?'<span style="color: red">失败</span>':(data==0)?'':data;
						}

					},
					{
						"data": 'createTime',
						"render": function ( data, type, row ) {
							return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
						}
					},
	                { 
	                	"data": 'updateTime',
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": 'ip', "visible" : true,"width":'20%'},
	                { 
	                	"data": 'handleMsg',
	                	"render": function ( data, type, row ) {
	                		return data?'<a class="logTips" href="javascript:;" >查看<span style="display:none;">'+ data +'</span></a>':"无";
	                	}
	                },
	                {
	                	"data": 'dynamicParameter' ,
	                	"render": function ( data, type, row ) {
	                		return data?"<a href='javascript:void(0);' onclick='showDynamicParameter(a"+row.id+");'>查看<span id='a"+row.id+"' title='"+row.label+"' style='display:none;'>"+ data +"</span></a>":"无";
	                	}
	                
	                },
					{
						"data": '执行日志' ,
						"render": function ( data, type, row ) {
                            var temp = '<a href="javascript:;" class="logDetail" _id="'+ row.id +'" _jobLogId="'+ row.logId +'">执行日志</a>&nbsp&nbsp';
							return temp;
						}
					}
//	                {
//						"data": 'handleMsg' ,
//						"bSortable": false,
//						"width": "15%" ,
//	                	"render": function ( data, type, row ) {
//	                		var logUrl = base_url +'/joblog/detaillog?jobId='+ row.id;
//	                		// better support expression or string, not function
//	                		return function () {
//		                		if (row.triggerCode == 200){
//		                			var temp = '<a href="javascript:;" class="logDetail" _id="'+ row.id +'">执行日志</a>&nbsp&nbsp';
//		                			temp += '<a href='+logUrl+'>详情</a>';
//		                			if(row.tasktype==5){
//                                     temp += '<br><a href="/monitor_zh_CN.html?appointedId='+ row.id +'&processId='+row.modelId+'"  _id="'+ row.id +'">监控</a>';
//                                    }
//                                    if(row.handleCode == 0){
//		                				temp += '<br><a href="javascript:;" class="logKill" _id="'+ row.id +'" style="color: red;" >终止任务</a>' ;
//		                			}
//		                			return temp;
//		                		}
//		                		return null;	
//	                		}
//	                	}
//	                }
	            ],
		"language" : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "每页 _MENU_ 条记录",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
			"sInfoEmpty" : "无记录",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "表中数据为空",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortDescending" : ": 以降序排列此列"
			}
		}
	});
	
	// 日志弹框提示
	$('#joblog_list').on('click', '.logTips', function(){
		var msg = $(this).find('span').html();
		ComAlertTec.show(msg);
	});
	
	// 搜索按钮
	$('#searchBtn').on('click', function(){
		logTable.fnDraw();
	});
	
	// 查看执行器详细执行日志
    $('#joblog_list').on('click', '.logDetail', function(){
        var _id = $(this).attr('_id');
		var _jobLogId=$(this).attr('_jobLogId');
        window.open(base_url + '/joblog/parallelLogDetailPage?id=' + _id+'&jobLogId='+_jobLogId);
        return;
    });

	/**
	 * 终止任务
	 */
	$('#joblog_list').on('click', '.logKill', function(){
		var _id = $(this).attr('_id');

        layer.confirm('确认主动终止任务?', {icon: 3, title:'系统提示'}, function(index){
            layer.close(index);

            $.ajax({
                type : 'POST',
                url : base_url + '/joblog/logKill',
                data : {"id":_id},
                dataType : "json",
                success : function(data){
                    if (data.code == 200) {
                        layer.msg( "操作成功", {
        					icon: 1,
        					time: 2000 //2秒关闭（如果不配置，默认是3秒）
        				}, function(layero, index){
        					logTable.fnDraw();
        				});
                    } else {
                    	 layer.msg(data.msg || "操作失败", {
         					icon: 2,
         					time: 2000 //2秒关闭（如果不配置，默认是3秒）
         				});
                    }
                },
            });
        });

	});

	/**
	 * 清理任务Log
	 */
	$('#clearLog').on('click', function(){

		var jobGroup = $('#jobGroup').val();
		var jobId = $('#jobId').val();

		var jobGroupText = $("#jobGroup").find("option:selected").text();
		var jobIdText = $("#jobId").find("option:selected").text();

		$('#clearLogModal input[name=jobGroup]').val(jobGroup);
		$('#clearLogModal input[name=jobId]').val(jobId);

		$('#clearLogModal .jobGroupText').val(jobGroupText);
		$('#clearLogModal .jobIdText').val(jobIdText);

		$('#clearLogModal').modal('show');

	});
	$("#clearLogModal .ok").on('click', function(){
		$.post(base_url + "/joblog/clearLog",  $("#clearLogModal .form").serialize(), function(data, status) {
			if (data.code == "200") {
				$('#clearLogModal').modal('hide');
				layer.msg( "日志清理成功", {
					icon: 1,
					time: 2000 //2秒关闭（如果不配置，默认是3秒）
				}, function(layero, index){
					logTable.fnDraw();
				});
			} else {
				layer.msg(data.msg || "日志清理失败", {
					icon: 2,
					time: 2000 //2秒关闭（如果不配置，默认是3秒）
				}); 
			}
		});
	});
	$("#clearLogModal").on('hide.bs.modal', function () {
		$("#clearLogModal .form")[0].reset();
	});

});

//显示执行参数
var showDynamicParameter =function(row) {
	dynamicParameterData = row.textContent;
	$("#childAlgoName").text(row.title);
	parallelInfo("reportTable3",dynamicParameterData);
	$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
};

// 提示-科技主题
var ComAlertTec = {
	html:function(){
		var html =
			'<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
			'<div class="modal-dialog">' +
			'<div class="modal-content-tec">' +
			'<div class="modal-body"><div class="alert" style="color:#fff;"></div></div>' +
			'<div class="modal-footer">' +
			'<div class="text-center" >' +
			'<button type="button" class="btn btn-info ok" data-dismiss="modal" >确认</button>' +
			'</div>' +
			'</div>' +
			'</div>' +
			'</div>' +
			'</div>';
		return html;
	},
	show:function(msg, callback){
		// dom init
		if ($('#ComAlertTec').length == 0){
			$('body').append(ComAlertTec.html());
		}

		// 弹框初始
		$('#ComAlertTec .alert').html(msg);
		$('#ComAlertTec').modal('show');

		$('#ComAlertTec .ok').click(function(){
			$('#ComAlertTec').modal('hide');
			if(typeof callback == 'function') {
				callback();
			}
		});
	}
};
