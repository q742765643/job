var _version = (navigator.language || navigator.browserLanguage).toLowerCase();
var pageConfig_version =null;
var monitor_version=null;
if(_version.indexOf('zh')>=0)
{
	pageConfig_version = "pageConfig";
	monitor_version = "monitor";
}
else if(_version.indexOf('en')>=0)
{
	pageConfig_version = "pageConfig_en";
	monitor_version = "monitor_en";
}
else
{
	monitor_version = "monitor";	
}

require.config({
	urlArgs : "v" + (new Date()).getTime(),
	paths : {
		"jquery" : "../javascript/jquery/jquery-1.8.3.min",
		"mxGraph" : "../mxgraph/js/mxClient1",
		"pageConfig" : pageConfig_version,
        "i18n":"../resources/lib/jquery.i18n.properties-1.0.9"
	},
	shim : {
		"jquery" : {
			exports: '$'
		},
		"mxGraph" : {
			exports: 'mxGraph'
		},
		"i18n" : {
			exports: 'i18n',
			deps : ["jquery"]
		},
		"pageConfig" : {
			exports: 'page',
			deps : ["jquery","mxGraph"]
		}
	}
})
require([monitor_version,"pageConfig","i18n"], function (monitor,page){		
	var thisUrlSrarch = location.search.replace("?","").split("&");			//获取当前url的search
	var urlObj = {};														//解析保存出url的obj
	thisUrlSrarch.map(function(x){
		urlObj[x.split("=")[0]] = x.split("=")[1];
	})
	//获取定义函数
	var Route = monitor.Route,							//创建进度条类	
		releaser = monitor.releaser,					//发布者发布类
		Scoket = monitor.Scoket,						//socket类
		ManageData = monitor.ManageData,				//数据处理类
		showProperty = monitor.showProperty,			//显示信息详情类
		taker = monitor.taker;							//订阅者订阅类
	var httpConfig = location.origin.replace("http://","");
//	 httpConfig = "192.168.30.221:8080";
	var httpPort = {
		socket : "ws://"+httpConfig+"/websocket/demo",
		getProcessXmlById : "http://" + httpConfig + '/flow/getProcessXmlById',
		getRootFolderList : "http://" + httpConfig + '/flow/getRootFolderList',
		listFilesByFolder : "http://" + httpConfig + "/flow/listFilesByFolder"
	}
	var setting = {										//-进度条配置
		director : "horizontal",
		appendTo : document.getElementById("wrap"),
		config : {
			routeLength : 400
		}
	}
	var route = new Route(setting);			//创建进度条
	route.createDirector();					//创建进度框
	$(".contain").on("click","li",function(){
		if($(this).attr("contenteditable") == ""){
			document.execCommand("selectAll",false);
		}
	})
	$(".tabData").on("click",".li3",function(){
		if($(this).attr("contenteditable") == ""){
			document.execCommand("selectAll",false);
		}
	})	
	page.vertexBaseStyle = function(){				//重写cell样式
		var style = new Object();
		style[mxConstants.STYLE_AUTOSIZE] = 1;
		style[mxConstants.STYLE_ROUNDED] = false;
		style[mxConstants.STYLE_SHADOW] = false;
		style[mxConstants.STYLE_FILLCOLOR] = '#FFF';
		style[mxConstants.STYLE_FONTCOLOR] = 'black';
		style[mxConstants.STYLE_FONTSIZE] = '12';
		style[mxConstants.STYLE_SPACING] = 4;
		style[mxConstants.STYLE_OPACITY] = 70;		
		style[mxConstants.STYLE_EDITABLE] = 0;
		style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = "middle";
		style[mxConstants.STYLE_GRADIENTCOLOR] = '#F5BCBC';
		return style;
	}
	page.getRightWidth = function(){
		var rWidth = $("#graphContainer").width() - 20;
		return rWidth;
	}
	page.addMenu = null;
	page.init(true);								//初始化graph
	//page.graph.setCellsLocked(false);		//单独设置cell锁定	
	$(".choseTabData").on("mousemove",".addFile",page.liMousemove);		//初始化提示信息
	$(".choseTabData").on("mouseout",".addFile",page.liMouseout);
	document.onkeydown = null;
	$(".show_detail").click(function(){
		$(".detail").toggleClass("show");
	})
	var addClickEvent = function(){					//添加cell单击事件
		var graph = page.graph;
  		var oldHeight,oldWidth,newWidth,newHeight;
		graph.addListener(mxEvent.CLICK, function(sender, evt){
			var e = evt.getProperty('event'); 			// mouse event 鼠标事件
			var cell = evt.getProperty('cell'); 		// cell may be null   可能为null的cell
			cell = graph.getSelectionCell();
			
			if(cell == null || cell.isEdge()){ 
				graph.clearSelection();
			}
			else{
				if(!cell.data){
					$(".detail").removeClass("show");
					// $(document).alert("当前cell无数据")
					return;
				}
				if(cell.id.indexOf("end") >= 0){
					$(".rewrite_para").hide();
				}
				else{
					$(".rewrite_para").show();					
				}
				var isPL = cell.isPL == "true" ? true : false;
				$("#isPL").prop("checked",isPL);
				var showPropertyIn = new showProperty(cell);
				showPropertyIn.showInDetail();				
			}
			evt.consume();
		});
		graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt){
			var e = evt.getProperty('event'); // mouse event 鼠标事件
			var cell = evt.getProperty('cell'); // cell may be null   可能为null的cell	
			if(cell != null){
				if(cell.isProcess && cell.processId){
					var processId = cell.processId;
					var appointedId = cell.appointedId;
					var openUrl = (location.href).split("?")[0] + "?processId=" + processId + "&appointedId=" + appointedId; 
					window.open(openUrl);
				}
			}
		})
		graph.addMouseListener({
			mouseDown: function(sender, me) {
				var cell = me.getCell();
				if(cell != null){					
					if(cell.isEdge()){
						graph.clearSelection();
					}
					else{
						oldHeight = cell.geometry.height;
						newHeight = oldHeight;
						oldWidth = cell.geometry.width;
						newWidth = oldWidth;
					}
				}
			},
			mouseMove: function(sender, me){	    	
   			},
   			mouseUp: function(sender,me) {		
   			}
  		});		
	}
	addClickEvent();		//为cell添加事件
	//获取xml
	$.ajax(
		{
            url: httpPort.getProcessXmlById+"/"+urlObj.processId,
            data:{processId:urlObj.processId},
            type:'get',
            dataType:'JSON',
            error : function(){
            	alert("error");
            },
            success:function(data){
               	var xml = data.xml;
            	var doc = mxUtils.parseXml(xml);
				var decode = new decodeXML(doc);
				$(".title").html($(doc).find("Process").attr("name"));
				decode.init();
				page.graph.setCellsLocked(false);		
				page.graph.clearSelection();
				var serverceLength = $(doc).find("Process Figure[type='Service']").length;		//总服务个数
				initRoute(serverceLength);
            }
        }
	);
	var initRoute = function(serverceLength){			//当ajax执行完毕开始初始化进度条
		var cells = page.getCellsFromBegin();			//获取graph中的cell
		$(".sum").html(serverceLength);					//流程总个数
		
		route.setLength(serverceLength);
		var createRoute = route.getSingle(route.createSchedule);		//惰性单例，闭包创建单个进度函数
		var rele = new releaser();								//发布者
		var takerReceive = new taker(createRoute);				//订阅者  
		takerReceive.receive("add");							//订阅消息
		var msgId = urlObj.appointedId;
		/*Scoket.prototype.scoket.onmessage = function(e){
			var data = e.data;
			if(!data){
				return
			}
			data = JSON.parse(data.replace("{#start#}","").replace("{#end#}","")).msgBody;		//取出msgBody
			var manageData = new ManageData(data,cells);				//收到消息进行数据处理
			var cell = manageData.saveDataIncell();						//

			
			if(cell){
				$(".detail").addClass("show");
				var showPropertyIn = new showProperty(cell);
				showPropertyIn.showInDetail();				
			}
			page.graph.setSelectionCell(cell);
			this.close()
		}				*/			//msgId
		var scoket = new Scoket(httpPort.socket,msgId,cells);			//初始化socket连接

		
		scoket.link();													//初始化打开socket连接
		$(".exec").click(function(){									//执行按钮添加事件
			
			var cell = page.graph.getSelectionCell();	
			var liList = $(".exec").parents(".tab").find(".tabData .li3");		//获取li
			var arr = [];
			var sendContent = {};
			sendContent.msgId = urlObj.appointedId;
			sendContent.msgType = "retry";
			var messageBody = {};
			messageBody.id = cell.id;
			if(cell.id == "begin"){
				messageBody.id = "startFigure"
			}
			liList.map(function(x) {
                var selectVal = $(liList[x]).find("select").val();
                var dataId = $(liList[x]).attr("dataID");
                if (undefined != dataId) {
                if (selectVal) {
                    var obj = {
                        dataId: dataId,
                        value: selectVal
                    }
                    arr.push(obj);
                }
                else {
                    var thisVal = $(liList[x]).html();
                    var obj = {
                        dataId: dataId,
                        value: thisVal
                    }
                    arr.push(obj);

                }
               }
			})
			messageBody.params = arr;
			sendContent.msgBody = messageBody;		
			$(".shade").fadeOut().find(".dataView").animate({"width":"0","height": "0"},300);
			$(document).alert(GetValueByKey("The.execution.code.is.being.sent."));
			var sendMsg = "{#start#}" + JSON.stringify(sendContent) + "{#end#}";
			ManageData.prototype.destoryCellData();

			scoket.scoket.send(sendMsg);	//发送socket请求
		})

		$(".pause_btn").click(function(){						//暂停继续按钮事件
			var thisVal = $(this).val();
			if(thisVal == "pause"){
				$(this).addClass("start_btn");
				$(this).val("start");
				scoket.scoket.send("{#start#}{'msgId':" + urlObj.appointedId + ",'msgType':'suspend'}{#end#}");
			}
			else{
				$(this).removeClass("start_btn");
				$(this).val("pause");
				scoket.scoket.send("{#start#}{'msgId':" + urlObj.appointedId + ",'msgType':'start'}{#end#}");
			}
		})
	}	
	var sendAjax = function(url,data,fn){
		$.ajax({
			url : url,
            type:'get',
         	data:data,
            dataType:'jsonp',
            error : function(){
            	alert("error");
            },
            success : function(msg){
            	if($(".subShade")){
            		$(".subShade").remove();
            	}
            	fn(msg);
            }
		})		
	}
	var manageFolderData = function(msg){			//文件夹根目录数据管理函数
		var folderList = msg.listFiles,
			str = "";
		folderList && folderList.map(function(x){
			var cls = "";
			if(x.file){
				cls = "addFile";
			}
			str += "<ol class='numb0'>" +
					   	"<li class='li3 folder "+cls+"'>" + x.name + "</li>" +
					   	"<li  class='up_data'>" + x.lastUpdateDate + "</li>" +
					   	"<li class='brn'>"+
					   		"<button value='"+x.path+"' data-type='"+x.file+"' class='choseNext'>"+GetValueByKey("Select")+"</button>"+
					   	"</li>" +
			  		"</ol>";
		})
		var length = folderList.length || 0;
		if(length * 30 >= 308){
			$(".dialog").width("97%");
		}
		else{
			$(".dialog").width("100%");
		}
		$(".choseTabData").html(str);
	}	
	

	//闭包保存事件e.target   currying函数柯里化
	var saveTargetEle = (function(){
		var thisTarget = null;
		return function(eTarget){		
			if(arguments.length === 0 && thisTarget){
				return thisTarget;
			}
			else{
				thisTarget = eTarget;
			}	
		}
	})()
	var goNextFolder = function(ele){
		var val = ele.val();
		var isFile = ele.attr("data-type");
		
		var folderFile = ele.parents(".numb0").find(".folder").html();
		ele.parents(".choseFile").hide();	
		var $targetEle = saveTargetEle().target;
		$($targetEle).parents("ol").find(".li3").html(folderFile);
		saveTargetEle(null);
//		if(isFile == "true"){
//			var folderFile = ele.parents(".numb0").find(".folder").html();
//			ele.parents(".choseFile").hide();	
//			var $targetEle = saveTargetEle().target;
//			$($targetEle).parents("ol").find(".li3").html(folderFile);
//			saveTargetEle(null);
//		}
//		else{
//			var folderVal = ele.parents("ol").find(".li3").html();
//			if(folderVal){				
//				var btn = "<button value='"+val+"'>"+folderVal+"</button>";
//				$(".thisPath").append(btn);
//			}
//			val = encodeURIComponent(val);
//			var url = httpPort.listFilesByFolder;
//			var data = {pathList:val};
//			sendAjax(url,data,manageFolderData);
//		}		
	}
	var dblcleckgoNextFolder = function(ele){
		var val = ele.val();
		var isFile = ele.attr("data-type");
		
		var folderVal = ele.parents("ol").find(".li3").html();
		if(folderVal){				
			var btn = "<button value='"+val+"'>"+folderVal+"</button>";
			$(".thisPath").append(btn);
		}
		val = encodeURIComponent(val);
		var url = httpPort.listFilesByFolder;
		var data = {pathList:val};
		sendAjax(url,data,manageFolderData);
				
	}
	var showSubView = function(arr){			//显示数据
        arr=eval('(' +arr+ ')');
		var str = "",				
			tempIndex = 0;
		if(!arr){
			return;
		}
		console.log(arr);
		arr.map(function(x,index){
			var select = "",
				isContentble = "",
				cls = "",
				addHeight = "",
				chose = "";
			if(x.parameterType == 'select'){			//有下拉
				select += "<select>";
				var inVal =eval('(' +x.url+ ')');
                var value =x.value;
				inVal.map(function(y){
                    var selected = "";
					if(value == y.id){
						selected = "selected";
					}
					select += "<option value='"+y.id+"' "+selected+">"+y.text+"</option>";
				})
				select += "</select>"
			}	
			else{					//无下拉
				select = x.value;
				if(select.length >= 70){
					cls = "hasBr";
					addHeight = (Math.ceil(select.length/70)+1) * 20 + "px";
				}
				isContentble = "contenteditable";	//无下拉可编辑	
				//var reg = new RegExp(/\//g);
				//select = select.replace(reg,"\\").replace(/\/\//g,"\\");
			}
			if(x.parameterType == 'infile' || x.parameterType =='outfile' || x.parameterType =='inputinfolder' || x.parameterType =='inputparamfile'){		//无下拉浏览按钮
				chose = "<button class='chose'>"+GetValueByKey("Select")+"</button>";
			}
			if(x.value.length >= 14){
				var textLine = Math.ceil(x.value.length/14 + 1) * 20;
				addHeight = textLine > parseInt(addHeight) ? textLine : parseInt(addHeight);
				addHeight += "px";
			}
			str += "<ol class='numb" + index % 2 + " "+ cls +"' style='height:"+addHeight+";'>" +
					   "<li style='line-height:"+addHeight+";'>" + x.parameterDesc + "</li>" +
					   "<li class='li3' " + isContentble + " dataID='" + x.dataID + "'>" + select + "</li>" +
					   "<li class='brn' style='line-height:"+addHeight+";'>" + chose + "</li>" +
			  		"</ol>";
			tempIndex = index;
		})
		for(var i = 0; i < 6; i++){
			tempIndex++;				
			str += "<ol class='numb" + tempIndex % 2 + "'>" +
					   "<li></li>" +
					   "<li class='li3'1></li>" +
					   "<li class='brn'></li>" +
			  		"</ol>";
		}
		$(".returnData").html(str);
		setTimeout(function(){
			var length = arr.length + 6;
			var accountHeight = 0;
			var returnDataHeight = $("#isScroll").height();
			for(var i = 0; i < length; i++){
				accountHeight += $("#isScroll").find("ol").eq(i).height();
			}	
			if(returnDataHeight <= accountHeight){
				$("#isScroll").width("102.2%");
			}
			else{
				$("#isScroll").width("100%");
			}
		},300)
	}
	var loadingWait = function(parentDom){
		var width = $(parentDom).width();
		var height = $(parentDom).height();
		var subShade = "<div class='subShade'></div>";
		$(parentDom).append(subShade);
		$(".subShade").css({width:width,height:height/*,left:offset.left,top:offset.top*/});
	}
	/**
	 * 事件添加区
	 */
	$(".dataClose,.cancel").click(function(){
		if($(this).parents().hasClass("dataView")){
			$(".shade").fadeOut().find(".dataView").animate({"width":"0","height": "0"},300);
		}
		else{
			$(this).parents(".choseFile").hide();
		}
		saveTargetEle(null);
	})
	$(".rewrite_para").on("click",function(){				
		var cell = page.graph.getSelectionCell();
		if(cell!=null && cell.data){			
			cell.data && showSubView(cell.data.in_put);
			$(".shade").css({"display":"block"}).find(".dataView").animate({"width":"780px","height":"400px"},300);
		}
		/*else{
			$(document).alert("当前cell无数据");
		}			*/
	})	
	//拖拽
	$(".dataTit").mousedown(function(ev){
		ev = ev || event;
		var oldX = ev.clientX;
		var oldY = ev.clientY;
		var cW = $(document).width();
		var cH = $(document).height();
		var parentNode = $(this).parent();
		var boxW = parentNode.width();
		var boxH = parentNode.height();
		var boxX,boxY,newX,newY;
		boxX = parentNode.offset().left;
		boxY = parentNode.offset().top;
		var x = oldX - boxX;
		var y = oldY - boxY;	
		$(document).on("mousemove",function(e){
		//$(document).mousemove(function(e){
			e = e || event;
			newX = e.clientX - x;
			newY = e.clientY - y;
			if(newX <= 0){
				newX = 0;
			}
			if(newY <= 0){
				newY = 0;
			}
			if(newY >= cH - boxH){
				newY = cH - boxH - 4;
			}
			if(newX >= cW - boxW){
				newX = cW - boxW - 2;
			}
			parentNode.css({"left":newX,"top":newY}); 
			return false;
		}).mouseup(function(){
			$(document).off("mousemove");
		})
	}).mouseover(function(){
		$(this).css("cursor","move");
	})
	$(".choseFile").on("click",".choseNext",function(){
		goNextFolder($(this));
	})
	$(".choseTabData").on("dblclick","ol",function(){			//双击ol进入下一级目录
		loadingWait($(this).parents(".choseFile"));
		dblcleckgoNextFolder($(this).find(".choseNext"));
	})
	$(".shade").on("click",".chose",function(e){				//单击选择按钮从根目录进入下一级目录
		e = e || event;
		$(".choseFile").show();
		saveTargetEle(e);
		loadingWait($(this).parents(".tab"));
		var url = httpPort.getRootFolderList;
		sendAjax(url,"",manageFolderData);
	})
	$(".thisPath").on("click","button",function(){
		var val = $(this).val();
		var nextEles = $(this).nextUntil();
		nextEles.remove();
		if(val == "root"){
			var url = httpPort.getRootFolderList;
			sendAjax(url,"",manageFolderData);
		}
		else{
			dblcleckgoNextFolder($(this));
		}	
	})
})	