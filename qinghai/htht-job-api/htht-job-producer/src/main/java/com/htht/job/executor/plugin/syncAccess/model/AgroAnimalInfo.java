package com.htht.job.executor.plugin.syncAccess.model;

import com.htht.job.core.util.BaseEntity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the data_agro_animal_husbandry_eco_station database table.
 * 
 */
@Entity
@Table(name="data_agro_animal_husbandry_eco_station")
public class AgroAnimalInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;
	private int isRevise;
	private Date fbday;
	private String nsta;
	private String csta;
	private String h11;
	private String h12;
	private String h13;
	private String h21;
	private String h22;
	private String h23;
	private String w101;
	private String w102;
	private String w103;
	private String w201;
	private String w202;
	private String w203;
	private String w301;
	private String w302;
	private String w303;
	public Date getFbday() {
		return fbday;
	}
	public void setFbday(Date fbday) {
		this.fbday = fbday;
	}
	public String getNsta() {
		return nsta;
	}
	public void setNsta(String nsta) {
		this.nsta = nsta;
	}
	public String getCsta() {
		return csta;
	}
	public void setCsta(String csta) {
		this.csta = csta;
	}
	public String getH11() {
		return h11;
	}
	public void setH11(String h11) {
		this.h11 = h11;
	}
	public String getH12() {
		return h12;
	}
	public void setH12(String h12) {
		this.h12 = h12;
	}
	public String getH13() {
		return h13;
	}
	public void setH13(String h13) {
		this.h13 = h13;
	}
	public String getH21() {
		return h21;
	}
	public void setH21(String h21) {
		this.h21 = h21;
	}
	public String getH22() {
		return h22;
	}
	public void setH22(String h22) {
		this.h22 = h22;
	}
	public String getH23() {
		return h23;
	}
	public void setH23(String h23) {
		this.h23 = h23;
	}
	public String getW101() {
		return w101;
	}
	public void setW101(String w101) {
		this.w101 = w101;
	}
	public String getW201() {
		return w201;
	}
	public void setW201(String w201) {
		this.w201 = w201;
	}
	public String getW301() {
		return w301;
	}
	public void setW301(String w301) {
		this.w301 = w301;
	}
	public String getW102() {
		return w102;
	}
	public void setW102(String w102) {
		this.w102 = w102;
	}
	public String getW202() {
		return w202;
	}
	public void setW202(String w202) {
		this.w202 = w202;
	}
	public String getW302() {
		return w302;
	}
	public void setW302(String w302) {
		this.w302 = w302;
	}
	public String getW103() {
		return w103;
	}
	public void setW103(String w103) {
		this.w103 = w103;
	}
	public String getW203() {
		return w203;
	}
	public void setW203(String w203) {
		this.w203 = w203;
	}
	public String getW303() {
		return w303;
	}
	public void setW303(String w303) {
		this.w303 = w303;
	}
	public int getIsRevise() {
		return isRevise;
	}
	public void setIsRevise(int isRevise) {
		this.isRevise = isRevise;
	}
	
}