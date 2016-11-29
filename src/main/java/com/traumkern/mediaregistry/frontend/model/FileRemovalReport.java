package com.traumkern.mediaregistry.frontend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class FileRemovalReport {

    private final List<FileRemovalResult> successList;

    private final List<FileRemovalResult> failureList;

    private long removalTotalFileSize;

    private FileRemovalReport() {
        this.successList = new ArrayList<>();
        this.failureList = new ArrayList<>();
        this.removalTotalFileSize = 0;
    }

    public List<FileRemovalResult> getSuccessList() {
        return Collections.unmodifiableList(this.successList);
    }

    public List<FileRemovalResult> getFailureList() {
        return Collections.unmodifiableList(this.failureList);
    }

    public long getRemovalTotalFileSize() {
        return this.removalTotalFileSize;
    }

    public String getFormattedRemovalTotalFileSize() {
        return FileUtils.byteCountToDisplaySize(this.removalTotalFileSize);
    }

    public int getSuccessCount() {
        return this.successList.size();
    }

    public int getFailureCount() {
        return this.failureList.size();
    }

    public static FileRemovalReport fromResultList(final List<FileRemovalResult> argResultList) {
        final FileRemovalReport myReport = new FileRemovalReport();
        for (final FileRemovalResult myResult : argResultList) {
            if (myResult.isSuccessful()) {
                myReport.successList.add(myResult);
                myReport.removalTotalFileSize += myResult.getSize();
            } else {
                myReport.failureList.add(myResult);
            }
        }
        return myReport;
    }

}
