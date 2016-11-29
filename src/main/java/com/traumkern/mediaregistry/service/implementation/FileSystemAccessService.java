package com.traumkern.mediaregistry.service.implementation;

import java.io.IOException;
import java.util.List;

import com.traumkern.mediaregistry.service.FileRemovalFailedException;
import com.traumkern.mediaregistry.service.model.PathAndSize;

public interface FileSystemAccessService {

    String generateMD5(final String argPath) throws IOException;

    List<PathAndSize> scanForMediaFilesInFileSystem() throws Exception;

    void removeFile(final String argPath) throws FileRemovalFailedException;

}
