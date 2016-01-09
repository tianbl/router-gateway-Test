package com.eastsoft.gateway;

public class Para
{
	public final static String qrcode = "SN:330300024B9810000001 USER:54328 PWD:123456";
	public final static String Gateway_IP = "192.168.1.254";
	public final static String localhost_IP = "192.168.1.2";
	public final static String accompany_IP = "192.168.1.3";
	public final static String pcgateway = "pcgateway.db";
	public final static String gateway = "gateway.db";
	public final static String password = "123456";
	
	// 视频功能库文件
	public final static String APP_LIB_FILE = "libCameraVideo.so";
	public final static String SYS_LIB_FILE = "libstdc++.so.6";
	
	//二维码匹配正则表达式
	public final static String regex1 = "(SN)(\\d{1,24})([A-Z' ''\n''\r']{1,8})"
			+ "(\\d{1,10})([A-Z' ''\n''\r']{1,8})(\\d{6})$";
	public final static String regex2 = "(SN)(\\w{1,24})(JUSER)(\\d{1,10})(JPWD)(\\d{6})$";
}
