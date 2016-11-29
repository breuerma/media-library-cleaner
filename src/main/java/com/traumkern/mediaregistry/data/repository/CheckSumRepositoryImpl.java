package com.traumkern.mediaregistry.data.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.traumkern.mediaregistry.data.entity.CheckSum;
import com.traumkern.mediaregistry.data.entity.CheckSumId;

@Component
public class CheckSumRepositoryImpl implements CheckSumRepositoryCustom {

    @Autowired
    private CheckSumRepository originalRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Iterable<CheckSum> findAllDuplicates() {
        final List<CheckSumId> myCheckSumIdList = this.entityManager.createNamedQuery("fetchDuplicateChecksumIds",
                CheckSumId.class)
                                                                    .getResultList();
        final List<Long> myIdList = myCheckSumIdList.stream()
                                                    .map(e -> e.getId())
                                                    .collect(Collectors.toList());
        return this.originalRepository.findAll(myIdList);
    }

}
