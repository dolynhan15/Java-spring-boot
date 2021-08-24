package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.PreviousPositionEmbedded;
import com.qooco.boost.data.oracle.entities.UserPreviousPosition;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Setter @Getter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPreviousPositionDTO {
    private Long id;
    private Date startDate;
    private Date endDate;
    private Long salary;
    private String contactPerson;
    private String companyName;
    private String positionName;
    private List<String> photos;
    private CurrencyDTO currency;

    public UserPreviousPositionDTO(PreviousPositionEmbedded previousPositionEmbedded, String locale) {
        if (Objects.nonNull(previousPositionEmbedded)) {
            id = previousPositionEmbedded.getId();
            startDate = previousPositionEmbedded.getStartDate();
            endDate = previousPositionEmbedded.getEndDate();
            salary = previousPositionEmbedded.getSalary();
            contactPerson = previousPositionEmbedded.getContactPerson();
            companyName = previousPositionEmbedded.getCompanyName();
            positionName = previousPositionEmbedded.getPositionName();
            currency = new CurrencyDTO(previousPositionEmbedded.getCurrency(), locale);
            photos = ServletUriUtils.getAbsolutePaths(previousPositionEmbedded.getPhotos());
        }
    }

    public UserPreviousPositionDTO(UserPreviousPosition userPreviousPosition, String locale) {
        if (Objects.nonNull(userPreviousPosition)) {
            id = userPreviousPosition.getId();
            startDate = DateUtils.getUtcForOracle(userPreviousPosition.getStartDate());
            if (Objects.nonNull(userPreviousPosition.getEndDate())) {
                endDate = DateUtils.getUtcForOracle(userPreviousPosition.getEndDate());
            }
            salary = userPreviousPosition.getSalary();
            contactPerson = userPreviousPosition.getContactPerson();
            companyName = userPreviousPosition.getCompanyName();
            positionName = userPreviousPosition.getPositionName();
            currency = new CurrencyDTO(userPreviousPosition.getCurrency(), locale);
            photos = ServletUriUtils.getAbsolutePaths(StringUtil.convertToList(userPreviousPosition.getPhoto()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPreviousPositionDTO that = (UserPreviousPositionDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
