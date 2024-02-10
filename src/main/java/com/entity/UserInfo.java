package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
	private String userid;
    /**
     * 角色id 1普通用户 2管理员
     */
    private Integer roleid;
    /**
     * 用户名
     */
	private String username;
    /**
     * 用户密码
     */
	private String password;
    /**
     * 手机号
     */
	private String mobilephone;
    /**
     * 用户邮箱
     */
	private String email;
    /**
     * 用户头像
     */
	private String uimage;
    /**
     * 用户性别
     */
	private String sex;
    /**
     * 1正常 0封号
     */
	private Integer userstatus;
    /**
     * 注册时间
     */
	private Date createtime;
}
