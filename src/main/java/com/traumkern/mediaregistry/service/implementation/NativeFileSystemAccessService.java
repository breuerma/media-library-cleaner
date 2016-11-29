package com.traumkern.mediaregistry.service.implementation;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.traumkern.mediaregistry.inject.InjectLogger;

@Component
@Profile("native")
public class NativeFileSystemAccessService extends AbstractFileSystemAccessService {

    @Value("${mediaregistry.md5sum.command}")
    private String md5SumCommand;

    @Value("${mediaregistry.md5sum.parameters")
    private String md5sumParameters;

    @InjectLogger
    private Log logger;

    @Override
    public String generateMD5(final String argPath) throws IOException {
        final ProcessBuilder myProcessBuilder = new ProcessBuilder(this.md5SumCommand, this.md5sumParameters, argPath);
        final Process myProcess = myProcessBuilder.start();
        final InputStream myOutputStream = myProcess.getInputStream();
        final String myOutput = IOUtils.toString(myOutputStream, "UTF-8");
        final String[] myMD5SumArray = myOutput.split(" ");
        if (myMD5SumArray.length < 2) {
            throw new RuntimeException("Could not determine MD5 sum for file " + argPath);
        }
        return myMD5SumArray[0];
    }

}
