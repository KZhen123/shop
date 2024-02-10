package com.controller.User;

import com.entity.Login;
import com.entity.UserInfo;
import com.service.LoginService;
import com.service.UserInfoService;
import com.util.*;
import com.vo.ResultVo;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 个人中心 控制器
 * </p>
 */
@Controller
public class UserController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;
    /**
     * 修改密码
     * 1.前端传入旧密码（oldpwd）、新密码（newpwd）
     * 2.判断输入旧密码和系统旧密码是否相等
     * 4.修改密码
     */
    @ResponseBody
    @PutMapping("/user/updatepwd")
    public ResultVo updatepwd(HttpSession session, HttpServletRequest request) throws IOException {
        JSONObject json = JsonReader.receivePost(request);
        String oldpwd = json.getString("oldpwd");
        String newpwd = json.getString("newpwd");
        String userid = (String) session.getAttribute("userid");
        Login login = new Login();
        UserInfo userInfo = new UserInfo();
        login.setUserid(userid);
        Login login1 = loginService.userLogin(login);
        String oldpwds = new Md5Hash(oldpwd, "Campus-shops").toString();
        //如果旧密码相等
        if (oldpwds.equals(login1.getPassword())){
            //盐加密
            String passwords = new Md5Hash(newpwd, "Campus-shops").toString();
            login.setPassword(passwords);
            userInfo.setPassword(passwords).setUserid(login1.getUserid());
            Integer integer = userInfoService.UpdateUserInfo(userInfo);
            if (integer == 1) {
                return new ResultVo(true, StatusCode.OK, "修改密码成功");
            }
            return new ResultVo(false, StatusCode.ERROR, "修改密码失败");
        }
        return new ResultVo(false, StatusCode.LOGINERROR, "当前密码错误");
    }

    /**
     * 展示用户头像昵称
     */
    @ResponseBody
    @PostMapping("/user/avatar")
    public ResultVo userAvatar( HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        UserInfo userInfo = userInfoService.queryPartInfo(userid);
        return new ResultVo(true, StatusCode.OK, "查询头像成功",userInfo);
    }

    /**
     * 修改头像
     * */
    @PostMapping(value = "/user/updateuimg")
    @ResponseBody
    public JSONObject updateuimg(@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        String filename = UUID.randomUUID().toString().replaceAll("-", "");
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());//获得文件扩展名
        String filenames = filename + "." + ext;//文件全名
        String pathname = "D:\\campusshops\\file\\" + filenames;
        file.transferTo(new File(pathname));
        resUrl.put("src", "/pic/"+filenames);
        res.put("msg", "");
        res.put("code", 0);
        res.put("data", resUrl);
        String uimgUrl = "/pic/" + filenames;

        String userid=(String) session.getAttribute("userid");
        UserInfo userInfo = new UserInfo().setUserid(userid).setUimage(uimgUrl);
        userInfoService.UpdateUserInfo(userInfo);
        return res;
    }

    /**
     * 展示个人信息
     */
    @RequiresPermissions("user:userinfo")
    @GetMapping("/user/lookinfo")
    public String lookinfo(HttpSession session, ModelMap modelMap) {
        String userid = (String) session.getAttribute("userid");
        UserInfo userInfo = userInfoService.LookUserinfo(userid);
        modelMap.put("userInfo",userInfo);
        return "/user/userinfo";
    }

    /**
     * 修改个人信息
     */
    @ResponseBody
    @PostMapping("/user/updateinfo")
    public ResultVo updateInfo(@RequestBody UserInfo userInfo, HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        userInfo.setUserid(userid);
        Integer integer1 = userInfoService.UpdateUserInfo(userInfo);
        if (integer1 == 1) {
            return new ResultVo(true, StatusCode.OK, "修改成功");
        }
        return new ResultVo(false, StatusCode.ERROR, "修改失败");
    }

}

