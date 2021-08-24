package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.qooco.boost.data.mongo.embedded.PublicKeyEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.utils.CipherKeys;
import com.qooco.boost.models.dto.LatestTimeDTO;
import com.qooco.boost.models.dto.company.CompanyDTO;
import com.qooco.boost.models.dto.user.UserProfileCvEmbeddedDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.collections.MapUtils;
import org.bson.types.ObjectId;
import org.codehaus.plexus.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldNameConstants
public class ConversationDTO extends LatestTimeDTO {
    @Setter
    @Getter
    private String id;
    @Setter
    @Getter
    private String messageCenterId;
    @Setter
    @Getter
    private int messageCenterType;

    @Getter
    private Long companyId;
    @Getter
    private CompanyDTO company;

    @Getter
    private UserProfileCvEmbeddedDTO customer;
    @Setter
    @Getter
    private List<UserProfileCvEmbeddedDTO> participants;
    @Setter
    @Getter
    private String encryptedKey;

    @Setter
    @Getter
    private long totalUnreadMessages;
    private boolean isLocked;

    @Getter
    private int contactPersonStatus;

    @Getter
    private Date createdDate;

    public ConversationDTO(ConversationDoc doc, Long totalUnreadMessages, String token) {
        this(doc, token, null);
        this.totalUnreadMessages = ofNullable(totalUnreadMessages).orElse(0L);
    }

    public ConversationDTO(ConversationBase doc, String token, String locale) {
        this.id = doc.getId().toHexString();
        this.messageCenterId = ofNullable(doc.getMessageCenterId()).map(ObjectId::toHexString).orElse(null);
        this.messageCenterType = doc.getMessageCenterType();
        this.companyId = doc.getCompanyId();
        this.setLastUpdateTime(doc.getUpdatedDate());
        this.createdDate = doc.getCreatedDate();
        setParticipants(convertToUserProfileCvEmbeddedDTOs(doc.getParticipants(), locale));
        this.encryptedKey = getEncryptedKey(doc.getUserKeys(), doc.getSecretKey(), token);
        this.isLocked = doc.isDisable();

        if(doc instanceof ConversationDoc){
            this.contactPersonStatus = ((ConversationDoc)doc).getContactPersonStatus();
        } else if(doc instanceof SupportConversationDoc){
            ofNullable(((SupportConversationDoc)doc).getCustomer()).ifPresent(it -> this.customer = new UserProfileCvEmbeddedDTO(it, locale));
            ofNullable(((SupportConversationDoc)doc).getCompany()).ifPresent(it -> this.company = new CompanyDTO(it));
        }
    }

    private List<UserProfileCvEmbeddedDTO> convertToUserProfileCvEmbeddedDTOs(List<UserProfileCvMessageEmbedded> embeddeds, String locale) {
        if (isNotEmpty(embeddeds)) {
            return embeddeds.stream().map(it -> new UserProfileCvEmbeddedDTO(it, locale)).collect(Collectors.toList());
        }
        return ImmutableList.of();
    }

    public UserProfileCvEmbeddedDTO getPartner(Long userProfileId) {
        return ofNullable(userProfileId)
                .filter(it -> participants != null)
                .flatMap(id -> participants.stream().filter(partner -> !id.equals(partner.getId())).findFirst())
                .orElse(null);
    }

    private String getEncryptedKey(Map<String, PublicKeyEmbedded> userKeys, String secretKey, String token) {
        if (MapUtils.isNotEmpty(userKeys) && Objects.nonNull(userKeys.get(token))) {
            String encryptedPublicKey = userKeys.get(token).getEncryptedPublicKey();
            if (StringUtils.isNotBlank(encryptedPublicKey)) {
                return userKeys.get(token).getEncryptedPublicKey();
            } else if (StringUtils.isNotBlank(userKeys.get(token).getPublicKey())) {
                byte[] publicKeyInBytes = Base64.getDecoder().decode(userKeys.get(token).getPublicKey());
                try {
                    PublicKey userPublicKey = CipherKeys.getPublicKey(publicKeyInBytes);
                    return CipherKeys.encryptByRSA(secretKey, userPublicKey);
                } catch (NoSuchAlgorithmException | IllegalBlockSizeException | UnsupportedEncodingException | BadPaddingException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @JsonProperty("isLocked")
    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
