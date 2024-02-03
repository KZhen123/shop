package com.controller;

import com.entity.Category;
import com.service.CategoryService;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "selectPage", produces = "application/json")
    @ResponseBody
    public LayuiPageVo selectPage(@RequestParam int size, @RequestParam int page) {
        List<Category> list = categoryService.queryPageList(page, size);
        int count = categoryService.getCount();
        return new LayuiPageVo("", 200, count, list);
    }

    @RequestMapping(value = "add", produces = "application/json")
    @ResponseBody
    public ResultVo addCategory(Category category) {
        Integer res = categoryService.insert(category);
        ResultVo result;
        if (res == 1) {
            result = new ResultVo(true, StatusCode.OK, "新增成功");
        } else {
            result = new ResultVo(false, StatusCode.SERVERERROR, "新增失败");
        }
        return result;
    }

    @RequestMapping(value = "update", produces = "application/json")
    @ResponseBody
    public ResultVo updateCategory(Category category) {
        Integer res = categoryService.update(category);
        ResultVo result;
        if (res == 1) {
            result = new ResultVo(true, StatusCode.OK, "编辑成功");
        } else {
            result = new ResultVo(false, StatusCode.SERVERERROR, "编辑失败");
        }
        return result;
    }

    @RequestMapping(value = "delete", produces = "application/json")
    @ResponseBody
    public ResultVo deleteCategory(@RequestParam int id) {
        Integer res = categoryService.delete(id);
        ResultVo result;
        if (res == 1) {
            result = new ResultVo(true, StatusCode.OK, "删除成功");
        } else {
            result = new ResultVo(false, StatusCode.SERVERERROR, "删除失败");
        }
        return result;
    }

    @RequestMapping(value = "all", produces = "application/json")
    @ResponseBody
    public List<Category> getAll() {
        List<Category> categoryVos = categoryService.selectAll();
        return categoryVos;
    }
}
