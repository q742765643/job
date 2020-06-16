package com.htht.job.executor.plugin.job;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;

@JobHandler("saveProdcutHandler")
@Service
public class SaveProdcutHandler extends IJobHandler {
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	protected ProductUtil productUtil;
	
	@Autowired
	protected ProductFileInfoService productFileInfoService;
	
	@Autowired
	protected DictCodeService dictCodeService;
	
	@SuppressWarnings("unused")
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		
		ResultUtil<String> result=new ResultUtil<String>();
		LinkedHashMap fixedParameter = triggerParam.getFixedParameter();
		
		String outputXml = (String) fixedParameter.get("outputXml");
		String inputXml = outputXml.replace("outputXml", "inputXml");
		String issue = (String) fixedParameter.get("issue");
		String cycle = (String) fixedParameter.get("cycle");
		String modelIdentify = (String) fixedParameter.get("modelIdentify");
		
		File fileXml = new File(outputXml);
		if (!fileXml.exists() && fileXml.isFile()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, fileXml + " is't exist");
		}

		XmlUtils XmlUtils = new XmlUtils();
		//读取xml，把xml转换成map对象
		Map<String,List<Element>> map = XmlUtils.outputFilesXmlToMap(outputXml);
		List<String> regionIdList = XmlUtils.getXmlAttrVal( map, "region", "identify");
		String mosaicFile = "";
		if(map.containsKey("mosaicFile")){
			List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(map,
					"mosaicFile");
			if (mosaicFiles.size() > 0) {
				mosaicFile = mosaicFiles.get(0);
			}
		}
		ProductInfo productInfo = null;

		if (regionIdList != null && regionIdList.size() > 0) {
			DictCode productPath = dictCodeService.findOneself("productPath");
			for (String regionId : regionIdList) {
				// 查重 并删除
				List<ProductInfo> pis = productInfoService.findProductExits(
						triggerParam.getProductId(), issue, cycle,
						modelIdentify, null, regionId);
				for (ProductInfo pi : pis) {
					productInfoService.deleteProductInfo(pi.getId());
					// 查重
					productFileInfoService.deleteByproductInfoId(pi.getId());
				}
				// 产品信息及文件信息入库
				productInfo = productUtil.saveProductInfo(
						triggerParam.getProductId(), regionId, issue, cycle,
						mosaicFile, modelIdentify, inputXml);


				List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(map,
						"region", "identify", regionId);
				for (String file : lFiles) {
					productUtil.saveProductInfoFile(productInfo.getId(),
							file.replace("\\", "/"), productPath.getDictCode(),
							regionId, issue, cycle);
				}

			}
			// 产品结果信息入库
			List<Element> xmllists = XmlUtils.getTablenameElements(outputXml, "table");
		}
			
        return ReturnT.SUCCESS;
		
	}
}
