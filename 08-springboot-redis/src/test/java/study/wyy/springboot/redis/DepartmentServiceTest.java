package study.wyy.springboot.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import study.wyy.springboot.redis.pojo.Department;
import study.wyy.springboot.redis.service.DepartmentService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author wyaoyao
 * @data 2019-12-17 20:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApp.class)
@Slf4j
public class DepartmentServiceTest {

    @Autowired
    DepartmentService departmentService;

    @Test
    public void testSave(){

        Department department = new Department();
        department.setDepartmentName("市场部");
        departmentService.save(department);
    }

    @Test
    public void testFindById(){
        Department byId = departmentService.findById(1);
        assertThat(byId.getId(),equalTo(1));
    }
}
