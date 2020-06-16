package com.htht.job.uus.model;

import java.util.HashMap;
import java.util.Map;

public class AchTableInfo {

	public static Map<String, Map<String, String>> titleMap = new HashMap<String, Map<String, String>>();
	
	//	种植结构信息
	//	station_num	lon	lat	altitude	city_name	county_name	plant	prop
	public static Map<String, String> titleMap0 = new HashMap<String, String>();
	
	//	主要作物信息
	//	station_num	lon	lat	altitude	city_name	county_name	plant	plant_method
	public static Map<String, String> titleMap1 = new HashMap<String, String>();
	
	// 主要农气指标
	//	station_num	lon	lat	altitude	city_name	county_name	plant	norm
	public static Map<String, String> titleMap2 = new HashMap<String, String>();
	
	//	新型经营主体信息
	//	station_num	lon	lat	altitude	city_name	county_name	township	new_business_name	area	contact	tel
	public static Map<String, String> titleMap3 = new HashMap<String, String>();
	
	// 农业产值
	//	station_num	lon	lat	altitude	city_name	county_name	plant	economic_value
	public static Map<String, String> titleMap4 = new HashMap<String, String>();
	
	//	种植大户
	//	station_num	lon	lat	altitude	city_name	county_name	township	planter_name	area	contact	tel
	public static Map<String, String> titleMap5 = new HashMap<String, String>();
	
	//	农业专业合作社
	// station_num	lon	lat	altitude	city_name	county_name	township	arg_cooperative_name	area	contact	tel
	public static Map<String, String> titleMap6 = new HashMap<String, String>();
	
	//	农机手
	//	station_num	lon	lat	altitude	city_name	county_name	township	agr_mach_operator	agr_mach_type	tel
	public static Map<String, String> titleMap7 = new HashMap<String, String>();
	
	//	农业专家
	// station_num	lon	lat	altitude	city_name	county_name	township	agr_expert	research_area	tel
	public static Map<String, String> titleMap8 = new HashMap<String, String>();
	
	//	田块信息
	//	station_num	lon	lat	altitude	city_name	county_name	township	village	principal	plant	plant_border_position	plant_cen_position	land_attr
	public static Map<String, String> titleMap9 = new HashMap<String, String>();
	
	//	“直通式”服务对象
	// 	station_num	lon	lat	altitude	city_name	county_name	township	service_obj	kind	contact	tel
	public static Map<String, String> titleMapj = new HashMap<String, String>();
	
	//	主要大宗作物分县产量统计
	// 	county	longitude	latitude	altitude	year	crop	per_yield	total_yield
	public static Map<String, String> titleMapk = new HashMap<String, String>();
	
	//	作物生长发育表T_R_AGME_C01_ELE 	
	//	area_station	latitude	longitude	station_elevation	create_timecreate_time	update_time	num_amendment	viewing_mode	crops	breeds_of_crops	maturity	development_time	developmental_anomaly	developmental_percentage	growth_status	plant_height	plant_spacing
	public static Map<String, String> titleMapm = new HashMap<String, String>();
	
	//	产量因素表T_R_AGME_C04_ELE
	//	area_station	latitude	longitude	station_elevation	create_time	update_time	num_amendment	viewing_mode	test_time	crops	maturity	project_name	measure_value
	public static Map<String, String> titleMapq = new HashMap<String, String>();
	
	//	产量结构表T_R_AGME_C05_ELE	
	//	area_station	latitude	longitude	station_elevation	create_time	update_time	num_amendment	viewing_mode	test_time	crops	project_name	measure_value
	public static Map<String, String> titleMapr = new HashMap<String, String>();
	
	//	关键农事活动表T_R_AGME_C06_ELE	
	//	area_station	latitude	longitude	station_elevation	create_time	update_time	num_amendment	viewing_mode	start_time	stop_time	crops	project_name	quality	methods_and_tools
	public static Map<String, String> titleMaps = new HashMap<String, String>();
	
	//	县产量水平表T_R_AGME_C07_ELE	
	//	area_station	latitude	longitude	station_elevation	create_time	update_time	num_amendment	viewing_mode	year	crops	station_output_leve	ave_yield_per_county	percentage_of_increase_and_decrease_in_production_in_counties
	public static Map<String, String> titleMapt = new HashMap<String, String>();
	
	static {
		titleMap0.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,plant,prop");
		titleMap0.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,种植作物,占比");
		titleMap1.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,plant,plant_method");
		titleMap1.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,种植作物,种植方式");
		titleMap2.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,plant,norm");
		titleMap2.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,种植作物,指标");
		titleMap3.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,township,new_business_name,area,contact,tel");
		titleMap3.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,乡名,新型经营主体名称,涉及领域（种植业、养殖业）,联系人,联系方式");
		titleMap4.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,plant,economic_value");
		titleMap4.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,种植作物,经济产值/种植面积/产量");
		titleMap5.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,township,planter_name,area,contact,tel");
		titleMap5.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,乡名,种植大户名称,涉及领域,联系人,联系方式");
		titleMap6.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,township,arg_cooperative_name,area,contact,tel");
		titleMap6.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,乡名,农业专业合作社名称,涉及领域,联系人,联系方式");
		titleMap7.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,township,agr_mach_operator,agr_mach_type,tel");
		titleMap7.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,乡名,农机手,农机类型,联系方式");
		titleMap8.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,township,agr_expert,research_area,tel");
		titleMap8.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,乡名,农业专家,研究领域,联系方式");
		titleMap9.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,township,village,principal,plant,plant_border_position,plant_cen_position,land_attr");
		titleMap9.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,乡名,村名,负责人,种植作物,种植田块边界经纬度,种植田块中心点经纬度,土地属性");
		titleMapj.put("titleId", "station_num,lon,lat,altitude,city_name,county_name,township,service_obj,kind,contact,tel");
		titleMapj.put("titleName", "区站号,经度,纬度,海拔高度,市州名,县名,乡名,服务对象（合作社、果园）,分类,联系人,联系方式");
		titleMapk.put("titleId", "county,longitude,latitude,altitude,year,crop,per_yield,total_yield");
		titleMapk.put("titleName", "县名,经度,纬度,海拔高度,年份,作物名称,单产（公斤）,总产（吨）");
		titleMapm.put("titleId", "area_station,latitude,longitude,station_elevation,create_timecreate_time,update_time,num_amendment,viewing_mode,crops,breeds_of_crops,maturity,development_time,developmental_anomaly,developmental_percentage,growth_status,plant_height,plant_spacing");
		titleMapm.put("titleName", "区站号,纬度,经度,测站高度,创建时间,更新时间,修改次数,观测方式,作物名称,作物品种,发育期,发育时间,发育期距平,发育期百分率,生长状况,植株高度,植株密度");
		titleMapq.put("titleId", "area_station,latitude,longitude,station_elevation,create_time,update_time,num_amendment,viewing_mode,test_time,crops,maturity,project_name,measure_value");
		titleMapq.put("titleName", "区站号,纬度,经度,测站高度,创建时间,更新时间,修改次数,观测方式,测定时间,作物名称,发育期,项目名称,测定值");
		titleMapr.put("titleId", "area_station,latitude,longitude,station_elevation,create_time,update_time,num_amendment,viewing_mode,test_time,crops,project_name,measure_value");
		titleMapr.put("titleName", "区站号,纬度,经度,测站高度,创建时间,更新时间,修改次数,观测方式,测定时间,作物名称,项目名称,测定值");
		titleMaps.put("titleId", "area_station,latitude,longitude,station_elevation,create_time,update_time,num_amendment,viewing_mode,start_time,stop_time,crops,project_name,quality,methods_and_tools");
		titleMaps.put("titleName", "区站号,纬度,经度,测站高度,创建时间,更新时间,修改次数,观测方式,起始时间,结束时间,作物名称,项目名称,质量,方法和工具");
		titleMapt.put("titleId", "area_station,latitude,longitude,station_elevation,create_time,update_time,num_amendment,viewing_mode,year,crops,station_output_leve,ave_yield_per_county,percentage_of_increase_and_decrease_in_production_in_counties");
		titleMapt.put("titleName", "区站号,纬度,经度,测站高度,创建时间,更新时间,修改次数,观测方式,年度,作物名称,测站产量水平,县平均单产,县产量增减产百分率");

		titleMap.put("SmartAgri-0002323nhnhtn54534", titleMap0);
		titleMap.put("SmartAgri-1112323nhnhtn54534", titleMap1);
		titleMap.put("SmartAgri-2222323nhnhtn54534", titleMap2);
		titleMap.put("SmartAgri-333323nhnhtn54534", titleMap3);
		titleMap.put("SmartAgri-444323nhnhtn54534", titleMap4);
		titleMap.put("SmartAgri-5552323nhnhtn54534", titleMap5);
		titleMap.put("SmartAgri-6662323nhnhtn54534", titleMap6);
		titleMap.put("SmartAgri-777323nhnhtn54534", titleMap7);
		titleMap.put("SmartAgri-8882323nhnhtn54534", titleMap8);
		titleMap.put("SmartAgri-9992323nhnhtn54534", titleMap9);
		titleMap.put("SmartAgri-jjjj2323nhnhtn54534", titleMapj);
		titleMap.put("SmartAgri-kkkk2323nhnhtn54534", titleMapk);
		titleMap.put("SmartAgri-mmmm2323nhnhtn54534", titleMapm);
		titleMap.put("SmartAgri-qqqq2323nhnhtn54534", titleMapq);
		titleMap.put("SmartAgri-rrrr2323nhnhtn54534", titleMapr);
		titleMap.put("SmartAgri-ssss2323nhnhtn54534", titleMaps);
		titleMap.put("SmartAgri-tttt2323nhnhtn54534", titleMapt);
	}
	
}
