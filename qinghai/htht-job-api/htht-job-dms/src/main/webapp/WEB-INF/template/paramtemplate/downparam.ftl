
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">

<div class="form-group">
    <div >
    <label for="modelName" class="col-sm-2 control-label">下载源目录类型</label>
    <div class="col-sm-4">
        <label  class="control-label">
            <input type="radio" name="forSouceType"  value="ftp" checked> FTP
        </label>
        <label class="control-label">
            <input type="radio" name="forSouceType"  value="file" > 文件系统
        </label>
        <label class="control-label">
            <input type="radio" name="forSouceType"  value="sftp" > SFTP
        </label>
        <label class="control-label">
            <input type="radio" name="forSouceType"  value="modis" > MODIS官方产品
        </label>
    </div>
    <div id="downloadSourcePath">
        <label for="modelName" class="col-sm-2 control-label">下载源目录</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="forPath"  value="${downParam.forPath}" placeholder="下载源目录" maxlength="500" >
        </div>
    </div>

    <div id="modisSourceType" style="display: none">
        <label for="modelName" class="col-sm-2 control-label">MODIS数据类型</label>
        <div class="col-sm-4">
            <select class="form-control" name="modisDataType" value="${downParam.modisDataType}">
                <option value="MOD09A1">MOD09A1</option>
                <option value="MOD09GA">MOD09GA</option>
                <option value="MOD09GQ">MOD09GQ</option>
                <option value="MOD09Q1">MOD09Q1</option>
                <option value="MOD10A1">MOD10A1</option>
                <option value="MOD11A1">MOD11A1</option>
                <option value="MOD11A2">MOD11A2</option>
                <option value="MOD13A1">MOD13A1</option>
                <option value="MOD13A2">MOD13A2</option>
                <option value="MOD13Q1">MOD13Q1</option>
                <option value="MOD15A2">MOD15A2</option>
                <option value="MOD17A2">MOD17A2</option>
                <option value="MODTBGA">MODTBGA</option>
                <option value="MOD04_3K">MOD04_3K</option>
                <option value="MYD11A2">MYD11A2</option>
            </select>
        </div>
    </div>
    </div>
</div>
<div class="form-group">
    <div id="forFtpDiv">
        <label for="modelName" class="col-sm-2 control-label">选择FTP:</label>
        <div class="col-sm-4">
            <input name="forFtp" class="form-control" id="forFtp" value="${downParam.forFtp}"/>
        </div>
    </div>

</div>
<div class="form-group">
    <div >
    <label for="modelName" class="col-sm-2 control-label">下载目录类型</label>
    <div class="col-sm-4">
        <label  class="control-label">
            <input type="radio" name="toSouceType"  value="ftp"  checked> FTP
        </label>
        <label class="control-label">
            <input type="radio" name="toSouceType"  value="file" > 文件系统
        </label>
        <label class="control-label">
            <input type="radio" name="toSouceType"  value="sftp" > SFTP
        </label>
    </div>
    <label for="modelName" class="col-sm-2 control-label">下载目标目录</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="toPath"  value="${downParam.toPath}"  placeholder="请输入目标目录路径" maxlength="500" >
    </div>
    <div id="toFtpDiv">
    <label for="modelName" class="col-sm-2 control-label">选择FTP:</label>
    <div class="col-sm-4">
        <input name="toFtp" class="form-control" id="toFtp" value="${downParam.toFtp}"/>
    </div>
    </div>
    </div>
</div>
<div class="form-group">
    <label for="modelName" class="col-sm-2 control-label">数据类型</label>
    <div class="col-sm-4">
        <label  class="control-label">
            <input type="radio" name="downloadType"  value="now"  checked> 实时数据
        </label>
        <label class="control-label">
            <input type="radio" name="downloadType"  value="history" > 历史数据
        </label>
    </div>
    <label for="modelName" class="col-sm-2 control-label">文件正则</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="downFileNamePattern"  value="${downParam.downFileNamePattern}"  maxlength="500" >
    </div>

</div>
<div class="form-group">
    <div id="downloadDaysDiv" >
        <label for="modelName" class="col-sm-2 control-label">下载天数</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="downloadDays" value="${downParam.downloadDays}"  placeholder="请输入下载天数" maxlength="50" >
        </div>
    </div>
    <div id="downloadDateDiv">
        <label for="modelName" class="col-sm-2 control-label">下载日期</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="downloadDate" id="downloadDate" value="${downParam.downloadDate}" readonly >
        </div>
    </div>
    <label for="modelName" class="col-sm-2 control-label"> 文件名时间格式</label>
    <div class="col-sm-4">
        <input type="text" class="form-control" name="dataTimePattern" value="${downParam.dataTimePattern}" >
    </div>
</div>
<div class="form-group">
    <div >
        <label for="modelName" class="col-sm-2 control-label"> 重命名格式</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="downloadFileName" value="${downParam.downloadFileName}" >
        </div>
    </div>
</div>
<div class="form-group">
    <div >
    <label for="modelName" class="col-sm-2 control-label">备注</label>
    <div class="col-sm-10">
        <textarea class="form-control" name="bz">${downParam.bz}</textarea>
    </div>
    </div>
</div>
<script>
    $("input[name='forSouceType']").click(function(){

         var val=$("input[name='forSouceType']:checked").val();
         if(val!='file'){
             $('#forFtpDiv').show();
         }else{
             $('#forFtpDiv').hide();
         }

        if(val!='modis'){
            $('#downloadSourcePath').show();
            $('#modisSourceType').hide();
        }else{
            $('#downloadSourcePath').hide();
            $('#forFtpDiv').hide();
            $('#modisSourceType').show();
        }
    });
    $("input[name='toSouceType']").click(function(){
        var val=$("input[name='toSouceType']:checked").val();
        if(val!='file'){
            $('#toFtpDiv').show();
        }else{
            $('#toFtpDiv').hide();
        }
    });

    $("input[name='downloadType']").click(function(){
        var val=$("input[name='downloadType']:checked").val();
        if(val=='now'){
            $('#downloadDateDiv').hide();
            $('#downloadDaysDiv').show();
        }else{
            $('#downloadDaysDiv').hide();
            $('#downloadDateDiv').show();
        }
    });
    var data={url:"/ftp/findSelectFtp"};
    $("#forFtp").bootstrapSelect(data);
    $("#toFtp").bootstrapSelect(data);

    if("${downParam.downloadType}"==""){
        $("input[name='downloadType'][value='now']").trigger("click");
    }else
    {
        //$("#timeFilter option[value='${downParam.timeFilter}']").prop('selected', true);
        // $("#forFtp option[value='${downParam.forFtp}']").prop('selected', true);
        //$("#toFtp option[value='${downParam.toFtp}']").prop('selected', true);
        $("input[name='forSouceType'][value='${downParam.forSouceType}']").prop("checked", true);
        $("input[name='toSouceType'][value='${downParam.toSouceType}']").prop("checked", true);
        $("input[name='downloadType'][value='${downParam.downloadType}']").prop("checked", true);
        $("input[name='forSouceType'][value='${downParam.forSouceType}']").trigger("click");
        $("input[name='toSouceType'][value='${downParam.toSouceType}']").trigger("click");
        $("input[name='downloadType'][value='${downParam.downloadType}']").trigger("click");
         $("#modisSourceType option[value='${downParam.modisDataType}']").prop('selected', true);
        
    }

</script>

<script>
    $('#downloadDate').daterangepicker({
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
            '今日': [moment().startOf('day'), moment().endOf('day')],
            '昨日': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
            '最近7日': [moment().subtract(6, 'days'), moment()],
            '最近30日': [moment().subtract(29, 'days'), moment()],
            '本月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        },
        locale : {
            format: 'YYYY-MM-DD HH:mm:ss',
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