package com.eastsoft.gateway;

import java.awt.CheckboxGroup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.eastsoft.gateway.util.Ping;
import com.eastsoft.gateway.util.ProgramDataManag;
import com.eastsoft.scanningGun.barcode.BarcodeBuffer;
import com.eastsoft.scanningGun.barcode.BarcodeProducter;

public class GatewayGeneralSet extends JPanel{
	
	private static GatewayGeneralSet instance;
	
	private GatewayJFrame gatewayJFrame;
	private JLabel gatewayIP_JLabel;
	private JTextField gatewayIP_JTextField;
	
	private JLabel accompanyIP_JLabel;
	private JTextField accompanyIP_JText;
	
	private JLabel localIP_JLabel;
	private JTextField localIP_JTextField;
	
	private JLabel defaultgw_JLabel;
	private JTextField defaultgw_JTextField;
	private JLabel qrcode_JLabel;
	private JTextField qrcode_JTextField;
	
	private JButton linkTest_JButton;
	//private String linkButton_title="";
	private JButton showClear_JButton;
	private JButton refresh_JButton;
	
	
	private JLabel plcTestAddr_JLabel;
	private JTextField plcTestAddr_JText;
	private JButton changeToHex;
	private JLabel numOfPing_JLabel;
	private JTextField numOfPing_JTextField;
	
	//private JTextChange jtextChange;
	private ButtonActionListener buttonActionListener;
	
	//二维码扫描枪数据 获取，二维码信息生产者的启动和管理
	private  BarcodeProducter barcodeProducter;

	private String queueInfo;
	
	public static GatewayGeneralSet getInstance(){
		if(null==instance){
			instance = new GatewayGeneralSet();
		}
		return instance;
	}
	
	private GatewayGeneralSet(){
		super();
		this.setLayout(null);
//		/jtextChange = new JTextChange();
		buttonActionListener = new ButtonActionListener();
		
		{
			int y = 20;
			linkTest_JButton = new JButton("测试路由器网关连接");
			linkTest_JButton.setBounds(530, y, 150, 30);
			linkTest_JButton.addActionListener(buttonActionListener);
			this.add(linkTest_JButton);
			
			showClear_JButton = new JButton("清空信息台");
			showClear_JButton.setBounds(680, y, 100, 30);
			showClear_JButton.addActionListener(buttonActionListener);
			this.add(showClear_JButton);
		
			qrcode_JLabel = new JLabel("二维码标签信息");
			qrcode_JTextField = new JTextField();
			qrcode_JLabel.setBounds(10, y, 100, 30);
			qrcode_JTextField.setBounds(100, y, 400, 30);
//			qrcode_JTextField.getDocument().addDocumentListener(jtextChange);
			//qrcode_JTextField.setText(Para.qrcode);
			this.add(qrcode_JLabel);
			this.add(qrcode_JTextField);
		}
		
		{
			int y = 140;
			gatewayIP_JLabel = new JLabel("设备IP");
			gatewayIP_JLabel.setBounds(10,y,50,30);
			gatewayIP_JTextField = new JTextField();
			gatewayIP_JTextField.setText(Para.Gateway_IP);
			gatewayIP_JTextField.setBounds(60, y, 100, 30);
			this.add(gatewayIP_JLabel);
			this.add(gatewayIP_JTextField);
			
			//配测设备IP
			accompanyIP_JLabel = new JLabel("陪测设备IP");
			accompanyIP_JLabel.setBounds(170, y, 60, 30);
			accompanyIP_JText = new JTextField(Para.accompany_IP);
			accompanyIP_JText.setBounds(240, y, 100, 30);
			this.add(accompanyIP_JLabel);
			this.add(accompanyIP_JText);
			
			//本机ip
			localIP_JLabel = new JLabel("本机IP");
			localIP_JTextField = new JTextField();
			try{
				localIP_JTextField.setText(InetAddress.getLocalHost().getHostAddress());
			}catch (UnknownHostException e){
				GatewayJFrame.showMssage("获取本机IP地址失败！\n");
			}
			localIP_JLabel.setBounds(350,y,50,30);
			localIP_JTextField.setBounds(400, y, 100, 30);
			this.add(localIP_JLabel);
			this.add(localIP_JTextField);
		
			/*defaultgw_JLabel defaultgw_JTextField*/
			defaultgw_JLabel = new JLabel("公司网关地址");
			defaultgw_JTextField = new JTextField();
			defaultgw_JLabel.setBounds(530, y, 100, 30);
			defaultgw_JTextField.setBounds(610, y, 100, 30);
			defaultgw_JTextField.setText("129.1.88.1");
			this.add(defaultgw_JLabel);
			this.add(defaultgw_JTextField);
			
			refresh_JButton = new JButton("刷新");
			refresh_JButton.setBounds(720, y, 60, 30);
			refresh_JButton.addActionListener(buttonActionListener);
			this.add(refresh_JButton);
		}
		{
			numOfPing_JLabel = new JLabel("ping连通次数");
			numOfPing_JTextField = new JTextField();
			numOfPing_JLabel.setBounds(10, 60, 100, 30);
			numOfPing_JTextField.setBounds(100, 60, 100, 30);
			numOfPing_JTextField.setText("5");
			JLabel jlabel = new JLabel("ping通3次才算成功，不填默认为0次");
			jlabel.setBounds(200, 60, 200, 30);
			this.add(jlabel);
			this.add(numOfPing_JLabel);
			this.add(numOfPing_JTextField);
		}
		{
			CheckboxGroup cg = new CheckboxGroup();
			plcTestAddr_JLabel = new JLabel("串口测试地址：");
			plcTestAddr_JText = new JTextField();
			changeToHex= new JButton("转换成16进制");
			plcTestAddr_JLabel.setBounds(10, 100, 100, 30);
			plcTestAddr_JText.setBounds(100, 100, 100, 30);
			changeToHex.setBounds(200, 100, 180, 30);
			this.add(changeToHex);
			this.add(plcTestAddr_JLabel);
			this.add(plcTestAddr_JText);
			changeToHex.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					toHex();
				}
			});
		}
		
		//初始化输入值
		setVersion();
		//自动化测试流程
		beginAutoTest();
	}

	public void beginAutoTest(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//启用生产者
				barcodeProducter = new BarcodeProducter();
				barcodeProducter.startProduct();

				while(true){
					/*if(null==gatewayJFrame){
						gatewayJFrame = GatewayJFrame.getInstance();	//获取主框架单例
					}*/
					try {
						queueInfo = BarcodeBuffer.consume();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								GatewayJFrame.showMssageln("扫码信息缓冲队列中获取二维码信息："+queueInfo+"");
								qrcode_JTextField.setText("");
								Thread.sleep(300);
								qrcode_JTextField.setText(queueInfo);
								Map map = getQrCode_Info();
								GatewayJFrame.showMssage("解析得到标签信息如下\n"+
										"sn:"+map.get("sn")+" gid:"+map.get("gid")+" pwd:"+map.get("pwd")+"\n");
								GatewayJFrame.getInstance().getGatewayTest().allTest();
								//Thread.sleep(2000);
								qrcode_JTextField.setText("");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		}).start();
	}
	class ButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg) {
			// TODO Auto-generated method stub
			String name = arg.getActionCommand();
			if("测试路由器网关连接".equals(name)){
				new Thread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						GatewayJFrame.showMssage("测试路由器网关连接被点击\n");
						if(null==gatewayJFrame){
							gatewayJFrame = GatewayJFrame.getInstance();	//获取主框架单例
						}
						if(null!=gatewayJFrame.telnetGateway(gatewayIP_JTextField.getText(), 23)){
							GatewayJFrame.showMssage("路由器连接成功\n");
						}
					}
				}).start();
			}else if("清空信息台".equals(name)){
				GatewayJFrame.clearShow();
			}else if("刷新".equals(arg.getActionCommand())){
				GatewayJFrame.showMssage("刷新,重新获取本机IP...\n");
				try{
					localIP_JTextField.setText(InetAddress.getLocalHost().getHostAddress());
				}catch (UnknownHostException e){
					GatewayJFrame.showMssage("获取本机IP地址失败！\n");
				}
				String gip = gatewayIP_JTextField.getText();
				String loip = localIP_JTextField.getText();

				if (!Ping.isSameSegment(gip,loip)) {
					GatewayJFrame.showMssageln("本机和网关不再同一网段");
				}
			}
		}
		
	}

	public void stopProduct(){
		barcodeProducter.stopProduct();
	}
	public String getPlcTestAddr() {
		return plcTestAddr_JText.getText();
	}

	public int getNumOfPing() {
		String pingNum = numOfPing_JTextField.getText();
		boolean isempty = (null==pingNum||"".equals(pingNum));
		return isempty==true?0:Integer.parseInt(pingNum);
	}
	public String getGateway_IP(){
		return gatewayIP_JTextField.getText();
	}
	public String getLocal_IP(){
		return localIP_JTextField.getText();
	}
	public String getAccompany_IP(){
		return accompanyIP_JText.getText();
	}
	public String getDefaultgw_IP(){
		return defaultgw_JTextField.getText();
	}
	
	public Map<String,String> getQrCode_Info(){
		String qrcodeinfo = qrcode_JTextField.getText();
		if(null==qrcodeinfo||"".equals(qrcodeinfo)){
			GatewayJFrame.showMssageln("没有输入网关条码信息...");
			return null;
		}
		
		if(false==(qrcodeinfo.contains("SN")&&qrcodeinfo.contains("USER")&&qrcodeinfo.contains("PWD"))){
			return null;
		}
		
		Map<String,String> map = new HashMap();
		if(checkCodeInfo(qrcodeinfo,false)==1){
			Pattern pattern = Pattern.compile(Para.regex1);
			Matcher matcher = pattern.matcher(qrcodeinfo);
			matcher.find();
			map.put("sn",matcher.group(2));
			map.put("gid", matcher.group(4));
			map.put("pwd", matcher.group(6));
		}else if(checkCodeInfo(qrcodeinfo,false)==2){
			Pattern pattern = Pattern.compile(Para.regex2);
			Matcher matcher = pattern.matcher(qrcodeinfo);
			matcher.find();
			map.put("sn",matcher.group(2));
			map.put("gid", matcher.group(4));
			map.put("pwd", matcher.group(6));
		}

		return map;
	}
	
	private boolean setVersion(){
		Map<String,String> map = ProgramDataManag.getConfigData("routeTestData.ini");
		if(null==map){
			return false;
		}
		
		numOfPing_JTextField.setText(map.get("pingNum"));
		plcTestAddr_JText.setText(map.get("serialAddr"));
		gatewayIP_JTextField.setText(map.get("routeGateIP"));
		accompanyIP_JText.setText(map.get("accompanyIP"));
		//localIP_JTextField.setText(map.get("hostIP"));
		defaultgw_JTextField.setText(map.get("gatewayIP"));
		return true;
	}
	public boolean saveVersion(){
		Map<String,String> map = new HashMap();
		
		map.put("pingNum", numOfPing_JTextField.getText());
		map.put("serialAddr", plcTestAddr_JText.getText());
		map.put("routeGateIP", gatewayIP_JTextField.getText());
		map.put("accompanyIP",  accompanyIP_JText.getText());
		//map.put("hostIP", localIP_JTextField.getText());
		map.put("gatewayIP", defaultgw_JTextField.getText());
		
		ProgramDataManag.updateConf("routeTestData.ini", map);
		return true;
	}
	
	
	private void toHex(){
		String aid = plcTestAddr_JText.getText();
		long aidLong = 0;
		String[] completions = {"","0","00","000","0000","00000","000000","0000000"};
		try{
			aidLong = Long.parseLong(aid);
			GatewayJFrame.showMssageln("将"+aid+"转换成16进制");
			String addr = Long.toHexString(aidLong);
			if(addr.length()<=8){
				plcTestAddr_JText.setText(completions[8-addr.length()]+addr);
			}else{
				GatewayJFrame.showMssageln("转换后的十六进制串口地址输入长度超过允许的8位，请重新检查输入...");
				JOptionPane.showConfirmDialog(this, "地址转换发生错误，长度超过允许？", "提示",
						JOptionPane.YES_NO_CANCEL_OPTION);
			}
		}catch(Exception e){
			GatewayJFrame.showMssageln("地址格式错误，或者已经转换成16进制，无需再次转换");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param str
	 * @param isProducer true表示是扫描枪线程,否则表示其他
	 * @return
	 */
	public int checkCodeInfo(String str,boolean isProducer){
		
//		String regex1 = "SN\\w{1,24}USER\\d{1,10}PWD\\d{6}$";
		///扫码器扫描标签后得到的信息中总是会带有'J'所以正则表达式的规则中包含了'J'
		
		Pattern pattern1 = Pattern.compile(Para.regex1);
		Pattern pattern2 = Pattern.compile(Para.regex2);
		Matcher matcher1 = pattern1.matcher(str);
		Matcher matcher2 = pattern2.matcher(str);
		if(matcher1.find()){
			if(isProducer){
				if(matcher1.start(0)>0){//如果不是完全匹配，则去掉干扰字符
					BarcodeBuffer.product(matcher1.group(0));
					GatewayJFrame.showMssageln("由表达式regex1匹配，缓冲区存在干扰字符，已将正确条码信息取出，测试期间请减少输入操作");
				}else{
					BarcodeBuffer.product(str);
					GatewayJFrame.showMssageln("正则表达式1完全匹配");
				}
			}
			return 1;
		}else if(matcher2.find()){
			if(isProducer){
				if(matcher2.start(0)>0){//如果不是完全匹配，则去掉干扰字符
					BarcodeBuffer.product(matcher2.group(0));
					GatewayJFrame.showMssageln("由表达式regex2匹配，缓冲区存在干扰字符，已将正确条码信息取出，测试期间请减少减少输入操作");
				}else{
					BarcodeBuffer.product(str);
					GatewayJFrame.showMssageln("正则表达式2完全匹配");
				}
			}
			return 2;
		}else{
			return 0;
		}
	}
}
