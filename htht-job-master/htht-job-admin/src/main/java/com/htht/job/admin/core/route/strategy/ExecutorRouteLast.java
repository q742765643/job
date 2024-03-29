package com.htht.job.admin.core.route.strategy;

import com.htht.job.admin.core.route.ExecutorRouter;
import com.htht.job.admin.core.trigger.XxlJobTrigger;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLast extends ExecutorRouter {

    public String route(List<String> addressList) {
        return addressList.get(addressList.size() - 1);
    }

    @Override
    public ReturnT<String> routeRun(TriggerParam triggerParam, List<String> addressList) {
        // address
        String address = route(addressList);

        // run executor
        ReturnT<String> runResult = XxlJobTrigger.runExecutor(triggerParam, address);
        runResult.setContent(address);
        return runResult;
    }
}
