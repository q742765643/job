//存放接口地址
var httpDateAndHeader = {
	date : new Date().getTime(),
	urlHeader : location.origin 
}
var httpUrls = {	
	treeUrl : httpDateAndHeader.urlHeader + "/PIESoaServer/processDesigner/getAllServiceTree",
	getDataUrl : httpDateAndHeader.urlHeader + "/PIESoaServer/service/getServiceById",
	saveFileUrl : httpDateAndHeader.urlHeader + "/PIESoaServer/processDesigner/saveLocal",
	deployUrl : httpDateAndHeader.urlHeader + "/PIESoaServer/process/deployCallback",
	getAllProcessList : httpDateAndHeader.urlHeader + "/PIESoaServer/process/getAllProcessList",
	getProcessXmlById : httpDateAndHeader.urlHeader +'/PIESoaServer/process/getProcessXmlById',
	saveProcessFromDesigner : httpDateAndHeader.urlHeader + "/PIESoaServer/process/saveProcessFromDesigner"
}

//重写indexof方法，property为属性值
Array.prototype.indexOf = function(data,property){
	var index = -1;
	for(var i=0;i<this.length;i++){
		if(property){
			if(data === this[i][property]){
				index = i;
			}			
		}
		else{
			if(data === this[i]){
				index = i;
			}		
		}			
	}
	return index;
}

//map遍历
Array.prototype.map = function(fun){
	var arr=[];
	for(var i = 0,length = this.length;i < length;i++){
		if(i in this){
			var r = fun(this[i],i);
			arr[i] = r;
		}
	}
	return arr;
}

var page = {
	httpUrl : httpUrls.treeUrl,
	graph : null,
	tree : null,
	zNodes : null,
	vContain : null,
	istrue : true,
	defaultParent : null,
	num2 : 0,
	edgeId : 0,
	cellEnd:null,
	init : function(isLocked){
		this.initMxgraph();		//初始化画布
		if(isLocked){
			this.graph.setCellsLocked(true);
		}
		$.fn.alert = page.alert;										//扩展jq方法
		this.reWriteFn();
		this.graphEvents();
		$("body").height($(document).height());
		$(".shade").height($(document).height());
	},initMxgraph : function(){
		var container = document.getElementById('graphContainer');
		var model = new mxGraphModel();
		var graph = new mxGraph(container, model); 
		this.graph = graph;
		mxSwimlane.prototype.crisp = false;		
		var rWidth = page.getRightWidth();
		if (mxClient.IS_IE){
			$("#graphContainer").css({width:rWidth,height:"100%"});
		}
		graph.setCellsResizable(false);						//cell是否可扩大
		graph.minimumContainerSize = new mxRectangle(0, 0, parseInt(rWidth), 600);
		graph.setConnectable(false);	 					//出现线条拖动
		// var highlight = new mxCellTracker(graph, '#00FF00');			//高亮
		var container = document.getElementById('graphContainer');
		mxEvent.disableContextMenu(container);			//禁用浏览器默认的右键菜单栏
		this.edgeStyleSheet(graph);
		
		//初始化页面开始和结束块
		this.initBeginEnd(container,graph);
		//鼠标滚轮放大缩小事件
		//this.scrollZoom(graph);
		graph.border = 100;
		graph.setEdgeLabelsMovable(false);
		graph.setCellsDeletable(true);
		graph.graphHandler.setRemoveCellsFromParent(false);
		graph.setResizeContainer(true);
		graph.setTooltips(true);			//鼠标悬停是否显示信息
		graph.setCellsLocked(false);		//锁定cell不让cell移动
		graph.setAutoSizeCells(true);
		graph.setAllowDanglingEdges(false);
		graph.setDisconnectOnMove(false);
		mxSpaceManager.prototype.extendParents = false;
		graph.panningHandler.autoExpand = true;
		this.flod(graph);
		this.keyHandler(graph); 	
	},addChild : function(child){				//添加子节点cell
		var graph = page.graph,
			style;

		if(page.defaultParent.id.indexOf("sequence") >= 0){
			return page.addSenquenCell(child);
		}
		if(page.defaultParent.id.indexOf("flowCell") >= 0){
			page.istrue = false;
		}
		if(child.id.indexOf("flowCell") >= 0){
			style = this.baseStyle();
			style[mxConstants.STYLE_PERIMETER] = mxPerimeter.CustomPerimeter;
			style[mxConstants.STYLE_OPACITY] = 80;			
		}
		else if(child.id.indexOf("sequence") >= 0){
			style = this.baseStyle();
		}
		else{
			style = this.vertexBaseStyle();			//调用样式函数
			style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;	
		}	
		var styleName = child.name;
		if(!child.name || child.name == ""){
			styleName = child.id;
		}	
		this.vertexOwnStyle(graph, style,styleName, null);		//调用样式函数
		var prototype = new mxCell(child.name,new mxGeometry(0,0,child.width,child.height),styleName);//流程框宽100高50
		prototype.setVertex(true);
		var vertex = graph.getModel().cloneCell(prototype);
		var cell;
		var beginCells = page.getBeginCell();
		graph.model.setCollapsed(vertex,false);
		var vCX = page.vContain.geometry.x;
		var vCY = page.vContain.geometry.y;
		var index = 0;
		cell = vertex;		
		cell.pId = parseInt(child.id);
		cell.id = child.id;
		if(page.istrue){			
			//获取容器距离画布位置
			vertex.geometry.x = 0;
			vertex.geometry.y = child.y - vCY;
			page.defaultParent = vertex;
			var nextCell,prevCell,nextEdge,prevEdge;
			
			if(!page.vContain.children || page.vContain.children.length == 0){
				//移除边缘刷新
				nextCell = page.getNextCells(page.vContain);
				prevCell = page.getNextCells(page.vContain,true);

				nextEdge = page.getEdge(page.vContain,"source");
				prevEdge = page.getEdge(page.vContain,"target");

				nextEdge.removeEdge(nextEdge,true);
				graph.refresh(nextEdge);
				prevEdge.removeEdge(prevEdge,true);
				graph.refresh(prevEdge);

				//重绘边缘指向
				graph.insertEdge(page.vContain,'edge-' + (page.edgeId++),null,vertex,page.vContain.target);
				graph.insertEdge(page.vContain,'edge-' + (page.edgeId++),null,page.vContain.source,vertex);
				
				graph.addCell(vertex,page.vContain);
			}
			else{
				var betweenCellObj = page.getBetweenCell(page.vContain,vertex);
				var inCellUp = betweenCellObj.inCellUp;
				var inCellNext = betweenCellObj.inCellNext;
				var targetEdge = betweenCellObj.targetEdge;
				index = page.vContain.getIndex(inCellNext);
				if(index < 0){
					index = graph.model.getChildCount(page.vContain);
				}				
				targetEdge.removeEdge(targetEdge,true);
				graph.refresh(targetEdge);
				graph.insertEdge(page.vContain,'edge-' + (page.edgeId++),null,inCellUp,vertex);
				graph.insertEdge(page.vContain,'edge-' + (page.edgeId++),null,vertex,inCellNext);

				graph.addCell(vertex,page.vContain,index);	
			}	
		}
		else{
			var parent = page.defaultParent,
				dCY = 0,
				dCX = 0;
			while(parent.geometry){				
				dCX += parent.geometry.x;
				dCY += parent.geometry.y;
				parent = parent.getParent();
			}
			dCY = child.y - dCY;
			if(dCY >= page.defaultParent.geometry.height - 30){
				dCY -= 30;
			}
			vertex.geometry.x = child.x - dCX;
			vertex.geometry.y = dCY;
			graph.cellSizeUpdated(cell,false);
			cell = graph.addCell(vertex,page.defaultParent);				
			if(cell.id.indexOf("flowCell") >= 0){
				alert("You replace a new parallel component into the parallel component which could cause the pisition displayed an error. You can delete the new parallel component or call the parent component in parallel.")
			}
		}		
		cell = vertex;

		graph.setCellsResizable(false);
		graph.setSelectionCell(cell);
		return cell;
	},changeValue : function(cell,graph,changeValue){
		var begincell = page.getBeginCell();
		var cellDefaultVal = cell.defaultVal;
		var cellId = cell.id;
		var beginDefaultVal = begincell.defaultVal;
		cellDefaultVal && cellDefaultVal.map(function(x){
			x.group = changeValue;
		})
		beginDefaultVal && beginDefaultVal.map(function(x){
			if(cellId == x.cellId){
				x.group = changeValue;
			}
		})
	},getBetweenCell : function(parentCell,thisCell){
		var graph = page.graph;
		var cells = graph.getChildCells(parentCell,true);
		var thisCellY = thisCell.geometry.y;
		var inCellUp = null;
		var inCellNext = null;
		var obj = {};
		var targetEdge = null;
		cells && cells.map(function(x){
			var cellY = x.geometry.y;
			var cellHeight = x.geometry.height;
			var distanceOnCell = cellY + cellHeight;
			if(thisCellY <= distanceOnCell + 30 && thisCellY >= cellY){
				inCellUp = x;
			}
		})
		if(inCellUp){
			inCellNext = page.getNextCells(inCellUp);
		}
		else{
			inCellUp = page.getBeginCell();
			inCellNext = page.getNextCells(inCellUp);
		}
		targetEdge = page.getEdge(inCellUp,"source");
		obj.inCellUp = inCellUp;
		obj.inCellNext = inCellNext;
		obj.targetEdge = targetEdge; 
		return obj;
	},addLabel : function(graph,cell,value,id){
		graph.constrainChildren = false;
		graph.extendParents = false;
		graph.extendParentsOnAdd = false;
		var style = this.baseStyle();					//调用样式函数
		style[mxConstants.STYLE_PERIMETER_SPACING] = 0;
		graph.getStylesheet().putCellStyle(value, style);
		var x = cell.geometry.width/2;
		var y = cell.geometry.y;
		var vertex2 = new mxCell(value,new mxGeometry(0,20,80,40));
		var cellCh = graph.insertVertex(cell, id, value,x-10, -10, 40,20,value);

		graph.constrainChildren = true;
		graph.extendParents = true;
		graph.extendParentsOnAdd = true;
	},isAddCellOverlay : function(cell,status){							//是否 添加/移除 loading
		var graph = page.graph;
		var overlays = graph.getCellOverlays(cell);
		if(overlays == null){
			var overlay = new mxCellOverlay(new mxImage('images/warning.gif',16, 16), 'loading');
			overlay.cursor = 'hand';
			overlay.offset = new mxPoint(-8, 0);
			overlay.align = "left";
			overlay.verticalAlign = "middle";			
			graph.addCellOverlay(cell, overlay);
			if(cell.id.indexOf("begin") >= 0 || cell.id.indexOf("end") >= 0){
				graph.stylesheet.styles[cell.getStyle()].gradientColor = "#8BC190";
				graph.refresh(cell);				
			}
		}
		else{
			graph.stylesheet.styles[cell.getStyle()].gradientColor = "#8BC190";
			graph.refresh(cell);
		}		
		if(status == 20){
			graph.stylesheet.styles[cell.getStyle()].gradientColor = '#F5BCBC';
			graph.refresh(cell);
		}
	},getRightWidth : function(){
		var rWidth = $(".right").width() - 20;
		return rWidth;
	},moveLabel : function(graph,labelCell){
		var parentCell = labelCell.getParent();
		var cellCenterX = parentCell.geometry.width/2;
		var dx = cellCenterX - labelCell.geometry.getCenterX();
		graph.constrainChildren = false;
		graph.extendParents = false;
		graph.extendParentsOnAdd = false;
		graph.moveCells([labelCell],dx,0);	
		graph.constrainChildren = true;
		graph.extendParents = true;
		graph.extendParentsOnAdd = true;		
	},initBeginEnd : function(container, graph){
		mxRectangleShape.prototype.crisp = true;
		new mxRubberband(graph);		
		var model = new mxGraphModel();
		var parent = graph.getDefaultParent();
		// graph.setEnabled(false);//设置gragh图只读
		graph.getModel().beginUpdate();
		try {                                                                                                                                                                                                                                                                                                                                                     
			//设置style样式
			var style = page.baseStyle();
			this.vertexOwnStyle(graph, style, "base", null);		//调用样式函数
			var style2 = {};
			for(var i in style){
				style2[i] = style[i];
			}
			var rWidth = page.getRightWidth();
			graph.getStylesheet().putCellStyle('container', style2);
			var vStart = graph.insertVertex(parent, null, "start", rWidth/2 - 40, 15, 80,30,"base");
			var centerX = vStart.geometry.getCenterX(); 
			var vEnd = graph.insertVertex(parent, null,  "end", rWidth/2 - 40, 280, 80,30,"base");
			page.vContain = graph.insertVertex(parent, null, '', rWidth/2 - 40, 80, 300,160,"container");
			this.centerCell.apply(null,[graph,[page.vContain],centerX]);
			page.begincells = vStart;			
			vStart.setId("begin");   
			vStart.pId = "begin";
			vEnd.setId("end");
			vEnd.pId = "end";
			page.cellEnd = vEnd;		
			page.vContain.setId("vContain");
			page.vContain.pId = "vContain";
			page.vContain.source = vStart;
			page.vContain.target = vEnd;
			graph.insertEdge(parent,'edge-' + (page.edgeId++),null,vStart,page.vContain);
			graph.insertEdge(parent,'edge-' + (page.edgeId++),null,page.vContain,vEnd);	
			page.defaultParent = page.vContain;
		} 
		finally {
			graph.getModel().endUpdate();
		}
	},edgeStyleSheet : function(graph) {
		var style2 = graph.getStylesheet().getDefaultEdgeStyle();
		mxEdgeStyle.MyStyle = function(state, source, target, points, result){
		    if (source != null && target != null){
		    	var pt = new mxPoint(target.getCenterX(), source.getCenterY());		  
		      	if (mxUtils.contains(source, pt.x, pt.y)){
		       		pt.y = source.y + source.height;
		      	}		  
		      	result.push(pt);
		    }
		};
		style2[mxConstants.STYLE_STROKECOLOR] = '#000';
		style2[mxConstants.STYLE_SHADOW] = false;
		style2[mxConstants.STYLE_EDGE] = mxEdgeStyle.MyStyle;
		style2[mxConstants.STYLE_STROKEWIDTH] = 1;
		style2[mxConstants.STYLE_EDITABLE] = 0;
		return style2;
	},keyHandler : function(graph) {		
		document.onkeydown = function(evt){
			evt = evt || event;
			if(evt.keyCode && (evt.keyCode == 46) && graph.selectionModel){
				var cell = graph.getSelectionCell();
				if(cell && (cell.id == "begin" || cell.id == "vContain" || cell.id == "end")){
					mxUtils.error("The component cannot be deleted.", 280,true);
				}else{
					if(cell && graph.isEnabled()){
						if(cell.id.indexOf("label") >= 0){
							cell = cell.getParent();
						}
						graph.removeCells([cell]);
					}					
				}
			}
		}
	},changeCellValue : function(changeCell,cellValue){
		var graph = page.graph;
		changeCell.value = cellValue;
		graph.refresh(changeCell);
		graph.cellSizeUpdated(changeCell,false);
		var parentCell = changeCell.getParent();
		page.centerCell(graph,[changeCell],parentCell.geometry.width/2);
		while(parentCell.geometry){
			if(parentCell.id.indexOf("flowCell") < 0){
				var cells = graph.getChildCells(parentCell,true);
				var pWidth = parentCell.geometry.width/2;
				page.centerCell(graph,cells,pWidth);
			}
			if(parentCell.id == "vContain"){
				var centerX = page.cellEnd.geometry.getCenterX(); 
				page.centerCell(graph,[parentCell],centerX);
			}
			parentCell = parentCell.getParent();
		}		
	},baseStyle : function(){
		var graph = page.graph;
		// var style = graph.getStylesheet().getDefaultVertexStyle();
		var style = {};
		style[mxConstants.STYLE_OPACITY] = 80;			
		style[mxConstants.STYLE_STROKECOLOR] = 'gray';
		style[mxConstants.STYLE_ROUNDED] = false;
		style[mxConstants.STYLE_SHADOW] = false;
		style[mxConstants.STYLE_FILLCOLOR] = '#FFF';
		style[mxConstants.STYLE_GRADIENTCOLOR] = 'white';
		style[mxConstants.STYLE_FONTCOLOR] = 'black';
		style[mxConstants.STYLE_FONTSIZE] = '12';
		style[mxConstants.STYLE_NOEDGESTYLE] = 1;
		style[mxConstants.STYLE_PERIMETER_SPACING] = 2;
		style[mxConstants.STYLE_EDITABLE] = 0;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = "middle";
		return style;
	},vertexOwnStyle : function(graph, style, serviceName, serviceIcon) {
		graph.getStylesheet().putCellStyle(serviceName, style);
	},vertexBaseStyle : function(){
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
		style[mxConstants.STYLE_GRADIENTCOLOR] = '#8BC190';
		return style;
	},alert : function(msg){		
	/*	if($(document).find(".info").html()){
			return;
		}*/
		var info = $("<div class='info'></div>");
		info.appendTo("body");
		msg = msg || $(".info").html();
		$(".info").html(msg).addClass("infoAct");
		setTimeout(function(){
			$(".info").removeClass("infoAct");
			setTimeout(function(){
				$(".info").remove();
			},500);
		},2000);		
	},isAddWidth : function(ele){
		var tabHeight = $(ele).find("ol");
		if(tabHeight.length * 30 >= 340){
			$(ele).width("102.45%");
		}
		else{
			$(ele).width("100%");
		}
	},liMousemove : function(e){
		e = e || event;
		$(".prompt").show().html($(this).html()).css({left:e.clientX - 4,top:e.clientY + 3});
		return false;
	},liMouseout : function(){
		$(".prompt").hide();
		return false;		
	},reWriteFn : function(){
		mxTooltipHandler.prototype.zIndex = 10;
		mxGraph.prototype.moveCells = function(cells, dx, dy, clone, target, evt){
			dx = (dx != null) ? dx : 0;
			dy = (dy != null) ? dy : 0;

			clone = (clone != null) ? clone : false;	
			if (cells != null && (dx != 0 || dy != 0 || clone || target != null)){
				this.model.beginUpdate();
				try{
					if (clone){
						cells = this.cloneCells(cells, this.isCloneInvalidEdges());

						if (target == null){
							target = this.getDefaultParent();
						}
					}

					// FIXME: Cells should always be inserted first before any other edit
					// to avoid forward references in sessions.
					// Need to disable allowNegativeCoordinates if target not null to
					// allow for temporary negative numbers until cellsAdded is called.
					var previous = this.isAllowNegativeCoordinates();
					
					if (target != null){
						this.setAllowNegativeCoordinates(true);
					}
					var constrain = true;

					if(cells[0].id.indexOf("flowCell") >= 0 && dx < 0){
						constrain = false;
					}

					this.cellsMoved(cells, dx, dy, !clone && this.isDisconnectOnMove()
							&& this.isAllowDanglingEdges(),constrain);
					
					this.setAllowNegativeCoordinates(previous);

					if (target != null){
						var index = this.model.getChildCount(target);
						this.cellsAdded(cells, target, index, null, null, true);
					}

					// Dispatches a move event
					this.fireEvent(new mxEventObject(mxEvent.MOVE_CELLS, 'cells', cells,
						'dx', dx, 'dy', dy, 'clone', clone, 'target', target, 'event', evt));
				}
				finally{
					this.model.endUpdate();
				}
			}
			
			return cells;
		};
		
		mxGraph.prototype.extendParent = function(cell){
			if (cell != null){
				var parent = this.model.getParent(cell);
				var p = this.model.getGeometry(parent);
				
				if (parent != null && p != null && !this.isCellCollapsed(parent)){
					var geo = this.model.getGeometry(cell);
					
					if (geo != null && (p.width < geo.x + geo.width)){
						p = p.clone();
						
						p.width = Math.max(p.width, geo.x + geo.width);
						
						this.cellsResized([parent], [p]);
					}
				}
			}
		};
		mxGraph.prototype.doResizeContainer = function(width, height){
			// Fixes container size for different box models
			if (mxClient.IS_IE){
				if (mxClient.IS_QUIRKS){
					var borders = this.getBorderSizes();				
					// max(2, ...) required for native IE8 in quirks mode
					width += Math.max(2, borders.x + borders.width + 1);
					height += Math.max(2, borders.y + borders.height + 1);
				}
				else if (document.documentMode >= 9){
					width += 3;
					height += 5;
				}
				else{
					width += 1;
					height += 1;
				}
				height = height - 40;	
				this.container.style.height ='100%';
			}
			else{
				height += 1;
				this.container.style.height = Math.ceil(height) + 'px';

			}
			
			if (this.maximumContainerSize != null){
				width = Math.min(this.maximumContainerSize.width, width);
				height = Math.min(this.maximumContainerSize.height, height);
			}	
			this.container.style.width = Math.ceil(width) + 'px';
		};
		mxVertexHandler.prototype.redraw = function(){
			var cell = this.state.cell;
			if(cell && cell.id.indexOf("label") >= 0){
				this.sizers = null;
			}
			this.selectionBounds = this.getSelectionBounds(this.state);
			this.bounds = new mxRectangle(this.selectionBounds.x, this.selectionBounds.y,
				this.selectionBounds.width, this.selectionBounds.height);
			if (this.sizers != null){
				var s = this.state;
				var r = s.x + s.width;
				var b = s.y + s.height;
				
				if (this.singleSizer){
					this.moveSizerTo(this.sizers[0], r, b);
				}
				else{
					var cx = s.x + s.width / 2;
					var cy = s.y + s.height / 2;
					
					if (this.sizers.length >= 1){	
						for(var nn = 0;nn<=6;nn++){
							this.sizers[nn].node.style.visibility = "hidden";
						}
						this.moveSizerTo(this.sizers[7], r, b);
						this.moveSizerTo(this.sizers[8],
							cx + s.absoluteOffset.x,
							cy + s.absoluteOffset.y);
					}
					else if (this.state.width >= 2 && this.state.height >= 2)
					{
						this.moveSizerTo(this.sizers[0],
								cx + s.absoluteOffset.x,
								cy + s.absoluteOffset.y);
					}
					else
					{
						this.moveSizerTo(this.sizers[0], s.x, s.y);
					}
				}
			}

			this.drawPreview();
		};			
		mxPerimeter.CustomPerimeter = function (bounds, vertex, next, orthogonal){
			var cx = bounds.getCenterX();
			var cy = bounds.getCenterY();
			var dx = next.x - cx;
			var dy = next.y - cy;
			var alpha = Math.atan2(dy, dx);
			var p = new mxPoint(0, 0);
			var pi = Math.PI;
			var pi2 = Math.PI/2;
			var beta = pi2 - alpha;
			var t = Math.atan2(bounds.height, bounds.width);
			
			if(alpha < -pi + t || alpha > pi - t){
				// Left edge
				p.x = bounds.x;
				p.y = cy - bounds.width * Math.tan(alpha) / 2;
			}
			else if(alpha < -t){
				// Top Edge
				p.y = bounds.y - 10;
				p.x = cx - bounds.height * Math.tan(beta) / 2;
			}
			else if(alpha < t){
				// Right Edge
				p.x = bounds.x + bounds.width;
				p.y = cy + bounds.width * Math.tan(alpha) / 2;
			}
			else{
				// Bottom Edge
				p.y = bounds.y + bounds.height;
				p.x = cx + bounds.height * Math.tan(beta) / 2;
			}
			
			if(orthogonal){
				if (next.x >= bounds.x && next.x <= bounds.x + bounds.width){
					p.x = next.x;
				}
				else if (next.y >= bounds.y && next.y <= bounds.y + bounds.height){
					p.y = next.y;
				}
				if (next.x < bounds.x){
					p.x = bounds.x;
				}
				else if (next.x > bounds.x + bounds.width){
					p.x = bounds.x + bounds.width;
				}
				if (next.y < bounds.y){
					p.y = bounds.y;
				}
				else if (next.y > bounds.y + bounds.height){
					p.y = bounds.y + bounds.height;
				}
			}
			return p;
		}
	},getNextCells : function(cell,isprev){
		var edge = cell.edges;
		var tempCell,prevCell;
		edge.map(function(x){
			if(x.target && x.source && x.target.id != cell.id){
				tempCell = x.target;
			}
			if(x.target && x.source && x.target.id == cell.id){
				prevCell = x.source;
			}
		});
		if(isprev){
			return prevCell;
		}
		else{
			return tempCell;
		}
	},getEdge : function(cell,type){			//获取cell下方或上方（source/target）的edge
 		var cellEdges = cell.edges,
 			edge = null;
 		if(!cellEdges){
 			return
 		}
		cellEdges.map(function(x){
			if(x.source && x.target && x[type].id == cell.id){
				edge = x;
			}
		})
		return edge;
	},getBeginCell : function(){
		var graph = page.graph;
		var cell = graph.getModel().cells;
		for(var i in cell){
			if(cell[i].pId == "begin"){				
				return cell[i];
			}
		}
	},getFlowChild : function(cells){			//取出所有cell
		var arr = [];
		var arr2 = [];
		var arr3 = [];
		var childCell = [];
		cells.map(function(m,index){
			if(arr.indexOf(m.id) == -1){
				arr.push(m.id);
				arr3.push(m);
			}
		})
		var unBlindCellsInArr = function(cellsArr){				//递归从并行/串行中解耦出所有的cell
			cellsArr.map(function(x){
				if(x.children){
					unBlindCellsInArr(page.graph.getChildCells(x,true));
				}
				else{
					if(x.id.indexOf("label") < 0){
						arr2.push(x);
					}
				}	
			})		
		}
		unBlindCellsInArr(arr3);
		return arr2;
	},getAllCellsDefaultVal : function(allCells,printType){				//获取参数	
		var tempArr = [];		
		var para = {
			isInput : function(data){
				if(data.type == "InputParameter"){
					tempArr.push(data);
				}
			},
			isOutput : function(data){
				if(data.type == "OutputParameter"){
					tempArr.push(data);
				}
			},
			isAllData : function(data){
				tempArr.push(data);
			}
		}
		allCells.map(function(x){
			if(x.defaultVal){
				x.defaultVal.map(function(y){
					para[printType](y);				
				})		
			}			
		});
		return tempArr;
	},moveCellsUp : function(cell){			//向上移动cell
		if(!cell.edges){
			return;
		}	
		var nextCell = page.getNextCells(cell),
			prevCell = page.getNextCells(cell,true),
			tempCell = cell,
			parentCell = cell.getParent(),
			graph = page.graph;
		if(!prevCell || !nextCell){
			return;
		}
		if(prevCell && (prevCell.id == "begin" || prevCell.id.indexOf("label") >= 0)){			
			tempCell.geometry.y = 30;
			if(prevCell.id.indexOf("label") >= 0){
				tempCell.geometry.y = prevCell.geometry.y + prevCell.geometry.height + 30;
			}			
			graph.refresh(tempCell);
		}
		if(nextCell && nextCell.id == "end"){
			page.moveEndCell();			
		}
		if(parentCell.children.length <= 1){	
			return;
		}

		//检测当前cell的上方cell是否在固定点（cell是否间距30）
		while(prevCell && prevCell.id != "begin" && prevCell.id.indexOf("label") < 0){
			var betweenCellSpace = prevCell.geometry.y + prevCell.geometry.height + 30;
			if(tempCell.geometry.y != betweenCellSpace){
				graph.moveCells([tempCell],0,betweenCellSpace - tempCell.geometry.y);
				graph.refresh(tempCell);
			}
			tempCell = prevCell;
			prevCell = page.getNextCells(tempCell,true);
		} 
		//向下移动cell
		tempCell = cell;
		while(nextCell && nextCell.id != "end" && nextCell.id.indexOf("sequence") < 0){
			var nextCellY = nextCell.geometry.y;
			var betweenCellSpace2 = tempCell.geometry.y + tempCell.geometry.height + 30;
			
			if(nextCellY != betweenCellSpace2 && nextCell.id != "end" && nextCell.id.indexOf("sequence") < 0){
				graph.moveCells([nextCell],0,betweenCellSpace2 - nextCellY);
				graph.refresh(nextCell);
			} 
			tempCell = nextCell;
			nextCell = page.getNextCells(tempCell);
			if(nextCell.id == "end"){
				page.moveEndCell();
			}
		}
	},moveEndCell : function(){								//移动end节点
		var graph = page.graph;
		var pageVCGeo = page.vContain.geometry;
		var arrivePositon = pageVCGeo.y + pageVCGeo.height + 40;
		var endY = page.cellEnd.geometry.y;				//获取end节点的Y坐标	
		if(arrivePositon - endY != 0){
			graph.moveCells([page.cellEnd],0,arrivePositon - endY);	
		}		
				
	},flod : function(graph){
		if (graph.isEnabled()){	
			// Keeps widths on collapse/expand					
			var foldingHandler = function(sender, evt){
				var cells = evt.getProperty('cells');
				
				for (var i = 0; i < cells.length; i++){
					var geo = graph.model.getGeometry(cells[i]);

					if (geo.alternateBounds != null){
						geo.width = geo.alternateBounds.width;
					}
				}
			};

			graph.addListener(mxEvent.FOLD_CELLS, foldingHandler);
		}
	},centerCell : function(graph,cells,parentCenterX){		
		var dx,dy;
		cells && cells.map(function(x){
			if(x.getParent().id.indexOf("flowCell") >= 0 && x.id.indexOf("label") < 0){
				return;
			}
			if(x.id.indexOf("label") >= 0){
				page.moveLabel(graph,x);
			}
			else{
				dx = parentCenterX - x.geometry.getCenterX();
				dy = 0;			
				graph.moveCells([x],dx,dy);
			}			
		})		
	},addSenquenCell : function(child){
		var graph = page.graph;
		var style;			//调用样式函数			
		var styleName = child.name;
		if(!styleName || styleName == ""){
			styleName = child.id;
		}
		if(child.id.indexOf("flowCell") >= 0){
			style = this.baseStyle();
			style[mxConstants.STYLE_PERIMETER] = mxPerimeter.CustomPerimeter;			
		}
		else{
			style = this.vertexBaseStyle();
			style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
		}
		this.vertexOwnStyle(graph, style, styleName, null);		//调用样式函数
		var prototype = new mxCell(child.name,new mxGeometry(0,0,child.width,child.height), styleName);//流程框宽100高50
		prototype.setVertex(true);
		var vertex = graph.getModel().cloneCell(prototype);
		var cell,index = 0;
		var parent = page.defaultParent;
		graph.cellSizeUpdated(vertex,false);		//自适应单元格
		var dY = parent.geometry.y;
		var parentCell = parent.getParent();
		while(parentCell.geometry != undefined){
			dY += parentCell.geometry.y;
			parentCell = parentCell.getParent();
		}
		dY = child.y - dY;	
		vertex.geometry.y = dY;
		if(parent.id.indexOf("sequence") >= 0 && parent.children.length <= 2){	
			graph.insertEdge(parent,"edge-"+(page.edgeId++),null,parent.children[0],vertex);
			graph.insertEdge(parent,"edge-"+(page.edgeId++),null,vertex,parent);
			cell = graph.addCell(vertex,parent,1);
			cell.pId = parseInt(child.id);
			cell.id = child.id;
			page.moveLabel(graph,parent.children[0]);
		}
		else{			
			var betweenCellObj = page.getBetweenCell(parent,vertex);
			var inCellUp = betweenCellObj.inCellUp;
			var inCellNext = betweenCellObj.inCellNext;
			var targetEdge = betweenCellObj.targetEdge;

			index = parent.getIndex(inCellNext);
			if(index < 0){
				index = graph.model.getChildCount(parent);
			}
			// if(index == 0 && inCellUp.id.indexOf("label") )
			targetEdge.removeEdge(targetEdge,true);
			graph.refresh(targetEdge);
			graph.insertEdge(parent,'edge-' + (page.edgeId++),null,inCellUp,vertex);
			graph.insertEdge(parent,'edge-' + (page.edgeId++),null,vertex,inCellNext);

			cell = graph.addCell(vertex,parent,index);
			cell.pId = parseInt(child.id);
			cell.id = child.id;
		}
	
		if(cell.id.indexOf("flowCell") >= 0){
			cell.geometry.width = 100;
			cell.geometry.height = 60;
			page.addLabel(graph,cell,"parallel","label-flow-"+cell.id.split("_")[1]);
			graph.refresh(cell);
		}

		return cell;
	},getCellsFromBegin : function(){				//获取所有cell，包括edge以及并行串行内的cell
		var beginCell = page.getBeginCell();
		var cell = beginCell;
		var cells = [],
			edges = [];
		function pushCell(thisCell,cellsArr,edgesArr){
			if(!thisCell.isEdge()){				//cell
				cellsArr.push(thisCell);
			}
			else if(thisCell.isEdge() && thisCell.source && thisCell.target){
				edgesArr.push(thisCell);
			}
		}	
		function inspect(cell){
			if(cell.children){				
				cell.children.map(function(x){
					if(x.id.indexOf("label") < 0){
						pushCell(x,cells,edges);
						inspect(x);
					}
				})
			}
			return			
		}
		while(cell.id != "end"){
			pushCell(cell,cells,edges);
			inspect(cell);
			if(!cell.isEdge()){
				cell = page.getEdge(cell,"source");
			}
			else{
				cell = cell.target;
			}
		}
		cells.push(page.cellEnd);
		var cellsAndEdges = cells.concat(edges);
		return cellsAndEdges;
	},getRemoveCellId : function(cells){
		var cellsDefault = [];
		var getCellId = function(cellsChild,cellsDefaultArr){
			if(!(cellsChild instanceof Array)){
				cellsChild = [cellsChild];
			}
			cellsChild.map(function(x){
				if(x.defaultVal){
					cellsDefaultArr.push(x.id);
				}
				else if(!x.defaultVal && x.children){
					var subChildCell = x.children;
					getCellId(subChildCell,cellsDefaultArr);
				}
			})
		}
		if(cells.children && !cells.defaultVal){
			var childCell = cells.children;
			getCellId(childCell,cellsDefault);
		}
		else{
			getCellId(cells,cellsDefault);
		}
		return cellsDefault;
	},removeDefaultVal : function(cellsId){			//移除默认数据
		var beginCellVal = page.getBeginCell().defaultVal;
		var endCellVal = page.cellEnd.defaultVal;
		var defaultValMap = function(defaultVal,id){
			for(var i = 0;i < defaultVal.length;i++){
				if(id == defaultVal[i].dataID.split("_")[0]){
					defaultVal.splice(i,1);
					i--;
				}			
			}
		}
		cellsId.map(function(x){
			defaultValMap(beginCellVal,x);
			defaultValMap(endCellVal,x);
		})
	},accountHeight : function(cell,graph,parentCell){					//累加出父容器原本高度
		var cells;
		if(parentCell){
			if(!parentCell.geometry || !cell.geometry){
				return false;
			}
			cells = graph.getChildCells(parentCell,true);
		}
		else{
			cells = graph.getChildCells(cell,true);
		}
		// var cells = graph.getChildCells(cell,true);
		var oH = (cells.length + 1) * 30;					//计算父容器中各个cell间的高度

		if(cell.getParent().id.indexOf("sequence") >= 0){
			oH = cells.length * 30 - 10;			
		}
		cells.map(function(x){
			if(x.id != cell.id && cell.id.indexOf("label") < 0){
				oH += Number(x.geometry.height);					//累加计算出当前除了新添加的cell的高度
			}					
		})
		return oH;
	},observerResizeHeight : function(cell){					//检测超出高度范围
		var cellY = cell.geometry.y,
			graph = page.graph,
			cellParent = cell.getParent();
		if(!cellParent.geometry){
			return;
		}
		var cellParentH = cellParent.geometry.height;
		var parentEndH = 0;
		if(cellParent.id.indexOf("flowCell") >= 0){
			parentEndH = cell.geometry.y + cell.geometry.height + 20;
		}
		else{
			parentEndH = page.accountHeight(cell,graph,cellParent) + cell.geometry.height;
		}
		var dy = parentEndH - cellParentH;
		if(parentEndH >= cellParentH){			//如果超出父容器范围   扩大父容器，移动cell
			while(cellParent.geometry != undefined){
				cellParent.geometry.height += dy;
				graph.refresh(cellParent);
				page.moveCellsUp(cellParent);
				cellParent = cellParent.getParent();
				cellParentH = cellParent.geometry ? cellParent.geometry.height : 0;
			}
		} 		
	},graphEvents : function(){
		var graph = page.graph;
		graph.addListener(mxEvent.CELLS_ADDED,function(sender,evt){
			var cell = evt.getProperty('cells')[0];
			if(cell != null && !cell.edge){				
				if(cell.id.indexOf('sequence') >= 0){
					page.defaultParent = cell;
					var randomNum = Math.ceil(Math.random()*100);
					page.addLabel(graph,cell,"serial","label-seq-"+cell.id.split("_")[1]);
					return;
				}
				if(cell.id.indexOf("flowCell") >= 0){
					page.defaultParent = cell;
					page.addLabel(graph,cell,"parallel","label-flow-"+cell.id.split("_")[1]);
					return;
				}		
				if(cell.id.indexOf("label") >= 0){
					cell = cell.getParent();
					page.defaultParent = cell;
				}
				graph.cellSizeUpdated(cell,false);					//自适应单元格	
				if(cell.id.indexOf("flowCell") >= 0){					
					cell.geometry.width = 180;
					cell.geometry.height = 150;					
					graph.refresh(cell);	
					page.moveLabel(graph,cell.children[0]);
				}
				if(cell.id.indexOf("sequence") >= 0){					
					cell.geometry.width = 80;
					cell.geometry.height = 100;
					graph.refresh(cell);
					page.moveLabel(graph,cell.children[0]);
				}
				var parentCell = cell.getParent(),					//当前新加入的cell的父级
					cellWidth = cell.geometry.width,				//当前cell的宽度
					cellHeight = cell.geometry.height,				//当前cell的高度
					beginCenterX = page.begincells.geometry.getCenterX(),		//画布水平中心点
					centerX;	
				page.observerResizeWidth(cell);						//监控超出宽度范围
				//扩大父容器的高度(这么计算的目的为了省去当父容器区无cell时的繁琐计算以及有cell时计算位置容易出错问题)
				if(cell.getParent().id.indexOf("vContain") >= 0 || cell.getParent().id.indexOf("sequence") >= 0){
					var oH = page.accountHeight(cell,graph,parentCell);					
					parentCell.geometry.height = cellHeight + oH;			//扩大父容器高度	
					page.observerResizeHeight(parentCell);					//监控超出高度范围
				}		
				while(parentCell.geometry != undefined){
					var cells = graph.getChildCells(parentCell,true);
					if(parentCell.getParent().geometry == undefined){	
						centerX = beginCenterX;
					}
					else{
						centerX = parentCell.getParent().geometry.width/2;
					}
					if(parentCell.getParent().id.indexOf("flowCell") < 0){
						page.centerCell.apply(null,[graph,[parentCell],centerX]);		//居中父容器					
					}
					graph.refresh(parentCell);							//父容器操作结束开始操作cell

					//居中cell
					if(parentCell.id.indexOf("flowCell") < 0){
						var pWidth = parentCell.geometry.width,
							pHeiht = parentCell.geometry.height,
							pCenterX = pWidth/2;							
						page.centerCell(graph,cells,pCenterX);				
						page.moveCellsUp(cell);					//移动cell					
					}
					else if(parentCell.id.indexOf("flowCell") >= 0){						
						page.moveCellsUp(parentCell);						
					}
					parentCell = parentCell.getParent();					
				}
			}
		});
		graph.addListener(mxEvent.CELLS_REMOVED, function(sender, evt){
			var cells = evt.getProperty('cells');
			var cell = cells[0];
			page.removeDefaultVal(page.getRemoveCellId(cell));
			var sourceCell,nextCell,parentCell;	
			if(cells.length <= 1 || cell.id.indexOf("sequence") >= 0){
				return;
			}
			cells.map(function(x){
				if(x.isEdge() && x.target && x.source){
					if(x.target.id == cell.id){
						sourceCell = x.source;
					}
					if(x.source.id == cell.id){
						targetCell = x.target;
					}
				}
			})
			parentCell = sourceCell.id=="begin" ? targetCell.getParent() : sourceCell.getParent();
			
			var oldHeights;
			if(parentCell.geometry == undefined){
				oldHeights = page.vContain.geometry.height
				page.vContain.geometry.width = 300;
				page.vContain.geometry.height = 160;
				graph.refresh(page.vContain);
				page.centerCell(graph,[page.vContain],sourceCell.geometry.getCenterX());
				graph.insertEdge(graph.getDefaultParent(),'edge-' + (page.edgeId++),null,sourceCell,page.vContain);
				graph.insertEdge(graph.getDefaultParent(),'edge-' + (page.edgeId++),null,page.vContain,targetCell);
				graph.moveCells([targetCell],0,page.vContain.geometry.height - oldHeights);
			}
			else{	
				if(targetCell.id.indexOf("sequence") < 0 || targetCell.children.length > 1){
					var tempEdge = graph.insertEdge(parentCell,'edge-' + (page.edgeId++),null,sourceCell,targetCell);
				}

				if(targetCell.id.indexOf("sequence") >= 0 || targetCell.getParent().id.indexOf("sequence") >= 0){		
					var parent = targetCell.id.indexOf("sequence") >= 0 ? targetCell : targetCell.getParent();	
					var maxWidth = page.getMaxWidthFromCellChild(parent);
					if(maxWidth <= cell.geometry.width && parent.children.length > 1){
						parent.geometry.width = maxWidth + 10;
						graph.refresh(parent);
						var getCells = graph.getChildCells(parent,true);
						page.centerCell(graph,getCells,parentCell.geometry.width/2)
					}
				}
				var dy = targetCell.geometry.y - cell.geometry.y;				
				if(targetCell.id == "end" || targetCell.id.indexOf("sequence") >= 0){
					dy = parentCell.geometry.height - cell.geometry.y;
				}
				oldHeights = parentCell.geometry.height;
				parentCell.geometry.height -= dy;
				graph.refresh(page.vContain);
				if(targetCell.id.indexOf("sequence") < 0){
					graph.moveCells([targetCell],0,-dy);
				}
				page.moveCellsUp(targetCell,"end");
			}			
		});
	},observerResizeWidth : function(cell){
		var graph = this.graph,
			cellWidth = cell.geometry.width,
			cellX = cell.geometry.x;
		var cellParent = cell.getParent(),
			pWidth ,
			thisCell = cell;
		graph.clearSelection();
		while(cellParent.geometry){
			pWidth = cellParent.geometry.width;		
			if(cellWidth + cellX >= pWidth){		
				cellParent.geometry.width = Number(cellWidth) + 60; 
				if(cellParent.id.indexOf("sequence") >= 0 || cellParent.id.indexOf("flowCell") >=0){
					if(cellParent.id.indexOf("flowCell") >=0){
						cellParent.geometry.width += Number(cellX) - 40;
					}
					page.moveLabel(graph,cellParent.children[0]);
				}
			}	
			graph.refresh(cellParent);		
			cellWidth = cellParent.geometry.width;
			cellX = cellParent.geometry.x;	
			thisCell = cellParent;
			cellParent = cellParent.getParent();
		}
	},instanceBorderCell : function(cell){				//检测cell的最小高度
		var graph = page.graph;
		var cells = graph.getChildCells(cell,true);
		var cellHeight = cell.geometry.height;
		var minHeight = cellHeight;			//最小高度，默认为当前cell的高
		cells && cells.map(function(x){
			minHeight = Math.max(x.geometry.y + x.geometry.height,minHeight);
		})
		return minHeight;
	},instanceBorderCellWidth : function(cell){
		var graph = page.graph;
		var cells = graph.getChildCells(cell,true);
		var cellWidth = cell.geometry.width;
		var minWidth = cellWidth;			//最小高度，默认为当前cell的宽
		cells && cells.map(function(x){
			var coordsX = Number(x.geometry.x);
			var coordsWidth = Number(x.geometry.width);
			if(coordsX + coordsWidth >= minWidth && x.id.indexOf("label") < 0){
				minWidth = 60 + coordsWidth + coordsX;
			}
		})
		return minWidth;
	},getMaxWidthFromCellChild : function(cell){
		var graph = page.graph;
		var cells = graph.getChildCells(cell,true);
		var cellWidth = cell.geometry.width;
		var maxWidth = 30;								//最大宽度，初识为30
		cells && cells.map(function(x){
			if(x.geometry.width >= maxWidth){
				maxWidth = x.geometry.width;
			}
		})
		return maxWidth;
	}
};
var callbackFnManage = function(obj){
	$(".info").alert(obj.message);
	$(".deploy").remove();
	$(".submitform").remove();	
	$(".shade").hide().removeClass("loading");
}
function callbackFn(msg){	//部署接口回调函数
	callbackFnManage && callbackFnManage(msg);
}
function cellChangeH(cell,dy){
	var graph = page.graph;
	dy = dy || 60;
	var cellHeight = cell.geometry.height;
	var cellParent = cell.getParent();
	var pH = cellParent.geometry ? cellParent.geometry.height : 0;	
	var minHeight = 0;
	if(dy < 0){
		if(cell.id.indexOf("vContain") >= 0){
			minHeight = page.accountHeight(cell,graph);			
		}
		else{
			minHeight = page.instanceBorderCell(cell);
		}
		if(cellHeight < minHeight){
			dy = minHeight - cellHeight + dy;
			cell.geometry.height = minHeight;
			graph.refresh(cell);
			$(document).alert("The minimum boundary is achieved and the boundary processing is being performed automatically.");			
		}
		else if(cellHeight <= 60){
			dy = 60 - cellHeight + dy;
			cell.geometry.height = 60;
			graph.refresh(cell);
			$(document).alert("The minimum boundary is achieved and the boundary processing is being performed automatically.");			
		}
	}
	if(cell.id.indexOf("flowCell") >= 0){				//如果变化的是并行		
		var nextCell;
		var tempCell = cell;
		while(cellParent.geometry != undefined){		
			var pMinHeight = Number(cellParent.geometry.height);
			var parentH = Number(cellParent.geometry.height);
			cellParent.geometry.height += dy;
			if(cellParent.id.indexOf("flowCell") >= 0 && dy < 0){
				pMinHeight = page.instanceBorderCell(cellParent);
				if(pMinHeight >= parentH + dy){
					$(document).alert("The minimum boundary is achieved and the boundary processing is being performed automatically.");
					dy = pMinHeight - (cellParent.geometry.height - dy);	
					cellParent.geometry.height = pMinHeight;
				}
			}			
			graph.refresh(cellParent);
			page.moveCellsUp(cellParent);
			cellParent = cellParent.getParent();
		}
		while(tempCell.id != "end" && tempCell.id != cell.getParent().id){
			if(tempCell.id.indexOf("sequence") >= 0){
				tempCell = tempCell.getParent();
			}
			nextCell = page.getNextCells(tempCell);
			tempCell = nextCell;
			if(tempCell.id.indexOf("sequence") < 0){
				graph.moveCells([tempCell],0,dy);
			}			
			graph.refresh(tempCell);
		};		

		page.moveEndCell();
		var cells = graph.getChildCells(page.vContain,true);
		page.moveCellsUp(cells[0]);
	}
	else{										//如果变化的是中间内容cell(vContain)
		var cells = graph.getChildCells(cell,true);
		if(cells.length <= 0){
			graph.moveCells([page.cellEnd],0,dy);
		}
		else{
			page.moveCellsUp(cells[0]);
		}
	}
}
function cellChangeW(cell,dx){  
	var graph = page.graph;
	if(cell.id.indexOf("flowCell") >= 0){			//并行宽度发生变化
		var parentCell = cell.getParent();	
		var tempCell = cell;
		var pWidth = parentCell.geometry.width;
		var cellCenterX = cell.geometry.getCenterX();
		var minWidth = page.instanceBorderCellWidth(cell);
		if(cell.geometry.width < minWidth && dx < 0){ 
			cell.geometry.width = minWidth;
			$(document).alert("The width is beyond the bounds and has been adjusted automatically.");
			graph.refresh(cell);

		}
		while(parentCell.geometry){
			var maxWidth = page.getMaxWidthFromCellChild(parentCell);
			var cellDor = tempCell.geometry.width + tempCell.geometry.x;
			var betweenCellWidth = maxWidth + 60;
			
			if(pWidth != betweenCellWidth && parentCell.id.indexOf("flowCell") < 0){
				parentCell.geometry.width = betweenCellWidth;					
			}
			else if(pWidth < cellDor && parentCell.id.indexOf("flowCell") >= 0){
				parentCell.geometry.width += dx;
			}
			graph.refresh(parentCell);	
			var centerX = parentCell.geometry.width/2;
			var cells = graph.getChildCells(parentCell,true);
			if(parentCell.id.indexOf("flowCell") < 0){
				page.centerCell(graph,cells,centerX);				
			}
			else{
				page.centerCell(graph,[cells[0]],centerX);				
			}
			
			tempCell = parentCell;
			parentCell = parentCell.getParent();
			pWidth = parentCell.geometry ? parentCell.geometry.width : 0;
		}
		page.centerCell(graph,[tempCell],page.getBeginCell().geometry.getCenterX());
		page.moveLabel(graph,cell.children[0]);			
	}
	else{
		var maxWidth = page.getMaxWidthFromCellChild(cell);
		if(maxWidth >= cell.geometry.width){
			cell.geometry.width = maxWidth + 60;
			$(document).alert("The minimum boundary is achieved and the boundary processing is being performed automatically.");	
			graph.refresh(cell);		
		}
		var cells = graph.getChildCells(cell,true);
		var centerX = cell.geometry.width/2;
		page.centerCell(graph,cells,centerX);		
		page.centerCell(graph,[cell],page.getBeginCell().geometry.getCenterX());

	}
}

function callbackUpload(json){			//打开本地xml文件接口回调函数
	if(json.code != ""){
		var doc = mxUtils.parseXml(json.code);
		page.graph = null;
		$("#graphContainer").html("");
		$(".mxTooltip").hide();
		page.init();
		page.graphClickEvent(page.graph);
    	$(".pNameVal").val($(doc).find("Process").attr("name"));
    	$("#defaultClass").val($(doc).find("Process").attr("categoryId"));    	
    	$(".descDeatil").val($(doc).find("Process").attr("describe"));
		var decode = new decodeXML(doc);
		decode.init();
	}	
}

function getData(nodeId,ele,cell,cellBegin,isTrue){
	if(isNaN(nodeId)){
		return;
	}
	var url = httpUrls.getDataUrl + "?serviceId="+nodeId+"&callback=?&"+new Date().getTime();
	var arr = [] , outArr = [] , defaultValArr = [];
	var str = "" , strOut = "";
	var cellObj = {};
	var num = 0;
	var outNum = 0;
	var cellEnd = page.cellEnd;
	cellEnd.defaultVal = cellEnd.defaultVal || [];
	cell = cell || {};
	cellBegin = cellBegin || {};
	cellBegin.defaultVal = cellBegin.defaultVal || [];
	cell.defaultVal = cell.defaultVal || [];
	cell.isPL = false;			//批量执行默认false
	$.getJSON(url,function(data){
		$(".serviceName").val(cell.value);
		$(".method").val(data.methodName);
		$(".urlDetail").val(data.wsdlPath);
		$(".serverDesc").val(data.serviceDesc);
		arr = data.inlist;
		var typeId = data.typeId;
		cell.isProcess = typeId == 2 ? true : false;
 		outArr = data.outlist;
		cellObj.serviceName = data.serviceName;
		cellObj.methodName = data.methodName;
		cellObj.wsdlPath = data.wsdlPath;
		cellObj.serverDesc = data.serviceDesc;
		cell.dataDesc = cellObj;
		if(data.processId && data.processId != ""){
			cell.processId = data.processId;
		}
		var saveRadomObjInput = {};	
		var saveRadomObjOutput = {};
		for(var i = 0;i < arr.length;i++){
			var obj = {};		
			var date = isRandomRepeat(arr.length,1000,saveRadomObjInput);
			try{
				if(date == undefined){
					date = isRandomRepeat(arr.length,1000,saveRadomObjInput);
				}
				date = date && date.toString().length == 3 ? date : date + "a";				
			}
			catch(e){
				console.log(date);
				alert("An unknown error occurred. Please refresh the page or delete the current service.");
			}
			obj.dataType = arr[i].type;
			obj.type = "InputParameter";
			obj.cellId = cell.id;
			obj.dataID = cell.id + "_" + date;
			obj.id = "Receive_" + nodeId + "_" + date;
			obj.name = "param" + nodeId + "_" + date;
			obj.parameterDesc = arr[i].desc/* + "-" + arr[i].group*/;
			obj.parameterName = arr[i].name;
			obj.group = page.graph.getLabel(cell);
			str += "<ol><li class='nth-1'>"+(++num)+"</li><li class='nth-2'>"+arr[i].type+
				   "</li><li class='nth-3'>"+arr[i].desc+"</li></ol>";
			defaultValArr.push(obj);
		}
		outArr.map(function(x){
			var obj2 = {};
			var l = outArr.length;
			var date = isRandomRepeat(outArr.length,1000,saveRadomObjInput);
			try{
				date = date && date.toString().length == 3 ? date : date + "a";							
			}
			catch(e){
				alert("An unknown error occurred. Please refresh the page or delete the current service.");
			}
			obj2.dataType = x.type;
			obj2.type = "OutputParameter";
			obj2.name = "param" + nodeId + "_" + date;
			obj2.id = "Reply_" + nodeId + "_" + date;
			obj2.cellId = cell.id;
			obj2.dataID = nodeId + "_" + date;
			obj2.parameterDesc = x.desc/* + "-" + x.group*/;
			obj2.parameterName = x.name;			
			obj2.group = page.graph.getLabel(cell);
			strOut += "<ol><li class='nth-1'>" + (++outNum) + 
					  "</li><li class='nth-2'>" + x.type + "</li><li class='nth-3'>" + x.desc + "</li></ol>";
			defaultValArr.push(obj2);			
		})
		$(".serverOutlist").find(".returnData").html(strOut);
		$(ele).find(".returnData").html(str);		
		if(isTrue){			
			var insertIndex = getInsertIndex(cell,cellBegin);
			defaultValArr.map(function(x){
				var objTemp = {};
				if(x.type !== "OutputParameter"){
					cellBegin.defaultVal.splice(insertIndex,0,x);
					insertIndex++;		
				}
				for(var a in x){
					objTemp[a] = x[a];
				}
				cell.defaultVal.push(objTemp);
			})
		}			
	})
}

//获取要插入到默认参数中的下标位置
function getInsertIndex(cell,beginCell){
	var count = 0;
	var parent = cell.getParent();
	if(parent.id.indexOf("flowCell") >= 0){
		return getInsertIndex(parent,beginCell);
	}
	var cellUp = page.getNextCells(cell,true);
	if(cellUp && cellUp.id != "begin" && parent.id.indexOf("flowCell") < 0){
		if(cellUp.id.indexOf("label") >= 0){
			return getInsertIndex(cellUp.getParent(),beginCell);
		}
		if(cellUp.id.indexOf("flowCell") >= 0){
			var childrenCells = page.graph.getChildCells(cellUp,true);
			var tempArray = [];
			childrenCells.map(function(x){
				if(x.id.indexOf("sequence") >= 0){
					var subChild = page.graph.getChildCells(x,true);
					subChild.map(function(y){
						if(y.id.indexOf("flowCell") >= 0){
							tempArray.push(getInsertIndex(y,beginCell));
						}
						else{
							var para = y.defaultVal && y.defaultVal.filter(function(z){
								return z.type == "InputParameter";
							})
							para = para || [{id:false}];
							var index = beginCell.defaultVal.indexOf(para[para.length - 1].id,"id") + 1;
							tempArray.push(index);
						}
					})
				}
				else{
					var childPara = x.defaultVal && x.defaultVal.filter(function(z){
						return z.type == "InputParameter";
					})
					childPara = childPara || [{id:false}];
					var index2 = beginCell.defaultVal.indexOf(childPara[childPara.length - 1].id,"id") + 1;
					tempArray.push(index2);
				}
			})	
			count = Math.max.apply(null,tempArray);
			if(count == 0){
				return getInsertIndex(childrenCells[childrenCells.length - 1],beginCell);
			}
			// return getInsertIndex(childrenCells[childrenCells.length - 1],beginCell);
			return count;
		}
		var inputParame = cellUp.defaultVal && cellUp.defaultVal.filter(function(z){
			return z.type == "InputParameter";
		});
		inputParame = inputParame || [{id:false}];
		var inputParameVal = inputParame[inputParame.length - 1];
		count = beginCell.defaultVal.indexOf(inputParameVal.id,"id") + 1;
	}
	else if(parent.id.indexOf("flowCell") >= 0 || !cellUp){
		return getInsertIndex(parent,beginCell);
	}
	return count;
}

//编写一个方法，判断所生成的随机数是否存在于某个对象中,传入的参数第三个必须为obj
function isRandomRepeat(){
	var arg = arguments;
	if(arg.length >= 2){		
		var max = Math.max(arg[0],arg[1]);
		var obj = arg[2];
	}
	else{
		return;
	}
	var random = Math.floor(Math.random()*max);
	if(random in obj){
		random = Math.floor(Math.random()*max);
		return isRandomRepeat();
	}
	else{
		obj[random] = true;
		return random + 1;
	}
}
//状态模式-----设置cell的状态
var cellStates = function(){
	var _currentState = {},
		states = {
			graph : page.graph,
			canMoveOffResize : function(){
				this.graph.setCellsResizable(false);	
				this.graph.setCellsMovable(true);
			},
			offMoveOffResize : function(){
				this.graph.setCellsResizable(false);
				this.graph.setCellsMovable(false);
			},
			offMoveCanResize : function(){
				this.graph.setCellsResizable(true);	
				this.graph.setCellsMovable(false);
			}
		};
	var Action = {
		changeState : function(){
			var arg = arguments;
			_currentState = {};
			if(arg.length){
				for(var i = 0;i < arg.length;i++){
					_currentState[arg[i]] = true;
				}
			}
			return this;
		},
		goes : function(){
			for(var i in _currentState){
				states[i] && states[i]();
			}
			return this;
		}
	};
	return {
		change : Action.changeState,
		goes : Action.goes
	}
};

//setXML.js 	生成/解压(encode/decode)XML类
var encodeXml = function(cells){
	var pNameVal = $(".pNameVal").val();
	var pDescribe = $(".descDeatil").val() || "";
	var categoryId = $("#defaultClass").val();
	var encoder = mxUtils.createXmlDocument();
	var Process = encoder.createElement("Process");
	var graph = page.graph;
	var dataLinkArr = [];
	Process.setAttribute("categoryId",categoryId);
	Process.setAttribute("name",pNameVal);
	Process.setAttribute("describe",pDescribe);
	Process.setAttribute("isNewVersion","true");	
	var addStarNode = function(arr,elements,Figure){
		var isPL = false;
		for(var i = 0;i < arr.length;i++){
			var str = encoder.createElement(elements);
			var x = arr[i];
			if(x.type !== "OutputParameter"){
				for(var j in x){
					var a = x[j];
					if(j == "type"){
						a = "Receive";
					}
					str.setAttribute(j,a);
				}
				str.setAttribute("outType",x.type);
				cells.map(function(y){
					if(x.dataID.split("_")[0] == y.id){
						isPL = y.isPL;
					}
				})
				str.setAttribute("isPL",isPL);
				Figure.appendChild(str);				
			}		
		}
	}		
	var addContainNode = function(arr,ele,parEle,isAttr){			
		if(isAttr){					
			var elements = encoder.createElement(ele);
			for(var i in arr){
				var str = createElements(i,arr[i]);
				elements.appendChild(str);
			}
			parEle.appendChild(elements);
		}
		else{	
			arr.map(function(x){						
				var elements = encoder.createElement(ele);		
				var obj = {};
				obj.parameterName = x.parameterName;
				obj.parameterType = x.dataType;
				obj.parameterDesc = x.parameterDesc;
				obj.dataID = x.id;
				elements.setAttribute("type",x.type);
				elements.setAttribute("id",x.dataID);
				if(x.addClassName){
					elements.setAttribute("addClassName",x.addClassName);
				}
				var attribute = encoder.createElement("Attributes");		
				for(var i in obj){
					str = createElements(i,obj[i]);
					attribute.appendChild(str);
				}			
				elements.appendChild(attribute);
				parEle.appendChild(elements);
			})			
		}
	}
	var createElements = function(index,data){
		var str = encoder.createElement("Attribute");
		str.setAttribute(index,data);
		return str;
	}
	var createChildDataLink = function(thisId,flowId,dataLinkArrs){
		if(thisId.indexOf("label") < 0){
			var childFigure = encoder.createElement("Figure");
			childFigure.setAttribute("type","Link");
			childFigure.setAttribute("isData","false");
			childFigure.setAttribute("startFigureId",flowId);
			childFigure.setAttribute("endFigureId",thisId);
			childFigure.setAttribute("isChild","true");

			var childFigure2 = encoder.createElement("Figure");
			childFigure2.setAttribute("type","Link");
			childFigure2.setAttribute("isData","false");
			childFigure2.setAttribute("startFigureId",thisId);
			childFigure2.setAttribute("endFigureId",flowId);
			childFigure2.setAttribute("isChild","true");

			dataLinkArrs.appendChild(childFigure);
			dataLinkArrs.appendChild(childFigure2);
		}
	}
	var getSeFlowLabel = function(cell){			//获取并行和串行的label值
		var labelCell = cell.children[0];
		return labelCell.value;
	}
	return {
		init : function(){
			this.parseModel();
			return Process;
		},parseModel : function(){			//获取model中的cell，并解析成XML		
			var vCY = page.vContain.geometry.y;
			var that = this;
			cells.map(function(x){
				var cellID = x.id;
				var Figure = encoder.createElement("Figure");
				var FigureId , FigureType , parentsNode , FigureF = false , isFlow = false;
				if(cellID == "begin"){				//开始节点
					FigureType = "Start";
					FigureId = "startFigure";
					FigureF = true;
					if(x.defaultVal != undefined){
						addStarNode(x.defaultVal,"Figure",Figure);
					}						
				}
				else if(!isNaN(parseInt(cellID))){					//中间节点		
					FigureType = "Service";
					FigureId = cellID;
					Figure.setAttribute("serviceId",parseInt(cellID));
					Figure.setAttribute("label",graph.getLabel(x));
					Figure.setAttribute("isProcess",x.isProcess);
					Figure.setAttribute("isPL",x.isPL);
					if(x.processId){
						Figure.setAttribute("processId",x.processId);
					}
					FigureF = true;
					if(x.defaultVal && x.dataDesc){			
						addContainNode(x.dataDesc,"Attributes",Figure,true);
						addContainNode(x.defaultVal,"Figure",Figure,false);
						that.figureDataLink(x.defaultVal,"OutputParameter");		//数据连线Figure
					}
					if((x.getParent().id).indexOf("flowCell") >= 0 || (x.getParent().id).indexOf("sequence") >= 0){
						var parentID = x.getParent().id;
						parentsNode =  $(Process).find("#"+parentID);
						isFlow = true;
					}
				}
				else if(cellID.indexOf("flowCell") >= 0){
					FigureF = true;
					FigureType = "Flow";
					var cellTitle = x.children[0].value;
					Figure.setAttribute("title",cellTitle);
					if(x.getParent().id.indexOf("vContain") < 0){
						var parentID = x.getParent().id;
						parentsNode =  $(Process).find("#"+parentID);
						isFlow = true;
					}
					FigureId = cellID;
				}
				else if(cellID.indexOf("sequence") >= 0){
					FigureF = true;
					FigureType = "Sequence";
					var titleVal = getSeFlowLabel(x);
					Figure.setAttribute("title",titleVal);
					FigureId = cellID;
					var parentID = x.getParent().id;
					parentsNode =  $(Process).find("#"+parentID);
					isFlow = true;
				}
				else if(cellID === "end" && x.pId === "end"){	//结束
					FigureType = "End";
					FigureId = "endFigure";
					if(x.defaultVal != undefined){
						var endVal = x.defaultVal;
						that.figureDataLink(x.defaultVal,"InputParameter");		//数据连线Figure						
						endVal.map(function(endValue){
							if(!endValue.addClassName){
								var endFigure = encoder.createElement("Figure");								
								for(var i in endValue){	
									var paraDate = endValue[i];
									if(endValue[i] == "OutputParameter"){
										paraDate = "Reply";
									}
									endFigure.setAttribute(i,paraDate);
									endFigure.setAttribute("outType",endValue.type);
								}
								cells.map(function(y){
									if(y.id == endValue.cellId){
										endFigure.setAttribute("isPL",y.isPL);
									}
								})
								Figure.appendChild(endFigure);
							}							
						})
					}
					FigureF = true;
				}
				else if(x.isEdge() && x.source != null && x.target != null){			//线
					FigureType = "Link";
					FigureId = cellID;
					var sourceID = null , targetID = null;
					sourceID = x.source.id;
					targetID = x.target.id;
					if(x.source.id == "begin"){
						sourceID = "startFigure";
					}
					if(x.target.id == "end"){
						targetID = "endFigure";
					}
					if(sourceID.indexOf("label") >= 0){
						sourceID = x.getParent().id;
					}
					if((x.target.id).indexOf("flowCell") >= 0 && x.target.children){			//处理cell并行和串行		
						var FlowCell = x.target.id;
						var childArr = x.target.children;
						childArr.map(function(y){
							createChildDataLink(y.id,FlowCell,Process);							
						})
					}
					Figure.setAttribute("type","Link");
					Figure.setAttribute("isData","false");
					Figure.setAttribute("startFigureId",sourceID);
					Figure.setAttribute("endFigureId",targetID);
					Figure.setAttribute("isChild","false");
					FigureF = true;
				}
				if(FigureF){
					Figure.setAttribute("id",FigureId);			
					if(!x.isEdge()){
						var pos = that.getPosition(x)
						Figure.setAttribute("name",FigureId);
						Figure.setAttribute("type",FigureType);
						Figure.setAttribute("PositionX",pos.x);
						Figure.setAttribute("PositionY",pos.y);
						Figure.setAttribute("width",x.geometry.width);
						Figure.setAttribute("height",x.geometry.height);	
						if(isFlow){
							parentsNode.append(Figure);
						}
						else{
							Process.appendChild(Figure);
						}
					}
					else{
						Process.appendChild(Figure);
					}
				}
			})	
			dataLinkArr.map(function(x){
				Process.appendChild(x);
			})
		},getPosition : function(cell){
			var position = {};
			var x = cell.geometry.x,
				y = cell.geometry.y;
				parentCell = cell.getParent();
			/*while(parentCell.geometry && parentCell.id !== "vContain"){
				var accountX = parentCell.geometry.x,
					accountY = parentCell.geometry.y;
				if(parentCell.id == "vContain"){
					accountX = 0;
					accountY = 0;
				}
				x += accountX;
				y += accountY;
				parentCell = parentCell.getParent();
			}*/

			position.x = x;
			position.y = y;

			return position;
		},figureDataLink : function(dataLink,outType){	
			dataLink.map(function(x){
				if(x.type !== outType){				
					var id = x.id;
					var dataID = x.dataID;
					if(x.type == "OutputParameter"){
						id = x.dataID;
						dataID = x.id;
					}
					var Figure = encoder.createElement("Figure");
					Figure.setAttribute("type","Link");
					Figure.setAttribute("isData","true");
					Figure.setAttribute("startFigureId",id);
					Figure.setAttribute("endFigureId",dataID);
					dataLinkArr.push(Figure);
				}
			})
		}
	}
}
var decodeXML = function(filename){
	var root = filename;
	var beginCell = page.getBeginCell();
	var endCell = page.cellEnd;
	var cells = [];
	var decodeBeginDefaultVal = function(term,findAttr){			//解压入开始节点参数
		var arr = [];
		$(root).find("#"+term).find(findAttr).each(function(x){
			var obj = {};
			obj.id = $(this).attr("id");
			obj.name = $(this).attr("name");
			obj.dataType = $(this).attr("dataType");
			obj.parameterDesc = $(this).attr("parameterDesc");
			obj.dataID = $(this).attr("dataID");
			obj.group = $(this).attr("group");
			obj.type = $(this).attr("outType");
			arr.push(obj);
		})
		return arr;
	}
	var decodeCellDefaultVal = function(cell,term,childArr){			//解压服务参数
		cell.defaultVal = cell.defaultVal || [];
		var childAttribute = term.find(childArr);
		cell.isPL = term.attr("isPL");
		cell.isProcess = term.attr("isProcess");
		if(term.attr("processId")){
			cell.processId = term.attr("processId");
		}		
		childAttribute.map(function(x){
			var subChild = childAttribute.eq(x).find("Attribute");
			var obj = {};
			obj.group = cell.value;
			obj.cellId = cell.id;
			obj.dataID = childAttribute.eq(x).attr("id");
			obj.type = childAttribute.eq(x).attr("type");
			if(childAttribute.eq(x).attr("addClassName")){
				obj.addClassName = childAttribute.eq(x).attr("addClassName");
			}				
			obj.parameterName = subChild.eq(0).attr("parameterName");
			obj.dataType = subChild.eq(1).attr("parameterType");
			obj.parameterDesc = subChild.eq(2).attr("parameterDesc");
			obj.id = subChild.eq(3).attr("dataID");
			obj.name = "parame" + cell.pId + "_" + obj.dataID.split("_")[1];
			cell.defaultVal.push(obj);
		})
	}	
	var decodeDataDesc = function(cell,term,childArr){			//添加cell的数据描述
		cell.dataDesc = cell.dataDesc || {};
		var childAttribute = term.find(childArr);		
		var subChild = childAttribute.eq(0).find("Attribute");
		cell.dataDesc.serviceName = subChild.eq(0).attr("serviceName");
		cell.dataDesc.methodName = subChild.eq(1).attr("methodName");
		cell.dataDesc.wsdlPath = subChild.eq(2).attr("wsdlPath");
		cell.dataDesc.serverDesc = subChild.eq(3).attr("serverDesc");		
	}
	var decodeCoordInDoc = function(cellParentId,cells){		//解析cell在页面中的位置x，y
		var coordX = 0;
		var coordY = 0;
		var coords = {};
		var parent = null;
		cells && cells.map(function(x){		
			if(x.id == cellParentId){
				parent = x;		
				return;		
			}
		});
		if(!parent){
			parent = page.vContain;
		}
		if(parent){
			coords.parentCell = parent;			
			while(parent.geometry){
				coordX += Number(parent.geometry.x);
				coordY += Number(parent.geometry.y);
				parent = parent.getParent();
			}
		}
		coords.x = Number(coordX);
		coords.y = Number(coordY);
		return coords;
	}
	var changeCellParent = function(cell){
		var cellParent = cell.getParent();
		var parentId = cellParent.id;
		if(parentId == "vContain"){
			return;
		}
		var $cellParentDetail = $(root).find("#"+parentId);
		var width = $cellParentDetail.attr("width");
		var height = $cellParentDetail.attr("height");
		var positionY = $cellParentDetail.attr("PositionY");
		cellParent.geometry.width = Number(width);
		cellParent.geometry.height = Number(height);
		cellParent.geometry.y = Number(positionY)
		page.graph.refresh(cellParent);
		if(cellParent.id.indexOf("flowCell") >= 0 || cellParent.id.indexOf("sequence") >= 0){
			page.moveLabel(page.graph,cellParent.children[0]);
		}	
	}
	var decodeInCell = function(endId){					//将解析出的cell加入到graph中
		if(endId == "endFigure"){
			var defaultVal = decodeBeginDefaultVal(endId,"Figure");
			endCell.defaultVal = defaultVal;
			return;
		}
		var pID = $(root).find("#"+endId).parent().attr("id");
		if(!pID){
			page.defaultParent = page.vContain;
			page.istrue = true;
		}		
		$(root).find("#" + endId).each(function(){			
			var obj = {};			
			if($(this).attr("label") && $(this).attr("label").split("_")[1]){
				page.num2 = page.num2 > $(this).attr("label").split("_")[1] ? page.num2 : $(this).attr("label").split("_")[1];				
			}
			var coords = decodeCoordInDoc(pID,cells);
			page.defaultParent = coords.parentCell;
			obj.name = $(this).attr("label");
			obj.id = $(this).attr("id");
			obj.width = Number($(this).attr("width")) || 0;
			obj.height = Number($(this).attr("height")) || 0;
			obj.x = Number($(this).attr("PositionX")) + coords.x;
			obj.y = Number($(this).attr("PositionY")) + coords.y;
			var cell = page.addChild(obj);
			cells.push(cell);
			if(isNaN(cell.pId)){
				cell.geometry.width = obj.width;
				cell.geometry.height = obj.height;
				page.moveLabel(page.graph,cell.children[0]);
				page.graph.refresh(cell);
				changeCellParent(cell);
				var labelName = $(this).attr("title");
				var changeCell = cell.children[0];
				page.changeCellValue(changeCell,labelName);
				if(cell.getParent().id.indexOf("flowCell") < 0){
					page.centerCell(page.graph,[cell],cell.getParent().geometry.width/2);
				}
				return;
			}
			decodeCellDefaultVal(cell,$(this),"Figure");
			decodeDataDesc(cell,$(this),"Attributes");
		})	
	}
	return {
		init : function(){
			this.decodeData("startFigure","endFigure");
		},decodeData : function(term,outTerm){				//解析数据并将数据加入到流程中
			var node;	
			var that = this;
			node = $(root).find("Figure[isData='false'][startFigureId='"+term+"'][isChild='false']");
			var endId = $(node).attr("endFigureId");
			while(endId != outTerm){				
				if(term.indexOf("flowCell") >= 0){
					page.istrue = false;
					node = $(root).find("Figure[isData='false'][startFigureId='"+term+"'][isChild='true']");
					node.map(function(x){
						var flowEndId = node.eq(x).attr("endFigureId");
						decodeInCell(flowEndId);
						if(flowEndId.indexOf("sequence") >= 0){
							that.decodeData(flowEndId,flowEndId);
						}
					})
				}			
				node = $(root).find("Figure[isData='false'][startFigureId='"+term+"'][isChild='false']");
				endId = $(node).attr("endFigureId");
				if(endId && endId.indexOf("sequence") >= 0){			//当查找的endId为串行时，递归结束					
					if(endId == page.defaultParent.id){
						var thisWidth = $(root).find("#"+endId).attr("width");
						var graph = page.graph;
						page.defaultParent.geometry.width = Number(thisWidth);
						graph.refresh(page.defaultParent);
					}					
					return;
				}
				page.istrue = true;
				if(term == "startFigure"){
					var defaultVal = decodeBeginDefaultVal(term,"Figure");
					beginCell.defaultVal = defaultVal;
				}
				decodeInCell(endId);
				term = endId;
			}
			var firstCell = page.getNextCells(page.getBeginCell());
			var accountHeight = page.accountHeight(firstCell,page.graph,page.vContain) + firstCell.geometry.height;
			var maxWidth = page.getMaxWidthFromCellChild(page.vContain);

			if(accountHeight > page.vContain.geometry.height){
				page.vContain.geometry.height = accountHeight;
			}	
			page.graph.refresh(page.vContain);

			if(maxWidth + 60 > page.vContain.geometry.width){
				page.vContain.geometry.width = maxWidth + 60;
				page.graph.refresh(page.vContain);
				var centerX = page.cellEnd.geometry.getCenterX(); 
				page.centerCell(page.graph,[page.vContain],centerX);
				var vCenterX = (maxWidth + 60)/2;
				var cells = page.graph.getChildCells(page.vContain,true);
				page.centerCell(page.graph,cells,vCenterX);
			}	
			
			page.moveCellsUp(firstCell);
		}
	}
}