/**
 * create in 2016/10/15 16:21
 * 
 */
//document.write("<script language=javascript src='common/taglibs.js'></script>"); 
var viewShow = function(obj,graph,allCells){
	this.obj = obj;
	objSaveBeforeCell.cells = allCells;
	graph = graph ? graph : page.graph;
	this.cell = graph.getSelectionCell();
	objSaveBeforeCell.cell = this.cell;	
}
viewShow.prototype.showZtree = function(){
	var initTree = new zNodesView(this.obj,this.cell);
	initTree.initTrees();
	var valueShowViews = new valueShowView(this.cell);
	valueShowViews.initView();
}
viewShow.prototype.destoryEvent = function(){
	addEvents.off("ol",$('.tabData'),"click",null);
	addEvents.off("ol .addPng",$('.tabData'),"click",null);
	addEvents.off("ol .delPng",$('.tabData'),"click",null);
	addEvents.off("ol .autoMatch",$('.tabData'),"click",null);
	$(".shade").fadeOut().find(".dataView").animate({"width":"0","height": "0"},300);
	$(".endTit").hide();
	$(".tabTit").show();
	$(".cencelData").show();
	$(".dataTit").off("click",".dataClose",null);						//销毁事件
}

//zNodesView show function
var zNodesView = function(obj,cell){
	this.obj = obj;
	this.cell = cell;
	this.zNodes = [];
	this.id = "treeData";
}
zNodesView.prototype.initTrees = function(){
	this.getZnode();
	this.configTree();
	$.fn.zTree.init($("#" + this.id),this.settings,this.zNodes);
}
zNodesView.prototype.onMouseDown = function(event, treeId, treeNode){			//右侧视图节点鼠标按下事件
	addEvents.on("ol",$('.tabData'),"hover",function(){
		$(this).addClass("hoverClick").siblings().removeClass("hoverClick");
	})
}
zNodesView.prototype.onMouseUp = function(event, treeId, treeNode){			//右侧视图节点鼠标谈起事件
	addEvents.off("ol",$('.tabData'),"hover",null);
}

zNodesView.prototype.onDrop = function(event,treeId,treeNodes){	
	var cell = page.graph.getSelectionCell();
	var targetDataEle = event.target;
	var parentEle = $(targetDataEle).parents("ol")[0];
	var nodesDefaultVal = treeNodes[0].cellDefaultVal;	
	addEvents.off("ol",$('.tabData'),"hover",null);				//卸载hover事件
	addEvents.removeCursors(".shade",".dataTit");				//移除鼠标样式
	if(treeNodes[0].isParent){
		mxUtils.error(GetValueByKey("Please.don't.drag.the.root.node."),280,true);
		return
	}
	if(cell.id == "end"){
		var isGo = null,
			isAdd = true;
		if(cell.defaultVal.length <= 0){
			isGo = true;
		}
		else{
			cell.defaultVal.map(function(x){
				if(x.cellId == nodesDefaultVal.cellId){
					$(".shade").alert(GetValueByKey("Data.has.been.added.")+GetValueByKey("Please.don't.repeat.to.add."));
					isAdd = false;
				}
				else{
					isGo = true;
				}
			})
		}
		if(isGo && isAdd){
			var obj = {};
			for(var i in nodesDefaultVal){
				obj[i] = nodesDefaultVal[i]
			}
			cell.defaultVal.push(obj);
			$(".shade").alert(GetValueByKey("Data.has.been.added."));
		}		
		var valueShowViews = new valueShowView(cell);
		valueShowViews.initView();
		return;
	}
	if(parentEle){
		var dataTypes = $(parentEle).find('li').eq(0).html();
		var dataDesc = $(parentEle).find('li').eq(1).html();

		if(dataTypes == nodesDefaultVal.dataType){				//类型相等进行数据重组
			var obj = {};
			obj.cell = objSaveBeforeCell.cell;
			obj.thisDataId = $(parentEle).attr("data-name");
			obj.nodesDefaultVal = nodesDefaultVal;
			obj.cells =  objSaveBeforeCell.cells;		
			if (obj.thisDataId != nodesDefaultVal.name) {			//添加成功
				//通过receiveid删除树
				var receiveData = $(parentEle).attr("data-receiveid");
				zNodesView.prototype.removeNode(receiveData);	
				var operates = new operateData(obj);
				operates.matchData();
				var $isHasBanData = $(parentEle).hasClass("banDate2");
				if($isHasBanData){
					$(parentEle).removeClass("banDate2");
					$(parentEle).find(".autoMatch").hide();
					$(parentEle).find(".addPng").hide();
					$(parentEle).find(".delPng").show();
                }
                $(parentEle).find("font").attr("color","blue");
                treeNodes[0].receive = receiveData + "," + treeNodes[0].receive;
				$(".shade").alert(GetValueByKey("Data.matching.succeeded."));
			}
			else{
				$(".shade").alert(GetValueByKey("Data.points.to.itself."));
			}
		}
		else{
			$(".shade").alert(GetValueByKey("Data.type.doesn't.match."));
		}
	}
	else{
		$(".shade").alert(GetValueByKey("Data.doesn't.match."));
	}	
}
zNodesView.prototype.onDragMove = function(event){
	zNodesView.prototype.setElementCursorState(event);
}

//移除ztree的节点
zNodesView.prototype.removeNode = function(operateDatas){		//移除节点
	var treeObj = $.fn.zTree.getZTreeObj("treeData");
	var nodes = treeObj.getNodes();
	var receiveNodes = treeObj.getNodesByParamFuzzy("receive", operateDatas, null);
	//删除树节点
	if(receiveNodes && receiveNodes.length > 0){
		if(receiveNodes[0].receive.split(",").length > 1){
			receiveNodes[0].receive = receiveNodes[0].receive.replace(operateDatas,"").replace(",,",",");
			receiveNodes[0].receive = receiveNodes[0].receive.replace(/^,{0,1}/,"").replace(/,{0,1}$/,"");
		}
		else{
			treeObj.removeNode(receiveNodes[0]);
		}
	}
}
//设置鼠标样式
zNodesView.prototype.setElementCursorState = function(event){
	var cell = page.graph.getSelectionCell();
	var targetDataEle = event.target;
	var parentEle = $(targetDataEle).parents("ol")[0];	
	if(parentEle){
		addEvents.removeCursors(".shade",".dataTit");
	}
	else{
		addEvents.setCursor(".shade",".dataTit");
	}
	if(cell.id == "end"){
		$(".tabData").css({"cursor":"pointer"});
	}
}

//添加数据
zNodesView.prototype.addNodeToParent = function(treeNodes){
	var treeObj = $.fn.zTree.getZTreeObj("treeData");
	var parentNode = treeObj.getNodeByParam("name", GetValueByKey("The.start.node.can.modify.the.parameter."), null);
	treeObj.addNodes(parentNode, treeNodes);
}
//decode obj getZnode for create tree obj
zNodesView.prototype.getZnode = function(){
	var obj = this.obj,
        me = this,
		tempReceive = [],			//暂存修改后的receiceId值
		tempDataObj = {},
		thisOutPara = {},
		tempArrData = [];
    obj.map(function(x){
		var childrenData = {};
		if(x.type == "InputParameter"){
			childrenData.name = x.parameterDesc + "[" + x.dataType + "]";
			childrenData.pId = x.type;
			childrenData.receive = x.id;
			childrenData.dataID = x.dataID;
			childrenData.id = x.dataID;
			childrenData.cellDefaultVal = x;
			tempDataObj.id = x.type;
                if (!tempDataObj.hasOwnProperty("name")) {
                    tempDataObj.isParent = true;
                    tempDataObj.name = GetValueByKey("The.start.node.can.modify.the.parameter.");
                    me.zNodes.push(tempDataObj);
                }
                if (x.id.indexOf(x.dataID.split("_")[1]) >= 0 && !x.addClassName) {
                    me.zNodes.push(childrenData);
                }
                else if (x.id.indexOf(x.dataID.split("_")[1]) < 0 && !x.addClassName) {
                    tempReceive.push(x.id);
                }

		}
		else if(x.type == "OutputParameter" && x.group != me.cell.value){					
			childrenData.name = x.parameterDesc + "[" + x.dataType + "]";
			childrenData.receive = x.id;
			childrenData.dataID = x.dataID;
			childrenData.id = x.dataID;
			childrenData.cellDefaultVal = x;
			if(!thisOutPara.hasOwnProperty("name")){		//如果没有输出参数
				thisOutPara.isParent = true;
				thisOutPara.id = x.cellId + x.type;
				thisOutPara.name = x.group;
				me.zNodes.push(thisOutPara);
			}
			else if(thisOutPara.name !== x.group){						//如果有输出参数需判断当前的输出是否需发生改变
				thisOutPara = {};
				thisOutPara.isParent = true;
				thisOutPara.id = x.cellId + x.type;
				thisOutPara.name = x.group;
				me.zNodes.push(thisOutPara);
			}
			childrenData.pId = thisOutPara.id;
			me.zNodes.push(childrenData);		
		}
	}) 	
	tempReceive.map(function(x){
		me.zNodes.map(function(y){
			if(x == y.receive){
				y.receive += "," + x;
			}
		})
	})
}

//配置zTree
zNodesView.prototype.configTree = function(){
	var me = this;
	this.settings = {
		edit: {
			drag : {
				autoExpandTrigger : true,
				inner : false,
				prev : false,
				next : false
			},
			enable : true,
			showRemoveBtn : false,
			showRenameBtn : false
		},
		view : {
			dblClickExpand : true,
			selectedMulti : false,
			showLine : true
		},
		data:{
			simpleData : {
				enable : true
			}
		},
		callback : {
			onMouseDown : me.onMouseDown,
			onDrop : me.onDrop,
			onDragMove : me.onDragMove,
			onMouseUp : me.onMouseUp
		}
	};
}
//右侧数据显示
var valueShowView = function(cell){
	this.cell = cell;
	this.parentDom = $(".tabData");
	this.defaultVal = cell.defaultVal;
}
valueShowView.prototype.initView = function(){
	this.createHtml();
	this.addUlEvent();
}
valueShowView.prototype.getPara = function(){
	var me = this;
	var data = page.getAllCellsDefaultVal.apply(me.getPara,arguments);
	return data;
}
valueShowView.prototype.createHtml = function(){
	var data = this.defaultVal;
	var cellId = this.cell.id;
	var outType = "OutputParameter";
	if(cellId != "end"){
		outType = "InputParameter";
	}
	if(data instanceof Array){
		var ulListHtml = "",
			num = 0;
        var treeObj = $.fn.zTree.getZTreeObj("treeData");
        var nodes = treeObj.getNodes();
		data.map(function(x){
			if(x.type == outType){				
				var hide = "",cls = "",show="";
				if(x.addClassName){
					hide = "hide";
					show = "block";
					var cls = x.addClassName;
				}
				ulListHtml += "<ol class='numb" + (num++) % 2 + " "+cls+"' data-name='"+x.name+"' data-id='"+x.dataID+"' data-receiveID='"+x.id+"'>" +
								   "<li>" + x.dataType + "</li>" ;
                var term = x.id;
				var selectedNode = treeObj.getNodesByParamFuzzy("receive", term, null);
                if(selectedNode.length>=2){
                    ulListHtml +="<li class='li3'><font  color='blue'>" + x.parameterDesc + "</font></li>" ;
                }else{
                    ulListHtml +="<li class='li3'><font  color='black'>" + x.parameterDesc + "</font></li>" ;
                }
                ulListHtml +="<li><div class='autoMatch' style='display:" + show + ";'></div></li>" +
								   "<li><div class='addPng' style='display:" + show + ";'></div></li>" +
						  		   "<li class='li4'><div class='delPng " + hide + "'></div></li>"  +
						  	  "</ol>";
			}
		})
		this.parentDom.html(ulListHtml);
		page.isAddWidth(".tabData");
	}
}

valueShowView.prototype.addUlEvent = function(){
	var thisDom = this.parentDom;
	var me = this;
	// me.cell = page.graph.getSelectionCell();
	addEvents.on("ol",thisDom,"click",function(){
		$(this).addClass("hoverClick").siblings().removeClass("hoverClick");
		var term = $(this).attr("data-receiveid");
		var treeObj = $.fn.zTree.getZTreeObj("treeData");
		var nodes = treeObj.getNodes();
		var selectedNode = treeObj.getNodesByParamFuzzy("receive", term, null);
        if(selectedNode.length > 1){
			selectedNode.map(function(x){
				var receiveData = x.receive;
				var dataIDDate = x.dataID;
				if(receiveData.indexOf(dataIDDate.split("_")[1]) >= 0){
					var parentNode = x.getParentNode();
					treeObj.expandNode(parentNode, true, true, true);
					treeObj.selectNode(x);
				}
			})
		}
		else{
			treeObj.selectNode(selectedNode[0]);
		}		
	})
	addEvents.on("ol .delPng",thisDom,"click",function(){
		var olParent = $(this).parents("ol");
		var operateDatas = $(olParent).attr("data-name");
		var $isHasBanData = $(olParent).hasClass("banDate2");
		$(olParent).find(".addPng,.autoMatch").show();
		$(this).hide();
		if($isHasBanData){
			$(".shade").alert(GetValueByKey("The.data.has.been.deleted..Please.don't.delete.again."));
		}
		else{
			$(olParent).addClass("banDate2");
			zNodesView.prototype.removeNode(operateDatas);
			var obj = {};
			obj.cell = me.cell;
			obj.thisDataId = operateDatas;
			obj.nodesDefaultVal = null;
			obj.cells = null;
			var operates = new operateData(obj);
			operates.addCellDefaultVal();
            $(olParent).find("font").attr("color","black");
			$(".shade").alert(GetValueByKey("The.data.has.been.deleted."));
		};
	})
	addEvents.on("ol .addPng",thisDom,"click",function(){
		var olParent = $(this).parents("ol");
		var operateDatas = $(olParent).attr("data-name");
		var $isHasBanData = $(olParent).hasClass("banDate2");
		if($isHasBanData){
			$(olParent).removeClass("banDate2");
			var obj = {};
			obj.cell = me.cell;
			obj.thisDataId = operateDatas;
			obj.nodesDefaultVal = null;
			obj.cells = null;
			var operates = new operateData(obj);
			operates.addCellDefaultVal();
			$(".shade").alert(GetValueByKey("The.data.has.been.added."));
			$(this).hide();
			$(olParent).find(".autoMatch").hide();
			$(olParent).find(".delPng").show();
		}
		else{
			$(".shade").alert(GetValueByKey("The.data.has.been.added.")+GetValueByKey("Please.don't.repeat.to.add."));
		}
	})
	addEvents.on("ol .autoMatch",thisDom,"click",function(){
		var olParent = $(this).parents("ol");
		var operateDatas = $(olParent).attr("data-name");
		var obj = {};
		obj.cell = me.cell;
		obj.thisDataId = operateDatas;
		obj.nodesDefaultVal = null;
		obj.cells = objSaveBeforeCell.cells;
		var operates = new operateData(obj);
		operates.automatically();
        var cellVal = operates.cell.defaultVal;
        cellVal.map(function(x){
        	if(x.name==$(olParent).attr("data-name")){
                $(olParent).attr("data-id",x.dataID);
                $(olParent).attr("data-receiveid",x.id);


            }
        });
		$(".shade").alert(GetValueByKey("The.data.has.been.matched"));
		$(olParent).removeClass("banDate2");
		$(this).hide();
		$(olParent).find(".addPng").hide();
		$(olParent).find(".delPng").show();
	})
}

var addEvents = {};
addEvents.on = function(dom,parentDom,type,func){
	parentDom.on(type,dom,func);
}
addEvents.off = function(dom,parentDom,type,func){
	parentDom.off(type,dom,func);
}
addEvents.setCursor = function(){
	for(var i = 0,l = arguments.length; i < l;i++){
		$(arguments[i]).css("cursor","no-drop");
	}	
}
addEvents.removeCursors = function(){
	for(var i = 0,l = arguments.length; i < l;i++){
		$(arguments[i]).css("cursor","default");
	}	
}

var objSaveBeforeCell = {};				//保存当前节点以前的cells

//操作cell数据删除增加
var operateData = function(obj){
	this.cell = obj.cell;
	this.beforeCells = obj.cells;		//当前上方cell
	this.nodesDefaultVal = obj.nodesDefaultVal;
	this.thisDataId = obj.thisDataId;	
	this.changeID = null;
}
operateData.prototype = {
	getDefaultVal : function(cell){			//获取cell的默认参数值
		return cell.defaultVal;
	},getCell : function(opeateId){			//给定一个cell的id，获取到对应的cell
		var cells = this.beforeCells;
		var cell = this.cell;
		var minCell = cell;
		cells.map(function(x){
			if(x.pId == opeateId && minCell.geometry.y > x.geometry.y){
				minCell = x;
			}
		})
		return minCell;
	},getChangeID : function(){
		var cell = this.cell;
		var cellName = this.thisDataId;
		var that = this;
		cell.defaultVal.map(function(x){
			if(cellName == x.name){
				that.changeID = x.dataID;
			}
		})
	},matchData : function(){			//匹配数据
		var operatesId = this.nodesDefaultVal.cellId;
		var changeCellValId = this.nodesDefaultVal.id;
		var operatesDateId = this.nodesDefaultVal.dataID;
		this.getChangeID();
		var beginCell = page.getBeginCell();
		this.changeBeginDefaultVal(this.thisDataId);
		var thisCellChange = this.returnCellObj(this.cell,this.thisDataId,changeCellValId,"id");
		var begincellsChange = this.returnCellObj(beginCell,operatesDateId,operatesDateId + "," + this.changeID,"dataID");
		this.changeDataId(begincellsChange,thisCellChange);
	},returnCellObj : function(cell,dataID,changeId,changeType){
		var obj = {
			"cell" : cell,
			"dataID" : dataID,
			"changeId" : changeId,
			"changeType" : changeType
		}
		return obj;
	},changeDataId : function(){					//改变当前cell默认值
		var arg = arguments;
		var l = arg.length;
		var defaultVal = [];
		if(l > 0){
			for(var i = 0 ; i < l;i++){
				defaultVal.push(arg[i].cell.defaultVal);
				var dataID = arg[i].dataID;
				var changeId = arg[i].changeId;
				var changeType = arg[i].changeType;
				defaultVal[i].map(function(x){
					if(changeType == "id"){
						if(x["name"].indexOf(dataID) >= 0 && x["id"].indexOf(x["dataID"].split("_")[1]) >= 0){
							if(changeId.indexOf("Reply") >= 0){
								changeType = "dataID";
								x.id = changeId.replace("Reply_","");
							}
							x[changeType] = changeId;

							delete x.addClassName;
						}
					}
					else{
						if(x[changeType].indexOf(dataID) >= 0){
							x[changeType] = changeId;
							delete x.addClassName;
						}
					}					
				})
			}
		}
	},addCellDefaultVal : function(){						//添加删除cell默认数值,不改变begincell的值
		var cellVal = this.cell.defaultVal;
		var operateData = this.thisDataId;
		var that = this;
		cellVal.map(function(x){
			var nameVal = x.name.split("_");
			var operateDataVal = operateData.split("_");


            if(operateData == x.name && !x.addClassName){
				var Id = "Receive" + "_" + operateDataVal[0].replace("param","") + "_" + nameVal[nameVal.length-1];
                zNodesView.prototype.removeNode(Id);					//输出指向
				x.addClassName = "banDate2";
				that.changeBeginDefaultVal(x.name);

				if(x.dataID.indexOf("Reply") >= 0){
					x.dataID = operateDataVal[0].replace("param","") + "_" + operateDataVal[1];
                    x.id = Id;
				}
			}
			else if(operateData == x.name && x.addClassName){
				delete x.addClassName;				
				var idVal = x.id.split("_");
				if(nameVal[nameVal.length-1] != idVal[idVal.length-1]){
					x.id = "Receive" + "_" + operateDataVal[0].replace("param","") + "_" + nameVal[nameVal.length-1];
					x.dataID = operateDataVal[0].replace("param","") + "_" + operateDataVal[1];
				}
				var treeNodes = {};
				treeNodes.name = x.parameterDesc + "[" + x.dataType + "]";
				treeNodes.pId = x.type;
				treeNodes.receive = x.id;
				treeNodes.dataID = x.dataID;
				treeNodes.id = x.dataID;
				treeNodes.cellDefaultVal = x;
				zNodesView.prototype.removeNode(x.id);					//删除输出指向
				zNodesView.prototype.addNodeToParent(treeNodes);		//加入tree
				var tempData = {};
				for(var i in x){
					tempData[i] = x[i];
				}
				var beginDefaultVal = that.getDefaultVal(page.getBeginCell());
				beginDefaultVal.push(tempData);
			}
		})
	},automatically : function(){
		var cell = this.cell;
		var cellID =cell.pId;
		var matchCell = this.getCell(cellID);
		if(matchCell.id == cell.id){
			this.addCellDefaultVal();
		}
		else{
			var tempDesc = null;
			var tempID = null;
			var that = this;
			cell.defaultVal.map(function(x){
				if(that.thisDataId == x.dataID && x.addClassName){				
					tempDesc = x.parameterDesc;
				}
			})
			matchCell.defaultVal.map(function(x){
				if(tempDesc == x.parameterDesc){
					tempID = x.id;
				}
			})
			cell.defaultVal.map(function(x){
				if(that.thisDataId == x.dataID && x.addClassName){
					delete x.addClassName;
					x.id = tempID;
				}
			})
		}
	},changeBeginDefaultVal : function(dataId){				//改变开始节点默认值
		var thisCellValId;
		var beginDefaultVal = this.getDefaultVal(page.getBeginCell());
		var that = this;
		beginDefaultVal.map(function(x,index){
			if(x.name.indexOf(dataId) >= 0){				
				var tempData = x.dataID.split(",");
				if(tempData.length <= 1){
					beginDefaultVal.splice(index,1);
				}
				x.dataID = x.dataID.replace(that.thisDataId,"").replace(",,",",");
				if(x.dataID.substr(-1,1) == ","){
					x.dataID = x.dataID.slice(0,-1);
				}
				if(x.dataID[0] == ","){
					x.dataID = x.dataID.slice(1);
				}		
			}
		})
	}
}
