  ##Store_fjw1.0 本地修改版本测试
 
  ###store_fjw-user用户服务端口8080
    
   127.0.0.1  com.jxau.store
    
   store-redission-test 8001
    
   store-user-web 用户服务端前端端口号8080
   store-user-service服务端端口号8070
     
     
   store-manage-web服务端口8081
   store-manage-service服务端口8071
     
     
   store-item-web前台的商品详情展示 8082
   store-item-service前台的商品详情服务 8072服务未构建
     
     
   store-search-web 商品搜索服务端口8083
   store-search-service 搜索服务端口8073
     
     
   store-cart-web 购物车服务的前台 8084
   store-cart-service 搜索服务的后台 8074
    
    
   store-password-web服务  用户认证服务中心端口8085
   store-user-service 用户服务的service层8070
   
   
   store-order-web 订单 8086
   store-order-service 订单服务 8076
   商品发布后台管理是为购物平台开发的信息管理系统，对商品信息进行管理。
   拟京东商城是类似于京东商城的网上购物平台，为用户提供网上购物的平台。
   该项目是基于SOA分布式架构搭建而成，将所有的controller、service接口、service实现都在一个工程，
   通过Spring的ioc就可以实现互相调用，通过dubbo+zookeeper实现各个服务之间的调用。
   2、软件工具及技术
   开发环境为idea+mysql5.7+tomcat9+maven+jdk1.8+git+dubbo2.6+zookeeper3.4+nginx1.12+linux， 
   采用的技术有Springboot 、TkMapper 、Redis 、FastDFS 文件存储服务器、elasticsearch 搜索服务器、activeMQ消息中间件
   3、功能模块
   商品信息模块、商品详情模块、商品搜索模块、商品购物车模块、用户登录模块、用户支付模块、商品库存模块。
   商品信息模块：采用FastDFS文件存储服务器存储商品库存单元的图片信息。   
   商品详情模块：采用Redis存储商品数据，减小mysql数据库压力。
   商品搜索模块：采用elasticsearch搜索服务器+kibana实现商城内的全文搜素。
   用户登录模块：实现用户单点登录及社交登录，并用md5对用户密码进行密文加密。
   用户支付模块：采用基于消息的，采取最终一致性策略的分布式事务(消息队列MQ)保证用户的支付安全。