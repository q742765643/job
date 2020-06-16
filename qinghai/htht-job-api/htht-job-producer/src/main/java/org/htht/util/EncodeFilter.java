package org.htht.util;
/**
 * @(#)EncodeFilter.java  1.00 
 * Apr 26, 2008 3:50:44 PM
 * Copyright (c) 2007-2008 __MyCorp 有限公司 版权所有
 * __Mycorp Company of China. All rights reserved.
 * 
 * This software is the confidential and proprietary
 * information of __Mycorp Company of China.
 *
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with __Mycorp.
 * 
 */
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/*
 * 字符编码转换 过滤器 实现类
 */
public class EncodeFilter implements Filter {

	protected String encoding = null;

	protected FilterConfig filterconfig = null;

	public void destroy() {
		this.encoding = null;
		this.filterconfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (request.getCharacterEncoding() == null) {
			String encoding = getEncoding();
			if (encoding != null)
				request.setCharacterEncoding(encoding);
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterconfig) throws ServletException {

		this.filterconfig = filterconfig;
		this.encoding = filterconfig.getInitParameter("encoding");
	}

	protected String getEncoding() {
		return (this.encoding);
	}

}
