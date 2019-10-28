package study.wyy.springboot;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void test1(){
        rocketMQTemplate.convertAndSend("boot_learn_wyy","hello springboot rocketmq");
    }
}
