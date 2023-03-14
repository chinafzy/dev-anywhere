package me.in1978.tools.anywhere.model;

import lombok.Data;
import lombok.experimental.Accessors;
import me.in1978.tools.anywhere.util.CloneBySerializable;
import me.in1978.tools.anywhere.util.Utils;

@Data
@Accessors(chain = true)
public class SessionModel implements CloneBySerializable<SessionModel> {
    private String user;
    private String pass;
    private String host;
    private Integer port = 22;
    private boolean strictHost;

    public static SessionModel ins(String host, Integer port, String user, String pass, boolean strictHost) {
        return new SessionModel()
                .setHost(host).setPort(port)
                .setUser(user).setPass(pass)
                .setStrictHost(strictHost);
    }

    public static SessionModel ins(String host, Integer port, String user, String pass) {
        return ins(host, port, user, pass, false);
    }

    public String calId() {
        return Utils.digest(toString());
    }
}
