package me.in1978.tools.anywhere.spring.boot;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.in1978.tools.anywhere.AnywhereEngine;
import me.in1978.tools.anywhere.AnywhereSession;
import me.in1978.tools.anywhere.model.BindModel;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.SocketException;
import me.in1978.tools.anywhere.util.CloneBySerializable;

import java.net.URI;
import java.util.Objects;

@Data
@Accessors(chain = true)
@Slf4j
public class AnyUrl implements CloneBySerializable<AnyUrl> {
    String schema;
    String userInfo;
    String host;
    Integer port;
    String path;
    String query;

    public static AnyUrl parse(String s) {
        AnyUrl ret = new AnyUrl();

        ret.schema = s.substring(0, s.indexOf("://"));

        URI u = URI.create("http" + s.substring(ret.schema.length()));
        ret.userInfo = u.getUserInfo();
        ret.host = u.getHost();
        ret.port = u.getPort() == -1 ? null : u.getPort();
        ret.path = u.getPath();
        ret.query = u.getQuery();

        return ret;
    }

    public static String tryUrl(String url, AnywhereConf.SessionConf conf, AnywhereEngine engine) throws SocketException, AuthException {
        AnyUrl u = AnyUrl.parse(url);
        String host = u.getHost();
        if (!conf.match(host)) {
            return url;
        }

        Integer port = u.getPort();
        AnywhereSession session = engine.retrieveSession(conf.getSessionModel());
        BindModel b = BindModel.prepareLocalBind(host, port);
        session.r2lBind(b);
        log.info("Make proxy {}:{} => {}:{}", b.getOriHost(), b.getOriPort(), b.getBindAddr(), b.getBindPort());

        u.setHost(b.getBindAddr()).setPort(b.getBindPort());
        return u.toUrl();
    }

    public static String tryUrl(String url, AnywhereConf conf, AnywhereEngine engine) throws SocketException, AuthException {
        for (AnywhereConf.SessionConf sessionConf : conf.retrieveSessions()) {
            String url2 = tryUrl(url, sessionConf, engine);
            if (!Objects.equals(url2, url)) {
                return url2;
            }
        }

        return url;
    }

    public static void main(String[] args) {

        for (String url : new String[]{
                "redis://192.168.1.1:6379/0?abc=1&bcd=2",
                "redis://user1:pass1@11.11.11.11/",
                "ftp://user:pass@10.10.10.10",
                "jdbc:mysql://10.0.24.2:3306/stock?useUnicode=true&characterEncoding=utf"
        }) {
            System.out.println(url);
            AnyUrl anyUrl = AnyUrl.parse(url);
            System.out.println(anyUrl);
            String url2 = anyUrl.toUrl();
            if (!url.equals(url2)) {
                System.err.println("ERROR:" + url2);
            }
        }

    }

    public String toUrl() {
        StringBuilder ret = new StringBuilder();

        ret.append(schema).append("://");

        if (userInfo != null) ret.append(userInfo).append("@");

        ret.append(host);

        if (port != null) ret.append(":").append(port);

        if (path != null) ret.append(path);

        if (query != null) ret.append("?").append(query);

        return ret.toString();
    }

}
