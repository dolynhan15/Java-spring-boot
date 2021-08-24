package com.qooco.boost.models.request.authorization;

import com.qooco.boost.utils.StringUtil;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 6/26/2018 - 2:37 PM
 */
public class ForgotPasswordReq {
	private String username;
	private String email;

	public ForgotPasswordReq(String username, String email) {
		this.username = username;
		this.email = email;
	}

	public ForgotPasswordReq(ForgotPasswordReq req) {
		this(req.getUsername(), req.getEmail());
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		if(!StringUtil.isEmpty(email)){
			return email.toLowerCase();
		}
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
