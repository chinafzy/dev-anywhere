package me.in1978.tools.anywhere;

import me.in1978.tools.anywhere.model.BindModel;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.SocketException;

import java.util.List;

public interface AnywhereSession {

    void l2rBind(BindModel bindModel) throws SocketException, AuthException;

    void l2rUnbind(String addr, Integer port);

    default void l2rUnbind(Integer port) {
        l2rUnbind("localhost", port);
    }

    List<BindModel> l2rBindings();

    void r2lBind(BindModel bindModel) throws SocketException, AuthException;

    void r2lUnbind(String addr, Integer port);

    default void r2lUnbind(Integer port) {
        r2lUnbind("localhost", port);
    }

    List<BindModel> r2lBindings();

}
