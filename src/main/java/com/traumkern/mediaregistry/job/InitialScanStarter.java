package com.traumkern.mediaregistry.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.traumkern.mediaregistry.service.MediaRegistryService;

@Component
public class InitialScanStarter implements ApplicationRunner {

    private static final String OPTION_START_INITIAL_SCAN = "start-initial-scan";

    @Autowired
    private MediaRegistryService service;

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        if (args.containsOption(OPTION_START_INITIAL_SCAN)) {
            this.service.syncRegistryWithFileSystem();
        }
    }

}
