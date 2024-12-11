# 喵听

微服务架构的共享音乐APP

## 友链
客户端仓库:https://github.com/flying-pig-z/MTMusic-Client

UI:https://www.figma.com/file/Y4OJRtIMkpFlonuqF8EFpX/%E5%96%B5%E5%90%AC%EF%BC%88%E5%8F%91%E7%96%AF%E7%8B%82%E6%94%B9%E7%89%88%EF%BC%89?type=design&node-id=223-972&mode=design&t=KDQWV4e8yuRQAxTG-0

接口文档:https://apifox.com/apidoc/shared-d7e55ccc-d4b0-46f4-8092-a027767ed284

## 技术栈

后端采用SpirngBoot+Spring Cloud Alibaba+Redis+Rabbitmq+ElasticSearch进行开发。采用Docker进行部署。

微服务方面选取的具体组件如下：

| 功能         | 采用的组件           |
| ------------ | -------------------- |
| 服务调用     | OpenFeign            |
| 服务注册发现 | Nacos                |
| 网关         | Spring Cloud Gateway |
| 微服务保护     | Sentinel             |

## 整体架构

![image](https://github.com/user-attachments/assets/766a8bac-1d27-4ba1-955c-6fcfffb4c2b2)



## 功能亮点

### 安全方面

实现了分布式认证和鉴权，具体架构如下：
![image](https://github.com/user-attachments/assets/a5aa8867-a260-4ed9-a48d-72fa7ef59412)



使用redis维护token，键为用户id，值为token对应的uuid，限制单账号只能一个用户。

网关主要认证校验逻辑：白名单放行->检查token[是否为空or非法]->token是否在redis白名单中->放行并将用户id和权限信息传递给各个微服务。

在各个微服务中将网关传递的用户id和权限信息存入TreadLocal中。

各个微服务授权：结合存储的权限信息和aop熟悉实现授权


### 优化性能方面

1.排行榜功能使用XXX-JOB定时任务实现，并且采用了redis进行优化。
![image](https://github.com/user-attachments/assets/acfff203-47af-41e9-96e0-db928e8804c3)


2.业务逻辑严密，比如对上传文件的格式进行检查，避免重复的点赞和收藏。

3.redis优化用户信息和音乐信息查询

![image](https://github.com/flying-pig-z/CloudMusic/assets/117554874/3ba2601e-b6ea-45b7-a473-467fc1e4b6ca)

4.redis优化点赞
![image](https://github.com/user-attachments/assets/ee66ae21-6e71-4524-9588-08fb25cb9cca)


> 设计思路：<br>

> * 这里设计了两个缓存值。一个是音乐的点赞数量，一个是用户的音乐点赞集合。这两个是最常用查询到的数据。如果需要用户的音乐点赞数量，就直接查询用户音乐点赞集合的大小就够了。
>   如果查询到了就直接返回，查询不到就去数据库查询同步到缓存再返回。
> * 最终一致性：一般一致性采用的是Cache Aside Pattern，先更新数据库再删除缓存，但是的话获取某个用户的点赞集合到redis中是个耗时的操作。在加上更新的频繁，所以不能采用删除缓存。<br>
> 那要不就先更新数据库再更新缓存，要不就先更新缓存再更新数据库。<br>
> 这里采用Write Behind Pattern。先更新缓存，再异步更新数据库。优点是效率很高，数据库压力很小.<br>
> 缺点是异步增大了数据库和缓存无法强一致的概率。比如说当过期的时候去读取，可能使得同一时间点赞或者取消点赞的数据更改并没有同步到缓存。但是的话可以结合前端缓存优化，问题不大。<br>
> * 竞态问题：使用redis的incs和decs解决竞态造成点赞数更新错误。【本来要使用reddision，但是粒度不用那么大】<br>
> 场景：例如，两个用户同时查看该音乐的点赞数为10，并都想取消点赞，但是由于并发操作，最终点赞数可能只减少了1次而不是2次。<br>

5.mq进行异步解耦优化上传接口，并引入幂等组件进行幂等校验
![image](https://github.com/user-attachments/assets/6b74b220-a7e4-415c-990d-a92f7884de44)


这么设计主要是因为前端在用户上传后返回给客户端审核后自动发布，上传文件的时效性并不高。再加上上传接口是个耗时的操作所以采用异步进行解耦。

6.使用ElasticSearch优化搜索

## 代办

实现大文件分片上传

