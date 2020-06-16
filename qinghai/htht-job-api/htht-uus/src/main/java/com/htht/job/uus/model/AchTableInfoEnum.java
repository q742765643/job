package com.htht.job.uus.model;

public enum AchTableInfoEnum {
	
	// 种植结构信息
	PLANT_STRUCTURE("SmartAgri-0002323nhnhtn54534", "htht_ach_plant_structure"),
	// 主要作物信息
	MAIN_CROP("SmartAgri-1112323nhnhtn54534", "htht_ach_main_crop"),
	// 主要农气指标
	MAIN_ARGINDEX("SmartAgri-2222323nhnhtn54534", "htht_ach_main_argindex"),
	// 新型经营主体信息
	NEW_BUSINESS("SmartAgri-333323nhnhtn54534", "htht_ach_new_business"),
	// 农业产值
	ARG_OUTPUT("SmartAgri-444323nhnhtn54534", "htht_ach_arg_output"),
	// 种植大户
	PLANTER_INFO("SmartAgri-5552323nhnhtn54534", "htht_ach_planter_info"),
	// 农业专业合作社
	ARG_COOPERATIVE("SmartAgri-6662323nhnhtn54534", "htht_ach_arg_cooperative"),
	// 农机手
	MACH_OPERATOR("SmartAgri-777323nhnhtn54534", "htht_ach_arg_mach_operator"),
	// 农业专家
	ARG_EXPERT("SmartAgri-8882323nhnhtn54534", "htht_ach_arg_expert"),
	// 田块信息
	LAND_INFO("SmartAgri-9992323nhnhtn54534", "htht_ach_land_info"),
	// “直通式”服务对象
	SERVICE_OBJ("SmartAgri-jjjj2323nhnhtn54534", "htht_ach_service_obj"),
	// 主要大宗作物分县产量统计
	YIELD_COUNT("SmartAgri-kkkk2323nhnhtn54534", "htht_ach_yield_count"),
	// 作物生长发育表T_R_AGME_C01_ELE 
	T_R_AGME_C01_ELE("SmartAgri-mmmm2323nhnhtn54534", "htht_ach_agme_c01_ele"),
	// 产量因素表T_R_AGME_C04_ELE
	T_R_AGME_C04_ELE("SmartAgri-qqqq2323nhnhtn54534", "htht_ach_agme_c04_ele"),  
	// 产量结构表T_R_AGME_C05_ELE
	T_R_AGME_C05_ELE("SmartAgri-rrrr2323nhnhtn54534", "htht_ach_agme_c05_ele"),  
	// 关键农事活动表T_R_AGME_C06_ELE
	T_R_AGME_C06_ELE("SmartAgri-ssss2323nhnhtn54534", "htht_ach_agme_c06_ele"),  
	// 县产量水平表T_R_AGME_C07_ELE
	T_R_AGME_C07_ELE("SmartAgri-tttt2323nhnhtn54534", "htht_ach_agme_c07_ele");  

	private String id;  
    private String name;  

    private AchTableInfoEnum(String id, String name) {  
    	this.id = id;
    	this.name = name;
	} 

    // 普通方法  
    public static String getName(String id) {  
        for (AchTableInfoEnum c : AchTableInfoEnum.values()) {  
            if (c.id.equals(id)) {  
                return c.name;  
            }  
        }  
        return null;  
    }
}
