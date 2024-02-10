package com.test;

import com.entity.Category;
import com.mapper.CategoryMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class test {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    public void test(){
//        List<Category> list = categoryMapper.queryPageList(0, 10);
//        list
    }
}
