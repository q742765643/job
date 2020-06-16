package com.htht.job.core.utilbean;

import java.io.File;

/**
 * 系统常量定义的类
 * 
 * @author JiahaoWong
 * 
 */
public class SystemConstants {

	// 保存用户信息在Session中
	public static final String SESSION_USER_INFO = "Session_User_Info";
	// 保存菜单信息在Session中
	public static final String SESSION_USER_MENU = "Session_User_Menu";
	// 资源管理员角色名称
	public static final String SYS_RESOURCE = "SYS_RESOURCE";

	public static final String SEPERATOR = File.separator;

	public static String createPrivateDir = "";
	
	// 来自于服务构建
    public static final Integer BUILD_SOURCE=1;
    
 // 资源目录属性文件值，0：公共目录,1:私有目录,2:其它目录
	public static final int RESOURCE_PUBLIC_DATASORUCE = 0 ;
	public static final int RESOURCE_PRIVATE_DATASORUCE = 1 ;
	public static final int RESOURCE_OTHER_DATASORUCE = 2 ;
    
	/**
	 * 状态
	 * 
	 * @author JiahaoWong
	 * 
	 */
	public enum Status {
		ENABLE(1), DISABLE(2), DEL(3);

		private int key;

		private Status(int i) {
			key = i;
		}

		public int getKey() {
			return key;
		}

		public static Status getStatusByKey(int key) {
			switch (key) {
			case 1:
				return ENABLE;
			case 2:
				return DISABLE;
			case 3:
				return DEL;
			default:
				return null;
			}
		}
	}

	/**
	 * 性别
	 * 
	 * @author JiahaoWong
	 * 
	 */
	public enum Gender {
		MALE(1, "男"), FEMALE(2, "女");

		private int key;
		private String name;

		private Gender(int key, String name) {
			this.key = key;
			this.name = name;
		}

		public int getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public static Gender getGenderByKey(int key) {
			switch (key) {
			case 1:
				return MALE;
			case 2:
				return FEMALE;
			default:
				return null;
			}
		}
	}

	/**
	 * 结果类型
	 * 
	 * @author JiahaoWong
	 * 
	 */
	public enum ResultType {
		SUCCESSFUL(1, "成功"), FAIL(2, "失败");

		private int key;
		private String name;

		private ResultType(int key, String name) {
			this.key = key;
			this.name = name;
		}

		public int getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public static ResultType getResultTypeByKey(int key) {
			switch (key) {
			case 1:
				return SUCCESSFUL;
			case 2:
				return FAIL;
			default:
				return null;
			}
		}
	}

	/**
	 * 是否审计
	 * 
	 * @author JiahaoWong
	 * 
	 */
	public enum Auditable {
		AUDIT(1, "记录"), NONAUDIT(2, "不记录");

		private int key;
		private String name;

		private Auditable(int key, String name) {
			this.key = key;
			this.name = name;
		}

		public int getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public static Auditable getAuditableByKey(int key) {
			switch (key) {
			case 1:
				return AUDIT;
			case 2:
				return NONAUDIT;
			default:
				return null;
			}
		}
	}

	/**
	 * 服务执行状态
	 * 
	 * @author JiahaoWong
	 * 
	 */
	public enum RunState {
		STATE_NEW(0, "新建"), STATE_READY(10, "等待中"), STATE_ACTIVE(20, "执行中"), STATE_COMPLETED_OK(
				30, "执行完成"), STATE_COMPLETED_WITH_FAULT(40, "异常"), STATE_SUSPENDED(
				50, "暂停"), STATE_TERMINATED(60, "终止"),STATE_PEOPLE_PROCESSING(70, "人工处理"),STATE_CANCER(80, "取消");

		private int key;
		private String name;

		private RunState(int key, String name) {
			this.key = key;
			this.name = name;
		}

		public int getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public static RunState getStateByKey(int key) {
			switch (key) {
			case 0:
				return STATE_NEW;
			case 10:
				return STATE_READY;
			case 20:
				return STATE_ACTIVE;
			case 30:
				return STATE_COMPLETED_OK;
			case 40:
				return STATE_COMPLETED_WITH_FAULT;
			case 50:
				return STATE_SUSPENDED;
			case 60:
				return STATE_TERMINATED;
			case 70:
				return STATE_PEOPLE_PROCESSING;
			case 80:
				return STATE_CANCER;
			default:
				return null;
			}
		}
	}

	public enum SecurityLevel {
		VIEW(1, "查看"), UPDATE(2, "修改"), EXCUTE(4, "执行"), ARRANGE(8, "编排");
		private int key;
		private String name;

		private SecurityLevel(int key, String name) {
			this.key = key;
			this.name = name;
		}

		public int getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public static SecurityLevel getStateByKey(int key) {
			switch (key) {
			case 1:
				return VIEW;
			case 2:
				return UPDATE;
			case 4:
				return EXCUTE;
			case 8:
				return ARRANGE;
			default:
				return null;
			}
		}
	}

	public enum Restethod {
		GET(0, "get"), POST(1, "post"), PUT(2, "put"), DELETE(3, "delete");
		private int key;
		private String name;

		private Restethod(int key, String name) {
			this.key = key;
			this.name = name;
		}

		public int getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public static Restethod getStateByKey(int key) {
			switch (key) {
			case 0:
				return GET;
			case 1:
				return POST;
			case 2:
				return PUT;
			case 3:
				return DELETE;
			default:
				return null;
			}
		}
	}

	public enum process {
		DELETE(1), NOTDELETE(0);

		private int key;

		private process(int i) {
			key = i;
		}

		public int getKey() {
			return key;
		}

		public static process getStateByKey(int key) {
			switch (key) {
			case 1:
				return DELETE;

			case 0:
				return NOTDELETE;

			default:
				return null;
			}
		}

	}

	public enum processState {
		DEPLOY(1), UNDEPLOY(0);
		private int key;

		private processState(int i) {
			key = i;
		}

		public int getKey() {
			return key;
		}

		public static processState getProcessState(int key) {
			switch (key) {
			case 1:
				return DEPLOY;
			case 0:
				return UNDEPLOY;
			default:
				return null;
			}
		}
	}

	public enum deployState {
		DEPLOYING(1), DEPLOY_SUCCESS(2), DEPLOY_FAILURE(3);
		private int key;

		private deployState(int key) {
			this.key = key;
		}

		public int getKey() {
			return key;
		}

		public static deployState getDeployState(int key) {
			switch (key) {
			case 1:
				return DEPLOYING;
			case 2:
				return DEPLOY_SUCCESS;
			case 3:
				return DEPLOY_FAILURE;
			default:
				return null;
			}
		}

	}

	public enum globalSystemParameter {
		/*
		 * 服务分类名称是否唯一:SERVICE_CATEGORY_UNIQUE
		 * 同一部门是否可以管理服务:SERVICE_SAMEDEPARTMENT_MANAGE
		 * 上级部门是否可以管理服务:SERVICE_PARENTDEPARTMENT_MANAGE
		 * 权限控制优先级:SERVICE_ACCESSCONTROL_PRIORITY
		 * 服务注册人是否可以始终拥有管理权限:SERVICE_REGISTRANT_MANAGE
		 * 部门策略是否可以执行同部门服务:SERVICE_DEPARTMENTSTRATEGY_SAMEDEPARTMENT
		 * 删除用户是否清除执行日志记录:USER_DELETE_CLEANEXCUTELOG
		 * 删除用户是否清除系统参数日志记录:USER_DELETE_CLEANPREFERENCELOG
		 * 删除用户是否清除个人空间下的文件:USER_DELETE_CLEANOWNFILE
		 * 删除部门是否清除部门文件:USER_DELETE_CLEANDEPARTMENTFILE
		 * 创建策略默认状态:STRATEGY_DEFAULTSTATE
		 * 同部门是否可以管理策略:STRATEGY_SAMEDEPARTMENT_MANAGE
		 * 上级部门是否可以管理策略:STRATEGY_PARENTDEPARTMENT_MANAGE
		 * 策略归属范围:STRATEGY_APPLIEDRANGE 日志导出记录最大数:LOG_EXPORT_MAX
		 * 运行状态更新频率（单位：秒）:LOG_RANSTATUS_UPDATEFREQUENCY
		 * 空间不足提醒（百分比：1-100%）:RESOURCE_INSUFFICIENTSPACE_REMIND
		 * 所属部门中普通用户的权限:RESOURCE_GENERALUSER_ACCESS
		 * 用户目录名称:RESOURCE_USERDIRECTORY_NAME
		 * 部门目录名称:RESOURCE_DEPARTMENTDIRECTORY_NAME
		 * 公共目录名称:RESOURCE_PUBLICDIRECTORY_NAME
		 * 是否给单个服务增加权限:SERVICE_SINGLE_ADD
		 */
		SERVICE_CATEGORY_UNIQUE(1, "服务分类名称是否唯一"), 
		SERVICE_SAMEDEPARTMENT_MANAGE(2, "同一部门是否可以管理服务"), 
		SERVICE_PARENTDEPARTMENT_MANAGE(3,"上级部门是否可以管理服务"), 
		SERVICE_ACCESSCONTROL_PRIORITY(4, "权限控制优先级"), 
		SERVICE_REGISTRANT_MANAGE(5, "服务注册人是否可以始终拥有管理权限"), 
		SERVICE_DEPARTMENTSTRATEGY_SAMEDEPARTMENT(6, "部门策略是否可以执行同部门服务"), 
		USER_DELETE_CLEANEXCUTELOG(7,"删除用户是否清除执行日志记录"), 
		USER_DELETE_CLEANPREFERENCELOG(8,"删除用户是否清除系统参数日志记录"), 
		USER_DELETE_CLEANOWNFILE(9,"删除用户是否清除个人空间下的文件"), 
		USER_DELETE_CLEANDEPARTMENTFILE(10,"删除部门是否清除部门文件"), 
		STRATEGY_DEFAULTSTATE(11, "创建策略默认状态"), 
		STRATEGY_SAMEDEPARTMENT_MANAGE(12, "同部门是否可以管理策略"), 
		STRATEGY_PARENTDEPARTMENT_MANAGE(13,"上级部门是否可以管理策略"), 
		STRATEGY_APPLIEDRANGE(14, "策略归属范围"), 
		LOG_EXPORT_MAX(15, "日志导出记录最大数"), 
		LOG_RANSTATUS_UPDATEFREQUENCY(16,"运行状态更新频率(单位：秒)"), 
		RESOURCE_INSUFFICIENTSPACE_REMIND(17,"空间不足提醒(百分比：1-100%)"), 
		RESOURCE_GENERALUSER_ACCESS(18,"所属部门中普通用户的权限"), 
		RESOURCE_USERDIRECTORY_NAME(19, "用户目录名称"), 
		RESOURCE_DEPARTMENTDIRECTORY_NAME(20, " 部门目录名称"), 
		RESOURCE_PUBLICDIRECTORY_NAME(21, "公共目录名称"),
		SERVICE_SINGLE_ADD(22, "是否给单个服务增加权限");
		
		private int key;
		private String name;

		private globalSystemParameter(int key, String name) {
			this.key = key;
			this.name = name;
		}

		public int getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public static globalSystemParameter getGlobalSystemParameter(int key) {
			switch (key) {
			case 1:
				return SERVICE_CATEGORY_UNIQUE;
			case 2:
				return SERVICE_SAMEDEPARTMENT_MANAGE;
			case 3:
				return SERVICE_PARENTDEPARTMENT_MANAGE;
			case 4:
				return SERVICE_ACCESSCONTROL_PRIORITY;
			case 5:
				return SERVICE_REGISTRANT_MANAGE;
			case 6:
				return SERVICE_DEPARTMENTSTRATEGY_SAMEDEPARTMENT;
			case 7:
				return USER_DELETE_CLEANEXCUTELOG;
			case 8:
				return USER_DELETE_CLEANPREFERENCELOG;
			case 9:
				return USER_DELETE_CLEANOWNFILE;
			case 10:
				return USER_DELETE_CLEANDEPARTMENTFILE;
			case 11:
				return STRATEGY_DEFAULTSTATE;
			case 12:
				return STRATEGY_SAMEDEPARTMENT_MANAGE;
			case 13:
				return STRATEGY_PARENTDEPARTMENT_MANAGE;
			case 14:
				return STRATEGY_APPLIEDRANGE;
			case 15:
				return LOG_EXPORT_MAX;
			case 16:
				return LOG_RANSTATUS_UPDATEFREQUENCY;
			case 17:
				return RESOURCE_INSUFFICIENTSPACE_REMIND;
			case 18:
				return RESOURCE_GENERALUSER_ACCESS;
			case 19:
				return RESOURCE_USERDIRECTORY_NAME;
			case 20:
				return RESOURCE_DEPARTMENTDIRECTORY_NAME;
			case 21:
				return RESOURCE_PUBLICDIRECTORY_NAME;
			case 22:
				return SERVICE_SINGLE_ADD;
			default:
				return null;
			}
		}

	}

	public enum processVersion {
		DEPLOYED(1), NOTDEPLOYED(0);

		private int key;

		private processVersion(int i) {
			key = i;
		}

		public int getKey() {
			return key;
		}

		public static processVersion getStateByKey(int key) {
			switch (key) {
			case 1:
				return DEPLOYED;

			case 0:
				return NOTDEPLOYED;

			default:
				return null;
			}
		}

	}

	public enum processVersionState {
		DRAFT(0), DEPLOY(1);
		private int key;

		private processVersionState(int i) {
			key = i;
		}

		public int getKey() {
			return key;
		}

		public static processVersionState getProcessState(int key) {
			switch (key) {
			case 1:
				return DRAFT;
			case 0:
				return DEPLOY;
			default:
				return null;
			}
		}
	}
    /**
     * 自定义参数类型
     * **/
	public enum paramType {
		P_INT(0, "整型"), P_FLOAT(1, "浮点型"), P_STRING(2, "字符串"), P_INPUT_FILE(3,
				"输入文件"), P_OUTPUT_FILE(4, "输出文件");
		private int key;
		private String value;

		private paramType(int i, String value) {
			this.key = i;
			this.value = value;
		}

		public int getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public static paramType getParamType(int key) {
			switch (key) {
			case 0:
				return P_INT;
			case 1:
				return P_FLOAT;
			case 2:
				return P_STRING;
			case 3:
				return P_INPUT_FILE;
			case 4:
				return P_OUTPUT_FILE;
			default:
				return null;
			}
		}
	}
 
	 /**
     * 服务来源
     * **/
	public enum serviceSource {
		COMMON_REG(0, "普通注册"), BUILD_REG(1, "服务构建"), PACKAGE_REG(2, "封装N+"),NET_REG(3,".net服务"),MY_PROCESS(4,"自定义流程"),PEOPLE_REG(5,"人工服务");
		private int key;
		private String value;

		private serviceSource(int i, String value) {
			this.key = i;
			this.value = value;
		}

		public int getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public static serviceSource getServiceSource(int key) {
			switch (key) {
			case 0:
				return COMMON_REG;
			case 1:
				return BUILD_REG;
			case 2:
				return PACKAGE_REG;
			case 3:
				return NET_REG;
			default:
				return null;
			}
		}
	}
	 /**
     * 流程仓库中的流程注册到服务管理时的状态
     * return: 0-成功 1-流程中所编排用到的服务已经不存在 2-rest地址有误 3-注册服务时出现异常 4-部署失败
     * **/
	public enum deployProcessStatus {
		DEPLOY_OK(0, "成功"), DEPLOY_SERVICE_INEXISTENCE(1, "流程中所编排用到的服务已经不存在"), DEPLOY_SERVICE_REST_ERROR(2, "地址有误"),DEPLOY_SERVICE_REGIST_UNUSUAL(3,"注册服务时出现异常"),DEPLOY_SERVICE_FAIL(4,"部署失败");
		private int key;
		private String value;

		private deployProcessStatus(int i, String value) {
			this.key = i;
			this.value = value;
		}

		public int getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public static deployProcessStatus getProcessStatus(int key) {
			switch (key) {
			case 0:
				return DEPLOY_OK;
			case 1:
				return DEPLOY_SERVICE_INEXISTENCE;
			case 2:
				return DEPLOY_SERVICE_REST_ERROR;
			case 3:
				return DEPLOY_SERVICE_REGIST_UNUSUAL;
			case 4:
				return DEPLOY_SERVICE_FAIL;
			default:
				return null;
			}
		}
	}
	/**
	 * 流程的状态消息
	 * @author guanxl
	 *
	 */
	public enum processStatusMessage {
		SAVE_SUCCESS(100, "流程保存成功"),UPDATE_SUCCESS(101, "流程更新成功"),NAME_EXIST(102, "流程名称已经存在"),SAVE_FAILURE(103,"流程保存失败"),UPDATE_FAILURE(104, "流程更新失败"),DEPLOY_OK(200,"流程部署成功"),DEPLOY_BYBPEL_Exception(203, "生成bpel文件失败"),DEPLOY_Exception(204, "流程部署失败");
		private int key;
		private String value;

		private processStatusMessage(int i, String value) {
			this.key = i;
			this.value = value;
		}

		public int getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public static processStatusMessage getStatus(int key) {
			switch (key) {
			case 100:
				return SAVE_SUCCESS;
			case 101:
				return UPDATE_SUCCESS;
			case 102:
				return NAME_EXIST;
			case 103:
				return SAVE_FAILURE;
			case 104:
				return UPDATE_FAILURE;
			case 200:
				return DEPLOY_OK;
			case 203:
				return DEPLOY_Exception;
			default:
				return null;
			}
		}
	}
	
	/**
	 * 树中文件的类型，即来源
	 * **/
	public enum FileTreeType{
		PUB(0),PRI(1),PRO(2),DATA(3);
		private int key;

		private FileTreeType(int i) {
			key = i;
		}

		public int getKey() {
			return key;
		}

		public static FileTreeType getStatusByKey(int key) {
			switch (key) {
			case 0:
				return PUB;
			case 1:
				return PRI;
			case 2:
				return DATA;
			default:
				return null;
			}
		}
	}
	
//	public static void main(String[] args) {
//		SecurityLevel[] s = SecurityLevel.values();
//		System.out.println(s[0].getName());
//	}
}
