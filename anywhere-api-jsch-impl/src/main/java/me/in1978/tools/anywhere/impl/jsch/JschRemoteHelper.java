package me.in1978.tools.anywhere.impl.jsch;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class JschRemoteHelper {
    final Session session;
    
    public static interface ChannelExecCallback<T> {
        T apply(ChannelExec channel) throws JSchException;
    }


    public <T> T runJschExec(ChannelExecCallback<T> fn) throws JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");

        try {
            return fn.apply(channel);
        } finally {
            channel.disconnect();
        }
    }

    public String runJschCommand(String cmd) throws JSchException {
        return runJschExec(channel -> {
            channel.setCommand(cmd);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            channel.connect();

            try (InputStream is = channel.getInputStream();
                 LineNumberReader reader = new LineNumberReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            ) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
