package com.qooco.boost.data.mongo.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Document(collection = "SocketConnectionDoc")
@Setter
@Getter
@NoArgsConstructor
public class SocketConnectionDoc {

    @Id
    private String token;
    private String username;
    private String appId;
    private Long userProfileId;
    private List<String> sessionIds;
    private Map<String, String> channels;

    private Date lastedOnlineDate;
    private Date updatedDate;
    private boolean isLogout;

    public SocketConnectionDoc(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketConnectionDoc that = (SocketConnectionDoc) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {

        return Objects.hash(token);
    }
}
