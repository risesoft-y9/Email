<p align="center">
 <img alt="logo" src="https://vue.youshengyun.com/files/img/qrCodeLogo.png">
</p>
<p align="center">基于SpringBoot+Vue前后端分离的电子邮件</p>
<p align="center">
 <a href='https://gitee.com/risesoft-y9/y9-email/stargazers'><img src='https://gitee.com/risesoft-y9/y9-email/badge/star.svg?theme=dark' alt='star'></img></a>
    <img src="https://img.shields.io/badge/version-v9.6.6-yellow.svg">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg">
    <img alt="logo" src="https://img.shields.io/badge/Vue-3.3-red.svg">
    <img alt="" src="https://img.shields.io/badge/JDK-11-green.svg">
    <a href="https://gitee.com/risesoft-y9/y9-core/blob/master/LICENSE">
<img src="https://img.shields.io/badge/license-GPL3-blue.svg"></a>
<img src="https://img.shields.io/badge/total%20lines-367k-blue.svg">
</p>

## 简介

电子邮件是一款简化的具备邮件服务器的企业邮箱，支持在将其他主流邮箱的邮件进行导入后自主控制邮件数据安全。电子邮件以其简洁精确的功能和小巧安全的架构方便根据企业和政府机构的业务要求进行二次开发。电子邮件是一个完全开源的项目，无商业版，但是需要依赖开源的数字底座进行人员岗位管控。[系统在线体验----->>>>>](#在线体验)

## 开源地址

源码地址：<a href="https://github.com/y9digitalbase/Email" target="_blank">https://github.com/y9digitalbase/Email </a>

## 源码目录

```
y9-email -- 电子邮件模块
 ├── risenet-y9boot-webapp-webmail -- 电子邮件webapp
vue -- 前端工程
 ├── y9vue-webmail  -- 电子邮件前端工程
```

## 产品特点 ##

### 简化模块功能

电子邮件去除繁琐且使用率低的功能，归还用户一个简洁有效的功能列表，支持所有常规的发送、草稿、抄送、收藏、检索、分类等功能，同时专注于文本撰写的兼容性、一键回复、密送、快速查看、通讯录等细节功能。

### 国产私有化部署

电子邮件支持在国产化环境中进行私有化部署并且支持容器化集群。电子邮件需要单独的邮件服务器从而支持对于邮件协议的解析。

### 内部通讯录

电子邮件与数字底座结合，在同步人员的情况下，具备内部每个用户的独立邮箱，可更加安全的管理内部权限

## 功能架构图 ##

![功能架构图](https://vue.youshengyun.com/files/img/电子邮件功能架构图.png)

## 功能模块

| 序&nbsp;号 | 特&nbsp;点&nbsp;名&nbsp;称 | 特点描述                                                                                                          |
|----------|------------------------|---------------------------------------------------------------------------------------------------------------|
| 1        | 系统设置                   | 可直接对电子邮件网站进行锁屏、搜索、刷新、全屏、设置（布局、语言、主题、菜单背景、菜单动画、菜单宽度、头部固定、进度条、刷新、搜索、通知、全屏、页面动画、锁屏等）、返回首页、退出等操作。                 |
| 2        | 写信                     | 支持图文并茂的正文编辑，支持多个附件、大附件的上传，支持发送、存草稿、取消、抄送密送。                                                                   |
| 3        | 收件箱                    | 支持查阅收到的所有邮件，并可查看邮件状态、收藏、发件人、收件人、邮件标题、有无附件、时间、大小等信息。                                                           |
| 4        | 草稿箱                    | 草稿箱中可保存已编辑但未发送的邮件。可查看邮件的状态、收藏/取消收藏、发件人、收件人、邮件标题、有无附件、时间、大小等信息。可对选中邮件进行“移动到”“标记为”“查看”“删除”、邮件查询和刷新页面的操作。        |
| 5        | 发件箱                    | 发件箱中可查看已发送的邮件，可查看邮件的状态、收藏/取消收藏、发件人、收件人、邮件标题、有无附件、时间、大小等信息。可对选中邮件进行“移动到”“标记为”“查看”“删除”、邮件查询和刷新页面的操作。            |
| 6        | 垃圾箱                    | 垃圾箱存储用户常规删除的邮件。可对选中邮件进行“移动到”、“标记为”、“查看”、“彻底删除”、邮件查询和刷新页面的操作。                                                  |
| 7        | 收藏夹                    | 用户关心的邮件可在邮件标题“收藏”列下进行标记，在收件箱、草稿箱、发件箱、垃圾箱、都可以对邮件进行“收藏”标记，标记的邮件会在收藏夹处显示。                                        |
| 8        | 通讯录                    | 由用户所属的组织架构所组成，通过通讯录，可以方便地获得并查看组织中联系人的个人信息。                                                                    |
| 9        | 邮件查询                   | 支持从邮件主体、邮件正文、状态、附件名、所在的文件夹、发件人、收件人、起止时间进行邮件查询。单击邮件，进入邮件详情，支持返回、回复、全部回复、转发、收藏/取消收藏、上一封邮件、下一封邮件、附件批量下载、快捷回复等操作。 |
| 10       | 文件夹                    | 可以整齐规范，并更好地保存邮件。                                                                                              |
| 11       | 邮件导入                   | 支持将各种邮件协议下的历史邮件压缩包一次性上传导入。                                                                                    |

## 后端技术选型

| 序号 | 依赖              | 版本      | 官网                                                                                                                 |
|----|-----------------|---------|--------------------------------------------------------------------------------------------------------------------|
| 1  | Spring Boot     | 2.7.10  | <a href="https://spring.io/projects/spring-boot" target="_blank">官网</a>                                            |
| 2  | SpringDataJPA   | 2.7.10  | <a href="https://spring.io/projects/spring-data-jpa" target="_blank">官网</a>                                        |
| 3  | SpringDataRedis | 2.7.10  | <a href="https://spring.io/projects/spring-data-redis" target="_blank">官网</a>                                      |
| 4  | SpringKafka     | 2.8.11  | <a href="https://spring.io/projects/spring-kafka" target="_blank">官网</a>                                           |
| 5  | nacos           | 2.2.1   | <a href="https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html" target="_blank">官网</a>                        |
| 6  | druid           | 1.2.16  | <a href="https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5" target="_blank">官网</a>                          |
| 7  | Jackson         | 2.13.5  | <a href="https://github.com/FasterXML/jackson-core" target="_blank">官网</a>                                         |
| 8  | javers          | 6.13.0  | <a href="https://github.com/javers/javers" target="_blank">官网</a>                                                  |
| 9  | lombok          | 1.18.26 | <a href="https://projectlombok.org/" target="_blank">官网</a>                                                        |
| 10 | logback         | 1.2.11  | <a href="https://www.docs4dev.com/docs/zh/logback/1.3.0-alpha4/reference/introduction.html" target="_blank">官网</a> | 

## 前端技术选型

| 序号 | 依赖           | 版本      | 官网                                                                     |
|----|--------------|---------|------------------------------------------------------------------------|
| 1  | vue          | 3.3.4   | <a href="https://cn.vuejs.org/" target="_blank">官网</a>                 |
| 2  | vite         | 4.4.9   | <a href="https://vitejs.cn/" target="_blank">官网</a>                    |
| 3  | vue-router   | 4.0.13  | <a href="https://router.vuejs.org/zh/" target="_blank">官网</a>          |
| 4  | pinia        | 2.0.11  | <a href="https://pinia.vuejs.org/zh/" target="_blank">官网</a>           |
| 5  | axios        | 0.24.0  | <a href="https://www.axios-http.cn/" target="_blank">官网</a>            |
| 6  | typescript   | 4.5.4   | <a href="https://www.typescriptlang.org/" target="_blank">官网</a>       |
| 7  | core-js      | 3.20.1  | <a href="https://www.npmjs.com/package/core-js" target="_blank">官网</a> |
| 8  | element-plus | 2.2.29  | <a href="https://element-plus.org/zh-CN/" target="_blank">官网</a>       |
| 9  | sass         | 1.58.0  | <a href="https://www.sass.hk/" target="_blank">官网</a>                  |
| 10 | animate.css  | 4.1.1   | <a href="https://animate.style/" target="_blank">官网</a>                |
| 11 | vxe-table    | 4.3.5   | <a href="https://vxetable.cn" target="_blank">官网</a>                   |
| 12 | echarts      | 5.3.2   | <a href="https://echarts.apache.org/zh/" target="_blank">官网</a>        |
| 13 | svgo         | 1.3.2   | <a href="https://github.com/svg/svgo" target="_blank">官网</a>           |
| 14 | lodash       | 4.17.21 | <a href="https://lodash.com/" target="_blank">官网</a>                   |

## 中间件选型

| 序号 | 工具               | 版本   | 官网                                                                        |
|----|------------------|------|---------------------------------------------------------------------------|
| 1  | JDK              | 11   | <a href="https://openjdk.org/" target="_blank">官网</a>                     |
| 2  | Tomcat           | 9.0+ | <a href="https://tomcat.apache.org/" target="_blank">官网</a>               | 
| 3  | filezilla server | 1.7+ | <a href="https://www.filezilla.cn/download/server" target="_blank">官网</a> |

## 数据库选型

| 序号 | 工具    | 版本         | 官网                                                         |
|----|-------|------------|------------------------------------------------------------|
| 1  | Mysql | 5.7 / 8.0+ | <a href="https://www.mysql.com/cn/" target="_blank">官网</a> |
| 2  | Redis | 6.2+       | <a href="https://redis.io/" target="_blank">官网</a>         |

## 信创兼容适配

| 序号 | 类型   | 对象                 |
|:---|------|--------------------|
| 1  | 浏览器  | 奇安信、火狐、谷歌、360等     |
| 2  | 插件   | 金山、永中、数科、福昕等       |
| 3  | 中间件  | 东方通、金蝶、宝兰德等        |
| 4  | 数据库  | 人大金仓、达梦、高斯等        |
| 5  | 操作系统 | 统信、麒麟、中科方德等        |
| 6  | 芯片   | ARM体系、MIPS体系、X86体系 |

## 在线体验

### 电子邮件

**演示地址
**：<a href="https://test.youshengyun.com/webmail/" target="_blank">https://test.youshengyun.com/webmail/ </a>

> 演示账号：测试人员 密码：Risesoft@2024
>

## 文档专区

待补充

## 电子邮箱截图

![收件箱](https://vue.youshengyun.com/files/img/电子邮件收件箱.png)

![通讯录](https://vue.youshengyun.com/files/img/电子邮件通讯录.png)

![写信](https://vue.youshengyun.com/files/img/电子邮件写信.png)

![邮件详情](https://vue.youshengyun.com/files/img/电子邮件邮件详情.png)

## 同构开源项目

| 序&nbsp;号 | 项&nbsp;目&nbsp;名&nbsp;称&nbsp; | 项目介绍                                                                                                                                          | 地&nbsp;址                                                                     |
|:---------|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------|
| 1        | 数字底座                         | 数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用。 | <a href="https://gitee.com/risesoft-y9/y9-core" target="_blank">码云地址</a>     |
| 2        | 数据流引擎                        | 数据流引擎是一款面向数据集成、数据同步、数据交换、数据共享、任务配置、任务调度的底层数据驱动引擎。数据流引擎采用管执分离、多流层、插件库等体系应对大规模数据任务、数据高频上报、数据高频采集、异构数据兼容的实际数据问题。                                 | <a href="https://gitee.com/risesoft-y9/y9-dataflow" target="_blank">码云地址</a> |

## 赞助与支持

### 中关村软件和信息服务产业创新联盟

官网：<a href="https://www.zgcsa.net" target="_blank">https://www.zgcsa.net</a>

### 北京有生博大软件股份有限公司

官网：<a href="https://www.risesoft.net/" target="_blank">https://www.risesoft.net/</a>

### 中国城市发展研究会

官网：<a href="https://www.china-cfh.com/" target="_blank">https://www.china-cfh.com/</a>

## 咨询与合作

联系人：曲经理

微信号：qq349416828

备注：开源咨询-姓名
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/img/曲经理统一二维码咨询.png"><div/>
联系人：有生博大-咨询热线


座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>


