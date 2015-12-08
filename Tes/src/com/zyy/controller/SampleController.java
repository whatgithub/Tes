/*
 * 项目名：Tes 文件名：SampleController.java 版权：Copyright by www.symboltech.com 描述： 修改人：symbol 修改时间：2015年12月7日 修改内容：
 */

package com.zyy.controller;


import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author symbol
 * @version 2015年12月7日
 * @see SampleController
 * @since
 */
@Controller
@EnableAutoConfiguration
public class SampleController
{

    @RequestMapping(value = "/home", method = RequestMethod.POST)
    @ResponseBody
    public String home(String file, String head1)
    {
        System.out.println("ok  ! " + file + head1);
        return "success haha." + file + head1;
    }

    @RequestMapping(value = "/home2", method = RequestMethod.POST)
    @ResponseBody
    public String home(HttpServletRequest request, @RequestHeader String head1, MultipartFile bin, String comment)
    {
        System.out.println("ok  ! " + request.getHeader("head1"));
        File f = new File("E://logs", bin.getOriginalFilename() + "1");
        try
        {
            if (f.isDirectory() && !f.exists())
            {
                f.mkdirs();
            }
            bin.transferTo(f);
        }
        catch (IllegalStateException | IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "success haha." + "";
    }

    public static void main(String[] args)
        throws Exception
    {
        SpringApplication.run(SampleController.class, args);
    }

}
