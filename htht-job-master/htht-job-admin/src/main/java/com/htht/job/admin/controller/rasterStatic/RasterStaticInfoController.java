package com.htht.job.admin.controller.rasterStatic;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.DubboShiroService;
import com.htht.job.executor.model.uus.RegionInfo;

/**
 * 
 * @author HG
 *
 *         2018年11月21日 下午2:26:17
 */
@Controller
@RequestMapping("/rasterStatic")
public class RasterStaticInfoController {
	@Autowired
	private DubboShiroService dubboShiroService;
	@Autowired
	private DubboService dubboService;

	@RequestMapping
	public String index(Model model) {
		return "/rasterStatic/rasterStatic.index";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0")int start, 
										@RequestParam(required = false, defaultValue = "10")int length, 
										String productId, String level, String cycle,
										String startDate, String endDate, String regionId) {
		Map<String, Object> result = new HashMap<>();
		Map<String, HashMap<String, String>> statisticMap = new TreeMap<>((str1, str2)->str1.compareTo(str2));
		Date startTime = null;
		Date endTime = null;
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
			try {
				startTime = DateUtils.parseDate(startDate, new String[] { "yyyy-MM-dd HH:mm:ss" });
				endTime = DateUtils.parseDate(endDate, new String[] { "yyyy-MM-dd HH:mm:ss" });
			} catch (ParseException e) {
			}
		}
		if (null != startDate && null != endDate) {
		}
		List<Object[]> statisticList= dubboService.rasterStaticList(productId, regionId, Integer.parseInt(level), cycle, startTime, endTime);
		for(Object[] objects : statisticList) {
			String issue = objects[0].toString();
			HashMap<String, String> temp = statisticMap.get(issue);
			if(temp == null) {
				temp = new HashMap<>();
				statisticMap.put(issue, temp);
				temp.put("issue", issue);
			}
			temp.put(objects[1].toString(), objects[2].toString());
		}
		List<Object> list = new ArrayList<>(statisticMap.values());
		result.put("data", new ArrayList<Object>(statisticMap.values()));
		result.put("recordsTotal", list.size());
		result.put("recordsFiltered", list.size());
		return result;
	}

	private List<RegionInfo> getRegionList(@RequestParam(required = false, defaultValue = "370000000000")String regionId) {
		List<RegionInfo> regionList = dubboShiroService.findAllRegionInfo();
		List<RegionInfo> result = new ArrayList<>();
		for (RegionInfo region : regionList) {
			if ((regionId.equals(region.getRegionId()) || regionId.equals(region.getParentRegionId())) && !result.contains(region)) {
				result.add(region);
			}
		}
		return result;
	}

	@RequestMapping("/regionList")
	@ResponseBody
	public Map<String, Object> regionList(
			@RequestParam(required = false, defaultValue = "370000000000") String regionId) {
		Map<String, Object> maps = new HashMap<>();
		maps.put("data", this.getRegionList(regionId));
		return maps;
	}
	@RequestMapping("/statisticTypeList")
	@ResponseBody
	public Map<String, Object> regionList() {
		Map<String, Object> map = new HashMap<>();
		List<Object> statisticList= dubboService.statisticTypeList();
		map.put("data", statisticList);
		return map;
	}
	
}
