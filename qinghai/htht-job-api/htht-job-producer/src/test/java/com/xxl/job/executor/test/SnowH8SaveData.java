package com.xxl.job.executor.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.htht.job.executor.Application;
import com.htht.job.executor.model.productfileinfo.ProductFileInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SnowH8SaveData {

	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductFileInfoService productFileInfoService;
	
	/**
	 * 补录H8深雪，只录入供H8积雪日数生产的'QHS'的tif文件
	 * 缺少期次：2019-10-01 至 2019-11-17
	 * 以及生产服务器遗漏的 2019-11-20、2019-11-21、2019-11-22、2019-11-23、2019-12-07、2019-12-11、2019-12-14、2019-12-22
	 */
	
	@Test
	public void test(){
		try{
			String strDateStr = "2019-12-11、2019-12-14、2019-12-22";
			for(String s:strDateStr.split("、")){
				String start = s;
				String end = s;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date startD = sdf.parse(start);
				Date endD = sdf.parse(end);
				Calendar c = Calendar.getInstance();
				String filePath = "Y:/data/YG/SNOW_H8_DEPTH/SnowDepth_H8/MYmonth/MYdate1400_COOH/QHS/SNW_DEPTH_RCUR_MYdate1400_COOH_H8_AHI_QHS.tif";
				while(!startD.after(endD)){
					c.setTime(startD);
					String yyyy = String.format("%tY", startD);
					String MM = String .format("%tm", startD);
					String dd = String .format("%td", startD);
					String today = yyyy+MM+dd;
					String issue = today+"1400";
					ProductInfo info = new ProductInfo();
					
					info.setCreateTime(startD);
					info.setIssue(issue);
					info.setMark("SnowDepth_H8");
					info.setRegionId("QHS");
					ProductInfo saveInfo = productInfoService.save(info);
					ProductFileInfo file = new ProductFileInfo();
					file.setCreateTime(startD);
					file.setIssue(issue);
					file.setFileType("tif");
					String fPath = filePath.replace("MYdate", today).replace("MYmonth", yyyy+MM);
					file.setFilePath(fPath);
					file.setProductInfoId(saveInfo.getId());
					file.setRegion("QHS");
					productFileInfoService.save(file);
					c.add(Calendar.DAY_OF_YEAR, 1);
					startD = c.getTime();
				}
			
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
