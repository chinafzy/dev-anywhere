package me.in1978.tools.anywhere.sample.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class SampleRedissonAppTest {

    @Resource
    RedissonClient client;
    @Resource
    RedisTemplate<String, String> redisTp;

    @Test
    public void test1() {
        System.out.println(redisTp.opsForValue().get("key1"));
        System.out.println(client.getAtomicLong("key1").incrementAndGet());
        System.out.println(redisTp.opsForValue().get("key1"));
    }

}