package com.htht.job.executor.service.algorithm;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.downupload.AlgoNodeRelationDao;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfoDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Transactional
@Service
public class ProductRelationServiceImpl extends BaseService<AlgorithmRelationInfoDTO> implements ProductRelationService {

    @Autowired
    private AlgoNodeRelationDao algoNodeRelationDao;

    @Override
    public AlgorithmRelationInfoDTO saveRelation(AlgorithmRelationInfoDTO algo) {
        AlgorithmRelationInfoDTO newalgo = new AlgorithmRelationInfoDTO();
        AlgorithmRelationInfoDTO info = algoNodeRelationDao.findByTreeIdAndAlgoId(algo.getTreeId(), algo.getAlgoId());
        if (null == info) {
            algo.setCreateTime(new Date());
            newalgo = algoNodeRelationDao.save(algo);
//            newalgo =  this.save(algo);
            return newalgo;
        }
        return info;
    }

    @Override
    public ReturnT<String> deleteRelation(String treeId, String lgoid) {
        algoNodeRelationDao.deleteByTreeIdAndAlgoId(treeId, lgoid);
        return ReturnT.SUCCESS;
    }

    @Override
    public List<AlgorithmRelationInfoDTO> queryAogo(String treeid) {
        return algoNodeRelationDao.findBytreeId(treeid);
    }

    @Override
    public BaseDao<AlgorithmRelationInfoDTO> getBaseDao() {
        return null;
    }
}
