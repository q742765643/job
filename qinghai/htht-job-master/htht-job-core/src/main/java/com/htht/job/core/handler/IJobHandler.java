package com.htht.job.core.handler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;

/**
 * remote job handler
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class IJobHandler {

	/**
	 * job handler
	 * @return
	 * @throws Exception
	 */
	public abstract ReturnT<String> execute( TriggerParam triggerParam ) throws Exception;
	
}
