package com.tcg.admin.utils;

import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ExportExcel {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExportExcel.class);
	
	private ExportExcel() {}
	
    public static final void exportExcel(HttpServletResponse response,
                                    String fileName,
                                    Map<Integer, ReportTO> data) {

        try(HSSFWorkbook workbook = new HSSFWorkbook()) {
            for (Entry<Integer, ReportTO> pair : data.entrySet()) {
                HSSFSheet sheet = workbook.createSheet();// 创建一个Excel的Sheet
                ReportTO report = pair.getValue();
                String title = report.getTitle();
                String search = report.getSubTitle();
                String[] tableTile = report.getTableTile();
                String[] attrName = report.getAttrName();
                JSONArray array = report.getArray();

                // 设置列宽
                for (int i = 0; i < tableTile.length; i++) {
                    sheet.setColumnWidth(i, 6000);
                }
                // Sheet样式
                setSheetStyle(workbook, sheet, tableTile);

                // 标题字体样式
                HSSFFont headfont = getDefaultHeadFont(workbook);

                //标题样式
                HSSFCellStyle headstyle = getDefaultHeadStyle(workbook, headfont);
                
                // 搜索条件字体样式
                HSSFFont searchFont = getDefaultSearchFont(workbook);
                HSSFCellStyle searchStyle = getDefaultSearchStyle(workbook, searchFont);

                // 表头字体样式
                HSSFFont columnHeadFont = getDefaultColumnHeadFont(workbook);
                HSSFCellStyle columnHeadStyle = getColumnHeadStyle(workbook, columnHeadFont);

                //主要内容样式
                HSSFFont font = getMainDefaultFont(workbook);
                
                HSSFCellStyle style = getDefaultMainCellStyle(workbook, font);
                
                /**
                 * 合并单元格
                 *    第一个参数：第一个单元格的行数（从0开始）
                 *    第二个参数：第二个单元格的行数（从0开始）
                 *    第三个参数：第一个单元格的列数（从0开始）
                 *    第四个参数：第二个单元格的列数（从0开始）
                 */
                CellRangeAddress range = new CellRangeAddress(0, 0, 0, tableTile.length-1);
                sheet.addMergedRegion(range);

                range = new CellRangeAddress(1, 1, 0, tableTile.length-1);
                sheet.addMergedRegion(range);

                // 创建第一行
                HSSFRow row0 = sheet.createRow(0);
                // 设置行高
                row0.setHeight((short) 900);
                // 创建第一列
                HSSFCell cell0 = row0.createCell(0);
                cell0.setCellValue(title);
                cell0.setCellStyle(headstyle);

                // 创建第二行
                HSSFRow row1 = sheet.createRow(1);
                row1.setHeight((short) 750);
                HSSFCell cell1 = row1.createCell(0);
                cell1.setCellValue(search);
                cell1.setCellStyle(searchStyle);

                // 第三行
                HSSFRow row2 = sheet.createRow(2);
                row2.setHeight((short) 550);
                for (int i = 0; i < tableTile.length; i++) {
                    HSSFCell cell = row2.createCell(i);
                    cell.setCellValue(tableTile[i]);
                    cell.setCellStyle(columnHeadStyle);
                }

                //主要内容
                for (int i = 0; i < array.size(); i++) {
                    HSSFRow row = sheet.createRow(3+i);
                    row.setHeight((short) 550);
                    JSONObject obj = JSONObject.fromObject(array.get(i));
                    for (int j = 0; j < attrName.length; j++) {
                        HSSFCell cell = row.createCell(j);

                        cell.setCellValue(null == obj.get(attrName[j]) ? "" : obj.get(attrName[j]) + "");
                        cell.setCellStyle(style);
                    }
                }
            }

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"),"ISO-8859-1"));
            
            try(OutputStream ouputStream = response.getOutputStream()) {
            	workbook.write(ouputStream);
            }
            
        } catch (Exception e) {
        	LOGGER.error("export excel error", e);
        }
    }

	private static HSSFFont getMainDefaultFont(HSSFWorkbook workbook) {
		 HSSFFont font = workbook.createFont();
         font.setFontName("宋体");
         font.setFontHeightInPoints((short) 10);
		return font;
	}

	private static HSSFFont getDefaultColumnHeadFont(HSSFWorkbook workbook) {
		HSSFFont columnHeadFont = workbook.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 10);
        columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		return columnHeadFont;
	}

	private static HSSFFont getDefaultHeadFont(HSSFWorkbook workbook) {
		HSSFFont headfont = workbook.createFont();
        headfont.setFontName("黑体");
        headfont.setFontHeightInPoints((short) 22);// 字体大小
        headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
        return headfont;
	}

	private static HSSFCellStyle setSheetStyle(HSSFWorkbook workbook, HSSFSheet sheet, String[] tableTile) {
		HSSFCellStyle sheetStyle = workbook.createCellStyle();
        // 背景色的设定
        sheetStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);

        // 前景色的设定
        sheetStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        // 填充模式
        sheetStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
        // 设置列的样式
        for (int i = 0; i <= tableTile.length; i++) {
            sheet.setDefaultColumnStyle((short) i, sheetStyle);
        }
		return sheetStyle;
	}

	private static HSSFCellStyle getDefaultMainCellStyle(HSSFWorkbook workbook, HSSFFont font) {
		HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        style.setLocked(true);
        style.setWrapText(true);// 自动换行
        //设置边框样式
        style.setTopBorderColor(HSSFColor.BLACK.index);
        style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        style.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色
        style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM); // 设置单元格的边框为粗体
        style.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);// 边框的大小
        style.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);// 边框的大小
		return style;
	}

	private static HSSFCellStyle getColumnHeadStyle(HSSFWorkbook workbook, HSSFFont columnHeadFont) {
		HSSFCellStyle columnHeadStyle = workbook.createCellStyle();
        columnHeadStyle.setFont(columnHeadFont);
        columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        columnHeadStyle.setLocked(true);
        columnHeadStyle.setWrapText(true);// 自动换行
        //设置边框样式
        columnHeadStyle.setTopBorderColor(HSSFColor.BLACK.index);
        columnHeadStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色
        columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM); // 设置单元格的边框为粗体
        columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        columnHeadStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);// 边框的大小
        columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        columnHeadStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);// 边框的大小
		return columnHeadStyle;
	}

	private static HSSFCellStyle getDefaultHeadStyle(HSSFWorkbook workbook, HSSFFont headfont) {
		HSSFCellStyle headstyle = workbook.createCellStyle();
        headstyle.setFont(headfont);
        headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        headstyle.setLocked(true);
        headstyle.setWrapText(true);// 自动换行
		return headstyle;
	}

	private static HSSFFont getDefaultSearchFont(HSSFWorkbook workbook) {
		HSSFFont searchFont = workbook.createFont();
        searchFont.setFontName("宋体");
        searchFont.setFontHeightInPoints((short) 14);// 字体大小
        searchFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
		return searchFont;
	}

	private static HSSFCellStyle getDefaultSearchStyle(HSSFWorkbook workbook, HSSFFont searchFont) {
		HSSFCellStyle searchStyle = workbook.createCellStyle();
		searchStyle.setFont(searchFont);
		searchStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		searchStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		searchStyle.setLocked(true);
		searchStyle.setWrapText(true);// 自动换行
		return searchStyle;
	}

}