package me.in1978.tools.anywhere.impl.jsch;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.in1978.tools.anywhere.AnywhereSession;
import me.in1978.tools.anywhere.model.BindModel;
import me.in1978.tools.anywhere.model.SessionModel;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.ForwardException;
import me.in1978.tools.anywhere.tr.SocketException;
import me.in1978.tools.anywhere.util.StringUtils;
import me.in1978.tools.anywhere.util.Utils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Slf4j
@Accessors(chain = true)
@Data
class AnywhereSessionJschImpl implements AnywhereSession {

    transient final private AnywhereEngineJschImpl anywhereEngineJschImpl;
    private final SessionModel conf;
    transient final private Object sessionLock = new Object();
    private final List<BindModel> l2rBindings = new CopyOnWriteArrayList<>(), r2lBindings = new CopyOnWriteArrayList<>();
    private final Random rnd = new Random();
    transient private Session jschSession;
    private String id;

    private static <T> void removeFrom(List<T> list, Predicate<T> predicate) {
        synchronized (list) {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (predicate.test(list.get(i))) {
                    list.remove(i);
                }
            }
        }
    }

    /**
     * 获取session，如果连接断开了，会尝试自动修复
     *
     * @param autoFix
     * @return
     * @throws JSchException
     */
    Session retrieveJschSession(boolean autoFix) throws SocketException, AuthException {
        synchronized (sessionLock) {
            if ((jschSession == null || !jschSession.isConnected()) && autoFix) {
                jschSession = createJschSession0();
            }
        }

        return jschSession;
    }

    @Override
    public void l2rBind(BindModel model) throws SocketException, AuthException {
        l2rBind0(model);
    }

    @Override
    public void l2rUnbind(String bindAddr, Integer bindPort) {
        l2rUnbind0(bindAddr, bindPort);
    }

    @Override
    public List<BindModel> l2rBindings() {
        return Utils.cloneBySer(l2rBindings);
    }

    @Override
    public void r2lBind(BindModel model) throws SocketException, AuthException {
        r2lBind0(model);
    }

    @Override
    public void r2lUnbind(String bindAddr, Integer bindPort) {
        r2lUnbind0(bindAddr, bindPort);
    }

    @Override
    public List<BindModel> r2lBindings() {
        return Utils.cloneBySer(r2lBindings);
    }

    @Override
    public String runRemote(String cmd) throws SocketException, AuthException {
        try {
            return new JschRemoteHelper(retrieveJschSession(true)).runJschCommand(cmd);
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    public void autoFix() {
        synchronized (sessionLock) {
            if (jschSession == null || jschSession.isConnected()) return;

            try {
                log.info("Restoring session on {}:{} {}:{}", conf.getHost(), conf.getPort(), conf.getUser(), conf.getPass());
                jschSession = createJschSession0();
            } catch (AuthException | SocketException e) {
                log.error("Fail on restoring session " + conf, e);
            }
        }
    }

    public boolean isConnected() {
        return jschSession != null && jschSession.isConnected();
    }

    private Session createJschSession0() throws AuthException, SocketException {
        Session ret = null;
        try {
            ret = anywhereEngineJschImpl.getJsch().getSession(conf.getUser(), conf.getHost(), conf.getPort());
        } catch (JSchException e) {
            // JSCH throws wrong exception here.
            //  Null argument check should throw IllegalArgumentException.
            throw new RuntimeException(e);
        }

        if (!conf.isStrictHost()) {
            ret.setConfig("StrictHostKeyChecking", "no");
        }
        if (StringUtils.hasText(conf.getPass())) {
            ret.setPassword(conf.getPass());
        }

        try {
            ret.setServerAliveInterval(10000);
            ret.connect();
        } catch (JSchException e) {
            if (AnywhereFactoryJschImpl.isAuthException(e)) throw new AuthException(e);
            else throw new SocketException(e);
        }

        for (BindModel l2r : l2rBindings) {
            log.info("Restore remote forward: {}", l2r);

            try {
                ret.setPortForwardingR(l2r.getBindAddr(), l2r.getBindPort(), l2r.getOriHost(), l2r.getOriPort());
            } catch (Exception ex) {
                log.error("Fail on remote forwarding:" + l2r, ex);

                ret.disconnect();
                throw new ForwardException(ex);
            }
        }

        for (BindModel r2l : r2lBindings) {
            log.info("Restore local forwarding: {}", r2l);

            try {
                ret.setPortForwardingL(r2l.getBindAddr(), r2l.getBindPort(), r2l.getOriHost(), r2l.getOriPort());
            } catch (Exception ex) {
                log.error("Fail on local forwarding: {}", r2l);

                ret.disconnect();
                throw new ForwardException(ex);
            }
        }

        return ret;
    }

    /**
     * remote to local bind
     *
     * @param model
     * @return
     * @throws JSchException
     */
    private void r2lBind0(BindModel model) throws SocketException, AuthException {
        model.fixBeforeBinding();

        int p = 0;
        try {
            p = retrieveJschSession(true).setPortForwardingL(model.getBindAddr(), model.getBindPort(), model.getOriHost(), model.getOriPort());
        } catch (JSchException e) {
            throw new ForwardException(e);
        }

        model.setBindPort(p);
        r2lBindings.add(model.clone2());
    }

    private void r2lUnbind0(String bindAddr, Integer bindPort) {
        String bindAddr2 = BindModel.fixBindAddr(bindAddr);

        if (jschSession != null && jschSession.isConnected()) {
            try {
                jschSession.delPortForwardingL(bindAddr2, bindPort);
            } catch (JSchException e) {
                log.error("Fail on unbinding local forward " + bindAddr2 + ":" + bindPort, e);
//                throw new RuntimeException(e);
            }
        }

        removeFrom(r2lBindings, binding -> bindAddr2.equals(binding.getBindAddr()) && bindPort.equals(binding.getBindPort()));
    }

    /**
     * local to remote bind
     *
     * @param model
     * @throws JSchException
     */
    private void l2rBind0(BindModel model) throws SocketException, AuthException {
        model.fixBeforeBinding();

        if (model.getBindPort() != null && model.getBindPort() != 0) {
            try {
                retrieveJschSession(true)
                        .setPortForwardingR(String.format("%s:%s:%s:%s", model.getBindAddr(), model.getBindPort(), model.getOriHost(), model.getOriPort()));
                l2rBindings.add(model.clone2());
                return;
            } catch (JSchException e) {
                throw new ForwardException(e);
            }
        }

        final int MAX_TRIES = 100;
        for (int i = 0; i < MAX_TRIES; i++) {
            int port = rnd.nextInt(62000) + 3000;
            BindModel model2 = model.clone2();
            model2.setBindPort(port);

            try {
                l2rBind0(model2);
                model.setBindPort(model2.getBindPort());
                return;
            } catch (ForwardException e) {
                if (!e.getCause().getMessage().contains("remote port forwarding failed for listen port")) {
                    throw e;
                }
            }
        }

        throw new ForwardException(String.format("Fail to bind %s:%s after %s tries", model.getBindAddr(), model.getBindPort(), MAX_TRIES));

    }

    private void l2rUnbind0(String bindAddr, Integer bindPort) {
        String bindAddr2 = BindModel.fixBindAddr(bindAddr);

        if (jschSession != null && jschSession.isConnected()) {
            try {
                jschSession.delPortForwardingR(bindAddr2, bindPort);
            } catch (JSchException nothing) {
                // JSCH remove exception here.
                throw new RuntimeException(nothing);
            }
        }

        removeFrom(l2rBindings, binding -> binding.getBindPort().equals(bindPort) && bindAddr2.equals(binding.getBindAddr()));
    }
}
