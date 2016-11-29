package com.traumkern.mediaregistry.frontend.model;

import org.apache.commons.io.FileUtils;

import com.traumkern.mediaregistry.service.model.PathAndSize;

public class FileRemovalResult {

    private final String path;

    private final long size;

    private final boolean successful;

    private final String failureReason;

    private FileRemovalResult(final String argPath, final long argSize) {
        this.path = argPath;
        this.size = argSize;
        this.successful = true;
        this.failureReason = null;
    }

    private FileRemovalResult(final String argPath, final String argRemovalFailureReason) {
        this.path = argPath;
        this.size = -1;
        this.successful = false;
        this.failureReason = argRemovalFailureReason;
    }

    public String getPath() {
        return this.path;
    }

    public long getSize() {
        return this.size;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public String getFailureReason() {
        return this.failureReason;
    }

    public String getFormattedSize() {
        return FileUtils.byteCountToDisplaySize(this.size);
    }

    public static FileRemovalResult success(final PathAndSize argPathAndSize) {
        return new FileRemovalResult(argPathAndSize.getPath(), argPathAndSize.getSize());
    }

    public static FileRemovalResult failure(final String argPath, final String argFailureReason) {
        return new FileRemovalResult(argPath, argFailureReason);
    }

}
