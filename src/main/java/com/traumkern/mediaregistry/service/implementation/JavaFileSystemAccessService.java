package com.traumkern.mediaregistry.service.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.traumkern.mediaregistry.inject.InjectLogger;

@Component
@Profile("!native")
public class JavaFileSystemAccessService extends AbstractFileSystemAccessService {

    @Value("${mediaregistry.md5sum.command}")
    private String md5SumCommand;

    @Value("${mediaregistry.md5sum.parameters")
    private String md5sumParameters;

    @InjectLogger
    private Log logger;

    @Override
    public String generateMD5(final String argPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(argPath))) {
            final String myMD5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            return myMD5;
        }
    }

}
