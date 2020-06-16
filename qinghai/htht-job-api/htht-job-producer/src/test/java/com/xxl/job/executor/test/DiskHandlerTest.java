package com.xxl.job.executor.test;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.Application;
import com.htht.job.executor.hander.dataarchiving.handler.service.DiskHandlerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by LY on 2018/4/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DiskHandlerTest {
	@Autowired
	private DiskHandlerService diskHandlerService;

	@Test
	public void testDisk() {
		ResultUtil<String> result = new ResultUtil<String>();
		ResultUtil<String> resultUtil = diskHandlerService.execute(new TriggerParam(), result);
		System.out.print(resultUtil);
	}
}
