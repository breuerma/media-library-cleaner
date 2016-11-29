package com.traumkern.mediaregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackageClasses = Application.class)
@EnableScheduling
public class Application {

    public static void main(final String[] argumentArray) throws Exception {
        SpringApplication.run(Application.class, argumentArray);
    }

}
