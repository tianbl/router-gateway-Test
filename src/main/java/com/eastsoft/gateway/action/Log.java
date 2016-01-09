package com.eastsoft.gateway.action;

import com.eastsoft.gateway.GatewayJFrame;
import com.eastsoft.gateway.util.Connect;

public class Log
{
	public static boolean uploadLog(Connect telnet, String logPath, String hostIP)
	{
		telnet.sendCommand("cd /");
		String cd = telnet.sendCommand("cd " + logPath);
		if (cd.contains("[root@(none) logs]#") == false
				&& cd.contains("root@OpenWrt:/gateway/cpp/main#") == false)
		{
			GatewayJFrame.showMssage("网关程序未下载，无法获得网关GID！\n");
			return false;
		}
		String tftps = telnet.sendCommand("tftp -pl rolling.log " + hostIP);
		if (tftps.contains("timeout"))
		{
			GatewayJFrame.showMssage("TFTP未打开，无法获得网关GID！\n");
			return false;
		}
		if (tftps.contains("can't open"))
		{
			GatewayJFrame.showMssage("网关中不存在日志文件，无法获得日志！\n");
			return false;
		}
		try
		{
			Thread.sleep(2500);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return true;
	}
}
