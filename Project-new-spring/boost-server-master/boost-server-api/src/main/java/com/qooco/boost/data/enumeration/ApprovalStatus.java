package com.qooco.boost.data.enumeration;


import com.qooco.boost.data.constants.Constants;

public enum ApprovalStatus {
	PENDING(Constants.PENDING_STATUS, Constants.PENDING_STATUS_MESSAGE),
	APPROVED(Constants.APPROVED_STATUS, Constants.APPROVED_STATUS_MESSAGE),
	DISAPPROVED(Constants.DISAPPROVED_STATUS, Constants.DISAPPROVED_STATUS_MESSAGE),
	UPDATED_GSD(Constants.UPDATED_GDS_STATUS, Constants.UPDATED_GDS_STATUS_MESSAGE);

	private final int code;
	private final String description;

	ApprovalStatus(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getCode() {
		return code;
	}

}