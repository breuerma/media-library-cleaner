package com.traumkern.mediaregistry.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.traumkern.mediaregistry.data.entity.CheckSum;

public interface CheckSumRepository extends CrudRepository<CheckSum, Long>, CheckSumRepositoryCustom {

    CheckSum findOneByMd5Sum(String argMD5Sum);

}
