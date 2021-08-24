package com.qooco.boost.models.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FitUserReq extends UserBaseReq {
    @JsonIgnore
    private Long id;

    public FitUserReq(Long id) {
        this.id = id;
    }
}
