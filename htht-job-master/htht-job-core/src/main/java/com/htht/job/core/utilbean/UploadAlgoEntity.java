package com.htht.job.core.utilbean;

import java.io.Serializable;

public class UploadAlgoEntity implements Serializable {
    public static final long serialVersionUID = 42L;

    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;

    private Integer code;
    private String msg;
    private String algoZipPath;
    private String algoZipName;

    public UploadAlgoEntity() {
        super();
    }

    public UploadAlgoEntity(Integer code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAlgoZipPath() {
        return algoZipPath;
    }

    public void setAlgoZipPath(String algoZipPath) {
        this.algoZipPath = algoZipPath;
    }

    public String getAlgoZipName() {
        return algoZipName;
    }

    public void setAlgoZipName(String algoZipName) {
        this.algoZipName = algoZipName;
    }

}


