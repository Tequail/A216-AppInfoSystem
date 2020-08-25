package com.github.dao;

import com.github.pojo.AppInfo;
import com.github.pojo.DataDictionary;
import com.github.pojo.QueryAppInfoVO;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppInfoMapper {
    int getTotalCount(QueryAppInfoVO queryAppInfoVO);

    List<AppInfo> findAppInfo(QueryAppInfoVO queryAppInfoVO);

    List<DataDictionary> findDictionaryList(String param);

    AppInfo apkNameExist(String apkName);

    int appInfoAdd(AppInfo appInfo);

    AppInfo findAppInfoById(Integer id);

    int checkSave(@Param("status") Integer status,@Param("id") Integer id);
}
