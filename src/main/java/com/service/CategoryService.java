package com.service;

import com.entity.Category;

import java.util.List;

public interface CategoryService {
    Integer insert(Category category);

    Integer delete(int id);

    Integer update(Category category);

    List<Category> queryPageList(Integer page, Integer size);

    Integer getCount();
}
