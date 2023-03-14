package me.in1978.tools.anywhere.model;

import lombok.Data;
import lombok.experimental.Accessors;
import me.in1978.tools.anywhere.util.CloneBySerializable;

/**
 * 端口映射绑定。 original -> target
 * <p>
 * 如果是从
 */
@Data
@Accessors(chain = true)
public class BindModel implements CloneBySerializable<BindModel> {

    /**
     * 来源的地址
     */
    String oriHost;
    /**
     * 来源的端口
     */
    Integer oriPort;
    /**
     * 要绑定到的网卡地址，如果是null，则自动调整为localhost；如果是*，自动调整为空字符串，表示全部网卡
     */
    String bindAddr;
    /**
     * 要绑定到的网卡端口
     */
    Integer bindPort;

    public static BindModel ins(String oriHost, Integer oriPort, String targetAddr, Integer targetPort) {
        return new BindModel()
                .setOriHost(oriHost).setOriPort(oriPort)
                .setBindAddr(targetAddr).setBindPort(targetPort);
    }

    public static BindModel prepareLocalBind(String host, Integer port) {
        return prepareLocalBind(host, port, 0);
    }

    public static BindModel prepareLocalBind(String host, Integer port, int bindPort) {
        return ins(host, port, "localhost", bindPort);
    }

    public static String fixRemoteBindAddr(String addr) {
        if (addr == null) return "localhost";

        if (addr.equals("*")) return "";

        return addr;
    }

    public static String fixLocalBindAddr(String addr) {
        if (addr != null) {
            if (addr.length() == 0 || addr.equals("*"))
                addr = "0.0.0.0";
            else if (addr.equals("localhost"))
                addr = "127.0.0.1";
        }

        return addr;
    }

    public void fixOnRemoteMode() {
        bindAddr = fixRemoteBindAddr(bindAddr);
    }

    public void fixOnLocalMode() {
        bindAddr = fixLocalBindAddr(bindAddr);
        if (bindPort == null) bindPort = 0;
    }


    public String descForward() {
        return String.format("%s:%s => %s:%s", oriHost, oriPort, bindAddr, bindPort);
    }

}
