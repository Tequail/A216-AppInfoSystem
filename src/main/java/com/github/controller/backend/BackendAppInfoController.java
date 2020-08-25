package com.github.controller.backend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pojo.*;
import com.github.service.AppCategoryService;
import com.github.service.AppVersionService;
import com.github.service.AppinfoService;
import com.github.util.JedisUtils;
import com.github.util.PageBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/manager/backend/app")
public class BackendAppInfoController {

    @Resource
    private AppinfoService appinfoService;
    @Resource
    private AppCategoryService appCategoryService;
    @Resource
    private AppVersionService appVersionService;

    @RequestMapping("/checksave")
    public String checkSave(Integer status,Integer id){
       if (appinfoService.checkSave(status,id)){

        return "redirect:/manager/backend/app/list";
       }
       return "backend/appcheck";
    }

    @RequestMapping("/check")
    public String chheck(Model model, Integer aid, Integer vid){
        AppInfo appInfo = appinfoService.findAppInfoById(aid);
        AppVersion appVersion = appVersionService.findAppVersionById(vid);
        model.addAttribute("appInfo",appInfo);
        model.addAttribute("appVersion",appVersion);
        return "backend/appcheck";
    }


    @ResponseBody
    @RequestMapping("/categorylevellist.json")
    public String getCategoryList(Integer pid){
        List<AppCategory> appCategoryList = appCategoryService.getAppCategoryListByParentiId(pid);
        return JSON.toJSONString(appCategoryList);
    }

    /**
     * 查询app列表
     * @param request
     * @param queryAppInfoVO
     * @return
     */
    @RequestMapping("/list")
    public String appList(HttpServletRequest request, @ModelAttribute QueryAppInfoVO queryAppInfoVO){
        // 起始页
        if (queryAppInfoVO.getPageIndex() == null){
            queryAppInfoVO.setPageIndex(1);
        }
        // 每页显示的条数
        queryAppInfoVO.setPageSize(5);
        // 后台审核条件 为1  待审核
        queryAppInfoVO.setQueryStatus(1);

        // 开始缓存   先判断redis中有没有这些数据
        // 第一次进来的时候 是从mysql数据库找那个查询数据  有点慢
        // 但是查到的数据保存到redis中  第二次查询的时候 判断redis中有没有这条数据
        // 如果有直接redis中获取数据 快
        Jedis jedis = JedisUtils.getJedis();
        PageBean<AppInfo> pages = null;
        List<DataDictionary> statusList = null;
        List<DataDictionary> flatFormList = null;
        List<AppCategory> categoryLevel1List = null;

        pages = appinfoService.findAppList(queryAppInfoVO);

        //查询app状态
        statusList = appinfoService.findDictionaryList("APP_STATUS");

        /*查询所属平台*/
        flatFormList = appinfoService.findDictionaryList("APP_FLATFORM");

        // 查询一级分类信息 categoryLevel1List
        categoryLevel1List = appCategoryService.getAppCategoryListByParentiId(null);

        /*进行数据回显*/
        request.setAttribute("querySoftwareName",queryAppInfoVO.getQuerySoftwareName());
        request.setAttribute("queryStatus",queryAppInfoVO.getQueryStatus());
        request.setAttribute("queryFlatformId",queryAppInfoVO.getQueryFlatformId());
        request.setAttribute("queryCategoryLevel1",queryAppInfoVO.getQueryCategoryLevel1());
        request.setAttribute("queryCategoryLevel2",queryAppInfoVO.getQueryCategoryLevel2());
        request.setAttribute("queryCategoryLevel3",queryAppInfoVO.getQueryCategoryLevel3());

        /*完善分类回显*/
        /*如果传了一级分类 代表触发过三级联动 认为你应该将二级分类中的信息全部查询到*/
        if (queryAppInfoVO.getQueryCategoryLevel1() != null){
            List<AppCategory> categoryLevel2List = appCategoryService.getAppCategoryListByParentiId(queryAppInfoVO.getQueryCategoryLevel1());
            request.setAttribute("categoryLevel2List",categoryLevel2List);
        }
        if (queryAppInfoVO.getQueryCategoryLevel2() != null){
            List<AppCategory> categoryLevel3List = appCategoryService.getAppCategoryListByParentiId(queryAppInfoVO.getQueryCategoryLevel2());
            request.setAttribute("categoryLevel3List",categoryLevel3List);
        }
        /*存储信息*/
        request.setAttribute("categoryLevel1List",categoryLevel1List);
        request.setAttribute("flatFormList",flatFormList);
        request.setAttribute("statusList",statusList);
        request.setAttribute("pages",pages);
        request.setAttribute("appInfoList",pages.getResult());
        return "backend/applist";
    }
}
