package com.github.controller.backend;

import com.github.pojo.BackendUser;
import com.github.service.backend.BackendUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager")
public class BackendLoginController {
    @Resource
    private BackendUserService backendUserService;

    @RequestMapping("/login")
    public String login(){
        return "backendlogin";
    }

    @RequestMapping("/dologin")
    public  String doLogin(HttpSession session, HttpServletRequest request, @RequestParam String userCode, @RequestParam String userPassword ){

        BackendUser backendUser = backendUserService.doLogin(userCode,userPassword);
        if (backendUser == null){
            request.setAttribute("error","用户名或者密码错误");
            return "backendlogin";
        }
        session.setAttribute("userSession",backendUser);
        return "backend/main";
    }
}
