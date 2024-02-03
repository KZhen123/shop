package com.service.impl;

import com.entity.Category;
import com.mapper.CategoryMapper;
import com.service.CategoryService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public Integer insert(Category category){
        return categoryMapper.insert(category);
    }

    public Integer delete(int id){
        return categoryMapper.deleteByPrimaryKey(id);
    }

    public Integer update(Category category){
        return categoryMapper.updateByPrimaryKey(category);
    }

    public List<Category> queryPageList(@Param("page") Integer page, @Param("size") Integer size){
        int begin = (page - 1) * size;
        return categoryMapper.queryPageList(begin, size);
    }

    public Integer getCount(){
        return categoryMapper.getCount().intValue();
    }

    @Override
    public List<Category> selectAll() {
        return categoryMapper.selectAll();
    }
}
