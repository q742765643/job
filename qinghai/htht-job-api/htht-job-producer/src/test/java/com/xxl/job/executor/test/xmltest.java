package com.xxl.job.executor.test;/**
 * Created by zzj on 2018/3/23.
 */

import com.htht.job.executor.util.ProcessXmlParse;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @program: htht-job
 * @description: xml解析测试
 * @author: zzj
 * @create: 2018-03-23 11:00
 **/

public class xmltest {

    @Test
    public void testGetRoot() throws Exception{
        SAXReader sax=new SAXReader();//创建一个SAXReader对象
       File xmlFile=new File("/zzj/aa.xml");//根据指定的路径创建file对象
        Document document=sax.read(xmlFile);//获取document对象,如果文档无节点，则会抛出Exception提前结束
        Element root=document.getRootElement();//获取根节点
         /*List<Element> listElement=root.elements();//所有一级子节点的list
        List<FlowXmlVo> list=new ArrayList<FlowXmlVo>();
        this.getNodes(listElement,list);//从根节点开始遍历所有节点

        for(FlowXmlVo f:list){
            StringBuffer nextId=new StringBuffer();
            getNextId(listElement,f.getId(),nextId);
        }
        System.out.println(JSON.toJSONString(list));
*/
        ProcessXmlParse processXmlParse=new ProcessXmlParse();
        //processXmlParse.parseToList(root);

    }


   public String   getNextId(List<Element> listElement,String id,StringBuffer nextId){
       for(Element e:listElement) {
           if ("Figure".equals(e.getName()) && "Link".equals(e.attributeValue("type"))) {
               if(id.equals(e.attributeValue("startFigureId"))){
                   if((e.attributeValue("endFigureId")).indexOf("flowCell")==0){
                          getNextFlowId(listElement, e.attributeValue("endFigureId"), nextId);

                      }else if((e.attributeValue("endFigureId")).indexOf("sequence")==0){
                          getNextSequenceIdFLow(listElement, e.attributeValue("endFigureId"), nextId);

                      }
                      else {
                          nextId.append(e.attributeValue("endFigureId") + ",");

                   }









                 }

           }

       }
       System.out.println(nextId.toString());
      return nextId.toString();

   }

   public  void getNextFlowId(List<Element> listElement,String id,StringBuffer nextId) {
       for (Element e : listElement) {
           if(id.equals(e.attributeValue("startFigureId"))&&"true".equals(e.attributeValue("isChild"))){

               getNextSequenceId(listElement,e.attributeValue("endFigureId"),nextId);
           }

       }
   }
   public void getNextSequenceId(List<Element> listElement,String id, StringBuffer nextId){
       for (Element e : listElement) {
           if (id.equals(e.attributeValue("startFigureId")) && "false".equals(e.attributeValue("isChild"))) {
               nextId.append(e.attributeValue("endFigureId") + ",");

           }
       }

   }

    public void getNextSequenceIdFLow(List<Element> listElement,String id, StringBuffer nextId){
        for (Element e : listElement) {
            if (id.equals(e.attributeValue("startFigureId")) && "true".equals(e.attributeValue("isChild"))) {
                getNextSequenceIdFLow1(listElement,e.attributeValue("endFigureId"),nextId);

            }
        }

    }

    public void getNextSequenceIdFLow1(List<Element> listElement,String id, StringBuffer nextId){
        for (Element e : listElement) {
            if (id.equals(e.attributeValue("startFigureId")) && "false".equals(e.attributeValue("isChild"))) {
                nextId.append(e.attributeValue("endFigureId") + ",");

            }
        }

    }
}

