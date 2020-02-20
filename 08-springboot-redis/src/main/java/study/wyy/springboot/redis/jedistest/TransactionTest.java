package study.wyy.springboot.redis.jedistest;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @author wyaoyao
 * @data 2020-02-20 14:59
 * Transaction测试
 */
public class TransactionTest {


    @Test
    public void tets(){
        Jedis jedis = new Jedis("127.0.0.1",6379);
        jedis.auth("wyy123");

        // 1 开启事务
        Transaction transaction = jedis.multi();
        // 2 设置数据
        transaction.set("user","张三");
        // 3 提交
        // transaction.exec();
        // 回滚
        transaction.discard();

        jedis.close();


    }

}
