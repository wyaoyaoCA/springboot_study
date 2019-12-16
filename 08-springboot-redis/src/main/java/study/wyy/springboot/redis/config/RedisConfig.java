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
