package com.htht.job.core.util;/**
 * Created by zzj on 2018/4/17.
 */

/**
 * @program: htht-job
 * @description:
 * @author: zzj
 * @create: 2018-04-17 14:40
 **/
public class CmdMessage {
    private int code = -1;
    private StringBuffer output = new StringBuffer();
    private StringBuffer error = new StringBuffer();
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getOutput() {
        return output.toString();
    }
    public void addOutput(String output) {
        this.output.append(output);
    }

    public void setOutput(String output) {
        this.output = new StringBuffer();
        this.output.append(output);
    }

    public void setError(String error) {
        this.error = new StringBuffer();
        this.error.append(error);
    }

    public String getError() {
        return error.toString();
    }
    public void addError(String error) {
        this.error.append(error);
    }

    public boolean isSuccess(){
        if(error.length() > 0){
            if(error.toString().toLowerCase().indexOf("warn") != -1 ){
                return true;
            }else{
                return false;
            }
        }
        return true;
    }
}

