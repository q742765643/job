<br/>
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">
<div class="form-group">
    <div >
        <label for="modelName" class="col-sm-2 control-label">算法可执行程序</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" name="exePath" value="${productParam.exePath}" >
        </div>
    </div>
</div>
<div class="form-group">
    <div >
        <label for="modelName" class="col-sm-2 control-label"> 算法源代码</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" name="scriptFile" value="${productParam.scriptFile}" >
        </div>
    </div>
</div>
<div class="form-group">
    <div >
        <label for="modelName" class="col-sm-2 control-label">inputXml存放路径</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" name="inputxml" value="${productParam.inputxml}" >
        </div>
    </div>
</div>
<div class="form-group">
    <div >
        <label for="modelName" class="col-sm-2 control-label">产品算法名称</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" name="prodname" value="${productParam.prodname}" >
        </div>
    </div>
</div>

<div class="form-group">
    <label for="modelName" class="col-sm-2 control-label">数据类型</label>
    <div class="col-sm-4">
        <label  class="control-label">
            <input type="radio" name="dateType"  value="1"> 实时数据
        </label>
        <label class="control-label">
            <input type="radio" name="dateType"  value="2"> 历史数据
        </label>
    </div>
	<div id="divRangeDayShow">   
		<label for="modelName" class="col-sm-2 control-label">天数</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" name="productRangeDay" id="productRangeDay" value="${productParam.productRangeDay}" >
		</div>
	</div>
	<div id="divRangeDateShow">   
		<label for="modelName" class="col-sm-2 control-label">开始日期</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" name="productRangeDate" id="productRangeDate" value="${productParam.productRangeDate}"  readonly >
		</div>
	</div>
	
</div>

<script>
     $("input[name='dateType']").click(function(){
        var val=$("input[name='dateType']:checked").val();
        if(2 == val){
			$('#divRangeDateShow').show();
			$('#divRangeDayShow').hide();
        }else{
            $('#divRangeDateShow').hide();
			$('#divRangeDayShow').show();
        }
    });
	
	if("${productParam.dateType}"==""){
		$("input[name='dateType'][value='1']").trigger("click");
    }else
    {
		$("input[name='dateType'][value='${productParam.dateType}']").prop("checked", true);
        $("input[name='dateType'][value='${productParam.dateType}']").trigger("click");
    }
</script>

<script>
    $('#productRangeDate').daterangepicker({
        autoApply:false,
        singleDatePicker:false,
        showDropdowns:false,        // 是否显示年月选择条件
        timePicker: true, 			// 是否显示小时和分钟选择条件
        //timePickerIncrement: 10, 	// 时间的增量，单位为分钟
        timePicker24Hour : true,
        drops:'down',
        opens : 'right', //日期选择框的弹出位置
        ranges: {
            //'最近1小时': [moment().subtract(1, 'hours'), moment()],
            //'今日': [moment().startOf('day'), moment().endOf('day')],
            //'昨日': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
            '最近7日': [moment().subtract(6, 'days'), moment()],
            '最近30日': [moment().subtract(29, 'days'), moment()],
            '本月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        },
        locale : {
            format: 'YYYY-MM-DD HH:mm',
//             format: 'YYYY-MM-DD',
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
        },

    });
</script>