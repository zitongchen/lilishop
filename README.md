## Lilishop 商城系统


### 商城介绍
**官网**：https://pickmall.cn

Lilishop商城系统支持商家入驻，后端基于SpringBoot 研发，前端使用 Vue、uniapp开发， **系统全端全部代码开源**

前后端分离，支持分布式部署，支持Docker，各个API独立，并且有独立的消费者。

### 商城 API/消费者 聚合版
api不需要单独部署，只需启动一个jar包就可以正常运转 如有需要，可以点击跳转
https://gitee.com/beijing_hongye_huicheng/lilishop-simplify

### 开发/使用/常见问题 帮助文档

https://docs.pickmall.cn


#### 欢迎交流需求，交流业务，交流技术（基础问题自行解决，其他问题先看文档后提问）

#### 开发新手或者不熟悉的同学在群内提问或新开Issue提问前，请先阅读[【提问的智慧】](https://github.com/ryanhanwu/How-To-Ask-Questions-The-Smart-Way/blob/master/README-zh_CN.md)，并确保已查阅过 [【在线文档】](https://docs.pickmall.cn/) ，避免浪费大家的宝贵时间；

##### 交流 微信群 1群 
![微信群](https://lilishop-wechat.oss-cn-beijing.aliyuncs.com/wechat.jpg)


##### 体验 公众号/小程序/APP 体验，扫描二维码

![image-20210511171611793](https://static.pickmall.cn/images/h5-qrcode.png)

[![star](https://gitee.com/beijing_hongye_huicheng/lilishop/badge/star.svg?theme=dark)](https://gitee.com/beijing_hongye_huicheng/lilishop/stargazers)
&nbsp;&nbsp;![github](https://img.shields.io/github/stars/hongyehuicheng/lilishop.svg?style=social&logo=#181717)


#### PS： **演示站点所有环境均部署master分支。如果有演示站点问题，可以反馈，如果演示站点没问题本地运行有问题，需自行处理** 


### 项目地址

gitee : https://gitee.com/beijing_hongye_huicheng

github 镜像: https://github.com/lilishop?tab=repositories

商城UI 项目下3个文件夹 
buyer：买家PC端，seller：商家端，manager：后台管理端


### 演示地址
PS：手机验证码为 ‘111111’

**平台管理端**：https://admin-b2b2c.pickmall.cn 账号：admin/123456

**店铺管理端**：https://store-b2b2c.pickmall.cn 账号：13011111111/111111

**商城PC页面**：https://pc-b2b2c.pickmall.cn

**商城 小程序/公众号/APP**：扫描二维码

![image-20210511171611793](https://static.pickmall.cn/images/h5-qrcode.png)

### 快速本地部署

[点击跳转](https://docs.pickmall.cn/deploy/%E8%BF%90%E8%A1%8C%E7%8E%AF%E5%A2%83%E5%87%86%E5%A4%87.html)

**商城数据库**
使用docker-compose部署数据库，自动初始化数据库，不需要手动下载等操作

如果手动部署，才需要获取sql [点击跳转](https://gitee.com/beijing_hongye_huicheng/docker/tree/master/init/mysql) （PS：这里有与tag版本一致的sql，如果是历史版本，则docker项目也切换至历史版本获取sql即可，历史版本升级则根据java相聚的根目录DB目录下的升级sql，按需执行）。



### 功能列表



#### 平台管理端功能
 

| 模块 <img width=80/> | 功能    |
|--------------------|-----------------------------------------------------------------| 
| 首页                 | 平台基础信息统计、待办事项                                                   |
| 会员                 | 会员列表、评价列表、积分历史、会员资金、会员充值                                        |
| 订单                 | 商品订单、虚拟订单、订单售后、订单投诉、售后原因维护、收款流水、退款流水                            |
| 商品                 | 商品列表、商品审核、商品分类、商品品牌、商品规格、商品计量单位                                 |
| 促销                 | 优惠券、券活动（每日&每月&每周&邀新 赠券）、秒杀活动、砍价活动、拼团活动、积分商品                     |
| 店铺                 | 店铺管理、店铺审核、店铺结算、店铺对账                                             |
| 运营                 | 楼层装修、分销商管理、文章管理、意见反馈、站内信、短信、搜索热词管理                              |
| 统计                 | 会员统计、订单统计、流量统计、商品销量统计                                           |
| 设置                 | 菜单管理、角色管理、部门管理、管理员管理、系统设置、行政地区管理、OSS管理、联合登陆、支付、物流公司、敏感词、验证码资源 |



#### 卖家功能


| 模块 <img width=80/> | 功能                            |
|----|-------------------------------| 
| 首页 | 店铺基础信息统计、待办事项、店铺公告            |
| 商品 | 商品发布、商品列表、商品模板、店铺分类           |
| 订单 | 商品订单、虚拟订单、订单评价、订单投诉、退款申请、退货申请 |
| 财务 | 店铺对账、店铺结算、发票管理                |
| 促销 | 优惠券、满额优惠、秒杀、拼团 、分销商品、分校订单     |
| 统计 |单统计、流量统计、商品销量统计                                         |

| 设置 | 配送公司、物流模板、店铺设置、店铺自提设置、PC装修、移动端装修、店员管理、部门管理、角色管理 |
| 消息 | 站内信 |


### 商城前端功能展示

#### 商城移动端

<img src="https://static.pickmall.cn/images/other/app.gif" alt="移动端功能展示" style="zoom:50%;" />

#### 平台管理端

![管理端功能展示](https://static.pickmall.cn/images/other/manager.gif)


### 技术选型

#### 架构图

![技术选型](https://lili-system.oss-cn-beijing.aliyuncs.com/docs/%E6%9E%B6%E6%9E%84.png)

##### 后台技术选型

| 说明           | 框架            | 说明           |               |
| -------------- | --------------- | -------------- | ------------- |
| 基础框架       | Spring Boot     | MVC框架        | Spring MVC    |
| 持久框架       | Mybatis-Plus    | 程序构建       | Maven         |
| 关系型数据库   | MySQL           | 消息中间件AMQP | RocketMQ      |
| 缓存           | Redis +MongoDB  | 搜索引擎       | Elasticsearch |
| 安全框架       | Spring Security | 数据库连接池   | Druid         |
| 数据库分库分表 | sharding        | 定时任务       | xxl-job       |
| 负载均衡       | Nginx           | 静态资源       | 阿里云OSS     |
| 短信           | 阿里云短信      | 认证           | JWT           |
| 日志处理       | Log4j           | 接口规范       | RESTful       |

##### 前端-运营后台、店铺后台

| 说明       | 框架       | 说明       | 框架    |
| ---------- | ---------- | ---------- | ------- |
| 构建工具   | webpack    | JS版本     | ES6     |
| 基础JS框架 | Vue.js     | 视频播放器 | Dplayer |
| 路由管理   | Vue Router | 状态管理   | Vuex    |
| 基础UI库   | iView      | UI界面基于 | iView   |
| 网络请求   | axios      |            |         |

##### 前端-移动端

| 说明      | 架构    | 说明     | 架构    |
| --------- | ------- | -------- | ------- |
| 基础UI库  | uViewui | 基础框架 | uni-app |
| CSS预处理 | scss    | 地图引擎 | amap    |

### 版本升级

```
系统后续会提供多场景解决方案。
更多架构：微服务、Saas、中台等，都会支持。 支持差价升级商业授权
```

### 商业授权
商业版本与开源版本代码一致，没有区分

商业使用需要授权，授权方式可选择联系官网客服。

商业授权模式为永久授权，支持永久升级。

商业案例由于涉及部分多层二开关系，如需了解可以咨询销售。


### 开源须知
1.仅允许用于个人学习研究使用.

2.禁止将本开源的代码和资源进行任何形式任何名义的出售.

3.软件受国家计算机软件著作权保护（登记号：2021SR0805085）。

4.限制商用，如果需要商业使用请联系我们。



### 附录
有人有自己的学习视频、学习记录文档、希望宣传关联开源项目等均可以私聊仓库所有者。

类似：

清晨敲代码同学的分析: https://blog.csdn.net/vaevaevae233/category_12103567.html