package com.traumkern.mediaregistry.service.implementation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.traumkern.mediaregistry.inject.InjectFileSystem;
import com.traumkern.mediaregistry.inject.InjectLogger;
import com.traumkern.mediaregistry.service.FileRemovalFailedException;
import com.traumkern.mediaregistry.service.model.PathAndSize;

@Component
public class FileSystemAccessService {

    @Value("${mediaregistry.max.scanner.depth}")
    private int maxScannerDepth;

    @Value("${mediaregistry.directory}")
    private String mediaDirectory;

    @Value("${mediaregistry.rm.command}")
    private String rmCommand;

    @Value("${mediaregistry.md5sum.command}")
    private String md5SumCommand;

    @Value("${mediaregistry.md5sum.parameters")
    private String md5sumParameters;

    @InjectLogger
    private Log logger;

    @InjectFileSystem
    private FileSystem fileSystem;

    @Autowired
    private FileFinder finder;

    @Autowired
    private MediaSuffixPredicate suffixPredicate;

    String generateMD5(final String argPath) throws IOException {
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

    List<PathAndSize> scanForMediaFilesInFileSystem() throws Exception {
        this.logger.info("Scanning " + this.mediaDirectory + " for media files");
        final Path myPath = this.fileSystem.getPath(this.mediaDirectory);
        final List<PathAndSize> myFileList = this.finder.find(myPath, this.maxScannerDepth, this.suffixPredicate)
                                                        .map(e -> createPathAndSize(e))
                                                        .collect(Collectors.toList());
        this.logger.info(
                "Found " + myFileList.size() + " files in path " + this.mediaDirectory + " - starting registry update");
        return myFileList;
    }

    void removeFile(final String argPath) throws FileRemovalFailedException {
        final ProcessBuilder myProcessBuilder = new ProcessBuilder(this.rmCommand, argPath);
        try {
            final Process myProcess = myProcessBuilder.start();
            final int myExitStatus = myProcess.waitFor();
            if (myExitStatus != 0) {
                this.logger.error("Could not remove file with path " + argPath + " - rm command exited with status "
                        + myExitStatus);
                throw new FileRemovalFailedException("Remove command exited with status " + myExitStatus, argPath);
            }
            this.logger.info("Removed file " + argPath);
        } catch (IOException | InterruptedException e) {
            this.logger.error("Could not remove file with path " + argPath + " - exception during rm command execution",
                    e);
            throw new FileRemovalFailedException("Failed to remove file " + argPath, argPath, e);
        }
    }

    private PathAndSize createPathAndSize(final Path myPath) {
        final String myFilePath = myPath.toAbsolutePath()
                                        .toString();
        final long myFileSize = myPath.toFile()
                                      .length();
        return new PathAndSize(myFilePath, myFileSize);
    }

}
