package study.wyy.springboot.redis.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wyaoyao
 * @data 2019-12-09 10:00
 */
@Data
public class Student implements Serializable {

    private Long id;
    private Integer age;
    private String name;
    private Date birthday;

}
