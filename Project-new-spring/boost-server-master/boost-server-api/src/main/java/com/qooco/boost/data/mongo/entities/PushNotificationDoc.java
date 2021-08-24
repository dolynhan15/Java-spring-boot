package com.qooco.boost.data.mongo.entities;

import com.baidu.yun.push.model.PushBatchUniMsgRequest;
import com.baidu.yun.push.model.PushMsgToAllRequest;
import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest;
import com.qooco.boost.threads.notifications.response.PushMessageResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "PushNotificationDoc")
public class PushNotificationDoc {
    @Id
    private ObjectId id;
    private Long requestId;
    private int errorCode;
    private String errorMsg;
    private int typeMsg;
    private int targetApp;
    private PushMsgToSingleDeviceRequest pushMsgToSingle;
    private PushMsgToAllRequest pushMsgToAll;
    private PushBatchUniMsgRequest pushBatchUniMsg;

    public PushNotificationDoc(Object message, PushMessageResponse response) {
        setRequestId(response.getRequestId());
        setErrorCode(response.getErrorCode());
        setErrorMsg(response.getErrorMsg());
        if (message instanceof PushMsgToSingleDeviceRequest) {
            setPushMsgToSingle((PushMsgToSingleDeviceRequest) message);
        } else if (message instanceof PushMsgToAllRequest) {
            setPushMsgToAll((PushMsgToAllRequest) message);
        } else if (message instanceof PushBatchUniMsgRequest) {
            setPushBatchUniMsg((PushBatchUniMsgRequest) message);
        }
    }
}
