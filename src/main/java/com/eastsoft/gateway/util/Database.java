package com.eastsoft.gateway.util;

import java.io.IOException;
import java.util.Map;

import com.eastsoft.gateway.GatewayJFrame;
import com.eastsoft.gateway.Para;
import com.eastsoft.gateway.util.Connect;
import com.eastsoft.gateway.util.SQLite;
import com.eastsoft.gateway.util.ToolUtil;

public class Database
{
	
	public static boolean downloadFile(Connect telnet,String path,String fileName,String hostIP){
		if(null==path||"".equals(path)){
			path = "./";
		}
		String cd = telnet.sendCommand("cd "+path);
		GatewayJFrame.showMssageln(cd);
		String tftp = telnet.sendCommand("tftp -g -l "+fileName+" -r "+fileName+" "+hostIP);
		if(tftp.contains("timeout")){
			GatewayJFrame.showMssageln("传输超时，请检查连接！");
			return false;
		}
		GatewayJFrame.showMssageln(tftp);
		return true;
	}
	public static boolean dbupdateDB(Connect telnet, String gatewaydb, String hostIP)
	{
		String mtd = telnet.sendCommand("mtd erase production");
		String tftps = telnet.sendCommand("dbupdate " + gatewaydb + " " + hostIP);
		//GatewayJFrame.showMssageln(tftps);
		if (tftps.contains("timeout"))
		{
			GatewayJFrame.showMssage("传输超时,检查TFTP是否打开，与路由器的连接是否断开\n");
			return false;
		}else if(tftps.contains("error")){
			GatewayJFrame.showMssage("传输失败 ,检查TFTP是否打开，与路由器的连接是否断开\n");
			return false;
		}
		String lsGatewayDb = telnet.sendCommand("ls /gateway/cpp/main");
		if(!lsGatewayDb.contains(gatewaydb)){
			GatewayJFrame.showMssageln("路由器下载"+gatewaydb+"失败！");
			return false;
		}else{
			GatewayJFrame.showMssageln("路由器下载"+gatewaydb+"成功！");
			String sync = telnet.sendCommand("sync");
		}
		return true;
	}
	
	public static boolean dbuploadGateway(Connect telnet, String hostIP, String gatewaydb,String path)
	{
		if(null==telnet){
			GatewayJFrame.showMssageln("智能路由器网关未连接,或者网关连接已中断,请重新连接...");
			return false;
		}
		String dbupload = telnet.sendCommand("dbupload "+gatewaydb+" "+hostIP);
		if(dbupload.contains("can't open '"+gatewaydb+"'")){
			GatewayJFrame.showMssageln("智能路由器网关中不存在数据库...");
			return false;
		}else if(dbupload.contains("timeout")){
			GatewayJFrame.showMssageln("连接超时，请检查tftp服务器是否开启，tftp服务器地址（这里是本机IP）是否正确...");
		}else if(dbupload.contains("ok")&&ToolUtil.isFileExist(path+"\\"+gatewaydb)){
			GatewayJFrame.showMssageln("数据库文件上传至tftp根目录，检验数据库信息...");
		}
		
		try{
			Thread.sleep(2000);
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean checkGatewayDb(Connect telnet,String gid,String hostIP,String gatewaydb,String path,Map<String, Object> map){
		boolean flag = true;
		dbuploadGateway(telnet,hostIP,gatewaydb,path);
		Map account = SQLite.getAccountByGid(path+"\\"+gatewaydb, gid);
		Map para = SQLite.getPara(path+"\\"+gatewaydb);
		if(null==account||null==para){
			GatewayJFrame.showMssageln("网关数据库检测失败...");
			flag=false;
		}else{
			if(gid.equals(account.get("gid"))){
				GatewayJFrame.showMssageln("网关号GID写入成功...");
			}
		}
		return flag;
	}
	
	public static boolean uploadFileOfUsb(Connect telnet,String hostIP){
		if(null==telnet){
			GatewayJFrame.showMssageln("路由器网关连接不存在...");
			return false;
		}
		String cd = telnet.sendCommand("cd /mnt");
		//GatewayJFrame.showMssageln(cd);
		String absolute = telnet.sendCommand("find -name testFile.txt").split("\r\n")[1];
		//GatewayJFrame.showMssageln(absolute);
		if(false==absolute.contains("testFile.txt")){
			GatewayJFrame.showMssageln("找不文件testFile.txt");
			return false;
		}
		String path = absolute.substring(0, absolute.length()-"testFile.txt".length());
		String smartRouter = telnet.sendCommand("cd "+path);
		//GatewayJFrame.showMssageln(smartRouter);
		String tftp = telnet.sendCommand("tftp -p -l testFile.txt "+hostIP);
		if(tftp.contains("timeout")){
			GatewayJFrame.showMssageln("文件传输超时检查主机地址，路由器地址等设置是否正确 ");
			return false;
		}
		GatewayJFrame.showMssageln(tftp);
		try{
			Thread.sleep(2500);
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
}
