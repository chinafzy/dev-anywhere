package me.in1978.tools.anywhere.impl.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.in1978.tools.anywhere.AnywhereEngine;
import me.in1978.tools.anywhere.AnywhereSession;
import me.in1978.tools.anywhere.model.EngineModel;
import me.in1978.tools.anywhere.model.SessionModel;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.SocketException;
import me.in1978.tools.anywhere.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
class AnywhereEngineJschImpl implements AnywhereEngine {

    private final EngineModel conf;
    private final ScheduledExecutorService scheduler;

    @Getter
    private final JSch jsch;
    @Getter
    private final Map<String, AnywhereSessionJschImpl> sessions = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    public AnywhereEngineJschImpl(EngineModel conf) {
        this(conf, Utils.niceScheduler());
    }

    public AnywhereEngineJschImpl(EngineModel conf, ScheduledExecutorService scheduler) {
        this.conf = conf;
        this.scheduler = scheduler;

        this.jsch = new JSch();
        Optional.ofNullable(conf.retrieveIdentifyFile())
                .ifPresent(file -> {
                    try {
                        jsch.addIdentity(file);
                    } catch (JSchException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public List<AnywhereSession> sessionList() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public AnywhereSessionJschImpl retrieveSession(SessionModel model) throws SocketException, AuthException {
        String id = model.calId();
        AnywhereSessionJschImpl ret = sessions.get(id);

        return ret != null ? ret : createSession0(model);
    }


    @Override
    public void removeSession(String id) {
        removeSession0(id);
    }

    /**
     * Find me.in1978.tools.anywhere.AnywhereFactory created session.
     *
     * @param id
     * @return
     */
    @Override
    public AnywhereSessionJschImpl getSession(String id) {
        return sessions.get(id);
    }

    /**
     * Create and Register me.in1978.tools.anywhere.AnywhereFactory new session.
     *
     * @param model
     * @return
     * @throws SocketException
     * @throws AuthException
     */
    private AnywhereSessionJschImpl createSession0(SessionModel model) throws SocketException, AuthException {
        String id = model.calId();

        if (sessions.get(id) != null)
            throw new RuntimeException("An old session has already been there:" + model);

        AnywhereSessionJschImpl session = new AnywhereSessionJschImpl(this, model)
                .setId(id);
        session.retrieveJschSession(true);
        sessions.put(id, session);

        long interval = conf.retrieveCheckInterval().toMillis();
        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(session::autoFix, interval, interval, TimeUnit.MILLISECONDS);
        futures.put(id, future);

        return session;
    }

    /**
     * Remove and Destroy session.
     *
     * @param id
     */
    private void removeSession0(String id) {
        log.info("try removing session {}", id);

        ScheduledFuture<?> future = futures.remove(id);
        if (future != null) {
            log.info("Canceling session watcher {}", id);
            future.cancel(true);
        }

        AnywhereSessionJschImpl session = sessions.remove(id);
        if (session != null) {
            Session jschSession = session.getJschSession();
            if (jschSession != null && jschSession.isConnected()) {
                log.info("Closing jschSession {}", id);
                jschSession.disconnect();
            } else {
                log.info("jschSession is not created/connected.");
            }
        }
    }
}
