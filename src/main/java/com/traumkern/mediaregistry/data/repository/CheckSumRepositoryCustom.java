package com.traumkern.mediaregistry.data.repository;

import com.traumkern.mediaregistry.data.entity.CheckSum;

public interface CheckSumRepositoryCustom {

    Iterable<CheckSum> findAllDuplicates();

}
