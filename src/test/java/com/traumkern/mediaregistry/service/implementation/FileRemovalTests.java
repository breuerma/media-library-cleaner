package com.traumkern.mediaregistry.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.traumkern.mediaregistry.data.entity.CheckSum;
import com.traumkern.mediaregistry.data.entity.MediaFile;
import com.traumkern.mediaregistry.data.repository.CheckSumRepository;
import com.traumkern.mediaregistry.data.repository.MediaFileRepository;
import com.traumkern.mediaregistry.service.FileRemovalFailedException;
import com.traumkern.mediaregistry.service.model.PathAndSize;
import com.traumkern.mediaregistry.test.builder.CheckSumBuilder;
import com.traumkern.mediaregistry.test.builder.MediaFileBuilder;
import com.traumkern.mediaregistry.test.builder.PathAndSizeBuilder;
import com.traumkern.mediaregistry.test.mock.FirstArgumentReturnedAnswer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class FileRemovalTests {

    @MockBean
    private FileSystemAccessService fileSystemService;

    @MockBean
    private MediaFileRepository mediaFileRepository;

    @MockBean
    private CheckSumRepository checkSumRepository;

    @Autowired
    private MediaRegistryServiceImpl registryService;

    @Test
    public void testSuccessfulUniqueFileRemoval() throws Exception {
        configureMocksForRemovalTests();
        final MediaFile myDefaultMediaFile = new MediaFileBuilder().build();
        final CheckSum myCheckSum = new CheckSumBuilder().withMediaFile(myDefaultMediaFile)
                                                         .build();
        removeDefaultMediaFile(myDefaultMediaFile);
        verify(this.mediaFileRepository, times(1)).delete(myDefaultMediaFile);
        verify(this.checkSumRepository, times(1)).delete(myCheckSum);
    }

    @Test
    public void testSuccessfulFileWDuplicateRemoval() throws Exception {
        configureMocksForRemovalTests();
        final MediaFile myDuplicateMediaFile = new MediaFileBuilder().withPathAndSize(PathAndSizeBuilder.duplicatePathAndSize())
                                                                     .build();
        final MediaFile myDefaultMediaFile = new MediaFileBuilder().build();
        final CheckSum myCheckSum = new CheckSumBuilder().withMediaFile(myDefaultMediaFile)
                                                         .withMediaFile(myDuplicateMediaFile)
                                                         .build();
        removeDefaultMediaFile(myDefaultMediaFile);
        verify(this.mediaFileRepository, times(1)).delete(myDefaultMediaFile);
        verify(this.mediaFileRepository, never()).delete(myDuplicateMediaFile);
        verify(this.checkSumRepository, never()).delete(myCheckSum);
    }

    private void removeDefaultMediaFile(final MediaFile myDefaultMediaFile) throws FileRemovalFailedException {
        when(this.mediaFileRepository.findOne(0L)).thenReturn(myDefaultMediaFile);
        final PathAndSize myPathAndSize = this.registryService.removeFile(0);
        assertThat(myPathAndSize.getPath()).isEqualTo(PathAndSizeBuilder.DEFAULT_MEDIA_FILE_PATH);
        assertThat(myPathAndSize.getSize()).isEqualTo(PathAndSizeBuilder.DEFAULT_MEDIA_FILE_SIZE);
    }

    private void configureMocksForRemovalTests() throws Exception {
        resetMocks();
        final MediaFile myDefaultMediaFile = new MediaFileBuilder().build();
        new CheckSumBuilder().withMediaFile(myDefaultMediaFile)
                             .build();
        when(this.mediaFileRepository.findOne(0L)).thenReturn(myDefaultMediaFile);
    }

    private void resetMocks() {
        reset(this.fileSystemService);
        reset(this.mediaFileRepository);
        reset(this.checkSumRepository);
        // The repositories shall always return the input when save is called:
        when(this.mediaFileRepository.save(any(MediaFile.class))).then(new FirstArgumentReturnedAnswer());
        when(this.checkSumRepository.save(any(CheckSum.class))).then(new FirstArgumentReturnedAnswer());
    }

}
