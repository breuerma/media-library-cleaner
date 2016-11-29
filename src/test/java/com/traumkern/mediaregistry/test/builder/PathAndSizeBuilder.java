package com.traumkern.mediaregistry.test.builder;

import com.traumkern.mediaregistry.service.model.PathAndSize;

public class PathAndSizeBuilder {

    public static final String DEFAULT_MEDIA_FILE_PATH = "/home/media/a.mkv";

    public static final long DEFAULT_MEDIA_FILE_SIZE = 1234567890L;

    public static final String DUPLICATE_MEDIA_FILE_PATH = "/home/media/b.mp4";

    public static final long DUPLICATE_MEDIA_FILE_SIZE = 9876543210L;

    public static final String ALTERNATE_MEDIA_FILE_PATH = "/home/media/c.vob";

    public static final long ALTERNATE_MEDIA_FILE_SIZE = 5432109876L;

    public static PathAndSize defaultPathAndSize() {
        return new PathAndSize(DEFAULT_MEDIA_FILE_PATH, DEFAULT_MEDIA_FILE_SIZE);
    }

    public static PathAndSize duplicatePathAndSize() {
        return new PathAndSize(DUPLICATE_MEDIA_FILE_PATH, DUPLICATE_MEDIA_FILE_SIZE);
    }

    public static PathAndSize alternatePathAndSize() {
        return new PathAndSize(ALTERNATE_MEDIA_FILE_PATH, ALTERNATE_MEDIA_FILE_SIZE);
    }

}
