package com.xxl.job.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.executor.model.algorithm.OptionBean;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class XmlTest {

    @Test
    public void parseXmlFile() {
        try {
            SAXReader reader = new SAXReader();
            File file = new File("D:/PIECalibration.xml");
            Document document = reader.read(file.getPath());
            Element root = document.getRootElement();

            List<Element> childElements = root.elements();
            for (Element child : childElements) {
                //未知属性名情况下
                // List<Attribute> attributeList = child.attributes();
                //  for (Attribute attr : attributeList) {
                //   System.out.println(attr.getName() + ": " + attr.getValue());
                // }
                String name = child.getName();
                System.out.println(name);
                List<Element> elements = child.elements();
                for (Element object : elements) {
                    List<Attribute> attributes = object.attributes();
                    for (Attribute object2 : attributes) {
                        System.out.println(object2.getName() + ": " + object2.getValue());
                    }
                }
                //已知属性名情况下
                // System.out.println("id: " + child.attributeValue("id"));

                //未知子元素名情况下
                //  List<Element> elementList = child.elements();
                //   for (Element ele : elementList) {
                //   System.out.println(ele.getName() + ": " + ele.getText());
                //   }
                //   System.out.println();

                // 已知子元素名的情况下
                //  System.out.println("title" + child.elementText("title"));
                //  System.out.println("author" + child.elementText("author"));
                //这行是为了格式化美观而存在
                System.out.println();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseOption() {
        String options = "{'表观辐亮度定标':'100','表观反射率定标':'200'}";
        JSONObject parseObject = JSON.parseObject(options);
        Set<String> keySet = parseObject.keySet();
        ArrayList<OptionBean> arrayList = new ArrayList<>();
        for (String key : keySet) {
            OptionBean optionBean = new OptionBean();
            String value = (String) parseObject.get(key);
            optionBean.setId(value);
            optionBean.setText(key);
            arrayList.add(optionBean);
            System.out.println(key + "       " + value);
        }
        String jsonString = JSON.toJSONString(arrayList);
        System.out.println(jsonString);
    }

    @Test
    public void extension() {
        String extensionName1 = "‪D:\\soa_share\\UPLOAD_ZIP_PATH\\12306Bypass_1.12.95.zip";
        String extensionName2 = "‪D:/soa_share/UPLOAD_ZIP_PATH/12306Bypass_1.12.95.zip";
        String substring = extensionName1.substring(extensionName1.lastIndexOf(File.separator) + 1);
        System.out.println(substring);
    }

    @Test
    public void CountDownLatchTest() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("10.1.7.46");
        arrayList.add("192.168.153.128");
        arrayList.add("192.168.153.133");
        // 开始的倒数锁 
        final CountDownLatch begin = new CountDownLatch(1);
        // 结束的倒数锁 
        final CountDownLatch end = new CountDownLatch(arrayList.size());

        // 十名选手
        final ExecutorService exec = Executors.newFixedThreadPool(arrayList.size());
        for (String string : arrayList) {
            Runnable run = new Runnable() {
                public void run() {
                    try {
                        // 如果当前计数为零，则此方法立即返回。
                        // 等待
                        begin.await();
                        //Thread.sleep((long) (Math.random() * 10000));
                        Thread.sleep((long) (10000));
                        System.out.println(System.currentTimeMillis());
                        System.out.println("No." + string + " arrived");
                    } catch (InterruptedException e) {
                    } finally {
                        // 每个选手到达终点时，end就减一
                        end.countDown();
                    }
                }
            };
            exec.submit(run);
        }
        System.out.println("Game Start");
        // begin减一，开始游戏
        begin.countDown();
        // 等待end变为0，即所有选手到达终点
        try {
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Game Over");
        exec.shutdown();
    }
}



