package me.in1978.tools.anywhere.spring.boot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.in1978.tools.anywhere.AnywhereEngine;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.SocketException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties({AnywhereConf.class, DataSourceProperties.class})
public class AnywhereDataSourceSetup implements BeanPostProcessor, InitializingBean, Ordered {
    final AnywhereConf conf;
    final AnywhereEngine engine;
    @Resource
    ApplicationContext ctx;

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Map.Entry<String, DataSourceProperties> entry : ctx.getBeansOfType(DataSourceProperties.class).entrySet()) {
            String name = entry.getKey();
            DataSourceProperties dsp = entry.getValue();

            log.info("Enhance datasource {}", name);
            enhanceJdbc(dsp);
        }
    }

    private void enhanceJdbc(DataSourceProperties dsp) throws SocketException, AuthException {
        String url = dsp.getUrl();
        if (!StringUtils.hasText(url)) {
            log.warn("url not found in datasource");
            return;
        }

        String url2 = AnyUrl.tryUrl(url, conf, engine);
        if (url != url2) {
            log.info("Change jdbc url: {} => {}", url, url2);
            dsp.setUrl(url2);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
