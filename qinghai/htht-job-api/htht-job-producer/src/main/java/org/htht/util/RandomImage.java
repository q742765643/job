package org.htht.util;
/**
 * @(#)RandomImage.java  1.00 
 * Apr 26, 2008 4:00:53 PM
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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RandomImage extends HttpServlet {

	private static final long serialVersionUID = -9012974373290892265L;

	public static final String SESSION_RANDOM_CODE_STR = "randomCode";

	/**
	 * Constructor of the object.
	 */
	public RandomImage() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}

	public Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// response.setContentType("application/octet-stream; charset=gb2312");
		// response.setContentType("text/html");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		int width = 60, height = 20;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();

		Random random = new Random();

		g.setColor(new Color(220, 220, 220));
		g.fillRect(0, 0, width, height);

		g.setFont(new Font("Georgia", Font.PLAIN, 18));

		// g.setColor(new Color());
		// g.drawRect(0,0,width-1,height-1);

		// g.setColor(getRandColor(180,220));
		g.setColor(new Color(150, 150, 150));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(1);
			int yl = random.nextInt(1);
			g.drawLine(x, y, x + xl, y + yl);
		}

		String sRand = "";
		for (int i = 0; i < 4; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
			// g.setColor(new
			// Color(20+random.nextInt(110),20+random.nextInt(110),20+random.nextInt(110)));//???????????????????????????????
			g.setColor(new Color(0, 0, 0));
			g.drawString(rand, 13 * i + 6, 16);
		}

		request.getSession().setAttribute(SESSION_RANDOM_CODE_STR, sRand);

		g.dispose();
		try {
			OutputStream out = response.getOutputStream();
			ImageIO.write(image, "JPEG", out);
			out.close();
		} catch (Exception e) {

		}
	}

	/**
	 * RandomCode
	 * 
	 * @param request
	 * @return boolean true it is right �� otherwise false;
	 */
	public static boolean validateRandomCode(HttpServletRequest request,
			String input) {
		String code = (String) request.getSession().getAttribute(
				SESSION_RANDOM_CODE_STR);
		if (code != null) {
			request.getSession().removeAttribute(SESSION_RANDOM_CODE_STR);
			return code.equals(input);
		} else {
			return true;
		}
	}
}
