package me.in1978.tools.anywhere.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AnywhereRedisSetup.class)
@ConditionalOnBean(AnywhereAuto.class)
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class AnywhereRedisAuto {
}
