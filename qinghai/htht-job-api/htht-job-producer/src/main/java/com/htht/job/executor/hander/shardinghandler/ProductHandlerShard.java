package com.htht.job.executor.hander.shardinghandler;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import com.htht.job.executor.util.DateUtil;

import org.htht.util.Consts;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yuguoqing
 * @Date 2018年4月23日 下午2:33:14
 *
 */
@Service("productHandlerShard")
public class ProductHandlerShard implements SharingHandler
{

	/**
	 * 查询待预处理的文件 将待预处理的文件提交至调度中心
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap)
	{
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();

		String startDate = "";
		String endDate = "";
		ProductParam productParam = null;
		try
		{
			productParam = JSON.parseObject(params, ProductParam.class);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		if (null == productParam)
		{
			return result;
		}

		if ("1".equals(productParam.getDateType()))// 实时数据
		{
			startDate = org.htht.util.DateUtil.getCurrentDateStringWithOffset(Consts.DateForMat.yyMMddFormatSplited,
					-1 * Integer.parseInt(productParam.getProductRangeDay()));
			endDate = org.htht.util.DateUtil.getCurrentDateString(Consts.DateForMat.yyMMddFormatSplited);
		} else
		{
			String dateStr[] = productParam.getProductRangeDate().split(" - ");
			if (2 == dateStr.length)
			{
				startDate = dateStr[0];
				endDate = dateStr[1];
			}
		}

		List<Date> list = DateUtil.getHistoryDatelist(startDate, endDate);
		List<String> dateList = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < list.size(); i++)
		{
			org.htht.util.MatchTime matchTimeUtil = new org.htht.util.MatchTime();
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			
			String dateStr = sf.format(list.get(i));
			String date = matchTimeUtil.matchIssue(dateStr, (String)dymap.get("period"));
			set.add(date);
		}
		dateList.addAll(set);
		result.setResult(dateList);
		return result;
	}
public static void main(String[] args)
{
	org.htht.util.MatchTime matchTimeUtil = new org.htht.util.MatchTime();
System.out.println(matchTimeUtil.matchIssue("20180422","COTD"));
}
}