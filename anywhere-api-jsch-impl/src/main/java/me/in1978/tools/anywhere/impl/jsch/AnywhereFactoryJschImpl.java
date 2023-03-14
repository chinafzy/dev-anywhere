package me.in1978.tools.anywhere.impl.jsch;

import me.in1978.tools.anywhere.AnywhereEngine;
import me.in1978.tools.anywhere.AnywhereFactory;
import me.in1978.tools.anywhere.model.EngineModel;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AnywhereFactoryJschImpl implements AnywhereFactory {
    public static String ID = "jsch";

    @Override
    public AnywhereEngine createEngine(EngineModel model) {
        return new AnywhereEngineJschImpl(model);
    }

    @Override
    public String id() {
        return ID;
    }

    public static boolean isAuthException(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        return sw.toString().contains("Auth fail");
    }
}
