// 打开properties文件，根据key找到相关的value值
var bLoad= false;
function getRootPath() {
    var pathName = window.location.pathname.substring(1);
    var webName = pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'));
    //return window.location.protocol + '//' + window.location.host + '/'+ webName + '/';
    return "static/js/flow";
    }  

function GetValueByKey(key){
	if(!bLoad)
	{
		loadProperties();
		bLoad=true;
	}
	var value = $.i18n.prop(key);
	return value;
};

function loadProperties(){
	var JsSrc = (navigator.language || navigator.browserLanguage).toLowerCase();
	if(JsSrc.indexOf('zh')>=0)
	{
		JsSrc = 'js_zh_CN';
	}
	else if(JsSrc.indexOf('en')>=0)
	{
	    JsSrc = 'js_en_US';
	}
	else
	{
		JsSrc = 'js_zh_CN'; 	
	}
	
	var i18npath = "static/js/flow/js/resources/js/"
	jQuery.i18n.properties({//加载资浏览器语言对应的资源文件
		name:JsSrc, //资源文件名称
		path: i18npath,                      //'resources/js/', //资源文件路径
		mode:'map', //用Map的方式使用资源文件中的值
		callback: function() {//加载成功后设置显示内容
			//用户名
//			$('#label_username').html($.i18n.prop('string_username'));
		}
	});
};