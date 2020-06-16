package com.htht.job.admin.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;

public class WriteToHtml {
   public static  String writeToHtml(String str,String path) throws IOException {
	   String s="<!DOCTYPE html>\r\n" + 
	   		"<html xmlns=\"http://www.w3.org/TR/REC-html40\" xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\">\r\n" + 
	   		"<head>\r\n" + 
	   		"<!--[if gte mso 9]><xml><w:WordDocument><w:View>Print</w:View><w:TrackMoves>false</w:TrackMoves><w:TrackFormatting/><w:ValidateAgainstSchemas/><w:SaveIfXMLInvalid>false</w:SaveIfXMLInvalid><w:IgnoreMixedContent>false</w:IgnoreMixedContent><w:AlwaysShowPlaceholderText>false</w:AlwaysShowPlaceholderText><w:DoNotPromoteQF/><w:LidThemeOther>EN-US</w:LidThemeOther><w:LidThemeAsian>ZH-CN</w:LidThemeAsian><w:LidThemeComplexScript>X-NONE</w:LidThemeComplexScript><w:Compatibility><w:BreakWrappedTables/><w:SnapToGridInCell/><w:WrapTextWithPunct/><w:UseAsianBreakRules/><w:DontGrowAutofit/><w:SplitPgBreakAndParaMark/><w:DontVertAlignCellWithSp/><w:DontBreakConstrainedForcedTables/><w:DontVertAlignInTxbx/><w:Word11KerningPairs/><w:CachedColBalance/><w:UseFELayout/></w:Compatibility><w:BrowserLevel>MicrosoftInternetExplorer4</w:BrowserLevel><m:mathPr><m:mathFont m:val=\"Cambria Math\"/><m:brkBin m:val=\"before\"/><m:brkBinSub m:val=\"--\"/><m:smallFrac m:val=\"off\"/><m:dispDef/><m:lMargin m:val=\"0\"/> <m:rMargin m:val=\"0\"/><m:defJc m:val=\"centerGroup\"/><m:wrapIndent m:val=\"1440\"/><m:intLim m:val=\"subSup\"/><m:naryLim m:val=\"undOvr\"/></m:mathPr></w:WordDocument></xml><![endif]-->\r\n" + 
	   		"<meta charset=\"utf-8\"/>\r\n" + 
	   		"<meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\"/>\r\n" + 
	   		"<meta content=\"ie=edge\" http-equiv=\"X-UA-Compatible\"/>\r\n" + 
	   		"<title>Document</title>\r\n" + 
	   		"<style>\r\n" + 
	   		"    * {\r\n" + 
	   		"      padding: 0;\r\n" + 
	   		"      margin: 0;\r\n" + 
	   		"    }\r\n" + 
	   		"  </style>\r\n" + 
	   		"</head>\r\n" + 
	   		"<body style=\"max-width:600px; margin: 0 auto;\">"+str+
	   		"</body>\r\n" + 
	   		"</html>";
//	   System.out.println(s);
	   FileWriter fw = null;
	   File f = new File(path);
	   try {
	   if(!f.exists()){
	   f.createNewFile();
	   }
	   fw = new FileWriter(f);
	   BufferedWriter out = new BufferedWriter(fw);
	   out.write(s, 0, s.length()-1);
	   out.close();
	   } catch (IOException e) {
	   e.printStackTrace();
	   }
	  
	return "success";
   }
   public static String saveHtmlToWord(String filePath, String htmlName,
			String docName) {// 创建 POIFSFileSystem 对象

		POIFSFileSystem poifs = new POIFSFileSystem();
		// 获取DirectoryEntry
		DirectoryEntry directory = poifs.getRoot();
		// 创建输出流
		OutputStream out = null;
		try {
			out = new FileOutputStream(filePath + docName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			// 创建文档,1.格式,2.HTML文件输入流
			InputStream is = new FileInputStream(filePath + htmlName);
			String htmlStr = IOUtils.toString(is,"utf-8");
			htmlStr.replaceAll("", "");
			System.out.println(htmlStr);
			
			InputStream changedInputStream = IOUtils.toInputStream(htmlStr);
			DocumentEntry entry = directory.createDocument("WordDocument", changedInputStream);
			// 写入
			poifs.writeFilesystem(out);
			// 释放资源
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return docName;
	}
//   public static void main(String[] args) throws IOException {
//	writeToHtml("<p style=\"text-align:center;line-height: 30px;\">二〇一六年</p>\r\n" +
//			"<p style=\"text-align:center;line-height: 30px;\">第36期</p>\r\n" +
//			"<p style=\"text-align:center;line-height: 30px;\">（总第36期）</p>");
//	saveHtmlToWord("D:\\html\\", "index.html", "newWord.doc");
//  }
   
}
