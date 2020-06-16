package com.htht.job.executor.model.datacategory;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Entity
@Table(name = "htht_cluster_schedule_data_category")
public class DataCategoryDTO extends BaseEntity {
    /**
     * 文本内容
     */
    @Column(name = "text")
    private String text;
    /**
     * 父id
     */
    @Column(name = "parent_id")
    private String parentId;
    /**
     * 分类列表
     */
    @Transient
    private List<DataCategoryDTO> nodes;
    /**
     * 目录递归名称
     */
    @Column(name = "menu")
    private String menu;
    /**
     * 目录递归id
     */
    @Column(name = "menu_id")
    private String menuId;
    /**
     * 图标路径
     */
    @Column(name = "icon_path")
    private String iconPath;
    /**
     * 树分类标示
     */
    @Column(name = "tree_key")
    private String treeKey;


    public String getTreeKey() {
        return treeKey;
    }

    public void setTreeKey(String treeKey) {
        this.treeKey = treeKey;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<DataCategoryDTO> getNodes() {
        return nodes;
    }

    public void setNodes(List<DataCategoryDTO> nodes) {
        this.nodes = nodes;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
