package com.github.controller;

import com.github.pojo.AppVersion;
import com.github.service.AppVersionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/dev/flatform/app")
public class AppVersionController {

    @Resource
    private AppVersionService appVersionService;

    /*appversionadd?id=58*/
    @RequestMapping("/appversionadd")
    public String appversionadd(Model model, Integer id){

        /*appVersionList*/
        List<AppVersion> appVersionList = appVersionService.appversionadd(id);
        model.addAttribute("appVersionList",appVersionList);
        return "developer/appversionadd";
    }
    /*appversionmodify?vid=41&aid=57*/
    @RequestMapping("/appversionmodify")
    public  String appversionmodify(Model model ,Integer vid ,Integer aid){
        List<AppVersion> appVersionList = appVersionService.appversionadd(aid);
        AppVersion appVersion  = appVersionService.findAppVersionById(vid);
        model.addAttribute("appVersionList",appVersionList);
        model.addAttribute("appVersion",appVersion);

        return "developer/appversionmodify";
    }

}
