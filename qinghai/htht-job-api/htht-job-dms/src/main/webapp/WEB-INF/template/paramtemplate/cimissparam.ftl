
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">

<div class="form-group">
    <span style="height: 5px"></span>
    <div >
        <label for="modelName" class="col-sm-2 control-label">资料类型</label>
        <div class="col-sm-4">
            <select class="form-control" name="cimissDataType" id="cimissDataType">
                <option value="SURF">地面资料</option>
                <option value="UPAR">高空资料</option>
                <option value="OCEN">海洋资料</option>
                <option value="RADI">辐射资料</option>
                <option value="AGME">农气资料</option>
                <option value="NAFP">数值模式</option>
                <option value="CAWN">大气成分</option>
                <option value="HPXY">历史作用</option>
                <option value="DISA">气象灾害</option>
                <option value="PADA">雷达资料</option>
                <option value="SATE">卫星资料</option>
                <option value="SCEX">科考资料</option>
                <option value="SEVP">服务产品</option>
                <option value="OTHE">其他资料</option>
            </select>
        </div>
        <div id="downloadSourcePath">
            <label for="modelName" class="col-sm-2 control-label">资料名称</label>
            <div class="col-sm-4">
                <select class="form-control" name="dataCode" id="dataCode">
          <#--  <#list dataCode as cimissParam>
                <option value="${cimissParam}" >${cimissParam.dataCode}</option>
            </#list>-->
                </select>
            </div>
        </div>
    </div>
</div>
<div class="form-group">
    <#--<span style="height: 5px"></span>-->
    <div >
        <#--<label for="modelName" class="col-sm-2 control-label">intorface</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="interfaceId"  value="" maxlength="50" >
        </div>-->
        <label for="modelName" class="col-sm-2 control-label" hidden>时间</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="times"  value="" maxlength="50" >
        </div>
        <label for="modelName" class="col-sm-2 control-label" hidden>时间类型值</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="isopen"  value="" maxlength="50" >
        </div>

        <#--<label for="modelName" class="col-sm-2 control-label" hidden>行政区域编号</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="adminCodes"  value="" maxlength="50" >
        </div>-->

        <label for="modelName" class="col-sm-2 control-label" hidden>最小纬度</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="minLat"  value="" maxlength="50" >
        </div>

        <label for="modelName" class="col-sm-2 control-label" hidden>最小经度</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="minLon"  value="" maxlength="50" >
        </div>

        <label for="modelName" class="col-sm-2 control-label" hidden>最大纬度</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="maxLat"  value="" maxlength="50" >
        </div>

        <label for="modelName" class="col-sm-2 control-label" hidden>最大经度</label>
        <div class="col-sm-4" hidden>
            <input type="text" class="form-control" name="maxLon"  value="" maxlength="50" >
        </div>

        <label for="modelName" class="col-sm-2 control-label">生成文件格式</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="fileFormat"  value="${cimissParam.filename}"  placeholder="请输入生成文件格式" maxlength="50" >
            <script>
                $("input[name='fileFormat']").val("${cimissParam.filename}".split(".")[1]);
            </script>
        </div>

        <label for="modelName" class="col-sm-2 control-label">保存路径</label>
         <div class="col-sm-4">
             <input type="text" class="form-control" name="filePath"  value="${cimissParam.filePath}"  maxlength="50" >
          </div>

        <label for="modelName" class="col-sm-2 control-label" style="display: none">生成文件名称</label>
        <div class="col-sm-4" style="display: none">
            <input type="text" class="form-control" name="filename"  value=""  placeholder="请输入生成文件格式" maxlength="50"  >
        </div>
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
                    $("#cimissDataType").find("option").each(function () {
                        if($(this).val() == "${cimissParam.cimissDataType}"){
                            $(this).attr("selected","selected");
                            $.ajax({
                                type:"post",
                                url:base_url + "/dataCollect/cimissQuery",
                                data:{
                                    dataType:$(this).val()
                                },
                                success:function (data) {
                                    $("select[name = 'dataCode']").empty();
                                    if(data.length>0){
                                        for(var i=0;i<data.length;i++){
                                            if(data[i].dataCode =="${cimissParam.dataCode}"){
                                                $("#dataCode").append(" <option selected='selected' value=" + data[i].dataCode + ">" + data[i].dataInfo + "</option>");
                                            }else{
                                              $("#dataCode").append(" <option value="+data[i].dataCode+">"+data[i].dataInfo+"</option>");
                                            }
                                        }
                                       /*$("#dataCode").find("option").each(function () {
                                            if($(this).val() == "${cimissParam.dataCode}"){
                                                $(this).selected = true;
                                            }
                                        });*/
                                    }
                                }
                            })
                        }
                    })
                    $("#bz").val("${cimissParam.bz}")
                }else{
                      $.ajax({
                            type:"post",
                            url:base_url + "/dataCollect/cimissQuery",
                            data:{
                                dataType:$("select[name='cimissDataType'] option:selected").val()
                            },
                            success:function (data) {
                                 $("select[name = 'dataCode']").empty();
                                if(data.length>0){
                                    for(var i=0;i<data.length;i++){
                                             $("select[name = 'dataCode']").append(" <option value="+data[i].dataCode+">"+data[i].dataInfo+"</option>");
                                     }
                                }
                             }
                         })
                }

            </script>
        </div>
    </div>
    <div>
        <label for="modelName" class="col-sm-2 control-label"> 要素字段</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" name="elements" value="${cimissParam.elements}" >
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
            $("input[name='downloadDate']").val(currentdate+"  00:00:00 *"+currentdate+"  59:59:59");
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
    var data={url:"/ftp/findSelectFtp"};
    $("#forFtp").bootstrapSelect(data);
    $("#toFtp").bootstrapSelect(data);
    if("${downParam.downloadType}"==""){
        $("input[name='downloadType'][value='now']").trigger("click");
    }else{
        //$("#timeFilter option[value='${downParam.timeFilter}']").prop('selected', true);
        // $("#forFtp option[value='${downParam.forFtp}']").prop('selected', true);
        //$("#toFtp option[value='${downParam.toFtp}']").prop('selected', true);
        $("input[name='forSouceType'][value='${downParam.forSouceType}']").prop("checked", true);
        $("input[name='toSouceType'][value='${downParam.toSouceType}']").prop("checked", true);
        $("input[name='downloadType'][value='${downParam.downloadType}']").prop("checked", true);
        $("input[name='forSouceType'][value='${downParam.forSouceType}']").trigger("click");
        $("input[name='toSouceType'][value='${downParam.toSouceType}']").trigger("click");
        $("input[name='downloadType'][value='${downParam.downloadType}']").trigger("click");
    }




    $("select[name = 'cimissDataType']").change(function () {
        $.ajax({
            type:"post",
            url:base_url + "/dataCollect/cimissQuery",
            data:{
                dataType:$("select[name = 'cimissDataType'] option:selected").val()
            },
            success:function (data) {
                console.log(data)
                $("select[name = 'dataCode']").empty();
                if(data.length>0){
                    for(var i=0;i<data.length;i++){
                        $("select[name = 'dataCode']").append(" <option value="+data[i].dataCode+">"+data[i].dataInfo+"</option>");
                    }
                }
            }
        })
        /*$.post(base_url + "/dataCollect/cimissQuery", $("select[name = 'cimissDataType'] option:selected").val(), function(data) {
            console.log(data)
            if(data.data.length>0){
                console.log(data.data[0].menu)
                $("select[name = 'dataCode']").empty()
                for(var i=0;i<data.data.length;i++){
                    console.log(data.data[i].menu)
                    $("select[name = 'dataCode']").append("<option value="+data.data[i].menuId+">"+data.data[i].menu+"</option>");
                }
            }else{
                alert("error")
            }
        });*/
    });


</script>

<script>
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
//             format: 'YYYY-MM-DD',
            separator : ' * ',
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
</script>