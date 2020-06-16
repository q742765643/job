package com.htht.job.core.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.List;


/**
 * @author yss
 * @date 2018年6月25日17:17:52
 */
public class CreateXmlUtils extends XMLWriter {

    public CreateXmlUtils() {
    }

    /**
     * @param format
     * @throws UnsupportedEncodingException
     */
    public CreateXmlUtils(OutputFormat format) throws UnsupportedEncodingException {
        super(format);
    }

    /**
     * @param out
     * @param format
     * @throws UnsupportedEncodingException
     */
    public CreateXmlUtils(OutputStream out, OutputFormat format) throws UnsupportedEncodingException {
        super(out, format);
    }

    /**
     *
     */

    /**
     * @param out
     * @throws UnsupportedEncodingException
     */
    public CreateXmlUtils(OutputStream out) throws UnsupportedEncodingException {
        super(out);
    }

    /**
     * @param writer
     * @param format
     */
    public CreateXmlUtils(Writer writer, OutputFormat format) {
        super(writer, format);
    }

    /**
     * @param writer
     */
    public CreateXmlUtils(Writer writer) {
        super(writer);
    }

    public static void main(String[] args) {
        // 将数据封装到对象
        /*MssPath mssPath = new MssPath();
		mssPath.setName("mss_path.xml");
		mssPath.setPath("E://xml");
		String[] data = {
				"D:\\satdata\\GF1-廊坊有基准\\langfang\\PAN\\GF1_PMS1_E116.5_N39.4_20131127_L1A0000117600-PAN1.tiff",
				"D:\\satdata\\GF1-廊坊有基准\\langfang\\PAN\\GF1_PMS1_E116.6_N39.7_20131127_L1A0000117599-PAN1.tiff",
				"D:\\satdata\\GF1-廊坊有基准\\langfang\\PAN\\GF1_PMS2_E116.9_N39.4_20131127_L1A0000117694-PAN2.tiff",
				"D:\\satdata\\GF1-廊坊有基准\\langfang\\PAN\\GF1_PMS2_E117.0_N39.6_20131127_L1A0000117693-PAN2.tiff" };
		mssPath.setData(data);
		// 将对象转化为json对象
		JSONObject jsonMssPath = JSONObject.fromObject(mssPath);
		// 解析json
		String string = jsonMssPath.toString();
		System.out.println(string);
		// 将json串转化
		MssPath bean = (MssPath) JSONObject.toBean(jsonMssPath,MssPath.class);*/

		/*testCreateMssPathXml(bean.getName(),bean.getPath(),bean.getData());*/

    }

    public static String testCreateMssPathXml(String name, String path, List<String> data) {
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        // 创建一个xml文档
        Document doc = DocumentHelper.createDocument();
        // 向xml文件中添加注释
        // doc.addComment("这里是注释");
        // 创建一个名为PIE_ORTHO_PATH的节点，因为是第一个创建，所以是根节点,再通过doc创建一个则会报错。
        Element root = doc.addElement("PIE_ORTHO_PATH");
        root.addAttribute("IMG_NUM", String.valueOf(data.size()));
        for (String string : data) {
            // 在root节点下创建一个名为IMG的节点
            Element imgEle = root.addElement("IMG");

            // 给imgEle节点添加一个子节点
            Element ingPathEle = imgEle.addElement("IMG_PATH");
            // 设置子节点的文本
            ingPathEle.setText(string);

        }

        // 用于格式化xml内容和设置头部标签
        OutputFormat format = OutputFormat.createPrettyPrint();
        // 设置xml文档的编码 为utf-8
        format.setEncoding("GB2312");
        format.setNewLineAfterDeclaration(false);
        OutputStream out;
        try {
            // 创建一个输出流对象
            out = new FileOutputStream(path + "/" + name);
            // 创建一个dom4j创建xml的对象
            // XMLWriter writer = new XMLWriter(out, format);
            CreateXmlUtils createMssPath = new CreateXmlUtils(out, format);
            createMssPath.write(doc);
            // 调用write方法将doc文档写到指定路径
            // writer.write(doc);
            // writer.close();
            createMssPath.close();

            return path + "/" + name;
        } catch (IOException e) {
            e.printStackTrace();
            return "生成XML文件失败";
        }
    }

    protected void writeDeclaration() throws IOException {
        OutputFormat format = getOutputFormat();

        String encoding = format.getEncoding();

        if (!format.isSuppressDeclaration()) {
            if (encoding.equals("UTF8")) {
                writer.write("<?xml version=\"1.0\"");

                if (!format.isOmitEncoding()) {
                    writer.write(" encoding=\"UTF-8\"");
                }

                writer.write(" standalone=\"no\"");
                writer.write("?>");
            } else {
                writer.write("<?xml version=\"1.0\"");

                if (!format.isOmitEncoding()) {
                    writer.write(" encoding=\"" + encoding + "\"");
                }

                writer.write(" standalone=\"no\"");
                writer.write("?>");
            }

            if (format.isNewLineAfterDeclaration()) {
                println();
            }

        }
    }

}
