package com.eastsoft.gateway.util;

import com.eastsoft.gateway.GatewayJFrame;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.util.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ToolUtil
{
	//用于记录文件
	public static FileWriter fileWriter;
	public static int count = 0;

	public static boolean isFileExist(String filename)
	{
		File file = new File(filename);
		if (file.exists())
		{
			return true;
		}
		return false;
	}
	
	public static boolean deleteFileByFilename(String filename)
	{
		File file = new File(filename);
		boolean isdeltet = false;
		if (!file.exists())
		{
			return false;
		}
		if(file.isFile()){
			return file.delete();
		}
		return isdeltet;
	}
	
	public static void copyFile(String original, String target) throws Exception
	{
		FileInputStream in = new FileInputStream(new File(original));
		FileOutputStream out = new FileOutputStream(new File(target));
		byte[] buff = new byte[512];
		int n = 0;
		while ((n = in.read(buff)) != -1)
		{
			out.write(buff, 0, n);
		}
		out.flush();
		in.close();
		out.close();
	}
	
	@SuppressWarnings("resource")
	public static String fileToHexString(int gid) throws IOException
	{
		String outPath = "out";
		String filepath = outPath + "/" + gid + ".jks";
		File file = new File(filepath);
		DataInputStream din = new DataInputStream(new FileInputStream(file));
		StringBuilder hexData = new StringBuilder();
		byte temp = 0;
		for(int i = 0; i < file.length(); i++)
		{
			temp = din.readByte();
			hexData.append(String.format("%02X", temp));
		}
		return hexData.toString();
	}
	
	public static String calculateTime(String fileName)
	{
		String dir = System.getProperty("user.dir");
		dir = dir + "\\"+fileName;
		File zip = new File(dir);
		if (zip.exists())
		{
			long l = zip.length();
			long time = (l / 1024 / 400) * 3 / 2 + 5;
			return Long.toString(time);
		} else
		{
			return null;
		}
	}
	
	public static String bytes2HexString(byte[] b)
	{
		if(b == null)
		{
			return null;
		}
		String ret = "";
		for (int i = 0; i < b.length; i++)
		{
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1)
			{
				hex = "0" + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static byte[] hexString2ByteArray(String str)
	{
		str = str.replaceAll(" ", "");
		byte[] result = new byte[str.length() / 2];
		for (int i = 0, j = 0; j < str.length(); i++)
		{
			result[i] = (byte) Integer.parseInt(str.substring(j, j + 2), 16);
			j += 2;
		}
		return result;
	}

	public static byte[] int2Byte(int i)
	{
		byte[] result = new byte[2];
		result[0] = (byte) ((i >> 8) & 0xFF);
		result[1] = (byte) (i & 0xFF);
		return result;
	}

	public static byte makeCs(byte b[])
	{
		int cs = 0;
		for (int i = 0; i < b.length; i++)
		{
			cs += b[i] & 0xFF;
		}
		return (byte) cs;
	}
	
	/**
     * 读取txt文件的内容
     * @param fileName 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String txt2String(String fileName){
        String result = "";
        try{
            //BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"GBK")); 
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result = result + "\n" +s;
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    public static boolean writeFile(String content, String fileName) {
        FileWriter fw;
        if (content == null || "".endsWith(content)) {
            System.out.println("---------------------------------------------Content is null");
            return false;
        }
        try {
            fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

	public static void appendMethod(String fileName,String content){
		try {
			count++;
			if(count%20==0){
				fileWriter.close();
				fileWriter = new FileWriter(fileName,true);
			}else if(fileWriter==null){
				fileWriter = new FileWriter(fileName,true);
			}
			fileWriter.write(content);
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String calMd5FromFile(String filePath){
		String md5 = null;
		try{
			FileInputStream fis= new FileInputStream(filePath);
			md5 = DigestUtils.md5Hex(IOUtils.toByteArray(fis));
			IOUtils.closeQuietly(fis);
		}catch (Exception e){
			GatewayJFrame.showMssageln("计算文件"+filePath+"的md5值出错");
			e.printStackTrace();
		}finally {
			return md5;
		}
	}
        
	public static void main(String[] args)
	{
		int count = 0;
		while (true){
			count++;
			appendMethod("C:\\Users\\baolei\\Desktop\\routeer\\111111111111.txt","content index="+count+"\n");
			System.out.println("content index="+count);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
