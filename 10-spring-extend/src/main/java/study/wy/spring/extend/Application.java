package study.wy.spring.extend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author by wyaoyao
 * @Description
 * @Date 2021/2/19 8:49 下午
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        // application.addInitializers(new MyApplicationContextInitializer());
        application.run(args);
    }
}
