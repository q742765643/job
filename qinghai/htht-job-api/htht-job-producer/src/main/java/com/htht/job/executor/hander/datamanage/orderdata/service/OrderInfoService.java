package com.htht.job.executor.hander.datamanage.orderdata.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.hander.datamanage.orderdata.dao.OrderInfoDao;
import com.htht.job.executor.hander.datamanage.orderdata.module.OrderInfo;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.dms.util.db.QueryCondition;

/**
 * 
 * @author LY 2018-05-07
 * 
 */
@Transactional
@Service("orderInfoService")
public class OrderInfoService extends BaseService<OrderInfo> {
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	@Autowired
	private OrderInfoDao orderInfoDao;
	@PersistenceContext
	protected EntityManager em;

	@Override
	public BaseDao<OrderInfo> getBaseDao() {
		return orderInfoDao;
	}

	public List<OrderInfo> findAll() {
		return this.getAll();
	}

	/**
	 * 按照id查询数据
	 */
	// public OrderInfo getById(String id) {
	// return orderInfoDao.findOne(id);
	// }
	/**
	 * 按照id查询数据
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public OrderInfo getById(String id) {

		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition("f_id", QueryCondition.EQ, id));
		List<OrderInfo> orderInfo = baseDaoUtil.get(OrderInfo.class, queryConditions);
		return orderInfo.size() > 0 ? orderInfo.get(0) : null;
	}

	/**
	 * 修改OrderInfo实体信息
	 * 
	 * @param OrderInfo
	 *            实体对象
	 * @return 返回1 修改成功 0 修改失败
	 * @author LY 2018/05/07
	 */
	public int update(OrderInfo OrderInfo) {
		int num = 1;
		try {
			orderInfoDao.saveAndFlush(OrderInfo);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 删除OrderInfo实体信息
	 * 
	 * @param id
	 *            需要删除的实体主键
	 * @return 1 删除成功 0删除失败
	 * @author LY 2018-05-07
	 */
	public int del(String id) {
		int num = 1;
		try {
			orderInfoDao.delete(id);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 获取所有未提取的数据
	 * 
	 * @return
	 */
	public List<OrderInfo> getData(String orderIds) {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" f_isexist = 0 and f_orderid in (" + orderIds + ") "));

		List<OrderInfo> orderInfo = baseDaoUtil.get(OrderInfo.class, queryConditions);

		return orderInfo;
	}

	/**
	 * 根据订单ID和数据状态获取数据提取信息
	 * 
	 * @return
	 */
	public List<OrderInfo> getOrderInfo(String orderid, String state) {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" f_orderid='" + orderid + "' and f_isexist=" + state + " "));
		try {
			List<OrderInfo> orderInfo = baseDaoUtil.get(OrderInfo.class, queryConditions);
			return orderInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
