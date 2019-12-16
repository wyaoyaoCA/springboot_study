package study.wyy.springboot.redis.service;

import com.sun.org.apache.xerces.internal.impl.xs.util.ShortListImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import study.wyy.springboot.redis.cache.StudentCache;
import study.wyy.springboot.redis.model.Student;

import java.util.Date;

/**
 * @author wyaoyao
 * @data 2019-12-09 10:19
 */
@Service
@AllArgsConstructor
@Slf4j
public class StudentService {

    private final StudentCache studentCache;


    public Student selectStudent(){
        // 0 从缓存中获取
        Student studentFromRedis = studentCache.getStudent();
        if(studentFromRedis != null){
            return studentFromRedis;
        }

        // 这里只是模拟查询
        Student student = new Student();
        student.setAge(18);
        student.setName("wyy");
        student.setBirthday(new Date());
        // 放入缓存
        studentCache.saveStudent(student);
        return student;
    }
}
