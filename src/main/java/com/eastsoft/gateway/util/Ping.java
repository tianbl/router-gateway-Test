package com.eastsoft.gateway.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.eastsoft.gateway.GatewayJFrame;

public class Ping{

	/**
	 * 执行cmd命令并输出命令执行结果
	 * @param cmd
	 * @return
	 */
	public static boolean executeCmd(String cmd){
    	boolean result = true;
    	Runtime runtime = Runtime.getRuntime();
    	BufferedReader bufferedReader = null;

    	try{
    		//System.out.println(pingCmd);
    		//GatewayJFrame.showMssageln(cmd);
    		Process process = runtime.exec(cmd);
    		if(null==process){
    			return false;
    		}
    		bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
    		int connectedCount = 0;
    		String line = null;
    		while((line = bufferedReader.readLine())!=null){
    			GatewayJFrame.showMssageln(line);
    		}
    		int i=process.waitFor();
    		return 0==i;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}finally{
    		try{

    			bufferedReader.close();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    	}
    }
	/**
	 * 普通的ip连通性测试，如ping 192.168.1.1
	 * @param ipAddress
	 * @param numOfPing
	 * @param timeout
	 * @return
	 */
    public static boolean ping(String ipAddress,int numOfPing,int timeout){
    	boolean result = true;
    	Runtime runtime = Runtime.getRuntime();
    	String pingCmd = "ping "+ipAddress+" -n "+numOfPing+" -w "+timeout;
    	BufferedReader bufferedReader = null;

    	try{
    		//System.out.println(pingCmd);
    		GatewayJFrame.showMssage(pingCmd+"\n");
    		Process process = runtime.exec(pingCmd);
    		if(null==process){
    			return false;
    		}
    		bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
    		int connectedCount = 0;
    		String line = null;
    		while((line = bufferedReader.readLine())!=null){
    			int tem = getResultCheck(line);
    			if(tem==1){
    				connectedCount+=tem;
    				GatewayJFrame.showMssage(line+"\n");
    			}
    		}
    		return connectedCount >= 3;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}finally{
    		try{

    			bufferedReader.close();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    	}
    }

    /**
     * 制定源地址的ping测试
     * @param targetIP
     * @param sourceIP
     * @param numOfPing
     * @param timeout
     * @return
     */
    public static boolean ping(String targetIP,String sourceIP,int numOfPing,int timeout){
    	boolean result = true;
    	Runtime runtime = Runtime.getRuntime();
    	String pingCmd = "ping "+targetIP+" -S "+sourceIP+" -n "+numOfPing+" -w "+timeout;
    	BufferedReader bufferedReader = null;

    	try{
    		//System.out.println(pingCmd);
    		GatewayJFrame.showMssage(pingCmd+"\n");
    		Process process = runtime.exec(pingCmd);
    		if(null==process){
    			return false;
    		}
    		bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
    		int connectedCount = 0;
    		String line = null;
    		while((line = bufferedReader.readLine())!=null){
    			int tem = getResultCheck(line);
    			if(tem==1){
    				connectedCount+=tem;
    				GatewayJFrame.showMssage(connectedCount+line+"\n");
    				//GatewayJFrame.showMssage(line+"\n");
    			}
    		}
    		int i=process.waitFor();
    		if(0!=i){
    			return false;
    		}
    		return connectedCount >= 3;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}finally{
    		try{

    			bufferedReader.close();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    	}
    }

    private static int getResultCheck(String line){
    	Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)",Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(line);
    	while(matcher.find()){
    		return 1;
    	}
    	return 0;
    }

    /**
     * 获得无限连接的接口名字
     * @param cmd
     * @return
     */
    public static String getInterface(String cmd){
    	String result = null;
    	Runtime runtime = Runtime.getRuntime();
    	BufferedReader bufferedReader = null;
    	try{
    		Process process = runtime.exec(cmd);
    		if(null==process){
    			return null;
    		}
    		bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
    		int connectedCount = 0;
    		String line = null;
    		while((line = bufferedReader.readLine())!=null){
    			connectedCount++;
    			//GatewayJFrame.showMssage(line+"\n");
    			if(4==connectedCount){
    				result = line.substring(line.indexOf(":")+1, line.length()).trim();
    			}
    		}
    		int i=process.waitFor();
    		return result;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return null;
    	}finally{
    		try{

    			bufferedReader.close();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    	}
    }

    public static String getWifiStrength(String ssid){
    	Runtime runtime = Runtime.getRuntime();
    	String cmd = "netsh wlan show networks mode=bssid";
    	BufferedReader bufferedReader = null;
    	String strength = null;
    	try{
    		System.out.println(cmd);
    		//GatewayJFrame.showMssage(cmd+"\n");
    		Process process = runtime.exec(cmd);
    		if(null==process){
    			return null;
    		}
    		bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));

    		String line = null;
    		int count = 0;
    		Pattern p = Pattern.compile("\\d+(\\.\\d+)?%");
            Matcher m = null;
    		while((line = bufferedReader.readLine())!=null){
    			if(line.contains(ssid)){
    				count=10;
    			}else if(count>0){
    				count--;
    				m = p.matcher(line);
    				if(m.find()){
    					strength = m.group();
    					System.out.println(strength);
    					return strength;
    				}
    			}
    		}
    		int i=process.waitFor();
    		System.out.println(i);
    		if(0!=i){
    			return null;
    		}
    		return strength;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return null;
    	}finally{
    		try{

    			bufferedReader.close();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    	}
    }

    public static String getWifiIP(String hostIP){
    	try {
			String localname = InetAddress.getLocalHost().getHostName().toString();
			InetAddress Addresses[] = InetAddress.getAllByName(localname);
			for (InetAddress address : Addresses) {
				String tmpIP = address.getHostAddress();
				String regExp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
				Pattern p = Pattern.compile(regExp);
				Matcher m = p.matcher(tmpIP);
				if (m.matches()) {
					if (hostIP.equals(tmpIP)) {
						System.out.println("有线连接IP：" + tmpIP);
					} else if (tmpIP.contains("192.168")) {
						System.out.println("无线连接IP：" + tmpIP);
						return tmpIP;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    public static boolean isSameSegment(String ip1,String ip2){
        boolean isSameNet = false;
        if(!(ip1==null||"".equals(ip1)||ip2==null||"".equals(ip2))){
            int index1 = ip1.indexOf('.',ip1.indexOf('.')+1);
            int index2 = ip2.indexOf('.',ip2.indexOf('.')+1);
			if(index1==-1||index2==-1){
				GatewayJFrame.showMssageln("无法自动判定IP所属网段，请检查IP地址格式是否错误!");
			}else {
				String net1 = ip1.substring(0,index1);
				String net2 = ip2.substring(0,index2);
//			System.out.println("index1=="+index1+" net1=="+net1);
//			System.out.println("index2=="+index2+" net2=="+net2);
				isSameNet = net1.equals(net2);
			}
        }
        return isSameNet;
    }

    public static void main(String[] args){
    	for(int i=0;i<3;i++){
    		if(null!=getWifiIP("192.168.1.2")){
    			System.out.println(getWifiIP("192.168.1.2"));
    			break;
    		}
    	}
    }
}
