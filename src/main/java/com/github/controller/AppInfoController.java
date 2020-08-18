package com.github.controller;

import com.alibaba.fastjson.JSON;
import com.github.pojo.*;
import com.github.service.AppCategoryService;
import com.github.service.AppVersionService;
import com.github.service.AppinfoService;
import com.github.util.PageBean;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/dev/flatform/app")
public class AppInfoController {

    @Resource
    private AppinfoService appinfoService;

    @Resource
    private AppVersionService appVersionService;

    @Resource
    private AppCategoryService appCategoryService;
    /*/appview/64查看app*/
    @RequestMapping("/appview/{id}")
    public String appView(Model model, @PathVariable Integer id){
        //app所有信息
        AppInfo appInfo = appinfoService.findAppInfoById(id);
        /*版本列表*/
        List<AppVersion> appVersionList = appVersionService.appversionadd(id);
        model.addAttribute("appVersionList",appVersionList);
        model.addAttribute("appInfo",appInfo);
        return "developer/appinfoview";
    }

    //appinfoaddsave 添加app信息附带logo图片 需要进行文件上传 1.form表单 enctype="multipart/form-data"
    @RequestMapping("/appinfoaddsave")
    public String appInfoAddSave(HttpServletRequest request, HttpSession session, @ModelAttribute AppInfo appInfo, @RequestParam("a_logoPicPath")MultipartFile multipartFile){
        /*图片地址*/
        String logoLocPath = null;
        String logoPicPath = null;
        // 判断是否是文件上传
        if (!multipartFile.isEmpty()){
            // 文件上传准备工作
            // 1.指定上传的目录
            // 获取相对路径的决定路径 statics/uploadfiles
            String realPath = session.getServletContext().getRealPath("statics/uploadfiles");
            // 2.定义上传文件的大小
            int fileSize = 2097152;//2M
            //3.定义上传文件的类型
            List<String> fileNameList = Arrays.asList("jpg","png");
            // 获取文件的大小
            long size = multipartFile.getSize();
            // 获取文件名
            String fileName = multipartFile.getOriginalFilename();
            // 获取文件的扩展名
            String extension = FilenameUtils.getExtension(fileName);
            // 判断是否符合你文件的要求
            if (fileSize < size){// 大小不合适
                request.setAttribute("fileUploadError","上传文件超过大小限制!");
                return "developer/appinfoadd";
            }else if(!fileNameList.contains(extension)){//文件格式不符合
                request.setAttribute("fileUploadError","不支持此种文件的格式!");
                return "developer/appinfoadd";
            }else{
                // 重命名  com.google.android.inputmethod.pinyin.jpg
                String newFileName = appInfo.getAPKName()+"_"+System.currentTimeMillis()+"."+extension;
                File dest = new File(realPath+File.separator+newFileName);
                try {
                    // 进行文件上传
                    multipartFile.transferTo(dest);
                    // 获取文件上传的地址 获取相对路径 /statics/uploadfiles
                    logoPicPath = File.separator+"statics"+File.separator+"uploadfiles"+File.separator+newFileName;
                    // 获取绝对路径
                    logoLocPath = realPath+File.separator+newFileName;
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                //设置相对路径
                appInfo.setLogoPicPath(logoPicPath);
                //设置绝对路径
                appInfo.setLogoLocPath(logoLocPath);
                DevUser devUser = (DevUser)session.getAttribute("devUserSession");
                appInfo.setCreatedBy(devUser.getId());
                appInfo.setCreationDate(new Date());
                appInfo.setDevId(devUser.getId());

                boolean add = appinfoService.appInfoAdd(appInfo);
                if (!add){
                    return "developer/appinfoadd";
                }
            }
        }
        return "redirect:/dev/flatform/app/list";
    }

    //apkexist.json
    @ResponseBody
    @RequestMapping("/apkexist.json")
    public String checkAPKName(@RequestParam String APKName){
        /*if(data.APKName == "empty"){//参数APKName为空，错误提示
            alert("APKName为不能为空！");
        }else if(data.APKName == "exist"){//账号不可用，错误提示
            alert("该APKName已存在，不能使用！");
        }else if(data.APKName == "noexist"){//账号可用，正确提示
            alert("该APKName可以使用！");
        }*/
        Map<Object,String> map = new HashMap<>( );
        if (APKName.isEmpty()){
            map.put("APKName","empty");
        }else if(appinfoService.apkNameExist(APKName)){
            map.put("APKName","exist");
        }else{
            map.put("APKName","noexist");
        }
        return JSON .toJSONString(map);
    }

    /**
     * AJAX动态加载所属平台
     * @param tcode
     * @return
     */
    @ResponseBody
    @RequestMapping("/datadictionarylist.json")
    public String getFlatFormList(@RequestParam String tcode){
        List<DataDictionary> flatFormList = appinfoService.findDictionaryList(tcode);
        return JSON.toJSONString(flatFormList);
    }

    /**
     * 跳转添加app信息
     * @return
     */
    @RequestMapping("/appinfoadd")
    public  String appInfoAdd(){
        return "developer/appinfoadd";
    }

    // AJAX请求必须添加注解 @ResponseBody   categorylevellist.json?pid=2  利用以前学习的rest风格接收参数
    @ResponseBody
    @RequestMapping("/categorylevellist.json")
    public String getCategoryList(Integer pid){
        List<AppCategory> appCategoryList = appCategoryService.getAppCategoryListByParentiId(pid);
        return JSON.toJSONString(appCategoryList);
    }
    /*想要通过对象来接收从后台传输过来的数据@ModelAttribute*/

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
        PageBean<AppInfo> pages = appinfoService.findAppList(queryAppInfoVO);

        //查询app状态
        List<DataDictionary> statusList = appinfoService.findDictionaryList("APP_STATUS");

        /*查询所属平台*/
        List<DataDictionary> flatFormList = appinfoService.findDictionaryList("APP_FLATFORM");

        // 查询一级分类信息 categoryLevel1List
        List<AppCategory> categoryLevel1List = appCategoryService.getAppCategoryListByParentiId(null);

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
        return "developer/appinfolist";
    }
}
