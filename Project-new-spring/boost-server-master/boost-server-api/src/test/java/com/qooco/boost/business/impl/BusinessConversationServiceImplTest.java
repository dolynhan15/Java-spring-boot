package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessConversationService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.model.ObjectLatest;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.utils.DateUtils;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class BusinessConversationServiceImplTest extends BaseUserService {
    @InjectMocks
    private BusinessConversationService businessConversationService = new BusinessConversationServiceImpl();
    @Autowired
    private ConversationDocService conversationDocService = Mockito.mock(ConversationDocService.class);
    @Autowired
    private MessageDocService messageDocService = Mockito.mock(MessageDocService.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getById_whenIdIsValid_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        String id = "5b8f52fef11a2e";
        mockitoGetById();
        businessConversationService.getById(initAuthentication(), id);
    }

    @Test
    public void getById_whenHaveNotConversation_thenReturnSuccess() {
        String id = "5b8f52fef11a2e77b96e93a1";
        Authentication authentication = initAuthentication();
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        mockitoGetById();

        Mockito.when(conversationDocService.findByIdAndUserProfileId(new ObjectId(id), user.getId())).thenReturn(null);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessConversationService.getById(authentication, id).getCode());
    }

    @Test
    public void getById_whenInputIsValid_thenReturnSuccess() {
        String id = "5b8f52fef11a2e77b96e93a1";
        mockitoGetById();
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessConversationService.getById(initAuthentication(), id).getCode());
    }

    /*==================================== Prepare before unit test =========================================== */

    private void mockitoGetByMessageCenterId() {
        String messageCenterId = "5b8f52fef11a2e77b96e93a0";
        Long userProfileId = 1L;
        int receiveInApp = 1;
        ObjectId conversationDocId = new ObjectId("5b8f52fef11a2e77b96e93a1");
        List<ConversationDoc> conversationDocs = Lists.newArrayList(new ConversationDoc(conversationDocId, new ObjectId(messageCenterId)));
        List<ObjectId> conversationIds = Lists.newArrayList(conversationDocId);
        List<ObjectIdCount> listCount = Lists.newArrayList(new ObjectIdCount(conversationDocId, 1));
        List<ObjectLatest> messageLatestUpdated = Lists.newArrayList(new ObjectLatest(conversationDocId, DateUtils.addDays(new Date(), -10)));

        Mockito.when(conversationDocService.findByMessageCenterIdAndUserProfileId(new ObjectId(messageCenterId), userProfileId))
                .thenReturn(conversationDocs);
        Mockito.when(messageDocService.countUnreadMessageGroupByConversation(new ObjectId(messageCenterId), userProfileId, receiveInApp))
                .thenReturn(listCount);
        Mockito.when(messageDocService.getLatestUpdatedDateByConversationIds(conversationIds, userProfileId, receiveInApp))
                .thenReturn(messageLatestUpdated);
    }

    private void mockitoGetById() {
        String id = "5b8f52fef11a2e77b96e93a1";
        String messageCenterId = "5b8f52fef11a2e77b96e93a0";
        Long userProfileId = 1L;
        ObjectId conversationDocId = new ObjectId("5b8f52fef11a2e77b96e93a1");
        ConversationDoc conversationDoc = new ConversationDoc(conversationDocId,  new ObjectId(messageCenterId));
        Long numberUnread = 1L;
        int receiveInApp = 1;
        List<ObjectId> conversationIds = Lists.newArrayList(conversationDocId);
        List<ObjectLatest> messageLatestUpdated = Lists.newArrayList(new ObjectLatest(conversationDocId, DateUtils.addDays(new Date(), -10)));

        Mockito.when(conversationDocService.findByIdAndUserProfileId(new ObjectId(id), userProfileId)).thenReturn(conversationDoc);
        Mockito.when(messageDocService.countUnreadMessageByUserProfileId(conversationDoc.getId(), userProfileId, receiveInApp)).thenReturn(numberUnread);
        Mockito.when(messageDocService.getLatestUpdatedDateByConversationIds(conversationIds, userProfileId, receiveInApp))
                .thenReturn(messageLatestUpdated);
    }
}