package com.htht.job.admin.controller.dms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.dms.module.ArchiveCatalog;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.module.MeataImgAndInfo;

/**
 * @author: yss
 * @time:2018年10月22日 上午10:39:10
 */
@Controller
@RequestMapping("/SatelliteInformation")
public class SatelliteInformationController {
	@Autowired
	private DubboService dubboService;

	@RequestMapping
	public String index() {
		return "/dms/satellite_information.index";
	}

	// 获取数据目录树
	@RequestMapping("/getArchiveCatalog")
	@ResponseBody
	public String ArchiveCatalog() {
		List<ArchiveCatalog> archiveCatalogs;
		archiveCatalogs = dubboService.findArchiveCatalogs();
		return JSON.toJSONString(archiveCatalogs);
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "10") int length, String catalogcode, String fProducetime,
			String fLevel,@RequestParam(required = false, defaultValue = "0")Integer fCloudamount,String fSatelliteid) {
		// parameterModel.setModelName("1");
		if (start != 0) {
			start = start / length;
		}
		Date fProducetimeStart = null;
		Date fProducetimeEnd = null;
		String[] temp = fProducetime.split(" - ");
		if (StringUtils.isNotBlank(fProducetime)) {

			if (temp != null && temp.length == 2) {
				try {
					fProducetimeStart = DateUtils.parseDate(temp[0], new String[] { "yyyy-MM-dd HH:mm:ss" });
					fProducetimeEnd = DateUtils.parseDate(temp[1], new String[] { "yyyy-MM-dd HH:mm:ss" });
				} catch (ParseException e) {
				}
			}
		}
		Map<String, Object> map = dubboService.pageListMeataImgAndInfo(start, length, catalogcode, fProducetimeStart,
				fProducetimeEnd,fLevel,fCloudamount,fSatelliteid);

		return map;
	}

	// 更改回收状态
	@RequestMapping("/updateRecycleflag/{mark}/{id}")
	@ResponseBody
	public ReturnT<String> updateRecycleflag(@PathVariable("mark") String mark, @PathVariable("id") String id) {
		try {
			dubboService.updateRecycleflag(mark, id);
			return ReturnT.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

}
