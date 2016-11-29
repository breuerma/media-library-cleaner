package com.traumkern.mediaregistry.service.implementation;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.traumkern.mediaregistry.inject.InjectLogger;
import com.traumkern.mediaregistry.service.FileRemovalFailedException;
import com.traumkern.mediaregistry.service.model.PathAndSize;

public abstract class AbstractFileSystemAccessService implements FileSystemAccessService {

    @Value("${mediaregistry.max.scanner.depth}")
    private int maxScannerDepth;

    @Value("${mediaregistry.directory}")
    private String mediaDirectory;

    @InjectLogger
    private Log logger;

    @Autowired
    private MediaSuffixPredicate suffixPredicate;

    @Override
    public List<PathAndSize> scanForMediaFilesInFileSystem() throws Exception {
        this.logger.info("Scanning " + this.mediaDirectory + " for media files");
        final Path myPath = FileSystems.getDefault()
                                       .getPath(this.mediaDirectory);
        final List<PathAndSize> myFileList = Files.find(myPath, this.maxScannerDepth, this.suffixPredicate)
                                                  .map(e -> createPathAndSize(e))
                                                  .collect(Collectors.toList());
        this.logger.info("Found " + myFileList.size() + " files in path " + this.mediaDirectory + " - starting registry update");
        return myFileList;
    }

    @Override
    public void removeFile(final String argPath) throws FileRemovalFailedException {
        final File myFile = new File(argPath);
        if (!myFile.delete()) {
            this.logger.error("Could not remove file with path " + argPath);
            throw new FileRemovalFailedException("The files was not removed", argPath);
        }
        this.logger.info("Removed file " + argPath);
    }

    private PathAndSize createPathAndSize(final Path myPath) {
        final String myFilePath = myPath.toAbsolutePath()
                                        .toString();
        final long myFileSize = myPath.toFile()
                                      .length();
        return new PathAndSize(myFilePath, myFileSize);
    }

}
