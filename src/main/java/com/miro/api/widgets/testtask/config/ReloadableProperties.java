package com.miro.api.widgets.testtask.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.StreamSupport;

public abstract class ReloadableProperties {

    @Autowired
    protected StandardEnvironment environment;
    private long lastModTime = 0L;
    private Path configPath = null;
    private PropertySource<?> appConfigPropertySource = null;

    @PostConstruct
    private void stopIfProblemsCreatingContext() {
        MutablePropertySources propertySources = environment.getPropertySources();
        Optional<PropertySource<?>> appConfigPsOp =
                StreamSupport.stream(propertySources.spliterator(), false)
                        .filter(ps -> ps.getName().matches("^.*applicationConfig.*file:.*$"))
                        .findFirst();
        if (appConfigPsOp.isEmpty())  {
            throw new RuntimeException("Unable to find property Source as file");
        }
        appConfigPropertySource = appConfigPsOp.get();

        String filename = appConfigPropertySource.getName();
        filename = filename
                .replace("applicationConfig: [file:", "")
                .replaceAll("]$", "");

        configPath = Paths.get(filename);
    }

    @Scheduled(fixedRate=2000)
    private void reload() throws IOException {
        System.out.println("reloading...");
        long currentModTs = Files.getLastModifiedTime(configPath).toMillis();
        if (currentModTs > lastModTime) {
            lastModTime = currentModTs;
            Properties properties = new Properties();
            InputStream inputStream = Files.newInputStream(configPath);
            properties.load(inputStream);
            environment.getPropertySources()
                    .replace(
                            appConfigPropertySource.getName(),
                            new PropertiesPropertySource(
                                    appConfigPropertySource.getName(),
                                    properties
                            )
                    );
            System.out.println("Reloaded.");
            String test = environment.getProperty("ratelimit.global");
            System.out.println(environment.getProperty("ratelimit"));
            inputStream.close();
            propertiesReloaded();
        }
    }

    protected abstract void propertiesReloaded();
}
