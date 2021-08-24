package com.qooco.boost.threads.notifications.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/30/2018 - 3:01 PM
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationMessage {
    private int targetApp;
    private Object message;
}
