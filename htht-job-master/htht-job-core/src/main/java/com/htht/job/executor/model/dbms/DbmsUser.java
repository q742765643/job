package com.htht.job.executor.model.dbms;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tb_uim_user")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class DbmsUser implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "F_ID", length = 32)
    @GeneratedValue(generator = "jpa-uuid")
    private String id; // 用户名
    @Column(name = "F_PWD")
    private String password; // 密码
    @Column(name = "F_DESC", columnDefinition = "TEXT")
    private String description;
    @Column(name = "F_ADDTIME")
    private Date createTime;
    @Column(name = "F_ISLOCK")
    private Long locked; // 0 有 1无
    @Column(name = "F_ORGID")
    private String ssdw;
    @Column(name = "F_USERNAME")
    private String userName;
    private String nickName;    //昵称
    @Column(name = "F_EMAIL")
    private String email;
    @Column(name = "F_PHONE")
    private String phone;
    @Transient
    private Long isPortalUser;
    @Transient
    private Integer fcountyCode;
    @Column(name = "F_SEX")
    private String sex;
    @Column
    private String birthday;
    @Transient
    private String cerNo;// 身份证号
    @Transient
    private String fax;
    @Transient
    private Long showIndex;// 显示顺序*
    @Column(name = "F_CAREER", columnDefinition = "TEXT")
    private String career;// 从事行业
    @Column(name = "F_APPLYTYPE", columnDefinition = "TEXT")
    private String applytype;// 应用类别
    @Column(name = "F_STATUS", columnDefinition = "TEXT")
    private String status;// 审核状态
    @Column(name = "F_WORKUNIT", columnDefinition = "TEXT")
    private String workunit;// 工作单位

    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "tb_uim_userrole",
            joinColumns = {@JoinColumn(name = "F_USERNAME")},
            inverseJoinColumns = {@JoinColumn(name = "F_ROLEID")})
    private java.util.Set<DbmsRole> roles;

    //	@ManyToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
//	@JoinTable(name = "tb_uim_usermodule", 
//		joinColumns = { @JoinColumn(name = "F_USERID") }, 
//		inverseJoinColumns = { @JoinColumn(name = "F_MODID")})
//	private java.util.Set<DbmsModule> modules;
//	@ManyToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
//	@JoinTable(name = "tb_uim_userdata", 
//		joinColumns = { @JoinColumn(name = "F_USERID") }, 
//		inverseJoinColumns = { @JoinColumn(name = "F_CATALOGID")})
//	private java.util.Set<DbmsArchiveCatalog> archiveCatalogs;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getLocked() {
        return locked;
    }

    public void setLocked(Long locked) {
        this.locked = locked;
    }

    public String getSsdw() {
        return ssdw;
    }

    public void setSsdw(String ssdw) {
        this.ssdw = ssdw;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getIsPortalUser() {
        return isPortalUser;
    }

    public void setIsPortalUser(Long isPortalUser) {
        this.isPortalUser = isPortalUser;
    }

    public Integer getFcountyCode() {
        return fcountyCode;
    }

    public void setFcountyCode(Integer fcountyCode) {
        this.fcountyCode = fcountyCode;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCerNo() {
        return cerNo;
    }

    public void setCerNo(String cerNo) {
        this.cerNo = cerNo;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Long getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(Long showIndex) {
        this.showIndex = showIndex;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getApplytype() {
        return applytype;
    }

    public void setApplytype(String applytype) {
        this.applytype = applytype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWorkunit() {
        return workunit;
    }

    public void setWorkunit(String workunit) {
        this.workunit = workunit;
    }

    public java.util.Set<DbmsRole> getRoles() {
        return roles;
    }

    public void setRoles(java.util.Set<DbmsRole> roles) {
        this.roles = roles;
    }
//	public java.util.Set<DbmsModule> getModules() {
//		return modules;
//	}
//	public void setModules(java.util.Set<DbmsModule> modules) {
//		this.modules = modules;
//	}
//	public java.util.Set<DbmsArchiveCatalog> getArchiveCatalogs() {
//		return archiveCatalogs;
//	}
//	public void setArchiveCatalogs(java.util.Set<DbmsArchiveCatalog> archiveCatalogs) {
//		this.archiveCatalogs = archiveCatalogs;
//	}
}
