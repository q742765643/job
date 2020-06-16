/**
 * 
 */
package org.htht.util;
/**
 * @(#)RandomString.java  1.00 
 * Apr 26, 2008 4:01:05 PM
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
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author cabby
 * 2006-8-9 10:35:11
 */

public class RandomString {
	
	public static String getRnadomDateTime(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		String timeString ="";
		
		Random random = new Random();
		random.nextInt();
		timeString = ""
			+String.valueOf(random.nextInt(10))
			+String.valueOf(c.get(Calendar.MONTH)+1)
			+String.valueOf(random.nextInt(10))
			+String.valueOf(c.get(Calendar.DATE))
			+String.valueOf(random.nextInt(10))
			+String.valueOf(c.get(Calendar.HOUR_OF_DAY))
			+String.valueOf(random.nextInt(10))
			+String.valueOf(c.get(Calendar.MINUTE))
			+String.valueOf(random.nextInt(10))
			+String.valueOf(c.get(Calendar.SECOND))
			+String.valueOf(c.get(Calendar.MILLISECOND));
		return timeString;
	}

}
