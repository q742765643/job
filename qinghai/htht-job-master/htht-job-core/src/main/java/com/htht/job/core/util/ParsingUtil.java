package com.htht.job.core.util;

import com.htht.job.core.biz.model.TriggerParam;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParsingUtil {
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" }) 
	public static  ResultUtil<String> argumentparsing(TriggerParam triggerParam,ResultUtil<String> result){
		LinkedHashMap map=new LinkedHashMap();

		LinkedHashMap fixmap = triggerParam.getFixedParameter();
		/**=======1.校验不能为空=====================**/
		checkIsEmpty(fixmap,result);
		if(!result.isSuccess()){
			return result;
		}
		map.put("fixmap", fixmap);
		LinkedHashMap dymap=triggerParam.getDynamicParameter();
		checkIsEmptyDy(dymap,result);
		if(!result.isSuccess()){
			return result;
		}
		map.put("dymap", dymap);
   		return  result;
	}
	@SuppressWarnings("rawtypes")
	public static ResultUtil<String> checkIsEmpty(Map fixmap,ResultUtil<String> result){
		String exePath = (String) fixmap.get("exePath");
		if(StringUtils.isEmpty(exePath)){
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_104_ERROR.getValue());
			return result;
		}
		/*String scriptFile = (String) fixmap.get("scriptFile");
		if(StringUtils.isEmpty(scriptFile)){
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_105_ERROR.getValue());
			return result;
		}*/
	
		return result;	
	}
	@SuppressWarnings("rawtypes")
	public static ResultUtil<String> checkIsEmptyDy(Map fixmap,ResultUtil<String> result){
		String outputlog = (String) fixmap.get("outputlog");
		if(StringUtils.isEmpty(outputlog)){
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_102_ERROR.getValue());
			return result;
		}	
		String outputxml = (String) fixmap.get("outputxml");
		if(StringUtils.isEmpty(outputxml)){
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_106_ERROR.getValue());
			return result;
		}
		return result;	
	}
	@SuppressWarnings("rawtypes")
	public static ResultUtil<String> checkIsEmptyProduct(Map fixmap,ResultUtil<String> result){
		String outputlog = (String) fixmap.get("outputlog");
		if(StringUtils.isEmpty(fixmap.get("outputlog"))){
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_102_ERROR.getValue());
			return result;
		}	
		String outputxml = (String) fixmap.get("outputxml");
		if(StringUtils.isEmpty(outputxml)){
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_106_ERROR.getValue());
			return result;
		}
		return result;	
	}

}
