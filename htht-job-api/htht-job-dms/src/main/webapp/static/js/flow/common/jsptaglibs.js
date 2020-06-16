// 打开properties文件，根据key找到相关的value值

function GetValueByKey(key){
	loadProperties();
	var value = $.i18n.prop(key);
	return value;
};

function loadProperties(){
	jQuery.i18n.properties({//加载资浏览器语言对应的资源文件
		name:'jsp_en_US', //资源文件名称
		path:'resources/js/', //资源文件路径resources/js/
		//path:'WEB-INF/classes/', //资源文件路径resources/js/
		mode:'map', //用Map的方式使用资源文件中的值
		callback: function() {//加载成功后设置显示内容
			//用户名
//			$('#label_username').html($.i18n.prop('string_username'));
		}
	});
}