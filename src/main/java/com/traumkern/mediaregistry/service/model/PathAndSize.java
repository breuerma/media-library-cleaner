package com.traumkern.mediaregistry.service.model;

public class PathAndSize {

    private final String path;

    private final long size;

    public PathAndSize(final String argPath, final long argSize) {
        this.path = argPath;
        this.size = argSize;
    }

    public String getPath() {
        return this.path;
    }

    public long getSize() {
        return this.size;
    }

}
