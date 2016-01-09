package com.eastsoft.gateway.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgramDataManag {
	
	/**
	 * 获取程序配置数据
	 * @param db
	 * @return
	 */
	public static Map<String,String> getConfigData(String db){
		if(false==ToolUtil.isFileExist(getNowPath()+db)){
			return null;
		}
		String sql = "SELECT * FROM configData";
		String[] format = {"name","value"};
		Map map = queryMap(getNowPath()+db,sql);
		return map;
	}
	
	/**
	 * 更新程序配置数据
	 * @param db
	 * @param map
	 * @return
	 */
	public static int updateConf(String db, Map<String, String> map) {

		if(false==ToolUtil.isFileExist(getNowPath()+db)){
			return 0;
		}
		String sql = "REPLACE INTO configData(name,value) VALUES";
		String[] sqls = new String[map.size()];
		Set<String> keyset = map.keySet();
		
		int i=0;
		for(String str:keyset){
			sqls[i] = sql+"('"+str+"','"+map.get(str)+"')";
			//System.out.println(sqls[i]);
			i++;
		}
		
		return cexecuteSql(getNowPath()+db, sqls);
	}
	
	/**
	 * 清空数据表
	 * @param db
	 * @return
	 */
	public static int deleteConf(String db){
		if(false==ToolUtil.isFileExist(getNowPath()+db)){
			return 0;
		}
		String[] sqls = {"delete  from configData"};
		return cexecuteSql(getNowPath()+db, sqls);
	}
	
	/**
	 * cexecuteSql的重载版本，执行多条sql语句
	 * @param db
	 * @param sql
	 */
	public static int cexecuteSql(String db, String[] sql)
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
	
	public static Map queryMap(String db,String sql){
		Map<String,String> map = null;
		try {
			Connection conn = open(db);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			map = new HashMap();
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
	
	private static String getNowPath() {
		File directory = new File(".");
		try {
			return directory.getCanonicalPath()+"\\";
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		}
	}
}
