function reportTableA(reportTableId,url){
    if (url==null||url==""){
        tableA(reportTableId,"");
    }else{
	    $.ajax({
		        url: url,
		        type: 'post',
		        dataType: 'json',
                success: function (data1) {
                    if(data1.length==13){
                        tableA(reportTableId,data1);
                        return;
                    }
                    if(data1.length<13){
                        $.ajax({
                            url: base_url + "/archiveCatalog/findAllArchiveFiledManage",
                            type: 'post',
                            dataType: 'json',
                            success: function (data) {
                                var keyword;
                                var myArray=new Array();
                                for(var i=0;i<data.length;i++){
                                    if(myArray.length<13){
                                        if(data[i].f_cname=="卫星标识"){
                                            myArray[0]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="传感器标识"){
                                            myArray[1]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="产品分辨率"){
                                            myArray[2]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="生产时间"){
                                            myArray[3]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="接收时间"){
                                            myArray[4]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="图像右下角经度"){
                                            myArray[5]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="图像右下角纬度"){
                                            myArray[6]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="图像左下角经度"){
                                            myArray[7]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="图像左下角纬度"){
                                            myArray[8]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="图像右上角经度"){
                                            myArray[9]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="图像右上角纬度"){
                                            myArray[10]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="图像左上角经度"){
                                            myArray[11]={f_fieldmanageid:data[i].id};
                                        }
                                        if(data[i].f_cname=="图像左上角纬度"){
                                            myArray[12]={f_fieldmanageid:data[i].id};
                                        }
                                        
                                    }
                                    
                                }
                                if(data1.length==0){
                                    tableA(reportTableId,myArray);
                                }else{
                                    for(var i=0;i<myArray.length;i++){
                                        for(var j=0;j<data1.length;j++){
                                            if(myArray[i].f_fieldmanageid==data1[j].f_fieldmanageid){
                                                myArray[i]={f_archivefield:data1[j].f_archivefield,f_fieldmanageid:data1[j].f_fieldmanageid}
                                            }
                                        }
                                    }
                                    tableA(reportTableId,myArray);
                                }
                                //myArray=[{f_fieldmanageid:"F33B26A33AB04EFF9F291BA4DC4B6536"},{f_fieldmanageid:"A776D54B42B344B8ABF86B47250B4C39"}];
                            }
                        });
                    }
                    
                    //tableA(reportTableId,data);

		        }
	        });
    }

}
function reportTableB(reportTableId,url){
    if (url==null||url==""){
        tableA(reportTableId,"");
    }else{
	    $.ajax({
		        url: url,
		        type: 'post',
		        dataType: 'json',
		        success: function (data) {
		        	var keyword;
		        	var myArray=new Array();
		        	for(var i=0;i<data.length;i++){
		        		if(myArray.length<13){
		        			if(data[i].f_cname=="卫星标识"){
			        			myArray[0]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="传感器标识"){
			        			myArray[1]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="产品分辨率"){
			        			myArray[2]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="生产时间"){
			        			myArray[3]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="接收时间"){
			        			myArray[4]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="图像右下角经度"){
			        			myArray[5]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="图像右下角纬度"){
			        			myArray[6]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="图像左下角经度"){
			        			myArray[7]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="图像左下角纬度"){
			        			myArray[8]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="图像右上角经度"){
			        			myArray[9]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="图像右上角纬度"){
			        			myArray[10]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="图像左上角经度"){
			        			myArray[11]={f_fieldmanageid:data[i].id};
			        		}
		        			if(data[i].f_cname=="图像左上角纬度"){
			        			myArray[12]={f_fieldmanageid:data[i].id};
			        		}
		        			
		        		}
		        		
		        	}
		        	//myArray=[{f_fieldmanageid:"F33B26A33AB04EFF9F291BA4DC4B6536"},{f_fieldmanageid:"A776D54B42B344B8ABF86B47250B4C39"}];
		            tableA(reportTableId,myArray);
		        }
	        });
    }

}
function tableA(reportTableId,data) {
	
    $("#" + reportTableId).bootstrapTable('destroy');
    $("#" + reportTableId).bootstrapTable({
        method: 'get',
        data: data,
        editable: true,//开启编辑模式
        clickToSelect: true,
        cache: false,
        columns: [
            {
                field: "id", title: "序号", width: "5%", edit: false, formatter: function (value, row, index) {
                row.id = index + 1;
                return index + 1;
            }
            },
            {field: "f_archivefield", title: "解析标识", width: "45%", edit: {required: true, type: 'text'}},
            {
                field: "f_fieldmanageid",
                title: "目标名称",
                width: "45%",
                align: 'center',
                edit: {
                    type: 'select',
                    url: base_url+'/archiveCatalog/findAllArchiveFiledManage',
                    valueField:'id',
                    textField:'f_cname',
                    editable : true,
                    disabled:"disabled",
                    onSelect:function(val,rec){
                    	//alert(val+"---------------"+rec);
                        console.log(val,rec);
                    },
                    click:function(e){
                    	//alert(11);
                    }
                },
                visible:true
            },
            {
                field: "operate", title: "操作", width: "5%", formatter: function (value, row, index) {
                return operateFormatter(index, reportTableId);
            }, edit: false,visible:false
            }
        ],
        onEditableHidden: function (field, row, $el, reason) { // 当编辑状态被隐藏时触发
            if (reason === 'save') {
                var $td = $el.closest('tr').children();
                $td.eq(-1).html((row.price * row.number).toFixed(2));
                $el.closest('tr').next().find('.editable').editable('show'); //编辑状态向下一行移动
            } else if (reason === 'nochange') {
                $el.closest('tr').next().find('.editable').editable('show');
            }
        },
        onEditableSave: function (field, row, oldValue, $el, reportTableId) {
            $table = $("#" + reportTableId).bootstrapTable({});
            $table.bootstrapTable('updateRow', {index: row.rowId, row: row});
        }
    });

}