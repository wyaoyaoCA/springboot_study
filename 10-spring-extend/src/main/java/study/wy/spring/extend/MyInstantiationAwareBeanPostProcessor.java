package study.wy.spring.extend;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;
import study.wy.spring.extend.service.MyUserService;

import java.beans.PropertyDescriptor;

/**
 * @author by wyaoyao
 * @Description
 * @Date 2021/2/19 9:53 下午
 * 以为MyUserService为例
 */
@Component
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
    /**
     * 实例化bean之前，相当于new这个bean之前
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if(beanClass.isAssignableFrom(MyUserService.class)){
            System.out.println("postProcessBeforeInstantiation ......");
            System.out.println("postProcessBeforeInstantiation beanClass: " + beanClass);
            System.out.println("postProcessBeforeInstantiation beanName: " + beanName);
        }
        return null;
    }

    /**
     * 实例化bean之后，相当于new这个bean之后
     */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyUserService){
            System.out.println("postProcessAfterInstantiation ......");
            MyUserService userService = (MyUserService) bean;
        }

        return false;
    }

    /**
     *
     *   bean已经实例化完成，在属性注入时阶段触发，@Autowired,@Resource等注解原理基于此方法实现
     */
    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        System.out.println("postProcessPropertyValues ......");
        if(bean instanceof MyUserService){
            System.out.println("postProcessPropertyValues ......");
        }
        return pvs;
    }

    /**
     * 初始化bean之前，相当于把bean注入spring上下文之前
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyUserService){
            System.out.println("postProcessBeforeInitialization ......");
        }
        return bean;
    }
    /**
     * 初始化bean之后，相当于把bean注入spring上下文之后
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyUserService){
            System.out.println("postProcessAfterInitialization ......");
        }
        return bean;
    }
}
