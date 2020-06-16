package com.htht.job.admin.controller.productfileinfo;

import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.util.WriteToHtml;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.fileinfo.FileInfoDTO;
import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/3.
 */
@Controller
@RequestMapping("/productfileinfo")
public class ProductFileInfoController {
    private static Logger logger = LoggerFactory.getLogger(ProductFileInfoController.class);

    @Autowired
    private DubboService dubboService;

    @RequestMapping
    public String index(Model model) {
        return "/productfileinfo/productfileinfo.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            ProductFileInfoDTO productFileInfoDTO) {
        if (start != 0) {
            start = start / length;
        }
        return dubboService.pageListProductFileInfo(start, length,
                productFileInfoDTO);
    }

    @RequestMapping("/pageLists")
    @ResponseBody
    public Map<String, Object> pageLists(
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            String id) {
        if (start != 0) {
            start = start / length;
        }
        return dubboService.pageListProductFileInfos(start, length, id);
    }

    @RequestMapping("/getFileUrls")
    @ResponseBody
    public DictCodeDTO getFileUrls(String dictName) {
        // 字典中获取ip:端口 其中关键字为fileUrl
        return dubboService.findOneselfDictCode(dictName);
    }

    @RequestMapping("/pageListProductInfo")
    @ResponseBody
    public Map<String, Object> pageList(
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            String menuId, String productType, String issue) {
        if (start != 0) {
            start = start / length;
        }

        if (StringUtils.isEmpty(menuId)) {
            return dubboService.pageListProductInfo(start, length, "");
        } else {
            if (ObjectUtils.isEmpty(dubboService.findByTreeId(menuId))) {
                Map<String, Object> maps = new HashMap<>();
                maps.put("recordsTotal", 0); // 总记录数
                maps.put("recordsFiltered", 0); // 过滤后的总记录数
                maps.put("data", new ArrayList<ProductInfoDTO>()); // 分页列表
                return maps;
            } else {
                return dubboService.pageListProductInfo(start, length,
                        dubboService.findByTreeId(menuId).getId());
            }
        }
    }

    @RequestMapping("/pageListProductFileInfo")
    @ResponseBody
    public Map<String, Object> pageListProductFileInfo(
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            String id) {
        if (start != 0) {
            start = start / length;
        }
        return dubboService.pageListProductFileInfos(start, length, id);
    }

    @RequestMapping("/saveProductFileInfo")
    @ResponseBody
    public ReturnT<String> saveProductFileInfo(ProductFileInfoDTO productFileInfoDTO) {
        ProductFileInfoDTO p = dubboService.saveProductFileInfo(productFileInfoDTO);
        if (null != p.getId()) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    @RequestMapping("/deleteProductFileInfo")
    @ResponseBody
    public ReturnT<String> deleteProductFileInfo(String id) {
        dubboService.deleteProductFileInfo(id);
        return ReturnT.SUCCESS;

    }

    @RequestMapping("/deleteProductInfo")
    @ResponseBody
    public ReturnT<String> deleteProductInfo(String id) {
        dubboService.deleteProductInfo(id);
        dubboService.deleteProductFileInfo(id);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/findFileInfoByWhere/{id}")
    @ResponseBody
    public List<FileInfoDTO> findFileInfoByWhere(@PathVariable String id) {
        List<FileInfoDTO> fileInfoDTOS;
        fileInfoDTOS=dubboService.findFileInfoByWhere(id);
        return fileInfoDTOS;
    }

    @RequestMapping("/htmlToStr")
    @ResponseBody
    public Map<String, String> testSendMessage(String filepath) throws IOException {
        Map<String, String> map = new HashMap<>();
        InputStream is;
        try {
            is = new FileInputStream(filepath.replace("doc", "html"));
            String htmlStr = IOUtils.toString(is, "utf-8");
            String c = "<body style=\"max-width:600px; margin: 0 auto;\">";
            htmlStr = htmlStr.substring(htmlStr.indexOf("<body style=\"max-width:600px; margin: 0 auto;\">") + c.length(), htmlStr.lastIndexOf("</body>"));
            DictCodeDTO dic = dubboService.findOneselfDictCode("productionPath");
            DictCodeDTO dict = dubboService.findOneselfDictCode("fileUrl");
            htmlStr = htmlStr.replace(dic.getDictCode(), dict.getDictCode());
            map.put("html", htmlStr);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        }
        return map;
    }

    @RequestMapping("/HtmlToWord")
    @ResponseBody
    public Map<String, String> stringtoHtml(HttpServletRequest req, HttpServletResponse resp, String filepath, String data) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码
        filepath = filepath.replace("doc", "html");
        File file = new File(filepath);
        Map<String, String> map = new HashMap<>();
        WriteToHtml.writeToHtml(data, filepath);
        String name = file.getName();
        String docName = WriteToHtml.saveHtmlToWord(file.getParent() + "\\", name, name.replace("html", "doc"));
        map.put("docName", docName);
        return map;
    }
}
