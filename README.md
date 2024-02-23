# 喵听

微服务架构的共享音乐APP

## 友链
前端项目:https://github.com/wuliwudu/MusicApp

UI:https://www.figma.com/file/Y4OJRtIMkpFlonuqF8EFpX/%E5%96%B5%E5%90%AC%EF%BC%88%E5%8F%91%E7%96%AF%E7%8B%82%E6%94%B9%E7%89%88%EF%BC%89?type=design&node-id=223-972&mode=design&t=KDQWV4e8yuRQAxTG-0

接口文档:https://apifox.com/apidoc/shared-d7e55ccc-d4b0-46f4-8092-a027767ed284

## 技术栈

后端采用SpirngBoot+Spring Cloud Alibaba+redis+rabbitmq进行开发。采用docker进行部署。

微服务方面选取的具体组件如下：

| 功能         | 采用的组件           |
| ------------ | -------------------- |
| 服务调用     | OpenFeign            |
| 服务注册发现 | Nacos                |
| 网关         | Spring Cloud Gateway |
| 微服务保护     | Sentinel             |

## 整体架构

![202401031857007](https://github.com/flying-pig-z/CloudMusic/assets/117554874/e2246ba1-6ebf-46ca-a7f0-a572a8c49744)

## 功能亮点

### 安全方面

实现了分布式鉴权，具体架构如下：
![image](https://github.com/flying-pig-z/CloudMusic/assets/117554874/b91c8159-75c8-465c-a681-b58eb4e3fbae)


在登出方面，采用了token黑名单实现登出功能【相比白名单而言存储方面更加节省资源】，在每次服务请求网关的时候判断服务的token有无被用户登出加入黑名单。

网关主要校验逻辑：白名单放行->检查token[是否为空or非法]->token是否在黑名单中已经登出->放行并将用户信息传递给各个微服务。

在各个微服务中将网关传递的用户信息存入TreadLocal中。

### 优化性能方面

1.排行榜功能使用Spring Task定时任务实现，并且采用了redis进行优化。
![202401031857681](https://github.com/flying-pig-z/CloudMusic/assets/117554874/a217bd6b-0004-4d2f-94c4-4f52eb5931a1)

2.业务逻辑严密，比如对上传文件的格式进行检查，避免重复的点赞和收藏。

3.redis优化用户信息查询

![image](https://github.com/flying-pig-z/CloudMusic/assets/117554874/3ba2601e-b6ea-45b7-a473-467fc1e4b6ca)


4.redis优化点赞
![whiteboard_exported_image (2)](https://github.com/flying-pig-z/CloudMusic/assets/117554874/66a57721-56c0-45a7-910d-8163d2d63595)
> 设计思路：<br>
> 【1】最终一致性：一般一致性采用的是Cache Aside Pattern，先更新数据库再删除缓存，但是的话获取某个音乐的点赞集合到redis中是个耗时的操作。在加上更新的频繁，所以不能采用删除缓存。<br>
> 那要不就先更新数据库再更新缓存，要不就先更新缓存再更新数据库。<br>
> 这里采用Write Behind Pattern。先更新缓存，再异步更新数据库。优点是效率很高，数据库压力很小，缺点是异步增大了数据库和缓存无法强一致的概率。<br>
> 【2】缓存穿透：防止数据库中的数据不存在导致这部分请求一直落在数据库。<br>
> 一般的解决方法有两种，一种是缓存空值，但是redis的复杂类型不能为空，另外一种是布隆过滤器，但是布隆过滤器会产生冲突。<br>
> 所以我在redis中也设计了一个变量作为计数器存储点赞总数，来判断缓存不存在是数据过期还是数据库不存在。同时点赞总数也是热点查询数据。<br>
> 【3】竞态问题：使用redission加锁解决竞态造成点赞数更新错误。<br>
> 场景：例如，两个用户同时查看该音乐的点赞数为10，并都想取消点赞，但是由于并发操作，最终点赞数可能只减少了1次而不是2次。<br>

5.mq进行异步解耦优化上传接口
![whiteboard_exported_image (1)](https://github.com/flying-pig-z/CloudMusic/assets/117554874/5a0fd272-ba9c-45c5-96de-2dcfab4f4d66)

这么设计主要是因为前端在用户上传后返回给客户端审核后自动发布，上传文件的时效性并不高。再加上上传接口是个耗时的操作所以采用异步进行解耦。

## 代办

1.es搜索引擎优化搜索

2.xxx-job优化大文件上传

3.设计模式优化代码

