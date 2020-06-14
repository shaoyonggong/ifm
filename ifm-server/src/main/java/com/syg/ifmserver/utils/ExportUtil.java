package com.syg.ifmserver.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by gexiaobing on 2019-01-21
 *
 * @description 导出工具类
 */
public class ExportUtil {

    /**
     * 生成excel文件
     *
     * @param headList
     * @return
     */
    public static ResponseEntity<byte[]> creatHttpHeaders(String filename, List<String> headList, List<List> contentList) throws IOException {

        byte[] content = toExcelFile(filename, headList, contentList);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename + ".xlsx", StandardCharsets.UTF_8).build());

        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel;charset=UTF-8"));

        headers.setContentLength(content.length);

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    /**
     * 生成excel文件
     *
     * @param headList
     * @return
     */
    public static ResponseEntity<byte[]> creatHttpHeaders(HttpServletRequest request, HttpServletResponse response, String filename, List<String> headList, List<List> contentList) throws IOException {

        byte[] content = toExcelFile("sheet", headList, contentList);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        response.setHeader("content-disposition", "attachment;filename=" + filename + ".xlsx");

        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    /**
     * 转成excel文件
     *
     * @return
     */
    public static byte[] toExcelFile(String sheetName, List<String> headList, List<List> contentList) throws IOException {

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFSheet sheet = wb.createSheet(sheetName);

        Font font = wb.createFont();

        font.setFontName("宋体");

        font.setFontHeightInPoints((short) 11);//设置字体大小

        CellStyle cellStyle = wb.createCellStyle();

        /*cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
        cellStyle.setBorderTop(BorderStyle.THIN );//上边框
        cellStyle.setBorderRight(BorderStyle.THIN);//右边框*/
        cellStyle.setFont(font);

        int rowPos = 0;
        Row headRow = sheet.createRow(rowPos++);

        for (int i = 0; i < headList.size(); i++) {
            sheet.setDefaultColumnStyle(0, cellStyle);
            headRow.createCell(i).setCellValue(headList.get(i));
        }

        for (int i = 0; i < contentList.size(); i++) {

            List rowContent = contentList.get(i);
            Row row = sheet.createRow(rowPos++);

            for (int j = 0; j < rowContent.size(); j++) {
                Object cellContent = rowContent.get(j);
                Cell cell = row.createCell(j);
                if (cellContent == null) {
                    cell.setCellValue("");
                } else {

                    if (cellContent instanceof BigDecimal) {
                        cell.setCellValue(((BigDecimal) cellContent).intValue());
                    } else {
                        cell.setCellValue(cellContent.toString());
                    }
                }
            }
        }

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        wb.write(content);
        wb.close();

        return content.toByteArray();
    }

    /**
     * 转成excel文件
     * 调用该方法一定要在外层关闭流
     *
     * @return
     */
    public static XSSFWorkbook getXSSFWorkbook(String sheetName, List<String> headList, List<List> contentList) throws IOException {

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFSheet sheet = wb.createSheet(sheetName);
        //设置字体
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 11);

        //设置单元格样式
        CellStyle cellStyle = wb.createCellStyle();
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//        cellStyle.setBorderLeft(BorderStyle.THIN);
//        cellStyle.setBorderTop(BorderStyle.THIN);
//        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setFont(font);

        int rowPos = 0;
        Row headRow = sheet.createRow(rowPos++);

        for (int i = 0; i < headList.size(); i++) {
            sheet.setDefaultColumnStyle(0, cellStyle);
            headRow.createCell(i).setCellValue(headList.get(i));
        }

        for (int i = 0; i < contentList.size(); i++) {

            List rowContent = contentList.get(i);
            Row row = sheet.createRow(rowPos++);

            for (int j = 0; j < rowContent.size(); j++) {
                Object cellContent = rowContent.get(j);
                Cell cell = row.createCell(j);
                if (cellContent == null) {
                    cell.setCellValue("");
                } else {

                    if (cellContent instanceof BigDecimal) {
                        cell.setCellValue(((BigDecimal) cellContent).intValue());
                    } else {
                        cell.setCellValue(cellContent.toString());
                    }
                }
            }
        }

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        wb.write(content);

        return wb;
    }
}
