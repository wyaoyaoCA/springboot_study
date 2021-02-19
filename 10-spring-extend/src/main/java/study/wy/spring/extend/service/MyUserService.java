package study.wy.spring.extend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import study.wy.spring.extend.dao.UserDao;

/**
 * @author by wyaoyao
 * @Description
 * @Date 2021/2/19 9:54 下午
 */
@Component
public class MyUserService {


    private final UserDao userDao;

    @Autowired
    public MyUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save(){
        userDao.save();
    }
}
