package study.wyy.springboot.retry.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.*;
import org.springframework.retry.policy.CircuitBreakerRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wyaoyao
 * @data 2020-03-09 16:39
 */
@Slf4j
public class RetryTemplateTest {

    public static void main(String[] args) throws Exception {
        //test1();
        //testRecoveryCallback();
        //testSimpleRetryPolicy();
        testCircuitBreakerRetryPolicy();
    }

    public static void test1() throws MyException {
        RetryTemplate retryTemplate = new RetryTemplate();
        AtomicInteger counter = new AtomicInteger();

        // 定义 retryCallback, 使用匿名内部类
        /*RetryCallback<Integer,MyException> retryCallback = new RetryCallback(){
            @Override
            public Integer doWithRetry(RetryContext context) throws Throwable {
                // 内部默认重试策略是最多尝试3次
                if(counter.incrementAndGet() < 3){
                    // counter等于1 抛出异常，进行重试（1）
                    // 再次执行doWithRetry方法的逻辑，此时counter为2 依旧小于3 再次重试（2）
                    // 再次执行doWithRetry方法的逻辑，此时counter为3 通过重试 返回counter
                    // 如果改成小于4，则最后会抛出MyException这个异常，因为第三次还是失败，内部默认重试策略是最多尝试3次
                    log.info("counter -> {}", counter);
                    log.info("重试第{}次",context.getRetryCount());
                    throw new MyException("自定义异常");
                }
                // 加1操作
                return counter.get();
            }

        };*/
        // 上面可以改成lambda表达式
        RetryCallback<Integer,MyException> retryCallback = context->{
            // 内部默认重试策略是最多尝试3次
            if(counter.incrementAndGet() < 3){
                // counter等于1 抛出异常，进行重试（1）
                // 再次执行doWithRetry方法的逻辑，此时counter为2 依旧小于3 再次重试（2）
                // 再次执行doWithRetry方法的逻辑，此时counter为3 通过重试 返回counter
                // 如果改成小于4，则最后会抛出MyException这个异常，因为第三次还是失败，内部默认重试策略是最多尝试3次
                log.info("counter -> {}", counter);
                log.info("重试第{}次",context.getRetryCount());
                throw new MyException("自定义异常");
            }
            // 加1操作
            return counter.get();
        };
        Integer result = retryTemplate.execute(retryCallback);
        log.info("result -> {}",result);
    }

    /**
     * 上面的例子简单改了下，改为调用包含RecoveryCallback入参的execute()，
     * RetryCallback内部也改为了即使尝试了3次后仍然会失败。此时将转为调用RecoveryCallback，
     * RecoveryCallback内部通过RetryContext获取了尝试次数，此时RetryCallback已经尝试3次了，
     * 所以RetryContext获取的尝试次数是3，RecoveryCallback的返回结果30将作为execute()的返回结果。
     * @throws MyException
     */
    public static void testRecoveryCallback() throws MyException {
        RetryTemplate retryTemplate = new RetryTemplate();
        AtomicInteger counter = new AtomicInteger();
        RetryCallback<Integer, MyException> retryCallback = retryContext -> {
            //内部默认重试策略是最多尝试3次，即最多重试两次。还不成功就会抛出异常。
            if (counter.incrementAndGet() < 4) {
                throw new MyException("自定义异常");
            }
            return counter.get();
        };

        RecoveryCallback<Integer> recoveryCallback = retryContext -> {
            //返回的应该是30。RetryContext.getRetryCount()记录的是尝试的次数，一共尝试了3次。
            return retryContext.getRetryCount() * 10;
        };
        //尝试策略已经不满足了，将不再尝试的时候会抛出异常。此时如果指定了RecoveryCallback将执行RecoveryCallback，
        //然后获得返回值。
        Integer result = retryTemplate.execute(retryCallback, recoveryCallback);
        log.info("result -> {}",result);
    }


    public static void testSimpleRetryPolicy() {
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap();
        // 我们可以指定对所有的RuntimeException都是可重试的，唯独IllegalArgumentException是一个例外。
        retryableExceptions.put(RuntimeException.class, true);
        retryableExceptions.put(IllegalArgumentException.class, false);
        // 指定尝试10次
        RetryPolicy retryPolicy = new SimpleRetryPolicy(10, retryableExceptions);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        AtomicInteger counter = new AtomicInteger();
        retryTemplate.execute(retryContext -> {
            if (counter.incrementAndGet() < 3) {
                throw new IllegalStateException();
            } else if (counter.incrementAndGet() < 6) {
                // 由于IllegalArgumentException不会重试，所以最终会抛出这个异常
                throw new IllegalArgumentException();
            }
            return counter.get();
        });
    }



    public static void testCircuitBreakerRetryPolicy() throws Exception {
        CircuitBreakerRetryPolicy retryPolicy = new CircuitBreakerRetryPolicy();
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        AtomicInteger counter = new AtomicInteger();
        RetryState retryState = new DefaultRetryState("key");
        for (int i=0; i<5; i++) {
            try {
                retryTemplate.execute(retryContext -> {
                    System.out.println(LocalDateTime.now() + "----" + counter.get());
                    TimeUnit.MILLISECONDS.sleep(100);
                    if (counter.incrementAndGet() > 0) {
                        throw new IllegalStateException();
                    }
                    return 1;
                }, null, retryState);
            } catch (Exception e) {

            }
        }
    }



    static class MyException extends Exception{
        public MyException(String message) {
            super(message);
        }
    }
}
