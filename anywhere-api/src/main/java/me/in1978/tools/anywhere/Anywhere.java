package me.in1978.tools.anywhere;

import java.util.Iterator;
import java.util.ServiceLoader;

public class Anywhere {

    public static AnywhereFactory factory() {
        Iterator<AnywhereFactory> itr = ServiceLoader.load(AnywhereFactory.class).iterator();
        if (itr.hasNext()) return itr.next();

        throw new IllegalStateException("No driver found for:" + AnywhereFactory.class.getName());
    }

    public static AnywhereFactory factory(String id) {
        for (AnywhereFactory factory : ServiceLoader.load(AnywhereFactory.class)) {
            if (id.equals(factory.id())) {
                return factory;
            }
        }

        throw new IllegalStateException("No driver found for:" + AnywhereFactory.class.getName());
    }
}
