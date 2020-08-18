package com.github.service.backend;

import com.github.dao.backend.BackendUserMapper;
import com.github.pojo.BackendUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BackendUserServiceImpl implements BackendUserService {

    @Resource
    private BackendUserMapper backendUserMapper;
    @Override
    public BackendUser doLogin(String userCode, String userPassword) {
        return backendUserMapper.doLogin(userCode,userPassword);
    }
}
