package me.in1978.tools.anywhere.spring.boot;


import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ConditionalOnBean(AnywhereAuto.class)
@Import({AnywhereDataSourceSetup.class, AnywhereDynamicDataSourceSetup.class})
public class AnywhereDataSourceAuto {
}
