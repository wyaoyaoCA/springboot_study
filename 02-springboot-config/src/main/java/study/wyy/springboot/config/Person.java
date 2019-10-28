package study.wyy.springboot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wyaoyao
 * @data 2019-10-26 17:41
 */
/**
 * 将配置文件中配置的每一个属性的值，映射到这个组件中
 * @author terminus
 * @ConfigurationProperties：告诉SpringBoot将本类中的所有属性和配置文件中相关的配置进行绑定；
 *      prefix = "person"：配置文件中哪个下面的所有属性进行一一映射
 *  @Component
 * 只有这个组件是容器中的组件，才能容器提供的@ConfigurationProperties功能；
 *
 */
@Component
@ConfigurationProperties(prefix = "person")
@PropertySource(value = {"classpath:person.properties"})
@Data
public class Person {

    private String name;
    private int age;
    private List<String> hobby;
    private Map<String,String> info;

}
