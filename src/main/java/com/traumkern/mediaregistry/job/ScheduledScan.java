package com.traumkern.mediaregistry.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.traumkern.mediaregistry.service.MediaRegistryService;

@Component
public class ScheduledScan {

    @Autowired
    private MediaRegistryService service;

    @Scheduled(cron = "0 0 8 * * *")
    public void performRegistryUpdate() throws Exception {
        this.service.syncRegistryWithFileSystem();
    }

}
