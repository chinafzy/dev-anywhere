package me.in1978.tools.anywhere.impl.jsch;

import me.in1978.tools.anywhere.model.BindModel;
import me.in1978.tools.anywhere.model.EngineModel;
import me.in1978.tools.anywhere.model.SessionModel;
import me.in1978.tools.anywhere.tr.AuthException;
import me.in1978.tools.anywhere.tr.SocketException;
import org.junit.jupiter.api.Test;

class AnywhereEngineJschImplTest {

    String host = "43.143.168.105";
    String user = "anywhere";
    String pass = "anywhere19781978";
    String identifyValue = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpAIBAAKCAQEAw0wJkjGB1fF8HALG3KBzBE8sONhU4R6Dr9is9gBn0wMk+5Oo\n" +
            "kL0zlcb9ZUZ+7/JAEK1cveMw9fVou+IlATXIaI3eltK6jswcOMGL9QqommrewD7w\n" +
            "p/niBTvkW0ypEM+jJt+yjs+QdEnkkORosy1B18FiG9CnwWYzbGrIkcxJYRdUFU4Z\n" +
            "VSWOckT64clsckKTBzyU8tFeU3W41UbP0n37PsLIuc/Vch1li3gGUFaIyex7uq7y\n" +
            "zHSBxaKQPaU5f/wXHyBetr4dusr9L6ECE4OPzUWE0SXgB9catn1PgCvby29V1rOd\n" +
            "i9uIzX5iuuwVvEZrl+Wo9Dk1tE964JB3KLJU3QIDAQABAoIBAQCgzdkLoOvxBvdT\n" +
            "8Q1g2FQb9jzJSYcKHZ/8iIJRxB+45VvsMbaYBtGUv9YhL5yRYXxSvwwf5hOIUds8\n" +
            "6IXjqy8qt7XRpg2qSnHqYaMlzvLowppK3Qg2p0/tU1VBv6tthiomdgaGBG5C8hzb\n" +
            "GNcEwXz3c+meUX3vhoFNAdfIBNdesu/XitnWZ+6eNe6/hUbBJne66w3iDyZ0Fu0n\n" +
            "dX0GdIkuMoUHRf1s+2cWvfOz7YrFJoUZT1wUaKUB6D2Sj+SWdlmy6PHZi0BKhJKk\n" +
            "d4pZZbNHilEpgOrD+R0rTGwdKJtSaJIweCQJhUyBbe43DD/yQEmc5fK7eE68F1Q0\n" +
            "EWpGf9KZAoGBAOz7hKQX0fjroE5nUkB2uBSUKCseynStFVDlSDdlBUqC+rWCNaj6\n" +
            "RQxeh1pnzs9jp4yuKpe29KwCCsCGCOq0+yHgYnolA0Tyk6FLZ1aCOPO39QWNRqTZ\n" +
            "0w53TcbnVAap0sXqK+yvRPl2rb4Yob+xnLfa08CXIKWWujZCE3LCRdmnAoGBANL4\n" +
            "JexKf0Wusxw0O+q9b2kapXla5XIOJsm657x0VV4gSoDCS5lUpkxHy9FBGKIwK9FE\n" +
            "MPx6iQqSj9C6iRROaQhnukFaBQkWYCcMCWZp1Hgslz86+zEF8DssXPTIc4V+C7Qi\n" +
            "o7KPyN2yyBr+fA8O8OIGkTskH8XiEEVo0zwFJCXbAoGAUyolL0zos/lEpZ8ev16q\n" +
            "VRZ1TkSc+fsvytbb1AzQoYh7oS0T1bwWdTPLPk/FxQQM7ZLFgb6D+MUyrsOa53sh\n" +
            "0o6jIIhSsGSMqIMEvdgvUmBRTWR50SpcJex6T/4of7ojLK90QLh1dTJP4LFWOiaD\n" +
            "gAX8X87pdxMBQD9KAfDpgGsCgYEAlbkUuOh6AMsmRDrx1vuQSOhnTYPr2JEgiASE\n" +
            "mCN7vmxuU4kQvAXua658gMqRH/HN9xtNJLIV9hHjzP/Fb0rikduDvQOVFUjy/mM1\n" +
            "fKBY8Ny8jlxejwEm5+fpJLPqAHBe//2hbYVLycMq7rVy+ADjhZWhlnZhsj9oysnF\n" +
            "OBByAgcCgYBe86suE9yC68RVaUj/R5pmQIbpDpr7bXFSrMQ6r4uYgPKUUSbmezos\n" +
            "0qCjYA3d0Q+I3FewmKw7iCfl748DIVYKHqy8NZj8HGRzL2Lq047lrGb8Qwxx8zO2\n" +
            "hV9UqnP4LJc3UZ4iRIaghZ7hhgQjArzmVtwzpH9xH4ikRHq0fSQSRw==\n" +
            "-----END RSA PRIVATE KEY-----";

    @Test
    public void testIdentify() throws SocketException, AuthException {
        EngineModel model = new EngineModel().setIdentifyValue(identifyValue);
        SessionModel sessionModel = new SessionModel().setHost(host).setUser(user);
        AnywhereSessionJschImpl session = new AnywhereEngineJschImpl(model).retrieveSession(sessionModel);
        System.out.println(session.getJschSession().isConnected());
    }

    @Test
    public void testPassword() throws SocketException, AuthException {
        EngineModel model = new EngineModel();
        SessionModel sessionModel = new SessionModel().setHost(host).setUser(user).setPass(pass);
        AnywhereSessionJschImpl session = new AnywhereEngineJschImpl(model).retrieveSession(sessionModel);
        System.out.println(session.getJschSession().isConnected());
    }

    @Test
    public void testRandomPorts() throws SocketException, AuthException {
        EngineModel model = new EngineModel().setIdentifyValue(identifyValue);
        SessionModel sessionModel = new SessionModel().setHost(host).setUser(user);
        AnywhereSessionJschImpl session = new AnywhereEngineJschImpl(model).retrieveSession(sessionModel);

        for (int i = 0; i < 30000; i++) {
            BindModel bindModel = new BindModel().setOriPort(80);
            session.l2rBind(bindModel);
            System.out.println(bindModel.getBindPort());
        }
    }


}
