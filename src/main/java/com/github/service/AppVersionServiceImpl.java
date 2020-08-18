package com.github.service;

import com.github.dao.AppVersionMapper;
import com.github.pojo.AppVersion;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class AppVersionServiceImpl implements AppVersionService {

    @Resource
    private AppVersionMapper appVersionMapper;
    @Override
    public List<AppVersion> appversionadd(Integer id) {
        return appVersionMapper.appversionadd(id);
    }

    @Override
    public AppVersion findAppVersionById(Integer vid) {
        return appVersionMapper.findAppVersionById(vid);
    }
}
