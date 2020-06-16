(function($){
    $.fn.serializeJson=function(){
        var serializeObj={};
        var array=this.serializeArray();
        var str=this.serialize();
        $(array).each(function(){
            if(serializeObj[this.name]){
                if($.isArray(serializeObj[this.name])){
                    serializeObj[this.name].push(this.value);
                }else{
                    serializeObj[this.name]=[serializeObj[this.name],this.value];
                }
            }else{
                serializeObj[this.name]=this.value;
            }
        });
        return serializeObj;
    };
})(jQuery);

$(function(){

	// logout
	$("#logoutBtn").click(function(){
		layer.confirm('确认注销登录?', {icon: 3, title:'系统提示'}, function(index){
			layer.close(index);

			$.get(base_url + "sso/logout", function(data) {
                window.location.href = base_url + "/";
                /*if (data.code == "200") {
                    layer.msg('注销成功', {
                        icon: 1,
                        time: 1000 //2秒关闭（如果不配置，默认是3秒）
                    }, function(){
                        window.location.href = base_url + "/";
                    });
                } else {
                    layer.msg(data.msg || "操作失败", {
                        icon: 2,
                        time: 1000 //2秒关闭（如果不配置，默认是3秒）
                    });

                }*/
			});
		});

	});

	// slideToTop
	var slideToTop = $("<div />");
	slideToTop.html('<i class="fa fa-chevron-up"></i>');
	slideToTop.css({
		position: 'fixed',
		bottom: '20px',
		right: '25px',
		width: '40px',
		height: '40px',
		color: '#eee',
		'font-size': '',
		'line-height': '40px',
		'text-align': 'center',
		'background-color': '#222d32',
		cursor: 'pointer',
		'border-radius': '5px',
		'z-index': '99999',
		opacity: '.7',
		'display': 'none'
	});
	slideToTop.on('mouseenter', function () {
		$(this).css('opacity', '1');
	});
	slideToTop.on('mouseout', function () {
		$(this).css('opacity', '.7');
	});
	$('.wrapper').append(slideToTop);
	$(window).scroll(function () {
		if ($(window).scrollTop() >= 150) {
			if (!$(slideToTop).is(':visible')) {
				$(slideToTop).fadeIn(500);
			}
		} else {
			$(slideToTop).fadeOut(500);
		}
	});
	$(slideToTop).click(function () {
		$("body").animate({
			scrollTop: 0
		}, 100);
	});

	// 左侧菜单状态，js + 后端 + cookie方式（新）
	$('.sidebar-toggle').click(function(){
		var xxljob_adminlte_settings = $.cookie('xxljob_adminlte_settings');	// 左侧菜单展开状态[xxljob_adminlte_settings]：on=展开，off=折叠
		if ('off' == xxljob_adminlte_settings) {
            xxljob_adminlte_settings = 'on';
		} else {
            xxljob_adminlte_settings = 'off';
		}
		$.cookie('xxljob_adminlte_settings', xxljob_adminlte_settings, { expires: 7 });	//$.cookie('the_cookie', '', { expires: -1 });
	});
	// 左侧菜单状态，js + cookie方式（遗弃）
	/*
	 var xxljob_adminlte_settings = $.cookie('xxljob_adminlte_settings');
	 if (xxljob_adminlte_settings == 'off') {
	 $('body').addClass('sidebar-collapse');
	 }
	 */
	$('#reportTable1','#reportTable2').on( 'click', 'td:has(.editable)', function (e) {
	    //e.preventDefault();
	    e.stopPropagation(); // 阻止事件的冒泡行为
	   $(this).find('.editable').editable('show'); // 打开被点击单元格的编辑状态
	} );

});

function reportTable(reportTableId,url){

    if (url==null||url==""){
        table(reportTableId,"");
    }else{
    $.ajax({
        url: url,
        type: 'post',
        dataType: 'json',
        success: function (data) {
            table(reportTableId,data.list);
        }
        });
    }

}

function reportTableFix1(reportTableId,data){
	tablefix(reportTableId,data.list);
}

function reportTableDyn(reportTableId,data){
	table(reportTableId,data.list);
}


function reportTableFix(reportTableId,url){
	if (url==null||url==""){
		tablefix(reportTableId,"");
	}
	else{
		$.ajax({
			url: url,
			type: 'post',
			dataType: 'json',
			success: function (data) {
				tablefix(reportTableId,data.list);
			}
		});
	}
	
}

function table(reportTableId,data) {
    $("#" + reportTableId).bootstrapTable('destroy');
    $("#" + reportTableId).bootstrapTable({
        method: 'get',
        data: data,
        editable: true,//开启编辑模式
        clickToSelect: true,
        cache: false,
        columns: [
            {
                field: "id", title: "序号", width: "5%", edit: false, formatter: function (value, row, index) {
                row.id = index + 1;
                return index + 1;
            }
            },
            {field: "parameterDesc", title: "参数名称", width: "10%", edit: {required: true, type: 'text'}},
            {field: "parameterName", title: "参数标识", width: "10%", edit: {required: true, type: 'text'}},
            {
                field: "parameterType",
                title: "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;参数类型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;",
                width: "20%",
                align: 'center',
                edit: {
                    type: 'select',
                    data: [{id: 'string', text: '字符串'},
                           {id: 'int', text: '整型'},
                           {id: 'float', text: '单浮点型'},
                           {id: 'double', text: '双浮点型'},
                           {id: 'long', text:'长整型'},
                           {id: 'array', text: '数组'},
                           {id: 'select',text:'枚举'},
                           {id: 'inputparamfile', text: '输入参数文件'},
                           {id: 'infile', text: '输入文件'},
                           {id: 'inputinfolder', text: '输入文件夹'},
                           {id: 'outputstring', text:'输出字符串'},
                           {id: 'outfile', text:'输出文件'},
                           {id: 'outfolder', text:'输出文件夹'}]
                }
            },
            {
                field: "display",
                title: "是否显示",
                width: "5%",
                align: 'center',
                edit: {
                    type: 'select',
                    data: [{id: 'true', text: '是'},{id: 'false', text: '否'}]
                }
            },
            {field: "url", title: "枚举选项", width: "40%", edit: {type: 'text'}},
            {field: "expandedname", title: "文件后缀名", width: "15%", edit: {type: 'text'}},
            {field: "value", title: "参数默认值", width: "15%", edit: {type: 'text'}},
            {
                field: "operate", title: "操作", width: "5%", formatter: function (value, row, index) {
                return operateFormatter(index, reportTableId);
            }, edit: false
            }
        ],
        onEditableHidden: function (field, row, $el, reason) { // 当编辑状态被隐藏时触发
            if (reason === 'save') {
                var $td = $el.closest('tr').children();
                $td.eq(-1).html((row.price * row.number).toFixed(2));
                $el.closest('tr').next().find('.editable').editable('show'); //编辑状态向下一行移动
            } else if (reason === 'nochange') {
                $el.closest('tr').next().find('.editable').editable('show');
            }
        },
        onEditableSave: function (field, row, oldValue, $el, reportTableId) {
            $table = $("#" + reportTableId).bootstrapTable({});
            $table.bootstrapTable('updateRow', {index: row.rowId, row: row});
        }
    });

}
function tablefix(reportTableId,data) {

	$("#" + reportTableId).bootstrapTable('destroy');
	$("#" + reportTableId).bootstrapTable({
		method: 'get',
		data: data,
		editable: true,//开启编辑模式
		clickToSelect: true,
		cache: false,
		columns: [
		          {
		        	  field: "id", title: "序号", width: "5%", edit: false, formatter: function (value, row, index) {
		        		  row.id = index + 1;
		        		  return index + 1;
		        	  }
		          },
		          {field: "parameterDesc", title: "参数名称", width: "10%", edit: {required: true, type: 'text'}},
		          {field: "parameterName", title: "参数标识", width: "10%", edit: {required: true, type: 'text'}},
		          {
		        	  field: "parameterType",
		        	  title: "参数类型",
		        	  width: "15%",
		        	  edit: {
		        		  type: 'select',
		        		  data: [{id: 'string', text: '字符串'},{id: 'select',text:'枚举'},{id: 'date', text: '时间'}]
		        	  }
		          },
		          {field: "url", title: "枚举选项", width: "40%", edit: {type: 'text'}},
		          {field: "value", title: "参数默认值", width: "15%", edit: {type: 'text'}},
		          {
		        	  field: "operate", title: "操作", width: "5%", formatter: function (value, row, index) {
		        		  return operateFormatter(index, reportTableId);
		        	  }, edit: false
		          }
		          ],
		          onEditableHidden: function (field, row, $el, reason) { // 当编辑状态被隐藏时触发
		        	  if (reason === 'save') {
		        		  var $td = $el.closest('tr').children();
		        		  $td.eq(-1).html((row.price * row.number).toFixed(2));
		        		  $el.closest('tr').next().find('.editable').editable('show'); //编辑状态向下一行移动
		        	  } else if (reason === 'nochange') {
		        		  $el.closest('tr').next().find('.editable').editable('show');
		        	  }
		          },
		          onEditableSave: function (field, row, oldValue, $el, reportTableId) {
		        	  $table = $("#" + reportTableId).bootstrapTable({});
		        	  $table.bootstrapTable('updateRow', {index: row.rowId, row: row});
		          }
	});
	
}
function parallelInfo(reportTableId,data) {
	data = JSON.parse(data);
	$("#" + reportTableId).bootstrapTable('destroy');
	$("#" + reportTableId).bootstrapTable({
		data: data,
		clickToSelect: true,
		cache: false,
		columns: [
		          {
		        	  field: "id", title: "序号", width: "5%", edit: false, formatter: function (value, row, index) {
		        		  row.id = index + 1;
		        		  return index + 1;
		        	  }
		          },
		          {field: "parameterDesc", title: "参数名称", width: "10%", edit: {required: true, type: 'text'}},
		          {field: "parameterName", title: "参数标识", width: "10%", edit: {required: true, type: 'text'}},
		          {
		        	  field: "parameterType",
		        	  title: "参数类型",
		        	  width: "15%",
		        	  edit: {
		        		  type: 'select',
		        		  data: [{id: 'string', text: '字符串'},{id: 'select',text:'枚举'},{id: 'date', text: '时间'}]
		        	  }
		          },
		          {field: "url", title: "枚举选项", width: "40%", edit: {type: 'text'}},
		          {field: "value", title: "执行参数值", width: "15%", edit: {type: 'text'}}
		          ]
	});
	
//    $("#"+reportTableId+"Div").html("");
//    $("#"+reportTableId+"Div").html(data);
//    $("#"+reportTableId+"Div").show();
//    $("#"+reportTableId).hide();
	
}
function reportTableinfo(reportTableId,url,jobId){
    console.log(url);
    $.ajax({
        url: url,
        type: 'post',
        dataType: 'json',
        success: function (data) {
            if(data.parameterType=="1") {
                $.ajax({
                    url: base_url +data.url+"?jobId="+jobId,
                    type: 'get',
                    dataType: 'html',
                    success: function (data) {
                        $("#"+reportTableId+"Div").html("");
                        $("#"+reportTableId+"Div").html(data);
                        $("#"+reportTableId+"Div").show();
                        $("#"+reportTableId).hide();
                    }
                });
            }else{
                $("#"+reportTableId+"Div").hide();
                $("#"+reportTableId).show();

            }
            $("#"+reportTableId).bootstrapTable('destroy');
            $("#"+reportTableId).bootstrapTable({
                method: 'get',
                data:data.list,
                editable:true,//开启编辑模式
                clickToSelect: true,
                cache : false,
                columns: [
                    {field:"id",title:"序号",width:"5%",edit:false,formatter: function (value, row, index) {
                        row.id=index+1;
                        return index+1;
                    }},
                    {field:"parameterDesc",title:"参数名称",width:"10%",edit:false},
                    {field:"parameterName",title:"参数标识",width:"20%",edit:false},
                    {
                        field: "parameterType",
                        title: "参数类型",
                        width: "15%",
                        edit:false,
                        formatter: function (value, row, index) {
                            var parameterType="";
                            switch(value)
                            {
                                case 'string':
                                    parameterType='字符串';
                                    break;
                                case 'int':
                                    parameterType='整型';
                                    break;
                                case 'float':
                                    parameterType='单浮点型';
                                    break;
                                case 'double':
                                    parameterType='双浮点型';
                                    break;
                                case 'long':
                                    parameterType='长整型';
                                    break;
                                case 'array':
                                    parameterType='数组';
                                    break;
                                case 'select':
                                    parameterType='枚举';
                                    break;
                                case 'inputparamfile':
                                    parameterType='输入参数文件';
                                    break;
                                case 'infile':
                                    parameterType='输入文件';
                                    break;
                                case 'inputinfolder':
                                    parameterType='输入文件夹';
                                    break;
                                case 'outputstring':
                                    parameterType='输出字符串';
                                    break;
                                case 'outfile':
                                    parameterType='输出文件';
                                    break;
                                case 'outfolder':
                                    parameterType='输出文件夹';
                                    break;
                            }
                            return parameterType;
                    }},
                    {field:"value",title:"参数默认值",width:"50%",edit:{type:'zdy'}},
                    {field: "expandedname", title: "文件后缀名", width: "15%", edit: {type: 'text'}},
                    {field:"operate",title:"操作","visible": false}

                ],
                onEditableHidden: function(field, row, $el, reason) { // 当编辑状态被隐藏时触发
                    if(reason === 'save') {
                        var $td = $el.closest('tr').children();
                        $td.eq(-1).html((row.price*row.number).toFixed(2));
                        $el.closest('tr').next().find('.editable').editable('show'); //编辑状态向下一行移动
                    } else if(reason === 'nochange') {
                        $el.closest('tr').next().find('.editable').editable('show');
                    }
                },
                onEditableSave: function (field, row, oldValue, $el,reportTableId) {
                    $table = $("#"+reportTableId).bootstrapTable({});
                    $table.bootstrapTable('updateRow', {index: row.rowId, row: row});
                }
            });

        }


    });

}
function reportTableFlow(reportTableId,url){
            $("#"+reportTableId).bootstrapTable('destroy');
            $("#"+reportTableId).bootstrapTable({
                method: 'get',
                url:url,
                editable:true,//开启编辑模式
                clickToSelect: true,
                cache : false,
                columns: [
                    {field:"id",title:"序号",width:"5%",edit:false,formatter: function (value, row, index) {
                        row.id=index+1;
                        return index+1;
                    }},
                    {field:"group",title:"分组",width:"10%",edit:false},
                    {field:"parameterDesc",title:"参数名称",width:"10%",edit:false},
                    {field:"parameterName",title:"参数标识",width:"20%",edit:false},
                    {
                        field: "parameterType",
                        title: "参数类型",
                        width: "15%",
                        edit:false,
                        formatter: function (value, row, index) {
                            var parameterType="";
                            switch(value)
                            {
                                case 'string':
                                    parameterType='字符串';
                                    break;
                                case 'int':
                                    parameterType='整型';
                                    break;
                                case 'float':
                                    parameterType='单浮点型';
                                    break;
                                case 'double':
                                    parameterType='双浮点型';
                                    break;
                                case 'long':
                                    parameterType='长整型';
                                    break;
                                case 'array':
                                    parameterType='数组';
                                    break;
                                case 'select':
                                    parameterType='枚举';
                                    break;
                                case 'inputparamfile':
                                    parameterType='输入参数文件';
                                    break;
                                case 'infile':
                                    parameterType='输入文件';
                                    break;
                                case 'inputinfolder':
                                    parameterType='输入文件夹';
                                    break;
                                case 'outputstring':
                                    parameterType='输出字符串';
                                    break;
                                case 'outfile':
                                    parameterType='输出文件';
                                    break;
                                case 'outfolder':
                                    parameterType='输出文件夹';
                                    break;
                            }
                            return parameterType;
                    }},
                    {field:"value",title:"参数默认值",width:"50%",edit:{type:'zdy'}},
                    {field: "expandedname", title: "文件后缀名", width: "15%", edit: {type: 'text'}},
                    {field:"operate",title:"操作","visible": false}

                ],
                onEditableHidden: function(field, row, $el, reason) { // 当编辑状态被隐藏时触发
                    if(reason === 'save') {
                        var $td = $el.closest('tr').children();
                        $td.eq(-1).html((row.price*row.number).toFixed(2));
                        $el.closest('tr').next().find('.editable').editable('show'); //编辑状态向下一行移动
                    } else if(reason === 'nochange') {
                        $el.closest('tr').next().find('.editable').editable('show');
                    }
                },
                onEditableSave: function (field, row, oldValue, $el,reportTableId) {
                    $table = $("#"+reportTableId).bootstrapTable({});
                    $table.bootstrapTable('updateRow', {index: row.rowId, row: row});
                }
            });

}

function tableMapping(reportTableId,data) {
            $("#"+reportTableId).bootstrapTable('destroy');
            $("#"+reportTableId).bootstrapTable({
                method: 'get',
                data:data,
                editable:true,//开启编辑模式
                clickToSelect: true,
                cache : false,
                columns: [
                    {field:"id",title:"序号",width:"5%",edit:false,formatter: function (value, row, index) {
                        row.id=index+1;
                        return index+1;
                    }},
                    {field:"label",title:"分组",width:"10%",edit:false},
                    {field:"parameterDesc",title:"参数描述",width:"20%",edit:false},
                    {field:"value",title:"对应参数",width:"25%",edit:{type:'zdy'}},
                    {field:"matchBefore",title:"匹配前",width:"25%",edit: {type: 'text'}},
                    {field:"matchAfter",title:"匹配后",width:"25%",edit: {type: 'text'}},
                    {field:"operate",title:"操作","visible": false}

                ],
                onEditableHidden: function(field, row, $el, reason) { // 当编辑状态被隐藏时触发
                    if(reason === 'save') {
                        var $td = $el.closest('tr').children();
                        $td.eq(-1).html((row.price*row.number).toFixed(2));
                        $el.closest('tr').next().find('.editable').editable('show'); //编辑状态向下一行移动
                    } else if(reason === 'nochange') {
                        $el.closest('tr').next().find('.editable').editable('show');
                    }
                },
                onEditableSave: function (field, row, oldValue, $el,reportTableId) {
                    $table = $("#"+reportTableId).bootstrapTable({});
                    $table.bootstrapTable('updateRow', {index: row.rowId, row: row});
                }

    });

}
function operateFormatter(index,reportTableId) {
    return [
      "<a class=\"remove\" href='javascript:removeRow("+index+",\""+reportTableId+"\")' title=\"删除改行\">",
      "<i class='glyphicon glyphicon-remove'></i>",
      "</a>     "
    ].join('');
  }
	//可编辑列表新增一行
function addRow(reportTableId){
    var rows = [];
    $("#"+reportTableId).bootstrapTable('append',rows);


}
function removeRow(deleteIndex,reportTableId){
	$("#"+reportTableId).bootstrapTable('removeRow', deleteIndex);

}

