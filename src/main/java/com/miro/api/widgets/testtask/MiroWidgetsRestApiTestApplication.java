package com.miro.api.widgets.testtask;

import com.miro.api.widgets.testtask.config.AppConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiroWidgetsRestApiTestApplication implements CommandLineRunner {

    private final AppConfig config;

    public MiroWidgetsRestApiTestApplication(AppConfig config) {
        this.config = config;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MiroWidgetsRestApiTestApplication.class);
        app.run();
    }

    public void run(String... args) {
        System.out.println("using environment: " + config.getConfig().getEnvironment());
        System.out.println("name: " + config.getConfig().getName());
    }

}
