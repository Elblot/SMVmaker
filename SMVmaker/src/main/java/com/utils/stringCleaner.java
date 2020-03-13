package com.utils;

public class stringCleaner {
	
	public static String clean(String s) {
		String res = s.replaceAll("=", "");
		res = res.replaceAll("[^A-Za-z0-9#-]", "");
		res = res.replaceAll("--", "-");
		return res;
	}
	
}
