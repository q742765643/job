package com.htht.job.executor.hander;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.CmdMessage;
import com.htht.job.core.util.ExecProcess;
import com.htht.job.core.util.ResultUtil;

import com.htht.job.core.util.ScriptUtil;
import com.htht.job.executor.model.product.ProductDTO;
import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.util.DubboIpUtil;
import com.htht.job.executor.util.RedLineMatchTime;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by zzj on 2018/1/31.
 */
@JobHandler(value = "redlineHandler")
@Service
public class RedlineHandler extends IJobHandler {
    private static final String RESULT_START = "<result>";
    private static final String RESULT_END = "</result>";
    private static final String windows = "Z:";
    private static final String linux = "/RSData6";

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductFileInfoService productFileInfoService;

    @Value("${cluster.job.executor.logpath}")
    private String logpath;
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result=new 	ResultUtil();
        String deploysyetem= DubboIpUtil.getOsName();
        LinkedHashMap fixmap = triggerParam.getFixedParameter();
        LinkedHashMap dymap=triggerParam.getDynamicParameter();

        String exePath = (String) fixmap.get("执行路径");
        List<String> output=new ArrayList<String>();
        String param="";
      Iterator<Map.Entry<String, Object>> it = dymap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if(entry.getValue()==null){
                if(exePath.indexOf("AlgoLoader")>=0){
                    param+= "|";
                }else if(exePath.indexOf(".jar")>=0){
                    param+= "\"\""+" ";
                }else{
                    param+=" ";
                }


            }else{
                if(exePath.indexOf("AlgoLoader")>=0){
                    if(deploysyetem.equals("windows")){
                        param+= entry.getValue().toString().replaceAll(linux,windows).replaceAll("/", "\\\\")+"|";
                    }else{
                        param+= entry.getValue().toString().replaceAll(windows,linux).replaceAll("\\\\","/")+"|";
                    }
                }else {
                    if(deploysyetem.equals("windows")) {
                        param += entry.getValue().toString().replaceAll(linux, windows).replaceAll("/", "\\\\") + " ";
                    }else{
                        param += entry.getValue().toString().replaceAll(windows, linux).replaceAll("\\\\", "/") + " ";
                    }
                }
            }
        }
        if(exePath.indexOf(".jar")>=0){
            exePath ="java -Dfile.encoding=utf-8 -jar "+exePath +" "+param;
        }else if(exePath.indexOf("AlgoLoader")>=0){
            String[] expaths=exePath.split(" ");
            exePath="\""+expaths[0]+"\""+" "+triggerParam.getParallelLogId()+" "+expaths[1]+" "+"\""+param.substring(0,param.length()-1)+"\"";
        }else{
            exePath=exePath +" "+param;
        }
        String outputLog=triggerParam.getLogFileName();

        XxlJobLogger.logByfileNoname(outputLog, exePath);
        int exitValue=ScriptUtil.execCmd(exePath,outputLog);
        if(exePath.indexOf(".jar")>=0){
            if(exitValue==0){
                String out =XxlJobFileAppender.readLogResult(outputLog);
                List<String> results = new ArrayList<String>();
                this.addResult(out, results);
                triggerParam.setOutput(results);

            }else{
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, triggerParam.getParallelLogId());
                return stopResult;
            }

        }else if(exePath.indexOf("AlgoLoader")>=0){
            if (exitValue == 0) {
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, triggerParam.getParallelLogId());
                return stopResult;

            }
        }else{
            if(exitValue != 0){
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, triggerParam.getParallelLogId());
                return stopResult;
            }
        }
        String  xmlpath = (String) dymap.get("输出日志文件路径");
        List<String> list  = new ArrayList<String>();
        parsingXml(xmlpath, result, list);
        String fileTiffPath = list.get(0);
		String fileJpgPath = fileTiffPath.replace("RCUR", "ZCUR").replace(".tif", ".jpg");
		String[] filePaths = new String[]{fileTiffPath,fileJpgPath};
		for (String filePath : filePaths) {
			
			File file = new File(filePath);
			String fileName = file.getName();
			String[] info = fileName.split("_");
//		VGT_NDVI_RCUR_201707240000_COFD_FY4A_AGRI_600000000000.jpg
			ProductFileInfoDTO productFileInfoDTO = new ProductFileInfoDTO();

			productFileInfoDTO.setFilePath(filePath);
			

			
			//区域和文件类型获取
			String[] regionAndFileType = info[info.length-1].split("\\.");
//			productFileInfo.setRegion(regionAndFileType[0]);
			productFileInfoDTO.setFileType(regionAndFileType[1]);
			
			//产品id和目录id获取
			ProductDTO productDTO = productService.findById(triggerParam.getProductId());
//TODO			productFileInfo.setProductId(product.getId());
//			productFileInfo.setMenuId(product.getMenuId());
//			productFileInfo.setCycle(product.getCycle());
			productFileInfoDTO.setProductType(productDTO.getMark());
			//日期匹配
			String matchIssue = RedLineMatchTime.matchIssue(info[3], productDTO.getCycle());
//			productFileInfo.setIssue(matchIssue);
			
			
			productFileInfoDTO.setIsDel("0");
			//如果产品已经存在，更新产品信息
			List<ProductFileInfoDTO> existProduct = productFileInfoService.findByWhereAndRegion(productDTO.getMark(), matchIssue, regionAndFileType);
			if (existProduct.size()>0) {
				productFileInfoDTO.setId(existProduct.get(0).getId());
			}
			
			productFileInfoService.save(productFileInfoDTO);
		}
        return ReturnT.SUCCESS;
    }

    public void addResult(String newMsg,List<String> results){
        if(newMsg.indexOf(RESULT_START) != -1 && newMsg.indexOf(RESULT_END) != -1){
            int startIndex = 0,endIndex = 0;
            while (newMsg != null && newMsg.length() > 0 && newMsg.indexOf(RESULT_END) > 0) {
                startIndex = newMsg.indexOf(RESULT_START) + RESULT_START.length();
                endIndex = newMsg.indexOf(RESULT_END);
                // returnResult
                String returnResult = newMsg.substring(startIndex,endIndex);
                results.add(returnResult);
                // 移除第一个参数值
                newMsg = newMsg.substring(newMsg.indexOf(RESULT_END) + RESULT_END.length());
            }
        }
    }
	  public static ResultUtil<String> parsingXml(String outputxml,ResultUtil<String> result,List<String> list){
			try {
				File f=new File(outputxml);
				if (!f.exists()||f.length()==0){
					result.setErrorMessage("outxml文件不存在");
					return result;
				}
				//创建SAXReader对象
				SAXReader reader = new SAXReader();
				//读取文件 转换成Document
				Document document = reader.read(f);
				//获取根节点元素对象
				Element root = document.getRootElement();
				if (root==null){
					result.setErrorMessage("outxml根节点获取错误");
					return result;
				}
				Element log = root.element("log");
				if (log==null){
					result.setErrorMessage("outxml log节点获取错误");
					return result;
				}
				String loginfo=log.elementText("ret");
				String[] split = loginfo.split(",");
				if(!"success".equals(split[0])){
					result.setErrorMessage("outxml loginfo节点获取错误");
					return result;
				}
				list.add(split[1]);

			} catch (Exception e) {
				 throw new RuntimeException();
			}
			return  result;
		}
    public static void main(String[] args) throws IOException {
        ExecProcess processor = new ExecProcess();
        //CmdMessage cmdMsg = processor.execCmd("ping 127.0.0.1",true,"/zzj/data/logs/111.log");
        CmdMessage cmdMsg = processor.execCmd("java -jar /zzj/data/logs/Demo.jar 111 1111",true,"/zzj/data/logs/111.log");
        //ScriptUtil.execCmd("java -jar /zzj/data/logs/Demo.jar 111 1111","/zzj/data/logs/111.log");
    }
}
