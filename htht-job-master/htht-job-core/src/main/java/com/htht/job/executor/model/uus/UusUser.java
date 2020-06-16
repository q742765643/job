package com.htht.job.executor.model.uus;

import com.alibaba.fastjson.annotation.JSONField;
import com.htht.job.core.util.BaseEntity;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "htht_uus_user")
public class UusUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 账户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 性别 0 女 1 男
     */
    private Integer sex;

    /**
     * 出生日期
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 电话
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 住址
     */
    private String address;

    /**
     * 逻辑删除状态 0 未删除 1 删除
     */
    private Integer deleteStatus;

    /**
     * 是否锁定
     * <p>
     * 0 未锁定 1 锁定
     */
    private Integer locked;

    /**
     * 用户描述
     */
    private String description;

    /**
     * 用户地区
     */
    private String region;


    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "htht_uus_user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private java.util.Set<UusRole> roles;


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public Integer getLocked() {
        return locked;
    }

    public void setLocked(Integer locked) {
        this.locked = locked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public java.util.Set<UusRole> getRoles() {
        return roles;
    }

    public void setRoles(java.util.Set<UusRole> roles) {
        this.roles = roles;
    }
}
