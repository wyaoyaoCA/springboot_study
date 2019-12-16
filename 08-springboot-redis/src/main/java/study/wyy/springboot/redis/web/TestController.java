package study.wyy.springboot.redis.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.wyy.springboot.redis.cache.StudentCache;
import study.wyy.springboot.redis.model.Student;
import study.wyy.springboot.redis.service.StudentService;

/**
 * @author wyaoyao
 * @data 2019-12-09 10:22
 */

@RestController
@Api(description = "后台测试", tags = "后台测试")
@AllArgsConstructor
public class TestController {
    private final StudentService studentService;
    private final StudentCache studentCache;


    @GetMapping("/study/wyy/springboot/redis/test_1")
    @ApiOperation("测试---缓存---redisTemplate<String,Student>")
    public Student findStudentById(){
        return studentService.selectStudent();

    }


    @GetMapping("/study/wyy/springboot/redis/test_del")
    @ApiOperation("测试---删除缓存---redisTemplate<String,Student>")
    public String delStudentCache(){
         studentCache.del();
        return "SUCCESS";
    }


}
