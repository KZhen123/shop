package com.mapper;

import com.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {

    Integer insert(Category category);

    Integer deleteByPrimaryKey(int id);

    Integer updateByPrimaryKey(Category category);

    List<Category> selectAll();

    /**
     * 查询分页列表
     * */
    List<Category> queryPageList(@Param("begin") Integer begin, @Param("size") Integer size);

    /**
     * 查找类别的总数
     * */
    Long getCount();
}
