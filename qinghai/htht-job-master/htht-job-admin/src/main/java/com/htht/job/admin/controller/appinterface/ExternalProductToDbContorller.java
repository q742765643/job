package com.htht.job.admin.controller.appinterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.htht.job.admin.service.ProductToDbService;
import com.htht.job.core.biz.model.ReturnT;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 * 提供给外部程序的产品入库接口
 * @author Administrator
 * 该接口需兼容所有系统，根据需求的不断更新，可能会出现不兼容的情况，需迭代完善
 */
@RestController
@RequestMapping("/api/externalProduct")
@Api(value = "/api/externalProduct", description = "提供给其他系统的产品入库接口")
public class ExternalProductToDbContorller {
	@Autowired
    private ProductToDbService externalProductToDbService;
    @RequestMapping(value = "/toDb", method = {RequestMethod.POST, RequestMethod.GET}, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "执行入库操作", notes = "输入12位开始期次和结束期次及产品类型,格式：yyyyMMddHHmm")
    public ReturnT<String> toDb(String startIssue, String endIssue, String productType) {
        return externalProductToDbService.toDb(startIssue, endIssue, productType);
    }
}
