## 1. 项目概述
本系统为二手交易系统，包括前台、用户个人中心、管理员后台三个端

### 1.1 技术框架
**前端**：LayUI + Thymleaf + html + css + ajax

**后端**：SpringBoot + MyBatis + MySQL + tomcat + JDBC

**其他**：使用maven依赖管理工具

### 1.2 功能实现
**前台**
* 查看商品列表：按价格范围筛选、按价格/点击率排序、按类别筛选、按商品名称查询
* 查看商品详情
* 购买
* 加入购物车
* 评论/回复

**管理员端**

* 用户管理：列表查看、权限变更、封禁/解封
* 类别管理：（增删改）
* 商品管理：列表展示、违规/审核通过、按状态查询
* 留言管理：评论和回复的违规/审核通过

**用户个人中心**
* 商品管理：发布闲置、查看我的商品、更新我的商品
* 购物车管理：加入购物车、移除购物车
* 订单管理：查看我卖出的、查看我买到的
* 消息中心：审核消息、留言消息
* 个人信息：更新个人资料、修改密码

### 1.3 环境
**开发工具**

* IDEA社区版（社区版没有js代码高亮，配置tomcat需要安装smart tomcat插件）
* MySQL可视化控制台，我使用的是DBeaver，其他navicat等均可

**后端环境**

* JDK1.8
* maven3
* tomcat8.5
* MySQL8
> 上述环境请务必提前安装好并保证可用

## 2. 部署建议
### 2.1 数据库表准备
本系统涉及12张数据表，相关的建表语句在

src/main/resources/dump-shop-202402050115.sql

新建数据库 shop 并执行上述sql，即可获得数据表及一些测试数据。
### 2.2 修改数据库连接

修改项目中application-prod.yml的mysql账号密码

### 2.3 数据文件准备

在D盘中创建文件夹”campusshops“，将压缩包”file“解压，并复制进campusshops文件夹中

> 文件结构：D://campusshops/file/图片文件，不要复制错了！

### 2.4 运行
确保idea的tomcat已经配置好，pom文件加载完毕，然后就可以运行了！

> 运行的application设置为ShopsApplication


启动完成后，
前台地址：localhost
管理员地址：localhost/admin
> 测试用户：小橘 111111
> 
> 测试管理员：admin 123456

## 3. 主要逻辑简介
### 登录注册
用户可以登录注册，管理员默认直接在数据库中设置

### 浏览
未登录状态下可访问前台，包括商品列表和详情

### 购物
点击”我要购买“后，填写收货地址，即可创建订单

### 发布
新发布的商品需要经过管理员审核，才能够在前台展示

## 4. 运行展示
### 前台
![](src/main/resources/resources/img/1.png)

![](src/main/resources/resources/img/2.png)

![](src/main/resources/resources/img/3.png)

### 个人中心
![](src/main/resources/resources/img/4.png)

![](src/main/resources/resources/img/5.png)

![](src/main/resources/resources/img/6.png)

![](src/main/resources/resources/img/7.png)

![](src/main/resources/resources/img/8.png)

### 管理后台
![](src/main/resources/resources/img/9.png)

![](src/main/resources/resources/img/10.png)

![](src/main/resources/resources/img/11.png)

![](src/main/resources/resources/img/12.png)