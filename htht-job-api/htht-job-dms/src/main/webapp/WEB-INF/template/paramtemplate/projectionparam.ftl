
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">
<br/>
<div class="form-group">
	<label for="modelName" class="col-sm-2 control-label">任务名称:</label>

    <div class="col-sm-4">
        <!-- <input type="text" class="form-control" name="preDataTaskName"  value="${projectionParam.preDataTaskName}" placeholder="卫星_传感器_分辨率" maxlength="50" > -->
		<select class="form-control" id="preDataTaskName" name="preDataTaskName">
			<option value="FY3A_MERSI_0250">FY3A_MERSI_0250</option>
			<option value="FY3A_MERSI_1000">FY3A_MERSI_1000</option>
			<option value="FY3A_VIRRX_1000">FY3A_VIRRX_1000</option>
			<option value="FY3B_MERSI_0250">FY3B_MERSI_0250</option>
			<option value="FY3B_MERSI_1000">FY3B_MERSI_1000</option>
			<option value="FY3B_VIRRX_1000">FY3B_VIRRX_1000</option>
			<option value="FY3C_VIRRX_1000">FY3C_VIRRX_1000</option>
			<option value="FY3C_MERSI_0250">FY3C_MERSI_0250</option>
			<option value="FY3C_MERSI_1000">FY3C_MERSI_1000</option>
			<option value="FY3D_MERSI_1000">FY3D_MERSI_ALL</option>
			<option value="FY4A_AGRI_4000">FY4A_AGRI_ALL</option>
			<option value="EOSA_MODIS_MOD02QK">EOSA_MODIS_0250</option>
			<option value="EOSA_MODIS_MOD02HK">EOSA_MODIS_0500</option>
			<option value="EOSA_MODIS_MOD021K">EOSA_MODIS_1000</option>
			<option value="EOST_MODIS_MOD02QK">EOST_MODIS_0250</option>
			<option value="EOST_MODIS_MOD02HK">EOST_MODIS_0500</option>
			<option value="EOST_MODIS_MOD021K">EOST_MODIS_1000</option>
			<option value="H08_AHI_1000">H08_AHI_ALL</option>
		</select>
    </div>
    <label for="modelName" class="col-sm-2 control-label">算法位置:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="projectionExeLocaiton"  value="${projectionParam.projectionExeLocaiton}" placeholder="算法位置" maxlength="100" >
    </div>
</div>
<!--
<div class="form-group">
    <label for="modelName" class="col-sm-2 control-label">输入路径:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="inputDataFilePath"  value="${projectionParam.inputDataFilePath}" placeholder="输入路径" maxlength="500" >
    </div>
	<label for="modelName" class="col-sm-2 control-label">文件名规则:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="fileNamePattern"  value="${projectionParam.fileNamePattern}" placeholder="输出路径" maxlength="500" >
    </div>
</div>
-->
<div class="form-group">
    <label for="modelName" class="col-sm-2 control-label">输出路径:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="outputDataFilePath"  value="${projectionParam.outputDataFilePath}" placeholder="输出路径" maxlength="500" >
    </div>
	<label for="modelName" class="col-sm-2 control-label">数据有效范围:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="validEnvelopes"  value="${projectionParam.validEnvelopes}" placeholder="数据有效范围" maxlength="5000" >
    </div>
</div>
<div class="form-group">
	<label for="modelName" class="col-sm-2 control-label">输出数据格式:</label>
	<div class="col-sm-4">
		<select class="form-control" id="preDataFormate" name="formate">
			<option value="LDF" selected="selected">LDF</option>
			<option value="TIFF">TIFF</option>
		</select>
	</div>
	<label for="modelName" class="col-sm-2 control-label">输出数据范围:</label>
	<div class="col-sm-4">
		<input type="text" class="form-control" name="envelopes"
			value="${projectionParam.envelopes}" placeholder="空值时输出整个文件"
			maxlength="5000">
	</div>
</div>
<div class="form-group">
	<label for="modelName" class="col-sm-2 control-label">卫星通道:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="bands"  value="${projectionParam.bands}" placeholder="卫星通道" maxlength="100" >
    </div>
	 <label for="modelName" class="col-sm-2 control-label">投影标识:</label>
    <div class="col-sm-4">
        <select class="form-control" id="projectionIdentify" name="projectionIdentify">
			<option value="GLL">等经纬度投影</option>
			<option value="ABS">阿尔波斯投影</option>
		</select>
    </div>
</div>
<div class="form-group">
	<label for="modelName" class="col-sm-2 control-label">经度方向分辨率:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="resolutionX"  value="${projectionParam.resolutionX}" placeholder="经度方向分辨率" maxlength="100" >
    </div>
	<label for="modelName" class="col-sm-2 control-label">纬度方向分辨率:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="resolutionY"  value="${projectionParam.resolutionY}" placeholder="纬度方向分辨率" maxlength="100" >
    </div>
</div>
<div class="form-group">
   <label for="modelName" class="col-sm-2 control-label">类型:</label>
    <div class="col-sm-4">
        <label  class="control-label">
            <input type="radio" name="dateType"  value="1"  checked> 实时数据
			<input type="hidden" class="form-control" name="rangeDay"  value="1">
        </label>
        <label class="control-label">
            <input type="radio" name="dateType"  value="2" > 历史数据
        </label>
    </div>
    <label for="modelName" class="col-sm-2 control-label">xml路径:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="projectionInputArgXml"  value="${projectionParam.projectionInputArgXml}" placeholder="算法输入路径" maxlength="100" >
    </div>
	
</div>
<div class="form-group">
	<div id="projectionDateShow">
		<label for="modelName" class="col-sm-2 control-label">数据时间:</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" name="projectioDate" id="projectioDate" value="${projectionParam.projectioDate}" readonly >
		</div>
	</div>
	<div id="projectionDaysDiv" >
        <label for="modelName" class="col-sm-2 control-label">处理天数</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="downloadDays" value="${downParam.downloadDays}"  placeholder="请输入处理天数" maxlength="100" >
        </div>
    </div>
     <label for="modelName" class="col-sm-2 control-label">补充参数:</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="extArgs"  value="${projectionParam.extArgs}" placeholder="补充参数按需填写" maxlength="100" >
    </div>
</div>

<script>
    $("input[name='dateType']").click(function(){
        var val=$("input[name='dateType']:checked").val();
        if(val=='1'){
            $('#projectionDateShow').hide();
            $('#projectionDaysDiv').show();
        }else{
            $('#projectionDateShow').show();
            $('#projectionDaysDiv').hide();
        }
    });
	
	 if("${projectionParam.dateType}"==""){
        $("input[name='dateType'][value='1']").trigger("click");
    }else
    {
        $("input[name='dateType'][value='${projectionParam.dateType}']").prop("checked", true);
        $("input[name='dateType'][value='${projectionParam.dateType}']").trigger("click");
    }
	if(true){
		var select = document.getElementById("preDataTaskName");  
		 for (var i = 0; i < select.options.length; i++){  
			if (select.options[i].value == "${projectionParam.preDataTaskName}"){  
				select.options[i].selected = true;  
				break;  
			}  
		}  
	}

	var selectFormate = document.getElementById("preDataFormate");
	for (var i = 0; i < selectFormate.options.length; i++) {
		if (selectFormate.options[i].value == "${projectionParam.formate}") {
			selectFormate.options[i].selected = true;
			break;
		}
	}
</script>

<script>
    $('#projectioDate').daterangepicker({
        autoApply:false,
        singleDatePicker:false,
        showDropdowns:false,        // 是否显示年月选择条件
        //timePicker: true, 			// 是否显示小时和分钟选择条件
        //timePickerIncrement: 10, 	// 时间的增量，单位为分钟
        //timePicker24Hour : true,
        drops:'down',
        opens : 'right', //日期选择框的弹出位置
        ranges: {
            //'最近1小时': [moment().subtract(1, 'hours'), moment()],
            //'今日': [moment().startOf('day'), moment().endOf('day')],
            //'昨日': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
            //'最近7日': [moment().subtract(6, 'days'), moment()],
           // '最近30日': [moment().subtract(29, 'days'), moment()],
            //'本月': [moment().startOf('month'), moment().endOf('month')],
            //'上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        },
        locale : {
            format: 'YYYY-MM-DD',
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