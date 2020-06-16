//package com.htht.job.admin.controller.brace;
//
//import com.htht.job.core.api.DubboDbmsService;
//import com.htht.job.core.api.DubboShiroService;
//import com.htht.job.core.biz.model.ReturnT;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
///**
// * @program: htht-job-api
// * @description: 角色管理
// * @author: fuyanchao
// * @create: 2018-09-03 11:22
// */
//@Controller
//@RequestMapping("/brace")
//public class BUserInfoController {
//
//    @Autowired
//    private DubboShiroService dubboShiroService;
//
//    @RequestMapping("/user")
//    public String UserInfo() {
//        return "brace/user";
//    }
//
//    @RequestMapping(value = "/user/delUser/{id}", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
//    @ResponseBody
//    public ReturnT<String> delUser(@PathVariable String id) {
//        try {
//            dubboShiroService.deleteUser(id);
//            return ReturnT.SUCCESS;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ReturnT.FAIL;
//        }
//    }
//}
