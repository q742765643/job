package com.htht.job.admin.core.model.resolve;

import java.util.List;

public class Level extends ZtreeViewPie {
    private String level;
    private List<Level> childrenLevelList;
    private List<Wkt> wktList;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<Level> getChildrenLevelList() {
        return childrenLevelList;
    }

    public void setChildrenLevelList(List<Level> childrenLevelList) {
        this.childrenLevelList = childrenLevelList;
    }

    public List<Wkt> getWktList() {
        return wktList;
    }

    public void setWktList(List<Wkt> wktList) {
        this.wktList = wktList;
    }


}
