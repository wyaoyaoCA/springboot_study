package study.wyy.springboot.redis.cache;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import study.wyy.springboot.redis.model.Student;

/**
 * @author wyaoyao
 * @data 2019-12-09 10:11
 */
@AllArgsConstructor
@Component
@Slf4j
public class StudentCache {

    private final RedisTemplate<String, Student> studentRedisTemplate;

    private final String KEY = "study:wyy:redis:study";

    public void saveStudent(Student student){
        studentRedisTemplate.opsForValue().set(KEY,student);
        log.info("将学生信息放入缓存 => {}",student);
    }

    public Student getStudent(){
        Student student = studentRedisTemplate.opsForValue().get(KEY);
        log.info("从缓存中去取出学生信息为 => {}",student);
        return student;
    }

    public void del(){
        Boolean delete = studentRedisTemplate.delete(KEY);
        log.info("删除缓存");
    }


}
