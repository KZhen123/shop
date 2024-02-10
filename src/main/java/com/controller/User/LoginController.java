package com.controller.User;


import com.entity.Login;
import com.entity.UserInfo;
import com.entity.UserRole;
import com.service.LoginService;
import com.service.UserInfoService;
import com.service.UserRoleService;
import com.util.*;
import com.vo.ResultVo;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 登录注册 控制器
 * </p>
 */
@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserRoleService userRoleService;

    /**
     * 图片验证码
     */
    @RequestMapping(value = "/images", method = {RequestMethod.GET, RequestMethod.POST})
    public void images(HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        //禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        ValidateCode vCode = new ValidateCode(820, 200, 5, 80);
        vCode.write(response.getOutputStream());
    }

    /**
     * 注册
     * 1.前端传入用户名（username）、密码（password）、邮箱（email）、手机号（mobilephone）
     * 2.查询账号是否已经注册
     * 3.查询用户名是否已存在
     * 4.判断验证码是否有效或正确
     * 5.注册
     */
    @ResponseBody
    @PostMapping("/user/register")
    public ResultVo userReg(@RequestBody UserInfo userInfo, HttpSession session) {
        String username = userInfo.getUsername();
        String password = userInfo.getPassword();
        String mobilephone = userInfo.getMobilephone();
        Login login = new Login().setMobilephone(mobilephone);
        //查询手机号是否已经注册
        Login userIsExist = loginService.userLogin(login);
        if (!StringUtils.isEmpty(userIsExist)) {//用户账号已经存在
            return new ResultVo(false, StatusCode.ERROR, "该手机号已经注册过了");
        }
        login.setUsername(username).setMobilephone(null);
        Login userNameIsExist = loginService.userLogin(login);
        if (!StringUtils.isEmpty(userNameIsExist)) {//用户名已经存在
            return new ResultVo(false, StatusCode.ERROR, "用户名已存在，请换一个吧");
        }

        //盐加密
        String passwords = new Md5Hash(password, "Campus-shops").toString();
        String userid = KeyUtil.genUniqueKey();
        login.setId(KeyUtil.genUniqueKey()).setUserid(userid).setMobilephone(mobilephone).setPassword(passwords);
        Integer integer = loginService.loginAdd(login);
        //新注册用户存入默认头像、存入默认签名
        userInfo.setUserid(userid).setPassword(passwords).setUimage("/pic/8c42362f920c45948255768d494d48ee.jpg");
        Integer integer1 = userInfoService.userReg(userInfo);
        if (integer == 1 && integer1 == 1) {
            /**注册成功后存入session*/
            session.setAttribute("userid", userid);
            session.setAttribute("username", username);
            /**存入用户角色信息*/
            userRoleService.InsertUserRole(new UserRole().setUserid(userid).setRoleid(1));
            UsernamePasswordToken token = new UsernamePasswordToken(mobilephone, new Md5Hash(password, "Campus-shops").toString());
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            return new ResultVo(true, StatusCode.OK, "注册成功");
        }
        return new ResultVo(false, StatusCode.ERROR, "注册失败");

    }

    /**
     * 登录
     * 1.判断输入账号的类型
     * 2.登录
     */
    @ResponseBody
    @PostMapping("/user/login")
    public ResultVo userLogin(@RequestBody Login login, HttpSession session) {
        String account = login.getUsername();
        String password = login.getPassword();
        String vercode = login.getVercode();
        UsernamePasswordToken token;
        if (!ValidateCode.code.equalsIgnoreCase(vercode)) {
            return new ResultVo(false, StatusCode.ERROR, "请输入正确的验证码");
        }
        //判断输入的账号是否手机号
        if (!JustPhone.justPhone(account)) {
            //输入的是用户名
            String username = account;
            //盐加密
            token = new UsernamePasswordToken(username, new Md5Hash(password, "Campus-shops").toString());
        } else {
            //输入的是手机号
            String mobilephone = account;
            login.setMobilephone(mobilephone);
            //将封装的login中username变为null
            login.setUsername(null);
            //盐加密
            token = new UsernamePasswordToken(mobilephone, new Md5Hash(password, "Campus-shops").toString());
        }
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            //盐加密
            String passwords = new Md5Hash(password, "Campus-shops").toString();
            login.setPassword(passwords);
            Login login1 = loginService.userLogin(login);
            session.setAttribute("userid", login1.getUserid());
            session.setAttribute("username", login1.getUsername());
            return new ResultVo(true, StatusCode.OK, "登录成功");
        } catch (UnknownAccountException e) {
            return new ResultVo(true, StatusCode.LOGINERROR, "用户名不存在");
        } catch (IncorrectCredentialsException e) {
            return new ResultVo(true, StatusCode.LOGINERROR, "密码错误");
        }
    }

    /**
     * 重置密码
     * 1.判断手机号格式是否正确
     * 2.查询手机号是否存在
     * 3.判断验证码是否有效或正确
     * 4.重置密码
     */
    @ResponseBody
    @PostMapping("/user/resetpwd")
    public ResultVo resetpwd(@RequestBody Login login) {
        String mobilephone = login.getMobilephone();
        String password = login.getPassword();
        String vercode = login.getVercode();
        Login login1 = new Login();
        UserInfo userInfo = new UserInfo();
        if (!JustPhone.justPhone(mobilephone)) {//判断输入的手机号格式是否正确
            return new ResultVo(false, StatusCode.ERROR, "请输入正确格式的手机号");
        }
        //查询手机号是否存在
        login1.setMobilephone(mobilephone);
        Login userIsExist = loginService.userLogin(login1);
        if (StringUtils.isEmpty(userIsExist)) {//用户账号不存在
            return new ResultVo(false, StatusCode.LOGINERROR, "该账号不存在");
        }

        //盐加密
        String passwords = new Md5Hash(password, "Campus-shops").toString();
        login1.setPassword(passwords).setId(userIsExist.getId()).setMobilephone(null);
        userInfo.setMobilephone(mobilephone).setPassword(passwords).setUserid(userIsExist.getUserid());
        Integer integer = loginService.updateLogin(login1);
        Integer integer1 = userInfoService.UpdateUserInfo(userInfo);
        if (integer == 1 && integer1 == 1) {
            return new ResultVo(true, StatusCode.OK, "重置密码成功");
        }
        return new ResultVo(false, StatusCode.ERROR, "重置密码失败");
    }

    /**
     * 退出登陆
     */
    @GetMapping("/user/logout")
    public String logout(HttpServletRequest request, HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        String username = (String) session.getAttribute("username");
        if (StringUtils.isEmpty(userid) && StringUtils.isEmpty(username)) {
            return "redirect:/";
        }
        request.getSession().removeAttribute("userid");
        request.getSession().removeAttribute("username");
        return "redirect:/";
    }


}