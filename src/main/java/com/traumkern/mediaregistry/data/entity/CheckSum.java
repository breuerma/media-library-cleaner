package com.traumkern.mediaregistry.data.entity;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "checksum")
public class CheckSum {

    @Id
    @SequenceGenerator(name = "checksum_id_generator", sequenceName = "checksum_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "checksum_id_generator", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "md5sum", nullable = false, unique = true)
    private String md5Sum;

    @OneToMany(targetEntity = MediaFile.class, fetch = FetchType.LAZY, mappedBy = "checkSum", cascade = { ALL })
    private List<MediaFile> fileList = new ArrayList<MediaFile>();

    public Long getId() {
        return this.id;
    }

    public String getMd5Sum() {
        return this.md5Sum;
    }

    public void setMd5Sum(final String argMd5Sum) {
        this.md5Sum = argMd5Sum;
    }

    public List<MediaFile> getFileList() {
        return this.fileList;
    }

    public void setFileList(final List<MediaFile> argFileList) {
        this.fileList = argFileList;
    }

}
