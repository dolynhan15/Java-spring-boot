package com.qooco.boost.models.dto;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/12/2018 - 5:30 PM
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.PataFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PataFileDTO {
    private Long id;
    private String fileName;
    private String downloadUrl;
    private String url;

    public PataFileDTO(Long id, String fileName, String url) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
    }

    public PataFileDTO(PataFile pataFile) {
        if (Objects.nonNull(pataFile)) {
            this.id = pataFile.getPataFileId();
            this.fileName = pataFile.getFileName();
            this.url = pataFile.getUrl();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PataFileDTO that = (PataFileDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
