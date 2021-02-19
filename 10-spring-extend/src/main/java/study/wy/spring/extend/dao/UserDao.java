package study.wy.spring.extend.dao;

import org.springframework.stereotype.Component;

/**
 * @author by wyaoyao
 * @Description
 * @Date 2021/2/19 9:55 下午
 */
@Component
public class UserDao {

    public void save() {
        System.out.println("模拟保存");
    }
}
