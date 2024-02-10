package com.mapper;

import com.entity.Login;

public interface LoginMapper {
    /**注册*/
    Integer loginAdd(Login login);
    /**登录及判断用户是否存在*/
    Login userLogin(Login login);
    /**修改角色信息*/
    Integer updateLogin(Login login);
}