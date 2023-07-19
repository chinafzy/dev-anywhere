package me.in1978.tools.anywhere.spring.boot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.in1978.tools.anywhere.AnywhereEngine;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.SocketException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(DataSourceProperties.class)
@EnableConfigurationProperties({AnywhereConf.class})
public class AnywhereDataSourceSetup implements BeanPostProcessor, InitializingBean, Ordered {
    final AnywhereConf conf;
    final AnywhereEngine engine;
    final ApplicationContext ctx;

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Map.Entry<String, DataSourceProperties> entry : ctx.getBeansOfType(DataSourceProperties.class).entrySet()) {
            String name = entry.getKey();
            DataSourceProperties dsp = entry.getValue();

            log.info("{}: Enhance datasource {}", getName(), name);
            enhanceJdbc(dsp);
        }
    }

    private void enhanceJdbc(DataSourceProperties dsp) throws SocketException, AuthException {
        String url = dsp.getUrl();
        if (!StringUtils.hasText(url)) {
            log.warn("{}: url not found in datasource", getName());
            return;
        }

        String url2 = AnyUrl.tryUrl(url, conf, engine);
        if (url != url2) {
            log.info("{}: Change jdbc url: {} => {}", getName(), url, url2);
            dsp.setUrl(url2);
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
