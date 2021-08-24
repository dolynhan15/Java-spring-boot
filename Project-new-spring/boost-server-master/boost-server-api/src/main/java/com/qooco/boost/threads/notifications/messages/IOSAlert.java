package com.qooco.boost.threads.notifications.messages;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/30/2018 - 2:47 PM
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class IOSAlert {
    private String tile;
    private String subtitle;
    private int body;
}
