package study.wyy.springboot.mapper;

import study.wyy.springboot.pojo.Employee;

//@Mapper或者@MapperScan将接口扫描装配到容器中
public interface EmployeeMapper {

     Employee getEmpById(Integer id);

     void insertEmp(Employee employee);
}
