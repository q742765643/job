package com.htht.job.executor.hander.predatahandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.htht.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.util.ScriptUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.hander.dataarchiving.util.UUIDTool;
import com.htht.job.executor.model.dms.module.MetaInfo;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.service.dms.MetaInfoService;
import com.htht.job.executor.util.xmlUtil.InputXmlUtil;

@JobHandler(value = "h8process")
@Service
public class H8preproccess extends IJobHandler {

	@Autowired
	private MetaInfoService metaInfoService;
//	@Autowired
//	private MetaImgService metaImgService;
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private BaseDaoUtil baseDaoUtil;

	@Override
	public ReturnT<String> execute(TriggerParam arg0) throws Exception {
		// TODO Auto-generated method stub
		/** =======2.创建日志文件=========== **/
		String outputlogpath = arg0.getLogFileName();
		XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);

		System.out.println("H8预处理");
		Map<String, Object> map = new HashMap<String, Object>();
		// executorParams='201802010400#D:\predata\H8\20180201'

		String executorParam = arg0.getExecutorParams();
		String[] execArray = executorParam.split("#");

		String fileTime = execArray[0];
		@SuppressWarnings("unchecked")
		Map<String, String> fixedParameter = arg0.getFixedParameter();

		String fileNameForm = (String) fixedParameter.get("fileNameForm");
		String outputPreDataPath = (String) fixedParameter
				.get("outputPreDataPath");
		String outputPreXmlPath = (String) fixedParameter
				.get("outputPreXmlPath");
		String tempPath = (String) fixedParameter.get("tempPath");
		String band = (String) fixedParameter.get("band");
		String stripe = (String) fixedParameter.get("stripe");
		String latlon = (String) fixedParameter.get("latlon");
		String type = (String) fixedParameter.get("type");
		String pixelRes = (String) fixedParameter.get("pixelRes");
		String inputXMLPath = (String) fixedParameter.get("inputXMLPath");
		String pythonPath = (String) fixedParameter.get("pythonPath");
		String satelliteSenior = (String) fixedParameter.get("satelliteSenior");

		outputPreXmlPath += fileTime + ".xml";

		map.put("fileTime", fileTime);
		map.put("fileNameForm", fileNameForm);
		map.put("inputDataPath", execArray[1]);
		map.put("outputPreDataPath", outputPreDataPath);
		map.put("outputPreXmlPath", outputPreXmlPath);// 输出文件路径
		map.put("tempPath", tempPath);
		map.put("band", band);
		map.put("stripe", stripe);
		map.put("latlon", latlon);
		map.put("type", type);
		map.put("pixelRes", pixelRes);

		File fileXml = new File(inputXMLPath + fileTime + "00.xml");

		if (!fileXml.isFile()) {
			fileXml.createNewFile();
		}

		InputXmlUtil.toInputXml(fileXml.toString(), map);

		String cmd = pythonPath + " " + fileXml;

		System.err.println("H8预处理" + fileTime + "执行CMD参数：" + cmd);
		 ScriptUtil.execCmd(cmd,outputlogpath);

		File outputXmlFile = new File(outputPreXmlPath);
		if (!outputXmlFile.exists()) {
			return ReturnT.FAIL;
		}

		SAXReader reader = new SAXReader();
		// 读取文件 转换成Document
		Document document = null;
		try {
			document = reader.read(outputXmlFile);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 获取根节点元素对象
		String hdfFilePath = "";
		try {
			Element root = document.getRootElement();
//			Element logElement = root.element("log");
			Element outFileElement = root.element("outFiles");
//			successInfo = logElement.element("loginfo").getText();
			hdfFilePath = outFileElement.element("HDF").getText();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		if (!"success".equals(successInfo)) {
//			return ReturnT.FAIL;
//		}
		File hdfFile = new File(hdfFilePath);
		if (!hdfFile.exists()) {
			return ReturnT.FAIL;
		}
		String ss[] = satelliteSenior.split("_");
		String satellite = ss[0];
		String sensor = ss[1];
		Date fileDate = DateUtil.getDate(fileTime, "yyyyMMddHHmm");
		saveProject2DB(hdfFile, satellite, sensor, fileDate);

		return ReturnT.SUCCESS;
	}

	private void saveProject2DB(File file, String satellite, String sensor,
			Date fileDate) {
		// 获取需要入库的主表名称

		String uuid = UUIDTool.getUUID();
		// 物理信息
		MetaInfo metaInfo = new MetaInfo();
		metaInfo.setId(uuid);
		metaInfo.setCreateTime(new Date());
		metaInfo.setF_dataid(uuid);
		metaInfo.setF_dataname(fileUtil.getFileNameWithoutSuffix(file));
		// metaInfo.setF_catalogcode(archiveRules.getCatalogcode());
		metaInfo.setF_importdate(new Date());
		metaInfo.setF_datasize(0L);
		metaInfo.setF_dataunit("B");
		metaInfo.setF_dataextname(fileUtil.getFileNameSuffix(file));
		metaInfo.setF_isfile(0);
		metaInfo.setF_location(file.getAbsolutePath());
		metaInfo.setF_flag(0);
		metaInfo.setF_recycleflag(0);
		metaInfo.setF_viewdatapath("");

		metaInfo.setF_datasourcename(file.getName());
		metaInfoService.save(metaInfo);
		// 影像信息
//		MetaImg metaImg = new MetaImg();
//		metaImg.setFDataid(uuid);
//		metaImg.setFLevel("L2");
//		metaImg.setFSatelliteid(satellite);
//		metaImg.setFSensorid(sensor);
//		metaImg.setFReceivetime(fileDate);
//		metaImg.setFProducetime(fileDate);
//		metaImg.setFCentertime(fileDate);
//		metaImg.setFResolutionx(new BigDecimal(1000));
//		metaImgService.save(metaImg);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(fileDate);
		String sql = "insert into htht_dms_meta_img";
		StringBuffer colSql = new StringBuffer("(f_dataid");
		StringBuffer valSql = new StringBuffer("('" + uuid + "'");
	
		colSql.append(",f_level");
		valSql.append(",'L2'");
		colSql.append(",f_satelliteid");
		valSql.append(",'"+satellite+"'");
		colSql.append(",f_sensorid");
		valSql.append(",'"+sensor+"'");
		colSql.append(",f_receivetime");
		valSql.append(",'"+dateStr+"'");
		colSql.append(",f_producetime");
		valSql.append(",'"+dateStr+"'");
		colSql.append(",f_centertime");
		valSql.append(",'"+dateStr+"'");
		colSql.append(",f_resolutionx");
		valSql.append(",'1000'");
		
		colSql.append(")");
		valSql.append(")");
		Object[] obj = {};
		
		System.out.println("=========>"+sql + colSql.toString() + " values " + valSql.toString());
		baseDaoUtil.executeSql(sql + colSql.toString() + " values " + valSql.toString(), obj);
		
	}

}
