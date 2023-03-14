package me.in1978.tools.anywhere.spring.boot;

import lombok.extern.slf4j.Slf4j;
import me.in1978.tools.anywhere.Anywhere;
import me.in1978.tools.anywhere.AnywhereEngine;
import me.in1978.tools.anywhere.AnywhereSession;
import me.in1978.tools.anywhere.model.BindModel;
import me.in1978.tools.anywhere.model.EngineModel;
import me.in1978.tools.anywhere.model.SessionModel;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.SocketException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(AnywhereConf.class)
@ConditionalOnClass(Anywhere.class)
@Slf4j
public class AnywhereBeans {
    @Bean
    @ConditionalOnMissingBean
    public AnywhereEngine anywhereEngine(AnywhereConf conf) throws SocketException, AuthException {
        EngineModel engineModel = conf.getEngineModel();

        if (engineModel == null) {
            log.warn("Engine model not defined, use default");
            engineModel = new EngineModel();
        }

        log.info("Setting up AnywhereEngine {}", engineModel);
        AnywhereEngine engine = Anywhere.factory(engineModel.getType()).createEngine(engineModel);

        for (AnywhereConf.SessionConf sessionConf : conf.retrieveSessions()) {
            SessionModel sessionModel = sessionConf.getSessionModel();

            log.info("Initializing session {}", sessionModel);
            AnywhereSession session = engine.retrieveSession(sessionModel);

            for (BindModel export : sessionConf.retrieveExports()) {
                log.info("Exporting to remote {}, {}", sessionModel.getHost(), export.descForward());
                session.l2rBind(export);
            }

            for (BindModel imp : sessionConf.retrieveImports()) {
                log.info("Importing from remote {}, {}", sessionModel.getHost(), imp.descForward());
                session.r2lBind(imp);
            }
        }

        return engine;
    }
}
