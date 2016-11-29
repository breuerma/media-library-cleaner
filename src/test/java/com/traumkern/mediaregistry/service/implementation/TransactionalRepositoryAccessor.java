package com.traumkern.mediaregistry.service.implementation;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.traumkern.mediaregistry.data.entity.CheckSum;
import com.traumkern.mediaregistry.data.entity.MediaFile;
import com.traumkern.mediaregistry.data.repository.CheckSumRepository;
import com.traumkern.mediaregistry.data.repository.MediaFileRepository;

@Component
public class TransactionalRepositoryAccessor {

    @Autowired
    private CheckSumRepository checkSumRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Transactional
    public void storeCheckSumAndMedia(final CheckSum argCheckSum, final MediaFile... argMediaFileArray) {
        final CheckSum mySavedCheckSum = this.checkSumRepository.save(argCheckSum);
        for (final MediaFile myMediaFile : argMediaFileArray) {
            mySavedCheckSum.getFileList()
                           .add(myMediaFile);
            myMediaFile.setCheckSum(mySavedCheckSum);
            this.mediaFileRepository.save(myMediaFile);
        }
    }

    @Transactional
    public List<CheckSum> fetchAllCheckSums() {
        // Force lazy loading - we're in a test so there are not many datasets:
        final List<CheckSum> myCheckSumList = new ArrayList<>();
        for (final CheckSum myCheckSum : this.checkSumRepository.findAll()) {
            myCheckSum.getFileList()
                      .size();
            myCheckSumList.add(myCheckSum);
        }
        return myCheckSumList;
    }

    @Transactional
    public void clearRepositories() {
        this.checkSumRepository.deleteAll();
        this.mediaFileRepository.deleteAll();
    }

}
