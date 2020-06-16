package com.htht.job.executor.hander.datamanage.orderdata.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.hander.datamanage.orderdata.dao.OrderDao;
import com.htht.job.executor.hander.datamanage.orderdata.module.Order;
import com.htht.job.executor.hander.datamanage.orderdata.module.OrderInfo;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.dms.util.db.QueryCondition;

/**
 * 
 * @author LY 2018-05-07
 * 
 */
@Transactional
@Service("orderService")
public class OrderService extends BaseService<Order> {
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	@Autowired
	private OrderDao orderDao;
	@PersistenceContext
	protected EntityManager em;
	@Autowired
	private OrderInfoService orderInfoService;
	
	@Override
	public BaseDao<Order> getBaseDao() {
		return orderDao;
	}

	public List<Order> findAll() {
		return this.getAll();
	}

	/**
	 * 按照id查询数据
	 */
//	public Order getById(String id) {
//		return orderDao.findOne(id);
//	}
	/**
	 * 按照id查询数据
	 * 
	 * @param id
	 *            
	 * @return
	 */
	public Order getById(String id) {
		
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition("f_id", QueryCondition.EQ, id));
		List<Order> order = baseDaoUtil.get(Order.class, queryConditions);
		return order.size() > 0 ? order.get(0) : null;
	}
	/**
	 * 修改Order实体信息
	 * 
	 * @param Order
	 *            实体对象
	 * @return 返回1 修改成功 0 修改失败
	 * @author LY 2018/05/07
	 */
	public int update(Order Order) {
		int num = 1;
		try {
			orderDao.saveAndFlush(Order);
		} catch (Exception e) {
			num = 0;
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 删除Order实体信息
	 * 
	 * @param id
	 *            需要删除的实体主键
	 * @return 1 删除成功 0删除失败
	 * @author LY 2018-05-07
	 */
	public int del(String id) {
		int num = 1;
		try {
			orderDao.delete(id);
		} catch (Exception e) {
			num = 0;
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 根据订单订单状态获取数据提取信息
	 * 
	 * @return
	 */
	public List<Order> getOrder(String state) {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" f_orderstate in ("+state+") "));

		List<Order> order = baseDaoUtil.get(Order.class, queryConditions);

		return order;
	}
	
	/**
	 * 根据订单ID更新订单数据提取信息
	 * @param orderid
	 */
	public void updateOrderInfo(String orderid) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 待提取数据
		List<OrderInfo> list = orderInfoService.getOrderInfo(orderid, "0");
		// 正在提取的数据
		List<OrderInfo> list10 = orderInfoService.getOrderInfo(orderid, "10");
		// 已提取数据
		List<OrderInfo> completelist = orderInfoService.getOrderInfo(orderid, "1");
		// 提取失败数据
		List<OrderInfo> errorlist = orderInfoService.getOrderInfo(orderid, "2");
		// 当前订单对象
		Order order = this.getById(orderid);
		if(order.getF_orderstateinfo().indexOf("审核通过") == -1) {
			return ;
		}
		order.setF_orderstate(3);// 默认正在提取数据的都是状态3
		if(order.getF_orderstateinfo().indexOf("数据提取开始") == -1) {
			order.setF_orderstateinfo(
					order.getF_orderstateinfo().substring(0,order.getF_orderstateinfo().indexOf("审核通过")+4)
							+ '\n'+sdf.format(new Date())+"  "
							+ "数据提取开始"
							+ '\n'+sdf.format(new Date())+"  "
							+ "数据共【"+(list.size()+list10.size()+completelist.size()+errorlist.size())+"】个、"
//							+ "待提取【"+list.size()+"】个、"
							+ "成功【"+completelist.size()+"】个、"
							+ "失败【"+errorlist.size()+"】个");
		} else {
			System.out.println("==================================="+order.getF_orderstateinfo().substring(0,order.getF_orderstateinfo().indexOf("数据提取开始")+6));
			order.setF_orderstateinfo(
					order.getF_orderstateinfo().substring(0,order.getF_orderstateinfo().indexOf("数据提取开始")+6)
							+ '\n'+sdf.format(new Date())+"  "
							+ "数据共【"+(list.size()+list10.size()+completelist.size()+errorlist.size())+"】个、"
//							+ "待提取【"+list.size()+"】个、"
							+ "成功【"+completelist.size()+"】个、"
							+ "失败【"+errorlist.size()+"】个");
		}
		
		// 如果失败+成功=订单数据量 则代表订单数据提取结束
		if((completelist.size()+errorlist.size() == order.getF_datacount())) {
			order.setF_orderstateinfo(order.getF_orderstateinfo()+'\n'+sdf.format(new Date())+"  "+"数据提取结束");
			order.setF_orderstate(4);
			order.setF_ordercompleted(new Date());
		}
		this.update(order);
	}
	
	/**
	 * 获取过期订单
	 * 
	 * @return
	 */
	public List<Order> getOverdueOrder(String day) {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" ((f_orderCompleted IS NOT NULL AND TO_DAYS(NOW()) - TO_DAYS(f_orderCompleted) > "+day+") OR TO_DAYS(NOW()) - TO_DAYS(f_orderDate) > ("+day+" + 5) ) AND f_orderState IN (4, 5, 7, 9, 10) "));

		List<Order> order = baseDaoUtil.get(Order.class, queryConditions);

		return order;
	}
}
