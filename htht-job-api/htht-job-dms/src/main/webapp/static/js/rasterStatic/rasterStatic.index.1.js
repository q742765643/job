var tasktype = 2;
var  myVar = {
    startDate:'',
    endDate:'',
    columns:[],
    farmLevel:'',
    level:'',
    wheat:'',
    farmType:'0',
    farmTypeValue:[],
    regionId:'370000000000',
    productId:'5984f49f14724c10a6a4268e028bf361',
    flag:false
};
// 过滤时间
$('#filterTime').daterangepicker({
    autoApply:false,
    singleDatePicker:false,
    showDropdowns:true,        // 是否显示年月选择条件
    timePicker: true, 			// 是否显示小时和分钟选择条件
    timePickerIncrement: 10, 	// 时间的增量，单位为分钟
    timePicker24Hour : true,
    opens : 'left', //日期选择框的弹出位置
    ranges: {
        /*'最近1小时': [moment().subtract(1, 'hours'), moment()],
        '今日': [moment().startOf('day'), moment().endOf('day')],
        '昨日': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],*/
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
}, function (t, e) {
    myVar.startDate = t.format("YYYY-MM-DD HH:mm:ss");
    myVar.endDate = e.format("YYYY-MM-DD HH:mm:ss");
});
//默认时间
myVar.startDate = moment().subtract("days", 0).format("YYYY-MM-DD") + ' '+ '00:00:00';
myVar.endDate = moment().format("YYYY-MM-DD") + ' ' + '23:59:59';
var jobTable;
//省、类型初始化获取数据
function initializeData() {
    myVar.columns = [];
    queryProvince = $.ajax({
        type: "post",
        url: base_url + "/rasterStatic/regionList",
        data:{
            regionId:myVar.regionId
        },
        success: function (data) {
            // console.log(data);
            myVar.columns.push(
                {
                    'title':'时间',
                    'data':'issue',
                    "visible": true
                }
            );
            data.data.forEach(function (item,index) {
                if(myVar.flag == false){
                    $("#regionid").append('<option value="'+item.regionId+'">'+item.areaName+'</option>');
                }
                myVar.columns.push(
                    {
                        'title':item.areaName,
                        'data':item.regionId,
                        "visible": true
                    }
                )
            });
            console.log(myVar.columns)
        },
        complete:function () {
            myVar.flag = true;
        }
    });

    $.when(queryProvince).done(function (){
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
                url: base_url + "/rasterStatic/pageList",
                data : function ( d ) {
                    var obj = {};
                    obj.regionId = $('#regionid option:selected').val();
                    obj.productId = myVar.productId;
                    obj.level = myVar.farmType;
                    obj.cycle = $('#period option:selected').val();
                    obj.startDate = myVar.startDate;
                    obj.endDate = myVar.endDate;
                    obj.start = d.start;
                    obj.length = d.length;
                    return obj;
                },
                "dataSrc": function ( data ) {
                    // for(i=0;i<data.data.length;i++){
                    //     data.data[i].issue =  moment(new Date(data.data[i].issue)).format("YYYY-MM-DD HH:mm:ss");
                    // }
                    console.log(data);
                    return data.data;
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
    });

}
function statisticTypeList() {
    queryType = $.ajax({
        type: "post",
        url: base_url + "/rasterStatic/statisticTypeList",
        success: function (data) {
            var dataChange = [];
            for(i=data.data.length;i>0;i--){
                dataChange.push(data.data[i-1])
            }
            dataChange.forEach(function (item,index) {
                $("#pType").append('<option value="'+item.productId+'">'+item.productName+'</option>');
                $("#pType option[value='5984f49f14724c10a6a4268e028bf361']").attr("selected","selected");
                myVar.farmTypeValue.push(item.productId)
                if(index == '0'){
                    myVar.level = item.level;
                    for(var i in myVar.level){
                        $("#farmType").append('<option value="'+i+'">'+myVar.level[i]+'</option>');
                    }
                }
                if(index == '1'){
                    myVar.wheat = item.level;
                }
            })
        },
    });
}
initializeData();
statisticTypeList();

$('#regionid').change(function () {
    myVar.regionId = $('#regionid option:selected').val();
    // initializeData();
});
$('#pType').change(function () {
    $("#farmType").empty();
    myVar.productId = $('#pType option:selected').val();
    if($('#pType option:selected').val() == myVar.farmTypeValue[0]){
        for(var i in myVar.level){
            $("#farmType").append('<option value="'+i+'">'+myVar.level[i]+'</option>');
        }
    }
    else if($('#pType option:selected').val() == myVar.farmTypeValue[1]){
        for(var i in myVar.wheat){
            $("#farmType").append('<option value="'+i+'">'+myVar.wheat[i]+'</option>');
        }
    }
});
$('#farmType').change(function () {
    myVar.farmType = $('#farmType option:selected').val();
});

//根据条件生成表格
function init() {


}
init();

// 搜索按钮
$('#searchBtn').on('click', function () {
    initializeData();
    // jobTable.fnDraw(false);
});



