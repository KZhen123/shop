package com.mapper;

import java.util.List;

public interface UserPermMapper {
    /**查询用户的权限*/
    List<String> LookPermsByUserid(Integer roleid);
}
