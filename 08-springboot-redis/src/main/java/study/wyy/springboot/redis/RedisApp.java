package study.wyy.springboot.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author wyaoyao
 * @data 2019-12-09 10:26
 */
@SpringBootApplication
@Slf4j
public class RedisApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RedisApp.class);
        log.info("Swagger-UI: http://127.0.0.1:{}/swagger-ui.html",
                context.getEnvironment().getProperty("server.port", "8080"));
    }
}
