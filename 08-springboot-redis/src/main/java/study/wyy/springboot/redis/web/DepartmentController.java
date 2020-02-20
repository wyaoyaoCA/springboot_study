package study.wyy.springboot.redis.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.wyy.springboot.redis.pojo.Department;
import study.wyy.springboot.redis.service.DepartmentService;

/**
 * @author wyaoyao
 * @data 2019-12-09 10:22
 */

@RestController
@Api(description = "jetcache测试", tags = "jetcache测试")
@AllArgsConstructor
@Slf4j
public class DepartmentController {
    private DepartmentService departmentService;


    @GetMapping("/study/wyy/springboot/jetcache/find")
    @ApiOperation("测试---设置缓存---jetcache-@Cache")
    public Department findStudentById(){
        if(log.isInfoEnabled()){
            log.info("根据部门ID查询部门信息");
        }
        return departmentService.findById(1);

    }

    @GetMapping("/study/wyy/springboot/jetcache/del")
    @ApiOperation("测试---删除缓存---jetcache-@CacheInvalidate")
    public void delDepartment(){

        departmentService.delById(1);
    }


    @GetMapping("/study/wyy/springboot/jetcache/edit")
    @ApiOperation("测试---更新缓存---jetcache-@CacheInvalidate")
    public void editDepartment(){

        Department department = new Department();
        department.setDepartmentName("PD");
        department.setId(1);
        departmentService.updateById(department);
    }


}
