package com.eastsoft.gateway.mysqlTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eastsoft.gateway.GatewayJFrame;
import com.eastsoft.gateway.Para;
import com.eastsoft.gateway.ServerSet;

public class MysqlOperation implements ServerInfo{
	private Connection conn = null;
	private String user;
	private String password;
	private String jdbc;
	private String port;
	
	public MysqlOperation(String ip,String usrname,String passwd){
		this.user = usrname;
		this.password = passwd;
		this.port = ServerSet.getInstance().getPort();
		this.jdbc = "jdbc:mysql://"+ip+":"+port+"/enterprise?user="
				+ user+"&password="+password+"&useUnicode=true&characterEncoding=UTF8";
	}
	public boolean open(){
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(jdbc);
			System.out.println("数据库连接成功...");
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public void close(){
		try {
			conn.close();
			System.out.println("连接已关闭！");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getTotalCount(String table){
		String sql = "select COUNT(*) from "+table;
		try {
			open();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int totalCount = 0;
			while(rs.next()){
				totalCount = rs.getInt(1);
			}
			return totalCount;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}finally{
			close();
		}
	}
	
	@Override
	public Map<String,Object> getServerInfo(String column,String value){
	
//		String[] formate={"id","sn","mac_label","gid","pwd","produce","devicekey","mac_3","mac_5","mac_6"};
		String[] formate={"id","sn","MAClabel","gid","pwd","produce","devicekey","mac_3","mac_5","mac_6"};
		StringBuffer sb = new StringBuffer();
		for(String str:formate){
			sb.append(str+",");
		}
		sb.deleteCharAt(sb.length()-1);
//		String sql = "select "+sb.toString()+" from terminal_device where "
//				+ column +"='" + value.replace(":", "") + "'";
		String sql = "select "+sb.toString()+" from mac_snkey where "
				+ column +"='" + value.replace(":", "") + "'";
		return queryMapBySql(formate,sql);
	}
	
	//查询结果，返回list使用数组格式化结果
	public List<Map<String,Object>> queryBySql(String[] format,String sql){
		List<Map<String,Object>> list = new ArrayList();
		try {
			open();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				Map<String,Object> map = new HashMap();
				for(String str:format){
					map.put(str, rs.getString(str));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close();
		}
		return list;
	}
	
	//查询结果，返回list使用数组格式化结果。查询单个结果
	public Map<String,Object> queryMapBySql(String[] format,String sql){
		if(false==open()){
			GatewayJFrame.showMssage("数据库连接失败！\n");
			return null;
		}
		Map<String,Object> map = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				map = new HashMap();
				for(String str:format){
					map.put(str, rs.getString(str));
				}
				//list.add(map);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close();
		}
		return map;
	}
	
	@Override
	public int setUsed(String column,String value,Map<String,String> mac){
//		String sql = "UPDATE terminal_device set mac_label='U'"
//				+ ",mac_3='"+mac.get("wifi")
//				+ "',mac_5='"+mac.get("wan")
//				+ "',mac_6='"+mac.get("lan")
//				+ "' WHERE "
//				+ column+"="+value;
		String sql = "UPDATE mac_snkey set MAClabel='U'"
				+ ",mac_3='"+mac.get("wifi")
				+ "',mac_5='"+mac.get("wan")
				+ "',mac_6='"+mac.get("lan")
				+ "' WHERE "
				+ column+"='"+value+"'";
		System.out.println(sql);
		open();
		try {
			Statement stmt = conn.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}finally{
			close();
		}
		
	}
}
