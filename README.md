# zhihu_come
## 好物推荐订单提醒

![](http://oss.stupidzhang.com/zhihu/haowu/20210130130930.png)

## 搭建条件
如果自己搭建需要具备下面三个条件

- 微信公众号
- 自己的域名
- 云服务器

## 具体操作
### 申请微信测试消息号
#### 登陆微信公众号管理平台
具体操作如下
1.左侧菜单 【开发】---【开发者工具】--【公共平台测试消息号】
如下图
![](http://oss.stupidzhang.com/zhihu/haowu/20210130131259.png)

#### 微信测试公众号测试消息配置

申请好，之后，会分配一堆密钥值

![](http://oss.stupidzhang.com/zhihu/haowu/20210130131635.png)

#### 配置域名和token认证
#### 域名解析

在nginx 服务中 将配置你的域名 80 或 443 端口 解析到 Ip：8891

#### token 认证

地址填入：http:域名/wx/portal/{appId} ，appId 就是微信给的 appID
Token 自定义 即可

![](http://oss.stupidzhang.com/zhihu/haowu/20210130132239.png)

将这appID,appsecret, Token 三个值填入项目的配置文件中，application.yml

![](http://oss.stupidzhang.com/zhihu/haowu/20210130132546.png)

#### 关注测试号和配置模版

#### 关注和新建模版
![](http://oss.stupidzhang.com/zhihu/haowu/20210130132738.png)

```
有新订单啦~	
数量：{{first.DATA}}，预估佣金：{{keyword1.DATA}} 
总金额：{{keyword2.DATA}} 
明细：{{keyword3.DATA}}
```
这是我的模版。可以供读者参考

#### 配置
#### 登陆京东联盟获取appKey 和 secretKey


[京东联盟](https://union.jd.com/overview)

![](http://oss.stupidzhang.com/zhihu/haowu/20210130133736.png)

将上面的关注列表中自己的openId和模版的模板ID，以及京东联盟的 appKey 和secretKey 填入到配置为文件中

![](http://oss.stupidzhang.com/zhihu/haowu/20210130133026.png)

### 部署项目
部署ok之后，需要在微信公众测试消息号认证一下
![](http://oss.stupidzhang.com/zhihu/haowu/20210130134115.png)


### 测试
因为没打算写页面所以可以使用接口测试


http://域名/api/send?openId=发送的对象&orderTimeStr=时间&interval=间隔

- 发送对象：即是关注列表中的openId
- orderTimeStr： 订单时间，可以找自己京粉上最近的订单记录，由该时间向前推 interval 个分钟，时间格式：2021-01-30 12:20:00
- interva：时间间隔，默认10分钟，最大不能超过60十分钟，京东该接口限制查询范围最多一个小时，不过可以指定时间