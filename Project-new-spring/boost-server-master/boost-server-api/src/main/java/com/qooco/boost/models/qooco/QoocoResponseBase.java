package com.qooco.boost.models.qooco;

import com.google.gson.annotations.SerializedName;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 6/19/2018 - 6:53 PM
*/
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class QoocoResponseBase {

    @SerializedName("Result")
    private String result;

    @SerializedName("ErrorDescription")
    private String errorDescription;

    @SerializedName("ErrorCode")
    private int errorCode;

    //To check user contact(email, phone number) from API response
    @SerializedName("exist")
    private boolean exist;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
