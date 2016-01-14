package com.eastsoft.gateway.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SQLite
{
	private final static String pwd = "123456";
	
	/**
	 * 打开连接
	 * @param db
	 * @return
	 */
	public static Connection open(String db){
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);
			return conn;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static int setGatewayDB(String db,String username,Map<String,Object> map){
		return resetPwd(db,username, (String) map.get("pwd"))+updatePara(db,map);
	}
	public static int resetPwd(String db, String username,String passwd)
	{
		String sql = "INSERT INTO Account(username,passwd,granted_privilege,alias)"
				+ "VALUES('"+username+"','"+passwd+"','0','')";
		return cexecuteSql(db, sql);
	}
	public static int updatePara(String db,Map<String,Object> map){
		
		StringBuffer sqlSb = new StringBuffer("REPLACE INTO Para(name,value) VALUES");
		String sql="REPLACE INTO Para(name,value) VALUES";
		
		String[] sqls = new String[3];
		
		int gidInt = Integer.parseInt((String) map.get("gid"));
		sqls[0] = sql+"('gid',x'"+Integer.toHexString(gidInt)+"')";
		sqls[1] = sql+"('sn',x'"+stringToHexString(map.get("sn").toString())+"')";
		sqls[2] = sql+"('gatewayKey',x'"+stringToHexString(map.get("devicekey").toString())+"')";
		System.out.println(sqlSb.toString());
		return cexecuteSql(db,sqls);
	}
	
	public static Map getAccountByGid(String db,String username){
		String sql = "SELECT username,passwd FROM Account where username='"+username+"'";
		Map<String,String> map = new HashMap();
		try
		{
			Connection conn =  open(db);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next())
			{
				map.put("gid", rs.getString(1));
				map.put("passwd", rs.getString(2));
				break;
			}
			rs.close();
			stat.close();
			conn.close();
			return map;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map getPara(String db){
		String sql = "SELECT * FROM Para where name in ('gid','sn','gatewayKey');";
		Map<String,String> map = new HashMap();
		try {
			Connection conn = open(db);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			while(rs.next()){
				map.put(rs.getString("name"), rs.getString("value"));
			}
			conn.close();
			stat.close();
			return map;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void deleteAccount(String db, String colname)
	{
		String sql = "DELETE FROM [Account] WHERE username = '" + colname + "';";
		cexecuteSql(db, sql);
	}

	public static String getValue(String db, String colname)
	{
		String sql = "SELECT value FROM [Para] WHERE name ='" + colname + "'";
		return ToolUtil.bytes2HexString(exec_return_bytes(db, sql));
	}


	/**
	 * 写入式sql语句执行
	 * @param db
	 * @param sql
	 */
	private static int cexecuteSql(String db, String sql)
	{
		int numOfModefy = 0;
		try
		{
			Connection conn =  open(db);
			Statement stat = conn.createStatement();
			numOfModefy=stat.executeUpdate(sql);
			stat.close();
			conn.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return numOfModefy;
	}
	
	/**
	 * cexecuteSql的重载版本，执行多条sql语句
	 * @param db
	 * @param sql
	 */
	private static int cexecuteSql(String db, String[] sql)
	{
		int changes = 0;
		try
		{
			Connection conn =  open(db);
			Statement stat = conn.createStatement();
			for(String str:sql){
				changes+=stat.executeUpdate(str);
			}
			stat.close();
			conn.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return changes;
	}
	
	private static String exec_return_string(String db, String sql)
	{
		try
		{
			Connection conn =  open(db);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			String result = null;
			while (rs.next())
			{
				result = rs.getString(1);
				break;
			}
			rs.close();
			stat.close();
			conn.close();
			return result;
		} catch (Exception e)
		{
			e.printStackTrace();
		} 
		return null;
	}
	
	private static byte[] exec_return_bytes(String db, String sql)
	{
		try
		{
			Connection conn =  open(db);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			byte[] data = null;
			while (rs.next())
			{
				data =  rs.getBytes(1);
				break;
			}
			rs.close();
			stat.close();
			conn.close();
			return data;
		} catch (Exception e)
		{
			e.printStackTrace();
		} 
		return null;
	}
	
	public static String stringToHexString(String strPart) {
        String hexString = "";
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString = hexString + strHex;
        }
        return hexString;
    }
}
