package me.in1978.tools.anywhere.impl.jsch;

import me.in1978.tools.anywhere.Anywhere;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DriverTest {

    @Test
    public void test1() {
        Assertions.assertNotNull(Anywhere.factory("jsch"));
    }
}
