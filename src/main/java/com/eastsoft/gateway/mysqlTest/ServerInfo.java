package com.eastsoft.gateway.mysqlTest;

import java.util.Map;

public interface ServerInfo {
	/**
	 * 获取服务器信息，通用接口，如果链接远程则从服务器直接查询获取，否则从本地execl文件读取
	 * @param column 查询使用的列名
	 * @param value 查询使用的列名值
	 * @return
	 */
	public Map<String,Object> getServerInfo(String column, String value);
	
	/**
	 * 设置已使用信息，包括设置mac地址
	 * @param column 设置时依据的列名
	 * @param value 所依据列名的值
	 * @param mac
	 * @return
	 */
	public int setUsed(String column, String value, Map<String, String> mac);
}
