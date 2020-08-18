package com.github.dao.backend;

import com.github.pojo.BackendUser;
import org.apache.ibatis.annotations.Param;

public interface BackendUserMapper {
    BackendUser doLogin(@Param("userCode") String userCode,@Param("userPassword") String userPassword);
}
