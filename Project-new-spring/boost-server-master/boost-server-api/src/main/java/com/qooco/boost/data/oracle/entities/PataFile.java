package com.qooco.boost.data.oracle.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PATA_FILE")
public class PataFile implements Serializable {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PATA_FILE_SEQUENCE")
    @SequenceGenerator(sequenceName = "PATA_FILE_SEQ", allocationSize = 1, name = "PATA_FILE_SEQUENCE")
    @Column(name = "PATA_FILE_ID", nullable = false)
    private Long pataFileId;


    @Basic(optional = false)
    @Column(name = "USER_PROFILE_ID")
    private Long userProfileId;

    @Basic(optional = false)
    @Column(name = "FILE_NAME", columnDefinition = "NVARCHAR2")
    @Size(min = 1, max = 2000)
    private String fileName;

    @Basic(optional = false)
    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Basic(optional = false)
    @Column(name = "CONTENT_TYPE", columnDefinition = "NVARCHAR2")
    private String contentType;

    @Lob
    @Basic(optional = false)
    @Column(name = "URL")
    private String url;

    @Lob
    @Column(name = "THUMBNAIL_URL")
    private String thumbnailUrl;

    @Column(name = "DURATION")
    private Long duration;

    @Column(name = "PURPOSE")
    private Integer purpose;

    @Basic(optional = false)
    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PataFile pataFile = (PataFile) o;
        return Objects.equals(pataFileId, pataFile.pataFileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pataFileId);
    }

}
