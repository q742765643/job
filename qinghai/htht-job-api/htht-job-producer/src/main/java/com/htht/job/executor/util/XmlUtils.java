package com.htht.job.executor.util;

import com.htht.job.executor.model.xml.XmlDTO;
import com.mysql.fabric.xmlrpc.base.Array;

import org.apache.commons.collections.map.HashedMap;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @program: htht-job-api
 * @description: xml工具
 * @author: dingjiancheng
 * @create: 2018-09-28 13:01
 */
public class XmlUtils {

    private Map<String,List<Element>> res = new HashMap<>();

    private List<String> lists = new ArrayList<>();

//    private List<Element> elements = new ArrayList<>();

    /**
     * @Author: dingjiancheng
     * @Description:
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public boolean createAlgorithmXml(String rootTag, List<XmlDTO> inputList, List<XmlDTO> outputList, String path) {

        //判断文件是否存在，不存在就创建
        File file = new File(path);

        File fileParent = file.getParentFile();
        if(!fileParent.exists()){
            fileParent.mkdirs();
        }

        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        // 创建文档对象
        Document doc = DocumentHelper.createDocument();
        // 创建根节点
        Element root = doc.addElement("xml");
        //根节点添加identify属性
        if(!StringUtils.isEmpty(rootTag)){
        	root.addAttribute("identify", rootTag);
        }
        //从inputList添加input节点
        for (int i = 0; i < inputList.size(); i++) {
            // 创建input节点
            Element inputElement = root.addElement("input");
            //赋值
            if(!StringUtils.isEmpty( inputList.get(i).getIdentify())){
            	inputElement.addAttribute("identify", inputList.get(i).getIdentify());
            }
            if(!StringUtils.isEmpty(inputList.get(i).getType())){
            	inputElement.addAttribute("type", inputList.get(i).getType());
            }
            if(!StringUtils.isEmpty(inputList.get(i).getDescription())){
            	inputElement.addAttribute("des", inputList.get(i).getDescription());
            }
            if(!StringUtils.isEmpty(inputList.get(i).getValue())){
                inputElement.setText(inputList.get(i).getValue());
            }
        }


        //从outputList添加outputs节点
        for (int i = 0; i < outputList.size(); i++) {
            // 创建input节点
            Element inputElement = root.addElement("output");
            //赋值
            if(!StringUtils.isEmpty( outputList.get(i).getIdentify())){
            	inputElement.addAttribute("identify", outputList.get(i).getIdentify());
            }
            if(!StringUtils.isEmpty(outputList.get(i).getType())){
            	inputElement.addAttribute("type", outputList.get(i).getType());
            }
            if(!StringUtils.isEmpty(outputList.get(i).getDescription())){
            	inputElement.addAttribute("des", outputList.get(i).getDescription());
            }
            if(!StringUtils.isEmpty(outputList.get(i).getValue())){
                inputElement.setText(outputList.get(i).getValue());
            }
        }

        // 设置XML文档格式
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        // 设置XML编码方式,即是用指定的编码方式保存XML文档到字符串(String),这里也可以指定为GBK或是ISO8859-1
        outputFormat.setEncoding("UTF-8");
        // outputFormat.setSuppressDeclaration(true); //是否生产xml头
        outputFormat.setIndent(true); // 设置是否缩进
        outputFormat.setIndent("    "); // 以四个空格方式实现缩进
        outputFormat.setNewlines(true); // 设置是否换行
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(outputFormat);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            FileOutputStream fos = new FileOutputStream(path);
            assert writer != null;
            writer.setOutputStream(fos);
            writer.write(doc);
            writer.close();
            System.out.println("写入完毕！文件位置：" + path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @Author: dingjiancheng
     * @Description:
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public Map<String,List<Element>> xmlToMap(String path) {
        //清空res map
//        res.clear();
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new File(path));
            //获取文档根节点
            Element root = document.getRootElement();
            //调用下面获取子节点的递归函数。
            getChildNodes(root);
            return res;
        } catch (DocumentException e) {
            e.printStackTrace();
            return res;
        }
    }

    /**
     * @Author: dingjiancheng
     * @Description:
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public Map<String,List<Element>> elementXmlToMap(String path, String elementName) {
        //清空res map
//        res.clear();
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        Document document = null;
        return getStringObjectMap(path, reader, elementName);
    }

    /**
     * @Author: dingjiancheng
     * @Description:
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public Map<String,List<Element>> outputFilesXmlToMap(String path) {
        //清空res map
        res.clear();
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        Document document = null;
        return getStringObjectMap(path, reader, "outFiles");
    }

    private Map<String,List<Element>> getStringObjectMap(String path, SAXReader reader, String outputfiles) {
        Document document;
        try {
            document = reader.read(new File(path));
            //获取文档outputfiles节点
            Element outputFilesElem = document.getRootElement().element(outputfiles);

            //调用下面获取子节点的递归函数。
            getChildNodes(outputFilesElem);
            return res;
        } catch (DocumentException e) {
            e.printStackTrace();
            return res;
        }
    }

    /**
     * @Author: dingjiancheng
     * @Description:
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public Map<String,List<Element>> tablesXmlToMap(String path) {
        //清空res map
        res.clear();
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        Document document = null;
        return getStringObjectMap(path, reader, "tables");
    }

    /**
     * @Author: dingjiancheng
     * @Description: 获取product.xml中日志状态
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public Map<String, Object> getProductXmlStatus(String path) {
        Map<String, Object> res = new HashedMap();
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new File(path));
            //获取文档根节点
            Element root = document.getRootElement();
            Element statusElem = root.element("log").element("status");
            Element infoElem = root.element("log").element("info");
            if(!StringUtils.isEmpty(statusElem)){
                res.put("status", statusElem.getText());
            }
            if(!StringUtils.isEmpty(infoElem)){
                res.put("info", infoElem.getText());
            }
            return res;
        } catch (DocumentException e) {
            e.printStackTrace();
            return res;
        }
    }

    /**
     * @Author: dingjiancheng
     * @Description: 获取product.xml中日志状态
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public boolean isSuccessByXml(String path) {
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new File(path));
            //获取文档根节点
            Element root = document.getRootElement();
            Element statusElem = root.element("log").element("status");
            if("1".equals(statusElem.getText())){
                return true;
            }else {
                return false;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @SuppressWarnings("finally")
	public String getFailedInfo(String path) {
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        Document document = null;
        String errMsg = "failed!";
        try {
			document = reader.read(new File(path));
			//获取文档根节点
			Element root = document.getRootElement();
			Element statusElem = root.element("log").element("info");
			if ("".equals(statusElem.getText())) {
			    return errMsg;
			}
			errMsg = statusElem.getText();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			return errMsg;
		}

    }
    
    

    /**
     * @Author: dingjiancheng
     * @Description: 获取product.xml中元素属性值
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public List<String >getXmlAttrVal(String path, String elementName , String attrName) {
    	Map<String,List<Element>> map = outputFilesXmlToMap(path);
    	List<String> eList = new ArrayList<>();
    	List<Element> list = map.get(elementName);
    	if(list!=null&&list.size()>0) {
    		for(Element e:list) {
    			eList.add(e.attribute(attrName).getValue().trim());
    		}
    	}
    	return eList;
    }
    public List<String >getXmlAttrVal(Map<String,List<Element>> map, String elementName , String attrName) {
    	List<String> eList = new ArrayList<>();
    	List<Element> list = map.get(elementName);
    	if(list!=null&&list.size()>0) {
    		for(Element e:list) {
    			eList.add(e.attribute(attrName).getValue().trim());
    		}
    	}
    	return eList;
    }

    /**
     * @Author: dingjiancheng
     * @Description: 获取product.xml中元素中的file值
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public List<String> getXmlAttrFileElementVal(String path, String elementName,String identify,String regionId) {
    	Map<String,List<Element>> map = outputFilesXmlToMap(path);
    	List<Element> elementList = map.get(elementName);
    	if(elementList!=null&&elementList.size()>0) {
    		for(Element element:elementList) {
    			if(element.attributeValue(identify).trim().equalsIgnoreCase(regionId)) {
    				 lists.clear();
    			        getElementsFileVal(element);
    			}
    		}
    	}
        return lists;
    }
    public List<String> getXmlAttrFileElementVal(Map<String,List<Element>> map, String elementName,String identify,String regionId) {
    	List<Element> elementList = map.get(elementName);
    	if(elementList!=null&&elementList.size()>0) {
    		for(Element element:elementList) {
    			if(element.attributeValue(identify).trim().equalsIgnoreCase(regionId)) {
    				lists.clear();
    				getElementsFileVal(element);
    			}
    		}
    	}
    	return lists;
    }
    
    /**
     * @Author: dingjiancheng
     * @Description: 获取product.xml中元素中的file值
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public List<String> getXmlAttrFileElementVal(String path, String elementName) {
    	Map<String,List<Element>> map = outputFilesXmlToMap(path);
        Element element = map.get(elementName).get(0);
        lists.clear();
        getElementsFileVal(element);
        return lists;
    }
    public List<String> getXmlAttrFileElementVal(Map<String,List<Element>> map, String elementName) {
    	Element element = map.get(elementName).get(0);
    	lists.clear();
    	getElementsFileVal(element);
    	return lists;
    }
    
    /**
     * @Author: dingjiancheng
     * @Description: 获取product.xml中元素中的Element
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public List<Element> getXmlElements(String path, String elementName) {
    	Map<String,List<Element>> map = outputFilesXmlToMap(path);
        return map.get(elementName);
    }

    /**
     * @Author: dingjiancheng
     * @Description: 获取product.xml中元素中的Element
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    @SuppressWarnings("finally")
	public List<Element> getTablenameElements(String path,String tableName) {
        List<Element> lists = new ArrayList<>();
        List res = new ArrayList();
		try {
			res = (List)tablesXmlToMap(path).get(tableName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (null == res || 0 == res.size()) {
				 return lists;
			}
			if(res.size() > 0){
	            for (int i = 0; i < res.size(); i++) {
	                lists.add((Element)res.get(i));
	            }
	        }
			 return lists;
		}
    }

    //递归查询节点函数,输出节点名称
    private void getChildNodes(Element elem){
        if(res.containsKey(elem.getName())){
        	res.get(elem.getName()).add(elem);
        }else{
        	List<Element> list = new ArrayList<>();
        	list.add(elem);
            res.put(elem.getName(),list);
        }
        Iterator<Node> it = elem.nodeIterator();
        while (it.hasNext()){
            Node node = it.next();
            if (node instanceof Element){
                Element e1 = (Element)node;
                getChildNodes(e1);
            }
        }
    }

    //递归查询节点
    private void getElementsFileVal(Element elem){
        Iterator<Node> it = elem.nodeIterator();
        while (it.hasNext()){
            Node node = it.next();
            if (node instanceof Element){
                Element e1 = (Element)node;
                if("file".equals(e1.getQName().getName())){
                    lists.add(e1.getText());
                }
                getChildNodes(e1);
            }
        }
    }

    /**
     * @Author: dingjiancheng
     * @Description: 获取product.xml中元素中的Element
     * @Param: path：全路径（包含文件名以及格式）
     * @return:
     * @date: 2018/9/28
     */
    public List<String> getFileElements(String path, String elementName) {
        lists.clear();
        Map<String,List<Element>> m = xmlToMap(path);
        Element e =  m.get(elementName).get(0);
        XmlUtils xmlUtils = new XmlUtils();
        xmlUtils.getNodes(e);
        return lists;
    }

    /**
     * 从指定节点开始,递归遍历所有子节点
     * @author dingjiancheng
     */
    public void getNodes(Element node){
        if("file".equals(node.getName())){
            lists.add(node.getTextTrim());
        }
        //递归遍历当前节点所有的子节点
        List<Element> listElement=node.elements();//所有一级子节点的list
        for(Element e:listElement){//遍历所有一级子节点
            this.getNodes(e);//递归
        }
    }

}
