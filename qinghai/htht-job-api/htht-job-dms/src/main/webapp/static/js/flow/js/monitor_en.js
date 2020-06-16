define(["monitor"],function(Route){
	/**
	 * 基于原生的进度条插件
	 */
	var Route = function(setting){
		this.eleType = "div";
		this.director = setting.director;
		this.length = setting.length;
		this.config = setting.config;
		this.appendTo = setting.appendTo;
		this.configDirector = {
			horizontal : "width",
			vertical : "height"
		};
	}
	Route.prototype = {
		createEle : function(){
			var ele = document.createElement(this.eleType);
			return ele;
		},
		createDirector : function(){			
			var dom = this.createEle();
			dom.className = "commonCss";
			dom.id = this.director;
			dom.style[this.configDirector[this.director]] = this.config.routeLength + "px";
			this.appendTo.appendChild(dom);
			return this;
		},
		createSchedule : function(){
			var dom = this.createEle();
			dom.className = "loadSchedule";
			dom.style[this.configDirector[this.director]] = parseInt(this.config.routeLength/this.length) + "px";
			document.getElementById(this.director).appendChild(dom);
		},
		getSingle : function(fn){
			var result;
			var that = this;		
			return function(){
				return result || (result = fn.apply(that,arguments));
			}
		},
		setLength : function(len){
			this.length = len;
			document.getElementById(this.director).
			style.width = this.length * parseInt(this.config.routeLength/this.length) + "px";
		}
	}
	//观察者模式
	var Observer = (function(){
	//防止消息队列暴露而被篡改故将消息容器作为静态私有变量保存
		var _messages = {};
		return {
			//消息信息接口
			regist : function(type,fn){
				if(typeof _messages[type] === "undefined"){
					_messages[type] = [fn];
				}
				else{
					_messages[type].push(fn);
				}
			},fire : function(type,args){
				if(!_messages[type]){
					return;
				}
				var events = {
					type : type,
					args : args || {}
				},
				i = 0,
				len = _messages[type].length;
				for(;i < len;i++){
					_messages[type][i].call(this,events);
				}
			},remove : function(type,fn){
				if(_messages[type] instanceof Array){
					var i = _messages[type].length - 1;
					for(;i >= 0;i--){
						_messages[type][i] === fn && _messages[type].splice(i,1);
					}
				}
			}
		}
	})();
	var releaser = function(){}
	releaser.prototype.msg = function(question,len){
		var num = Number($(".sum").html());
		var thisSum = Number($(".this_sum").html());
		Observer.fire(question,len);
	}
	var taker = function(fn){
		var that = this;
		that.result = fn;
		that.active = function(arg){
			that.result();
		}
	}
	taker.prototype.receive = function(question){
		Observer.regist(question,this.active);
	}
	taker.prototype.sleep = function(question){
		Observer.remove(question,this.active);
	}	
	
	var Scoket = function(url,msgId,cells){
		this.msgId = msgId;
		this.url = url;
		this.cells = cells;
		this.scoket = new WebSocket(url);
		return this;
	}
	Scoket.prototype.link = function(){
		var that = this;
		var cells = this.cells;
		//耦合度不好放在这
		this.scoket.onmessage = function(e){
			var data = e.data;
			if(!data){
				return;
			}
			data = JSON.parse(data.replace("{#start#}","").replace("{#end#}","")).msgBody;		//取出msgBody
			var manageData = new ManageData(data,cells);				//收到消息进行数据处理
			var cell = manageData.saveDataIncell();						//

			/*var isRealAdd = isReal(cell);
			var i = Number($(".this_sum").html());							//当前进度初识为0
			if(i < serverceLength && isRealAdd && !cell.appointedId){
				rele.msg("add");											//发布消息
				$(".this_sum").html(++i);									
			}*/
			if(cell){
				$(".detail").addClass("show");
				var showPropertyIn = new showProperty(cell);
				showPropertyIn.showInDetail();				
			}
			page.graph.setSelectionCell(cell);
		}		
		this.scoket.onopen = function(e){
			this.send("{#start#}{'msgId':"+that.msgId+",'msgType':'monitor'}{#end#}");
			$(".link").html("The server is already connected.").css({color:"#000"});			
		}
		this.scoket.onerror = function(e){			
			$(".link").html("An error occured,The server has disconnected.").css({color:"red"});
		}
		this.scoket.onclose = function(e){
			$(".link").html("The server has disconnected,Trying to reconnect.").css({color:"red"});
			// var soc = new WebSocket(that.url);
			new Scoket(that.url,that.msgId,that.cells).link()
			// this.onmessage.apply(soc,arguments);
		}	

		
	}
	var ManageData = function(data,cells){
		this.data = data;
		this.cells = cells;
	}
	ManageData.prototype = {
		getCell : function(FigureId){		//根据ID查找出cell
			var cell,
				cellID = FigureId,
				cells = this.cells;
			cells.map(function(x){
				if(x.id === cellID){
					cell = x;
				}
			})
			return cell;
		},saveDataIncell : function(){	
			var FigureId = this.data.figureId;
			var cell = null,
				graph = page.graph;
			switch(Number(FigureId)){
				case 1:
					cell = page.getBeginCell();
					cell.data = cell.data || {};
					this.mapData("start node","flow starts",cell.data);	
				break;
				case 2:
					cell = page.cellEnd;
					cell.data = cell.data || {};
					this.mapData("end node","flow starts",cell.data);
					var beginCell = page.getBeginCell();
					cell.data.in_put = beginCell.data.in_put;
					beginCell.data.out_put = cell.data.out_put;
				break;
				default :
					cell = this.getCell(FigureId);
					if(cell){
						cell.data = cell.data || {};
						this.mapData("common service",cell.value,cell.data);
					}
				break;
			}
			var serverceLength = Number($(".sum").html());							//流程总个数
			var thisSum = Number($(".this_sum").html());							//当前进度初识为0
			
			if(cell && this.data.status){
				if(this.data.status == 30){
					page.isAddCellOverlay(cell);
					var cellData = cell.data;
					for(var i in cellData){
						if(cellData[i] == undefined){
							cellData[i] = "";
						}
					}
					if(FigureId == 2){
						$(".pause_btn").hide();
					}
					else{
						$(".pause_btn").show();
					}				
					page.graph.removeCellOverlays(cell);
				}
				if(this.data.status == 20){				
					cell.data.carry_state = "executing...";
					page.isAddCellOverlay(cell,this.data.status);
				}
				if(cell.id != "begin" && cell.id != "end" && thisSum < serverceLength && 
					!cell.appointedId){						
					if(this.data.status != 20){
						cell.appointedId = this.data.appointedId;	
						if(this.data.status == 30){
							$(".this_sum").html(++thisSum);
							var rele = new releaser();								//发布者
							rele.msg("add");										//发布消息
						}
					}
				}
			}	
			
			return cell;
		},mapData : function(serviceType,serviceName,cellData){			//遍历数据，将数据返回存入cell
			var obj = cellData,
				data = this.data;
			obj.service_type = serviceType;
			obj.service_desc = serviceName;
			obj.node_type = data.endPoint ? data.endPoint : obj.node_type;
			obj.task_number = data.appointedId ? data.appointedId : obj.task_number;
			obj.start_time = data.startTime ? data.startTime : obj.start_time;
			obj.end_time = data.endTime ? data.endTime : obj.end_time;
			obj.out_put = data.output ? data.output : obj.out_put;
			obj.in_put = data.input ? data.input : obj.in_put;
			obj.carry_state = data.status == 40 ? data.error : "completed";
			if(data.status == 40){
				$(".carry_state").css("color","red");
			}
			else{
				$(".carry_state").css("color","#000");
			}
			return cellData;
		},destoryCellData : function(){
			var cells = page.getCellsFromBegin();
			var graph = page.graph;
			$(".this_sum").html("0");		
			cells && cells.map(function(x){
				delete x.data;
				if(x.isEdge()){
					return;
				}
				if(x.id == "begin" || x.id == "end"){
					graph.stylesheet.styles[x.getStyle()].gradientColor = "#FFF";
				}
				else{
					if(x.id.indexOf("flowCell") < 0 && x.id.indexOf("sequence") < 0){
						graph.stylesheet.styles[x.getStyle()].gradientColor = "#F5BCBC";
					}
				}
				graph.removeCellOverlays(x);
				graph.refresh(x);
			})
		}
	}
	var showProperty = function(cell){			//显示详情属性类
		$(".detail").addClass("show");
		this.cell = cell;
	}
	showProperty.prototype = {
		showInDetail : function(){				//
			var data = this.cell.data;
			if(this.cell.id.indexOf("end") >= 0){
				$(".rewrite_para").hide();
			}
			for(var i in data){				
				if(i != "in_put" && i != "out_put"){
					$("."+i).html("");
					$("."+i).html(data[i]);
				}
				else if(i == "in_put" || i == "out_put"){
					this.createLi(data[i],i);
				}
			}
		},createLi : function(arr,selector){
			var str = "",
				i = 0;
			if(!arr){
				return;
			}
			arr.map(function(x,index){
				var inVal = x.value;
				if(x.select){
					x.select.map(function(x){
						if(x.value == inVal){
							inVal = x.label;
						}
					})
				}
				var liHeight = parseInt(((inVal.toString().length) * 200)/320 + 40)
				//var reg = new RegExp(/\//g);
				//inVal = inVal.replace(reg,"\\").replace(/\/\//g,"\\	");

				str += 	'<li style="height:'+liHeight+'px;" class="width20 odd' + index % 2 + ' border_right">' + x.text + '</li>' +
						'<li style="height:'+liHeight+'px;" class="width79  odd' + index % 2 + ' " contenteditable><span>' + inVal + '</span></li>';
				i = index;
			})
			//多生成3行
			for(var j = 0;j < 3; j++){
				i++;
				str += '<li class="width20 odd' + i % 2 + ' border_right"></li>'+
					   '<li class="width79 odd' + i % 2 + '"></li>';
			}			
			$("." + selector).html(str);
		}
	}
	return {
		Route : Route,
		Observer : Observer,
		releaser : releaser,
		taker : taker,
		ManageData : ManageData,
		showProperty : showProperty,
		Scoket : Scoket
	}
})