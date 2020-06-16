package com.htht.job.core.util;


public enum ReturnCodeEnum {
	SUCCESS(0, "成功"),
	FIAL(1, " 失败"),
	
	/**==========101 100===============**/
	ReturnCodeEnum_101_ERROR(101,"固定参数为空"),
	ReturnCodeEnum_102_ERROR(102,"outputlog为空"),
	ReturnCodeEnum_103_ERROR(103,"inputxml为空"),
	ReturnCodeEnum_104_ERROR(104,"exePath为空"),
	ReturnCodeEnum_105_ERROR(105,"scriptFile为空"),
	ReturnCodeEnum_106_ERROR(106,"outputxml为空"),
	ReturnCodeEnum_107_ERROR(107,"输入参数为空"),
	ReturnCodeEnum_108_ERROR(108,"拼装xml map失败"),
	ReturnCodeEnum_109_ERROR(109,"创建文件失败"),
	ReturnCodeEnum_110_ERROR(110,"demoHanderService执行失败"),
	ReturnCodeEnum_111_ERROR(111,"period为空"),
	ReturnCodeEnum_112_ERROR(112,"issue为空"),
	ReturnCodeEnum_113_ERROR(113,"inputfiles为空"),
	ReturnCodeEnum_114_ERROR(114,"outputpath为空"),

	/**==========201 AppJobInfo 200===============**/
	ReturnCodeEnum_201_ERROR(201,"taskType不能为空"),
	ReturnCodeEnum_202_ERROR(202,"jobGroup不存在"),
	ReturnCodeEnum_203_ERROR(203,"jobCron格式不正确"),
	ReturnCodeEnum_204_ERROR(204,"JobDesc描述不能为空"),
	ReturnCodeEnum_205_ERROR(205,"executorRouteStrategy路由策略非法"),
	ReturnCodeEnum_206_ERROR(206,"executorBlockStrategy阻塞处理策略非法"),
	ReturnCodeEnum_207_ERROR(207,"executorFailStrategy调度异常处理非法"),
	ReturnCodeEnum_208_ERROR(208,"glueType运行模式非法"),
	ReturnCodeEnum_209_ERROR(209,"executorHandler不能为空"),
	ReturnCodeEnum_210_ERROR(210,"新增任务失败"),
	ReturnCodeEnum_211_ERROR(211,"任务名称不能为空"),
	ReturnCodeEnum_212_ERROR(212,"流程未选择"),
	ReturnCodeEnum_213_ERROR(213,"流程不存在"),
	ReturnCodeEnum_214_ERROR(214,"添加定时任务失败"),


	ReturnCodeEnum_301_ERROR(301,"节点为空，无法选取节点"),
	ReturnCodeEnum_302_ERROR(302,"获取文件列表失败"),
	ReturnCodeEnum_303_ERROR(303,"获取文件夹失败"),
	ReturnCodeEnum_304_ERROR(304,"获取流程为空"),
	ReturnCodeEnum_305_ERROR(305,"获取输入参数报错"),
	ReturnCodeEnum_306_ERROR(306,"参数存入map报错"),
	ReturnCodeEnum_307_ERROR(307,"处理第一步参数传递给子流程出错"),
	ReturnCodeEnum_308_ERROR(308,"上一步执行未完成"),
	ReturnCodeEnum_309_ERROR(309,"流程中止"),
	ReturnCodeEnum_310_ERROR(309,"流程已加锁");

























	private int key;
	private String value;

	private ReturnCodeEnum(int key, String value) {
		this.key = key;
		this.value = value;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static String getValue(int key) {
		for (ReturnCodeEnum st : ReturnCodeEnum.values()) {
			if (key==st.key) {
				return st.value;
			}
		}
		return "";
	}
}
