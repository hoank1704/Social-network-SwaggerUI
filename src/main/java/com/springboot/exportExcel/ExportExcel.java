package com.springboot.exportExcel;

import com.springboot.entities.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class ExportExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private User loggedInUser;

    public ExportExcel(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        workbook = new XSSFWorkbook();
    }

    void writeHeaderLine() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);

        createCell(row, 0, "Posts", style);
        createCell(row, 1, "Likes Count", style);
        createCell(row, 2, "Comments Count", style);
    }

    public void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    void writeDataLines() {
        Row row = sheet.createRow(1);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);

        // Số bài đã viết tuần qua
        createCell(row, 0, loggedInUser.getPostsCount(), style);

        // Số like
        createCell(row, 1, loggedInUser.getLikesCount(), style);

        // Số comment mới tuần qua
        createCell(row, 2, loggedInUser.getCommentsCount(), style);

    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
