package com.controller;


import com.alibaba.fastjson.JSONObject;
import com.entity.*;
import com.service.*;
import com.util.KeyUtil;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.PageVo;
import com.vo.ResultVo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 */
@Controller
public class CommodityController {
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private CommimagesService commimagesService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private CollectService collectService;

    /**
     * 跳转到发布商品
     */
    @GetMapping("/user/relgoods")
    public String torelgoods(HttpSession session){
        return "/user/product/relgoods";
    }

    /**
     * 跳转到修改商品
     *  1、查询商品详情
     *  2、查询商品得其他图
     */
    @GetMapping("/user/editgoods/{commid}")
    public String toeditgoods(@PathVariable("commid")String commid, HttpSession session, ModelMap modelMap){
        Commodity commodity=commodityService.LookCommodity(new Commodity().setCommid(commid));
        modelMap.put("goods",commodity);
        modelMap.put("otherimg",commimagesService.LookGoodImages(commid));
        return "/user/product/changegoods";
    }

    /**
     * 修改商品
     * 1、修改商品信息
     * 2、修改商品的其他图的状态
     * 3、插入商品的其他图
     */
    @PostMapping("/changegoods/rel")
    @ResponseBody
    public String changegoods(@RequestBody Commodity commodity, HttpSession session){
        String userid = (String) session.getAttribute("userid");
        commodityService.ChangeCommodity(commodity);
        commimagesService.DelGoodImages(commodity.getCommid());
        List<Commimages> commimagesList=new ArrayList<>();
        for (String list:commodity.getOtherimg()) {
            commimagesList.add(new Commimages().setId(KeyUtil.genUniqueKey()).setCommid(commodity.getCommid()).setImage(list));
        }
        commimagesService.InsertGoodImages(commimagesList);
        return "0";
    }

    /**
     * 发布商品
     * 1、插入商品信息
     * 2、插入商品其他图
     */
    @PostMapping("/relgoods/rel")
    @ResponseBody
    public String relgoods(@RequestBody Commodity commodity, HttpSession session){
        String userid = (String) session.getAttribute("userid");
        String commid = KeyUtil.genUniqueKey();
        commodity.setCommid(commid).setUserid(userid);//商品id
        commodityService.InsertCommodity(commodity);
        List<Commimages> commimagesList=new ArrayList<>();
        for (String list:commodity.getOtherimg()) {
            commimagesList.add(new Commimages().setId(KeyUtil.genUniqueKey()).setCommid(commid).setImage(list));
        }
        commimagesService.InsertGoodImages(commimagesList);
        return "0";
    }

    /**
     * 上传主图
     */
    @PostMapping("/relgoods/pic")
    @ResponseBody
    public JSONObject relgoodsPic(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        String filename = UUID.randomUUID().toString().replaceAll("-", "");
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String filenames = filename + "." + ext;
        String pathname = "D:\\campusshops\\file\\" + filenames;
        file.transferTo(new File(pathname));
        resUrl.put("src", "/pic/"+filenames);
        res.put("msg", "");
        res.put("code", 0);
        res.put("data", resUrl);
        return res;
    }

    /**
     * 上传其他图片
     */
    @PostMapping(value="/relgoods/images")
    @ResponseBody
    public JSONObject relgoodsimages(@RequestParam(value = "file", required = false) MultipartFile[] file) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        List<String> imageurls=new ArrayList<>();
        for (MultipartFile files:file){
            String filename = UUID.randomUUID().toString().replaceAll("-", "");
            String ext = FilenameUtils.getExtension(files.getOriginalFilename());
            String filenames = filename + "." + ext;
            String pathname = "D:\\campusshops\\file\\" + filenames;
            files.transferTo(new File(pathname));
            imageurls.add("/pic/"+filenames);
            res.put("msg", "");
            res.put("code", 0);
        }
        resUrl.put("src", imageurls);
        res.put("data", resUrl);
        return res;
    }

    /**
     * 商品详情
     * 根据商品id（commid）查询商品信息、用户昵称及头像
     * */
    @GetMapping("/product-detail/{commid}")
    public String product_detail(@PathVariable("commid") String commid, ModelMap modelMap,HttpSession session){
        Commodity commodity = commodityService.LookCommodity(new Commodity().setCommid(commid));
        commodity.setOtherimg(commimagesService.LookGoodImages(commid));
        modelMap.put("userinfo",userInfoService.queryPartInfo(commodity.getUserid()));
        modelMap.put("gddetail",commodity);
        return "/common/product-detail";
    }

    /**
     * 搜索商品分页数据
     * 前端传入搜索的商品名（commname）
     * */
    @GetMapping("/product/search/number/{commname}")
    @ResponseBody
    public PageVo searchCommodityNumber(@PathVariable("commname") String commname){
        Integer dataNumber = commodityService.queryCommodityByNameCount(commname);
        return new PageVo(StatusCode.OK,"查询成功",dataNumber);
    }

    /**
     * 搜索商品
     * 前端传入当前页数（nowPaging）、搜索的商品名（commname）
     * */
    @GetMapping("/product/search/{nowPaging}/{commname}")
    @ResponseBody
    public ResultVo searchCommodity(@PathVariable("nowPaging") Integer page, @PathVariable("commname") String commname){
        List<Commodity> commodityList = commodityService.queryCommodityByName((page - 1) * 20, 20, commname);

        if(!StringUtils.isEmpty(commodityList)){//如果有对应商品
            for (Commodity commodity : commodityList) {
                /**查询商品对应的其它图片*/
                List<String> imagesList = commimagesService.LookGoodImages(commodity.getCommid());
                commodity.setOtherimg(imagesList);
            }
            return new ResultVo(true,StatusCode.OK,"查询成功",commodityList);
        }else{
            return new ResultVo(true,StatusCode.ERROR,"没有相关商品");
        }
    }

    /**
     * 产品清单分页数据
     * */
    @GetMapping("/list-number/{name}")
    @ResponseBody
    public PageVo productListNumber(@PathVariable("name") String name) {
        Integer dataNumber = commodityService.queryAllCommodityByCategoryCount(name);
        return new PageVo(StatusCode.OK,"查询成功",dataNumber);
    }

    /**
     * 产品清单界面
     * */
    @GetMapping("/product-listing/{nowPaging}/{name}")
    @ResponseBody
    public ResultVo productlisting(@PathVariable("nowPaging") Integer page,@PathVariable("name") String name) {
        List<Commodity> commodityList = commodityService.queryAllCommodityByCategory((page - 1) * 16, 16, name);
        for (Commodity commodity : commodityList) {
            /**查询商品对应的其它图片*/
            List<String> imagesList = commimagesService.LookGoodImages(commodity.getCommid());
            commodity.setOtherimg(imagesList);
        }
        return new ResultVo(true,StatusCode.OK,"查询成功",commodityList);
    }

    /**
     * 分页展示个人各类商品信息
     *前端传入页码、分页数量
     *前端传入商品信息状态码（commstatus）-->全部:100，正常:1，已完成:2
     */
    @GetMapping("/user/commodity/{commstatus}")
    @ResponseBody
    public LayuiPageVo userCommodity(@PathVariable("commstatus") Integer commstatus, int limit, int page, HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        //如果未登录，给一个假id
        if(StringUtils.isEmpty(userid)){
            userid = "123456";
        }
        List<Commodity> commodityList=null;
        Integer dataNumber;
        if(commstatus==100){
            commodityList = commodityService.queryAllCommodity((page - 1) * limit, limit, userid,null);
            dataNumber = commodityService.queryCommodityCount(userid,null);
        }else{
            commodityList = commodityService.queryAllCommodity((page - 1) * limit, limit, userid,commstatus);
            dataNumber = commodityService.queryCommodityCount(userid,commstatus);
        }
        return new LayuiPageVo("",0,dataNumber,commodityList);
    }

    /**
     * 删除商品
     * */
    @ResponseBody
    @GetMapping("/user/delete/{commid}")
    public ResultVo delete(@PathVariable("commid") String commid) {
        Integer i = commodityService.delete(commid);
        if (i == 1){
            return new ResultVo(true,StatusCode.OK,"操作成功");
        }
        return new ResultVo(false,StatusCode.ERROR,"操作失败");
    }
}

