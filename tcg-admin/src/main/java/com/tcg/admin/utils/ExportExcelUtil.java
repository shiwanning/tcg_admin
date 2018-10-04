package com.tcg.admin.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public class ExportExcelUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportExcelUtil.class);
	
	private ExportExcelUtil() {}
	
	public static final String exportExcel(HttpServletResponse response, String fileName,
			String title, String[] tableTile, String[] attrName, JSONArray array) {
		HSSFWorkbook workbook = new HSSFWorkbook(); 
		HSSFCellStyle sheetStyle = workbook.createCellStyle();  
		HSSFCellStyle headstyle = workbook.createCellStyle();  
		HSSFSheet sheet = generateSheet(workbook, sheetStyle, headstyle, tableTile);

		HSSFFont columnHeadFont = workbook.createFont();  
		columnHeadFont.setFontName("宋体");  
		columnHeadFont.setFontHeightInPoints((short) 10);  
		columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
		HSSFCellStyle columnHeadStyle = workbook.createCellStyle();  
		columnHeadStyle.setFont(columnHeadFont);  
		columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		columnHeadStyle.setLocked(true);  
		columnHeadStyle.setWrapText(true);

		columnHeadStyle.setTopBorderColor(HSSFColor.BLACK.index);
		columnHeadStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM); 
		columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		columnHeadStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);
		columnHeadStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		
		HSSFFont font = workbook.createFont();  
		font.setFontName("宋体");  
		font.setFontHeightInPoints((short) 10);  
		HSSFCellStyle style = workbook.createCellStyle();  
		style.setFont(font);  
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setLocked(true);  
		style.setWrapText(true);

		style.setTopBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM); 
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);

		CellRangeAddress range = new CellRangeAddress(0, 0, 0, tableTile.length-1);
		sheet.addMergedRegion(range);
		
		range = new CellRangeAddress(1, 1, 0, tableTile.length-1);
		sheet.addMergedRegion(range);
		
		
		try {  
			HSSFRow row0 = sheet.createRow(0);  
			row0.setHeight((short) 900);  
			HSSFCell cell0 = row0.createCell(0);  
			cell0.setCellValue(title);  
			cell0.setCellStyle(headstyle);
			
			HSSFRow row2 = sheet.createRow(2);
			row2.setHeight((short) 550);
			for (int i = 0; i < tableTile.length; i++) {
				HSSFCell cell = row2.createCell(i);
				cell.setCellValue(tableTile[i]);  
				cell.setCellStyle(columnHeadStyle);
			}

			for (int i = 0; i < array.size(); i++) {
				HSSFRow row = sheet.createRow(3+i);
				row.setHeight((short) 550);
				for (int j = 0; j < attrName.length; j++) {
					JSONObject obj = JSONObject.fromObject(array.get(i));
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(obj.get(attrName[j])==null?"":obj.get(attrName[j])+"");  
					cell.setCellStyle(style);
				}
			}

			response.setContentType("application/vnd.ms-excel");  
			response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(),"ISO-8859-1"));  
			OutputStream ouputStream = response.getOutputStream();  
			workbook.write(ouputStream);  
			ouputStream.flush();  
			ouputStream.close();
			workbook.close();

		} catch (Exception e) {  
			LOGGER.error("export excel error", e);
		}  
		return null;  
	}

	private static HSSFSheet generateSheet(HSSFWorkbook workbook, HSSFCellStyle sheetStyle, HSSFCellStyle headstyle, String[] tableTile) {
		HSSFSheet sheet = workbook.createSheet();
		
		for (int i = 0; i < tableTile.length; i++) {
			sheet.setColumnWidth(i, 6000);  
		}

		sheetStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);  
		sheetStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);  
		sheetStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);  
		for (int i = 0; i <= tableTile.length; i++) {  
			sheet.setDefaultColumnStyle((short) i, sheetStyle);  
		}

		HSSFFont headfont = workbook.createFont();  
		headfont.setFontName("黑体");  
		headfont.setFontHeightInPoints((short) 22);
		headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		headstyle.setFont(headfont);  
		headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  
		headstyle.setLocked(true);  
		headstyle.setWrapText(true);
		
		return sheet;
	}
}
