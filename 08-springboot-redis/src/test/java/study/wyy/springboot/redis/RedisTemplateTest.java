package study.wyy.springboot.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author wyaoyao
 * @data 2019-12-16 16:10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApp.class)
public class RedisTemplateTest {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Test
    public void testPath(){
        redisTemplate.opsForValue().set("wyy.verify-sms-code:notice-token:login:mobile:13105320532","1234");
    }

}
