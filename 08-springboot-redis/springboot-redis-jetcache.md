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


### 方法缓存注解

> 为了演示效果，配置mybatis，沿用05-springboot-mybatis模块

`study.wyy.springboot.redis.service.DepartmentService`

详细配置可参考[官方文档](https://github.com/alibaba/jetcache/wiki/MethodCache_CN)



### 高级缓存API

#### CacheBuilder
CacheBuilder提供使用代码直接构造Cache实例的方式，使用说明看这里。
**如果没有使用Spring，可以使用CacheBuilder，否则没有必要直接使用CacheBuilder。**

#### Builder
**JetCache2版本的@Cached和@CreateCache等注解都是基于Spring4.X版本实现的，**在没有Spring支持的情况下，注解将不能使用。
但是可以直接使用JetCache的API来创建、管理、监控Cache，多级缓存也可以使用。

#### 创建缓存
创建缓存的操作类似guava/caffeine cache，例如下面的代码创建基于内存的LinkedHashMapCache：
```java
Cache<String, Integer> cache = LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder()
                .limit(100)
                .expireAfterWrite(200, TimeUnit.SECONDS)
                .buildCache();
```

创建RedisCache：

```java
GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(2);
        pc.setMaxIdle(10);
        pc.setMaxTotal(10);
        JedisPool pool = new JedisPool(pc, "localhost", 6379);
Cache<Long, OrderDO> orderCache = RedisCacheBuilder.createRedisCacheBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE)
                .jedisPool(pool)
                .keyPrefix("orderCache")
                .expireAfterWrite(200, TimeUnit.SECONDS)
                .buildCache();
```
#### 多级缓存

在2.2以后通过下面的方式创建多级缓存：

Cache multiLevelCache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder()
      .addCache(memoryCache, redisCache)
      .expireAfterWrite(100, TimeUnit.SECONDS)
      .buildCache();
      
实际上，使用MultiLevelCache可以创建多级缓存，它的构造函数接收的是一个Cache数组（可变参数）。
Cache memoryCache = ...
Cache redisCache = ...
Cache multiLevelCache = new MultiLevelCache(memoryCache, redisCache);

