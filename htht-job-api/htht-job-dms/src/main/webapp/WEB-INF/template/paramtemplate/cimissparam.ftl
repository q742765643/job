
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">

<div class="form-group">
        <label for="modelName" class="col-sm-2 control-label">资料类型</label>
        <div class="col-sm-4">
            <select class="form-control" name="cimissDataTypeShow" id="cimissDataTypeShow">
            </select>
        </div>
        <label for="modelName" class="col-sm-2 control-label">资料名称</label>
        <div class="col-sm-4">
            <select class="form-control" name="dataCodeShow" id="dataCodeShow">
            </select>
        </div>
</div>
<div class="form-group">
    <label for="modelName" class="col-sm-2 control-label">访问接口</label>
    <div class="col-sm-4">
        <select class="form-control" name="interfaceIdShow" id="interfaceIdShow">
        </select>
    </div>
</div>
<div class="form-group">
    <label for="modelName" class="col-sm-2 control-label">时间类型</label>
    <div class="col-sm-4">
        <label  class="control-label">
            <input type="radio" name="timeType"  value="now"  checked id="now"> 时间点
        </label>
        <label class="control-label" >
            <input type="radio" name="timeType"  value="history" > 时间段
        </label>
    </div>
<#-- <label for="modelName" class="col-sm-2 control-label">区域类型</label>
 <div class="col-sm-4">
     <label  class="control-label">
         <input type="radio" name="reg"  value="latLon"  checked> 经纬度
     </label>
     <label class="control-label">
         <input type="radio" name="reg"  value="region" > 区域
     </label>
 </div>-->
</div>

<div class="form-group">
    <div id="downloadDaysDiv" >
        <label for="modelName" class="col-sm-2 control-label">时间点</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="downloadDays" id="downloadDays" value="${cimissParam.times}" readonly >
        </div>
    </div>
    <div id="downloadDateDiv" hidden>
        <label for="modelName" class="col-sm-2 control-label">时间段</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="downloadDate" id="downloadDate" value="${cimissParam.times}" readonly >
        </div>
    </div>
    <div id="latLon" style="display: none">
        <label for="modelName" class="col-sm-2 control-label"> 经纬度</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="latAndLon" value="" >
        </div>
    </div>
    <div id="region" >
        <label for="modelName" class="col-sm-2 control-label"> 行政区域编号</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="adminCodes" value="${cimissParam.adminCodes}" >
        </div>
    </div>
</div>

<div class="form-group">
    <label for="modelName" class="col-sm-2 control-label">要素字段代码</label>
    <div class="col-sm-4">
        <input class="form-control" name="elements" id="elements" value="${cimissParam.elements}">
    </div>
    <label for="modelName" class="col-sm-2 control-label">要素值范围</label>
    <div class="col-sm-4">
        <input class="form-control" name="eleValueRanges" id="eleValueRanges" value="${cimissParam.eleValueRanges}">
    </div>
</div>
<div class="form-group">
    <#--<span style="height: 5px"></span>-->
    <div >
        <#--<label for="modelName" class="col-sm-2 control-label">intorface</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="interfaceId"  value="" maxlength="100" >
        </div>-->
        <label for="modelName" class="col-sm-2 control-label" hidden>时间</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="times"  value="" maxlength="100" >
        </div>
        <label for="modelName" class="col-sm-2 control-label" hidden>时间类型值</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="isopen"  value="" maxlength="100" >
        </div>

        <#--<label for="modelName" class="col-sm-2 control-label" hidden>行政区域编号</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="adminCodes"  value="" maxlength="100" >
        </div>-->

        <label for="modelName" class="col-sm-2 control-label" hidden>最小纬度</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="minLat"  value="" maxlength="100" >
        </div>

        <label for="modelName" class="col-sm-2 control-label" hidden>最小经度</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="minLon"  value="" maxlength="100" >
        </div>

        <label for="modelName" class="col-sm-2 control-label" hidden>最大纬度</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="maxLat"  value="" maxlength="100" >
        </div>

        <label for="modelName" class="col-sm-2 control-label" hidden>最大经度</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="maxLon"  value="" maxlength="100" >
        </div>

        <label for="modelName" class="col-sm-2 control-label">生成文件格式</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="fileFormat"  value="${cimissParam.filename}"  placeholder="请输入生成文件格式" maxlength="100" >
            <script>
                $("input[name='fileFormat']").val("${cimissParam.filename}".split(".")[1]);
            </script>
        </div>

        <label for="modelName" class="col-sm-2 control-label">保存路径</label>
         <div class="col-sm-4">
             <input type="text" class="form-control" name="filePath"  value="${cimissParam.filePath}"  maxlength="100" >
          </div>

        <label for="modelName" class="col-sm-2 control-label" style="display: none">生成文件名称</label>
        <div class="col-sm-4" style="display: none">
            <input type="text" class="form-control" name="filename"  value=""  placeholder="请输入生成文件格式" maxlength="100"  >
        </div>
    </div>
</div>
<div class="form-group">
    <div>
        <label for="modelName" class="col-sm-2 control-label"> 重命名</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="filePrefix" value="${cimissParam.filename}" >
            <script>

                if("${cimissParam.isopen}" !=""){
                    $("input[name='filePrefix']").val("${cimissParam.filename}".split(".")[0]);
                    if(${cimissParam.isopen} == true){
                        $('#downloadDateDiv').hide();
                        $('#downloadDaysDiv').show();
                        $("input[name='timeType'][value='now']").attr("checked","checked")
                    }else{
                        $('#downloadDaysDiv').hide();
                        $('#downloadDateDiv').show();
                        $("input[name='timeType'][value='history']").attr("checked","checked")
                    }
                    $("#bz").val("${cimissParam.bz}")
                }

            </script>
        </div>
    </div>
</div>
<div class="form-group">
    <div >
        <label for="modelName" class="col-sm-2 control-label">备注</label>
        <div class="col-sm-10">
            <textarea class="form-control" name="bz" value="${cimissParam.bz}" id="bz"></textarea>
        </div>
    </div>
</div>
<input type="hidden" name="cimissDataType" id="cimissDataType">
<input type="hidden" name="dataCode" id="dataCode">
<input type="hidden" name="interfaceId" id="interfaceId">
<script>


    var cimissDataTypeShow='${cimissParam.cimissDataTypeShow}';
    var dataCodeShow='${cimissParam.dataCodeShow}';
    var interfaceIdShow='${cimissParam.interfaceIdShow}';

    var beginTimeStore = '';
    $('#downloadDate').daterangepicker({
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
        },

    });
    $('#downloadDays').daterangepicker(
            {
                singleDatePicker: true,//设置为单个的datepicker，而不是有区间的datepicker 默认false
                showDropdowns: true,//当设置值为true的时候，允许年份和月份通过下拉框的形式选择 默认false
                autoUpdateInput: false,//1.当设置为false的时候,不给与默认值(当前时间)2.选择时间时,失去鼠标焦点,不会给与默认值 默认true
                timePicker24Hour : true,//设置小时为24小时制 默认false
                timePicker : false,//可选中时分 默认false
                locale: {
                    format: "YYYY-MM-DD",
                    separator: " - ",
                    daysOfWeek: ["日","一","二","三","四","五","六"],
                    monthNames: ["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"]
                }

            }
    ).on('cancel.daterangepicker', function(ev, picker) {
        $("#downloadDays").val("请选择日期");
        $("#submitDate").val("");
    }).on('apply.daterangepicker', function(ev, picker) {
        $("#submitDate").val(picker.startDate.format('YYYY-MM-DD HH:mm:ss'));
        $("#downloadDays").val(picker.startDate.format('YYYY-MM-DD HH:mm:ss'));
    });

    $.ajax({
        url: base_url + '/template/findChildren?dictName=资料类别',
        dataType: "json",
        success: function (data) {
            $("#cimissDataTypeShow").empty();
            for (var i = 0; i < data.length; i++) {
                if(data[i].id==cimissDataTypeShow){
                    $('#cimissDataTypeShow').append("<option value=" + data[i].id + " selected='selected'>" + data[i].text + "</option>");
                }else {
                    $('#cimissDataTypeShow').append("<option value=" + data[i].id + ">" + data[i].text + "</option>");
                }
            }
            $("#cimissDataTypeShow").val(cimissDataTypeShow);

            $('#cimissDataTypeShow').trigger("change");

        }
    });
    $("#cimissDataTypeShow").change(function(){
        var parentId=$("#cimissDataTypeShow").val();
        addOption("dataCodeShow",parentId,dataCodeShow);
        var text=$("#cimissDataTypeShow").find("option:selected").text();
        $("#cimissDataType").val(text.split("(")[0]);

    });
    $("#dataCodeShow").change(function(){
        var parentId=$("#dataCodeShow").val();
        addOption("interfaceIdShow",parentId,interfaceIdShow);
        var text=$("#dataCodeShow").find("option:selected").text();
        $("#dataCode").val(text.split("(")[0]);
    });
    $("#interfaceIdShow").change(function(){
        var text=$("#interfaceIdShow").find("option:selected").text();
        $("#interfaceId").val(text.split("(")[0]);
    });

    function addOption(id,parentId,value){
        $.ajax({
            url: base_url + '/template/findByParentId?parentId='+parentId,
            dataType: "json",
            async : true,
            success: function (data) {
                $("#"+id).empty();
                for (var i = 0; i < data.length; i++) {
                    if(data[i].id==value){
                        $("#"+id).append("<option value=" + data[i].id + " selected='selected'>" + data[i].text + "</option>");

                    }else{
                        $("#"+id).append("<option value=" + data[i].id + ">" + data[i].text + "</option>");
                    }
                }
                $("#"+id).trigger("change");

            }
        });
    }
    $("input[name='timeType']").click(function(){
        var date = new Date();

        var seperator1 = "-";
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }

        var currentdate = year + seperator1 + month + seperator1 + strDate;
        var val=$("input[name='timeType']:checked").val();
        if(val=='now'){
            $("input[name='downloadDays']").val(currentdate+"  00:00:00");
            $('#downloadDateDiv').hide();
            $('#downloadDaysDiv').show();
        }else{
            $("input[name='downloadDate']").val(currentdate+"  00:00:00 -"+currentdate+"  59:59:59");
            $('#downloadDaysDiv').hide();
            $('#downloadDateDiv').show();
        }
    });
    $("input[name='reg']").click(function(){
        var val=$("input[name='reg']:checked").val();
        if(val=='latLon'){
            $('#region').hide();
            $('#latLon').show();
        }else{
            $('#latLon').hide();
            $('#region').show();
        }
    });


</script>