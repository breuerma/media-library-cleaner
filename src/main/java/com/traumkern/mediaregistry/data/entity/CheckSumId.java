package com.traumkern.mediaregistry.data.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

@Entity
@NamedNativeQueries({
        @NamedNativeQuery(name = "fetchDuplicateChecksumIds", query = "select checksum as id from moviefile group by checksum having count(checksum)>1", resultClass = CheckSumId.class) })
public class CheckSumId {

    @Id
    private Long id;

    public Long getId() {
        return this.id;
    }

}
