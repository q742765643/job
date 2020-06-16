package com.htht.job.uus.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/screen")
public class ScreenController {
	
	@RequestMapping(value="/push")
	public void push(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //媒体类型为 text/event-stream
        PrintWriter out = response.getWriter();
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");
        //响应报文格式为:
        //data:Hello World
        //event:load
        //id:140312
        //换行符(/r/n)
        while(true){
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	 out.println("data:" + System.currentTimeMillis()+"/n/n");
        	 out.flush();
        }
        //out.close();
}

}
