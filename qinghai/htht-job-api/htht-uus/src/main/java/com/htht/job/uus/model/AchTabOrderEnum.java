package com.htht.job.uus.model;

public enum AchTabOrderEnum {
	
	// 主要大宗作物分县产量统计
	YIELD_COUNT("SmartAgri-kkkk2323nhnhtn54534", "county,year,crop"),
	// 作物生长发育表T_R_AGME_C01_ELE 
	T_R_AGME_C01_ELE("SmartAgri-mmmm2323nhnhtn54534", "area_station,crops"),
	// 产量因素表T_R_AGME_C04_ELE
	T_R_AGME_C04_ELE("SmartAgri-qqqq2323nhnhtn54534", "area_station,crops"),  
	// 产量结构表T_R_AGME_C05_ELE
	T_R_AGME_C05_ELE("SmartAgri-rrrr2323nhnhtn54534", "area_station,crops"),  
	// 关键农事活动表T_R_AGME_C06_ELE
	T_R_AGME_C06_ELE("SmartAgri-ssss2323nhnhtn54534", "area_station,crops"),  
	// 县产量水平表T_R_AGME_C07_ELE
	T_R_AGME_C07_ELE("SmartAgri-tttt2323nhnhtn54534", "area_station,crops");  

	private String id;  
    private String name;  

    private AchTabOrderEnum(String id, String name) {  
    	this.id = id;
    	this.name = name;
	} 

    // 普通方法  
    public static String getName(String id) {  
        for (AchTabOrderEnum c : AchTabOrderEnum.values()) {  
            if (c.id.equals(id)) {  
                return c.name;  
            }  
        }  
        return null;  
    }
}
