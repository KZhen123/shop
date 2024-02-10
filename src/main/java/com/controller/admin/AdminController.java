package com.controller.admin;

import com.entity.*;
import com.service.*;
import com.util.JustPhone;
import com.util.KeyUtil;
import com.util.StatusCode;
import com.util.ValidateCode;
import com.vo.LayuiPageVo;
import com.vo.ResultVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private CommodityService commodityService;

    /**
     * 管理员跳转登录
     */
    @GetMapping("/admin")
    public String admintologin() {
        return "admin/login/login";
    }

    /**
     * 管理员登录
     * 1.判断输入账号的类型
     * 3.登录
     * */
    @ResponseBody
    @PostMapping("/admin/login")
    public ResultVo adminlogin(@RequestBody Login login, HttpSession session){
        String account=login.getUsername();
        String password=login.getPassword();
        String vercode=login.getVercode();
        UsernamePasswordToken token;
        if(!ValidateCode.code.equalsIgnoreCase(vercode)){
            return new ResultVo(false,StatusCode.ERROR,"请输入正确的验证码");
        }
        //判断输入的账号是否手机号
        if (!JustPhone.justPhone(account)) {
            //输入的是用户名
            String username = account;
            //盐加密
            token=new UsernamePasswordToken(username, new Md5Hash(password,"Campus-shops").toString());
        }else {
            //输入的是手机号
            String mobilephone = account;
            login.setMobilephone(mobilephone);
            //将封装的login中username变为null
            login.setUsername(null);
            //盐加密
            token=new UsernamePasswordToken(mobilephone, new Md5Hash(password,"Campus-shops").toString());
        }
        Subject subject= SecurityUtils.getSubject();
        try {
            subject.login(token);
            //盐加密
            String passwords = new Md5Hash(password, "Campus-shops").toString();
            login.setPassword(passwords);
            Login login1 = loginService.userLogin(login);
            //查询登录者的权限
            Integer roleId = userRoleService.LookUserRoleId(login1.getUserid());
            if (roleId == 2){
                session.setAttribute("admin",login1.getUsername());
                session.setAttribute("username",login1.getUsername());
                return new ResultVo(true,StatusCode.OK,"登录成功");
            }
            return new ResultVo(true,StatusCode.ACCESSERROR,"权限不足");
        }catch (UnknownAccountException e){
            return new ResultVo(true,StatusCode.LOGINERROR,"用户名不存在");
        }catch (IncorrectCredentialsException e){
            return new ResultVo(true,StatusCode.LOGINERROR,"密码错误");
        }
    }

    /**
     * 用户列表
     * */
    @GetMapping("/admin/userlist")
    public String userlist(){
        return "/admin/user/userlist";
    }

    /**
     * 管理员列表
     * */
    @GetMapping("/admin/adminlist")
    public String adminlist(){
        return "/admin/user/adminlist";
    }

    /**
     * 分页查询不同角色用户信息
     * roleid：1普通成员 2管理员
     * userstatus：1正常 0封号
     */
    @GetMapping("/admin/userlist/{roleid}/{userstatus}")
    @ResponseBody
    public LayuiPageVo userlist(int limit, int page,@PathVariable("roleid") Integer roleid,@PathVariable("userstatus") Integer userstatus) {
        List<UserInfo> userInfoList = userInfoService.queryAllUserInfo((page - 1) * limit, limit,roleid,userstatus);
        Integer dataNumber = userInfoService.queryAllUserCount(roleid);
        return new LayuiPageVo("",0,dataNumber,userInfoList);
    }

    /**
     * 设置为管理员或普通成员（roleid）
     * 1：普通成员   2：管理员
     */
    @PutMapping("/admin/set/administrator/{userid}/{roleid}")
    @ResponseBody
    public ResultVo setadmin(@PathVariable("userid") String userid,@PathVariable("roleid") Integer roleid) {
        if (roleid == 2){
            Integer i = loginService.updateLogin(new Login().setUserid(userid).setRoleid(roleid));
            if (i == 1){
                userRoleService.UpdateUserRole(new UserRole().setUserid(userid).setRoleid(2));
                return new ResultVo(true, StatusCode.OK, "设置管理员成功");
            }
            return new ResultVo(true, StatusCode.ERROR, "设置管理员失败");
        }else if (roleid == 1){
            Integer i = loginService.updateLogin(new Login().setUserid(userid).setRoleid(roleid));
            if (i == 1){
                userRoleService.UpdateUserRole(new UserRole().setUserid(userid).setRoleid(1));
                return new ResultVo(true, StatusCode.OK, "设置成员成功");
            }
            return new ResultVo(true, StatusCode.ERROR, "设置成员失败");
        }
        return new ResultVo(false,StatusCode.ACCESSERROR,"违规操作");
    }

    /**
     * 将用户封号或解封（userstatus）
     * 0：封号  1：解封
     */
    @PutMapping("/admin/user/forbid/{userid}/{userstatus}")
    @ResponseBody
    public ResultVo adminuserlist(@PathVariable("userid") String userid,@PathVariable("userstatus") Integer userstatus) {
        if (userstatus == 0){
            Integer j = userInfoService.UpdateUserInfo(new UserInfo().setUserid(userid).setUserstatus(userstatus));
            if (j == 1){
                return new ResultVo(true, StatusCode.OK, "封号成功");
            }
            return new ResultVo(true, StatusCode.ERROR, "封号失败");
        }else if (userstatus == 1){
            Integer j = userInfoService.UpdateUserInfo(new UserInfo().setUserid(userid).setUserstatus(userstatus));
            if (j == 1){
                return new ResultVo(true, StatusCode.OK, "解封成功");
            }
            return new ResultVo(true, StatusCode.ERROR, "解封失败");
        }
        return new ResultVo(false,StatusCode.ACCESSERROR,"违规操作");
    }

    /**
     * 管理员商品列表
     * */
    @GetMapping("/admin/product")
    public String adminproduct(){
        return "/admin/product/productlist";
    }

    /**
     * 管理员类别列表
     * */
    @GetMapping("/admin/category")
    public String admincategory(){
        return "/admin/product/category";
    }

    /**
     * 分页管理员查看各类商品信息
     *前端传入页码、分页数量
     *前端传入商品信息状态码（commstatus）-->全部:100，正常:1，已完成:2
     * 因为是管理员查询，将userid设置为空
     */
    @GetMapping("/admin/commodity/{commstatus}")
    @ResponseBody
    public LayuiPageVo userCommodity(@PathVariable("commstatus") Integer commstatus, int limit, int page) {
        if(commstatus==100){
            List<Commodity> commodityList = commodityService.queryAllCommodity((page - 1) * limit, limit, null, null);
            Integer dataNumber = commodityService.queryCommodityCount(null, null);
            return new LayuiPageVo("",0,dataNumber,commodityList);
        }else{
            List<Commodity> commodityList = commodityService.queryAllCommodity((page - 1) * limit, limit, null, commstatus);
            Integer dataNumber = commodityService.queryCommodityCount(null, commstatus);
            return new LayuiPageVo("",0,dataNumber,commodityList);
        }
    }

}
