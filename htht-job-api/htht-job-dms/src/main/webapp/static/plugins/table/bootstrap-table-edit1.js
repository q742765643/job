/**
 * 可编辑表格插件
 * 如果编辑表格中有用到，下拉框和日期，必须先引入
 * bootstrap-select 和 bootstrap-datetimepicker两个控件
 * @author lizx <851697971@qq.com>
 * @version 1.0
 * @date 2015-10-13
 */

var tableTrTd;
var inputId;
var inputId1;
var arrPie;

(function ($) {
    'use strict';
    $.extend($.fn.bootstrapTable.defaults, {
        editable: false
    });

    var BootstrapTable = $.fn.bootstrapTable.Constructor,
        _init = BootstrapTable.prototype.init,
        _initBody = BootstrapTable.prototype.initBody,
        _onSort = BootstrapTable.prototype.onSort,
        _append = BootstrapTable.prototype.append,
        _initHeader = BootstrapTable.prototype.initHeader;
    //添加编辑表格默认属性,如何edit设置为false时，表示该列不可编辑
    $.extend(true, BootstrapTable.COLUMN_DEFAULTS, {
        edit: {
            type: 'text'//目前只支持 文本:text 下拉:select 日期:date
        }
    });
    BootstrapTable.prototype.init = function () {
        _init.apply(this, Array.prototype.slice.apply(arguments));
        var that = this;
        that.prevEditRow = null;//上一次编辑的行
        that.columns = {};//列配置信息
        that.insertRowVal = {};//新插入行的默认值
        that.enableAppend = true;//允许添加新行
        if (that.options.editable) {
            var columnObj = this['getColumns']();
            $.each(columnObj, function (i, obj) {
                $.each(obj, function (z, col) {
                    if (!isNaN(col.fieldIndex) && col.fieldIndex >= 0) {
                        if (col.checkbox) col.edit = false;
                        that.columns['column' + col.fieldIndex] = col;
                        that.insertRowVal[col.field] = '';
                    }
                });
            });
            //this.initEdit();
        }
    };
    /*BootstrapTable.prototype.initHeader = function(){
     _initHeader.apply(this, Array.prototype.slice.apply(arguments));
     this.$container.find('.fixed-table-header').addClass('success');
     };*/
    BootstrapTable.prototype.initBody = function () {
        var that = this;

        _initBody.apply(this, Array.prototype.slice.apply(arguments));
        if (!that.options.editable) return;
        this.initEdit();

        //如果列是下拉框，则转换值为对应的文本
        $.each(that.columns, function (indx, col) {
            if (col.edit.type == 'zdy') {
                col.edit = $.extend({}, $.fn.bootstrapSelect.defaults, col.edit);
                that.$body.find('>tr').each(function () {
                    if (that.getData().length < 1)return true;
                    var rowData = that.data[$(this).data('index')];//当前点击td所在行的数据
                    var $td = $(this).find('td').eq(col.fieldIndex);
                    var type=rowData.parameterType;
                    if (type == 'select') {
                        var data = "";
                        var url = "";
                        if (null != rowData.url) {
                            if (0 == rowData.url.indexOf("[")) {
                                data = eval('(' + rowData.url + ')');

                            } else {
                                url = rowData.url;

                            }

                        } else {
                            data = [{id: '', text: '请选择'}];
                        }
                        if (data) {
                            $.each(data, function (i, data) {
                                if (data[col.edit.valueField] == rowData[col.field]) {
                                    $td.html(data[col.edit.textField]);
                                }
                            });
                        } else if (url) {
                            $.ajax({
                                url: url,
                                type: 'post',
                                data: col.edit.paramsType == 'json' ? JSON.stringify(col.edit.params) : col.edit.params,
                                dataType: 'json',
                                success: function (jsonLst) {
                                    $.each(jsonLst, function (i, data) {
                                        if (data[col.edit.valueField] == rowData[col.field]) {
                                            $td.html(data[col.edit.textField]);
                                        }
                                    });
                                },
                                error: function (xhr, textStatus, errorThrown) {
                                    col.edit.onLoadError.call(this);
                                    col.edit.data = [];
                                    col.edit.url = null;
                                    throw col.field + ' 列下拉框数据加载失败';
                                }
                            });
                        }
                    }

                });

            } else {

                if (col.edit && col.edit.type == 'select') {
                    col.edit = $.extend({}, $.fn.bootstrapSelect.defaults, col.edit);
                    if (col.edit.data.length > 0) {
                        that.$body.find('>tr').each(function () {
                            if (that.getData().length < 1)return true;
                            var rowData = that.data[$(this).data('index')];//当前点击td所在行的数据
                            var $td = $(this).find('td').eq(col.fieldIndex);
                            $.each(col.edit.data, function (i, data) {
                                if (data[col.edit.valueField] == rowData[col.field]) {
                                    $td.html(data[col.edit.textField]);
                                }
                            });
                        });
                    }
                    else if (col.edit.url) {
                        $.ajax({
                            url: col.edit.url,
                            type: 'post',
                            data: col.edit.paramsType == 'json' ? JSON.stringify(col.edit.params) : col.edit.params,
                            dataType: 'json',
                            success: function (jsonLst) {
                                col.edit.onLoadSuccess.call(this, jsonLst);
                                that.$body.find('>tr').each(function () {
                                    if (that.getData().length < 1)return true;
                                    var rowData = that.data[$(this).data('index')];//当前点击td所在行的数据
                                    var $td = $(this).find('td').eq(col.fieldIndex);
                                    $.each(jsonLst, function (i, data) {
                                        if (data[col.edit.valueField] == rowData[col.field]) {
                                            $td.html(data[col.edit.textField]);
                                        }
                                    });
                                });
                                col.edit.data = jsonLst;
                                col.edit.url = null;
                            },
                            error: function (xhr, textStatus, errorThrown) {
                                col.edit.onLoadError.call(this);
                                col.edit.data = [];
                                col.edit.url = null;
                                throw col.field + ' 列下拉框数据加载失败';
                            }
                        });
                    }
                }

            }

        });
    };
    //根据行号删除指定行
    BootstrapTable.prototype.removeRow = function (rowNum) {
        var that = this;
        var len = that.options.data.length;
        if (isNaN(rowNum)) {
            return;
        }
        if (that.$body.find('.editable-select').data('index') != rowNum) {
            recover(that);
        }
        //删除数据
        that.options.data.splice(rowNum, 1);
        if (len === that.options.data.length) {
            return;
        }
        var oldClass = {};//保存被标记修改的样式
        that.$body.find('>tr').each(function (indx) {
            if ($(this).hasClass('editable-modify')) {
                if (indx > rowNum) {
                    oldClass[indx - 1] = 'editable-modify';
                }
                else {
                    oldClass[indx] = 'editable-modify';
                }
            }
        });
        //this.prevEditRow = null;
        //this.$body.find('>tr').removeClass('editable-select');
        that.initBody();
        //将标记改变过行的样式从新设置回去
        for (var key in oldClass) {
            that.$body.find('>tr').eq(key).addClass(oldClass[key]);
        }
        //this.initEdit();
        //没有数据时给提示加上样式
        if (that.getData().length < 1) {
            that.$body.find('>tr').addClass('no-records-found');
        }
    };
    BootstrapTable.prototype.append = function () {
        var that = this;
        //if(!that.enableAppend)return;
        var oldClass = {};//保存被标记修改的样式
        that.$body.find('>tr').each(function (indx) {
            if ($(this).hasClass('editable-modify') || $(this).hasClass('editable-insert')) {
                oldClass[indx] = 'editable-modify';
            }
        });
        arguments[0] = $.extend({}, that.insertRowVal, arguments[0]);
        _append.apply(this, Array.prototype.slice.apply(arguments));
        if (that.options.editable) {
            //that.initEdit();
            setTimeout(function () {
                //将标记改变过行的样式从新设置回去
                for (var key in oldClass) {
                    that.$body.find('>tr').eq(key).addClass(oldClass[key]);
                }
                that.$body.find('>tr:last').addClass('editable-modify');
                that.$body.find('>tr:last').addClass('editable-insert');//双重保险，防止在快速点击添加时，为给新增行设置editable-modify属性
                that.$body.find('>tr:last').click();
            }, 60);
        }

    };

    BootstrapTable.prototype.onSort = function () {
        _onSort.apply(this, Array.prototype.slice.apply(arguments));
        var that = this;
        if (that.options.editable) {
            this.initEdit();
        }
    };
    BootstrapTable.prototype.getData = function () {
        return (this.searchText || this.searchCallback) ? this.data : this.options.data;
    };
    //获取全部的数据
    BootstrapTable.prototype.getAllData = function () {
        return this.options.data;
    };

    BootstrapTable.prototype.getColumns = function () {
        return this.options.columns;
    };
    /**
     * 获取有被修改过行的值
     */
    BootstrapTable.prototype.getModiDatas = function () {
        var that = this;
        var datas = [];
        that.$body.find('.editable-modify').each(function () {
            if (that.data[$(this).data('index')]) {
                datas.push(that.data[$(this).data('index')]);
            }
        });
        return datas;
    };

    /**
     * 获取指定列的和，参数为列下标
     */
    BootstrapTable.prototype.getColTotal = function (num) {
        var retVal = 0;
        this.$body.find('>tr').each(function () {
            var colNum = 0;
            if ($(this).hasClass('editable-select')) {
                colNum = $(this).find('td').eq(num).find('input').val();
            }
            else {
                colNum = $(this).find('td').eq(num).html();
            }

            if (!isNaN(colNum)) {//是数字才做想加
                retVal += Number(colNum);
            }
        });
        return retVal;
    };

    /**
     * 创建可编辑表格
     */
    BootstrapTable.prototype.initEdit = function () {
        var that = this,
            data = this.getData();
        //this.$body.find('> tr').unbind('click').on('click'
        //this.$body.delegate('>tr','click'
        this.$body.find('> tr').unbind('click').on('click', function () {
            var $tr = $(this);
            if ($tr.hasClass('editable-select') || data.length < 1 || $tr.hasClass('no-records-found')) {
                return;
            }
            $tr.removeClass('no-records-found');
            recover(that);
            that.prevEditRow = $tr;
            $tr.addClass('editable-select');//给当前编辑行添加样式，目前样式为空只做标识使用
            that.$body.find('> tr').not(this).removeClass('editable-select');
            $tr.find('td').closest('td').siblings().html(function (i, html) {
                initTrClick(that, this);
            });
        });
        //鼠标点击事件
        $(document).bind('mousedown', function (event) {
            var $target = $(event.target);
            if (!($target.parents().andSelf().is(that.$body)) && !($target.parents().andSelf().is($('.datetimepicker')))
                && !($target.parents().andSelf().is($('.layui-layer')))
                && !($target.parents().andSelf().is($('.layui-layer-shade')))
                && !($target.parents().andSelf().is($('.layui-layer-move')))
                && !($target.parents().andSelf().is($('.zdymodal')))) {
                setTimeout(function () {
                    recover(that);
                    //that.prevEditRow = null;
                    //that.$body.find('> tr').removeClass('editable-select');
                }, 10);
            }
            ;
        });

    };

    $.fn.bootstrapTable.methods.push('getColumns',
        'getModiDatas', 'removeRow', 'getColTotal', 'getAllData');

    /**
     * 给tr添加点击事件
     */
    function initTrClick(that, _this) {
        that.enableAppend = true;
        var $td = $(_this);
        var $tr = $td.parent();
        var rowData = that.data[$tr.data('index')];//当前点击td所在行的数据
        var tdIndex = $tr.children().index($td);//当前点击的td下标
        var tdOpt = that.columns['column' + tdIndex];
        if (rowData.IS_TYPING != "0" && rowData.IS_TYPING != "2") {//判断接口取数方式
            if (!tdOpt.edit || typeof tdOpt.edit != 'object') {
                return;
            }
            $td.data('field', tdOpt.field);
            if (!$td.data('oldVal')) {
                $td.data('oldVal', $.trim(rowData[tdOpt.field]));
            }
            var height = $td.innerHeight() - 3;
            var width = $td.innerWidth() - 2;
            $td.data('style', $td.attr('style'));//保存原来的样式
            $td.attr('style', 'margin:0px;padding:1px!important;');
            var placeholder = '';
            if (tdOpt.edit.required == true) {
                placeholder = '必填项';
            }
            var value = rowData[tdOpt.field] == null || rowData[tdOpt.field] == '' ? '' : rowData[tdOpt.field];
            $td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;">'
                + '<input type="text" placeholder="' + placeholder + '" value="' + value + '" style="margin-left: 0px; margin-right: 0px; padding-top: 1px; padding-bottom: 1px; width:100%;height:100%">'
                + '</div>');
            $td.width(width);
            var $input = $td.find('input');

            if (tdOpt.edit.type == 'zdy') {
                if (!rowData.parameterType || !rowData.parameterType == 'text') {
                    if (tdOpt.edit['click'] && typeof tdOpt.edit['click'] === 'function') {
                        $input.unbind('click').bind('click', function (event) {
                            tdOpt.edit['click'].call(this, event);
                        });
                    }
                    if (tdOpt.edit['focus'] && typeof tdOpt.edit['focus'] === 'function') {
                        $input.unbind('focus').bind('focus', function (event) {
                            tdOpt.edit['focus'].call(this, event);
                        });
                    }
                    $input.unbind('blur').on('blur', function (event) {
                        if (tdOpt.edit['blur'] && typeof tdOpt.edit['blur'] === 'function') {
                            tdOpt.edit['blur'].call(this, event);
                        }
                    });
                } else if (rowData.parameterType == 'number') {
                    if (tdOpt.edit['click'] && typeof tdOpt.edit['click'] === 'function') {
                        $input.unbind('click').bind('click', function (event) {
                            tdOpt.edit['click'].call(this, event);
                        });
                    }
                    if (tdOpt.edit['focus'] && typeof tdOpt.edit['focus'] === 'function') {
                        $input.unbind('focus').bind('focus', function (event) {
                            tdOpt.edit['focus'].call(this, event);
                        });
                    }
                    $input.unbind('blur').on('blur', function (event) {
                        if (tdOpt.edit['blur'] && typeof tdOpt.edit['blur'] === 'function') {
                            tdOpt.edit['blur'].call(this, event);
                        }
                    });
                    $input.unbind('keyup').on('keyup', function (event) {

                        //控制只输入数字
                        var reg = new RegExp("/[^\d{1,}\.\-\d{1,}|\d{1,}]/g");
                        if (!reg.test($input.val())) {
                            $input.val($input.val().replace(/[^\d{1,}\.\-\d{1,}|\d{1,}]/g, ""));
                        }
                        //控制只能输入4位
                        if ($input.val().length > 4) {
                            this.value = this.value.slice(0, 4);
                        }

                    });
                } else if (rowData.parameterType == 'select') {
                    var option=tdOpt.edit;
                    if (null != rowData.url) {
                        if (0 == rowData.url.indexOf("[")) {
                            var data = eval('(' + rowData.url + ')');
                            option.data = data;
                            option.url="";

                        } else {
                            option.data="";
                            option.url = rowData.url;

                        }

                    } else {
                        option.data = [{id: '', text: '请选择'}];
                        option.url="";

                    }
                    $input.bootstrapSelect(option);

                }
                else if (rowData.parameterType == 'date') {
                    $td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;" class="input-group date form_datetime" data-link-field="dtp_editable_input">'
                        + '<input class="form-control " onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm:ss\'})" type="text" value="' + value + '">'
                        + '</div>'
                        + '<input type="hidden" id="dtp_editable_input" value="' +  + '"/>'
                    );
                }
                else if(rowData.dialogType == 'ref_xml_path'){
                	tableTrTd=$td;
                	$td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;" class="input-group date form_file" data-link-field="file_editable_input">'
                			+ '<input type="text" class="form-control " id='+rowData.parameterName+' onclick="showRef_xml_pathModel(this)" value="' +value + '"/>'
                			+ '</div>'
                			+ '<input type="hidden" id="file_editable_input" value="' + + '"/>'
                	);
                }
                else if(rowData.parameterType == 'infile' ||rowData.parameterType == 'outfolder'|| rowData.parameterType =='outfile'|| rowData.parameterType =='inputinfolder'||rowData.parameterType =='inputparamfile'){
                    tableTrTd=$td;
                    $td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;" class="input-group date form_file" data-link-field="file_editable_input">'
                        + '<input type="text" class="form-control " id='+rowData.parameterName+' onclick="showtab5(this)"   value="' + value + '" />'
                        + '</div>'
                        + '<input type="hidden" id="file_editable_input" value="' + + '"/>'
                    );
                }
                else if (rowData.dialogType == 'prj_path'){
                    tableTrTd=$td;
                    $td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;" class="input-group date form_file" data-link-field="file_editable_input">'
                        + '<input type="text" class="form-control " id='+rowData.parameterName+' onclick="showPrj_pathModel(this)" value="' +value + '"/>'
                        + '</div>'
                        + '<input type="hidden" id="file_editable_input" value="' + + '"/>'
                    );
                }
                else if(rowData.dialogType == 'pan_xml_path'){
                    tableTrTd=$td;
                    $td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;" class="input-group date form_file" data-link-field="file_editable_input">'
                        + '<input type="text" class="form-control " id='+rowData.parameterName+' onclick="showPan_xml_pathModel(this)" value="' +value + '"/>'
                        + '</div>'
                        + '<input type="hidden" id="file_editable_input" value="' + + '"/>'
                    );
                }
                else if(rowData.dialogType == 'mss_xml_path'){
                    tableTrTd=$td;
                    $td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;" class="input-group date form_file" data-link-field="file_editable_input">'
                        + '<input type="text" class="form-control " id='+rowData.parameterName+' onclick="showMss_xml_pathModel(this)" value="' +value + '"/>'
                        + '</div>'
                        + '<input type="hidden" id="file_editable_input" value="' + + '"/>'
                    );
                }
                else if(rowData.dialogType == 'out_wkt'){
                    tableTrTd=$td;
                    $td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;" class="input-group date form_file" data-link-field="file_editable_input">'
                        + '<input type="text" class="form-control " id='+rowData.parameterName+' onclick="showtab6(this)" value="' +value + '"/>'
                        + '</div>'
                        + '<input type="hidden" id="file_editable_input" value="' + + '"/>'
                    );
                }

            } else {

                if (!tdOpt.edit.type || tdOpt.edit.type == 'text') {
                    if (tdOpt.edit['click'] && typeof tdOpt.edit['click'] === 'function') {
                        $input.unbind('click').bind('click', function (event) {
                            tdOpt.edit['click'].call(this, event);
                        });
                    }
                    if (tdOpt.edit['focus'] && typeof tdOpt.edit['focus'] === 'function') {
                        $input.unbind('focus').bind('focus', function (event) {
                            tdOpt.edit['focus'].call(this, event);
                        });
                    }
                    $input.unbind('blur').on('blur', function (event) {
                        if (tdOpt.edit['blur'] && typeof tdOpt.edit['blur'] === 'function') {
                            tdOpt.edit['blur'].call(this, event);
                        }
                    });

                } else if (tdOpt.edit.type == 'number') {
                    if (tdOpt.edit['click'] && typeof tdOpt.edit['click'] === 'function') {
                        $input.unbind('click').bind('click', function (event) {
                            tdOpt.edit['click'].call(this, event);
                        });
                    }
                    if (tdOpt.edit['focus'] && typeof tdOpt.edit['focus'] === 'function') {
                        $input.unbind('focus').bind('focus', function (event) {
                            tdOpt.edit['focus'].call(this, event);
                        });
                    }
                    $input.unbind('blur').on('blur', function (event) {
                        if (tdOpt.edit['blur'] && typeof tdOpt.edit['blur'] === 'function') {
                            tdOpt.edit['blur'].call(this, event);
                        }
                    });
                    $input.unbind('keyup').on('keyup', function (event) {

                        //控制只输入数字
                        var reg = new RegExp("/[^\d{1,}\.\-\d{1,}|\d{1,}]/g");
                        if (!reg.test($input.val())) {
                            $input.val($input.val().replace(/[^\d{1,}\.\-\d{1,}|\d{1,}]/g, ""));
                        }
                        //控制只能输入4位
                        if ($input.val().length > 4) {
                            this.value = this.value.slice(0, 4);
                        }

                    });

                }
                else if (tdOpt.edit.type == 'select') {
                    $input.bootstrapSelect(tdOpt.edit);
                }
                else if (tdOpt.edit.type == 'date') {
                    $td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;" class="input-group date form_datetime" data-link-field="dtp_editable_input">'
                        + '<input class="form-control" type="text" value="' + value + '">'
                        + '<span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>'
                        + '</div>'
                        + '<input type="hidden" id="dtp_editable_input" value="' + value + '"/>'
                    );
                    that.$body.find('.form_datetime').datetimepicker({
                        weekStart: 1,
                        todayBtn: 1,
                        autoclose: 1,
                        todayHighlight: 1,
                        startView: 2,
                        forceParse: 0,
                        language: 'zh-CN',
                        format: 'yyyy-mm-dd hh:ii:ss',
                        pickerPosition: 'bottom-left',
                        showMeridian: 1
                    });
                }
                else if(tdOpt.edit.type == 'infile' || tdOpt.edit.type =='outfile'|| tdOpt.edit.type =='outfolder'|| tdOpt.edit.type =='inputinfolder'||rowData.parameterType =='inputparamfile'){
                    $td.html('<div style="margin:0;padding:0;overflow:hidden;border:solid 0px red;height:' + (height) + 'px;width:' + (width) + 'px;" class="input-group date form_datetime" data-link-field="file_editable_input">'
                        + '</div>'
                        + '<input type="hidden" id="file_editable_input" value="' +  + '"/>'
                    );
                }

                else {
                    return;
                }


            }
        }

    }

    /**
     * 恢复tr，使之处于不可编辑状态
     */
    function recover(that) {
        var isModi = false;//判断行值是否变动过
        if (that.prevEditRow != null) {
            that.prevEditRow.find('td').closest('td').siblings().html(function (i, html) {
                $(this).attr('style', $(this).data('style'));
                var textVal = $(this).find('input[type="text"]').val();
                var hiddenVal = $(this).find('input[type="hidden"]').val();
//				if(typeof $(this).find('input[type="text"]').bootstrapSelect('getText') != 'object'){
                if (typeof $(this).find('input[type="text"]') != 'object') {
                    $(this).find('input[type="text"]').bootstrapSelect('destroy');
                }

                if (textVal != undefined) {
                    if ($(this).data('oldVal') != (hiddenVal ? hiddenVal : $.trim(textVal)) && $(this).data('field')) {
                        that.data[that.prevEditRow.data('index')][$(this).data('field')] = hiddenVal ? hiddenVal : $.trim(textVal);
                        isModi = true;
                    }
                    if (that.columns['column' + i].edit.required == true) {
                        if (textVal == null || textVal == '') {
                            that.enableAppend = false;
                            return '<span style="color:red;">必填项不能为空</span>';
                        }
                    }
                    return $.trim(textVal);
                }
            });
            //新值跟旧值不匹配证明被改过
            if (isModi || that.prevEditRow.hasClass('editable-insert')) {
                that.prevEditRow.addClass('editable-modify');
            }
            else {
                that.prevEditRow.removeClass('editable-modify');
            }
            that.prevEditRow = null;
            that.$body.find('> tr').removeClass('editable-select');
        }
    }

})(jQuery);


//扫描文件
function showtabPie5(ipt) {
    inputId=ipt;
    arrPie="";
    layer.open({
        type: 2,
        btn: ['确定', '关闭'],
        btnAlign: 'c',
        title: '选择文件',
        area: ['630px', '360px'],
        shade: 0.8,
        closeBtn: 1,
        shadeClose: true,
        content: base_url+'/template/ztree2',
        yes: function(index, layero){
            layer.close(index);
        },btn2: function(index, layero){
            //return false //开启该代码可禁止点击该按钮关闭
        }
        ,cancel: function(){
            //右上角关闭回调
            //return false 开启该代码可禁止点击该按钮关闭
        }
    });
}

//多光谱影像 选择文件
function showtabPie7(ipt) {
    inputId=ipt;
    layer.open({
        type: 2,
        btn: ['确定', '关闭'],
        btnAlign: 'c',
        title: '选择文件',
        area: ['630px', '360px'],
        shade: 0.8,
        closeBtn: 1,
        shadeClose: true,
        content: base_url+'/template/ztree4',
        yes: function(index, layero){
            layer.close(index);
        },btn2: function(index, layero){
            //return false //开启该代码可禁止点击该按钮关闭
        }
        ,cancel: function(){
            //右上角关闭回调
            //return false 开启该代码可禁止点击该按钮关闭
        }
    });
}

//基准影像 选择文件
function showtabPie8(ipt) {
	inputId=ipt;
	layer.open({
		type: 2,
		btn: ['确定', '关闭'],
		btnAlign: 'c',
		title: '选择文件',
		area: ['630px', '360px'],
		shade: 0.8,
		closeBtn: 1,
		shadeClose: true,
//		content: base_url+'/template/ztree4',
		content: base_url+'/template/ztree5',
		yes: function(index, layero){
			layer.close(index);
		},btn2: function(index, layero){
			//return false //开启该代码可禁止点击该按钮关闭
		}
		,cancel: function(){
			//右上角关闭回调
			//return false 开启该代码可禁止点击该按钮关闭
		}
	});
}

//选择文件夹
function showtabPie6(ipt) {
	$("#say").remove();
	$("#say1").remove();
	$("#say2").remove();
	
    inputId=ipt;
    layer.open({
        type: 2,
        btn: ['确定', '关闭'],
        btnAlign: 'c',
        title: '选择目录',
        area: ['630px', '360px'],
        shade: 0.8,
        closeBtn: 1,
        shadeClose: true,
        content: base_url+'/template/ztree3',
        yes: function(index, layero){
            layer.close(index);
        },btn2: function(index, layero){
            //return false 开启该代码可禁止点击该按钮关闭
        }
        ,cancel: function(){
            //右上角关闭回调
            //return false 开启该代码可禁止点击该按钮关闭
        }
    });
}


function showtab5(ipt) {
    inputId=ipt;
    layer.open({
        type: 2,
        btn: ['确定', '关闭'],
        btnAlign: 'c',
        title: '选择目录',
        area: ['630px', '360px'],
        shade: 0.8,
        closeBtn: 1,
        shadeClose: true,
        content: base_url+'/template/ztree',
        yes: function(index, layero){
            layer.close(index);
        },btn2: function(index, layero){
            //return false 开启该代码可禁止点击该按钮关闭
        }
        ,cancel: function(){
            //右上角关闭回调
            //return false 开启该代码可禁止点击该按钮关闭
        }
    });
}

function showtab6(ipt) {
    inputId=ipt;
    layer.open({
        type: 2,
        btn: ['确定', '关闭'],
        btnAlign: 'c',
        title: '选择输出投影',
        area: ['630px', '360px'],
        shade: 0.8,
        closeBtn: 1,
        shadeClose: true,
        content: base_url+'/template/outwktztree',
        yes: function(index, layero){
            layer.close(index);
        },btn2: function(index, layero){
            //return false 开启该代码可禁止点击该按钮关闭
        }
        ,cancel: function(){
            //右上角关闭回调
            //return false 开启该代码可禁止点击该按钮关闭
        }
    });
}

function showPrj_pathModel(ipt){
    inputId1=ipt;
    $("#prj_path.form select[name=modelId]").trigger("change");
    $('#prj_path').modal({backdrop: false, keyboard: false}).modal('show');
}

function showPan_xml_pathModel(ipt){
    inputId1=ipt;
    $("#pan_xml_path.form select[name=modelId]").trigger("change");
    $('#pan_xml_path').modal({backdrop: false, keyboard: false}).modal('show');
}

function showMss_xml_pathModel(ipt){
    inputId1=ipt;
    $("#mss_xml_path.form select[name=modelId]").trigger("change");
    $('#mss_xml_path').modal({backdrop: false, keyboard: false}).modal('show');
}

function showRef_xml_pathModel(ipt){
	inputId1=ipt;
	$("#ref_xml_path.form select[name=modelId]").trigger("change");
	$('#ref_xml_path').modal({backdrop: false, keyboard: false}).modal('show');
}

function prj_pathSave1(){
    var prjName = $("#prj_path .form input[name='prjName']").val();
    var prjPath = $("#prj_path .form input[name='prjPath']").val();
    var tal = prjPath + "\\"+prjName+".PIEPrj";
    $(inputId1).val(tal);
    $("#prj_path .form input[name='prj_Path']").val(tal);
    layer.msg("保存成功", {
        icon: 1,
        time: 1000 //2秒关闭（如果不配置，默认是3秒）
    });
}

function pan_xml_pathSave1(){
    var pan_xml_path = $("#pan_xml_path .form input[type='radio'][name='pan_xml_path']:checked").val();
    var pan_xml_folder =$(".form input[name='jobDesc']").val();
    var updateModel_pan_xml_folder =$("#updateModal .form input[name='jobDesc']").val();
    if(updateModel_pan_xml_folder != undefined  && updateModel_pan_xml_folder != ""){
    	pan_xml_folder = updateModel_pan_xml_folder;
    }
    $.post(base_url + "/pieorthoplugin/pan_xml_path1", { "pan_xml_path": pan_xml_path,"pan_xml_folder": pan_xml_folder },
        function(data){
            // alert(data.msg); // John
            // console.log(data.msg); //  2pm
            var tal = data.msg;
            $(inputId1).val(tal);
            $("#pan_xml_path .form input[name='panxml_path']").val(tal);
            layer.msg("保存成功", {
                icon: 1,
                time: 1000 //2秒关闭（如果不配置，默认是3秒）
            });
        }, "json");
}

function mss_xml_pathSave1(){
    var mss_xml_path = $("#mss_xml_path .form input[type='radio'][name='mss_xml_path']:checked").val();
    var mss_xml_folder =$(".form input[name='jobDesc']").val();
    var updateModel_mss_xml_folder =$("#updateModal .form input[name='jobDesc']").val();
    if(updateModel_mss_xml_folder != undefined  && updateModel_mss_xml_folder != ""){
    	mss_xml_folder = updateModel_mss_xml_folder;
    }
    $.post(base_url + "/pieorthoplugin/mss_xml_path", { "mss_xml_path": mss_xml_path , "mss_xml_folder": mss_xml_folder },
        function(data){
            // alert(data.msg); // John
            // console.log(data.msg); //  2pm
            var tal = data.msg;
            $(inputId1).val(tal);
            $("#mss_xml_path .form input[name='mssxml_path']").val(tal);
            layer.msg("保存成功", {
                icon: 1,
                time: 1000 //2秒关闭（如果不配置，默认是3秒）
            });
        }, "json");
}

function ref_xml_pathSave1(){
	var ref_xml_path = $("#ref_xml_path .form input[type='radio'][name='ref_xml_path']:checked").val();
	var ref_xml_folder =$(".form input[name='jobDesc']").val();
	var updateModel_ref_xml_folder =$("#updateModal .form input[name='jobDesc']").val();
	if(updateModel_ref_xml_folder != undefined  && updateModel_ref_xml_folder != ""){
		ref_xml_folder = updateModel_ref_xml_folder;
	}
	$.post(base_url + "/pieorthoplugin/ref_xml_path", { "ref_xml_path": ref_xml_path , "ref_xml_folder": ref_xml_folder },
			function(data){
		// alert(data.msg); // John
		// console.log(data.msg); //  2pm
		var tal = data.msg;
		$(inputId1).val(tal);
		$("#ref_xml_path .form input[name='refxml_path']").val(tal);
		layer.msg("保存成功", {
			icon: 1,
			time: 1000 //2秒关闭（如果不配置，默认是3秒）
		});
	}, "json");
}

//生成 已选文件表格
function say(){
	if(arrPie!=null){
		var arr = arrPie.split(',');
		//检测是不是数组的实例
		//console.log(arr instanceof Array);
	    var k = 0;
	    var str= '<hr>';
	    str += "<table id='say' class='table table-bordered table-striped'>";
	    str += '<tr>'+'<td align='+'center'+'>'+'已选影像列表'+'<td>'+'<tr>';
		for(var i=0; i<arr.length; i++) {
			str += '<tr>'+'<td>'+arr[i]+'<td>'+'<tr>';
		    /*str += '<tr>';
		    for(var j=0; j<6; j++) {
		        var tds = '<td>' + arr[k] + '</td>';
		        k++;
		        str += tds;
		    }
		    str += '</tr>';*/
		}
		str += '</table>';
		$('#divPie').html(str);
	}
	console.log(arr);
	//$("#pan_xml_path .form input[name='pan_xml_path1']").val(arrPie);
}

function say1(){
	if(arrPie!=null){
		var arr = arrPie.split(',');
		//检测是不是数组的实例
		//console.log(arr instanceof Array);
	    var k = 0;
	    var str= '<hr>';
	    str += "<table id='say1' class='table table-bordered table-striped'>";
	    str += '<tr>'+'<td align='+'center'+'>'+'已选影像列表'+'<td>'+'<tr>';
		for(var i=0; i<arr.length; i++) {
			str += '<tr>'+'<td>'+arr[i]+'<td>'+'<tr>';
		    /*str += '<tr>';
		    for(var j=0; j<6; j++) {
		        var tds = '<td>' + arr[k] + '</td>';
		        k++;
		        str += tds;
		    }
		    str += '</tr>';*/
		}
		str += '</table>';
		//str +='<hr>';
		$('#divPie1').html(str);
	}
	console.log(arr);
	//$("#pan_xml_path .form input[name='pan_xml_path1']").val(arrPie);
}
function say2(){
	if(arrPie!=null){
		var arr = arrPie.split(',');
		//检测是不是数组的实例
		//console.log(arr instanceof Array);
		var k = 0;
		var str= '<hr>';
		str += "<table id='say2' class='table table-bordered table-striped'>";
		str += '<tr>'+'<td align='+'center'+'>'+'已选影像列表'+'<td>'+'<tr>';
		for(var i=0; i<arr.length; i++) {
			str += '<tr>'+'<td>'+arr[i]+'<td>'+'<tr>';
			/*str += '<tr>';
		    for(var j=0; j<6; j++) {
		        var tds = '<td>' + arr[k] + '</td>';
		        k++;
		        str += tds;
		    }
		    str += '</tr>';*/
		}
		str += '</table>';
		//str +='<hr>';
		$('#divPie2').html(str);
	}
	console.log(arr);
	//$("#pan_xml_path .form input[name='pan_xml_path1']").val(arrPie);
}