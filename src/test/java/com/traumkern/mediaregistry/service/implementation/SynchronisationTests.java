package com.traumkern.mediaregistry.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.traumkern.mediaregistry.data.entity.CheckSum;
import com.traumkern.mediaregistry.data.entity.MediaFile;
import com.traumkern.mediaregistry.data.repository.CheckSumRepository;
import com.traumkern.mediaregistry.data.repository.MediaFileRepository;
import com.traumkern.mediaregistry.service.MediaRegistryService;
import com.traumkern.mediaregistry.service.model.PathAndSize;
import com.traumkern.mediaregistry.test.builder.CheckSumBuilder;
import com.traumkern.mediaregistry.test.builder.MediaFileBuilder;
import com.traumkern.mediaregistry.test.builder.PathAndSizeBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase
public class SynchronisationTests {

    @MockBean
    private FileSystemAccessService fileSystemService;

    @Autowired
    private CheckSumRepository checkSumRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private MediaRegistryService registryService;

    @Autowired
    private TransactionalRepositoryAccessor repositoryAccessor;

    @Test
    public void testSyncOnEmptyDatabase() throws Exception {
        configureFileSystemMocksWithAllFiles();
        this.registryService.syncRegistryWithFileSystem();
        checkDatabaseWithAllFilesSyncedStatus();
        this.repositoryAccessor.clearRepositories();
    }

    @Test
    public void testSyncOnPrefilledDatabase() throws Exception {
        configureFileSystemMocksWithAllFiles();
        final MediaFile myDefaultMediaFile = new MediaFileBuilder().build();
        final MediaFile myDuplicateMediaFile = new MediaFileBuilder().withPathAndSize(PathAndSizeBuilder.duplicatePathAndSize())
                                                                     .build();
        final CheckSum myDuplicateCheckSum = new CheckSumBuilder().build();
        this.repositoryAccessor.storeCheckSumAndMedia(myDuplicateCheckSum, myDefaultMediaFile, myDuplicateMediaFile);
        this.registryService.syncRegistryWithFileSystem();
        checkDatabaseWithAllFilesSyncedStatus();
        this.repositoryAccessor.clearRepositories();
    }

    @Test
    public void testSyncOnPrefilledDatabaseWithOneFileMissingInFileSystem() throws Exception {
        configureFileSystemMocksWithOneFileMissing();
        final MediaFile myDefaultMediaFile = new MediaFileBuilder().build();
        final MediaFile myDuplicateMediaFile = new MediaFileBuilder().withPathAndSize(PathAndSizeBuilder.duplicatePathAndSize())
                                                                     .build();
        final CheckSum myDuplicateCheckSum = new CheckSumBuilder().build();
        this.repositoryAccessor.storeCheckSumAndMedia(myDuplicateCheckSum, myDefaultMediaFile, myDuplicateMediaFile);
        final MediaFile myAlternateMediaFile = new MediaFileBuilder().withPathAndSize(PathAndSizeBuilder.alternatePathAndSize())
                                                                     .build();
        final CheckSum myAlternateCheckSum = new CheckSumBuilder().withAlternateMd5()
                                                                  .build();
        this.repositoryAccessor.storeCheckSumAndMedia(myAlternateCheckSum, myAlternateMediaFile);
        this.registryService.syncRegistryWithFileSystem();
        checkDatabaseWithOneFileMissingStatus();
        this.repositoryAccessor.clearRepositories();
    }

    private void checkDatabaseWithOneFileMissingStatus() {
        assertThat(this.checkSumRepository.count()).isEqualTo(1);
        assertThat(this.mediaFileRepository.count()).isEqualTo(2);
        final CheckSum myCheckSum = this.repositoryAccessor.fetchAllCheckSums()
                                                           .get(0);
        assertThat(myCheckSum.getFileList()
                             .size()).isEqualTo(2);
        assertThat(myCheckSum.getMd5Sum()).isEqualTo(CheckSumBuilder.DEFAULT_MD5);
    }

    private void checkDatabaseWithAllFilesSyncedStatus() {
        assertThat(this.checkSumRepository.count()).isEqualTo(2);
        assertThat(this.mediaFileRepository.count()).isEqualTo(3);
        for (final CheckSum myCheckSum : this.repositoryAccessor.fetchAllCheckSums()) {
            final List<MediaFile> myMediaFileList = myCheckSum.getFileList();
            assertThat(myMediaFileList.size()).isBetween(1, 2);
            if (myMediaFileList.size() == 1) {
                final MediaFile myMediaFile = myMediaFileList.get(0);
                assertThat(myMediaFile.getPath()).isEqualTo(PathAndSizeBuilder.ALTERNATE_MEDIA_FILE_PATH);
                assertThat(myMediaFile.getSize()).isEqualTo(PathAndSizeBuilder.ALTERNATE_MEDIA_FILE_SIZE);
                assertThat(myCheckSum.getMd5Sum()).isEqualTo(CheckSumBuilder.ALTERNATE_MD5);
            } else {
                assertThat(myCheckSum.getMd5Sum()).isEqualTo(CheckSumBuilder.DEFAULT_MD5);
            }
        }
    }

    private void configureFileSystemMocksWithOneFileMissing() throws Exception, IOException {
        resetMocks();
        // File system contains 2 files:
        final List<PathAndSize> myPathAndSizeList = new ArrayList<>();
        myPathAndSizeList.add(PathAndSizeBuilder.defaultPathAndSize());
        myPathAndSizeList.add(PathAndSizeBuilder.duplicatePathAndSize());
        when(this.fileSystemService.scanForMediaFilesInFileSystem()).thenReturn(myPathAndSizeList);
        // Both files are duplicates:
        mockMd5(PathAndSizeBuilder.defaultPathAndSize(), CheckSumBuilder.DEFAULT_MD5);
        mockMd5(PathAndSizeBuilder.duplicatePathAndSize(), CheckSumBuilder.DEFAULT_MD5);
    }

    private void configureFileSystemMocksWithAllFiles() throws Exception, IOException {
        resetMocks();
        // File system contains 3 files:
        final List<PathAndSize> myPathAndSizeList = new ArrayList<>();
        myPathAndSizeList.add(PathAndSizeBuilder.defaultPathAndSize());
        myPathAndSizeList.add(PathAndSizeBuilder.duplicatePathAndSize());
        myPathAndSizeList.add(PathAndSizeBuilder.alternatePathAndSize());
        when(this.fileSystemService.scanForMediaFilesInFileSystem()).thenReturn(myPathAndSizeList);
        // Two of those files are duplicates:
        mockMd5(PathAndSizeBuilder.defaultPathAndSize(), CheckSumBuilder.DEFAULT_MD5);
        mockMd5(PathAndSizeBuilder.duplicatePathAndSize(), CheckSumBuilder.DEFAULT_MD5);
        mockMd5(PathAndSizeBuilder.alternatePathAndSize(), CheckSumBuilder.ALTERNATE_MD5);
    }

    private void mockMd5(final PathAndSize argPathAndSize, final String argMd5) throws IOException {
        when(this.fileSystemService.generateMD5(argPathAndSize.getPath())).thenReturn(argMd5);
    }

    private void resetMocks() {
        reset(this.fileSystemService);
    }

}
