## Project Overview(EN)

This project is a digital solution customized for the restaurant industry, aiming to improve operational efficiency and user experience through an integrated system management backend and mini-program frontend.
The project consists of two parts: the system management backend and the mini-program frontend.
The system management backend is mainly used by internal staff of the restaurant to manage categories, dishes, set meals, orders, staff, etc., as well as to conduct data statistics.
The Wechat mini-program frontend is mainly used by customers to browse dishes, add items to the shopping cart, place orders, make payments, and request order updates.
I have implemented all functionalities in the backend except for the payment feature (the payment function requires a government-issued restaurant industry operating qualification, which I cannot implement).

------

## Technology Stack

- **Backend Framework**: Spring Boot 2.7.3
- **Project Management Tool**: Maven
- **Persistence Framework**: MyBatis
- **API Documentation Tool**: Swagger
- **Database**: MySQL, Redis
- **User Authentication**: JWT
- **Pagination Plugin**: PageHelper
- **Logging Framework**: Logback
- **Version Control**: Git
- **Project Hosting**: GitHub: https://github.com/MountKhan/sky-take-out
- **File Storage**: Alibaba Cloud OSS: https://www.alibabacloud.com/en
- **Geolocation Service**: Baidu Map API: https://lbsyun.baidu.com
- **Excel Operations**: Apache POI
- **Mini-Program Development**: WeChat Mini Program: https://mp.weixin.qq.com/?lang=en_US
- **Other Development Tools**: Lombok, API Fox, Git, JUnit, Spring Task

------

## Deployment Steps

1. Clone the repository.

2. Import the project into your IDE (recommended IntelliJ IDEA).

3. Configure the `application.yml` file: fill in the port number, database configuration, Redis, OSS, and other information.

4. Download, extract, and start the frontend project: [nginx-1.20.2.zip](https://kaku-sky-take-out.oss-cn-beijing.aliyuncs.com/nginx-1.20.2.zip)

5. Start the backend project: run `sky-take-out\sky-server\src\main\java\com\sky\SkyApplication.java`.

6. For the user side, please download the official WeChat mini-program development software and import the code: [mp-weixin.zip](https://kaku-sky-take-out.oss-cn-beijing.aliyuncs.com/mp-weixin.zip)

   WeChat mini-program development software download: https://mp.weixin.qq.com/?lang=en_US

------

## Project Highlights

- Login functionality implemented via JWT.
- Supports user registration and order placement on mobile (via WeChat mini-program).
- Integrated Baidu’s geolocation API to customize store locations and define a range of delivery addresses allowed for orders.
- Supports Excel export of operational reports.
- Integrated Alibaba Cloud OSS for file uploads.

------

## API Documentation

After starting the project, access the documentation at: `http://localhost:8080/doc.html`

Alternatively, access the uploaded API documentation on Alibaba Cloud OSS:

- User-side API documentation: https://kaku-sky-take-out.oss-cn-beijing.aliyuncs.com/userAPI.html
- 

## 项目简介（中文）

本项目是专为餐饮企业定制的数字化解决方案，旨在通过一体化的系统管理后台与小程序端应用，
提高餐饮企业的运营效率和用户体验。本项目包括 系统管理后台 和 小程序端应用 两部分。
其中系统管理后台主要提供给餐饮企业内部员工使用，可以对餐厅的分类、菜品、套餐、订单、员工等进行管理维护，对餐厅的各类数据进行统计。
小程序端主要提供给消费者使用，可以在线浏览菜品、添加购物车、下单、支付、催单等。
我实现了后段部分除支付功能外的的全部内容（支付功能的实现需要政府发行的餐饮行业经营资质，故无法实现）。

------

## 技术选型

- **后端框架**：Spring Boot 2.7.3
- **项目管理工具**：Maven
- **持久化框架**：MyBatis
- **接口文档工具**：Swagger
- **数据库**：MySQL、Redis
- **用户认证**：JWT
- **分页插件**：PageHelper
- **日志框架**：Logback
- **版本控制**：Git
- **项目托管**：Github：https://github.com/MountKhan/sky-take-out
- **文件存储**：阿里云 OSS：https://www.alibabacloud.com/en
- **地理位置服务**：百度地图API：https://lbsyun.baidu.com
- **Excel 操作**：Apache POI
- **用户端小程序开发**：微信小程序：https://mp.weixin.qq.com/?lang=en_US
- **分页插件**：PageHelper
- **其他开发工具**：Lombok、API Fox、Git、Junit、Spring Task

------

## 部署步骤

1. 克隆代码

2. 导入到 IDE（推荐 IntelliJ IDEA）

3. 配置 `application.yml` 文件：填写端口号、数据库配置、Redis、OSS 等信息

4. 下载，解应并启动前端项目：https://kaku-sky-take-out.oss-cn-beijing.aliyuncs.com/nginx-1.20.2.zip

5. 启动后端项目：运行  sky-take-out\sky-server\src\main\java\com\sky\SkyApplication.java

6. 用户端请下载微信官方的小程序开发软件并导入代码：https://kaku-sky-take-out.oss-cn-beijing.aliyuncs.com/mp-weixin.zip

   微信小程序开发软件下载：https://mp.weixin.qq.com/?lang=en_US

------

## 项目亮点

- 通过JWT实现登录功能
- 支持手机端用户注册，下单。（通过微信小程序）
- 导入了百度提供的地理坐标接口，可以自定义店铺的地理位置，并自定义限制允许下单的用户住址范围
- 支持运营报表 Excel 导出
- 集成阿里云 OSS 文件上传

------

## 接口文档

启动后访问：`http://localhost:8080/doc.html`

或者访问我上传至阿里云OSS的接口文档：

用户端接口文档：https://kaku-sky-take-out.oss-cn-beijing.aliyuncs.com/userAPI.html

管理端接口文档：https://kaku-sky-take-out.oss-cn-beijing.aliyuncs.com/adminAPI.html
