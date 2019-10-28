package study.wyy.springboot.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import study.wyy.springboot.config.Person;


/**
 * @author wyaoyao
 * @data 2019-10-26 20:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestConfig {
    @Autowired
    Person person;

    @Test
    public void test1(){
        System.out.println(person);
    }
    @Autowired
    ApplicationContext applicationContext;
    @Test
    public void test2(){
        boolean userService = applicationContext.containsBean("userService");
        System.out.println(userService);

    }

}
