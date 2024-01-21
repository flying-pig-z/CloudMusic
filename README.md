# 喵听

微服务架构的共享音乐APP

## 技术栈

后端采用SpirngBoot+Spring Security+Spring Cloud Alibaba+redis进行开发。采用docker进行部署。

微服务方面选取的具体组件如下：

| 功能         | 采用的组件           |
| ------------ | -------------------- |
| 服务调用     | OpenFeign            |
| 服务注册发现 | Nacos                |
| 网关         | Spring Cloud Gateway |
| 流量控制     | Sentinel             |

## 整体架构

![E}Y}_P%Y_5O%J IRY9}D4OS](https://raw.githubusercontent.com/flying-pig-z/picture-bed/main/img/202401031857007.png?token=A4A35OVE2ATEXMNWUJHEPL3FSU66K)

## 功能亮点

### 安全方面

实现了分布式鉴权，具体架构如下：
![93WF)O6}J0{DCXF4C7 A1IC](https://raw.githubusercontent.com/flying-pig-z/picture-bed/main/img/202401031857716.png?token=A4A35OXHKKIF6BVQAKC37CDFSU66K)


在登出方面，采用了token黑名单实现登出功能【相比白名单而言存储方面更加节省资源】，在每次服务请求网关的时候判断服务的token有无被用户登出加入黑名单。

### 业务功能方面

1.排行榜功能使用Spring Task定时任务实现，并且采用了redis进行优化。
![F 60I YJ@7U DC%3P1SUJP8](https://raw.githubusercontent.com/flying-pig-z/picture-bed/main/img/202401031857681.png?token=A4A35OSG5RZXQJB6BW5DL7DFSU66K)

2.业务逻辑严密，比如对上传文件的格式进行检查，避免重复的点赞和收藏。
