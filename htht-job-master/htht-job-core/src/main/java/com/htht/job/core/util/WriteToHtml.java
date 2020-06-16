package com.htht.job.core.util;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;

public class WriteToHtml {
	
	private WriteToHtml() {
	}
    public static String writeToHtml(String str, String path) throws IOException {
        String s = "<!DOCTYPE html>\r\n" +
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
                "<body style=\"max-width:600px; margin: 0 auto;\">" + str +
                "</body>\r\n" +
                "</html>";

        File f = new File(path);
        if (!f.exists()) {
        	f.createNewFile();
        }
        try(
        	FileOutputStream fw = new FileOutputStream(f, false);
        	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fw, "UTF-8"));
        	){
            out.write(s, 0, s.length() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "success";
    }

    public static String saveHtmlToWord(String filePath, String htmlName,
                                     String docName) {// 创建 POIFSFileSystem 对象

        POIFSFileSystem poifs = new POIFSFileSystem();
        // 创建输出流
        try(
        	InputStream is = new FileInputStream(filePath + htmlName);
        	OutputStream out = new FileOutputStream(filePath + docName);
        	){
            // 写入
            poifs.writeFilesystem(out);
            // 释放资源
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docName;
    }
    
   public static void main(String[] args) throws IOException {
	   saveHtmlToWord("D:/Test/","Acid_Rain_Monitoring(201806).docx","Acid_Rain_Monitoring(201806)123.docx");
  }

}