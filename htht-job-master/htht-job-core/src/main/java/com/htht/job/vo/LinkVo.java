package com.htht.job.vo;/**
 * Created by zzj on 2018/3/25.
 */

/**
 * @program: htht-job
 * @description: link节点Vo
 * @author: zzj
 * @create: 2018-03-25 11:32
 **/
public class LinkVo {
    private String startFigureId;

    private String endFigureId;

    private String isData;

    private String isChild;


    public String getStartFigureId() {
        return startFigureId;
    }

    public void setStartFigureId(String startFigureId) {
        this.startFigureId = startFigureId;
    }

    public String getEndFigureId() {
        return endFigureId;
    }

    public void setEndFigureId(String endFigureId) {
        this.endFigureId = endFigureId;
    }

    public String getIsData() {
        return isData;
    }

    public void setIsData(String isData) {
        this.isData = isData;
    }

    public String getIsChild() {
        return isChild;
    }

    public void setIsChild(String isChild) {
        this.isChild = isChild;
    }
}

