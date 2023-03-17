package me.in1978.tools.anywhere.model;

import lombok.Data;
import lombok.experimental.Accessors;
import me.in1978.tools.anywhere.util.CloneBySerializable;

/**
 * PortBinding Model。 oriHost:oriPort => bindAddr:bindPort
 * Bind a remote host+port to address(network interface) of local machine,
 * or bind a local host+port to address(network interface) of remote machine.
 * <pre>
 * E.g (suppose that remote machine is 10.10.10.111), Bind remote to local:
 *  10.10.10.10:3306 => localhost:3306
 *  null:3306 => localhost:3306     --> 10.10.10.111:3306 => localhost:3306
 *  10.10.10.10:3306 => null:3306   --> 10.10.10.10:3306 => localhost:3306
 *  10.10.10.10:3306 => null:null   --> 10.10.10.10:3306 => localhost:[random_port]
 *  10.10.10.10:3306 => null:0      --> 10.10.10.10:3306 => localhost:[random_port]
 *
 *
 *  </pre>
 */
@Data
@Accessors(chain = true)
public class BindModel implements CloneBySerializable<BindModel> {

    public static final String LOCALHOST = "127.0.0.1";
    public static final String ALL_ADDRESS = "*";


    /**
     *
     */
    String oriHost = "127.0.0.1";
    /**
     * 来源的端口
     */
    Integer oriPort;
    /**
     * null => 127.0.0.1
     * [empty_string] => "*", Bind to all
     */
    String bindAddr = "127.0.0.1";
    /**
     * 要绑定到的网卡端口，设置为0或者null，则自动分配。
     */
    Integer bindPort;

    /**
     * Prepare an BindModel for PortBinding.
     * oriHost:oriPort => bindAddr:bindPort
     *
     * @param oriHost
     * @param oriPort
     * @param bindAddr
     * @param bindPort
     * @return
     */
    public static BindModel ins(String oriHost, Integer oriPort, String bindAddr, Integer bindPort) {
        return new BindModel()
                .setOriHost(oriHost).setOriPort(oriPort)
                .setBindAddr(bindAddr).setBindPort(bindPort);
    }

    /**
     * Prepare a remote-to-locale PortBinding.
     * oriHost:oriPort => localhost:<random_port>
     *
     * @param oriHost remote
     * @param oriPort 远程的端口
     * @return
     */
    public static BindModel prepareLocalBind(String oriHost, Integer oriPort) {
        return prepareLocalBind(oriHost, oriPort, 0);
    }

    /**
     * Prepare a remote-to-local PortBinding.
     * oriHost:oriPort => localhost:bindPort
     *
     * @param oriHost
     * @param oriPort
     * @param bindPort
     * @return
     */
    public static BindModel prepareLocalBind(String oriHost, Integer oriPort, Integer bindPort) {
        return ins(oriHost, oriPort, "localhost", bindPort);
    }

    public static String fixBindAddr(String addr) {
        if (addr == null) return LOCALHOST;

        if (addr.equals("")) return ALL_ADDRESS;

        return addr;
    }

    public void fixBeforeBinding() {
        if (oriPort == null || oriPort < 0) throw new IllegalArgumentException("oriPort must > 0: " + oriPort);

        bindAddr = fixBindAddr(bindAddr);
        oriHost = fixBindAddr(oriHost);
        if (bindPort == null) bindPort = 0;
    }


    public String descForward() {
        return String.format("%s:%s => %s:%s", oriHost, oriPort, bindAddr, bindPort);
    }

}
