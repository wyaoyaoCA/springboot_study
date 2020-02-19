package study.wyy.springboot.redis.service;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CreateCache;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import study.wyy.springboot.redis.mapper.DepartmentMapper;
import study.wyy.springboot.redis.pojo.Department;

/**
 * @author wyaoyao
 * @data 2019-12-17 19:59
 */
@Service
@AllArgsConstructor
@Slf4j
public class DepartmentService {


    private final DepartmentMapper departmentMapper;




    public void save(Department department){
        departmentMapper.insertDept(department);
    }


    @Cached(name = "departmentCache", key = "#departId", expire = 60000)
    public Department findById(Integer departId){
        Department deptById = departmentMapper.getDeptById(departId);
        return deptById;

    }


    @CacheInvalidate(name = "departmentCache", key = "#departId")
    public void delById(int departId) {
        // 模拟删除，这个时候应该清楚指定id的缓存
        if(log.isInfoEnabled()){
            log.info("删除部门ID为 [{}] 的部门", departId);
        }
    }

    @CacheUpdate(name = "departmentCache", key = "#department.id", value = "#department")
    public void updateById(Department department) {

        departmentMapper.updateDept(department);
    }
}
