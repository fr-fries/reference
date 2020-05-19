package com.fr.fries.reference.config;

import com.fr.fries.reference.BaseTest;
import com.fr.fries.reference.handler.LogInterceptor;
import org.apache.catalina.connector.Connector;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebConfigTest extends BaseTest {

    @Test
    public void whenShutdown_thenSuccess() {
        WebConfig webConfig = new WebConfig(new LogInterceptor());
        webConfig.customize(new Connector());

        try {
            webConfig.shutdown();
        } catch (Exception e) {
            assertThat(e.getCause(), sameInstance(NullPointerException.class));
        }
    }
}
