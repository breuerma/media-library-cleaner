package com.traumkern.mediaregistry.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.traumkern.mediaregistry.data.entity.MediaFile;

public interface MediaFileRepository extends CrudRepository<MediaFile, Long> {

    MediaFile findOneByPathAndSize(String argPath, long argSize);

    MediaFile findOneByPath(String argPath);

}
