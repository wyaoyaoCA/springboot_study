package study.wyy.springboot.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import study.wyy.springboot.service.UserService;

/**
 * @author wyaoyao
 * @data 2019-10-26 21:16
 */
@Configuration
public class BeanConfig {

    @Bean
    public UserService userService(){
        return new UserService();
    }
}
