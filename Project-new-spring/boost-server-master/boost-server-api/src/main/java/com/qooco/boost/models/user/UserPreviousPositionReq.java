package com.qooco.boost.models.user;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/4/2018 - 2:56 PM
 */

import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.entities.UserPreviousPosition;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserPreviousPositionReq {

    private Long id;
    private Date startDate;
    private Date endDate;
    private Long salary;
    private String contactPerson;
    private String companyName;
    private String positionName;
    private Long currencyId;
    private List<String> photos;

    public UserPreviousPositionReq() {
        //Do any thing
    }

    public UserPreviousPositionReq(Long id, Date startDate, Date endDate, Long salary, String contactPerson
            , String companyName, String positionName, List<String> photos) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.contactPerson = contactPerson;
        this.companyName = companyName;
        this.positionName = positionName;
        this.photos = photos;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public UserPreviousPosition updateToPreviousPosition(UserPreviousPosition original) {
        original.setId(this.id);
        original.setCurrency(new Currency(this.currencyId));
        original.setCompanyName(this.companyName);
        original.setPositionName(this.positionName);
        original.setStartDate(DateUtils.toUtcForOracle(this.startDate));
        if (Objects.isNull(endDate)) {
            original.setEndDate(null);
        } else {
            original.setEndDate(DateUtils.toUtcForOracle(this.endDate));
        }
        original.setSalary(this.salary);
        original.setContactPerson(this.contactPerson);
        original.setPhoto(StringUtil.convertToJson(ServletUriUtils.getRelativePaths(photos)));
        return original;
    }
}
