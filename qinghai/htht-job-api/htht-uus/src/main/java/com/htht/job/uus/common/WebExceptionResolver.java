package com.htht.job.uus.common;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.htht.job.uus.util.ResponseModel;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author yuguoqing
 * @Date 2018年5月9日 下午1:46:29 统一异常封装 status500服务器有错 status200服务器正确返回数据
 *
 */
public class WebExceptionResolver implements HandlerExceptionResolver
{
	// private static transient Logger logger =
	// LoggerFactory.getLogger(WebExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex)
	{
//		String uri=
//		StringBuffer sbu =  request.getRequestURL();
//		String url = request.getRequestURI();
//		String uname = request.getParameter("userName");
		ModelAndView mv = new ModelAndView();
		response.setStatus(Consts.ResposeStatus.STATUS_OK); // 设置状态码
		response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 设置ContentType
		response.setCharacterEncoding("UTF-8"); // 避免乱码
		response.setHeader("Cache-Control", "no-cache, must-revalidate");

		ResponseModel resp = new ResponseModel();
		resp.setStatus(Consts.ResposeStatus.SERVER_ERROR);
		resp.setCode(Consts.ResposeStatus.SERVER_ERROR);
		resp.setData("Server Error");
		try
		{
			response.getWriter().write(JSON.toJSONString(resp));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println(ex.getMessage());
		return mv;
	}

}