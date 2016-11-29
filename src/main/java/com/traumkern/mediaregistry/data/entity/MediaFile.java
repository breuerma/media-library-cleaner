package com.traumkern.mediaregistry.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "moviefile")
public class MediaFile {

    @Id
    @SequenceGenerator(name = "moviefile_id_generator", sequenceName = "moviefile_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "moviefile_id_generator", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String path;

    @Column(nullable = false)
    private long size;

    @ManyToOne(targetEntity = CheckSum.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checksum", nullable = false, updatable = false)
    private CheckSum checkSum;

    public Long getId() {
        return this.id;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String argPath) {
        this.path = argPath;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(final long argSize) {
        this.size = argSize;
    }

    public CheckSum getCheckSum() {
        return this.checkSum;
    }

    public void setCheckSum(final CheckSum argCheckSum) {
        this.checkSum = argCheckSum;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.path == null) ? 0 : this.path.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MediaFile other = (MediaFile) obj;
        if (this.path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!this.path.equals(other.path)) {
            return false;
        }
        return true;
    }

}
