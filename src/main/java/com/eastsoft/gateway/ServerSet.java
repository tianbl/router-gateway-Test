package com.eastsoft.gateway;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.eastsoft.gateway.util.Ping;
import com.eastsoft.gateway.util.ProgramDataManag;

public class ServerSet extends JPanel implements ActionListener{
	
	private static ServerSet instance=null;
	private String[] title={"资源服务器 IP","数据库用户名","密码","端口"};
	private String[] defaultValue={"127.0.0.1","root","123456","3303"};
	private JLabel[] info_JLabel;
	private JTextField[] info_JTextField;
	
	private JLabel fileChoserLabel;
	private JButton fileChooser;
	private JButton exchange;
	private JPanel panelServer;
	
	private String fileName;
	private String realPath;
	private boolean isLocalSelected=true;
	
	public static ServerSet getInstance(){
		if(null==instance){
			instance = new ServerSet();
		}
		return instance;
	}
	private ServerSet(){
		super();
		this.setLayout(null);
		panelServer = new JPanel();
		panelServer.setBounds(0, 0, 800, 200);
		panelServer.setLayout(null);
		{
			int y = 0;
			int width = 0;
			info_JLabel = new JLabel[title.length];
			info_JTextField = new JTextField[title.length];
			for(int i=0;i<title.length;i++){
				info_JLabel[i] = new JLabel(title[i]);
				info_JTextField[i] = new JTextField();
				info_JLabel[i].setBounds(20, 40*i+10, 100, 30);
				info_JTextField[i].setBounds(120, 40*i+10, 200, 30);
				info_JTextField[i].setText(defaultValue[i]);
				panelServer.add(info_JLabel[i]);
				panelServer.add(info_JTextField[i]);
			}
			panelServer.setVisible(false);
			this.add(panelServer);
		}
		
		{
			exchange = new JButton("直接使用资源服务器数据库");
			exchange.setBounds(20, 200, 200, 30);
			exchange.addActionListener(this);
			this.add(exchange);
		}
		{
			fileChoserLabel = new JLabel("请选择存放数据的数据execl文件");
			fileChoserLabel.setBounds(20, 50, 200, 30);
			this.add(fileChoserLabel);
			
			fileChooser = new JButton("...");
			fileChooser.setBounds(200, 50, 200, 30);
			fileChooser.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					JFileChooser jfc=new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
					jfc.showDialog(new JLabel(), "选择");
					File file=jfc.getSelectedFile();
					if(file.isDirectory()){
						GatewayJFrame.showMssageln("请选择一个文件...");
						fileName=null;
						realPath=null;
						fileChooser.setText("....");
					}else if(file.isFile()){
						GatewayJFrame.showMssageln("文件:"+file.getAbsolutePath());
						fileChooser.setText(jfc.getSelectedFile().getName());
					}
					fileName = jfc.getSelectedFile().getName();
					if(fileName.endsWith(".xlsx")){
						GatewayJFrame.showMssageln("选择的文件版本不对，需要03版本execl...");
						return ;
					}else if(false==fileName.endsWith(".xls")){
						GatewayJFrame.showMssageln("选择的文件格式不对...");
						return ;
					}
					realPath = file.getAbsolutePath();
				}
			});
			fileChooser.setVisible(true);
			this.add(fileChooser);
		}
		
		//初始化信息
		setVersion();
	}
	
	public String getServerIP(){
		return info_JTextField[0].getText();
	}
	
	public String getUSername(){
		return info_JTextField[1].getText();
	}
	
	public String getPasswd(){
		return info_JTextField[2].getText();
	}
	
	public String getPort(){
		return info_JTextField[3].getText();
	}
	
	public String getRealPath(){
		return realPath;
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public boolean isLocalSelected(){
		return isLocalSelected;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if("使用本地execl文件测试".equals(arg0.getActionCommand())){
			exchange.setText("直接使用资源服务器数据库");
			panelServer.setVisible(false);
			fileChooser.setVisible(true);
			isLocalSelected = true;
		}else if("直接使用资源服务器数据库".equals(arg0.getActionCommand())){
			fileName=null;
			realPath=null;
			fileChooser.setText("....");
			exchange.setText("使用本地execl文件测试");
			fileChooser.setVisible(false);
			panelServer.setVisible(true);
			isLocalSelected = false;
		}
	}
	
	private boolean setVersion(){
		Map<String,String> map = ProgramDataManag.getConfigData("routeTestData.ini");
		if(null==map){
			return false;
		}
		String[] key = {"sourceIP","username","password","port"};
		for(int i=0;i<info_JTextField.length;i++){
			info_JTextField[i].setText(map.get(key[i]));
		}
		
		return true;
	}
	public boolean saveVersion(){
		Map<String,String> map = new HashMap();

		String[] key = {"sourceIP","username","password","port"};
		for(int i=0;i<info_JTextField.length;i++){
			map.put(key[i], info_JTextField[i].getText());
		}
		ProgramDataManag.updateConf("routeTestData.ini", map);
		return true;
	}
}
