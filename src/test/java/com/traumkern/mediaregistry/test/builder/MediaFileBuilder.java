package com.traumkern.mediaregistry.test.builder;

import com.traumkern.mediaregistry.data.entity.MediaFile;
import com.traumkern.mediaregistry.service.model.PathAndSize;

public class MediaFileBuilder {

    private final MediaFile mediaFile;

    public MediaFileBuilder() {
        this.mediaFile = new MediaFile();
        withPathAndSize(PathAndSizeBuilder.defaultPathAndSize());
    }

    public MediaFile build() {
        return this.mediaFile;
    }

    public MediaFileBuilder withPathAndSize(final PathAndSize argPathAndSize) {
        this.mediaFile.setPath(argPathAndSize.getPath());
        this.mediaFile.setSize(argPathAndSize.getSize());
        return this;
    }

}
