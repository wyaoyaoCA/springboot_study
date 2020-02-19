package study.wyy.springboot.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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
        // springBoot 2.0以后可以这配置
        //redisTemplate.setKeySerializer(RedisSerializer.string());
        //redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // 设置value的序列化方式
        // redisTemplate.setValueSerializer(RedisSerializer.json());
        //redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setHashKeySerializer(stringRedisSerializer());
        // 设置value的序列化方式
        redisTemplate.setValueSerializer(jsonRedisSerializer());
        redisTemplate.setHashValueSerializer(jsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;

    }


    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }
    @Bean
    public GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }


}
