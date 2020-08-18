package com.github.service.backend;

import com.github.pojo.BackendUser;

public interface BackendUserService {
    BackendUser doLogin(String userCode, String userPassword);
}
