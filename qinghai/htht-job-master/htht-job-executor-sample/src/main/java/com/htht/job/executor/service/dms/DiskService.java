package com.htht.job.executor.service.dms;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.dao.dms.DiskDao;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.module.SystemParam;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.dms.util.db.QueryCondition;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;

/**
 * 
 * @author LY 2018-03-29
 * 
 */
@Transactional
@Service("diskService")
public class DiskService extends BaseService<Disk> {
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	@Autowired
	private DiskDao diskDao;
	@PersistenceContext
	protected EntityManager em;

	@Override
	public BaseDao<Disk> getBaseDao() {
		return diskDao;
	}

	public List<Disk> findAll() {
		return this.getAll();
	}

	/**
	 * 按照id查询数据
	 */
	public Disk getById(String id) {
		return diskDao.findOne(id);
	}

	/**
	 * 修改Disk实体信息
	 * 
	 * @param disk
	 *            实体对象
	 * @return 返回1 修改成功 0 修改失败
	 * @author LY 2018/03/29
	 */
	public int update(Disk disk) {
		int num = 1;
		try {
			diskDao.save(disk);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 删除Disk实体信息
	 * 
	 * @param id
	 *            需要删除的实体主键
	 * @return 1 删除成功 0删除失败
	 * @author LY 2018-03-29
	 */
	public int del(String id) {
		int num = 1;
		try {
			diskDao.delete(id);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 查询可以使用的"扫描磁盘"
	 * 
	 * @return
	 */
	public List<Disk> getScanDisk() {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" diskstatus = 0 and disktype = 0 "));

		List<Disk> disks = baseDaoUtil.get(Disk.class, queryConditions);

		return disks;
	}

	/**
	 * 查询可以使用的"归档磁盘 "默认剩余使用率大于百分之二的是可使用磁盘
	 * 
	 * @return
	 */
	public List<Disk> getArchiveDisk() {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" usagerate > 2 and  diskstatus = 0 and disktype = 1 "));

		List<Disk> disks = baseDaoUtil.get(Disk.class, queryConditions, " order by create_time");

		return disks;
	}

	/**
	 * 查询可以使用的"图片磁盘 "默认剩余使用率大于百分之二的是可使用磁盘
	 * 
	 * @return
	 */
	public List<Disk> getImgDisk() {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" usagerate > 2 and  diskstatus = 0 and disktype = 2 "));

		List<Disk> disks = baseDaoUtil.get(Disk.class, queryConditions, " order by create_time");

		return disks;
	}

	/**
	 * 查询可以使用的"工作磁盘" 默认剩余使用率大于百分之二的是可使用磁盘
	 * 
	 * @return
	 */
	public List<Disk> getWorkDisk() {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" usagerate > 2 and  diskstatus = 0 and disktype = 3 "));

		List<Disk> disks = baseDaoUtil.get(Disk.class, queryConditions, " order by create_time");

		return disks;
	}

	/**
	 * 查询可以使用的"订单数据磁盘" 默认剩余使用率大于百分之二的是可使用磁盘
	 * 
	 * @return
	 */
	public List<Disk> getOrderDisk() {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" usagerate > 2 and  diskstatus = 0 and disktype = 4 "));

		List<Disk> disks = baseDaoUtil.get(Disk.class, queryConditions, " order by create_time");

		return disks;
	}

	/**
	 * 查询可以使用的"近线磁盘 "默认剩余使用率大于百分之二的是可使用磁盘
	 * 
	 * @return
	 */
	public List<Disk> getNearLineDisk() {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" usagerate > 2 and  diskstatus = 0 and disktype = 5 "));

		List<Disk> disks = baseDaoUtil.get(Disk.class, queryConditions, " order by create_time");

		return disks;
	}

	/**
	 * 查询可以使用的"离线磁盘 "默认剩余使用率大于百分之二的是可使用磁盘
	 * 
	 * @return
	 */
	public List<Disk> getOffLineDisk() {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" usagerate > 2 and  diskstatus = 0 and disktype = 6 "));

		List<Disk> disks = baseDaoUtil.get(Disk.class, queryConditions, " order by create_time");

		return disks;
	}

	/**
	 * 查找归档磁盘
	 */
	public List<Disk> findfileDisks() {
		SimpleSpecificationBuilder<Disk> builder = new SimpleSpecificationBuilder<Disk>();
		//builder.add("diskdesc", "likeAll", "归档磁盘").generateSpecification();
		builder.add("disktype", "eq", "1").generateSpecification();
		List<Disk> findAll = diskDao.findAll(builder.generateSpecification());
		return findAll;
	}

	// 查找归档磁盘列表
	public String diskList(int start, int length, String searchText) {
		SimpleSpecificationBuilder<Disk> builder = new SimpleSpecificationBuilder<Disk>();
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		PageRequest d = new PageRequest(start, length, sort);
		if (StringUtils.isNotBlank(searchText)) {
			builder.add("diskdesc", "likeAll", searchText);
		}
		/*
		 * if (!StringUtils.isEmpty(id)) { builder.addOr("parentId","eq",id); }
		 */
		Page<Disk> page = this.getPage(builder.generateSpecification(), d);
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("recordsTotal", page.getTotalElements()); // 总记录数
		maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
		maps.put("data", page.getContent()); // 分页列表
		return JSON.toJSONString(maps, SerializerFeature.WriteMapNullValue);
	}

	public void saveOrUpdateDisk(Disk disk) {
		File dir = null;
		dir = new File(disk.getLoginurl());
		Long mindiskfreesize = unitToByte(disk.getUnitType(), disk.getMindiskfreesize());
		if (!StringUtils.isEmpty(disk.getId())) {
			String unit = disk.getUnitType();

			Disk dbDisk = this.getById(disk.getId());

			dbDisk.setMindiskfreesize(mindiskfreesize);
			dbDisk.setDiskdesc(disk.getDiskdesc());
			// dbDisk.setDiskdrive(diskdrive);
			dbDisk.setUpdateTime(new Date());
			dbDisk.setDiskfreesize(dir.getUsableSpace());
			// dbDisk.setDiskstatus(diskstatus);
			dbDisk.setLoginurl(disk.getLoginurl());
			dbDisk.setDisktotlesize(dir.getTotalSpace());
			dbDisk.setDisktype(disk.getDisktype());
			dbDisk.setDiskusesize(dir.getTotalSpace() - dir.getUsableSpace());
			dbDisk.setId(disk.getId());
			dbDisk.setLoginpwd(disk.getLoginpwd());
			dbDisk.setDiskstatus(disk.getDiskstatus());
			dbDisk.setUsagerate((int) ((dir.getUsableSpace() / (dir.getTotalSpace() * 1.0)) * 100));
			diskDao.save(dbDisk);
		} else {
			disk.setMindiskfreesize(mindiskfreesize);
			disk.setCreateTime(new Date());
			disk.setDiskfreesize(dir.getUsableSpace());
			disk.setDisktotlesize(dir.getTotalSpace());
			disk.setDiskusesize(dir.getTotalSpace() - dir.getUsableSpace());
			disk.setUsagerate((int) ((dir.getUsableSpace() / (dir.getTotalSpace() * 1.0)) * 100));
			diskDao.save(disk);

		}
	}

	// 单位换算
	public Long unitToByte(String unit, Long mindiskfreesize) {
		Long a = 0L;
		if (unit.equals("MB")) {
			a = mindiskfreesize * 1024L * 1024L;
		}
		if (unit.equals("GB")) {
			a = mindiskfreesize * 1024L * 1024L * 1024L;
		}
		if (unit.equals("TB")) {
			a = mindiskfreesize * 1024L * 1024L * 1024L * 1024L;
		}
		if (unit.equals("PB")) {
			a = mindiskfreesize * 1024L * 1024L * 1024L * 1024L * 1024L;
		}
		return a;
	}
}
