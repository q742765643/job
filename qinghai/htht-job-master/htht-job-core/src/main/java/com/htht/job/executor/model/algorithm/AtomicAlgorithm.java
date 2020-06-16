package com.htht.job.executor.model.algorithm;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;

@Entity  
@Table(name = "htht_cluster_schedule_atomic_algorithm" )
public class AtomicAlgorithm extends BaseEntity {
	/**
	 * DataCategory id
	 */
	@Column(name = "tree_id") 
	private String treeId;
	/**
	 * java模型参数
	 */
	@Column(columnDefinition="TEXT",name = "fixed_parameter") 
	private String fixedParameter;
	/**
	 * 算法模型所需参数
	 */
	@Column(columnDefinition="TEXT",name = "dynamic_parameter") 
	private String dynamicParameter;
	/**
	 * 模型名称
	 */
	@Column(name = "model_name") 
	private String modelName;
	/**
	 * 模型标示
	 */
	@Column(name = "model_identify") 
	private String modelIdentify;
	/**
	 * 任务加载原子算法类型 0列表，1页面
	 */
	private String type;
	/**
	 * type为1是url为要加载的controller路径
	 */
	private String url;
	@Transient
	private ArrayList<String> nodeList;
	/**
	 * 算法共享路径
	 */
	@Column(name = "alog_path")
	private String algoPath;
	/**
	 * 算法类型
	 */
	@Column(name = "algo_type")
	private String algoType;
	@Column(name = "executor_block_strategy")
	private String executorBlockStrategy;    // 阻塞处理策略
	/**
	 * 所需能力
	 */
	@Column(name = "deal_amount")
	private int dealAmount;
	@Transient
	private String algoZipName;

	/**
	 * 算法模型所需参数
	 */
	@Transient
	private String outputParameter;

	@Transient
	private int typeId;

	@Transient
	private String processId;

	

	public String getAlgoZipName() {
		return algoZipName;
	}
	public void setAlgoZipName(String algoZipName) {
		this.algoZipName = algoZipName;
	}
	public String getAlgoType() {
		return algoType;
	}
	public String getExecutorBlockStrategy() {
		return executorBlockStrategy;
	}
	public void setExecutorBlockStrategy(String executorBlockStrategy) {
		this.executorBlockStrategy = executorBlockStrategy;
	}
	public void setAlgoType(String algoType) {
		this.algoType = algoType;
	}
	
	public String getAlgoPath() {
		return algoPath;
	}
	public void setAlgoPath(String algoPath) {
		this.algoPath = algoPath;
	}
	public ArrayList<String> getNodeList() {
		return nodeList;
	}
	public void setNodeList(ArrayList<String> nodeList) {
		this.nodeList = nodeList;
	}
	public String getTreeId() {
		return treeId;
	}
	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}
	public String getFixedParameter() {
		return fixedParameter;
	}
	public void setFixedParameter(String fixedParameter) {
		this.fixedParameter = fixedParameter;
	}
	public String getDynamicParameter() {
		return dynamicParameter;
	}
	public void setDynamicParameter(String dynamicParameter) {
		this.dynamicParameter = dynamicParameter;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModelIdentify() {
		return modelIdentify;
	}
	public void setModelIdentify(String modelIdentify) {
		this.modelIdentify = modelIdentify;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(int dealAmount) {
		this.dealAmount = dealAmount;
	}

	public String getOutputParameter() {
		return outputParameter;
	}

	public void setOutputParameter(String outputParameter) {
		this.outputParameter = outputParameter;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
}
