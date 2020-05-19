package com.fr.fries.reference.config;

import com.fr.fries.reference.SignUrlApplication;
import com.fr.fries.reference.handler.LogInterceptor;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.fr.fries.reference.common.Constants.RT_OK;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer, TomcatConnectorCustomizer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final LogInterceptor logInterceptor;
    private Connector connector;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.debug("[WebConfig] Start logInterceptor configuration");
        registry.addInterceptor(logInterceptor).excludePathPatterns("/index.html");
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        log.debug("[WebConfig] Start tomcatCustomConnector configuration");
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addConnectorCustomizers(this);
        return tomcat;
    }

    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    public void shutdown() {
        final Thread thread = new Thread(new Runnable() {
            Connector connector;

            Runnable init(Connector connector) {
                this.connector = connector;
                return this;
            }

            public void run() {
                try {
                    log.info("Application shutdown process started");
                    connector.pause();

                    final Executor executor = connector.getProtocolHandler().getExecutor();
                    final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;

                    log.info("Current active threads: {}", threadPoolExecutor.getActiveCount());
                    threadPoolExecutor.shutdown();
                    if (!threadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                        log.warn("Tomcat thread pool did not shut down gracefully within 30 seconds. " +
                                "Proceeding with forceful shutdown");
                    } else {
                        log.info("Tomcat thread pool is empty, we stop now");
                    }
                    log.info("Application has been shutdown");
                    SignUrlApplication.shutdown(RT_OK);
                } catch (InterruptedException | NullPointerException e) {
                    log.error("Application shutdown process interrupted. {}");
                    Thread.currentThread().interrupt();
                }
            }
        }.init(connector));
        thread.start();
    }
}
