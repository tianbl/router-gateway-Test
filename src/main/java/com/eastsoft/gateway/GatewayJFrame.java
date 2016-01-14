package com.eastsoft.gateway;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import com.eastsoft.gateway.util.Connect;
import com.eastsoft.gateway.util.ProgramDataManag;
import com.eastsoft.gateway.util.ToolUtil;
import com.eastsoft.scanningGun.barcode.BarcodeProducter;

//主界用于组织各个功能模块，各模块分开编写
public class GatewayJFrame extends JFrame{
	
	private static GatewayJFrame instance;
	private static JTextArea jTextArea_View;
	private static String stdoutLogPath;
	
	private JPanel jpanel_View;	//信息显示panel
	private JScrollPane jScrollPane_View;	//添加滚动条
	
	private JTabbedPane jtab;	//个功能选项卡
	private GatewayTest gateTest;
	private GatewayUpdate gateUpdate;
	private ServerSet serverSet;
	
	public static GatewayJFrame getInstance(){
		if(instance==null){
			instance = new GatewayJFrame();
		}
		return instance;
	}
	
	private GatewayJFrame(){
		super();
		setResizable(false);
		setBackground(Color.WHITE);
		this.setTitle("智能路由器网关测试工具");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		stdoutLogPath = getNowPath()+"/stdout.log";
		initGUI();
		System.out.println("GateTestJFrame signal instance...");
	}
	
	private void initGUI(){
		this.setIconImage(new ImageIcon(getClass().getClassLoader()
				.getResource("logo.png")).getImage());
		{	//信息输出区域设置
			jpanel_View = new JPanel();
			jpanel_View.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"),
					"", TitledBorder.LEFT,
					TitledBorder.TOP, null, new Color(0, 0, 0)));
			jTextArea_View = new JTextArea();
			jTextArea_View.setEditable(false);
			jScrollPane_View = new JScrollPane();
			jScrollPane_View.setViewportView(jTextArea_View);
			jpanel_View.setLayout(new GridLayout(1,1));
			jpanel_View.add(jScrollPane_View);
		}
		
		{	//标签页设置
			jtab = new JTabbedPane(JTabbedPane.TOP);
			
			//测试部分
			gateTest  = new GatewayTest();
			jtab.add(gateTest, "  1.智能路由器网关测试    ");
			
			//升级部分
			gateUpdate = new GatewayUpdate();
			jtab.add(gateUpdate, "  2.智能路由器网关固件更新   ");
			
			serverSet = ServerSet.getInstance();
			jtab.add(serverSet, " 3.资源服务器地址设置  ");
			
			//jtab.setEnabledAt(0, false);
		}
		
		
		this.setSize(800, 700);
		this.setLayout(new GridLayout(2, 1));
		this.add(jpanel_View);
		this.add(jtab);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//System.out.println("触发windowClosing事件");
				ProgramDataManag.deleteConf("routeTestData.ini");
				GatewayGeneralSet.getInstance().saveVersion();
				serverSet.saveVersion();
				gateUpdate.saveVersion();

				GatewayGeneralSet.getInstance().stopProduct();
			}

			public void windowClosed(WindowEvent e) {
				//System.out.println("触发windowClosed事件");
			}
		});
		
	}
	
	public Connect telnetGateway(String gatewayIP,int port)
	{
		showMssage("正在连接网关，请稍后...\n");
		Connect telnet;
		telnet = new Connect(gatewayIP, 23);
		if (telnet.getIn() == null)
		{
			showMssage("网关连接失败,请确认是否连接正确！\n");
			return null;
		}
		showMssage("网关连接成功！\n");
		return telnet;
	}
	
	public static void showMssage(String str)
	{
		ToolUtil.appendMethod(stdoutLogPath, str);
		jTextArea_View.append(str);
		jTextArea_View.setSelectionStart(jTextArea_View.getText().length());
	}
	public static void showMssageln(String str){
		showMssage(str + "\n");
	}
	public static void clearShow(){
		jTextArea_View.setText("");
	}
	
	public GatewayTest getGatewayTest(){
		return gateTest;
	}

	public static String getNowPath() {
		File directory = new File(".");
		try {
			return directory.getCanonicalPath();
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		}
	}
}
