package me.in1978.tools.anywhere.spring.boot;

import me.in1978.tools.anywhere.Anywhere;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AnywhereBeans.class})
@ConditionalOnClass(Anywhere.class)
@ConditionalOnProperty(value = "enable", prefix = AnywhereConf.PREFIX, havingValue = "true", matchIfMissing = true)
public class AnywhereAuto {
}
