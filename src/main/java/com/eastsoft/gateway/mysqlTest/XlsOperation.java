package com.eastsoft.gateway.mysqlTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

public class XlsOperation implements ServerInfo {

	private File file;
	private InputStream is;
	private HSSFWorkbook hssfWorkbook;
	private FileOutputStream outReport = null;

	public XlsOperation(String fileName) {
		file = new File(fileName);
		try {
			is = new FileInputStream(file);
			hssfWorkbook = new HSSFWorkbook(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> getServerInfo(String column, String value) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
			if (hssfSheet == null) {
				continue;
			}

			int columnNum = 0;
			HSSFRow tablehead = hssfSheet.getRow(0);
			String[] key = new String[tablehead.getLastCellNum()];
			// 循环查找column所在列的索引
			for (int i = 0; i < tablehead.getLastCellNum(); i++) {
				HSSFCell hssfCell = tablehead.getCell(i);
				key[i] = getValue(hssfCell);
				if (column.equals(getValue(hssfCell))) {
					columnNum = i;
				}
			}

			// 循环行Row中的column列，找到列值为value的那一行取出
			for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow == null) {
					continue;
				}
				HSSFCell hssfCell = hssfRow.getCell(columnNum);
				if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
					hssfCell.setCellType(Cell.CELL_TYPE_STRING);
				}
				if (value.equals(getValue(hssfCell))) {
					Map<String, Object> map = new HashMap();
					for (int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++) {
						hssfCell = hssfRow.getCell(cellNum);
						if (hssfCell == null) {
							continue;
						}
						if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
							hssfCell.setCellType(Cell.CELL_TYPE_STRING);
						}
						map.put(key[cellNum], getValue(hssfCell));
						System.out.print("    " + getValue(hssfCell));
					}
					return map;
				}
			}
		}
		return null;
	}

	@Override
	public int setUsed(String column, String value, Map<String, String> mac) {
		// TODO Auto-generated method stub
//		String[] macColumn = { "mac_label", "mac_3", "mac_5", "mac_6" };
		String[] macColumn = { "MAClabel", "mac_3", "mac_5", "mac_6" };
		int[] indexToSet = new int[4];

		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
			if (hssfSheet == null) {
				continue;
			}

			// 循环查找column所在列的索引,得到要操作的行索引
			int columnNum = 0;
			HSSFRow tablehead = hssfSheet.getRow(0);
			String[] key = new String[tablehead.getLastCellNum()];
			for (int i = 0; i < tablehead.getLastCellNum(); i++) {
				HSSFCell hssfCell = tablehead.getCell(i);
				key[i] = getValue(hssfCell);
				if (column.equals(getValue(hssfCell))) {
					columnNum = i;
				} else {
					for (int j = 0; j < macColumn.length; j++) {
						if (macColumn[j].equals(getValue(hssfCell))) {
							indexToSet[j] = i;
						}
					}
				}
			}

			// 循环行Row中的column列，找到列值为value的那一行取出
			for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow == null) {
					continue;
				}

				// 得到 columnNum列
				HSSFCell hssfCell = hssfRow.getCell(columnNum);
				if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
					hssfCell.setCellType(Cell.CELL_TYPE_STRING);
				}
				if (value.equals(getValue(hssfCell))) {

					// 找到所需要设置的行,设置此行。
					for (int cellNum = 0; cellNum < indexToSet.length; cellNum++) {
						hssfCell = hssfRow.getCell(indexToSet[cellNum]);
						if(null==hssfCell){
							hssfCell = hssfRow.createCell(indexToSet[cellNum]);
						}
						if(0==cellNum){
							hssfCell.setCellValue("U");
						}else{
							switch(macColumn[cellNum]){
							case "mac_3":hssfCell.setCellValue(mac.get("wifi"));break;
							case "mac_5":hssfCell.setCellValue(mac.get("wan"));break;
							case "mac_6":hssfCell.setCellValue(mac.get("lan"));break;
							}
							
						}
						
					}

					try {
						outReport = new FileOutputStream(file);
						hssfWorkbook.write(outReport);
						outReport.flush();
						outReport.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return 0;
					}
					return 1;
				}
			}
		}
		return 0;
	}
	
	@SuppressWarnings("static-access")
	private String getValue(HSSFCell hssfCell) {
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(hssfCell.getNumericCellValue());
		} else {
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}
}
