package com.eastsoft.gateway.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import com.eastsoft.gateway.GatewayJFrame;
import com.eastsoft.gateway.Para;
import com.eastsoft.gateway.util.Connect;
import com.eastsoft.gateway.util.SQLite;
import com.eastsoft.gateway.util.ToolUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.util.IOUtils;

public class Database
{
	public static String usbFilePath = null;
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
        GatewayJFrame.showMssageln("清空production分区...");
		String mtd = telnet.sendCommand("mtd erase production");
		String tftps = telnet.sendCommand("dbupdate " + gatewaydb + " " + hostIP);
		GatewayJFrame.showMssageln(tftps);
		if (tftps.contains("timeout"))
		{
			GatewayJFrame.showMssage("传输超时,检查TFTP是否打开，与路由器的连接是否断开\n");
			return false;
		}else if(tftps.contains("error")){
			GatewayJFrame.showMssage("传输失败,请检查产测环境;检查TFTP是否打开,与路由器的连接是否断开\n");
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
			return false;
		}else if(dbupload.contains("ok")&&ToolUtil.isFileExist(path+"\\"+gatewaydb)){
			GatewayJFrame.showMssageln("数据库文件上传至tftp根目录，进行MD5检验数据库信息...");
		}

		try{
			Thread.sleep(2000);
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean checkGatewayDb(Connect telnet,String hostIP,String gatewaydb,String path,String localDbPath){
		boolean flag = false;
		dbuploadGateway(telnet,hostIP,gatewaydb,path);
//		Map account = SQLite.getAccountByGid(path+"\\"+gatewaydb, gid);
//		Map para = SQLite.getPara(path+"\\"+gatewaydb);
        FileInputStream gatewayfis = null;
        FileInputStream localdbfis = null;
        try{
            //计算网关.db文件md5
            gatewayfis = new FileInputStream(path+"\\"+gatewaydb);
            String gateWayDbMd5 = DigestUtils.md5Hex(IOUtils.toByteArray(gatewayfis));
            IOUtils.closeQuietly(gatewayfis);
            //计算本地.db文件md5
            localdbfis = new FileInputStream(localDbPath);
            String localDbMd5 = DigestUtils.md5Hex(IOUtils.toByteArray(localdbfis));
            IOUtils.closeQuietly(localdbfis);
            GatewayJFrame.showMssageln("pc生成gateway.db的md5码:"+localDbMd5+"\n"
                    +"网关上传的gateway.db的md5码:"+gateWayDbMd5);
            if(gateWayDbMd5.equalsIgnoreCase(localDbMd5)){
                GatewayJFrame.showMssageln("网关数据库md5验证通过...");
                flag=true;
            }else {
                GatewayJFrame.showMssageln("网关数据库md5验证失败！");
            }
        }catch (Exception e){
            GatewayJFrame.showMssageln("md5验证网关数据库失败，原因找不到文件，检查pc备份文件和网关上传文件是否存在！");
            e.printStackTrace();
        }finally {
            try {
                if(gatewayfis!=null){
                    gatewayfis.close();
                }
                if(localdbfis!=null){
                    localdbfis.close();
                }
            } catch (IOException e) {
                GatewayJFrame.showMssageln("文件流关闭失败");
                e.printStackTrace();
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
		boolean needFineTestFile = true;
		if(usbFilePath==null){
			needFineTestFile = true;
		}else {
			String findTestFile = telnet.sendCommand("ls " + usbFilePath+"testFile.txt").split("\r\n")[1];
			if(findTestFile.contains("No such file or directory")){
				needFineTestFile = true;
			}else if(findTestFile.contains(usbFilePath+"testFile.txt")){
				needFineTestFile = false;
			}
		}
		if(needFineTestFile){	//重新找usb中测试文件的路径
			GatewayJFrame.showMssageln("搜索U盘中testFile.txt文件路径...");
			String absolute = telnet.sendCommand("find -name testFile.txt").split("\r\n")[1];
			if(false==absolute.contains("testFile.txt")){
				GatewayJFrame.showMssageln("找不文件testFile.txt...");
				return false;
			}else {
				GatewayJFrame.showMssageln("U盘中testFile.txt文件在路由器的路径为:/mnt"+absolute);
			}
			usbFilePath = absolute.substring(0, absolute.length()-"testFile.txt".length());
//			GatewayJFrame.showMssageln("---------"+usbFilePath+","+absolute);
		}

		GatewayJFrame.showMssageln("上传USB测试文件...");
		String smartRouter = telnet.sendCommand("cd "+usbFilePath);
		String tftp = telnet.sendCommand("tftp -p -l testFile.txt "+hostIP);
		if(tftp.contains("timeout")){
			GatewayJFrame.showMssageln("文件传输超时检查主机地址，路由器地址等设置是否正确");
			return false;
		}else if(tftp.contains("with error command not found")){
			GatewayJFrame.showMssageln("文件传输失败");
			return false;
		}
//		GatewayJFrame.showMssageln(tftp);
		try{
			Thread.sleep(2500);
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
}
