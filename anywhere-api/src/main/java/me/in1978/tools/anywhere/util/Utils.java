package me.in1978.tools.anywhere.util;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static ScheduledThreadPoolExecutor niceScheduler() {
        ScheduledThreadPoolExecutor ret = new ScheduledThreadPoolExecutor(1);
        ret.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() * 10);
        ret.setKeepAliveTime(10, TimeUnit.MINUTES);
        ret.setRemoveOnCancelPolicy(true);

        return ret;
    }

    public static String digest(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("sha1");
            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, bytes).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T cloneBySer(T obj) {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(obj);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                 ObjectInputStream ois = new ObjectInputStream(bais)
            ) {
                return (T) ois.readObject();
            } catch (ClassNotFoundException never) {
                throw new RuntimeException(never);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
