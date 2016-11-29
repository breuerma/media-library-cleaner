package com.traumkern.mediaregistry.service;

public class FileRemovalFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String filePath;

    public FileRemovalFailedException(final String argMessage, final String argFilePath, final Throwable argCause) {
        super(argMessage, argCause);
        this.filePath = argFilePath;
    }

    public FileRemovalFailedException(final String argMessage, final String argFilePath) {
        super(argMessage);
        this.filePath = argFilePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

}
