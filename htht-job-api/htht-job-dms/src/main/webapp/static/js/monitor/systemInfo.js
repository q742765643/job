$(function() {

    //初始化实际监控数据
	var myChart = echarts.init(document.getElementById('main'),"roma");
    var myChartSecond = echarts.init(document.getElementById('mainSecond'),"roma");
    var myChartMin = echarts.init(document.getElementById('mainMin'),"roma");
    var myChartHour = echarts.init(document.getElementById('mainHour'),"roma");
    var main_one = echarts.init(document.getElementById('main_one'),"roma");
    var main_two = echarts.init(document.getElementById('main_two'),"roma");
    var main_three = echarts.init(document.getElementById('main_three'),"roma");
	var now = new Date();
	var res = [];
    var resMin = [];
    var resHour = [];
    var dataSecendJVM=[];
    var dataSecendRAM=[];
    var dataSecendCPU=[];
    var dataMinJVM=[];
    var dataMinRAM=[];
    var dataMinCPU=[];
    var dataHourJVM=[];
    var dataHourRAM=[];
    var dataHourCPU=[];
    var ip;
    var option;
    var optionSecond;
    var optionMin;
    var optionHour;
    var one_option;
    var two_option;
    $('#ipselect').bootstrapSelect({
            url: base_url + '/monitor/findIpList',
            async : true,

    onLoadSuccess: function(data){
                var isRun = false;
                for (var i = 0; i < data.length; i++) {
                    if(data[i].isRun == "1"){
                        ip= data[i].id;
                        isRun = true;
                        break;
                    }
                }
                if(!isRun){
                    ip= data[0].id;
                }
                $('#ipselect').bootstrapSelect('select',ip);
                getChart();
            },
            onSelect : function(val,rec){
                ip=val;
                 now = new Date();
                 res = [];
                 resMin = [];
                 resHour = [];
                 dataSecendJVM=[];
                 dataSecendRAM=[];
                 dataSecendCPU=[];
                 dataMinJVM=[];
                 dataMinRAM=[];
                 dataMinCPU=[];
                 dataHourJVM=[];
                 dataHourRAM=[];
                 dataHourCPU=[];
                getChart();
            }
    });


    function getChart(){
        console.log(ip);
        $.ajax({
            type : "POST",
            url : base_url + '/monitor/usage',
            data:{"ip":ip},
            async : false,
            dataType : 'json',
            success : function(data) {
                if(data.content !== null){
                    var json={"cpuUsage":data.content.cpuUsage,
                        "ramUsage":data.content.memoryUsage,
                        "jvmUsage":data.content.jvmUsage};

                    var len = 20;
                    while (len--) {
                        var time = now.toLocaleTimeString().replace(/^\D*/, '');
                        time = time.substr(time.indexOf(":") + 1);
                        res.unshift(now.getHours()+":"+time);
                        dataSecendJVM.push( json.jvmUsage);
                        dataSecendRAM.push(json.ramUsage);
                        dataSecendCPU.push(json.cpuUsage);
                        now = new Date(now - 1000);
                    }
                    var lenMin = 60;
                    now = new Date();
                    while (lenMin--) {
                        var time = now.getHours()+":"+now.getMinutes();
                        resMin.unshift(time);
                        dataMinJVM.push( json.jvmUsage);
                        dataMinRAM.push(json.ramUsage);
                        dataMinCPU.push(json.cpuUsage);
                        now = new Date(now - 60000);
                    }
                    var lenHour = 24;
                    now = new Date();
                    while (lenHour--) {
                        var time = now.getHours()+":"+now.getMinutes();
                        resHour.unshift(time);
                        dataHourJVM.push( json.jvmUsage);
                        dataHourRAM.push(json.ramUsage);
                        dataHourCPU.push(json.cpuUsage);
                        now = new Date(now - 3600000);
                    }
                }

            },error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert(XMLHttpRequest.status);
                alert(XMLHttpRequest.readyState);
                alert(textStatus);
            }
        });
         option = {
            title : {
                text: '每秒',
                //subtext: '每秒'
            },
            legend: {
                data:['最高','最低']
            },
            calculable : true,
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            legend : {
                data : [ 'jvm内存使用率', '物理内存使用率', 'cpu使用率' ]
            },
            grid : {
                x : 40,
                y : 30,
                x2 : 10,
                y2 : 35,
                borderWidth : 0,
                borderColor : "#FFFFFF"
            },
            toolbox: {
                show : false,
                feature : {
                    mark : {show: true},
                    dataView : {show: true, readOnly: false},
                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                    restore : {show: true},
                    saveAsImage : {show: true}
                }
            },
            xAxis : [ {
                axisLabel : {
                    rotate : 20,
                },
                type : 'category',// 坐标轴类型，横轴默认为类目型'category'，纵轴默认为数值型'value'
                data : res
            } ],
            yAxis : [ {
                min : 0,
                max : 100,
                axisLabel : {
                    formatter : '{value}%'
                }
            } ],
            series : [
                {
                    name : 'jvm内存使用率',
                    type : 'line',
                    data : dataSecendJVM
                },
                {
                    name : '物理内存使用率',
                    type : 'line',
                    data :dataSecendRAM
                },
                {
                    name : 'cpu使用率',
                    type : 'line',
                    data : dataSecendCPU
                } ]
        };
         optionSecond = {
            title : {
                text: '每秒',
                //subtext: '每秒'
            },
            legend: {
                data:['最高','最低']
            },
            calculable : true,
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            legend : {
                data : [ 'jvm内存使用率', '物理内存使用率', 'cpu使用率' ]
            },
            grid : {
                x : 40,
                y : 30,
                x2 : 10,
                y2 : 35,
                borderWidth : 0,
                borderColor : "#FFFFFF"
            },
            toolbox: {
                show : false,
                feature : {
                    mark : {show: true},
                    dataView : {show: true, readOnly: false},
                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                    restore : {show: true},
                    saveAsImage : {show: true}
                }
            },
            xAxis : [ {
                axisLabel : {
                    rotate : 20,
                },
                type : 'category',// 坐标轴类型，横轴默认为类目型'category'，纵轴默认为数值型'value'
                data : res
            } ],
            yAxis : [ {
                min : 0,
                max : 100,
                axisLabel : {
                    formatter : '{value}%'
                }
            } ],
            series : [
                {
                    name : 'jvm内存使用率',
                    type:'bar',
                    data : dataSecendJVM
                },
                {
                    name : '物理内存使用率',
                    type:'bar',
                    data :dataSecendRAM
                },
                {
                    name : 'cpu使用率',
                    type:'bar',
                    data : dataSecendCPU
                } ]
        };
         optionMin = {
            title : {
                text: '每分钟',
                //subtext: ''
            },
            legend: {
                data:['最高','最低']
            },
            calculable : true,
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            legend : {
                data : [ 'jvm内存使用率', '物理内存使用率', 'cpu使用率' ]
            },
            grid : {
                x : 40,
                y : 30,
                x2 : 10,
                y2 : 35,
                borderWidth : 0,
                borderColor : "#FFFFFF"
            },
            toolbox: {
                show : false,
                feature : {
                    mark : {show: true},
                    dataView : {show: true, readOnly: false},
                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                    restore : {show: true},
                    saveAsImage : {show: true}
                }
            },
            xAxis : [ {
                axisLabel : {
                    rotate : 20,
                },
                type : 'category',// 坐标轴类型，横轴默认为类目型'category'，纵轴默认为数值型'value'
                data : resMin
            } ],
            yAxis : [ {
                min : 0,
                max : 100,
                axisLabel : {
                    formatter : '{value}%'
                }
            } ],
            series : [
                {
                    name : 'jvm内存使用率',
                    type : 'line',
                    data :dataMinJVM
                },
                {
                    name : '物理内存使用率',
                    type : 'line',
                    data :dataMinRAM
                },
                {
                    name : 'cpu使用率',
                    type : 'line',
                    data : dataMinCPU
                } ]
        };
         optionHour = {
            title : {
                text: '每小时',
                //subtext: '每小时'
            },
            legend: {
                data:['最高','最低']
            },
            calculable : true,
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            legend : {
                data : [ 'jvm内存使用率', '物理内存使用率', 'cpu使用率' ]
            },
            grid : {
                x : 40,
                y : 30,
                x2 : 10,
                y2 : 35,
                borderWidth : 0,
                borderColor : "#FFFFFF"
            },
            toolbox: {
                show : false,
                feature : {
                    mark : {show: true},
                    dataView : {show: true, readOnly: false},
                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                    restore : {show: true},
                    saveAsImage : {show: true}
                }
            },
            xAxis : [ {
                axisLabel : {
                    rotate : 20,
                },
                type : 'category',// 坐标轴类型，横轴默认为类目型'category'，纵轴默认为数值型'value'
                data : resHour
            } ],
            yAxis : [ {
                min : 0,
                max : 100,
                axisLabel : {
                    formatter : '{value}%'
                }
            } ],
            series : [
                {
                    name : 'jvm内存使用率',
                    type : 'line',
                    data : dataHourJVM,
                    markPoint : {
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    },
                    markLine : {
                        data : [
                            {type : 'average', name: '平均值'}
                        ]
                    }
                },
                {
                    name : '物理内存使用率',
                    type : 'line',
                    data : dataHourRAM,
                    markPoint : {
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    },
                    markLine : {
                        data : [
                            {type : 'average', name: '平均值'}
                        ]
                    }
                },
                {
                    name : 'cpu使用率',
                    type : 'line',
                    data : dataHourCPU,
                    markPoint : {
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    },
                    markLine : {
                        data : [
                            {type : 'average', name: '平均值'}
                        ]
                    }
                } ]
        };
        myChart.setOption(option,true);
        myChartSecond.setOption(optionSecond,true);
        myChartMin.setOption(optionMin,true);
        myChartHour.setOption(optionHour,true);

        //初始化仪表数据

         one_option = {
            tooltip : {
                formatter: "{a} <br/>{b} : {c}%"
            },
            series : [
                {
                    title:{
                        show : true,
                        offsetCenter: [0, "95%"],
                    },
                    radius : 95,
                    pointer: {
                        color: '#FF0000'
                    },
                    name:'监控指标',
                    axisLine: {            // 坐标轴线
                        lineStyle: {       // 属性lineStyle控制线条样式
                            width: 20
                        }
                    },
                    detail : {formatter:'{value}%'},
                    type:'gauge',
                    data:[{value: 50, name: 'JVM使用率'}]
                }
            ]
        };
         two_option = {
            tooltip : {
                formatter: "{a} <br/>{b} : {c}%"
            },
            series : [
                {
                    name:'监控指标',
                    type:'gauge',
                    startAngle: 180,
                    endAngle: 0,
                    center : ['50%', '80%'],    // 默认全局居中
                    radius : 180,
                    axisLine: {            // 坐标轴线
                        lineStyle: {       // 属性lineStyle控制线条样式
                            width: 40
                        }
                    },
                    axisTick: {            // 坐标轴小标记
                        splitNumber: 10,   // 每份split细分多少段
                        length :12,        // 属性length控制线长
                    },
                    axisLabel: {           // 坐标轴文本标签，详见axis.axisLabel

                        textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                            fontSize: 15,
                        }
                    },

                    pointer: {
                        width:10,
                        length: '80%',
                    },
                    title : {
                        show : true,
                        offsetCenter: [0, 15],       // x, y，单位px
                        /* textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                         color: '#fff',
                         fontSize: 25
                         }*/
                    },
                    detail : {
                        show : true,
                        borderWidth: 0,
                        offsetCenter: [40, -20],       // x, y，单位px
                        formatter:'{value}%',
                        textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                            fontSize : 30
                        }
                    },
                    data:[{value: 50, name: 'cpu使用率'}]
                }
            ]
        };
        one_option.series[0].data[0].value =dataSecendJVM[0];
        one_option.series[0].data[0].name ='JVM使用率';
        one_option.series[0].pointer.color='#FF0000'
        main_one.setOption(one_option, true);

        two_option.series[0].data[0].value =dataSecendCPU[0];
        main_two.setOption(two_option, true);

        one_option.series[0].data[0].value =dataSecendRAM[0];
        one_option.series[0].data[0].name ='内存使用率';
        one_option.series[0].pointer.color='#428bca'
        main_three.setOption(one_option, true);
    }


	//定时刷新
    var axisData;
	clearInterval(timeTicket);
    clearInterval(timeTicketMin);
    clearInterval(timeTicketHour);
	var timeTicket = setInterval(function() {
		axisData = (new Date()).toLocaleTimeString().replace(/^\D*/, '');
		axisData = now.getHours()+":"+axisData.substr(axisData.indexOf(":") + 1);
		var jvm = [];
		var ram = [];
		var cpu = [];
		$.ajax({
			type : "POST",
			url : base_url + '/monitor/usage',
            data:{"ip":ip},
			/*async : false,*/
			dataType : 'json',
			success : function(data) {
                if(data.content !== null){
                    var json={"cpuUsage":data.content.cpuUsage,
                        "ramUsage":data.content.memoryUsage,
                        "jvmUsage":data.content.jvmUsage};
                    $("#td_jvmUsage").html(json.jvmUsage);
                    $("#td_serverUsage").html(json.ramUsage);
                    $("#td_cpuUsage").html(json.cpuUsage);


                    jvm.push(json.jvmUsage);
                    ram.push(json.ramUsage);
                    cpu.push(json.cpuUsage);
                    option.xAxis[0].data.shift();
                    option.xAxis[0].data.push(axisData);
                    option.series[0].data.shift();
                    option.series[0].data.push(jvm[0]);
                    option.series[1].data.shift();
                    option.series[1].data.push(ram[0]);
                    option.series[2].data.shift();
                    option.series[2].data.push(cpu[0]);
                    // 动态数据接口 addData
                    myChart.setOption(option);
                    optionSecond.xAxis[0].data.shift();
                    optionSecond.xAxis[0].data.push(axisData);
                    optionSecond.series[0].data.shift();
                    optionSecond.series[0].data.push(jvm[0]);
                    optionSecond.series[1].data.shift();
                    optionSecond.series[1].data.push(ram[0]);
                    optionSecond.series[2].data.shift();
                    optionSecond.series[2].data.push(cpu[0]);

                    // 动态数据接口 addData
                    myChartSecond.setOption(optionSecond);

                    one_option.series[0].data[0].value =json.jvmUsage;
                    one_option.series[0].data[0].name ='JVM使用率';
                    one_option.series[0].pointer.color='#FF0000'
                    main_one.setOption(one_option, true);

                    two_option.series[0].data[0].value =json.cpuUsage;
                    main_two.setOption(two_option, true);

                    one_option.series[0].data[0].value =json.ramUsage;
                    one_option.series[0].data[0].name ='内存使用率';
                    one_option.series[0].pointer.color='#428bca'
                    main_three.setOption(one_option, true);
                }
			}
		});
	}, 10000);
    var timeTicketMin = setInterval(function() {
        now=new Date();
        axisData =  now.getHours()+":"+now.getMinutes();
        var jvm = [];
        var ram = [];
        var cpu = [];
        $.ajax({
            type : "POST",
            url : base_url + '/monitor/usage',
            data:{"ip":ip},
            /*async : false,*/
            dataType : 'json',
            success : function(data) {
                if(data.content !==null){
                    var json={"cpuUsage":data.content.cpuUsage,
                        "ramUsage":data.content.memoryUsage,
                        "jvmUsage":data.content.jvmUsage};
                    jvm.push(json.jvmUsage);
                    ram.push(json.ramUsage);
                    cpu.push(json.cpuUsage);
                    optionMin.xAxis[0].data.shift();
                    optionMin.xAxis[0].data.push(axisData);
                    optionMin.series[0].data.shift();
                    optionMin.series[0].data.push(jvm[0]);
                    optionMin.series[1].data.shift();
                    optionMin.series[1].data.push(ram[0]);
                    optionMin.series[2].data.shift();
                    optionMin.series[2].data.push(cpu[0]);

                    // 动态数据接口 addData
                    myChartMin.setOption(optionMin);
                }
            }
        });
    }, 60000);
    var timeTicketHour = setInterval(function() {
        now=new Date();
        axisData =  now.getHours()+":"+now.getMinutes();
        var jvm = [];
        var ram = [];
        var cpu = [];
        $.ajax({
            type : "POST",
            url : base_url + '/monitor/usage',
            data:{"ip":ip},
            /*async : false,*/
            dataType : 'json',
            success : function(data) {
                if(data.content !==null){
                    var json={"cpuUsage":data.content.cpuUsage,
                        "ramUsage":data.content.memoryUsage,
                        "jvmUsage":data.content.jvmUsage};
                    jvm.push(json.jvmUsage);
                    ram.push(json.ramUsage);
                    cpu.push(json.cpuUsage);
                    optionHour.xAxis[0].data.shift();
                    optionHour.xAxis[0].data.push(axisData);
                    optionHour.series[0].data.shift();
                    optionHour.series[0].data.push(jvm[0]);
                    optionHour.series[1].data.shift();
                    optionHour.series[1].data.push(ram[0]);
                    optionHour.series[2].data.shift();
                    optionHour.series[2].data.push(cpu[0]);
                    // 动态数据接口 addData
                    myChartHour.setOption(optionHour);
                }
            }
        });
    }, 360000);
});
