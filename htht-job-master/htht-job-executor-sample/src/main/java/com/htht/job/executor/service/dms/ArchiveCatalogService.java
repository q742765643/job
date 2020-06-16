package com.htht.job.executor.service.dms;

import com.htht.job.executor.dao.dms.ArchiveCatalogDao;
import com.htht.job.executor.model.dms.module.ArchiveCatalog;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @date:2018年9月14日上午10:24:54
 * @author:yss
 */
@Transactional
@Service("archiveCatalogService")
public class ArchiveCatalogService extends BaseService<ArchiveCatalog> {

    @Autowired
    private ArchiveCatalogDao archiveCatalogDao;

    @Override
    public BaseDao<ArchiveCatalog> getBaseDao() {
        return archiveCatalogDao;
    }

    // 获取目录树
    @Cacheable(value = "archiveCatalogCache")
    public List<ArchiveCatalog> findArchiveCatalogs() {
        Sort sort = new Sort(Sort.Direction.ASC, "catalogCode", "fid");
        List<ArchiveCatalog> all = archiveCatalogDao.findAll(sort);
        List<ArchiveCatalog> tree = new ArrayList<ArchiveCatalog>();
        for (ArchiveCatalog archiveCatalog : all) {
            if (!StringUtils.isNotBlank(archiveCatalog.getText())) {
                archiveCatalog.setText(archiveCatalog.getCatalogName());
            }
            if (org.springframework.util.StringUtils.isEmpty(archiveCatalog.getCatalogCode())) {
                tree.add(getNodes(archiveCatalog, all));
            }
        }
        return tree;
    }

    // 递归获取子目录
    private ArchiveCatalog getNodes(ArchiveCatalog archiveCatalog, List<ArchiveCatalog> all) {
        for (ArchiveCatalog ac : all) {
            if (!StringUtils.isNotBlank(archiveCatalog.getText())) {
                ac.setText(ac.getCatalogName());
            }
            if (archiveCatalog.getFid().equals(ac.getPid())) {
                if (archiveCatalog.getNodes() == null) {
                    archiveCatalog.setNodes(new ArrayList<ArchiveCatalog>());
                }
                archiveCatalog.getNodes().add(getNodes(ac, all));
            }
        }
        return archiveCatalog;
    }

    // 保存目录
    @CacheEvict(value = "archiveCatalogCache")
    public int saveArchiveCatalog(ArchiveCatalog archiveCatalog) {
        try {
            if (!StringUtils.isEmpty(archiveCatalog.getId())) {
                ArchiveCatalog dbArchiveCatalog = getById(archiveCatalog.getId());
                if (!StringUtils.isEmpty(dbArchiveCatalog.getCatalogCode())) {
                    archiveCatalog.setCatalogCode(dbArchiveCatalog.getCatalogCode());
                }
                archiveCatalog.setUpdateTime(new Date());
                archiveCatalogDao.update(archiveCatalog.getCatalogName(), archiveCatalog.getId(),
                        archiveCatalog.getFid(), archiveCatalog.getUpdateTime(), archiveCatalog.getMainTableName(),
                        archiveCatalog.getyNodeType(), archiveCatalog.getNodeDesc());
            } else {
                if (!archiveCatalog.getPid().equals("0")) {

                    archiveCatalog.setCatalogCode(getCatlogCode(archiveCatalog.getCatalogCode()));
                }
                archiveCatalog.setCreateDate(new Date());
                archiveCatalog.setCreateTime(new Date());
                archiveCatalog.setFid(getUUID());

                this.save(archiveCatalog);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    // 获取目录编码
    public String getCatlogCode(String catalogCode) {
        // 递增 A B C
        if (StringUtils.isEmpty(catalogCode)) {
            catalogCode = getCatlogCodeII();
            return catalogCode;
        } else {
            String catlogCodeSon = getCatlogCodeSon(catalogCode);
            return catlogCodeSon;
        }
    }

    // 获取二级以下的目录编码
    public String getCatlogCodeSon(String catalogCode) {
        SimpleSpecificationBuilder<ArchiveCatalog> builder = new SimpleSpecificationBuilder<ArchiveCatalog>();
        builder.add("catalogCode", "likeAll", catalogCode);
        List<ArchiveCatalog> aclist = archiveCatalogDao.findAll(builder.generateSpecification());
        List<String> newaclist = new ArrayList<String>();
        for (ArchiveCatalog ac : aclist) {
            if (ac.getCatalogCode().length() == catalogCode.length() + 2) {
                newaclist.add(ac.getCatalogCode());
            }
        }
        if (newaclist.size() == 0) {
            catalogCode = catalogCode + "01";
        } else if (newaclist.size() == 1) {
            if (getCatlogCodeInt(newaclist.get(0)) == 1) {
                catalogCode = catalogCode + "02";
            } else {
                catalogCode = catalogCode + "01";
            }
        } else {
            Collections.sort(newaclist);
            if (getCatlogCodeInt(newaclist.get(0)) != 1) {
                return catalogCode + "01";
            }
            int a1 = getCatlogCodeInt(newaclist.get(newaclist.size() - 1));
            int a2 = getCatlogCodeInt(newaclist.get(0));
            if ((a1 - a2 + 1) == newaclist.size()) {
                return getCatlogCode(catalogCode, a1);
            } else {
                for (int i = 0; i < newaclist.size(); i++) {
                    if ((i + 1) < newaclist.size()) {
                        int b1 = getCatlogCodeInt(newaclist.get(i + 1));
                        int b2 = getCatlogCodeInt(newaclist.get(i));
                        if (b1 - b2 > 1) {
                            return getCatlogCode(catalogCode, b2);
                        }
                    }

                }
            }

        }
        return catalogCode;
    }

    // 获取目录编码后两位
    private int getCatlogCodeInt(String strcatalogCode) {
        String tab1 = strcatalogCode.substring(strcatalogCode.length() - 2, strcatalogCode.length());
        return Integer.parseInt(tab1);
    }

    public String getCatlogCode(String catalogCode, int a) {
        if (a < 9) {
            a++;
            catalogCode = catalogCode + "0" + a;
        }
        if (a >= 9) {
            a++;
            catalogCode = catalogCode + a;
        }
        return catalogCode;
    }

    // 递增AB 获取二级目录编码
    public String getCatlogCodeII() {
        String catalogCode = null;
        List<ArchiveCatalog> all = archiveCatalogDao.findAll();
        ArrayList<String> all1 = new ArrayList<String>();
        for (ArchiveCatalog ar : all) {
            if (!StringUtils.isEmpty(ar.getCatalogCode()) && ar.getCatalogCode().length() == 1) {
                all1.add(ar.getCatalogCode());
            }
        }
        Collections.sort(all1);
        if (all1.size() == 0) {
            catalogCode = "A";
        } else if (all1.size() == 1) {
            if (all1.get(0).equals("A")) {
                int stringToA = StringToA("A");
                stringToA++;
                catalogCode = AToString(stringToA);
            } else {
                catalogCode = "A";
            }
        } else {
            int a = StringToA(all1.get(all1.size() - 1));
            int b = StringToA(all1.get(0));
            if ((a - b + 1) == all1.size()) {
                catalogCode = AToString(a + 1);
            } else {
                for (int j = 0; j < all1.size(); j++) {
                    if ((j + 1) < all1.size()) {
                        int a1 = StringToA(all1.get(j + 1));
                        int a2 = StringToA(all1.get(j));
                        if (a1 - a2 > 1) {
                            catalogCode = AToString(a2 + 1);
                        }
                    }

                }
            }
        }

        return catalogCode;
    }

    // String转换ACII
    public int StringToA(String content) {
        int result = 0;
        int max = content.length();
        for (int i = 0; i < max; i++) {
            char c = content.charAt(i);
            int b = (int) c;
            result = result + b;
        }
        return result;
    }

    // ascii转换为string
    public String AToString(int i) {
        return Character.toString((char) i);
    }

    public String getUUID() {

        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * @param catalogCode
     * @return
     */
    public List<ArchiveCatalog> getByCatalogCode(String catalogCode) {
        SimpleSpecificationBuilder<ArchiveCatalog> builder = new SimpleSpecificationBuilder<ArchiveCatalog>();
        builder.add("catalogCode", "likeL", catalogCode);
        List<ArchiveCatalog> findAll = archiveCatalogDao.findAll(builder.generateSpecification());
        return findAll;
    }

    @CacheEvict(value = "archiveCatalogCache")
    public void deleteTreeNodes(List<ArchiveCatalog> allArchiveRules) {
        archiveCatalogDao.deleteInBatch(allArchiveRules);

    }

}
