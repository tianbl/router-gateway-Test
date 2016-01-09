package com.eastsoft.gateway.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;

import org.apache.commons.net.telnet.TelnetClient;

import com.eastsoft.gateway.GatewayJFrame;

public class Connect {
	private TelnetClient telnet = new TelnetClient();
	private InputStream in;

	public TelnetClient getTelnet() {
		return telnet;
	}

	public void setTelnet(TelnetClient telnet) {
		this.telnet = telnet;
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public PrintStream getOut() {
		return out;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	public char getPrompt() {
		return prompt;
	}

	public void setPrompt(char prompt) {
		this.prompt = prompt;
	}

	private PrintStream out;
	private char prompt = '#'; // 普通用户结束

	public Connect(String ip, int port) {
		try {
			String localIP;
			localIP = InetAddress.getLocalHost().getHostAddress();
			boolean subIP1 = ip.startsWith("192.168");
			boolean subLocalIP1 = localIP.startsWith("192.168");
			boolean subIP2 = ip.startsWith("129.1");
			boolean subLocalIP2 = localIP.startsWith("129.1");
			if ((subIP1 && subLocalIP1) || (subIP2 && subLocalIP2)) {
				telnet.connect(ip, port);
				System.out.println("创建连接成功！");
				in = telnet.getInputStream();
				out = new PrintStream(telnet.getOutputStream());
				this.prompt = '#';
				String str = readUntil("login:");
				if (str != null) {
					if (str.contains("login:")) {
						tryLogin("root");
					} else if ("c++".equals(str)) {
						GatewayJFrame.showMssage("");
					} else {
						GatewayJFrame.showMssage("非java或c++网关!\n");
					}
				}
			} else {
				GatewayJFrame.showMssage("网关和本机不在一个网段，请检查并修改本机地址！\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			GatewayJFrame.showMssage("网关无法登录！\n");
			e.printStackTrace();
		}
	}

	/**
	 * 登录，如果有密码为ihome_309
	 * 
	 * @param user
	 */
	public void login(String user) {
		readUntil("login:");
		write(user);
		StringBuffer sb = new StringBuffer();
		char ch = ' ';
		for (int i = 0; i < 16; i++) {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sb.append(ch);
		}

		String str = sb.toString();
		System.out.println(str);
		if (str.contains("Pass")) {
			write("ihome_309");
		}
		readUntil(prompt + " ");
	}

	public void tryLogin(String user) {
		write(user);
		StringBuffer sb = new StringBuffer();
		char ch = ' ';
		for (int i = 0; i < 16; i++) {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sb.append(ch);
		}

		String str = sb.toString();
		//System.out.println(str);
		if (str.contains("Pass")) {
			write("eastsoft");
		}
		System.out.println(readUntil(prompt + " "));
	}

	/**
	 * 读取分析结果
	 * 
	 * @param pattern
	 * @return
	 */
	public String readUntil(String pattern) {
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();
			int count = 0;
			while (true) {
				sb.append(ch);
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						// ============
						// sub = sb.toString().replaceAll(
						// "(\\[1;34m)|(\\[0m)|(\\[0;0m)|(\\[1;36m)",
						// "");
						// ============
						// 传输过来的字符串为ISO8859-1，需要转换为GBK格式的
						byte[] temp = sb.toString().getBytes("ISO8859-1");
						return new String(temp, "GBK");
						// return sb.toString();
					}
				}
				ch = (char) in.read();
				count++;
				if (count > 500 && sb.toString().contains("root@Eastsoft:/#")) {
					return "c++";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**  * 读取分析结果 * * @param pattern * @return  */
	public String readUntilDoNothing(String pattern){
		try{
			char lastChar = pattern.charAt(pattern.length()-1);
			StringBuffer sb = new StringBuffer();
			char ch = (char)in.read();
			while(true){
				sb.append(ch);
				if(ch==lastChar){
					if(sb.toString().endsWith(pattern)){
						byte[] temp = sb.toString().getBytes("ISO8859-1");
						return new String(temp,"GBK");
					}
				}
				ch = (char)in.read();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取分析结果，逐行打印
	 * 
	 * @param pattern
	 * @return
	 */
	public String readUntilLiner(String pattern) {
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();
			// String sub = "";
			int start = 0;
			int end = 0;
			int count = 0;
			while (true) {
				sb.append(ch);
				if (Character.toString(ch).equals("\n")) {
					end = sb.length();
					byte[] temp = sb.substring(start, end).getBytes("ISO8859-1");
					String str = new String(temp, "GBK");
					GatewayJFrame.showMssage(str);
					// 清空
					// sb.delete(0, sb.length()-1);
					start = end;
				}
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						byte[] temp1 = sb.substring(end, sb.length() - 1).getBytes("ISO8859-1");
						String str = new String(temp1, "GBK");
						GatewayJFrame.showMssage(str +"count="+count+ "\n");
						// 传输过来的字符串为ISO8859-1，需要转换为GBK格式的
						byte[] temp = sb.toString().getBytes("ISO8859-1");
						return new String(temp, "GBK");
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void write(String value) {
		try {
			out.println(value);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用readUntilLiner读取命令执行结果
	 * @param command
	 * @return
	 */
	public String sendCommandLiner(String command) {
		try {
			write(command);
			return readUntilLiner(prompt + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public String sendCommand(String command) {
		try {
			write(command);
			return readUntilDoNothing(prompt + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void disconnect() {
		try {
			telnet.disconnect();
			GatewayJFrame.showMssage("连接关闭...\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
