package study.wyy.springboot.redis.jedistest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

/**
 * @author wyaoyao
 * @data 2020-04-14 16:33
 * 延时队列
 */
public class RedisDelayingQueue<T> {

    private Jedis jedis;
    private String queueKey;
    private Type TaskType = new TypeReference<TaskItem<T>>() { }.getType();
    public RedisDelayingQueue(Jedis jedis, String queueKey) {
        this.jedis = jedis;
        this.queueKey = queueKey;
    }

    public void delay(T msg){
        TaskItem task = new TaskItem();
        task.id = UUID.randomUUID().toString();
        task.msg = msg;
        // fastjson 序列化
        String s = JSON.toJSONString(task);
        // 塞入延时队列 ,5s 后再试
        jedis.zadd(queueKey, System.currentTimeMillis() + 5000, s);
    }

    public void loop() {
        while(!Thread.interrupted()){
            // 只取一条
            Set values = jedis.zrangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
            if (values.isEmpty()) {
                try {
                    Thread.sleep(500); // 歇会继续
                }
                catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            String s = (String) values.iterator().next();
            if (jedis.zrem(queueKey, s) > 0) { // 抢到了
                TaskItem task = JSON.parseObject(s, TaskType); // fastjson 反序列化
                this.handleMsg((T) task.msg);
            }
        }
    }
    public void handleMsg(T msg) {
        System.out.println(msg);
    }

    static class TaskItem<T> {
        public String id;
        public T msg;
    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        jedis.auth("wyy123");
        RedisDelayingQueue queue = new RedisDelayingQueue<>(jedis, "q-demo");

        Thread producer = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println("推送消息：codehole" + i);
                    queue.delay("codehole" + i);
                }
            }
        };

        Thread consumer = new Thread() {
            @Override
            public void run() {
                queue.loop();
            }
        };

        producer.start();
        consumer.start();

        try {
            producer.join();
            Thread.sleep(6000);
            consumer.interrupt();
            consumer.join();
        }
        catch (InterruptedException e) {
        }
    }
}
