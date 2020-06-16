//package com.htht.job.executor.model.dbms;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.IdClass;
//import javax.persistence.Table;
//
//import org.hibernate.annotations.GenericGenerator;
//
//import com.htht.job.executor.model.dbms.idClass.IdClassUserRole;
//@IdClass(IdClassUserRole.class)
//@Entity
//@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
//@Table(name = "htht_dms_uim_user")
//public class UserRole implements java.io.Serializable{
//
//	private static final long serialVersionUID = 1L;
//    @Column(name = "FD_ID", nullable = true)
//	@GeneratedValue(generator = "jpa-uuid")
//	private String id;
//    @Id String F_USERNAME;
//    @Id String F_ROLEID;
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getF_USERNAME() {
//		return F_USERNAME;
//	}
//	public void setF_USERNAME(String f_USERNAME) {
//		F_USERNAME = f_USERNAME;
//	}
//	public String getF_ROLEID() {
//		return F_ROLEID;
//	}
//	public void setF_ROLEID(String f_ROLEID) {
//		F_ROLEID = f_ROLEID;
//	}
//	
//}
