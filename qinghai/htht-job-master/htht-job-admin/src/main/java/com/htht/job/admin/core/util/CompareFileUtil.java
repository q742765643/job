package com.htht.job.admin.core.util;/**
 * Created by zzj on 2018/7/27.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-07-27 14:37
 **/
public class CompareFileUtil {
    public static void main(String[] args) {
        String s1 = "GF1_PMS2_E117.0_N39.6_20131127_L1A0000117***";
        String s2 = "G_PMS2_E116.9_N39.4_20131127_L1A0000117***";
        String s3 = "GF1_PMS2_E117.0_N39.6_20131127_L1A0000117775";

        char[] charArray = s1.toCharArray();
        char[] charArray2 = s2.toCharArray();

        getGoalFileName(s1, s2,s3);
    }

    public static String getGoalFileName( String mate_fileName,  String quilt_fileName,String fileName) {
        char[] charArray_mate=mate_fileName.toCharArray();
        char[] charArray_quilt=quilt_fileName.toCharArray();
        List<String> match_value=new ArrayList<String>();
        String goal_fileName="";
        for(int i=0;i<charArray_mate.length;i++){
            if(charArray_mate[i]==new Character('*')){
                match_value.add(  fileName.substring(i,i+1));
            }
        }
        int k=0;
        for(int i=0;i<charArray_quilt.length;i++){
            if(charArray_quilt[i]==new Character('*')){
                goal_fileName+=match_value.get(k);
               k++;
            }else{
                goal_fileName+=charArray_quilt[i];
            }
        }
        System.out.print(goal_fileName);
      return goal_fileName;
    }
}

