package org.htht.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class XmlMakeUtil
{

	public static boolean makeXml(Map<String, Object> argMap, String xmlFilePath) throws Exception
	{

		XmlMakeUtil util = new XmlMakeUtil();
		util.doMakeXml(argMap, xmlFilePath);
		return true;

	}

	public static boolean deleteXml(final String xmlFilePath) throws Exception
	{

		// new Thread(new Runnable(){
		// public void run() {
		// try {
		// Thread.sleep(10*1000);
		// } catch (InterruptedException e) {
		// System.out.print("try sleep 10s faild.");
		// e.printStackTrace();
		// }
		// File xmlFile = new File(xmlFilePath);
		// xmlFile.delete();
		// }
		// }).run();
		return true;
	}

	/**
	 * 生成exe执行需要的xml
	 * 
	 * @param argMap
	 * @param xmlFilePath
	 * @throws Exception
	 */
	private void doMakeXml(Map<String, Object> argMap, String xmlFilePath) throws Exception
	{	
		String newFileContent = "";
		if (xmlFilePath.contains("FY4A-")) {
			newFileContent = getXmlContentFY4A(argMap);
		} else {
			newFileContent = getXmlContent(argMap);
		}
//		String newFileContent = getXmlContent(argMap);

		File xmlFile = new File(xmlFilePath);
		xmlFile.deleteOnExit();
		if (!xmlFile.getParentFile().exists())
		{
			xmlFile.getParentFile().mkdirs();
		}
		xmlFile.createNewFile();

		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(xmlFile, false), "UTF-8");
		osw.write(newFileContent);
		osw.close();
	}

	/**
	 * 根据参数Map生成xml内容
	 * 
	 * @param argMap
	 * @return
	 */
	private String getXmlContent(Map<String, Object> argMap) throws Exception
	{

		String newFileContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r" + "<xml identify=\"projectionarg\">";
		Set<String> keySet = argMap.keySet();

		for (Iterator<String> iter = keySet.iterator(); iter.hasNext();)
		{
			String key = iter.next();
			Object value = argMap.get(key);
			String sLine = makeXmlLine(key, value);
			if (sLine != null && !sLine.equals(""))
				newFileContent += "\r\t" + sLine;
		}
		return newFileContent + "\r</xml>";
	}
	
	/**
	 * 根据参数Map生成xml内容(FY4A)
	 * 
	 * @param argMap
	 * @return
	 */
	private String getXmlContentFY4A(Map<String, Object> argMap) throws Exception
	{

		String newFileContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r" + "<xml>";
		Set<String> keySet = argMap.keySet();

		for (Iterator<String> iter = keySet.iterator(); iter.hasNext();)
		{
			String key = iter.next();
			Object value = argMap.get(key);
			String sLine = makeXmlLine(key, value);
			if (sLine != null && !sLine.equals(""))
				newFileContent += "\r\t" + sLine;
		}
		return newFileContent + "\r</xml>";
	}

	/**
	 * 根据单个参数创建一个DOM节点
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private String makeXmlLine(final String key, Object value) throws Exception
	{

		String newLineStr = "";

		if (value == null || "none".equals(value) || "".equals(value))
		{
			newLineStr += "<" + key + "></" + key + ">";
		} else if (value instanceof String)
		{
			newLineStr += "<" + key + ">" + value + "</" + key + ">";
		} else if (value instanceof List)
		{

		} else if (value instanceof Map)
		{	
			if ("OutputFiles".equals(key)) {
				newLineStr +="<" + key + ">" + "\r\t" + "<File>";
			} else {
				newLineStr +="<" + key + ">";
			}
			Set<String> keySet = ((Map<String, Object>) value).keySet();

			for (Iterator<String> iter = keySet.iterator(); iter.hasNext();)
			{
				String newkey = iter.next();
				Object newvalue = ((Map<String, Object>) value).get(newkey);
				String sLine = makeXmlLine(newkey, newvalue);
				if (sLine != null && !sLine.equals(""))
					newLineStr += "\r\t" + sLine;
			}
			if ("OutputFiles".equals(key)) {
				newLineStr += "\r\t"+ "</File>" + "\r\t" + "</" + key + ">";
			} else {
				newLineStr += "\r\t" + "</" + key + ">";
			}
			// newLineStr+= "<"+key+">"+value+"</"+key+">";
		} else if (value instanceof String[])
		{	
			if ("ValidEnvelopes".equals(key)) {
				newLineStr += "<" + key + ">";
				newLineStr += makeEnvelopes((String[]) value);
				newLineStr += "</" + key + ">";
			} else {
				newLineStr += makeEnvelopes((String[]) value);
			}
		}
		return newLineStr;
	}

	private String makeEnvelopes(String[] estrs) throws Exception
	{
		String retStr = "";
		if (estrs == null || "".equals(estrs))
		{
			return "";
		}
		for (String estr : estrs)
		{
			if (estr == null || "".equals(estr))
			{
				continue;
			}
			retStr += "<Envelope ";
			estr = estr.replaceAll("\\s+", "");
			String attrs[] = estr.split(",");
			for (String attr : attrs)
			{
				String a[] = attr.split(":");
				retStr += " " + a[0] + "=\"" + a[1] + "\"";
			}
			retStr += "/>";
		}
		return retStr;
	}

	
	public static String getDescXmlContent(Map<String, Object> argMap)
	{
		String newFileContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\t" +"<ProductMetaData>";
		Set<String> keySet = argMap.keySet();
		XmlMakeUtil util = new XmlMakeUtil();
		for (Iterator<String> iter = keySet.iterator(); iter.hasNext();)
		{
			String key = iter.next();
			Object value = argMap.get(key);
			String sLine="";
			try
			{
				sLine = util.makeXmlLine(key, value);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sLine != null && !sLine.equals(""))
				newFileContent += "\r\t\t" + sLine;
		}
		newFileContent +="\r\t<ProductMetaData>";
		return newFileContent + "\r</xml>";
	}
	
	/**
	 * 字符集转换(UTF-8转为UTF-8无BOM)
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public static void turnUTF8withBOM(String filePath, String targetFilePath) throws FileNotFoundException {
		File file = new File(filePath); 
		File targetFile = new File(targetFilePath); 
		if (!targetFile.exists()) {
            try {
				targetFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	    OutputStreamWriter osw = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        FileOutputStream fos = new FileOutputStream(targetFile, true);
        try {
        	br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			
			 if (targetFile.length() < 1) {
	             final byte[] bom = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
	             fos.write(bom);
	         }
			 String data = "";
	         osw = new OutputStreamWriter(fos, "UTF-8");
	         bw = new BufferedWriter(osw);
	         while ((data = br.readLine()) != null) {
		         bw.write(data+"\r\n");    
		     }
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			 try {
				br.close();
				bw.close();
			    fos.close();
			    file.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		     
		}            
    }
	
	public static void main(String[] args) throws Exception {
		/*
		String loginfo = "success";
		String xmlPath = "E:\\testData\\Share\\FY4A\\AGRI\\2018\\07\\05\\test.xml";
		String fileName = "FY4A-_AGRI--_N_DISK_1047E_L1-_FDI-_MULT_NOM_20170721234500_20170721235959_1000M_V0001.HDF";
		Map<String, Object> argMap = new LinkedHashMap<String, Object>();
		argMap.put("OrbitFilename", "");
		String[] ssr = "FYA4_AGRI_1000M".split("_");
		String satellite = "",sensor = "",resolution = "";
		if(3 == ssr.length)
		{
			satellite = ssr[0];
			sensor = ssr[1];
			resolution = ssr[2];
		}
		argMap.put("Satellite", satellite);
		argMap.put("Sensor", sensor);
		argMap.put("Level", "L1");
		argMap.put("ProjectionIdentify","");
		argMap.put("ObservationDate", "20180625");
		argMap.put("ObservationTime", "0400");
		argMap.put("Station", "MS");
		argMap.put("DayOrNight", "D");
		argMap.put("Length", "147701696");
		argMap.put("OrbitIdentify", "0400");
		Map<String, Object> argMapSecond = new LinkedHashMap<String, Object>();
		argMapSecond.put("OutputFilename", fileName.replace(".HDF", ".tif"));
		argMapSecond.put("Thumbnail", fileName.replace(".HDF", ".png"));
		argMapSecond.put("ExtendFiles", "");
		argMapSecond.put("Envelope", new String[] { "name:'GBAL',minx:105.10220289701361,maxx:112.44107008451367,miny:31.25820888451677,maxy:39.783599509516826"});
		argMapSecond.put("Length", "147701696");
		argMap.put("OutputFiles", argMapSecond);
		Map<String, Object> argMapThird = new LinkedHashMap<String, Object>();
		if (StringUtils.isNotBlank(loginfo) && "success".equals(loginfo)) {
			argMapThird.put("loglevel", "info");
			argMapThird.put("loginfo", loginfo);
		} else {
			argMapThird.put("loglevel", "error");
			argMapThird.put("loginfo", loginfo);
		}
		argMap.put("log", argMapThird);
		XmlMakeUtil.makeXml(argMap, xmlPath);
		*/
	}
	
}
