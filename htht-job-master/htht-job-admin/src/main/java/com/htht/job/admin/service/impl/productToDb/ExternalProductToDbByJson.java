package com.htht.job.admin.service.impl.productToDb;

import com.htht.job.admin.service.ProductToDbService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author HG
 *         <p>
 *         2018年11月13日 上午10:03:18
 */
@Service("externalProductToDbByJson")
public class ExternalProductToDbByJson implements ProductToDbService {
    @Resource
    private DubboService dubboService;

    @Override
    public ReturnT<String> toDbByJson(String json) {
        ReturnT<String> returnT = new ReturnT<String>();
        Object object = new JSONTokener(json).nextValue();
        if (object instanceof JSONArray) {
            JSONArray jsa = (JSONArray) object;
            returnT = toDbByJson(jsa, returnT);
        } else if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            returnT = toDbByJson(jsonObject, returnT);
        } else {
            returnT.setCode(ReturnT.FAIL_CODE);
            returnT.setContent("smart入库失败");
            returnT.setMsg("入库JSON不符合规范：" + json);
        }
        return returnT;
    }

    public ReturnT<String> toDbByJson(JSONArray jsonArray, ReturnT<String> returnT) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            returnT = toDbByJson(jsonObject, returnT);
        }
        return returnT;
    }

    public ReturnT<String> toDbByJson(JSONObject jsonObject, ReturnT<String> returnT) {
        return dubboService.smartToDbByJson(jsonObject.toString(), returnT);
    }

    @Override
    public ReturnT<String> toDb(String startIssue, String endIssue, String productType) {
        return null;
    }
}
