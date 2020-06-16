/**
 * 基于jQuery类库的自定义对话框：jDialog
 *
 * @type {*}
 */

var jDialog = (function($, undefined){
    /**
     * 浏览器相关信息
     * @type {*}
     */
    var browserInfo = (function(){
        var userAgent = navigator.userAgent.toLowerCase();
        return {
            version: (userAgent.match( /.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/ ) || [])[1],
            safari: /webkit/.test( userAgent ),
            opera: /opera/.test( userAgent ),
            msie: /msie/.test( userAgent ) && !/opera/.test( userAgent ),
            mozilla: /mozilla/.test(userAgent)&&!/(compatible|webkit)/.test(userAgent)
        };
    })();

    /**
     * 判断是否为IE6
     * @type {*|Boolean}
     */
    var isIE6 = browserInfo.msie && parseInt(browserInfo.version,10) == 6;

    /**
     * dialog支持的事件列表
     * @type {Array}
     */
    var eventName = ['show','close', 'resize', 'hide', 'enterKey', 'escKey'];

    /**
     * dialog实例群
     * @type {Array}
     * @private
     */
    var dialogInstances = [];

    /**
     * 对话框的默认配置项目
     * @type {Object}
     */
    var defaultOptions = {
        modal           : true,     //是否模态窗口
        title           : null,     //窗口标题
        content         : null,     //内容
        width           : 300,      //对话框默认宽度：300px
        height          : null,     //自适应
        minWidth        : 200,      //窗口最小宽度
        minHeight       : 60,       //窗口最小高度
        maxWidth        : null,     //窗口最大宽度：默认无限大
        maxHeight       : null,     //窗口最大高度：默认无限大
        padding         : '10px',   //内边距，默认10px，可以配置上右下左四个值
        fixed           : true ,    //是否使用fix属性定位窗口
        left            : null,     //初始显示位置
        top             : null,     //初始显示位置
        closeable       : true,     //是否可关闭
        hideOnClose     : false,    //关闭时是否只隐藏窗口，而不是删除，可通过show方法再次显示
        draggable       : true,     //是否可拖拽
        contentType     : null,     //如果是iframe,请指定url
        zIndex          : 1024,     //默认z-index为1024
        resizable       : false,    //是否可以通过拖拽改变大小
        autoShow        : true,     //是否自动显示
        autoMiddle      : true,     //窗口大小改变时，保持居中
        autoClose       : 0,        //自动关闭，单位毫秒，0表示不自动关闭
        showShadow      : true,     //是否显示阴影
        showTitle       : true,     //是否显示标题
        textAlign       : 'inherit',//内容对其方式，默认：inherit
        buttonAlign     : 'right',  //按钮对齐方式，可选值：left / right / center，默认：right
        dialogClassName : null,     //对话框的自定义class
        maskClassName   : null,     //遮罩层的自定义class
        wobbleEnable    : false,    //模态下，点击空白处，是否允许对话框呈现动画摆动
        closeOnBodyClick: false,    //点击对话框之外的地方自动关闭
        buttons         : [],       //对话框中包含的按钮
        events          : {}        //事件集合，可选项有：show / close / hide / resize / enterKey / escKey
    };

    /**
     * Dialog类
     * @param s
     */
    var DialogClass = function(s) {
        // 对象管理
        dialogInstances.push(this);
        // 用户自定义配置
        this.cfg = $.extend({}, defaultOptions, s);

        // 相关DOM节点,element表示对话框节点，buttons表示按钮
        this.dom = {
            element : null,
            buttons : []
        };

        // 初始化
        this._init();
    };

    /**
     * Dialog类方法定义
     * @type {Object}
     */
    DialogClass.prototype = {
        constructor: DialogClass,


        /**
         * 临时变量
         * @type {Object}
         * @private
         */
        _tempVars : {},

        /**
         * 设置 / 获取 窗口标题
         * @param   {String}  title    需要设置的标题；不设置参数时，表示获取窗口标题
         * @return  {Object/String}   设置标题时，返回窗口对象；获取窗口标题时，返回标题
         */
        title: function(title){
            if((title || '').length) {
                // setter
                this.dom.element.find('.j-dialog-title>span.j-dialog-txt').html(title || "");
                return this;
            }else{
                // getter
                return this.dom.element.find(".j-dialog-title>span.j-dialog-txt").html();
            }
        },

        /**
         * 设置 / 获取 窗口内容
         * @param   {String}  html    需要设置的内容；不设置参数时，表示获取窗口内容
         * @return  {Object/String}   设置内容时，返回窗口对象；获取窗口内容时，返回内容
         */
        content: function(html) {
            if((html || '').length) {
                // setter
                this.dom.element.find(".j-dialog-body").html(html).css({
                    'text-align' : this.cfg.textAlign
                });
                return this;
            }else{
                // getter
                return $(".j-dialog-body", this.dom.element).html();
            }
        },

        /**
         * 设置 / 获取 窗口宽度
         * @param   {String}  width    需要设置的宽度；不设置参数时，表示获取窗口宽度
         * @return  {Object/Integer}   设置宽度时，返回窗口对象；获取窗口宽度时，返回宽度
         */
        width: function(width){
            if(parseInt(width,10) >= 0) {
                // setter
                this.dom.element.css("width", width);
                return this;
            }else{
                // getter
                return parseInt(this.dom.element.css('width'),10);
            }
        },

        /**
         * 设置 / 获取 窗口高度
         * @param   {String}  height    需要设置的高度；不设置参数时，表示获取窗口高度
         * @return  {Object/Integer}    设置高度时，返回窗口对象；获取窗口高度时，返回高度
         */
        height: function(height){
            height = parseInt(height,10) || 0;
            if(height) {
                // setter
                var shellHeight = 0;

                // 真正的弹层壳高度=titleHeight + bodyPadding
                var bodyPaddings = (this.cfg.padding || '').split(/\s+/);
                switch (bodyPaddings.length) {
                    case 4:
                    case 3:
                        shellHeight += (parseInt(bodyPaddings[0],10) || 0)
                            + (parseInt(bodyPaddings[2],10) || 0);
                        break;
                    case 2:
                    case 1:
                        shellHeight += 2 * (parseInt(bodyPaddings[0],10) || 0);
                        break;
                }
                shellHeight += 35;

                if(this.cfg.minHeight) {
                    height = Math.max(height,this.cfg.minHeight);
                }
                if(this.cfg.maxHeight) {
                    height = Math.min(height,this.cfg.maxHeight);
                }
                height -= shellHeight;
                $(".j-dialog-body", this.dom.element).css("height", height);
                return this;
            }else{
                // getter
                return parseInt(this.dom.element.css('height'),10);
            }
        },

        /**
         * 设置窗口在浏览器中垂直水平居中对其
         * @return {Object} 当前窗口对象
         */
        middle : function(){
            //居中显示
            var doc = $(document);
            var win = $(window);
            var o = this.cfg.fixed && !isIE6 ? [0, 0] : [doc.scrollLeft(), doc.scrollTop()];
            var left = o[0] + (win.width() - this.dom.element.outerWidth()) / 2;
            //考虑用户体验，top不能小于0
            var top =  o[1] + (win.height() - this.dom.element.outerHeight()) /2;
            top = (top >= 0) ? top : 0;

            // 更新位置
            return this.position({
                left : left,
                top : top
            });
        },

        /**
         * 设置/获取 对话框的位置
         * @param       {Object}  pos    需要设置的位置；不设置参数时，表示获取窗口位置
         * @p-config    {Integer} left   窗口位置left坐标
         * @p-config    {Integer} top    窗口位置top坐标
         * @return      {Object}         设置位置时，返回窗口对象；获取窗口位置时，返回位置
         */
        position: function(pos){
            if((!pos.left || isNaN(parseInt(pos.left,10))) && (!pos.top || isNaN(parseInt(pos.top,10)))) {
                // getter
                return this.dom.element.offset();
            }else{
                // setter
                if(!pos.left || isNaN(parseInt(pos.left,10))){
                    // setter for top
                    this.dom.element.css({"top" : pos.top});
                }else if(!pos.top || isNaN(parseInt(pos.top,10))){
                    // setter for left
                    this.dom.element.css({"left" : pos.left});
                }else{
                    // setter for left & top
                    this.dom.element.css({"left" : pos.left, "top" : pos.top});
                }
                this.triggerHandler('resize');
                return this;
            }
        },

        /**
         * 显示对话框
         * @return {Object} 返回当前窗口对象
         */
        show: function(){
            this.dom.element.show.apply(this.dom.element, arguments);
            if(this.mask){
                this.mask.cfg.safety = this.dom.element;
                this.mask.show.apply(this.mask, arguments);
            }

            // 配置了自动关闭
            if(this.cfg.autoClose) {
                var self = this;
                setTimeout(function(){
                    self.close();
                },parseInt(this.cfg.autoClose,10) || 3000)
            }
            this.triggerHandler('show');
            return this;
        },

        /**
         * 隐藏对话框
         * @return {Boolean}
         */
        hide: function(){
            this.dom.element.hide();
            if(this.mask){
                this.mask.hide.apply(this.mask, arguments);
            }
            return this;
        },


        /**
         * 关闭对话框
         * @return {Boolean}
         */
        close : function(){
            var self = this;
            if(!self.dom.element[0]) {
                return this;
            }
            self.triggerHandler('close');
            $(window).unbind("resize", this._tempVars.onResize);
            self.mask && self.mask.remove();
            this._tempVars.dragObj && this._tempVars.dragObj.remove();

            self.dom.element.remove();
            for(var i = 0, len = dialogInstances.length; i < len ; i++){
                if(dialogInstances[i] == self){
                    dialogInstances.splice(i, 1);
                    break;
                }
            }

            return this;
        },

        /**
         * 自定义对话框
         * @param  buttons   对话框按钮
         *         [{
         *               type : 'normal',    // normal 或者 highlight
         *               text : '确定',      // 按钮的显示文本
         *               handler : function(button,dialog){ // 按钮点击事件
         *                    // TODO ...
         *               }
         *         }]
         * @return  {Object} 设置按钮时，返回窗口对象；获取窗口按钮时，返回按钮
         */
        buttons: function(buttons){
            var self = this;
            if(buttons && buttons.length > 0) {
                // setter
                this.cfg.buttons = buttons;

                // 把按钮append到dialog中
                var htmlBtns = [];
                $.each(buttons,function(i,btn){
                    var v,cls = 'j-dialog-btn';
                    if(btn.type == 'highlight') {
                        cls += ' x-highlight';
                    }
                    v = btn.text || '确定';
                    htmlBtns.push('<input type="button" class="' + cls + '" value="' + v + '" />');
                });
                var btnContainer = $('<div class="j-dialog-buttons"></div>').appendTo(this.dom.element).html(htmlBtns.join(''));

                // 按钮对其方式
                btnContainer.css('text-align',this.cfg.buttonAlign || 'right');

                // 给每个按钮绑定点击事件
                var selfBtns = this.dom.buttons = $('input[type=button]', btnContainer);
                $.each(buttons,function(i,btn){
                    var thisBtn = $(selfBtns[i]);
                    thisBtn.click(function(e){
                        if(btn.handler && typeof btn.handler == 'function') {
                            btn.handler.call(thisBtn[0],thisBtn,self);
                        }
                    });
                    if(!isIE6) {
                        thisBtn.hover(function(e){
                            var cls_h = 'x-hover';
                            if(thisBtn.hasClass('x-highlight')) {
                                cls_h = 'x-hlhover';
                            }
                            thisBtn.addClass(cls_h);
                        },function(e){
                            thisBtn.removeClass('x-hover').removeClass('x-hlhover');
                        });
                    }
                });

                return this;
            }else{
                // getter
                return this.dom.buttons;
            }
        },

        /**
         * 给对话框绑定事件
         * @return {*}
         */
        bind: function(){
            this.dom.element.bind.apply(this.dom.element, arguments);
            return this;
        },

        /**
         * 触发对话框的事件
         */
        triggerHandler: function(){
            this.dom.element.trigger.apply(this.dom.element, arguments);
            return this;
        },

        /**
         * 对话框初始化
         * @private
         */
        _init : function(){
            var self = this;
            // 如果未设置标题，则强制设置窗口不可拖拽
            if(!this.cfg.showTitle){
                this.cfg.draggable = false;
            }
            // 如果设置了窗口的位置，无论是top还是left，都强制设置自动居中对其属性为false
            if(!isNaN(parseInt(this.cfg.top)) || !isNaN(parseInt(this.cfg.left))
                || this.cfg.anchor){
                this.cfg.autoMiddle = false;
            }

            // 对话框的css class name
            var className = 'j-dialog ' + (this.cfg.dialogClassName ? this.cfg.dialogClassName : '');
            // 如果配置了fixed
            if (!isIE6 && this.cfg.fixed){
                className += ' j-dialog-fix';
            }
            // 显示阴影
            if (this.cfg.showShadow){
                className += ' j-dialog-shadow';
            }

            // 遮罩层控制
            this._addMask();

            // dialog节点
            this.dom.element = $('<div class="' + className + '"></div>')
                .css({
                    "zIndex" : this.cfg.zIndex,
                    "display" : "none"
                }).appendTo(document.body).focus();

            // 拼装dialog并初始化其位置
            this._setupTitleBar().title(this.cfg.title);
            this._setupContent().content(this.cfg.content);
            this.buttons(this.cfg.buttons);
            this.width(this.cfg.width);
            this.height(this.cfg.height);

            // 设置padding
            $('#j-dialog-body',this.dom.element).css('padding',this.cfg.padding);

            //事件绑定
            $.each(eventName, function(i, evt){
                if(self.cfg.events[evt]){
                    self.dom.element.bind(evt, {
                        dialog : self     // 在每个事件参数中，可以通过event.data.dialog获取到窗口对象
                    }, self.cfg.events[evt]);
                }
            });

            //响应Esc键关闭窗口，Enter键确认提交，其他地方点击，关闭窗口
            this._setupEscKey()._setupEnterKey()._setupBodyClick();

            // 是否初始化即显示
            this.cfg.autoShow && this.show();

            // 设置anchor：锚点
            if(this.cfg.anchor) {
                this._tempVars.domAnchor = $(this.cfg.anchor.target);
                // 设置位置
                if(!this._tempVars.domAnchor[0]) {
                    throw new Error('The "anchor.target" must be a HTMLElement instance!');
                    return this;
                }

                this._setupTriangle();
            }else if(this.cfg.autoMiddle){
                // 是否垂直水平居中对齐
                this.middle();
            }else{
                this.position({
                    left : this.cfg.left,
                    top : this.cfg.top
                });
            }

            /**
             * 窗口的Resize
             */
            this._tempVars.onResize = function(){
                // resize事件是一个非常特殊的东西，必须采取 解绑->执行->再次绑定 的过程
                $(window).unbind("resize", self._tempVars.onResize);
                // 窗口resize时候，自动居中对其
                if(self.cfg.autoMiddle) {
                    self.middle();
                }
                self.triggerHandler('resize');
                self.sizeTimer = setTimeout(function(){
                    $(window).bind("resize", self._tempVars.onResize);
                }, 10);
            }

            $(window).bind("resize", this._tempVars.onResize);
        },

        /**
         * 添加遮罩层
         * @private
         */
        _addMask : function(){
            var self = this;
            if(this.cfg.modal){
                var maskCfg = {};
                // 是否单独配置了遮罩层的class name
                if(this.cfg.maskClassName){
                    maskCfg.className = this.cfg.maskClassName;
                }
                this.mask = jMask.init(maskCfg);

                // 模态情况下，点击空白处，支持窗口摆动，默认：false
                if(this.cfg.wobbleEnable) {
                    this._tempVars.cssAnimationGoing = false;
                    this.mask.element.click(function(e){
                        if(self._tempVars.cssAnimationGoing) return false;
                        self.dom.element.addClass('j-ani-wobble');
                        self._tempVars.cssAnimationGoing = true;
                        setTimeout(function(){
                            self.dom.element.removeClass('j-ani-wobble');
                            self._tempVars.cssAnimationGoing = false;
                        },1100); // 因为动画需要执行1s，所以这里延迟一点时间
                    });
                }
            }
            return self;
        },

        /**
         * 当其他地方被点击时，关闭窗口
         * @return {*}
         * @private
         */
        _setupBodyClick: function(){
            var self = this;
            if(!self.cfg.closeOnBodyClick) {
                return this;
            }
            var func = function(evt){
                if(!$.contains(self.dom.element[0],evt.target)) {
                    self.close();
                }
            };

            self.bind('show', function(evt){
                $(document).bind("mousedown", func);
            });
            $.each(['hide','close'],function(i,eventName){
                self.bind(eventName, function(evt){
                    $(document).unbind('mousedown', func);
                });
            });
            return self;
        },

        /**
         * 设置对话框的标题栏
         * @private
         */
        _setupTitleBar: function() {
            var self = this;

            // 是否支持：关闭
            if (this.cfg.closeable) {
                var btnClose = $('<a href="#" class="j-dialog-close" title="关闭">&nbsp;</a>')
                    .appendTo(this.dom.element)
                    .bind({
                        click : function(evt){
                            self.cfg.hideOnClose ? self.hide() : self.close();
                            evt.preventDefault();
                            evt.stopPropagation();
                        },
                        mousedown : function(evt){
                            evt.preventDefault();
                            evt.stopPropagation();
                        }
                    });
                // 不显示标题的情况下，调整关闭按钮的位置到右上角处
                if (!this.cfg.showTitle) {
                    btnClose.addClass('btn-without-title');
                }
            }

            // 配置标题栏
            if (!this.cfg.showTitle) {
                return self;
            }

            // titleBar 设置
            this._tempVars.titleBar = $('<div class="j-dialog-title"><span class="j-dialog-txt"></span></div>')
                .appendTo(this.dom.element);

            // 是否支持： 拖拽
            if (this.cfg.draggable) {
                this._tempVars.titleBar.addClass('j-draggable');
                var offset = null;
                this._tempVars.dragObj = jDrag.init({
                    handle: self._tempVars.titleBar,
                    target: self.dom.element,
                    onDown: function(){
                        self._setupHackDiv(1);
                        self.dom.element.addClass('j-user-select');
                    },
                    onUp: function(){
                        self._setupHackDiv(0);
                        self.dom.element.removeClass('j-user-select');
                    }
                });
            }
            return self;
        },

        /**
         * 处理内部有iframe页面会卡的情况
         * @param display
         * @private
         */
        _setupHackDiv: function(display){
            var self = this;
            if(display){
                if($("IFRAME", self.dom.element).length > 0){
                    //当内部有iframe的时候，需要特殊处理，要不然页面会卡
                    var con = $(".j-dialog-content", self.dom.element);
                    self.hack_div = (self.hack_div || $("<div></div>")).appendTo(con).css({
                        position:"absolute",
                        "left" : 0,
                        "top" : 0,
                        "cursor" : "move",
                        "display": "block",
                        "width": self.dom.element.outerWidth(),
                        "height" : self.dom.element.outerHeight()
                    });
                }
            } else {
                if(self.hack_div) {
                    self.hack_div.css("display", "none");
                }
            }
            return self;
        },

        /**
         * 响应Esc键关闭窗口
         * @private
         */
        _setupEscKey: function(){
            var self = this;
            var func =  function(event){
                if(event.keyCode == 27){
                    self.triggerHandler('escKey');
                }
            };

            $(self.dom.element).bind('show', function(evt){
                $(document).bind("keydown", func);
            });
            $.each(['hide','close'],function(i,eventName){
                $(self.dom.element).bind(eventName, function(evt){
                    $(document).unbind('keydown', func);
                });
            });
            return self;
        },

        /**
         * 响应回车键
         * @private
         */
        _setupEnterKey: function(){
            var self = this;
            var func = function(event){
                if(event.keyCode == 13 || event.keyCode == 10){
                    self.triggerHandler("enterKey");
                }
            };

            $(self.dom.element).bind('show', function(evt){
                $(document).bind("keydown", func);
            });
            $.each(['hide','close'],function(i,eventName){
                $(self.dom.element).bind(eventName, function(evt){
                    $(document).unbind('keydown', func);
                });
            });

            return self;
        },

        /**
         * 创建小三角
         * @return {*}
         * @private
         */
        _setupTriangle : function(){
            this._tempVars.triangle = $([
                '<div class="j-triangle">',
                '<div class="t-border"></div>',
                '<div class="t-inset"></div>',
                '</div>'
            ].join('')).appendTo(this.dom.element).addClass('anchor-' + {
                left : 'right',
                right : 'left',
                top : 'bottom',
                bottom : 'top'
            }[{
                'left':'left',
                'left-top':'left',
                'left-bottom':'left',

                'top':'top',
                'top-left':'top',
                'top-right':'top',

                'right':'right',
                'right-top':'right',
                'right-bottom':'right',

                'bottom':'bottom',
                'bottom-left':'bottom',
                'bottom-right':'bottom'
            }[this.cfg.anchor.position || 'right']]);

            // 确定小三角的位置
            var posTriangle = {},posDialog = {};
            var domAnchorOffset = this._tempVars.domAnchor.offset();
            var domAnchorSize = {
                width : parseInt(this._tempVars.domAnchor.width(),10),
                height : parseInt(this._tempVars.domAnchor.height(),10)
            };
            var domDialogSize = {
                width : parseInt(this.width(),10),
                height : parseInt(this.height(),10)
            };
            var offset = $.extend({
                top:0,
                left:0,
                right:0,
                bottom:0
            },this.cfg.anchor.offset || {});
            var tpfs = parseInt(this.cfg.anchor.trianglePosFromStart,10) || 0;

            switch ((this.cfg.anchor.position || 'right').toLowerCase()){
                // 顶部居中对齐
                case 'top':
                    posTriangle = {
                        top : 0,
                        left : tpfs ? tpfs : (domDialogSize.width - 24) / 2
                    };
                    posDialog = {
                        top : domAnchorOffset.top - domDialogSize.height - 12 - offset.top,
                        left : domAnchorOffset.left + (domAnchorSize.width - domDialogSize.width) / 2 + offset.left
                    };
                    break;

                // 左上角对齐
                case 'top-left':
                    posTriangle = {
                        top : 0,
                        left : tpfs ? tpfs : (domDialogSize.width - 24) / 2
                    };
                    posDialog = {
                        top : domAnchorOffset.top - domDialogSize.height - 12 - offset.top,
                        left : domAnchorOffset.left + offset.left
                    };
                    break;

                // 右上角对齐
                case 'top-right':
                    posTriangle = {
                        top : 0,
                        left : tpfs ? domDialogSize.width - 24 - tpfs : (domDialogSize.width - 24) / 2
                    };
                    posDialog = {
                        top : domAnchorOffset.top - domDialogSize.height - 12 - offset.top,
                        left : (domAnchorOffset.left + domAnchorSize.width) - domDialogSize.width + offset.right
                    };
                    break;

                // 右，居中对齐
                case 'right':
                    posTriangle = {
                        top : tpfs ? tpfs-domDialogSize.height : -(domDialogSize.height + 24) / 2,
                        left : -24
                    };
                    posDialog = {
                        top : domAnchorOffset.top + (domAnchorSize.height - domDialogSize.height) / 2 + offset.top,
                        left : domAnchorOffset.left + domAnchorSize.width + 12 + offset.right
                    };
                    break;

                // 右上角对齐
                case 'right-top':
                    posTriangle = {
                        top : tpfs ? tpfs-domDialogSize.height : -(domDialogSize.height + 24) / 2,
                        left : -24
                    };
                    posDialog = {
                        top : domAnchorOffset.top + offset.top,
                        left : domAnchorOffset.left + domAnchorSize.width + 12 + offset.right
                    };
                    break;

                // 右下角对齐
                case 'right-bottom':
                    posTriangle = {
                        top : tpfs ? -24-tpfs : -(domDialogSize.height + 24) / 2,
                        left : -24
                    };
                    posDialog = {
                        top : domAnchorOffset.top + domAnchorSize.height - domDialogSize.height - offset.bottom,
                        left : domAnchorOffset.left + domAnchorSize.width + 12 + offset.right
                    };
                    break;

                // 下对齐
                case 'bottom':
                    posTriangle = {
                        top : -(domDialogSize.height + 24),
                        left : tpfs ? tpfs : (domDialogSize.width - 24) / 2
                    };
                    posDialog = {
                        top : domAnchorOffset.top + domAnchorSize.height + 12 + offset.bottom,
                        left : domAnchorOffset.left + (domAnchorSize.width - domDialogSize.width) / 2 + offset.left
                    };
                    break;

                // 下左角对齐
                case 'bottom-left':
                    posTriangle = {
                        top : -(domDialogSize.height + 24),
                        left : tpfs ? tpfs : (domDialogSize.width - 24) / 2
                    };
                    posDialog = {
                        top : domAnchorOffset.top + domAnchorSize.height + 12 + offset.bottom,
                        left : domAnchorOffset.left + offset.left
                    };
                    break;

                // 下右角对齐
                case 'bottom-right':
                    posTriangle = {
                        top : -(domDialogSize.height + 24),
                        left : tpfs ? domDialogSize.width - 24 - tpfs : (domDialogSize.width - 24) / 2
                    };
                    posDialog = {
                        top : domAnchorOffset.top + domAnchorSize.height + 12 + offset.bottom,
                        left : (domAnchorOffset.left + domAnchorSize.width) - domDialogSize.width + offset.right
                    };
                    break;

                // 左居中对齐
                case 'left':
                    posTriangle = {
                        top : tpfs ? tpfs-domDialogSize.height : -(domDialogSize.height + 24) / 2,
                        left : domDialogSize.width
                    };
                    posDialog = {
                        top : domAnchorOffset.top + (domAnchorSize.height - domDialogSize.height) / 2 + offset.top,
                        left : domAnchorOffset.left - domDialogSize.width - 12 - offset.left
                    };
                    break;

                // 左上对齐
                case 'left-top':
                    posTriangle = {
                        top : tpfs ? tpfs-domDialogSize.height : -(domDialogSize.height + 24) / 2,
                        left : domDialogSize.width
                    };
                    posDialog = {
                        top : domAnchorOffset.top + offset.top,
                        left : domAnchorOffset.left - domDialogSize.width - 12 - offset.left
                    };
                    break;

                // 左下对齐
                case 'left-bottom':
                    posTriangle = {
                        top : tpfs ? -24-tpfs : -(domDialogSize.height + 24) / 2,
                        left : domDialogSize.width
                    };
                    posDialog = {
                        top : domAnchorOffset.top + domAnchorSize.height - domDialogSize.height - offset.bottom,
                        left : domAnchorOffset.left - domDialogSize.width - 12 - offset.left
                    };
                    break;

            }

            this._tempVars.triangle.css(posTriangle);

            this.position(posDialog);

            this.dom.element.css('overflow','visible');

            return this;
        },

        /**
         * 安装Content-layout
         * @private
         */
        _setupContent: function(){
            if(this.cfg.contentType === 'iframe'){
                this.cfg.content = $("<iframe></iframe>")
                    .css({
                        width: "100%",
                        height : "100%",
                        border : "none"
                    }).attr({
                        "src": this.cfg.content,
                        "frameBorder" : 0
                    });
            }

            var wrap = $([
                '<div class="j-dialog-content">',
                '<div class="j-dialog-body" id="j-dialog-body"></div>',
                '</div>'].join(''));
            var self= this;

            this.dom.element.append(wrap);

            // 是否支持：resize
            if(this.cfg.resizable){
                var con = $(".j-dialog-content", self.dom.element);
                this.cfg.minWidth = this.cfg.minWidth || 0;
                this.cfg.minHeight = this.cfg.minHeight || 0;
                $.each(['es'], function(k, v){
                    var ele = $('<div class="resizable-' + v + '"><div></div></div>');
                    self.dom.element.append(ele);
                    var val_W = null, val_H = null;
                    jDrag.init({
                        handle: ele,
                        onDown: function(event){
                            self._setupHackDiv(1);
                            val_W = parseInt(self.dom.element.width());val_H = parseInt(con.height());
                            self.dom.element.addClass('j-user-select');
                        },
                        onMove: function(event, x, y){
                            var width = val_W + x;
                            var height = val_H + y;
                            if(!((self.cfg.minWidth && width < self.cfg.minWidth && x < 0)
                                || (self.cfg.maxWidth && (width > self.cfg.maxWidth) && x >0)))
                                self.dom.element.width(width);
                            if(!( (self.cfg.minHeight  && (height < self.cfg.minHeight) && y < 0)
                                || ((self.cfg.maxHeight) && (height > self.cfg.maxHeight) && y >0 )))
                                con.height(height);
                            var w = self.dom.element.outerWidth(),
                                h = self.dom.element.outerHeight();
                            if(self.hack_div)
                                self.hack_div.css({"width": w, "height" : h});
                        },
                        onUp: function(event){
                            self._setupHackDiv(0);
                            self.dom.element.removeClass('j-user-select');
                        }
                    });
                });
            }

            return self;
        }
    };


    /**
     * 自定义对话框，最通用最基础的一个API接口
     * @param       {Object}  options dialog的其他配置项
     * @return      {Object}  当前dialog对象
     */
    var _dialog = function(options){
        var cfg = $.extend({},  options || {});
        return new DialogClass(cfg);
    };

    /**
     * 普通alert框
     * @param       {String}  content 提示框的内容
     *
     * @param       {Object}  button  确定按钮，最多只有一个按钮
     * @p-config    {String}  text    按钮文字，默认：确定
     * @p-config    {String}  type    按钮类型，默认：normal，可选：highlight（高亮）
     * @p-config    {String}  handler 按钮点击后的执行动作，默认：关闭当前对话框
     *
     * @param       {Object}  options dialog的其他配置项
     *
     * @return      {Object}  当前dialog对象
     */
    var _alert = function(content,button,options){
        options = options || {};
        button = $.extend({
            type : 'highlight',
            handler : function(btn,dlg){
                dlg.close();
            }
        },button || {});
        options = $.extend({
            wobbleEnable:true
        },options,{
            content : content,
            buttons : [].concat(button),
            title : options.title ? options.title : '提示'
        });
        return _dialog(options);
    };

    /**
     * 确认对话框
     * @param       {String}  content       提示框的内容
     *
     * @param       {Object}  acceptButton  确认按钮
     * @p-config    {String}  text          按钮文字，默认：确定
     * @p-config    {String}  type          按钮类型，默认：normal，可选：highlight（高亮）
     * @p-config    {String}  handler       按钮点击后的执行动作，默认：关闭当前对话框
     *
     * @param       {Object}  cancelButton  取消按钮
     * @p-config    {String}  text          按钮文字，默认：取消
     * @p-config    {String}  type          按钮类型，默认：normal，可选：highlight（高亮）
     * @p-config    {String}  handler       按钮点击后的执行动作，默认：关闭当前对话框
     *
     * @param       {Object}  options       dialog的其他配置项
     *
     * @return      {Object}  当前dialog对象
     */
    var _confirm = function(content,acceptButton,cancelButton,options){
        options = options || {};

        // 确认按钮
        acceptButton = $.extend({
            type : 'highlight',
            text : '保存',
            handler : function(btn,dlg){
                dlg.close();
            }
        },acceptButton || {});

        // 取消按钮
        cancelButton = $.extend({
            text : '另存为',
            handler : function(btn,dlg){
                dlg.close();
            }
        },cancelButton || {});

        options = $.extend({
            wobbleEnable : true
        },options,{
            content : content,
            buttons : [].concat([acceptButton,cancelButton]),
            title : options.title ? options.title : '修改/新增'
        });
        return _dialog(options);
    };


    /**
     * 普通消息框，无title
     * @param       {String}  content 消息的内容
     *
     * @param       {Object}  options dialog的其他配置项
     *
     * @return      {Object}  当前dialog对象
     */
    var _message = function(content,options){
        options = options || {};
        options = $.extend({
            content : content,
            padding : '20px 10px 20px 10px',
            textAlign : 'center'
        },options,{
            showTitle : false
        });
        return _dialog(options);
    };


    /**
     * 一个带有小三角箭头的tip消息框，无title，非模态
     *
     * @param       {String}  content 消息的内容
     *
     * @param       {Object}  anchor 小三角箭头的相关配置
     *
     * @p-config    {jQ-Elm}  target    小箭头需要指向的HTML节点，且用jQuery封装的对象
     * @p-config    {String}  position  tip消息框出现的位置（相对于target），可选：
     *                                  top / top-left / top-right
     *                                  right / right-top / right-bottom
     *                                  bottom / bottom-left / bottom-right
     *                                  left / left-top / left-bottom
     * @p-config    {Object}  offset    消息框与target之间的位置偏移
     * @p-c-item    {Integer} top       dialog与target之间顶部偏移，position中含top时生效
     * @p-c-item    {Integer} right     dialog与target之间右侧偏移，position中含right时生效
     * @p-c-item    {Integer} bottom    dialog与target之间底部偏移，position中含bottom时生效
     * @p-c-item    {Integer} left      dialog与target之间左侧偏移，position中含left时生效
     * @p-config    {Integer} trianglePosFromStart 小三角距离弹窗边缘的距离
     *
     * @param       {Object}  options dialog的其他配置项
     *
     * @return      {Object}  当前dialog对象
     */
    var _tip = function(content,anchor,options){
        options = options || {};
        options = $.extend({
            padding : '20px 10px 20px 10px',
            textAlign : 'center',
            width : 'auto',
            anchor : {
                target : null,
                position : 'right'
            }
        },options,{
            content : content,
            anchor : anchor,
            showTitle : false,
            showShadow: false,
            modal : false,
            fixed : false
        });
        return _dialog(options);
    };

    /**
     * 在对话框中显示一个iframe页面
     * @param       {String}  url     消息的内容
     *
     * @param       {Object}  options dialog的其他配置项
     *
     * @return      {Object}  当前dialog对象
     */
    var _iframe = function(url,options){
        options = options || {};
        options = $.extend({
            content : url,
            title : '窗口',
            width:600,
            height:300
        },options,{
            contentType : 'iframe'
        });
        return _dialog(options);

    };


    return {
        version : '1.3',
        dialog  : _dialog,
        alert   : _alert,
        confirm : _confirm,
        message : _message,
        tip     : _tip,
        iframe  : _iframe
    };

})(jQuery);

/**
 * 遮罩层，通过jMask.init(options)即可展示一个遮罩层
 * @author zhaoxianlie
 */
var jMask = (function($, undefined){
    /**
     * 遮罩层
     * @param options 配置项
     */
    var MaskClass = function(options){
        var self = this;
        // 配置项
        this.cfg = $.extend({
            zIndex : 1024,
            resizable: true
        }, options);

        // 遮罩层节点
        this.element = $('<div class="j-dialog-mask ' + (this.cfg.className || '') + '"/>').appendTo(document.body)
            .css({
                'display': 'none',
                'zIndex' : this.cfg.zIndex,
                'width': this.width(),
                'height' : this.height()
            });

        // 显示遮罩
        if(this.cfg.show) {
            this.show();
        }

        // 尺寸重绘方法
        this.resizeFunc = function(){
            self.css("width", self.width());
            self.css("height", self.height());
            self.triggerHandler('resize');
        };

        //绑定resize事件
        if(this.cfg.resizable){
            $(window).bind('resize', this.resizeFunc);
        }

    };

    /**
     * 遮罩层的原型链方法
     * @type {Object}
     */
    MaskClass.prototype = {
        constructor: MaskClass,
        /**
         * 显示
         */
        show: function(){
            this.element.show.apply(this.element, arguments);
            this._processTages(1);
        },

        /**
         * 隐藏
         */
        hide: function(){
            this.element.hide.apply(this.element, arguments);
            this._processTages(0);
        },

        /**
         * 获取当前可视区域窗口的宽度
         */
        width: function() {
            return $('body').width();
        },

        /**
         * 获取当前可视区域窗口的高度
         */
        height: function() {
            return Math.max($('body').height(),$(window).height());
        },

        /**
         * 设置遮罩层的样式
         */
        css: function(){
            this.element.css.apply(this.element, arguments);
        },

        /**
         * 事件触发
         */
        triggerHandler: function(){
            this.element.triggerHandler.apply(this.element, arguments);
        },

        /**
         * 事件绑定
         */
        bind: function(){
            this.element.bind.apply(this.element, arguments);
        },

        /**
         * 析构方法
         */
        remove: function(){
            this._processTages(0);
            this.element && this.element.remove();
            $(window).unbind('resize', this.resizeFunc);
            for(var i in this)
                delete this[i];
        },

        _processTages: function(isHide){
            var self = this;

            var userAgent = navigator.userAgent.toLowerCase();
            var isMSIE = /msie/.test( userAgent ) && !/opera/.test( userAgent );
            if(!isMSIE) {
                return;
            }

            self.special = self.special || [];
            if(isHide){
                if(self.special.length > 0)
                    return;

                var doms = $("SELECT");

                if(this.cfg.safety){
                    doms = doms.filter(function(index){
                        return self.cfg.safety.find(this).length == 0;
                    });

                }
                doms.each(function(){
                    var obj = $(this);

                    self.special.push({dom: this, css: obj.css('visibility')});
                    obj.css('visibility', 'hidden');
                })
            }
            else{
                for(var i = 0, len = self.special.length; i < len; i++){
                    $(self.special[i].dom).css('visibility', self.special[i].css || '');
                    self.special[i].dom = null;
                }
            }
        }
    };

    /**
     * 创建一个遮罩
     * @param options
     * @return {*}
     * @private
     */
    var _init = function(options) {
        return new MaskClass(options);
    }

    return {
        init : _init
    };
})(jQuery);

/**
 * jQuery drag plugin
 * @author jQuery / zhaoxianlie
 */
var jDrag = (function($, undefined){
    /**
     * 可拖拽的节点封装
     * @param options
     */
    var DraggableClass = function draggable(options){
        if(!options.handle)
            return;
        var s = $.extend({}, options);
        var handle = $(s.handle);
        var target = s.target && $(s.target);
        var currPos = null;
        var type = (s.type || '').toString().toUpperCase();
        handle[0].onselectstart = function() { return false; }
        handle.attr( "unselectable", "on" ).css( "MozUserSelect", "none" );

        var _onDrag = null, _dragging = null;
        var _mouseMove = function(evt){

            var x = evt.pageX - _dragging[0],
                y = evt.pageY - _dragging[1];

            if(target){
                var css = {};
                if(type !== 'Y')
                    css.left = currPos.left + x;
                if(type !== 'X')
                    css.top = currPos.top + y;
                if(!$.isEmptyObject(css))
                    target.css(css);
            }

            if($.isFunction(s.onMove))
                s.onMove(evt, x, y);
        };
        var _mouseUp = function(evt){
            $(document).unbind("mousemove", _mouseMove).unbind("mouseup", _mouseUp);
            _onDrag = false;
            if($.isFunction(s.onUp))
                s.onUp(evt);
        };
        var _mouseDown =  function(evt){
            if(_onDrag)
                _mouseUp();
            if($.isFunction(s.onDown))
                s.onDown(evt);
            if(target)
                currPos = {left: target[0].offsetLeft, top: target[0].offsetTop};
            _dragging = [evt.pageX, evt.pageY];
            $(document).bind("mousemove", _mouseMove).bind("mouseup", _mouseUp);
        };

        s.handle.bind('mousedown', _mouseDown);

        this.remove = function(){
            $(document).unbind("mousemove", _mouseMove);
            $(document).unbind("mousemove", _mouseUp);
            $(document).unbind("mousedown", _mouseDown);
        }

        this.destroy = function () {
            handle.unbind('mousedown', _mouseDown);
        }
    }

    /**
     * 初始化一个可拖拽的节点
     * @param options
     * @private
     */
    var _init = function(options){
        return new DraggableClass(options);
    };

    return {
        init : _init
    };
})(jQuery);

//////////////////////////////////////////////////////////////////////////////以上为弹出对话框代码

var _version = (navigator.language||navigator.browserLanguage).toLowerCase();
var pageConfig_version =null;
if(_version.indexOf('zh')>=0)
{
	pageConfig_version = "pageConfig";
}
else if(_version.indexOf('en')>=0)
{
	pageConfig_version = "pageConfig_en";
}
else
{
	pageConfig_version = "pageConfig";
}

//提示框代码


require.config({	
	urlArgs : "v" + (new Date()).getTime(),
	paths: {
		"jquery": "../javascript/jquery/jquery-1.8.3.min",
		"zTree": "../javascript/jquery/zTree/js/jquery.ztree.all",
		"ztreeExhide": "../javascript/jquery/zTree/js/jquery.ztree.exhide",
		"mxGraph" : "../mxgraph/js/mxClient1",
		"i18n":"../resources/lib/jquery.i18n.properties-1.0.9"
	},
	shim: {

		"jquery" : {
			exports: '$'
		},
		"i18n": {
			exports : "i18n"
		},
		"zTree" : {
			deps : ["jquery"],
			exports : "zTree"
		},
		"ztreeExhide" : {
			deps : ["jquery","zTree"],
			exports : "ztreeExhide"
		},
		"mxGraph" : {
			exports : "mxGraph"
		}
	}	
});
require(['jquery', 'zTree', 'ztreeExhide',"mxGraph"], function ($, zTree, ztreeExhide,mxGraph){
	require.config({
		baseUrl : "static/js/flow/js",
		paths: {
			"pageConfig": pageConfig_version,
			"viewShow": "viewShow",
			"i18n":"resources/lib/jquery.i18n.properties-1.0.9"
		},
		shim: {
			"i18n": {
				exports : "i18n"
			},
			"pageConfig" : {
				deps : ["jquery","i18n"],
				exports : "page"
			},
			"viewShow" : {
				deps : ["jquery"],
				exports : "viewShow"
			}
		}	
	});
	require(["i18n","pageConfig","viewShow"],function(i18n,page,viewShow){
		//初始化页面
		//jQuery.i18n.properties();
        var id=getParam("id");

		page.init();
		if("null"!=id&&null!=id){
            gotoUpdate(id);
        }
		$(".tabData").on("mousemove","li:nth-child(2)",page.liMousemove);		//初始化提示信息
		$(".tabData").on("mouseout","li:nth-child(2)",page.liMouseout);
		var sendAjax = function(url,data,fn){
			$.ajax({
				url : url,
	            type: 'post',
	         	data: data,
	            dataType:'jsonp',
	            error : function(){
	            	alert("error");
	            },
	            success : function(msg){
	            	fn(msg);
	            }
			})		
		}
		var treeLoad = {
			httpUrl : httpUrls.treeUrl,
			initTree : function(){
				$.fn.zTree.init($("#treeDemo"),settings,this.zNodes);
				this.tree = $.fn.zTree.getZTreeObj("treeDemo");
			},
			loadData : function() {
				var date = new Date().getTime();
				var me = this;
				var url = this.httpUrl;
                $.getJSON(url,function(json){
					me.zNodes = json;
					var str = "<option value='1'>"+GetValueByKey("default.classification")+"</option>";
					json.map(function(x){
						if(x.isParent){
							str += "<option value='"+x.dataId+"'>"+x.name+"</option>"
						}
					});
					$("#defaultClass").html(str);
					me.initTree();
					$(".left").removeClass("loading");
					$(".topTree").show();
					$(".searchInput").show();
				});
			}
		}
		page.graphClickEvent = function(graph){
			var oldHeight,oldWidth,newWidth,newHeight;
			graph.addListener(mxEvent.CLICK, function(sender, evt){
				var e = evt.getProperty('event'); // mouse event 鼠标事件
				var cell = evt.getProperty('cell'); // cell may be null   可能为null的cell
				if(cell == null || cell.isEdge()){
					graph.clearSelection();
				}
				page.showFooterLiData(cell);
				evt.consume();
			});
			graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt){
				var e = evt.getProperty('event'); // mouse event 鼠标事件
				var cell = evt.getProperty('cell'); // cell may be null   可能为null的cell					
				if(cell != undefined && cell.id != "vContain" && (cell.id).split("_")[0] != "flowCell" && cell.defaultVal != undefined){
					$(".shade").css({"display":"block"}).find(".dataView").animate({"width":"780px","height":"400px"},300);
					var cellId = cell.pId;
					var cellDefaultVal = cell.defaultVal;
					switch(cellId){
						case "begin":						
							if(cellDefaultVal != undefined){
								var allCells = graph.getChildCells(page.vContain,true);
								var openChildCell =  page.getFlowChild(allCells);
								var allCellsDefaultVal = page.getAllCellsDefaultVal(openChildCell,"isInput");
								var viewShows = new viewShow(allCellsDefaultVal,graph);
								viewShows.showZtree();
							}
							break;
						case Number(cellId):
							if(cellDefaultVal != undefined){
								var prevCell = [];
								var tempCell = cell;
								if(tempCell.getParent().id.indexOf("flowCell") >= 0){
									tempCell = tempCell.getParent();
								}
								while(tempCell.id != "begin"){
									tempCell = page.getNextCells(tempCell,true);
									if(tempCell.id.indexOf("label") >= 0){
										tempCell = tempCell.getParent().getParent();
										continue;
									}
									if(tempCell.id != "begin"){
										prevCell.push(tempCell);
									}									
								}								
								var openChildCell = page.getFlowChild(prevCell);
								var allCellsDefaultVal = page.getAllCellsDefaultVal(openChildCell,"isAllData").concat(cellDefaultVal);							
								var viewShows = new viewShow(allCellsDefaultVal,graph,openChildCell);
								viewShows.showZtree();
							}
							break;
						case "end":						
							if(cellDefaultVal != undefined){
								var allCells = graph.getChildCells(page.vContain,true);
								var openChildCell =  page.getFlowChild(allCells);
								var allCellsDefaultVal = page.getAllCellsDefaultVal(openChildCell,"isOutput");
								var viewShows = new viewShow(allCellsDefaultVal,graph);
								viewShows.showZtree();
							}
							break;
                        default:
                            if(cellDefaultVal != undefined){
                            var prevCell = [];
                            var tempCell = cell;
                            if(tempCell.getParent().id.indexOf("flowCell") >= 0){
                                tempCell = tempCell.getParent();
                            }
                            while(tempCell.id != "begin"){
                                tempCell = page.getNextCells(tempCell,true);
                                if(tempCell.id.indexOf("label") >= 0){
                                    tempCell = tempCell.getParent().getParent();
                                    continue;
                                }
                                if(tempCell.id != "begin"){
                                    prevCell.push(tempCell);
                                }
                            }
                            var openChildCell = page.getFlowChild(prevCell);
                            var allCellsDefaultVal = page.getAllCellsDefaultVal(openChildCell,"isAllData").concat(cellDefaultVal);
                            var viewShows = new viewShow(allCellsDefaultVal,graph,openChildCell);
                            viewShows.showZtree();
                        };
					}
					evt.consume();
					$(".dataTit").on("click",".dataClose",function(){
						viewShows.destoryEvent();
					})
				}
			});	
			

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
	     			var cell = me.getCell();
	     			if(cell != null){
	     				var cellStateActive = "";   
	     				//offMoveCanResize---不可移动可扩大	
	     				//offMoveOffResize---不可移动不可扩大  
	     				//canMoveOffResize---可移动不可扩大
	     				if(cell.id.indexOf("vContain") >= 0){
							page.istrue = true;
							page.defaultParent = cell;
							cellStateActive = "offMoveCanResize";		
						}
						else{
							cellStateActive = "offMoveOffResize";
						}
						if(cell.id.indexOf("flowCell") < 0 && cell.getParent().id.indexOf("vContain") >= 0){
							page.defaultParent = cell.getParent();
							cellStateActive = "offMoveOffResize";
						}
						if(cell.id.indexOf("flowCell") >= 0){
							page.istrue = false;
							page.defaultParent = cell;
							cellStateActive = "offMoveCanResize";
						}
						if(cell.getParent().id.indexOf("flowCell") >= 0 && cell.id.indexOf("sequence") < 0){						
							page.defaultParent = cell.getParent();
							cellStateActive = "canMoveOffResize";
						}
						if(cell.id.indexOf("sequence") >= 0){
							cellStateActive = "canMoveOffResize";
							page.defaultParent = cell;
						}
						if(cell.id.indexOf("label") >= 0 || (cell.getParent().id.indexOf("sequence") >= 0 &&
							cell.id.indexOf("flow") < 0)){
							cellStateActive = "offMoveOffResize";
						}
						var cellState = new cellStates();
						cellState.change(cellStateActive).goes();
	     			}
	   			},
	   			mouseUp: function(sender,me) {	
					// var cell = me.getCell();
	   			}
	  		});	 
	  		/*graph.addListener(mxEvent.CELLS_RESIZED,function(sender,evt){
	  		}) 		*/
			graph.addListener(mxEvent.RESIZE_CELLS, function(sender, evt){
				var cell = evt.getProperty('cells')[0];
				if(cell != null){
					newHeight = cell.geometry.height;
					newWidth = cell.geometry.width;
					if(oldWidth != newWidth){
						cellChangeW(cell,newWidth - oldWidth); 
						page.observerResizeWidth(cell);
					}
					if(oldHeight != newHeight){
						var dy = newHeight - oldHeight;
						cellChangeH(cell,dy);
					}
				}			
				graph.clearSelection();			
			});		
		}
		page.addMenu = function(){
			var graph = page.graph;
			graph.panningHandler.factoryMethod = function(menu, cell, evt){
				if(cell && cell.id != "begin" && cell.id != "end" && cell.id != "vContain"){
					menu.addItem(GetValueByKey("delete"),null,function(){
						if(graph.isEnabled()){
							if(cell.id.indexOf("label") >= 0){
								cell = cell.getParent();
							}
							graph.removeCells([cell]);
						}
					})
					menu.addItem(GetValueByKey("modify.the.name.of.the.service"),null,function(){
						var changeCell = cell;
						var cells = page.getCellsFromBegin();
						if(cell.id.indexOf("flowCell") >= 0 || cell.id.indexOf("sequence") >= 0){
							changeCell = cell.children[0];
						}
						var oldValue = changeCell.getValue();
						var cellValue = prompt(GetValueByKey("Please.enter.the.value.you.want.to.modify."),oldValue);
						if(cellValue.length > 20){
							$(document).alert(GetValueByKey("The.length.is.more.than.20.characters..The.first.20.characters.are.extracted.automatically."));
							cellValue = cellValue.substring(0,20);
						}			
						if(cellValue && cellValue !=  oldValue && cellValue != ""){
							if(!changeCell.pId){
								if(cells.indexOf(cellValue,"value") >= 0){
									$(document).alert(GetValueByKey("The.names.are.the.same.and.translated.automatically."));
									cellValue = cellValue.replace(/\_\S*/g,"_" + (++page.num2));
								}										
								page.changeValue(cell,graph,cellValue);
							}
							page.changeCellValue(changeCell,cellValue);
						}
					})
				}
				
			}
		}
		page.showFooterLiData = function(cell){		
			if (cell != null || cell != undefined){
				var thisId = cell.pId;
				var cellVal = cell.defaultVal;
				if(cell.id.indexOf("flow") >= 0 || cell.id.indexOf("seq") >= 0 ){
					$(".item li").eq(2).addClass("act").siblings().removeClass("act");	
					$(".para").hide();
					$(".wrapModelName").addClass("showModelName");
					$(".detail .liList").eq(2).addClass("activeBlock").siblings().removeClass("activeBlock");
					if(cell.id.indexOf("flowCell") >= 0){
						$(".modelName").val(cell.children[0].value);
						$(".typeVal").val(GetValueByKey("parallel"));
					}
					else if(cell.id.indexOf("sequence") >= 0){
						$(".modelName").val(cell.children[0].value);
						$(".typeVal").val(GetValueByKey("serial"));
					}
					else{
						$(".modelName").val(cell.value);
						$(".typeVal").val(cell.style);
					}
				}
				switch(thisId){
					case "begin":
						$(".para").show();
						$(".wrapModelName").removeClass("showModelName");
						$(".item li").eq(2).addClass("act").siblings().removeClass("act");
						$(".detail .liList").eq(2).addClass("activeBlock").siblings().removeClass("activeBlock");
						if(cellVal != undefined){
							var str = "";
							var num = 0;
							$(".typeVal").val(GetValueByKey("input"));
							cellVal.map(function(x){
								if(x.type != "OutputParameter"){
									str += "<ol><li class='nth-1'>"+(++num)+"</li><li class='nth-2'>"+x.dataType+
										   "</li><li class='nth-3'>"+x.parameterDesc+"</li></ol>";
								}
							})
							$(".para .returnData").html(str);
						}
					break;
					case "end":
						$(".para").show();
						$(".wrapModelName").removeClass("showModelName");
						$(".item li").eq(2).addClass("act").siblings().removeClass("act");				
						$(".detail .liList").eq(2).addClass("activeBlock").siblings().removeClass("activeBlock");
						if(cellVal != undefined){
							var str = "";
							var num = 0;
							$(".typeVal").val(GetValueByKey("output"));
							cellVal.map(function(x){
								str += "<ol><li class='nth-1'>"+(++num)+"</li><li class='nth-2'>"+x.dataType+
									   "</li><li class='nth-3'>"+x.parameterDesc+"</li></ol>";
							})
							$(".para .returnData").html(str);
						}
					break;
                    default:
                        $(".item li").eq(1).addClass("act").siblings().removeClass("act");
                        $(".detail .liList").eq(1).addClass("activeBlock").siblings().removeClass("activeBlock");
                        $(".serverInlist .returnData").html("");
                        $(".serverOutlist .returnData").html("");
                        if(cell.isPL == "false"){
                            cell.isPL = false;
                        }
                        $("#isPL").prop("checked",cell.isPL);
                        $(".serviceName").val(cell.value);
                        $(".serverDesc").val(cell.dataDesc.serverDesc);
                        // getData(thisId,".serverInlist",cell);
                        showDefaultValOnClickCelll(cell.defaultVal,".serverInlist");

				}
			} 	
		}
		page.createBtn = function(){
			var funct = function(graph, evt, cell, x, y){
				if (graph.canImportCell(cell)){
					var pt = page.graph.getPointForEvent(evt);
					var randomNum = Math.ceil(Math.random()*100);
					var child = {};
					child.x = pt.x;
					child.y = pt.y;
					child.id = "flowCell" + "_" + randomNum;
					child.width = 160;
					child.height = 160;
					page.addChild(child);
				}
			}

			var funct2 = function(graph, evt, cell, x, y){
				if (graph.canImportCell(cell)){
					var pt = page.graph.getPointForEvent(evt);
					var randomNum = Math.ceil(Math.random()*100);				
					var child = {};
					child.x = pt.x;
					child.y = pt.y;
					child.id = "sequence" + "_" + randomNum;
					child.width = 100;
					child.height = 120;
					if(page.defaultParent.id.indexOf("flowCell") >= 0){
						page.addChild(child);
					}
				}
			}

			var top = document.getElementById("top2");
			var divFlow = document.createElement('div');
			divFlow.className = "flow common_show";

			var divSequence = document.createElement('div');
			divSequence.className = "sequence common_show";

			var divDescShow = document.createElement('div');
			divDescShow.className = "desc_show";
			divDescShow.innerHTML = GetValueByKey("parallel");

			divFlow.appendChild(divDescShow);
			top.appendChild(divFlow);

			var divDescShow2 = document.createElement('div');
			divDescShow2.className = "desc_show";
			divDescShow2.innerHTML = GetValueByKey("serial");
			divSequence.appendChild(divDescShow2);
			top.appendChild(divSequence);

			var dragImage = divFlow.cloneNode(true);
			var ds = mxUtils.makeDraggable(divFlow, page.graph, funct, dragImage);

			var dragImage2 = divSequence.cloneNode(true);
			var ds2 = mxUtils.makeDraggable(divSequence, page.graph, funct2, dragImage2);

			/*添加事件*/
			$(".inter_open").click(function(){
				var date = new Date().getTime();
				
				//传递用户id给接口,实现数据权限功能
				var url2 = location.search; //获取url中含"?"符后的字串
				var theRequest = new Object();
				if (url2.indexOf("?") != -1) {
					var str = url2.substr(1);
					strs = str.split("&");
					for(var i = 0; i < strs.length; i ++) {
						theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
					}
				}
				
				var url = httpUrls.getAllProcessList + "?date=" + date+"&userId="+theRequest['userId'];
				// url = "http://192.168.30.203:8080/PIESoaServer/process/getAllProcessList";
				$(".shade").show();
				$(".flowList").show();
				sendAjax(url,"",function(msg){
					var dataListHtml = templateFlowList(msg);					
					$(".processList").html(dataListHtml);
					if(msg.length*30 > 300){
						$(".flowList-tab").width("97%");
					}
				})
			})

			$(".inter_save").click(function(){
				var url = httpUrls.saveProcessFromDesigner;
				saveProcessAndDeploy(url);	
			})

			$(".deploy_process").click(function(){
				var url = httpUrls.deployUrl;
				saveProcessAndDeploy(url);				
			})
			$(".pNameVal").change(function(){
				var val = $(this).val();
				var pattern = /^[0-9a-zA-Z\u4e00-\u9fa5]+$/;
				if(!pattern.test(val)){
					$(".error_prompt").html(GetValueByKey("Please.use.letters,.numbers.or.Chinese.for.the.name.of.the.file."));
				}
				else{
					$(".error_prompt").html("");
				}
				if(val == ""){
					$(".error_prompt").html(GetValueByKey("Please.enter.the.name.of.the.file."));
				}
			})
			$(".local_save").click(function(){
				var date = new Date().getTime();
				var url = httpUrls.saveFileUrl + "?date=" + date;
				var pNameVal = $(".pNameVal").val() || "xml";
				var fileName = prompt(GetValueByKey("Please.enter.the.name.of.the.saved.file."),pNameVal);
				if(fileName){ 
					var cells = page.getCellsFromBegin();
					var a = new encodeXml(cells);
					var xml = a.init();	
					var xml2 = mxUtils.getPrettyXml(xml);
					$("#hidXml").val(xml2);
					$("#hidName").val(fileName);
					var txtForm = document.getElementById("txtForm");
					txtForm.method = "post";
					txtForm.action = url;
					txtForm.submit();
				}
			})

		}
		page.createBtn();
		page.addMenu();
		page.graphClickEvent(page.graph);
		treeLoad.loadData();
		$(".modelName").on("keyup",function(e){
			e = e || event;
			if(e.keyCode == "13"){
				setCellVal();
			}
		}).on("blur",function(){
			setCellVal();
		})
		$(".processList").on("dblclick","ol",function(){
			var dataId = $(this).attr("data-id");
			var categoryId = $(this).attr("data-categoryId");
			var describe = $(this).find(".bR-no").html();
			var url = httpUrls.getProcessXmlById + "?date=" + httpDateAndHeader.date;
			// url = "http://192.168.30.203:8080/PIESoaServer/process/getProcessXmlById";
			var sendData = {processId:dataId};
			sendAjax(url,sendData,function(msg){
				$(".flowList").hide();
				$(".shade").hide();
				var xml = msg.xml;
            	var doc = mxUtils.parseXml(xml);
            	page.graph = null;
				$("#graphContainer").html("");
				page.init();
            	page.graphClickEvent(page.graph);
            	$(".pNameVal").val($(doc).find("Process").attr("name"));
				var decode = new decodeXML(doc);
				decode.init();
				$("#defaultClass").val(categoryId);
				$(".descDeatil").val(describe);
				$(".processId").val(dataId);
			})
			
		})
		function setCellVal(){				//设置并行串行值
			var graph = page.graph;
			var cell = graph.getSelectionCell();
			var changeCell = cell;
			graph.clearSelection();

			var value = $(".modelName").val();
			if(value == ""){
				value = $(".typeVal").val();
				$(".modelName").val(value);
			}
			if(!cell || !isNaN(cell.pId)){
				return;
			}
			if(cell.id != "begin" && cell.id != "end"){
				if(cell.id.indexOf("flowCell") >= 0 || cell.id.indexOf("sequence") >= 0){
					changeCell = cell.children[0];
				}
			}
			if(value.length > 20){
				$(document).alert(GetValueByKey("The.length.is.more.than.20.characters..The.first.20.characters.are.extracted.automatically."));
				value = value.subString(0,20);
			}	
			changeCell.value = value;
			graph.refresh(changeCell);
			graph.cellSizeUpdated(changeCell,false);
			page.moveLabel(graph,changeCell);
			if(cell.id.indexOf("flowCell") >= 0){
				var parentCell = cell.getParent();
				page.centerCell(graph,[cell],parentCell.geometry.width/2);
			}
			graph.setSelectionCell(cell);
		}
		function onDrop(event,treeId,treeNodes){	
			var pt = page.graph.getPointForEvent(event);
			var child = {};
			child.x = pt.x;
			child.y = pt.y;
			child.width = 160;
			child.height = 30;
			if(treeNodes[0].isParent){
				mxUtils.error(GetValueByKey("Please.don't.drag.the.root.node."),280,true);
				return;
			}
			if(!treeNodes[0].isParent && onDragMove(event) && treeNodes != null){
				var date = new Date().getTime().toString().substr(-4);
				child.name = treeNodes[0].name + "_" + (++page.num2);
				child.id = treeNodes[0].id + "a" + date;
				var cell = page.addChild(child);
				var beginCell = page.getBeginCell();
				getData(treeNodes[0].id,".serverInlist",cell,beginCell,true);
				$(".detail li").eq(1).addClass("activeBlock").siblings().removeClass("activeBlock");
				$(".item li").eq(1).addClass("act").siblings().removeClass("act");
			}
		}
		function zTreeOnClick(event, treeId, treeNode) {
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			treeObj.reAsyncChildNodes(treeNode, "refresh");
		}
		function getContentPosition(){
			if(page.vContain != null){
				var pt = {};
				pt.y = page.vContain.geometry.y;
				pt.x = page.vContain.geometry.x;
				pt.height = page.vContain.geometry.height;
				pt.width = page.vContain.geometry.width;
				pt.leftWidth = document.getElementById("left2").offsetWidth;
				pt.topHeight = document.getElementById("top2").offsetHeight;
				return pt;
			}
		}
		function onDragMove(event){ 
			var pt = getContentPosition();
			var xTrue = pt.leftWidth + pt.x;
			var yTrue = pt.topHeight + pt.y;
			//判断位置返回true or false
			if(event.pageY >= yTrue && event.pageY <= yTrue + pt.height && event.clientX >= xTrue 
				&& event.clientX <= xTrue + pt.width){
				// page.addMouseListener(true);
				return true;
			}
			else{
				return false;
			}
		}
		var showDefaultValOnClickCelll = function(cellData,dom){
			var str = "",
				str2 = "",
				num = 0,
				num2 = 0;
			cellData && cellData.map(function(x){
				if(x.type == "InputParameter"){						
					str += "<ol><li class='nth-1'>"+(++num)+"</li><li class='nth-2'>"+x.dataType+
					   	   "</li><li class='nth-3'>"+x.parameterDesc+"</li></ol>";
				}
				else{
					str2 += "<ol><li class='nth-1'>"+(++num2)+"</li><li class='nth-2'>"+x.dataType+
					   	   "</li><li class='nth-3'>"+x.parameterDesc+"</li></ol>";
					$(".serverOutlist").find(".returnData").html(str2);
				}
			})
			dom && $(dom).find(".returnData").html(str);
		}
		var settings = {
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
				selectedMulti : true,
				showLine : true
			},
			data:{
				simpleData : {
					enable : true
				}
			},
			callback : {
				onClick : zTreeOnClick,
				onDrop : onDrop,			
				onDragMove : onDragMove
			}
		};	
		$(".fileVal").change(function(){
			var txtForm = document.getElementById("txtForm2");
			txtForm.submit();
		})	
		var saveProcessAndDeploy = function(url){
			var graph = page.graph;
			var endCell = page.cellEnd;
//			if(endCell.defaultVal.length <= 0){
//				$(".item li").eq(0).addClass("act").siblings().removeClass("act");
//				$(".detail .liList").eq(0).addClass("activeBlock").siblings().removeClass("activeBlock");
//				mxUtils.error(GetValueByKey("Make.sure.there.is.at.least.one.output.for.the.root.node."),280,true);
//				return;
//			}
			var val = $(".pNameVal").val();
			var pattern = /^[0-9a-zA-Z\u4e00-\u9fa5]+$/;
			if(!pattern.test(val)){
				$(".error_prompt").html(GetValueByKey("Please.use.letters,.numbers.or.Chinese.for.the.name.of.the.file."));
				return;
			}
			else{
				$(".error_prompt").html("");
			}
			if(val == ""){
				$(".error_prompt").html(GetValueByKey("Please.enter.the.name.of.the.file."));
				return;
			}
			var date = new Date().getTime();			
			var associateServiceId = page.vContain.hasID;
			var categoryId = $("#defaultClass").val();
			var processCHName = $(".pNameVal").val();
			var pic = "";
			var cells = page.getCellsFromBegin();
			var encodeXmls = new encodeXml(cells);
			var xml = encodeXmls.init();
			var file = mxUtils.getPrettyXml(xml);
			var processDescribe = $(".descDeatil").val();
			var processId = $(".processId").val();
			if(processId == ""){
				//说明为新增流程
				if(processCHName != "" || processDescribe != ""){
					var xmlDoc = mxUtils.createXmlDocument();
					var root = xmlDoc.createElement('output');
					xmlDoc.appendChild(root);
					
					var xmlCanvas = new mxXmlCanvas2D(root);
					var imgExport = new mxImageExport();
					imgExport.drawState(graph.getView().getState(graph.model.root), xmlCanvas);
					
					var bounds = graph.getGraphBounds();
					var w = Math.ceil(bounds.x + bounds.width + page.vContain.geometry.x);
					var h = Math.ceil(bounds.y + bounds.height + 100);
					
					var xml2 = mxUtils.getXml(root);
					pic = encodeURIComponent(xml2);
					
					var obj = {};
					obj.associateServiceId = associateServiceId;
					obj.categoryId = categoryId;
					obj.file = file;
					obj.processCHName = processCHName;
					obj.picture = pic;
					obj.processDescribe = processDescribe;
					obj.picWidth = w;
					obj.picHeight = h;
					obj.processId = processId;
					//设置修改标识  1为新增
					obj.deployType = 1;
					//传入当前用户id,使得绑定用户和流程
					var url1 = location.search; //获取url中含"?"符后的字串
					var theRequest = new Object();
					if (url1.indexOf("?") != -1) {
						var str = url1.substr(1);
						strs = str.split("&");
						for(var i = 0; i < strs.length; i ++) {
							theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
						}
					}
					obj.userId = theRequest['userId'];
					var s = {};
					s.date = date;
					s.requestBody = JSON.stringify(obj);
					$(".shade").show().addClass("loading");
					setForm(url,s,"post");
				}
				else{
					mxUtils.error(GetValueByKey("Please.enter.the.name.or.the.description.of.theflow"),280,true)
				}
			}else{
				var dialog = jDialog.confirm("选择'保存'会修改当前流程(之前流程跑过的数据不做参考)选择'另存为'则会新增流程!",{
					handler : function(button,dialog) {
						dialog.close();
						if(processCHName != "" || processDescribe != ""){
							var xmlDoc = mxUtils.createXmlDocument();
							var root = xmlDoc.createElement('output');
							xmlDoc.appendChild(root);
							
							var xmlCanvas = new mxXmlCanvas2D(root);
							var imgExport = new mxImageExport();
							imgExport.drawState(graph.getView().getState(graph.model.root), xmlCanvas);
							
							var bounds = graph.getGraphBounds();
							var w = Math.ceil(bounds.x + bounds.width + page.vContain.geometry.x);
							var h = Math.ceil(bounds.y + bounds.height + 100);
							
							var xml2 = mxUtils.getXml(root);
							pic = encodeURIComponent(xml2);
							
							var obj = {};
							obj.associateServiceId = associateServiceId;
							obj.categoryId = categoryId;
							obj.file = file;
							obj.processCHName = processCHName;
							obj.picture = pic;
							obj.processDescribe = processDescribe;
							obj.picWidth = w;
							obj.picHeight = h;
							obj.processId = processId;
							//设置修改标识  修改为0
							obj.deployType = 0;
							//传入当前用户id,使得绑定用户和流程
							var url1 = location.search; //获取url中含"?"符后的字串
							var theRequest = new Object();
							if (url1.indexOf("?") != -1) {
								var str = url1.substr(1);
								strs = str.split("&");
								for(var i = 0; i < strs.length; i ++) {
									theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
								}
							}
							obj.userId = theRequest['userId'];
							var s = {};
							s.date = date;
							s.requestBody = JSON.stringify(obj);
							$(".shade").show().addClass("loading");
							setForm(url,s,"post");
						}
						else{
							mxUtils.error(GetValueByKey("Please.enter.the.name.or.the.description.of.theflow"),280,true)
						}
					}
				},{
					handler : function(button,dialog) {
						dialog.close();
						if(processCHName != "" || processDescribe != ""){
							var xmlDoc = mxUtils.createXmlDocument();
							var root = xmlDoc.createElement('output');
							xmlDoc.appendChild(root);
							
							var xmlCanvas = new mxXmlCanvas2D(root);
							var imgExport = new mxImageExport();
							imgExport.drawState(graph.getView().getState(graph.model.root), xmlCanvas);
							
							var bounds = graph.getGraphBounds();
							var w = Math.ceil(bounds.x + bounds.width + page.vContain.geometry.x);
							var h = Math.ceil(bounds.y + bounds.height + 100);
							
							var xml2 = mxUtils.getXml(root);
							pic = encodeURIComponent(xml2);
							
							var obj = {};
							obj.associateServiceId = associateServiceId;
							obj.categoryId = categoryId;
							obj.file = file;
							obj.processCHName = processCHName;
							obj.picture = pic;
							obj.processDescribe = processDescribe;
							obj.picWidth = w;
							obj.picHeight = h;
							obj.processId = "";
							//设置修改标识  1为新增
							obj.deployType = 1;
							//传入当前用户id,使得绑定用户和流程
							var url1 = location.search; //获取url中含"?"符后的字串
							var theRequest = new Object();
							if (url1.indexOf("?") != -1) {
								var str = url1.substr(1);
								strs = str.split("&");
								for(var i = 0; i < strs.length; i ++) {
									theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
								}
							}
							obj.userId = theRequest['userId'];
							var s = {};
							s.date = date;
							s.requestBody = JSON.stringify(obj);
							$(".shade").show().addClass("loading");
							setForm(url,s,"post");
						}
						else{
							mxUtils.error(GetValueByKey("Please.enter.the.name.or.the.description.of.theflow"),280,true)
						}
					}
				});
			}
		}
		
		var templateFlowList = function(flowList){		//流程列表模板
			var str = "";
			var length = flowList.length;
			var j = 0;
			for(var i = length-1; i >= 0; i--){
				str += "<ol class='numb" + i%2 +"' data-id='"+flowList[i].id+"' data-categoryId='"+flowList[i].categoryId+"'>" +
					       "<li class='order-num'>"+(++j)+"</li>" +
					   	   "<li>" + flowList[i].processCHName + "</li>" +
					   	   "<li>" + FormatDate(flowList[i].createTime) +"</li>" + 
					   	   "<li class='bR-no'>" + flowList[i].processDescribe + "</li>" +
			  			"</ol>";
			}
			return str;
		}
		function FormatDate(startTime) { 
			startTime = startTime || new Date();
			var date = new Date(startTime); 
			var year = date.getFullYear();
			var month = (date.getMonth() + 1).toString();
			var day = date.getDate().toString();
			var hour = date.getHours().toString();
			var minute = date.getMinutes().toString();

			month = month.length == 2 ? month : "0" + month;
			day = day.length == 2 ? day : "0" + day;
			hour = hour.length == 2 ? hour : "0" + hour;
			minute = minute.length == 2 ? minute : "0" + minute;
			var str = year + "-" + month + "-" + day + "  " + hour + ":" + minute;
			return str;
		}
		var treeDemoOperate = function(term){					//ztree搜索
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var commonFn = function(){
				var nodes = treeObj.getNodesByParam("isHidden", true);
				var node = treeObj.getSelectedNodes();
				return {
					showNode : function(){
						treeObj.showNodes(nodes);
					},
					expandNodes : function(){
						if (node.length>0) { 
							node.map(function(x){
								treeObj.cancelSelectedNode(x);
							})	
							treeObj.expandAll(false);			
						}
					}
				}				
			}
			if(term == ""){				//等于空时折叠树
				commonFn().showNode();
				commonFn().expandNodes();
			}
			else{
//				var treeNodes = treeObj.getNodes();
//				var selectedNode = treeObj.getNodesByParamFuzzy("name", term, null);
//				if(selectedNode.length > 0) {
//					commonFn().showNode();	
//					treeNodes.map(function (x){
//						var arr = [];
//						x.children.map(function a(y){
//							if(y.isParent){
//								y.children.map(function (z){
//									if(z.name.indexOf(term) < 0){
//										arr.push(z);
//									}
//								});
//							}
//							if(y.name.indexOf(term) < 0){
//								arr.push(y);
//							}
//						})
//						treeObj.hideNodes(arr);
//					})
//					
//					selectedNode.map(function(x){
//						treeObj.showNode(x.getParentNode());
//						treeObj.showNode(x);
//						treeObj.expandNode(x.getParentNode(),true);
//						treeObj.selectNode(x,true,true);
//					})
//				}
//				else{
//					$(document).alert(GetValueByKey("No.matching.result.is.searched."));
//					commonFn().expandNodes();
//				}
				
			  var allNode = treeObj.transformToArray(treeObj.getNodes());;
		        treeObj.hideNodes(allNode);
		        nodeList = treeObj.getNodesByParamFuzzy("name", term, null);
		        nodeList = treeObj.transformToArray(nodeList);
		        for(var n in nodeList){
		           findParent(treeObj,nodeList[n]);
		        }
		        treeObj.showNodes(nodeList);
		            if (value == "") {
		            treeObj.expandAll(false);
		            }
		        }
			 function findParent(treeObj,node){
			        treeObj.expandNode(node,true,false,false);
			           var pNode = node.getParentNode();
			           if(pNode != null){
			            nodeList.push(pNode);
			            findParent(treeObj,pNode);
			           }
			}			
		};
		$("#searchVal").focus(function(){
			$(this).keyup(function(){
				var searchTerm = $(this).val();		
				treeDemoOperate(searchTerm);		
			})
		}).blur(function(){
			$(this).keyup(null);
		});
		$("#btnSearch").click(function(){
			var searchTerm = $(this).val();		
			treeDemoOperate(searchTerm);
		});
		$("#isPL").change(function(){
			var isPL = $(this).prop("checked");
			var graph = page.graph,
				cell = graph.getSelectionCell();
			if(cell){
				cell.isPL = isPL;
			}
		})
		$(".item li").click(function(){
			var thIndex = $(this).index();
			var cell = page.graph.getSelectionCell() || page.getBeginCell();
			/*if(cell.id == "vContain" || cell.id.split("_")[0] == "flowCell"){
				cell = page.getBeginCell();
			}*/
			page.showFooterLiData(cell);
			var detail = $(".detail .liList");
			detail.eq(thIndex).addClass("activeBlock").siblings().removeClass("activeBlock");
			$(".item li").eq(thIndex).addClass("act").siblings().removeClass("act");
		})
		$(".dataClose").click(function(){
			$(this).parents(".flowList").hide();
			$(".shade").hide();
		})

		//定义拖拽
		$(".dataTit").mousedown(function(ev){
			ev = ev || event;
			var oldX = ev.clientX;
			var oldY = ev.clientY;
			var $parent = $(this).parents(".moveFlag");
			var cW = $(document).width();
			var cH = $(document).height();
			var boxW = $parent.width();
			var boxH = $parent.height();
			var boxX,boxY,newX,newY;
			boxX = $parent.offset().left;
			boxY = $parent.offset().top;
			var x = oldX - boxX;
			var y = oldY - boxY;	
			$(document).on("mousemove",function(e){
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
				$parent.css({"left":newX,"top":newY}); 
				return false;
			}).mouseup(function(){
				$(document).off("mousemove");
			})
		}).mouseover(function(){
			$(this).css("cursor","move");
		})
		function setForm(url,queryobj,method){
		    var form = $('<form><input name="callback" type="hidden" value="callbackFn"></form>');  
		 	var iFrame = $('<iframe class="deploy" name="deploy" style="display:none;"></iframe>');
		    // 设置属性  
		    form.attr('action', url);
		    form.attr('class', "submitform");
		    form.attr('method', method ? method : "get");  
		    // form的target属性决定form在哪个页面提交  
		    // _self -> 当前页面 _blank -> 新页面
		    iFrame.appendTo("body");
		    form.attr('target', "deploy");
		    // 创建Input 
		    for(var p in queryobj){
				if(queryobj[p] != ""){
					var my_input = $('<input type="hidden" name="'+p+'" />');  
				    my_input.attr('value', queryobj[p]);
				    form.append(my_input);  
				}
			}

            // 附加到Form
		    form.appendTo("body");
		    // 提交表单  
		   	//form.submit();
            $.ajax(
                {
                    url:  url,
                    type:'post',
                    data:form.serialize(),
                    error : function(){
                        alert("error");
                    },
                    success:function(msg){
                        window.location.replace("/flow");
                    }
                }
            );
           //
		    return false;
		}
        function gotoUpdate(id) {
            $.ajax(
                {
                    url:  httpUrls.getProcessXmlById+"/"+id,
                    type:'get',
                    dataType:'JSON',
                    error : function(){
                        alert("error");
                    },
                    success:function(msg){
                        var xml = msg.xml;
                        var doc = mxUtils.parseXml(xml);
                        page.graph = null;
                        $("#graphContainer").html("");
                        page.init();
                        page.graphClickEvent(page.graph);
                        page.addMenu();
                        $(".pNameVal").val($(doc).find("Process").attr("name"));
                        var decode = new decodeXML(doc);
                        decode.init();
                         //$("#defaultClass").val(msg.categoryId);
                         $(".descDeatil").val(msg.processDescribe);
                        $(".processId").val(msg.id);
                    }
                }
            );
        }

        function getParam(paramName) {
            paramValue = "", isFound = !1;
            if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=") > 1) {
                arrSource = unescape(this.location.search).substring(1, this.location.search.length).split("&"), i = 0;
                while (i < arrSource.length && !isFound) arrSource[i].indexOf("=") > 0 && arrSource[i].split("=")[0].toLowerCase() == paramName.toLowerCase() && (paramValue = arrSource[i].split("=")[1], isFound = !0), i++
            }
            return paramValue == "" && (paramValue = null), paramValue
        }
	});
});