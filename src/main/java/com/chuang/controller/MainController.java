package com.chuang.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
//    @Autowired
//    ILocalmanService localmanService;
   
    @RequestMapping("/")
    public String main(ModelMap model){
        return "/home";
    }
}
