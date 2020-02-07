package org.dru.dusap.injection;

import org.dru.dusap.time.TimeModule;
import org.junit.Test;

public class InjectionTest {
    @Test
    public void testDefaultBehaviour() {
        Injection.getInjector(TimeModule.class);
    }
}