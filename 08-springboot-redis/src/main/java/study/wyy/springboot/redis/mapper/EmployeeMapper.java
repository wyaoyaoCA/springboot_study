package study.wyy.springboot.redis.mapper;

import study.wyy.springboot.redis.pojo.Employee;

//@Mapper或者@MapperScan将接口扫描装配到容器中
public interface EmployeeMapper {

     Employee getEmpById(Integer id);

     void insertEmp(Employee employee);
}
