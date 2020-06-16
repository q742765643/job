package org.htht.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchEngine {

	class Engine{
		String name;
		String url;
		String encode;

		String key;
		
	}
	public String parse(String url) {
		String word=null;
		url = url.toLowerCase();
		String regex = "^http://[^\\.]*\\.*([^/]*)/([^?]*)\\?([^\n]*)$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(url);
		boolean result = m.find();
		System.out.println(result);
		String skey,query;
		if (result){
			int count=m.groupCount();
			if(count>=2){
				skey=m.group(1);
				query=m.group(2);
				word = this.searchKey(skey,query);
			}
		}
		return word;
	}
	private String searchKey(String skey,String query){
		String word = null;
		return skey+","+query;
	}
	public static void main(String[] args) {
		String url = "http://www.baidu.com/s?lm=0&si=&rn=10&ie=gb2312&ct=0&wd=datagrid+%CA%B9%D3%C3&pn=10&cl=3";
		SearchEngine se = new SearchEngine();

		String m = se.parse(url);
		System.out.println(m);
	}

}
