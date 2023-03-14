package me.in1978.tools.anywhere.spring.boot;

import lombok.Data;
import lombok.experimental.Accessors;
import me.in1978.tools.anywhere.model.BindModel;
import me.in1978.tools.anywhere.model.EngineModel;
import me.in1978.tools.anywhere.model.SessionModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@ConfigurationProperties(AnywhereConf.PREFIX)
@Accessors(chain = true)
public class AnywhereConf {
    public static final String PREFIX = "tools.anywhere";
    EngineModel engineModel;
    List<SessionConf> sessions;
    SessionConf session;

    public List<SessionConf> retrieveSessions() {
        List<SessionConf> ret = new ArrayList<>();
        if (session != null) {
            ret.add(session);
        }
        if (sessions != null) {
            ret.addAll(sessions);
        }

        return ret;
    }

    @Data
    public static class SessionConf {
        String name;
        SessionModel sessionModel;
        String pattern;
        BindModel l2rBind;
        List<BindModel> l2rBinds;
        BindModel r2lBind;
        List<BindModel> r2lBinds;

        public void setImport(BindModel imp) {
            this.r2lBind = imp;
        }

        public boolean match(String host) {
            if (!StringUtils.hasText(pattern)) {
                return true;
            }

            Matcher matcher = Pattern.compile(pattern).matcher(host);
            if (!matcher.find()) {
                return false;
            }

            return matcher.start() == 0;
        }

        public List<BindModel> retrieveExports() {
            List<BindModel> ret = new ArrayList<>();

            if (l2rBind != null) ret.add(l2rBind);
            if (l2rBinds != null) ret.addAll(l2rBinds);

            return ret;
        }

        public List<BindModel> retrieveImports() {
            List<BindModel> ret = new ArrayList<>();

            if (r2lBind != null) ret.add(r2lBind);
            if (r2lBinds != null) ret.addAll(r2lBinds);

            return ret;
        }
    }

}
