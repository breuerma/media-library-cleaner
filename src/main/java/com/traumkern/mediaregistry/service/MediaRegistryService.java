package com.traumkern.mediaregistry.service;

import java.util.List;

import com.traumkern.mediaregistry.service.model.Duplicates;
import com.traumkern.mediaregistry.service.model.PathAndSize;

public interface MediaRegistryService {

    void syncRegistryWithFileSystem() throws Exception;

    List<Duplicates> fetchAllDuplicates();

    Long countAllRegisteredFiles();

    PathAndSize removeFile(long argIdToBeRemoved) throws FileRemovalFailedException;

}
