package com.htht.job.executor.util;/**
 * Created by zzj on 2018/3/25.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import com.htht.job.vo.LinkVo;
import org.dom4j.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: htht-job
 * @description: 流程Xml解析
 * @author: zzj
 * @create: 2018-03-25 11:13
 **/
@Service
public class ProcessXmlParse {
    /**
     * @Description: 解析获得list
     * @Param: [root]
     * @return: void
     * @Author: zzj
     * @Date: 2018/3/26
     */
    public List<ProcessStepsDTO> parseToList(String xmlStr, StringBuffer startFigureBuffer) {
        List<ProcessStepsDTO> processStepsDTOList = new ArrayList<ProcessStepsDTO>();
        try {
            // 将字符串转为XML
            Document document = DocumentHelper.parseText(xmlStr);
            //获取跟节点
            Element root = document.getRootElement();
            List<Element> listElement = root.elements();
            List<LinkVo> linkVos = new ArrayList<LinkVo>();

            //从根节点开始遍历所有节点
            this.getNodes(listElement, processStepsDTOList, linkVos);

            for (int i = 0; i < processStepsDTOList.size(); i++) {
                StringBuffer nextId = new StringBuffer();
                getNextId(linkVos, processStepsDTOList.get(i).getDataId(), nextId);
                processStepsDTOList.get(i).setNextId(nextId.toString());
            }
            getNextId(linkVos, ProcessConstant.STRATFIGURE, startFigureBuffer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return processStepsDTOList;
    }

    /**
     * @Description: 获取下级子节点
     * @Param: [listElement, list]
     * @return: void
     * @Author: zzj
     * @Date: 2018/3/25
     */
    public void getNodes(List<Element> listElement, List<ProcessStepsDTO> processStepsDTOList, List<LinkVo> linkVos) {
        //遍历所有一级子节点
        for (Element e : listElement) {
            if (ProcessConstant.FIGUER.equals(e.getName())) {
                if (ProcessConstant.START.equals(e.attributeValue(ProcessConstant.TYPE))) {
                    ProcessStepsDTO processStepsDTO = new ProcessStepsDTO();
                    processStepsDTO.setDataId(ProcessConstant.STRATFIGURE);
                    processStepsDTOList.add(processStepsDTO);

                }
                if (ProcessConstant.SERVICE.equals(e.attributeValue(ProcessConstant.TYPE))) {
                    ProcessStepsDTO processStepsDTO = new ProcessStepsDTO();
                    List<Map> inPutParams = new ArrayList<Map>();
                    List<Map> outPutParams = new ArrayList<Map>();
                    String serviceId = e.attributeValue(ProcessConstant.SERVICEID);
                    String IsProcess = e.attributeValue(ProcessConstant.IsProcess);
                    processStepsDTO.setServiceId(serviceId.substring(0, serviceId.length() - 5));
                    processStepsDTO.setDataId(e.attributeValue(ProcessConstant.ID));
                    processStepsDTO.setIsPl(e.attributeValue(ProcessConstant.ISPL));
                    processStepsDTO.setLabel(e.attributeValue(ProcessConstant.LABEL));
                    processStepsDTO.setIsProcess(IsProcess);
                    List<Element> next = e.elements();
                    this.getParam(next, inPutParams);
                    processStepsDTO.setDynamicParameter(JSON.toJSONString(inPutParams));
                    processStepsDTOList.add(processStepsDTO);

                }
                if (ProcessConstant.END.equals(e.attributeValue(ProcessConstant.TYPE))) {
                    ProcessStepsDTO processStepsDTO = new ProcessStepsDTO();
                    processStepsDTO.setDataId(ProcessConstant.ENDFIGURE);
                    List<Element> next = e.elements();
                    List<CommonParameter> commonParameterList = new ArrayList<CommonParameter>();
                    for (Element end : next) {
                        CommonParameter commonParameter = new CommonParameter();
                        String cellId = end.attributeValue(ProcessConstant.CELLID);
                        commonParameter.setCellId(cellId.substring(0, cellId.length() - 5));
                        commonParameter.setDataID(end.attributeValue(ProcessConstant.DATAID));
                        commonParameter.setParameterName(end.attributeValue(ProcessConstant.PARAMERTNAME));
                        commonParameter.setParameterType(end.attributeValue(ProcessConstant.PARAMERTTYPE));
                        commonParameter.setParameterDesc(end.attributeValue(ProcessConstant.PARAMERTERDESC));
                        commonParameter.setGroup(end.attributeValue(ProcessConstant.GROUP));
                        commonParameter.setUuid(end.attributeValue(ProcessConstant.uuid));

                        commonParameterList.add(commonParameter);

                    }
                    processStepsDTO.setDynamicParameter(JSON.toJSONString(commonParameterList));
                    processStepsDTOList.add(processStepsDTO);

                }
                if (ProcessConstant.LINK.equals(e.attributeValue(ProcessConstant.TYPE))) {
                    LinkVo linkVo = new LinkVo();
                    linkVo.setIsData(e.attributeValue(ProcessConstant.ISDATA));
                    linkVo.setIsChild(e.attributeValue(ProcessConstant.ISCHILD));
                    linkVo.setStartFigureId(e.attributeValue(ProcessConstant.STARTFIGUREID));
                    linkVo.setEndFigureId(e.attributeValue(ProcessConstant.ENDFIGUREID));
                    linkVos.add(linkVo);
                } else {
                    List<Element> next = e.elements();
                    this.getNodes(next, processStepsDTOList, linkVos);
                }

            }

        }

    }

    /**
     * @Description:获取下级ID
     * @Param: [listElement, id, nextId]
     * @return: java.lang.String
     * @Author: zzj
     * @Date: 2018/3/25
     */
    public void getNextId(List<LinkVo> linkVos, String id, StringBuffer nextId) {
        for (LinkVo linkVo : linkVos) {
            if (id.equals(linkVo.getStartFigureId())) {
                if (linkVo.getEndFigureId().indexOf(ProcessConstant.FLOWCELL) >= 0) {
                    getChildId(linkVos, linkVo.getEndFigureId(), nextId);
                } else if (linkVo.getEndFigureId().indexOf(ProcessConstant.SEQUENCE) >= 0) {
                    getChildId(linkVos, linkVo.getEndFigureId(), nextId);
                } else {
                    nextId.append(linkVo.getEndFigureId() + ",");
                }
            }
        }

    }

    /**
     * @Description: 获取子节点对应ID
     * @Param: [linkVos, id, nextId]
     * @return: void
     * @Author: zzj
     * @Date: 2018/3/25
     */
    public void getChildId(List<LinkVo> linkVos, String id, StringBuffer nextId) {
        for (LinkVo linkVo : linkVos) {
            if (id.equals(linkVo.getStartFigureId()) && ProcessConstant.TRUE.equals(linkVo.getIsChild())) {
                getNoChildId(linkVos, linkVo.getEndFigureId(), nextId);
            }
        }

    }

    /**
     * @Description: 获取子节点对应具体Id
     * @Param: [linkVos, id, nextId]
     * @return: void
     * @Author: zzj
     * @Date: 2018/3/25
     */
    public void getNoChildId(List<LinkVo> linkVos, String id, StringBuffer nextId) {
        for (LinkVo linkVo : linkVos) {
            if (id.equals(linkVo.getStartFigureId()) && ProcessConstant.FALSE.equals(linkVo.getIsChild())) {
                if (linkVo.getEndFigureId().indexOf(ProcessConstant.FLOWCELL) >= 0 || linkVo.getEndFigureId().indexOf(ProcessConstant.SEQUENCE) >= 0) {
                    this.getChildId(linkVos, linkVo.getEndFigureId(), nextId);
                } else {
                    nextId.append(linkVo.getEndFigureId() + ",");
                }
            }
        }
    }

    /**
     * @Description: 获取输入输出参数
     * @Param: [listElement, inPutParams, outPutParams]
     * @return: void
     * @Author: zzj
     * @Date: 2018/3/26
     */
    public void getParam(List<Element> listElement, List<Map> inPutParams) {
        for (Element e : listElement) {
            if (ProcessConstant.INPUTPARAMETER.equals(e.attributeValue(ProcessConstant.TYPE))) {
                Map intputMap = new HashMap(20);
                List<Element> next = e.elements();
                getParamMap(next, inPutParams, intputMap);
                inPutParams.add(intputMap);

            }
            if (ProcessConstant.OUTPUTPARAMETER.equals(e.attributeValue(ProcessConstant.TYPE))) {
                Map outputMap = new HashMap(20);
                List<Element> next = e.elements();
                getParamMap(next, inPutParams, outputMap);
                inPutParams.add(outputMap);
            } else {
                List<Element> next = e.elements();
                this.getParam(next, inPutParams);
            }
        }


    }

    public void getOutParam(List<Element> listElement, List<Map> outPutParams) {
        for (Element e : listElement) {
            if (ProcessConstant.OUTPUTPARAMETER.equals(e.attributeValue(ProcessConstant.TYPE))) {
                Map outputMap = new HashMap(20);
                List<Element> next = e.elements();
                getParamMap(next, outPutParams, outputMap);
                outPutParams.add(outputMap);
            } else {
                List<Element> next = e.elements();
                this.getOutParam(next, outPutParams);
            }
        }


    }

    /**
     * @Description:获取参数Map
     * @Param: [listElement, maps, map]
     * @return: void
     * @Author: zzj
     * @Date: 2018/3/26
     */
    public void getParamMap(List<Element> listElement, List<Map> maps, Map map) {
        for (Element e : listElement) {
            List<Element> attributes = e.elements();
            for (Element attribute : attributes) {
                //当前节点的所有属性的list
                List<Attribute> listAttr = attribute.attributes();
                //遍历当前节点的所有属性
                for (Attribute attr : listAttr) {
                    //属性名称
                    String name = attr.getName();
                    //属性的值
                    String value = attr.getValue();
                    map.put(name, value);
                }

            }

        }
    }

    /**
     * @Description: 获取输入参数
     * @Param: [listElement, commonParameters, set]
     * @return: void
     * @Author: zzj
     * @Date: 2018/3/26
     */
    public void getReceive(String xmlStr, List<List<CommonParameter>> commonParameters, Set<String> set, List<String> cellIds) {
        try {
            Document document = DocumentHelper.parseText(xmlStr);
            //获取跟节点
            Element root = document.getRootElement();
            //所有一级子节点的list
            List<Element> listElement = root.elements();
            getReceive(listElement, commonParameters, set, cellIds);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void getReceive(List<Element> listElement, List<List<CommonParameter>> commonParameters, Set<String> set, List<String> cellIds) {
        for (Element e : listElement) {
            if (ProcessConstant.SERVICE.equals(e.attributeValue(ProcessConstant.TYPE))) {
                List<Map> outPutParams = new ArrayList<Map>();
                List<Element> next = e.elements();
                this.getParam(next, outPutParams);
                List<CommonParameter> commonParameterList = new ArrayList<CommonParameter>();
                String cellId = e.attributeValue(ProcessConstant.SERVICEID);
                for (Map map : outPutParams) {
                    CommonParameter commonParameter = new CommonParameter();
                    commonParameter.setCellId(cellId.substring(0, cellId.length() - 5));
                    commonParameter.setDataID((String) map.get(ProcessConstant.DATAID));
                    commonParameter.setParameterName((String) map.get(ProcessConstant.PARAMERTNAME));
                    commonParameter.setParameterType((String) map.get(ProcessConstant.PARAMERTTYPE));
                    commonParameter.setParameterDesc((String) map.get(ProcessConstant.PARAMERTERDESC));
                    commonParameter.setGroup(e.attributeValue(ProcessConstant.LABEL));
                    commonParameter.setUuid((String) map.get(ProcessConstant.uuid));
                    commonParameterList.add(commonParameter);
                    set.add(cellId.substring(0, cellId.length() - 5));

                }
                set.add(cellId.substring(0, cellId.length() - 5));
                cellIds.add(cellId.substring(0, cellId.length() - 5));
                commonParameters.add(commonParameterList);
            }
            if (ProcessConstant.FLOW.equals(e.attributeValue(ProcessConstant.TYPE)) ||
                    ProcessConstant.Sequence.equals(e.attributeValue(ProcessConstant.TYPE))) {
                List<Element> next = e.elements();
                getReceive(next, commonParameters, set, cellIds);

            }
        }

    }
}

