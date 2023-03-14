package me.in1978.tools.anywhere;

import me.in1978.tools.anywhere.model.SessionModel;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.SocketException;

import java.util.List;

public interface AnywhereEngine {

    /**
     * List current sessions.
     *
     * @return
     */
    List<AnywhereSession> sessionList();


    /**
     * Get or Create a session.
     *
     * @param model
     * @return
     * @throws SocketException
     * @throws AuthException
     */
    AnywhereSession retrieveSession(SessionModel model) throws SocketException, AuthException;

    /**
     * Get or Create a session.
     *
     * @param host
     * @param port
     * @param user
     * @param pass
     * @return
     * @throws SocketException
     * @throws AuthException
     */
    default AnywhereSession retrieveSession(String host, Integer port, String user, String pass) throws SocketException, AuthException {
        return retrieveSession(SessionModel.ins(host, port, user, pass));
    }

    /**
     * Get or Create a session.
     *
     * @param host
     * @param user
     * @return
     * @throws SocketException
     * @throws AuthException
     */
    default AnywhereSession retrieveSession(String host, String user) throws SocketException, AuthException {
        return retrieveSession(host, 22, user, null);
    }


    /**
     * Remove and Destroy a session by id.
     *
     * @param id
     */
    void removeSession(String id);

    /**
     * Get a session.
     *
     * @param id
     * @return
     */
    AnywhereSession getSession(String id);
}
