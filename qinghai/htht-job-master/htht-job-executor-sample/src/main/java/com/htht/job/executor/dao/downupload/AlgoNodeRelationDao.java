package com.htht.job.executor.dao.downupload;

import com.htht.job.executor.model.downupload.AlgorithmRelationInfo;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface AlgoNodeRelationDao extends BaseDao<AlgorithmRelationInfo> {

	List<AlgorithmRelationInfo> findBytreeId(String treeId);

	List<AlgorithmRelationInfo> findByalgoId(String algoId);

	AlgorithmRelationInfo findByTreeIdAndAlgoId(String treeId, String algoId);

	@Modifying
	@Query(nativeQuery = true,value = "delete from htht_cluster_schedule_algorithm_node_relation where tree_id =?1  and algo_id =?2")
	void deleteByTreeIdAndAlgoId(String treeId, String algoId);
}
