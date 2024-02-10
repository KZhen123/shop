package com.controller;


import com.entity.Collect;
import com.service.CollectService;
import com.util.KeyUtil;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 *  购物车控制器
 * </p>
 *
 */
@Controller
public class CollectController {
    @Autowired
    private CollectService collectService;

    @ResponseBody
    @PostMapping("/collect/operate")
    public ResultVo insertcollect(@RequestBody Collect collect, HttpSession session){
        String couserid = (String) session.getAttribute("userid");
        collect.setCouserid(couserid);

        if (StringUtils.isEmpty(couserid)){
            return new ResultVo(false, StatusCode.ACCESSERROR,"请先登录");
        }

        Collect collect1 = collectService.queryCollect(collect);
        if(!StringUtils.isEmpty(collect1)){
            return new ResultVo(false,StatusCode.ERROR,"已在购物车中！");
        }else{
            collect.setId(KeyUtil.genUniqueKey());
            Integer i = collectService.insertCollect(collect);
            if (i == 1){
                return new ResultVo(true, StatusCode.OK,"加入购物车成功");
            }
            return new ResultVo(false,StatusCode.ERROR,"加入购物车失败");
        }
    }

    @ResponseBody
    @PutMapping("/collect/delete/{id}")
    public ResultVo deletecollect(@PathVariable("id") String id,HttpSession session){
        String couserid = (String) session.getAttribute("userid");
        Collect collect = new Collect().setId(id).setCouserid(couserid);
        Collect collect1 = collectService.queryCollect(collect);
        /**判断是否为本人操作*/
        if (collect1.getCouserid().equals(couserid)){
            Integer i = collectService.updateCollect(collect);
            if (i == 1){
                return new ResultVo(true, StatusCode.OK,"移除成功");
            }
            return new ResultVo(false,StatusCode.ERROR,"移除失败");
        }
        return new ResultVo(false,StatusCode.ACCESSERROR,"禁止操作");
    }


    @ResponseBody
    @GetMapping("/user/collect/queryall")
    public LayuiPageVo usercollect(int limit, int page, HttpSession session) {
        String couserid = (String) session.getAttribute("userid");
        List<Collect> collectList = collectService.queryAllCollect((page - 1) * limit, limit, couserid);
        Integer dataNumber = collectService.queryCollectCount(couserid);
        return new LayuiPageVo("",0,dataNumber,collectList);
    }
}

