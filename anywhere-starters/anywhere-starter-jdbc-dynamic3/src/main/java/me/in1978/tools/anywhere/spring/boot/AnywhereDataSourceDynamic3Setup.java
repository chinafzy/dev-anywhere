package me.in1978.tools.anywhere.spring.boot;


import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.in1978.tools.anywhere.AnywhereEngine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(AnywhereBeans.class)
@EnableConfigurationProperties({AnywhereConf.class})
@ConditionalOnClass(DynamicDataSourceProperties.class)
public class AnywhereDataSourceDynamic3Setup implements BeanPostProcessor, InitializingBean, Ordered {

    final AnywhereConf conf;
    final AnywhereEngine engine;
    final ApplicationContext ctx;

    private final Pattern ENC_PATTERN = Pattern.compile("^ENC\\((.*)\\)$");

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Map.Entry<String, DynamicDataSourceProperties> entry : ctx.getBeansOfType(DynamicDataSourceProperties.class).entrySet()) {
            String name = entry.getKey();
            DynamicDataSourceProperties p = entry.getValue();

            log.info("{}: Enhance DynamicDataSourceProperties {}", name, getName());

            for (Map.Entry<String, DataSourceProperty> e : p.getDatasource().entrySet()) {
                String subName = e.getKey();
                DataSourceProperty d2 = e.getValue();

                log.info("{}: Checking {}.{}", getName(), name, subName);

                String url = d2.getUrl();
                if (ENC_PATTERN.matcher(url).find()) {
                    log.warn("{}: encrypted url not supported", getName());
                    break;
                }

                String url2 = AnyUrl.tryUrl(url, conf, engine);
                if (!Objects.equals(url2, url)) {
                    log.info("{}: Enhance url from \n {} to \n {}", getName(), url, url2);
                    d2.setUrl(url2);
                }
            }
        }
    }

    private String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
