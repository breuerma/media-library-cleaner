package com.traumkern.mediaregistry.service.implementation;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.traumkern.mediaregistry.data.entity.CheckSum;
import com.traumkern.mediaregistry.data.entity.MediaFile;
import com.traumkern.mediaregistry.data.repository.CheckSumRepository;
import com.traumkern.mediaregistry.data.repository.MediaFileRepository;
import com.traumkern.mediaregistry.inject.InjectLogger;
import com.traumkern.mediaregistry.service.model.Duplicates;
import com.traumkern.mediaregistry.service.model.PathAndSize;

@Component
public class MD5RegistryService {

    @InjectLogger
    private Log logger;

    @Autowired
    private CheckSumRepository checkSumRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Transactional
    boolean isRegistered(final PathAndSize argPathAndSize) {
        final String myPath = argPathAndSize.getPath();
        final long mySize = argPathAndSize.getSize();
        final boolean myIsRegistered = this.mediaFileRepository.findOneByPathAndSize(myPath, mySize) != null;
        if (myIsRegistered) {
            this.logger.info("Skipping file " + myPath + " - it is already registred.");
        }
        return myIsRegistered;
    }

    @Transactional(REQUIRES_NEW)
    public CheckSum registerMediaWithCheckSum(final PathAndSize argPathAndSize, final String argMD5Sum) {
        CheckSum myCheckSum = this.checkSumRepository.findOneByMd5Sum(argMD5Sum);
        if (myCheckSum == null) {
            myCheckSum = createCheckSum(argMD5Sum);
        } else {
            this.logger.info("Found duplicate - file: " + argPathAndSize.getPath() + " - md5sum: " + argMD5Sum);
        }
        MediaFile myMediaFile = this.mediaFileRepository.findOneByPath(argPathAndSize.getPath());
        if (myMediaFile == null) {
            myMediaFile = createMediaFile(argPathAndSize, myCheckSum);
        } else {
            myMediaFile.setSize(argPathAndSize.getSize());
            myMediaFile.setCheckSum(myCheckSum);
        }
        final List<MediaFile> myFilesForThisCheckSum = myCheckSum.getFileList();
        if (!myFilesForThisCheckSum.contains(myMediaFile)) {
            myFilesForThisCheckSum.add(myMediaFile);
        }
        return this.checkSumRepository.save(myCheckSum);
    }

    @Transactional(REQUIRES_NEW)
    public void unregisterMedia(final String argPath) {
        final MediaFile myMedia = this.mediaFileRepository.findOneByPath(argPath);
        if (myMedia == null) {
            return;
        }
        unregisterMedia(myMedia);
    }

    @Transactional
    public void unregisterMedia(final MediaFile argMediaFile) {
        final CheckSum myCheckSum = argMediaFile.getCheckSum();
        // Unfortunately JPA does not cascade correctly (at least not in the same way as a RDBMS does):
        myCheckSum.getFileList()
                  .remove(argMediaFile);
        this.mediaFileRepository.delete(argMediaFile);
        this.logger.info("Unregistered file: " + argMediaFile.getPath());
        if (myCheckSum.getFileList()
                      .isEmpty()) {
            this.checkSumRepository.delete(myCheckSum);
        }
        this.logger.info("Unregistered checksum: " + myCheckSum.getMd5Sum());
    }

    @Transactional
    public MediaFile fetchMediaFile(final Long argId) {
        return this.mediaFileRepository.findOne(argId);
    }

    public Long countAllRegisteredPathes() {
        return this.mediaFileRepository.count();
    }

    @Transactional
    public List<String> allRegisteredPathes() {
        final List<String> myRegisteredPathList = new ArrayList<>();
        for (final MediaFile myMediaFile : this.mediaFileRepository.findAll()) {
            myRegisteredPathList.add(myMediaFile.getPath());
        }
        return myRegisteredPathList;
    }

    @Transactional
    public List<Duplicates> allDuplicates() {
        final List<Duplicates> myDuplicateList = new ArrayList<>();
        for (final CheckSum myCheckSum : this.checkSumRepository.findAllDuplicates()) {
            final List<MediaFile> myFileList = myCheckSum.getFileList();
            // There must be at least two elements on the list - it's a list of duplicates...
            final long mySize = myFileList.get(0)
                                          .getSize();
            myDuplicateList.add(new Duplicates(myFileList, mySize, myCheckSum.getMd5Sum()));
        }
        return myDuplicateList;
    }

    private MediaFile createMediaFile(final PathAndSize argPathAndSize, final CheckSum argCheckSum) {
        final MediaFile myMediaFile = new MediaFile();
        myMediaFile.setPath(argPathAndSize.getPath());
        myMediaFile.setSize(argPathAndSize.getSize());
        myMediaFile.setCheckSum(argCheckSum);
        return this.mediaFileRepository.save(myMediaFile);
    }

    private CheckSum createCheckSum(final String argMD5Sum) {
        final CheckSum myCheckSum = new CheckSum();
        myCheckSum.setMd5Sum(argMD5Sum);
        myCheckSum.setFileList(new ArrayList<MediaFile>());
        return this.checkSumRepository.save(myCheckSum);
    }

}
