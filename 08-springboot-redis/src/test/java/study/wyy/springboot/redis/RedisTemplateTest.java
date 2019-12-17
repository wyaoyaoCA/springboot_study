package study.wyy.springboot.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author wyaoyao
 * @data 2019-12-16 16:10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApp.class)
@Slf4j
public class RedisTemplateTest {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    final String OpsForValueKey = "wyy.opsForValue:test";

    final String OpsForValueTimeOutKey = "wyy.opsForValue:test:timeout";


    @Test
    public void testOpsForValue(){
        // redis存入数据
        redisTemplate.opsForValue().set(OpsForValueKey,"helloWorld");
        // 设置过期时间
        redisTemplate.opsForValue().set(OpsForValueTimeOutKey,"helloWorld",60L, TimeUnit.SECONDS);

        // 获取值
        String s = redisTemplate.opsForValue().get(OpsForValueKey);
        log.info("redis中的值为 => {}", s);

        // 获取旧值设置新值，返回的是之前的值
        String s1 = redisTemplate.opsForValue().getAndSet(OpsForValueKey, "wyaoyao");

        log.info("redis中的旧值为 => {}", s1);
        String s2 = redisTemplate.opsForValue().get(OpsForValueKey);
        log.info("redis中的设置的新值为 => {}", s2);

        redisTemplate.opsForValue().append(OpsForValueKey," java");
        String s3 = redisTemplate.opsForValue().get(OpsForValueKey);
        log.info("append => {}", s3);


    }

    /**
     * 测试一些公共方法
     */
    public void testCommon(){

    }


}
