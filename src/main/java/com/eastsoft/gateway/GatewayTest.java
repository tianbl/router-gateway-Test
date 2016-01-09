package com.eastsoft.gateway;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.eastsoft.gateway.mysqlTest.MysqlOperation;
import com.eastsoft.gateway.mysqlTest.ServerInfo;
import com.eastsoft.gateway.mysqlTest.XlsOperation;
import com.eastsoft.gateway.util.Connect;
import com.eastsoft.gateway.util.Database;
import com.eastsoft.gateway.util.Ping;
import com.eastsoft.gateway.util.SQLite;
import com.eastsoft.gateway.util.ToolUtil;
import com.eastsoft.gateway.util.XmlManager;

//网关测试部分
public class GatewayTest extends JPanel {

	private int index_Of_Clicked_Button;
	private Map<String, String> macMap = null;
	private GatewayJFrame gatewayJFrame;
	private GatewayGeneralSet gatewayGeneralSet;
	private ServerSet serverSet;

	private JButton allTest_JButton;
	// 分项测试按钮组
	private JButton[] signalTest_JButton;
	private String[] buttonTitle = { "1.测试WAN和LAN口", "2.信息设置", "3.测试wifi", "4.测试 串口", "5.测试USB" };

	private ButtonActionListener buttonActionListener;

	private ServerInfo mysqlOperation = null; // 获取资源信息

	public GatewayTest() {
		super();
		buttonActionListener = new ButtonActionListener();
		this.setLayout(null);
		{
			gatewayGeneralSet = GatewayGeneralSet.getInstance();
			gatewayGeneralSet.setBounds(0, 0, 800, 180);
			gatewayGeneralSet.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "测试信息设置",
					TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
			this.add(gatewayGeneralSet);
			serverSet = ServerSet.getInstance();
		}
		{ // 单项测试按钮组
			allTest_JButton = new JButton("一键测试");
			allTest_JButton.setBounds(10, 200, 150, 30);
			allTest_JButton.setBackground(Color.GREEN);
			allTest_JButton.addActionListener(buttonActionListener);
			this.add(allTest_JButton);

			signalTest_JButton = new JButton[buttonTitle.length];

			int y = 240;
			for (int i = 0; i < buttonTitle.length; i++) {
				signalTest_JButton[i] = new JButton(buttonTitle[i]);
				int buttonLength = 130;
				signalTest_JButton[i].setBounds(10 + (10 + buttonLength) * i, y, buttonLength, 30);
				signalTest_JButton[i].addActionListener(buttonActionListener);
				this.add(signalTest_JButton[i]);
				/*
				 * if((i+1)%4==0){ y+=40; }
				 */
			}
		}

		GatewayJFrame.showMssageln("程序当运行路径：" + getNowPath());
		String gip = gatewayGeneralSet.getGateway_IP();
		String loip = gatewayGeneralSet.getLocal_IP();
		if(gip.length()>7&&loip.length()>7){
			gip = gip.substring(0, 7);
			loip = loip.substring(0, 7);
		}
		if (!gip.equals(loip)) {
			GatewayJFrame.showMssageln("本机和网关不再同一网段");
		}
	}

	class ButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg) {
			// TODO Auto-generated method stub

			String buttonName = arg.getActionCommand();
			final String gateway_IP = gatewayGeneralSet.getGateway_IP();
			final String host_IP = gatewayGeneralSet.getLocal_IP();

			if ("一键测试".equals(buttonName)) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						allTest();
					}
				}).start();
			} else {
				for (int i = 0; i < buttonTitle.length; i++) {
					if (buttonTitle[i].equals(buttonName)) {
						final int lastIndex = buttonTitle.length - 1;
						switch (i) {
						case 0: {// 测试lan口和wan口
							new Thread(new Runnable() {
								@Override
								public void run() { // 启动线程执行后续操作
									wan_Lan_Test();
								}
							}).start();
							break;
						}
						case 1: {
							new Thread(new Runnable() {
								@Override
								public void run() { // 启动线程执行后续操作
									setGatewayInfo(gateway_IP, host_IP);
								}
							}).start();
							break;
						}
						case 2: {
							new Thread(new Runnable() {
								@Override
								public void run() {
									wifi_Test(gateway_IP, host_IP);
								}
							}).start();
							break;
						}
						case 3: {
							new Thread(new Runnable() {
								@Override
								public void run() {
									//GatewayJFrame.showMssageln(buttonTitle[2]);
									serialport_Test(gateway_IP, buttonTitle[3]);
								}
							}).start();
							break;
						}
						case 4: {
							new Thread(new Runnable() {
								@Override
								public void run() {
									UsbTest(gateway_IP, host_IP);
								}
							}).start();
							;
							break;
						}

						}
						break;
					}
				}
			}
		}
	}

	public boolean allTest() {
		String gateway_ip = gatewayGeneralSet.getGateway_IP();
		String localhost_ip = gatewayGeneralSet.getLocal_IP();
		String accompany_ip = gatewayGeneralSet.getAccompany_IP();

		if (gatewayGeneralSet.getPlcTestAddr().trim().equals("")) {
			GatewayJFrame.showMssageln("请先输入串口测试使用的载波设备地址");
			return false;
		}
		if (false == wan_Lan_Test()) {
			int i = JOptionPane.showConfirmDialog(this, "WAN口和LAN口测试发生错误，是否继续其他测试？", "提示",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (0 != i) {
				return false;
			}
		}
		if (false == setGatewayInfo(gateway_ip, localhost_ip)) {
			int i = JOptionPane.showConfirmDialog(this, "信息设置发生错误，是否继续其他测试？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
			if (0 != i) {
				return false;
			}
		}
		if (false == wifi_Test(gateway_ip, localhost_ip)) {
			int i = JOptionPane.showConfirmDialog(this, "wifi测试发生错误，是否继续其他测试？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
			if (0 != i) {
				return false;
			}
		}
		if (false == serialport_Test(gateway_ip, localhost_ip)) {
			int i = JOptionPane.showConfirmDialog(this, "串口测试发生错误，是否继续其他测试？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
			if (0 != i) {
				return false;
			}
		}
		if (false == UsbTest(gateway_ip, localhost_ip)) {
			int i = JOptionPane.showConfirmDialog(this, "USB测试发生错误，是否继续其他测试？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
			if (0 != i) {
				return false;
			}
		}
		return true;
	}

	private boolean wan_Lan_Test() {
		GatewayJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>1.WAN口和LAN口测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");

		int numOfping = gatewayGeneralSet.getNumOfPing();
		String gateway_IP = gatewayGeneralSet.getGateway_IP();
		String[] targetIP = { gateway_IP, gatewayGeneralSet.getAccompany_IP(), gatewayGeneralSet.getDefaultgw_IP() };
		String[] title = { "路由器网关", "配测IP(lan口连接设备)", "公司网关" };
		for (int i = 0; i < targetIP.length; i++) {
			GatewayJFrame.showMssage(title[i] + "连通性测试    ");
			if (true == Ping.ping(targetIP[i], numOfping, 3000)) {
				GatewayJFrame.showMssageln(title[i] + "ping测试通过！\n");
			} else {
				GatewayJFrame.showMssageln(title[i] + "ping测试不通过\n");
				return false;
			}
		}

		{ // 获取路由器系统时钟，并计算系统时间误差
			GatewayJFrame.showMssage("准备获取路由器系统时钟...\n");
			Connect connect = GatewayJFrame.getInstance().telnetGateway(gateway_IP, 23);
			String routeTime = connect.sendCommand("date +%s").split("\r\n")[1];
			long routeTimeLong = Long.parseLong(routeTime, 10);
			long nowTime = Calendar.getInstance().getTimeInMillis();
			long diff = Math.abs(((nowTime / 1000) - routeTimeLong));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
			GatewayJFrame.showMssage("获取到路由器时间为：" + format.format(new Date(routeTimeLong * 1000)) + "\n");
			if (diff < 300) {
				GatewayJFrame.showMssageln("智能路由器系统时间误差为(精确到秒)：" + diff + ",小于误差要求5分钟（300秒)");
			} else {
				GatewayJFrame.showMssageln("智能路由器系统时间误差为(精确到秒)：" + diff + ",超出误差要求5分钟（300秒)");
				return false;
			}
			connect.disconnect();
		}

		// 全部通过后测试完成，返回true
		GatewayJFrame.showMssageln("WAN口和LAN口测试通过！");
		return true;
	}

	// 设置信息
	private boolean setGatewayInfo(String gateway_IP, String hostIP) {
		GatewayJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>2.设置路由器网关信息<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		Map<String, Object> serverInfo = null;

		// 获取MAC地址
		Connect connect = GatewayJFrame.getInstance().telnetGateway(gateway_IP, 23);
		if (null == connect) {
			// GatewayJFrame.showMssageln("");
			return false;
		}
		if (null == macMap) {
			macMap = getMac(connect);
		}

		// 获取标签信息
		Map<String, String> qrcodeInfo = gatewayGeneralSet.getQrCode_Info();
		if (null == qrcodeInfo) {
			GatewayJFrame.showMssage("获取二维码标签信息失败 ！\n");
			return false;
		}

		{// 查询服务器信息
			if (serverSet.isLocalSelected()) {
				if(serverSet.getRealPath()==null||serverSet.getRealPath().equals("")){
					GatewayJFrame.showMssage("使用本地数据，但没有选中的本地execl文件 ,请在“资源服务器地址设置”中设置好本地文件后重新开始！\n");
					return false;
				}
				GatewayJFrame.showMssage("使用导出到本地的execl文件获取资源信息...\n");
				mysqlOperation = new XlsOperation(serverSet.getRealPath());
			} else {
				GatewayJFrame.showMssage(serverSet.getServerIP() + "\n" + serverSet.getUSername() + "\n");
				mysqlOperation = new MysqlOperation(serverSet.getServerIP(), serverSet.getUSername(),
						serverSet.getPasswd());
			}

			GatewayJFrame.showMssage("从资源服务器获取信息...\n");
			Map<String, Object> map = mysqlOperation.getServerInfo("sn", qrcodeInfo.get("sn")); // 通过gid查询
			if (null == map) {
				GatewayJFrame.showMssageln("查询不到数据！");
				return false;
			} else {
				serverInfo = map;
				if (!serverSet.isLocalSelected()) {
					int gid = Integer.parseInt((String)serverInfo.get("gid"),16);
					serverInfo.put("gid", Integer.toString(gid));
				}
			}
		}

		// 检查信息，标志位为U则表示该条信息已经设置使用过 ，并设置
//		if (null == serverInfo || "U".equals(serverInfo.get("mac_label"))) {
		if (null == serverInfo || "U".equals(serverInfo.get("MAClabel"))) {
			GatewayJFrame.showMssageln("信息已使用，信息设置操作结束！");
			return false;
		}

		// 标签信息和数据库对比
		boolean gid = qrcodeInfo.get("gid").equals(serverInfo.get("gid"));
		boolean pwd = qrcodeInfo.get("pwd").equals(serverInfo.get("pwd"));
		boolean sn = qrcodeInfo.get("sn").equals(serverInfo.get("sn"));
		if (gid && pwd && sn) {
			GatewayJFrame.showMssageln("正在进行路由器网关信息设置...");
		} else {
			GatewayJFrame.showMssageln("资源服务器信息标签信息不一致,测试结束！");
			return false;
		}

		// 设置数据库信息到数据库
		{
			String db = getNowPath() + "/" + Para.gateway;
			try {
				ToolUtil.deleteFileByFilename(db);
				ToolUtil.copyFile(getNowPath() + "/" + Para.pcgateway, db);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				GatewayJFrame.showMssageln(Para.pcgateway + "=====>" + Para.gateway + " 数据库生成失败");
				e.printStackTrace();
				return false;
			}
			if (ToolUtil.isFileExist(db)) {
				int ch = SQLite.setGatewayDB(db, (String) serverInfo.get("gid"), serverInfo);
				if (ch > 0) {
					GatewayJFrame.showMssageln("信息设置完成,共插入和修改" + ch + "条数据，路由器网关下载gateway.db");
				} else {
					GatewayJFrame.showMssageln("数据库信息设置失败！\n");
					return false;
				}
			} else {
				GatewayJFrame.showMssageln(Para.gateway + "文件不存在\n");
				return false;
			}
			// 路由器网关下载设置好的数据库
			if (!Database.dbupdateDB(connect, Para.gateway, hostIP)) {
				GatewayJFrame.showMssageln("结束当前操作...");
				return false;
			}

			if(Database.checkGatewayDb(connect, (String) serverInfo.get("gid"), hostIP, Para.gateway, getNowPath(),
					serverInfo)){
				int setUsed = mysqlOperation.setUsed("sn", (String) serverInfo.get("sn"), macMap);
				if (setUsed <= 0) {
					GatewayJFrame.showMssageln("同步资源服务器信息失败！若是使用本地的execl文件，请检查execl是否在窗口打开，如果打开请关闭！");
					return false;
				}else{
					GatewayJFrame.showMssageln("同步资源服务器信息成功,信息设置完成！");
					return true;
				}
			}else{
				return false;
			}
			
		}
//		GatewayJFrame.showMssageln("信息设置完成！");
//		return true;
	}

	public boolean wifi_Test(String gateway_IP, String hostIp) {
		GatewayJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>3.wifi测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");

		String wifiInterface = Ping.getInterface("netsh wlan show interfaces");
		//String wifiInterface = "无线网络连接";
		if(false=="WLAN".equals(wifiInterface)&&false==wifiInterface.contains("无线网络连接")){
			wifiInterface = "WLAN";
			GatewayJFrame.showMssageln("请到电脑中\"控制面板->网络和 Internet->网络连接\"中将无线连接名字改为WLAN");
			int i = JOptionPane.showConfirmDialog(this, "请按信息输出提示修改无线名称，然后点击“是”继续！", "提示",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (0 != i) {
				return false;
			}
		}
		GatewayJFrame.showMssageln("电脑存在无线接口："+wifiInterface);
		{
			
			String disabled = "netsh interface set interface name=\""+wifiInterface+"\" admin=DISABLED";
			String enabled = "netsh interface set interface name=\""+wifiInterface+"\" admin=ENABLED";
			if (Ping.executeCmd(disabled)) {
				if (Ping.executeCmd(enabled)) {
					GatewayJFrame.showMssageln("wifi重启成功...");
				}
			}else{
				GatewayJFrame.showMssageln("无法重启无线...");
				int i = JOptionPane.showConfirmDialog(this, "无法重启无线网卡，是否手动连接无线后继续？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
				if (0 != i) {
					return false;
				}
			}
		}

		String wifi_connectIP = null;
		if (null == macMap) {
			macMap = getMac(GatewayJFrame.getInstance().telnetGateway(gateway_IP, 23));
		}
		Connect telnet = GatewayJFrame.getInstance().telnetGateway(gateway_IP, 23);
		String getssid = telnet.sendCommand("uci get wireless.@wifi-iface[0].ssid");
		
		String ssid = null;
		if (getssid.split("\r\n").length > 2) {
			ssid = getssid.split("\r\n")[1];
		}else{
			GatewayJFrame.showMssageln("查询无线命令执行结果格式不符合程序要求"+getssid);
		}
		telnet.disconnect();
//		String ssid = "Eastsoft_" + wifiMac.substring(wifiMac.length() - 6);
		//检测wifi，并获取信号强度
		String wifiStrength = Ping.getWifiStrength(ssid);
		if (null != wifiStrength) {
			GatewayJFrame.showMssageln(ssid + "wifi信号强度：" + wifiStrength);
		} else {
			GatewayJFrame.showMssageln("检测不到有关wifi " + ssid + "的信息...");
//			Connect telnet = GatewayJFrame.getInstance().telnetGateway(gateway_IP, 23);
//			String gid = telnet.sendCommand("uci get wireless.@wifi-iface[0].ssid");
//			ssid = "Eastsoft_"+gid.split("\r\n")[1];
////			Map<String,String> map = GatewayGeneralSet.getInstance().getQrCode_Info();
////			ssid = "Eastsoft_"+map.get("gid");
//			wifiStrength = Ping.getWifiStrength(ssid);
//			if (null == wifiStrength) {
//				GatewayJFrame.showMssageln("检测不到有关wifi " + ssid + "的信息...");
//			}else{
//				GatewayJFrame.showMssageln(ssid + "wifi信号强度：" + wifiStrength);
//			}
//			telnet.disconnect();
		}
		String connectWifi = "netsh wlan connect name=\"" + ssid + "\" ssid=\""+ ssid + "\" interface=\""
				+ wifiInterface+"\"";
		String disconnect = "netsh wlan disconnect";

		// 添加配置文件
		new XmlManager().modifyNode("", ssid);
		String addConfig = "netsh wlan add profile filename=" + getNowPath() + "\"\\Eastsoft_wifi.xml\"";
		String deleteConfig = "netsh wlan delete profile name=" + ssid;
		if (Ping.executeCmd(addConfig)) {
			// GatewayJFrame.showMssageln("");
		}
		

		// wifi尝试3次连接，
		GatewayJFrame.showMssageln("连接wifi将进行3次连接尝试...");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < 3; i++) {
			GatewayJFrame.showMssageln("正在进行第" + (i + 1) + "次连接");
			if (false == Ping.executeCmd(connectWifi)) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (i >= 2) {
					String alarmInfo = "请自行连接无线" + ssid + ",然后继续...";
					int select = JOptionPane.showConfirmDialog(this, alarmInfo, "提示", JOptionPane.YES_NO_CANCEL_OPTION);
					if (0 != i) {
						GatewayJFrame.showMssageln("无法连接wifi：" + ssid);
						Ping.executeCmd(deleteConfig);
						return false;
					} else {
						break;
					}
				} else {
					GatewayJFrame.showMssageln("连接失败还将进行" + (3 - i - 1) + "次尝试...");
				}
			} else {
				break;
			}
		}

		// 尝试3次 获取ip，以减少因为获取不到ip导致的虚假测试失败
		for (int i = 0; i < 3; i++) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			wifi_connectIP = Ping.getWifiIP(hostIp);
			if (null == wifi_connectIP) {
				GatewayJFrame.showMssageln("第" + (i + 1) + "次尝试获取wifiIP...");
			} else {
				GatewayJFrame.showMssageln("成功获取到wifi IP:" + wifi_connectIP);
				break;
			}
		}

		// 删除测试wifi配置文件

		if (null == wifi_connectIP) {
			GatewayJFrame.showMssageln("无法获取连接到的wifiIP,请先手动连接路由器网关的wifi再继续测试！");
			Ping.executeCmd(disconnect);
			Ping.executeCmd(deleteConfig);
			return false;
		}

		if (Ping.ping(gateway_IP, wifi_connectIP, gatewayGeneralSet.getNumOfPing(), 3000)) {
			Ping.executeCmd(disconnect);
			GatewayJFrame.showMssageln("wifi测试 成功...");
			Ping.executeCmd(deleteConfig);
		} else {
			Ping.executeCmd(disconnect);
			Ping.executeCmd(deleteConfig);
			GatewayJFrame.showMssageln("wifi测试失败...");
		}
//		Ping.executeCmd(deleteConfig);
		return true;
	}

	public boolean serialport_Test(String gateway_IP, String buttonTitle) {
		GatewayJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>4.串口测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		if (gatewayGeneralSet.getPlcTestAddr().trim().equals("")) {
			GatewayJFrame.showMssageln("请先输入串口测试使用的载波设备地址");
			return false;
		}else{
			GatewayJFrame.showMssageln("串口测试进行3次，任意一次测试结果为\"载波电路测试    合格    !\"表示测试通过...");
		}
		Connect telnet = GatewayJFrame.getInstance().telnetGateway(gateway_IP, 23);
		/*
		 * if (false == Database.downloadFile(telnet, "/tmp", "gateway_test",
		 * Para.localhost_IP)) { return false; }
		 */
		telnet.sendCommand("cd /gateway/cpp/main");
		// telnet.sendCommand("chmod 755 gateway_test");
		String testRes = telnet.sendCommandLiner("./gateway_test p " + gatewayGeneralSet.getPlcTestAddr() + " 32 3");
//		GatewayJFrame.showMssageln(testRes);
		if (testRes.contains("载波电路测试    合格    !")) {
			GatewayJFrame.showMssageln("载波电路测试通过,串口测试完成..."+"\n点亮led，持续3秒后熄灭...");
			telnet.sendCommandLiner("cd /sys/class/leds/*plc/");
			telnet.sendCommandLiner("echo 1 > brightness");
			telnet.sendCommandLiner("sleep 3");
			telnet.sendCommandLiner("echo 0 > brightness");
		} else {
			GatewayJFrame.showMssageln("载波电路测试失败,串口测试失败...");
			telnet.disconnect();
			return false;
		}
		return true;
	}

	public boolean UsbTest(String gateway_IP, String localhost_IP) {
		GatewayJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>5.USB测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		Connect connect = GatewayJFrame.getInstance().telnetGateway(gateway_IP, 23);
		if (false == Database.uploadFileOfUsb(connect, localhost_IP)) {
			return false;
		}

		// 文件内容比较
		String file1 = getNowPath() + "\\smartRouter\\testFile.txt";
		String file2 = getNowPath() + "\\testFile.txt";
		String localFile = ToolUtil.txt2String(file1);
		String uploadFile = ToolUtil.txt2String(file2);
		if (localFile != null && localFile.equals(uploadFile)) {
			GatewayJFrame.showMssageln("本地约定文件：" + localFile + "\n路由器usb测试设备文件内容:" + uploadFile);
			GatewayJFrame.showMssageln("智能路由器网关USB设备文件读取正常，测试通过");
		} else {
			GatewayJFrame.showMssageln("智能路由器网关USB设备文件内容与本地约定文件内容不同，测试不通过");
			return false;
		}
		return true;
	}

	private String getNowPath() {
		File directory = new File(".");
		try {
			return directory.getCanonicalPath();
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		}
	}

	private Map<String, String> getMac(Connect connect) {
		if (null == connect) {
			GatewayJFrame.showMssageln("智能路由器telnet连接失败，无法获取MAC...");
			return null;
		}
		String[] linkShow = connect.sendCommand("link show").split("\r\n");
		Map<String, String> map = new HashMap();
		for (String str : linkShow) {
			if (str.contains("lan") && str.contains("wifi") && str.contains("wan")) {
				String[] mac = str.split(" ");
				for (String tmp : mac) {
					int index = tmp.indexOf(":");
					String key = tmp.substring(0, index);
					String value = tmp.substring(index + 1, tmp.length()).replace(":", "");
					map.put(key, value);
				}
				break;
			}
		}
		// connect.disconnect();
		GatewayJFrame.showMssageln("MAC got");
		return map;
	}

	public boolean setGeneralInfo(){
		//gatewayGeneralSet.set
		return true;
	}
}
