package org.htht.util;

/**
 * @(#)UtilNumber.java  1.00 
 * Apr 26, 2008 4:18:58 PM
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
import java.text.NumberFormat;
/**
 * 数字工具类
 *
 */
public class UtilNumber {
	public static String farmatNum(Object num,int maxDot) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(maxDot);
        return format.format(num);
    }

    public static void main(String[] args) {
        System.out.println(farmatNum(10.0119,6));
    }

}
