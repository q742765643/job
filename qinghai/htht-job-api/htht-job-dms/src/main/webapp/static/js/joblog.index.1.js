var logParamsTable;
var logConcent = "";
$(function() {

	// 任务组列表选中, 任务列表初始化和选中
	$("#jobGroup").on("change", function () {
		var jobGroup = $(this).children('option:selected').val();
		$.ajax({
			type : 'POST',
            async: false,   // async, avoid js invoke pagelist before jobId data init
			url : base_url + '/joblog/getJobsByGroup',
			data : {"jobGroup":jobGroup},
			dataType : "json",
			success : function(data){
				if (data.code == 200) {
					$("#jobId").html('<option value="0" >全部</option>');
					$.each(data.content, function (n, value) {
                        $("#jobId").append('<option value="' + value.id + '" >' + value.jobDesc + '</option>');
                    });
                    if ($("#jobId").attr("paramVal")){
                        $("#jobId").find("option[value='" + $("#jobId").attr("paramVal") + "']").attr("selected",true);
                    }
				} else {
					layer.msg(data.msg || "接口异常", {
						icon: 2,
						time: 2000 //2秒关闭（如果不配置，默认是3秒）
					});
				}
			},
		});
	});
//	if ($("#jobGroup").attr("paramVal")){
//		$("#jobGroup").find("option[value='" + $("#jobGroup").attr("paramVal") + "']").attr("selected",true);
//        $("#jobGroup").change();
//	}
	$("#jobGroup").change();

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


	var logTable = $("#joblog_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
	        url: base_url + "/joblog/pageList" ,
	        data : function ( d ) {
	        	var obj = {};
	        	obj.jobGroup = $('#jobGroup').val();
	        	obj.jobId = $('#jobId').val();
                obj.logStatus = $('#logStatus').val();
				obj.filterTime = $('#filterTime').val();
	        	obj.start = d.start;
	        	obj.length = d.length;
                return obj;
            }
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": false,
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : false},
					{ "data": 'jobGroup', "visible" : false},
					{ "data": 'executorParam', "visible" : false},
					{ "data": 'triggerTime', "visible" : false},
					{ "data": 'executorAddress', "visible" : false},
					{ "data": 'handleMsg', "visible" : false},
	                { "data": 'jobId', "visible" : false},
	                { "data": 'executorHandler', "visible" : true,"width":'20%'},
					{
						"data": 'triggerTime',
						"render": function ( data, type, row ) {
							return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
						}
					},
					{
						"data": 'triggerCode',
						"render": function ( data, type, row ) {
							return (data==200)?'<a title="查看日志" style="color: green" class="logTips" href="javascript:;">成功<span style="display:none;">'+ row.triggerMsg +'</span></a>'
								:(data==500)?'<a title="查看日志" style="color: red" class="logTips" href="javascript:;">失败<span style="display:none;">'+ row.triggerMsg +'</span></a>':(data==0)?'':data;
						}

					}/*,
					{
						"data": 'triggerMsg',
						"render": function ( data, type, row ) {
							return data?'<a class="logTips" href="javascript:;" >查看<span style="display:none;">'+ data +'</span></a>':"无";
						}
					}*/,
	                { 
	                	"data": 'handleTime',
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                {
						"data": 'handleCode',
						"render": function ( data, type, row ) {
							if(data==4){
								return '<span style="color: #FF6600">异常</span>';
							}
							return (data==200)?'<span style="color: green">成功</span>':(data==500)?'<span style="color: red">失败</span>':(data==0)?'':data;
						}
	                },
	                { 
	                	"data": 'handleMsg',
	                	"render": function ( data, type, row ) {
	                		return data?'<a class="logTips" href="javascript:;" >查看备注<span style="display:none;">'+ data +'</span></a>':"无";
	                	}
	                },
	                {
						"data": 'logFileName' ,
						"bSortable": false,
						"width": "15%" ,
	                	"render": function ( data, type, row ) {
	                		var logUrl = base_url +'/joblog/detaillog?jobId='+ row.id;
	                		// better support expression or string, not function
	                		return function () {
		                		if (row.triggerCode === 200){
                                    //var temp = '<a href="javascript:;" class="logDetail" _id="'+ row.id +'">查看日志</a>&nbsp&nbsp';
									//var temp = '<a href="javascript:;" onclick="showLogMessage()" _id="'+ row.id +'">查看日志</a>&nbsp&nbsp';
									//根据条件请求回日志
									var temp
                                    //var temp = '<a href="javascript:;" class="logTips logDetail" _id="'+ row.id +'"  onclick="showDynamicParameter(a)">查看日志<span id="showLogMsg" style="display:none;"></span></a>&nbsp&nbsp';

									if(row.tasktype ==5){
										console.log(111)
										temp = '&nbsp&nbsp';
										}else{
										console.log(222)
										temp = '&nbsp&nbsp<a href="javascript:;" class="logDetail" _id="'+ row.id +'" logFileName="'+ row.logFileName +'" executorAddress = "'+row.executorAddress+'">查看日志</a>&nbsp&nbsp';

									}

		                			/*此处是针对流程日志，不做处理*/
		                			if(row.tasktype===5 ||  row.tasktype===7){
		                				temp += '<a href="/monitor_zh_CN.html?appointedId='+ row.id +'&processId='+row.modelId+'"  _id="'+ row.id +'">监控</a>';
                                        temp += '&nbsp&nbsp<a href='+logUrl+'>详情</a>';
                                    }
                                    if(row.handleCode === 0){
		                				temp += '<br>&nbsp&nbsp<a href="javascript:;" class="logKill" _id="'+ row.id +'" style="color: red;" >终止任务</a>' ;
		                			}
		                			return temp;
		                		}
		                		return null;	
	                		}
	                	}
	                },
	                {
	                  	"data": 'id',
	                	"render": function ( data, type, row ) {
	                		return data?'<button class="cleanOneLog btn btn-danger btn-xs delete"  _id="'+data+'" type="button" " >删除</button>':"无";
	                	}
	                }
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
		// init date tables
		var fromLineNum = 1;    // [from, to], start as 1
		var pullFailCount = 0;
		$('#showLogModal').modal('show');
		var _id = $(this).attr('_id');
		var logFileName =$(this).attr('logFileName');
		var executorAddress =$(this).attr('executorAddress');

		$("#logConsoleRunning").show()
		$('#logConsole').html("");
		logConcent = "";
  		var tabData= [];
		$.ajax({
			type:"get",
			url:base_url + "/joblog/detailPageList",
			data:{
				logId:_id,
				start:0,
				length:10,
				logStatus:-1
			},
			success:function (data) {
				tabData= JSON.parse(data.data[0].dynamicParameter)
				console.log(tabData)
				$("#logParamsTable").dataTable().fnDestroy();
				logParamsTable = $("#logParamsTable").dataTable({
					"deferRender": true,
					"processing": true,
					"aaSorting": [
						[1, "asc"]
					],
					"searching": false,
					"ordering": false,
					data:tabData,
					"fnDrawCallback": function() {
						this.api().column(0).nodes().each(function(cell, i) {
							cell.innerHTML = i + 1;
						});
					},
					columns: [{
						data: null
					}, {
						data: 'parameterDesc'
					},{
						data: 'parameterName'
					},{
						data: 'parameterType'
					},{
						data: 'value'
					}
					],
					"language": {
						"sProcessing": "处理中...",
						"sLengthMenu": "每页 _MENU_ 条记录",
						"sZeroRecords": "没有匹配结果",
						"sInfo": "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
						"sInfoEmpty": "",
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
			}
		})


		function pullLog() {
			// pullFailCount, max=20
			if (pullFailCount++ > 3) {
				logRunStop('<span style="color: red;">终止请求Rolling日志,请求失败次数超上限,可刷新页面重新加载日志</span>');
				return;
			}

			$.ajax({
				type : 'POST',
				async: false,   // sync, make log ordered
				url : base_url + '/joblog/parallelLogDetailCat1',
				data : {
					"fromLineNum":fromLineNum,
					"logFileName":logFileName,
					"executorAddress":executorAddress
				},
				dataType : "json",
				success : function(data){
					console.log(data)
					if (data.code == 200) {
						if (!data.content) {
							console.log('pullLog fail');
							return;
						}
						if (fromLineNum != data.content.fromLineNum) {
							console.log('pullLog fromLineNum not match');
							return;
						}
						if (fromLineNum > data.content.toLineNum ) {
							console.log('pullLog already line-end');

							// valid end
							if (data.content.end) {
								logRunStop('<br><span style="color: green;">[Rolling Log Finish]</span>');
								return;
							}

							return;
						}

						// append content
						fromLineNum = data.content.toLineNum + 1;
						logConcent += data.content.logContent+"<br/>"
						console.log(data.content.logContent)
						$('#logConsole').html(logConcent);
						pullFailCount = 0;

						// scroll to bottom
						scrollTo(0, document.body.scrollHeight);        // $('#logConsolePre').scrollTop( document.body.scrollHeight + 300 );

					} else {
						console.log('pullLog fail:'+data.msg);
					}
				}
			});
		}

		// pull first page
		pullLog();


		 var logRun = setInterval(function () {
			pullLog()
		}, 3000);
		function logRunStop(content){
			$('#logConsoleRunning').hide();
			logRun = window.clearInterval(logRun);
			$('#logConsole').append(content);
		}

		return;
	});


	// 查看执行器详细执行日志
	$('#joblog_list').on('click', '.logProcessDetail', function(){
		var _id = $(this).attr('_id');

		window.open(base_url + '/joblog/logDetailPage?id=' + _id);

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
	// 清除单条日志
	$('#joblog_list').on('click', '.cleanOneLog', function(){

		var _id = $(this).attr("_id");
        $.ajax({
            type : 'POST',
            url : base_url + '/joblog/clearOneLog',
            data : {"id":_id},
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    layer.msg( "删除成功", {
						icon: 1,
						time: 2000 //2秒关闭（如果不配置，默认是3秒）
					}, function(layero, index){
						logTable.fnDraw();
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
				layer.msg("日志清理成功", {
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
function migrateParameter(obj) {
	$('#margin').modal({
		backdrop: false,
		keyboard: false
	}).modal('show');
}
function showLogMessage(obj) {
	var _id = $(this).parents("#joblog_list").attr('_id');
}

