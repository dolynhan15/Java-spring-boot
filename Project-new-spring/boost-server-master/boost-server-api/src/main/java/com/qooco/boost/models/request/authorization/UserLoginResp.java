package com.qooco.boost.models.request.authorization;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.UserInfoDTO;

public class UserLoginResp extends BaseResp<UserInfoDTO> {

	public UserLoginResp(UserInfoDTO data) {
		this.setData(data);
	}

	public UserInfoDTO getData() {
		return super.getData();
	}
}
