package com.htht.job.executor.hander.resolvehandler;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.util.ObjectTranscoder;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
/**
 * @date 2018年6月22日10:25:49
 * @author yss
 * 
 *
 */
@Service
@JobHandler(value="ResolvePieprjHandler")
public class ResolvePieprjHandler extends IJobHandler {

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		LinkedHashMap dymap = triggerParam.getDynamicParameter();
		String pieprjPath = (String) dymap.get("工程文件路径");
		if(null!=pieprjPath&&!"".equals(pieprjPath)) {
			try {
				Map<String, Object> map = new HashMap<>();
				List<String> mssPanList=new ArrayList<String>();
				File file = new File(pieprjPath);
				SAXReader reader = new SAXReader();
				// 读取xml文件到Document中
				Document doc = reader.read(file);
				// 获取xml文件的根节点
				Element rootElement = doc.getRootElement();
				// 取得某节点的单个子节点
				Element elementSAT = rootElement.element("SAT_PAIRS");
				String PAIR_NUM = elementSAT.attribute("PAIR_NUM").getText();
				String PAN_NUM = elementSAT.attribute("PAN_NUM").getText();
				String MSS_NUM = elementSAT.attribute("MSS_NUM").getText();
				//System.out.println(Integer.parseInt(MSS_NUM));
				String PAN_INDEX1 = null;
				String MSS_INDEX1 = null;
				if(Integer.parseInt(PAN_NUM)>0) {
					String PAN_INDEX = elementSAT.attribute("PAN_INDEX").getText();
					PAN_INDEX1 = PAN_INDEX.replace(",", "#HT#");
					String PAN_INDEX2=PAN_INDEX1.replaceAll("\\d", "0");
					mssPanList.add(PAN_INDEX1);
					mssPanList.add(PAN_INDEX2);
				}
				if(Integer.parseInt(MSS_NUM)>0) {
					String MSS_INDEX = elementSAT.attribute("MSS_INDEX").getText();
					MSS_INDEX1 = MSS_INDEX.replace(",", "#HT#");
					String MSS_INDEX2 = MSS_INDEX1.replaceAll("\\d", "1");
					mssPanList.add(MSS_INDEX1);
					mssPanList.add(MSS_INDEX2);
				}
				/*if(Integer.parseInt(PAN_NUM)>0&&Integer.parseInt(MSS_NUM)>0) {
					String pan_mss=PAN_INDEX1+"#HT#"+MSS_INDEX1;
					String nPanMss=PAN_INDEX1.replaceAll("\\d", "0")+"#HT#"+MSS_INDEX1.replaceAll("\\d", "1");
					mssPanList.add(pan_mss);
					mssPanList.add(nPanMss);
				}*/
				
				
				Element elementREF = rootElement.element("REF_IMG");
				String REF_NUM = elementREF.attribute("REF_NUM").getText();
				
				
				Element elementDEM = rootElement.element("DEM_IMG");
				String DEM_NUM = elementDEM.attribute("DEM_NUM").getText();
				int demNum = Integer.parseInt(DEM_NUM);
				String demNums1  =null;
				if(demNum>1) {
					String demNums="";
					for(int i=0;i<demNum;i++) {
						demNums=demNums+i+"#HT#";
					}
					demNums1 = demNums.substring(0,demNums.lastIndexOf("#HT#"));
					
					System.out.println(demNums.substring(0,demNums.lastIndexOf("#HT#")));
				}
				map.put("demNums1", demNums1);
				
				
				map.put("PAIR_NUM", PAIR_NUM);
				map.put("REF_NUM", REF_NUM);
				map.put("DEM_NUM", DEM_NUM);
				map.put("PAN_NUM", PAN_NUM);
				map.put("MSS_NUM", MSS_NUM);
				map.put("mssPanList", mssPanList);
				//String string = map.toString();
				//System.out.println(string);

			} catch (Exception e) {
				e.printStackTrace();
				return ReturnT.FAIL;
			}
			
			return ReturnT.SUCCESS;
		}
		

		return ReturnT.FAIL;
	}
	
	public static void main(String[] args) throws IOException {
		List<String> aa=new ArrayList<String>();
		aa.add("111");
		aa.add("222");
		byte[] bb= ObjectTranscoder.serialize(aa);
		List<String> aaa=new ArrayList<String>();
        aaa=ObjectTranscoder.deserialize(bb);
		System.out.println(aaa);


	}

}
