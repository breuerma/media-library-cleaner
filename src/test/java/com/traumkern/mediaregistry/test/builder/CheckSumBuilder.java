package com.traumkern.mediaregistry.test.builder;

import java.util.ArrayList;

import com.traumkern.mediaregistry.data.entity.CheckSum;
import com.traumkern.mediaregistry.data.entity.MediaFile;

public class CheckSumBuilder {

    public static final String DEFAULT_MD5 = "bff6048c951be8ce3437f7ee00424e3e";

    public static final String ALTERNATE_MD5 = "f3fe37fbb7197859a6021f3724a39cd9";

    private final CheckSum checkSum;

    public CheckSumBuilder() {
        this.checkSum = new CheckSum();
        this.checkSum.setFileList(new ArrayList<MediaFile>());
        withDefaultMd5();
    }

    public CheckSum build() {
        return this.checkSum;
    }

    public CheckSumBuilder withDefaultMd5() {
        this.checkSum.setMd5Sum(DEFAULT_MD5);
        return this;
    }

    public CheckSumBuilder withAlternateMd5() {
        this.checkSum.setMd5Sum(ALTERNATE_MD5);
        return this;
    }

    public CheckSumBuilder withMediaFile(final MediaFile argMediaFile) {
        this.checkSum.getFileList()
                     .add(argMediaFile);
        argMediaFile.setCheckSum(this.checkSum);
        return this;
    }

}
