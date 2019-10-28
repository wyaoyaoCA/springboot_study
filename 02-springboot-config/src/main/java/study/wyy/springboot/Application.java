package study.wyy.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * @author wyaoyao
 * @data 2019-10-26 20:10
 */
//@ImportResource(locations = {"classpath:bean.xml"})
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class);
        boolean userService = run.containsBean("userService");
        System.out.println(userService);
    }
}
