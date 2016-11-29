package com.traumkern.mediaregistry.service.model;

import java.util.Collections;
import java.util.List;

import com.traumkern.mediaregistry.data.entity.MediaFile;

public class Duplicates {

    private final List<MediaFile> duplicateFileList;

    private final long size;

    private final String md5Sum;

    public Duplicates(final List<MediaFile> argDuplicateFileList, final long argSize, final String argMd5Sum) {
        this.duplicateFileList = Collections.unmodifiableList(argDuplicateFileList);
        this.size = argSize;
        this.md5Sum = argMd5Sum;
    }

    public List<MediaFile> getDuplicateFileList() {
        return this.duplicateFileList;
    }

    public long getSize() {
        return this.size;
    }

    public String getMd5Sum() {
        return this.md5Sum;
    }

}
