package com.htht.job.executor.hander.shardinghandler;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.util.ProductUtil;

import org.htht.util.DataTimeHelper;
import com.htht.job.core.util.DateUtil;
import org.htht.util.FileUtil;
import com.htht.job.core.util.MatchTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;


@Service("ARMProductHandlerShard")
public class ARMProductHandlerShard extends StandardShard  {

}
