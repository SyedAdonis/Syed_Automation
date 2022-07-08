package com.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.testng.ITestResult;
import org.testng.thread.ITestNGThreadPoolExecutor;

public class ExcelUtil {
	private static XSSFSheet xlsxWorkSheet;
	private static XSSFWorkbook xlsxWorkBook;
	private static XSSFCell xlsxCell;
	private static XSSFRow xlsxRow;
	private static HSSFSheet xlsWorkSheet;
	private static HSSFWorkbook xlsWorkBook;
	private static HSSFCell xlsCell;
	private static HSSFRow xlsRow;

	FileInputStream fis = null;
	FileOutputStream fos = null;

	public ExcelUtil(String path, String sheetName) {
		try {
			File file = new File(path);
			if (file.getAbsolutePath().endsWith(".xlsx")) {
				fis = new FileInputStream(file);
				xlsxWorkBook = new XSSFWorkbook(fis);
				xlsxWorkSheet = xlsxWorkBook.getSheet(sheetName);
			} else if (file.getAbsolutePath().endsWith(".xls")) {
				fis = new FileInputStream(file);
				xlsWorkBook = new HSSFWorkbook(fis);
				xlsWorkSheet = xlsWorkBook.getSheet(sheetName);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n" + e.getStackTrace());
		}
	}

	/**
	 * Execute This method to release excel instance
	 * 
	 * @throws Exception
	 */
	public void releaseExcel() throws Exception {
		if (fis != null) {
			fis.close();
		}
	}

	public void releaseExcel(String path, String sheetName) throws IOException {
		if(fis != null) {
			fis.close();
		}
		fos = new FileOutputStream(path);
		xlsxWorkBook.write(fos);
		fos.close();
	}

	public int rowCountX() {
		int rowNum = xlsxWorkSheet.getLastRowNum() + 1;
		return rowNum;
	}

	public int columnCountX() {
		int colNum = xlsxWorkSheet.getRow(0).getLastCellNum();
		return colNum;
	}

	public String getCellData(int rowNum, int colNum) {
		try {
			return xlsxWorkSheet.getRow(rowNum).getCell(colNum).toString();
		} catch (NullPointerException e) {
			return "";
		}
	}

	public void setCellData(int rowNum, int colNum, String cellValue)
	{
		xlsxWorkSheet.getRow(rowNum).getCell(colNum).setCellValue(cellValue);
	}

	/**
	 * Get column number based on the column name. This method will throw an
	 * 
	 * @throws Exception
	 */
	public int findColumnByName(String columnName) throws Exception {
		int columnCount = columnCountX();
		int rowNum = 0;
		int columnNum = 0;

		try {
			for (int i = 0; i < columnCount; i++) {
				if (xlsxWorkSheet.getRow(rowNum).getCell(i).toString().equalsIgnoreCase(columnName)) {
					columnNum = i;
					break;
				}
			}
		} catch (NullPointerException e) {
			throw new Exception("No columns found with the column name " + columnName);
		}
		return columnNum;
	}

	public int findRowNumByColumnNameAndValue(String columnLabel, String columnValue) throws Exception {
		int columnNumberofTestName = findColumnByName(columnLabel);
		int numberofUsedRows = rowCountX();
		int rowNum = 0;

		for (int i = 0; i < numberofUsedRows; i++) {
			String value = getCellData(i, +columnNumberofTestName);
			if (value.equalsIgnoreCase(columnValue)) {
				rowNum = i;
			}
		}
		if (rowNum != 0) {
			return rowNum;
		} else {
			throw new Exception("Value " + columnValue + " is not found in column " + columnLabel);
		}
	}

	public String getDatabyColumnLabel(String columnLabelforDesiredData, String columnLabelofSearch,
			String columnValueofSearch) throws Exception {
		int rowNum = findRowNumByColumnNameAndValue(columnLabelofSearch, columnLabelforDesiredData);
		String data = getCellData(rowNum, findColumnByName(columnLabelforDesiredData));
		return data;
	}

	/**
	 * This method gets the data from a cell based on the value of another cell on
	 * the same row
	 * 
	 * @throws Exception
	 */
	public String getTestDatabyTestName(String testName, String columnLabelofTestName, String columnLabelofTestData)
			throws Exception {
		int rowNum = findRowNumByColumnNameAndValue(columnLabelofTestName, testName);
		int colNum = findColumnByName(columnLabelofTestData);
		return getCellData(rowNum, colNum);
	}

	public void setTestDatabyTestName(String testName, String columnLabelofTestName, String columnLabelofTestData, String cellValue) throws Exception {
		int rowNum = findRowNumByColumnNameAndValue(columnLabelofTestName,testName);
		int colNum = findColumnByName(columnLabelofTestData);
		setCellData(rowNum,colNum, cellValue);
	}

	public Object[] FindTestNamesByTemplateName(String templateColumnLabel, String templateName,
			String excutionFlagColumnLabel, String testNameColumnLabel) throws Exception {
		int columnNum = findColumnByName(templateColumnLabel);
		int exFlagColumnNum = findColumnByName(excutionFlagColumnLabel);

		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < rowCountX(); i++) {
			if (getCellData(i, exFlagColumnNum).equalsIgnoreCase("TRUE")
					&& getCellData(i, columnNum).equalsIgnoreCase(templateName)) {
				names.add(getCellData(i, findColumnByName(testNameColumnLabel)));
			}
		}
		return names.toArray();

	}

	public Object[] getAllTestNamesbyColumnLabel(String testNameColumnLabel, String exFlagColumnLabel)
			throws Exception {
		int exFlagColumnNum = findColumnByName(exFlagColumnLabel);

		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < rowCountX(); i++) {
			if (getCellData(i, exFlagColumnNum).equalsIgnoreCase("TRUE")) {
				names.add(getCellData(i, findColumnByName(testNameColumnLabel)));
			}
		}
		return names.toArray();
	}

	public void updateTestResult(ITestResult result, String testName) throws Exception {

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
		setTestDatabyTestName(testName, "Test Name", "Execution Date", formatter.format(date));
		System.out.println(formatter.format(date));
		if (result.getStatus() == ITestResult.SUCCESS)
		{
			setTestDatabyTestName(testName, "Test Name", "Execution Flag", "FALSE");
			setTestDatabyTestName(testName, "Test Name", "Status", "Passed");

		} else if (result.getStatus() == ITestResult.FAILURE) {
			setTestDatabyTestName(testName, "Test Name", "Status", "Failed");

		}
	}

}
