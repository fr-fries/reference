package com.fr.fries.reference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SignUrlApplication {

    private static ConfigurableApplicationContext appContext = null;

    public static void main(String[] args) {
        try {
            SpringApplication app = new SpringApplication(SignUrlApplication.class);
            app.addListeners(new ApplicationPidFileWriter());
            appContext = app.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown(int returnCode) {
        SpringApplication.exit(appContext, () -> returnCode);
    }
}
