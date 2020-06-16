var tasktype = 2;
var  myVar = {
    columns:[],
    element:'',
    station:'',
    data:[],
    id:'',
    flag:false
};

var jobTable;
//要素、站号初始化获取数据

function defult(){
	var element =$.ajax({
        type: "post",
        url: base_url + "/ywsjgl/findAllElement",
        data:{},
        success: function (data) {
        	myVar.data = data
	        data.forEach(function (item,index) {
	           $("#element").append('<option value="'+item.id+'">'+item.nickName+'</option>');
	           if(index == 0){
	        	   myVar.element = item.id
	        	   console.log(myVar.element)
	            }        
	        });
        	station()    
	    },
        complete:function () {
        	
        }
    });
}


	function station() {
		$.ajax({
		    type: "post",
		    url: base_url + "ywsjgl/findStationsByName",
		    data:{
		    	name:myVar.element  
		    },
		    success: function (data) {
		    	if(data){
		    		$("#station").empty();
		    		$("#station").append('<option value="-1">全部</option>');
		    		data.forEach(function (item,index) {
		      		  $("#station").append('<option value="'+item+'">'+item+'</option>');
		      		 if(index == 0){
		      			myVar.station = '-1'
		             }
		      		  
		      	   })
		    	}
		    	columns();
		    	if(myVar.flag == false){
		    		tableData();	
		    	}
		    },
		    complete:function () {
		     
		    }
		}); 
	}

	function columns(){
	 myVar.columns = []
   	  if(myVar.data){
   	    	 myVar.data.forEach(function (item,index) {
   		       if(myVar.element == item.id){
   		    	   let columnsArray = item.excelHeaders
   		    	   console.log(columnsArray)
   		    	   for(var i=0;i<columnsArray.length;i++){
   		    		   myVar.columns.push({
   		                   'title':columnsArray[i].value,
   		                   'data':columnsArray[i].key,
   		                   "visible": true
   		               })
   		    	   }
   		     	 
   		       }           
   		     });
   	     }
	}


defult();

$('#element').change(function () {
    myVar.element = $('#element option:selected').val();
    console.log(myVar.element)
    station();
    myVar.flag = true
});

$('#station').change(function () {
    myVar.station = $('#station option:selected').val();
});

function tableData(){
	if(jobTable){
	    jobTable.fnDestroy();
	    $('#createTable').empty();
	    $('#createTable').append("<table id='joblog_list' class='table table-bordered table-striped' style='width: 100%'></table>");
	}
	//清空table数据
	$("#colTb").html("");
	$('#theadTitle').html();
	jobTable = $("#joblog_list").dataTable({
	    "deferRender": true,
	    "processing": true,
	    "serverSide": true,
	    'bStateSave': true,
	    "searching": false,
	    "ordering": false,
	    "ajax": {
	        type: "post",
	        url: base_url + "ywsjgl/findAllByName",
	        data : function ( d ) {
	            var obj = {};
	            obj.name = myVar.element;
	            obj.params = 'area_station:' + myVar.station
	            obj.pageNum = (d.start/d.length)+1;
	            obj.pageSize = d.length;
	            return obj;
	        },
	        "dataSrc": function ( data ) {
	            return data.dataList;
	        },
	    },

	    //"scrollX": false,
	    // "destroy": true,
	    //显示序号
	    // "fnDrawCallback": function () {
	    //     this.api().column(0).nodes().each(function (cell, i) {
	    //         cell.innerHTML = i + 1;
	    //     });
	    // },
	    "columns": myVar.columns,
	    "language" : {
	        "sProcessing" : "处理中...",
	        "sLengthMenu" : "每页 _MENU_ 条记录",
	        "sZeroRecords" : "没有匹配结果",
	        "sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
	        "sInfoEmpty" : "无记录",
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
	    },
	});
	
    // jobTable.fnDraw(false);
}


// 搜索按钮
$('#searchBtn').on('click', function () {
	tableData();
});

$('#exportBtn').click(function(){
//	location.href = base_url + "ywsjgl/exportData?pageNum=3&pageSize=20&name="+myVar.element+"&params=area_station:" + myVar.station+""
	location.href = base_url + "ywsjgl/exportData?name="+myVar.element+"&params=area_station:" + myVar.station+""	
		
})


