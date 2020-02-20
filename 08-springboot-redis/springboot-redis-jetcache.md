## jetcache

### 介绍

JetCache是一个基于Java的缓存系统封装，提供统一的API和注解来简化缓存的使用。 
JetCache提供了比SpringCache更加强大的注解，可以原生的支持TTL、两级缓存、分布式自动刷新，还提供了Cache接口用于手工缓存操作。

全部特性:

- 通过统一的API访问Cache系统
- 通过注解实现声明式的方法缓存，支持TTL和两级缓存
- 通过注解创建并配置Cache实例
- 针对所有Cache实例和方法缓存的自动统计
- Key的生成策略和Value的序列化策略是可以配置的
- 分布式缓存自动刷新，分布式锁 (2.2+)
- 异步Cache API (2.2+，使用Redis的lettuce客户端时)
- Spring Boot支持

- github地址
    - ![github地址](https://github.com/alibaba/jetcache/wiki/Home_CN)


### 环境搭建（SpringBoot）

#### 基本配置

> 引入依赖

```xml
<dependency>
    <groupId>com.alicp.jetcache</groupId>
    <artifactId>jetcache-starter-redis</artifactId>
    <version>2.5.14</version>
</dependency>
```
> application.yml配置

```yaml
jetcache:
  statIntervalMinutes: 15
  areaInCacheName: false
  local:
    default:
      type: linkedhashmap
      keyConvertor: fastjson
  remote:
    default:
      type: redis
      keyConvertor: fastjson
      valueEncoder: java
      valueDecoder: java
      poolConfig:
        minIdle: 5
        maxIdle: 20
        maxTotal: 50
      host: 127.0.0.1
      port: 6379

```

> 开启Cached和CreateCache注解

- `@EnableCreateCacheAnnotation`
- `@EnableMethodCache(basePackages = "com.company.mypackage")`


### 核心注解

> 为了演示效果，配置mybatis，沿用05-springboot-mybatis模块


#### 
