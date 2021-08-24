package com.qooco.boost.data.oracle.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qooco.boost.utils.DateUtils;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@SuperBuilder(toBuilder = true)
public class BaseEntity {

	@JsonIgnore
	@Basic(optional = false)
	@Column(name = "CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Basic(optional = false)
	@Column(name = "IS_DELETED")
	private boolean isDeleted;

	@JsonIgnore
	@Basic(optional = false)
	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Basic(optional = false)
	@Column(name = "UPDATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@JsonIgnore
	@Basic(optional = false)
	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	public BaseEntity() {
		Date now = DateUtils.nowUtcForOracle();

		this.createdDate = now;
		this.updatedDate = now;
		this.isDeleted = false;
	}

	public BaseEntity(Long createdBy) {
		Date now = DateUtils.nowUtcForOracle();
		this.createdDate = now;
		this.updatedDate = now;
		this.isDeleted = false;
		this.createdBy = createdBy;
		this.updatedBy = createdBy;
	}

	public BaseEntity(Date createdDate, Long createdBy, Date updatedDate, Long updatedBy, boolean isDeleted) {
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.updatedDate = updatedDate;
		this.updatedBy = updatedBy;
		this.isDeleted = isDeleted;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}
}
