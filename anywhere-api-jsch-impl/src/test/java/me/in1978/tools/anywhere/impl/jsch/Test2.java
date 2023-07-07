package me.in1978.tools.anywhere.impl.jsch;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;


public class Test2 {
    static Supplier<Session> sessionSupplier = () -> {
        JSch jsch = new JSch();
        try {
            jsch.addIdentity("/Users/zeyufang/.ssh/id_rsa");
            Session session = jsch.getSession("zeyufang", "localhost", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            return session;
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    };

    public static void main(String[] args) throws JSchException {

        String s = runJschCommand("ls");
        System.out.println(s);
    }

    static <T> T runJschSession(JschSessionCallback<T> fn) throws JSchException {
        Session session = sessionSupplier.get();

        try {
            return fn.apply(session);
        } finally {
            session.disconnect();
        }
    }

    static <T> T runJschExec(JschChannelExecCallback<T> fn) throws JSchException {
        return runJschSession(session -> {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");

            try {
                return fn.apply(channel);
            } finally {
                channel.disconnect();
            }
        });

    }

    static String runJschCommand(String cmd) throws JSchException {
        return runJschExec(channel -> {
            channel.setCommand(cmd);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            channel.connect();

            try (InputStream is = channel.getInputStream();
                 LineNumberReader lrn = new LineNumberReader(new InputStreamReader(is, StandardCharsets.UTF_8))
            ) {
                String s;
                StringBuilder sb = new StringBuilder();
                while ((s = lrn.readLine()) != null) {
                    sb.append(s).append("\n");
                }
                return sb.substring(0, sb.length() - 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static interface JschSessionCallback<T> {
        T apply(Session session) throws JSchException;
    }

    static interface JschChannelExecCallback<T> {
        T apply(ChannelExec channel) throws JSchException;
    }
}
