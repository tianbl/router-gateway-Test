package com.eastsoft.gateway.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

public class SaveToXLS
{
	/**
	 * 从xls表格中读取gid，gid为十进制表示形式的字符串， 第一次读取xls中无数据，
	 * */
	public static String readLastGidFromExcelSheet1()
	{
		HSSFWorkbook wb = null;
		File printRecord = new File("网关生产记录.xls");
		if (!printRecord.exists())
		{
			return "网关生产记录.xls不存在！\n";
		}
		FileInputStream fs = null;
		POIFSFileSystem ps = null;
		try
		{
			fs = new FileInputStream(printRecord);
			ps = new POIFSFileSystem(fs);
			wb = new HSSFWorkbook(ps);
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		HSSFSheet sheet = wb.getSheetAt(0);
		if (sheet.getLastRowNum() < 1)
		{
			return "网关生产记录.xls格式不正确，缺少默认网关GID行！\n";
		}
		HSSFRow row = sheet.getRow(sheet.getLastRowNum());
		HSSFCell cell = row.getCell(1);
		if (cell == null)
		{
			return "网关生产记录.xls格式不正确，未指定起始GID！\n";
		}
		row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
		return cell.getStringCellValue();
	}

	/**
	 * 向xls表格中存放数据gid，gid为十进制表示形式
	 * */
	public static int saveGidToExcelSheet1(String gid, String testusername)
	{
		HSSFWorkbook wb = null;
		File printRecord = new File("网关生产记录.xls");
		if (printRecord.exists())
		{
			FileInputStream fs = null;
			POIFSFileSystem ps = null;
			try
			{
				fs = new FileInputStream(printRecord);
				ps = new POIFSFileSystem(fs);
				wb = new HSSFWorkbook(ps);
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			HSSFSheet sheet = wb.getSheetAt(0);
			// 创建一行并向其中添加一些cell,Rows
			// 下标从0开始(Create a row and put some cells in
			// it. Rows are 0 based.)
			HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String time = format.format(date);
			row.createCell(0).setCellValue(time);
			row.createCell(1).setCellValue(gid);
			row.createCell(2).setCellValue(testusername);
		} else
		{
			return 0;
		}

		FileOutputStream fileOut = null;
		try
		{
			fileOut = new FileOutputStream(printRecord);
		} catch (FileNotFoundException e)
		{
			return 1;
			// appendTextareaText(jTextArea_status,
			// "\n打印记录.xls 另一个程序正在使用此文件，请关闭");
			// JOptionPane.showMessageDialog(getParent(),
			// "打印记录.xls 另一个程序正在使用此文件，请关闭", "打印记录.xls文件错误",
			// JOptionPane.WARNING_MESSAGE);
			// e.printStackTrace();
		}
		try
		{
			wb.write(fileOut);
			fileOut.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return 10;
	}

	/**
	 * 用户自己添加的gid保存在sheet2中
	 * 
	 * */
	public static int saveGidToExcelSheet2(String gid, String testusername)
	{
		HSSFWorkbook wb = null;
		File printRecord = new File("网关生产记录.xls");
		if (printRecord.exists())
		{
			FileInputStream fs = null;
			POIFSFileSystem ps = null;
			try
			{
				fs = new FileInputStream(printRecord);
				ps = new POIFSFileSystem(fs);
				wb = new HSSFWorkbook(ps);
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			HSSFSheet sheet = wb.getSheetAt(1);
			// 创建一行并向其中添加一些cell,Rows，下标从0开始
			HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String time = format.format(date);
			row.createCell(0).setCellValue(time);
			row.createCell(1).setCellValue(gid);
			row.createCell(2).setCellValue(testusername);
		} else
		{
			return 0;
		}
		FileOutputStream fileOut = null;
		try
		{
			fileOut = new FileOutputStream(printRecord);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return 1;
		}
		try
		{
			wb.write(fileOut);
			fileOut.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return 10;
	}

}
