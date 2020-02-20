package study.wyy.springboot.redis.jedistest;

import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @author wyaoyao
 * @data 2020-02-20 14:38
 */
public class CrudApiTest {

    /**
     * 测试连接
     */
    @Test
    public void testConnection() {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        String result = jedis.auth("wyy123");
        String ping = jedis.ping();
        Assert.assertEquals(result,"OK");
        Assert.assertEquals(ping,"PONG");
        jedis.close();
    }



    /**
     * 测试设置获取数据
     */
    @Test
    public void testGetAndSet() {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        jedis.auth("wyy123");
        String set = jedis.set("name", "大师兄");
        System.out.println(set);
        Assert.assertEquals(set,"OK");
        // 获取
        String name = jedis.get("name");
        Assert.assertEquals(name,"大师兄");

    }

}
