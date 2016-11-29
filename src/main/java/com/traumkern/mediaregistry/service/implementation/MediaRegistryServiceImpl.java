package com.traumkern.mediaregistry.service.implementation;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.traumkern.mediaregistry.data.entity.MediaFile;
import com.traumkern.mediaregistry.inject.InjectLogger;
import com.traumkern.mediaregistry.service.FileRemovalFailedException;
import com.traumkern.mediaregistry.service.MediaRegistryService;
import com.traumkern.mediaregistry.service.model.Duplicates;
import com.traumkern.mediaregistry.service.model.PathAndSize;

@Component
public class MediaRegistryServiceImpl implements MediaRegistryService {

    @Value("${mediaregistry.threads}")
    private int numberOfThreads;

    @InjectLogger
    private Log logger;

    @Autowired
    private FileSystemAccessService fileSystemService;

    @Autowired
    private MD5RegistryService registryService;

    @Override
    public List<Duplicates> fetchAllDuplicates() {
        return this.registryService.allDuplicates();
    }

    @Override
    public Long countAllRegisteredFiles() {
        return this.registryService.countAllRegisteredPathes();
    }

    @Override
    public void syncRegistryWithFileSystem() throws Exception {
        this.logger.info("Registry update job started");
        final List<PathAndSize> myFileList = this.fileSystemService.scanForMediaFilesInFileSystem();
        this.logger.info("Starting registry update");
        updateMd5RegistryWithNewMediaFiles(myFileList);
        // Finally check if all registered files still exist in the filesystem and unregister them if not:
        this.logger.info("Finished registry update - starting clean up of orphaned database entries");
        unregisterNonExistingFiles(myFileList);
        this.logger.info("Clean up of orphaned database entries finished - exiting registry update job");
    }

    @Override
    public PathAndSize removeFile(final long argId) throws FileRemovalFailedException {
        final MediaFile myMediaFile = this.registryService.fetchMediaFile(argId);
        if (myMediaFile == null) {
            this.logger.error("Could not remove file with database id " + argId + " - no such id in database");
            throw new FileRemovalFailedException("Could not find file with id " + argId + " in media registry", "?");
        }
        this.fileSystemService.removeFile(myMediaFile.getPath());
        this.registryService.unregisterMedia(myMediaFile);
        return new PathAndSize(myMediaFile.getPath(), myMediaFile.getSize());
    }

    private void updateMd5RegistryWithNewMediaFiles(final List<PathAndSize> myFileList) throws Exception {
        // Parallel processing of all files:
        final ForkJoinPool myThreadPool = new ForkJoinPool(this.numberOfThreads);
        myThreadPool.submit(() -> myFileList.parallelStream()
                                            .filter(myFile -> !this.registryService.isRegistered(myFile))
                                            .forEach(myFile -> registerNewMediaFile(myFile)))
                    .get();
    }

    private void registerNewMediaFile(final PathAndSize argPathAndSize) {
        final String myFilePath = argPathAndSize.getPath();
        try {
            this.logger.info("Generating MD5 for file " + myFilePath);
            final String myMD5Sum = this.fileSystemService.generateMD5(myFilePath);
            this.registryService.registerMediaWithCheckSum(argPathAndSize, myMD5Sum);
            this.logger.info("Registered MD5 for file " + myFilePath);
        } catch (final IOException ioe) {
            this.logger.error("IO error while trying to access file " + myFilePath);
            // This error is eaten on purpose: the application shall continue if a single file fails due to an IO error.
        }
    }

    private void unregisterNonExistingFiles(final List<PathAndSize> myFileList) {
        final List<String> myRegisteredPathList = this.registryService.allRegisteredPathes();
        final List<String> myExistingPathList = myFileList.stream()
                                                          .map(myPathAndSize -> myPathAndSize.getPath())
                                                          .collect(Collectors.toList());
        myRegisteredPathList.parallelStream()
                            .filter(myPath -> !myExistingPathList.contains(myPath))
                            .forEach(myPath -> unregisterMedia(myPath));
    }

    private void unregisterMedia(final String argPath) {
        this.logger.info("File: " + argPath + " - present in database but not in filesystem - unregistering it.");
        this.registryService.unregisterMedia(argPath);
    }

}
