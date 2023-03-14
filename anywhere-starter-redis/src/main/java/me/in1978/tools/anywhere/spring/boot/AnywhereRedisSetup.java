package me.in1978.tools.anywhere.spring.boot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.in1978.tools.anywhere.AnywhereEngine;
import me.in1978.tools.anywhere.AnywhereSession;
import me.in1978.tools.anywhere.model.BindModel;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.SocketException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

@Component
@EnableConfigurationProperties({RedisProperties.class, AnywhereConf.class})
@RequiredArgsConstructor
@Slf4j
public class AnywhereRedisSetup implements InitializingBean, BeanPostProcessor, Ordered {

    final AnywhereConf anywhereConf;
    final AnywhereEngine engine;
    @Resource
    ApplicationContext ctx;

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Map.Entry<String, RedisProperties> entry : ctx.getBeansOfType(RedisProperties.class).entrySet()) {
            String name = entry.getKey();
            RedisProperties rp = entry.getValue();
            log.info("Enhance redis ps {}", name);
            enhanceRedisProperties(rp);
        }
    }

    private void enhanceRedisProperties(RedisProperties rp) throws SocketException, AuthException {

        for (AnywhereConf.SessionConf cnf : anywhereConf.retrieveSessions()) {
            if (enhanceHost(cnf, rp)) {
                return;
            }
        }

        String url = rp.getUrl();
        if (StringUtils.hasText(url)) {
            log.info("Found redis url: {}", url);

            String url2 = AnyUrl.tryUrl(url, anywhereConf, engine);
            if (url2 != url) {
                log.info("Change redis url: {} => {}", url, url2);
                rp.setUrl(url2);
            }

        }

    }

    private boolean enhanceHost(AnywhereConf.SessionConf conf, RedisProperties rp) throws SocketException, AuthException {
        log.info("Setting up proxy for redis {}", conf.getSessionModel());

        AnywhereSession session = engine.retrieveSession(conf.getSessionModel());

        String host = rp.getHost();
        int port = rp.getPort();
        if (!conf.match(host)) {
            log.info("No matching {} -> {}", conf.getPattern(), host);
            return false;
        }

        BindModel bindModel = BindModel.ins(host, port, "localhost", 0);
        session.r2lBind(bindModel);
        log.info("Change redis connection {}:{} => {}:{}",
                bindModel.getOriHost(), bindModel.getOriPort(), bindModel.getBindAddr(), bindModel.getBindPort());

        rp.setHost(bindModel.getBindAddr());
        rp.setPort(bindModel.getBindPort());
        return true;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
