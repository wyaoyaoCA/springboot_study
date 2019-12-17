## SpringBoot整合Redis

### 1 SpringBoot配置redis

#### 引入依赖
```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### application.yaml配置

```yaml
spring:
  redis:
    port: 6379
    password: xxxx
```

### 2 redisTemplate操作缓存

#### SpringBoot中部分源码的解读
##### RedisAutoConfiguration类
```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@Import({ LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class })
public class RedisAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	@ConditionalOnMissingBean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

}
```
通过源码可以看出，SpringBoot自动帮我们在容器中生成了一个`RedisTemplate`和一个`StringRedisTemplate`。

**但是这个redisTemplate的泛型为<Object,Object>，我们一般需要的是<String,Object>这样的操作，并且，这个RedisTemplate没有设置数据存在Redis时，key及value的序列化方式。**

##### StringRedisTemplate

```java
public class StringRedisTemplate extends RedisTemplate<String, String> {


	public StringRedisTemplate() {
	    // 设置key和value的序列化方式
		setKeySerializer(RedisSerializer.string());
		setValueSerializer(RedisSerializer.string());
		setHashKeySerializer(RedisSerializer.string());
		setHashValueSerializer(RedisSerializer.string());
	}

	public StringRedisTemplate(RedisConnectionFactory connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}

	protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
		return new DefaultStringRedisConnection(connection);
	}
}
```
而StringRedisTemplate的泛型为<String, String>，并在构造方法方法设置了key和value的序列化方式，但是value我们一般采用json的方式进行序列化

##### org.springframework.data.redis.serializer.RedisSerializer

> 这个接口就是SpringBoot为我们提供的序列化方式

![](redis_note_img/SpringBoot提供的redis序列化方式.jpg)

#### 实操一波

> 根据上述分析，我们完全可以配置一个自己的RedisTemplate

##### 配置一个RedisTemplate<String,Student>
- study.wyy.springboot.redis.model.Student 是自定义的实体类，redis缓存该对象的信息

- study.wyy.springboot.redis.config.RedisConfig 配置一个RedisTemplate<String,Student>

```java
package study.wyy.springboot.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import study.wyy.springboot.redis.model.Student;

/**
 * @author wyaoyao
 * @data 2019-12-09 10:04
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Student> studentRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Student> redisTemplate = new RedisTemplate();
        // key的缓存方式
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // 设置value的序列化方式
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;

    }
}
```

### 3 RedisTemplate方法演示

spring中根据redis的数据类型提供了以下几个接口，这几个接口的是实现也都在RedisTemplate中引入了

- private ValueOperations<K, V> valueOps;
- private ListOperations<K, V> listOps;
- private SetOperations<K, V> setOps;
- private ZSetOperations<K, V> zSetOps;

> 这几个接口的是实现也都在RedisTemplate中引入了

```java
	private final ValueOperations<K, V> valueOps = new DefaultValueOperations<>(this);
	private final ListOperations<K, V> listOps = new DefaultListOperations<>(this);
	private final SetOperations<K, V> setOps = new DefaultSetOperations<>(this);
	private final StreamOperations<K, ?, ?> streamOps = new DefaultStreamOperations<>(this, new ObjectHashMapper());
	private final ZSetOperations<K, V> zSetOps = new DefaultZSetOperations<>(this);
	private final GeoOperations<K, V> geoOps = new DefaultGeoOperations<>(this);
	private final HyperLogLogOperations<K, V> hllOps = new DefaultHyperLogLogOperations<>(this);
	private final ClusterOperations<K, V> clusterOps = new DefaultClusterOperations<>(this);
```

#### 3.1 String类型

在RedisTemplate中，已经提供了一个工厂方法:opsForValue()。这个方法会返回这个默认的操作类

主要的api：
- redisTemplate.opsForValue().set(String key,String value)
- String s = redisTemplate.opsForValue().get(String key);
- String oldValue = redisTemplate.opsForValue().getAndSet(String key, String new value);
- redisTemplate.opsForValue().set(K key, V value, long timeout, TimeUnit unit);

测试代码：`study/wyy/springboot/redis/RedisTemplateTest.java`

#### 3.2 哈希类型相关操作




