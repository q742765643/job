package org.htht.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.htht.util.execmd.ProcCmd;

public class ServerImpUtil {

	/**根据输入文件名及输出根路径获得输出路径
	 * @param inputFilePath
	 * @param outputRootDir
	 * @return
	 */
	public static String getNewMosaicOutputDir(String inputFilePath, String outputRootDir){
		
		String inFileName = (new File(inputFilePath)).getName();
		String dateStr = "",sdir="";
		
		if(inFileName.startsWith("TERRA_")){
			//Modis
			Pattern pattern = Pattern.compile("\\d{4}_\\d{2}_\\d{2}");
			Matcher matcher =pattern.matcher(inFileName);
			while (matcher.find()) {
				dateStr = matcher.group(0);
				dateStr = dateStr.replaceAll("_", "");
				System.out.println(dateStr);
			}
			sdir = inFileName.replaceAll("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_|\\..{3}$", "");
		}else if(inFileName.startsWith("FY3A_")){
			//风云3A
			Pattern pattern = Pattern.compile("\\d{8}");
			Matcher matcher =pattern.matcher(inFileName);
			while (matcher.find()) {
				dateStr = matcher.group(0);
				System.out.println(dateStr);
			}
			sdir = inFileName.replaceAll("\\d{8}_\\d{4}_|\\..{3}$", "");
		}else if(inFileName.startsWith("p1bn")){
			sdir = inFileName.substring(0,6);
		}else if(inFileName.startsWith("AQUA_")){
			//AQUA_2012_11_07_21_51_XJ.MOD021KM.hdf
			Pattern pattern = Pattern.compile("\\d{4}_\\d{2}_\\d{2}");
			Matcher matcher =pattern.matcher(inFileName);
			while (matcher.find()) {
				dateStr = matcher.group(0);
				dateStr = dateStr.replaceAll("_", "");
				System.out.println(dateStr);
			}
			sdir = inFileName.replaceAll("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_|\\..{3}$", "");
		}
		
		String outputDir = outputRootDir;
		if(!"".equals(dateStr)){
			outputDir = outputDir + "/"+dateStr;
		}
		if(!"".equals(sdir)){
			outputDir = outputDir + "/"+sdir;
		}
		return outputDir;
	}
	
	/**根据输入文件名及输出根路径获得输出路径
	 * @param inputFilePath
	 * @param outputRootDir
	 * @return
	 */
	public static String getNewOutputDir(String inputFilePath, String outputRootDir){
		
		String inFileName = (new File(inputFilePath)).getName();
		String dateStr = "",sdir="";
		
		if(inFileName.startsWith("TERRA_")){
			//Modis
			Pattern pattern = Pattern.compile("\\d{4}_\\d{2}_\\d{2}");
			Matcher matcher =pattern.matcher(inFileName);
			while (matcher.find()) {
				dateStr = matcher.group(0);
				dateStr = dateStr.replaceAll("_", "");
				System.out.println(dateStr);
			}
			sdir = inFileName.replaceAll("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_|\\..{3}$", "");
		}else if(inFileName.startsWith("FY3A_")){
			//风云3A
			Pattern pattern = Pattern.compile("\\d{8}");
			Matcher matcher =pattern.matcher(inFileName);
			while (matcher.find()) {
				dateStr = matcher.group(0);
				System.out.println(dateStr);
			}
			sdir = inFileName.replaceAll("\\d{8}_\\d{4}_|\\..{3}$", "");
		}else if(inFileName.startsWith("p1bn")){
			sdir = inFileName.substring(0,6);
		}else if(inFileName.startsWith("AQUA_")){
			//AQUA_2012_11_07_21_51_XJ.MOD021KM.hdf
			Pattern pattern = Pattern.compile("\\d{4}_\\d{2}_\\d{2}");
			Matcher matcher =pattern.matcher(inFileName);
			while (matcher.find()) {
				dateStr = matcher.group(0);
				dateStr = dateStr.replaceAll("_", "");
				System.out.println(dateStr);
			}
			sdir = inFileName.replaceAll("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_|\\..{3}$", "");
		}
		
		String outputDir = outputRootDir;
		if(!"".equals(dateStr)){
			outputDir = outputDir + "/"+dateStr;
		}
		if(!"".equals(sdir)){
			outputDir = outputDir + "/"+sdir;
		}
		return outputDir;
	}
	
	public static boolean touchFile(String ... filePath){
		if(filePath!=null){
			for(String fp : filePath){
				File file = new File(fp);
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
			}
		}
		return true;
	}

	public static String makeTempXmlPath(String argXmlPath,String inputFilePath) {
		File infile = new File(inputFilePath);
//		String argXmlFilename = infile.getName()+System.currentTimeMillis()+".xml";
		argXmlPath = argXmlPath +File.separator + infile.getName()+".xml";
		return argXmlPath;
	}

	public static String getOutputXMLContent(String outputDir,String inputFilePath) throws IOException {
		String tempfile = inputFilePath;
		tempfile = tempfile.replaceAll("\\\\|//", "/").replaceAll("\\([0-9]+\\.\\)", ".");
		tempfile = tempfile.substring(tempfile.lastIndexOf("/"));
		String outputXml = outputDir + tempfile+".xml";
		String fcontent = FileUtils.readFileToString(new File(outputXml),"UTF-8");
		return fcontent;
	}

	public static void executeCmd(String exePath, String argXmlPath) {
		String cmd = exePath
				+ " \"" + argXmlPath + "\" "
				;
		ProcCmd pc = new ProcCmd();
		pc.setCharsetName("GBK");
		pc.setLogFilePath("D:\\logs\\exelog");
		pc.exec(cmd);
	}
	
	public static void executeCmdByCommandstr(String exePath, String commandstr) {
		String cmd = exePath + " " + commandstr;
		ProcCmd pc = new ProcCmd();
		pc.setCharsetName("GBK");
		pc.setLogFilePath("D:\\logs\\exelog");
		System.out.println("执行参数为:"+cmd);
		pc.exec(cmd);
	}

	public static String encodeXMLContent(String content){
		return content;
	}
	
	public static void main(String[] args) {
		String outdir = getNewOutputDir("D:\\GeoDo\\MODIS\\TERRA_2010_03_25_03_09_GZ.MOD021KM.hdf","D:\\GeoDo\\output");
		System.err.print(outdir);
	}
	
}
