package com.qooco.boost.models.dto.referral;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.data.oracle.entities.ReferralGift;
import com.qooco.boost.models.dto.assessment.ShortAssessmentDTO;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferralGiftDTO {
    private Long id;

    private Long assessmentId;

    private String name;

    private String description;

    private String image;

    private int coin;

    private int total;

    private int remain;

    private ShortAssessmentDTO assessment;
    private Date activeDate;
    private Date expiredDate;

    private ReferralGiftDTO(ReferralGift referralGift, String locale, String qoocoDomainPath) {
        if (Objects.nonNull(referralGift)) {
            id = referralGift.getId();
            if (Objects.isNull(referralGift.getAssessment())) {
                image = ServletUriUtils.getAbsolutePath(referralGift.getImage());
            } else {
                if (Objects.nonNull(qoocoDomainPath) && Objects.nonNull(referralGift.getImage())) {
                    this.image = qoocoDomainPath.concat(referralGift.getImage());
                } else {
                    this.image = referralGift.getImage();
                }
            }
            coin = referralGift.getCoin();
            total = referralGift.getTotal();
            remain= referralGift.getRemain();
            activeDate = DateUtils.getUtcForOracle(referralGift.getActiveDate());
            expiredDate = DateUtils.getUtcForOracle(referralGift.getExpiredDate());
            assessment = ShortAssessmentDTO.init(referralGift.getAssessment(), locale, qoocoDomainPath);
            if (Objects.nonNull(assessment)) {
                this.assessmentId = assessment.getId();
                this.name = referralGift.getNameEnUs();
                this.description = referralGift.getDescriptionEnUs();
            } else {
                switch (locale) {
                    case QoocoApiConstants.LOCALE_ZH_CN:
                        this.name = referralGift.getNameZhCn();
                        this.description = referralGift.getDescriptionZhCn();
                        break;
                    case QoocoApiConstants.LOCALE_ZH_TW:
                        this.name = referralGift.getNameZhTw();
                        this.description = referralGift.getDescriptionZhTw();
                        break;
                    case QoocoApiConstants.LOCALE_ID_ID:
                        this.name = referralGift.getNameIdId();
                        this.description = referralGift.getDescriptionIdId();
                        break;
                    case QoocoApiConstants.LOCALE_JA_JP:
                        this.name = referralGift.getNameJaJp();
                        this.description = referralGift.getDescriptionJaJp();
                        break;
                    case QoocoApiConstants.LOCALE_MS_MY:
                        this.name = referralGift.getNameMsMy();
                        this.description = referralGift.getDescriptionMsMy();
                        break;
                    case QoocoApiConstants.LOCALE_TH_TH:
                        this.name = referralGift.getNameThTh();
                        this.description = referralGift.getDescriptionThTh();
                        break;
                    case QoocoApiConstants.LOCALE_VI_VN:
                        this.name = referralGift.getNameViVn();
                        this.description = referralGift.getDescriptionViVn();
                        break;
                    case QoocoApiConstants.LOCALE_KO_KR:
                        this.name = referralGift.getNameKoKr();
                        this.description = referralGift.getDescriptionKoKr();
                        break;
                    default:
                        this.name = referralGift.getNameEnUs();
                        this.description = referralGift.getDescriptionEnUs();
                        break;
                }
            }
        }
    }

    public static ReferralGiftDTO init(ReferralGift referralGift, String locale, String qoocoDomainPath) {
        if (Objects.nonNull(referralGift)) {
            return new ReferralGiftDTO(referralGift, locale, qoocoDomainPath);
        }
        return null;
    }

    public static ReferralGiftDTO init(ReferralGift referralGift) {
        return init(referralGift, QoocoApiConstants.LOCALE_EN_US, "");
    }
}