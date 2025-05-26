package tasks.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tasks.entity.AttendanceRecord;
import tasks.entity.AttendanceStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelExporter {

    public static void exportAll(OutputStream out, List<AttendanceRecord> records, List<AttendanceStatus> statuses) throws IOException {
        Workbook wb = new XSSFWorkbook();

        Sheet recordSheet = wb.createSheet("상세 내역");
        Sheet statusSheet = wb.createSheet("근태 현황");

        // Header for Records
        Row rHeader = recordSheet.createRow(0);
        String[] rHeaders = {"이름", "종류", "시작일", "종료일", "일수", "사유"};
        for (int i = 0; i < rHeaders.length; i++) {
            rHeader.createCell(i).setCellValue(rHeaders[i]);
        }

        int rRowNum = 1;
        for (AttendanceRecord r : records) {
            Row row = recordSheet.createRow(rRowNum++);
            row.createCell(0).setCellValue(r.getUser().getName());
            row.createCell(1).setCellValue(r.getType().name());
            row.createCell(2).setCellValue(r.getStartDate().toString());
            row.createCell(3).setCellValue(r.getEndDate().toString());
            row.createCell(4).setCellValue(r.getDays().doubleValue());
            row.createCell(5).setCellValue(r.getReason() != null ? r.getReason() : "");
        }

        // Header for Status
        Row sHeader = statusSheet.createRow(0);
        String[] sHeaders = {"이름", "투입일", "해지일", "연차(부여)", "연차(사용)", "연차(잔여)", "대휴(부여)", "대휴(사용)", "대휴(잔여)"};
        for (int i = 0; i < sHeaders.length; i++) {
            sHeader.createCell(i).setCellValue(sHeaders[i]);
        }

        int sRowNum = 1;
        for (AttendanceStatus s : statuses) {
            Row row = statusSheet.createRow(sRowNum++);
            row.createCell(0).setCellValue(s.getUser().getName());
            row.createCell(1).setCellValue(s.getJoinDate().toString());
            row.createCell(2).setCellValue(s.getLeaveDate() != null ? s.getLeaveDate().toString() : "");
            row.createCell(3).setCellValue(s.getAnnualGranted().doubleValue());
            row.createCell(4).setCellValue(s.getAnnualUsed().doubleValue());
            row.createCell(5).setCellValue(s.getAnnualGranted().subtract(s.getAnnualUsed()).doubleValue());
            row.createCell(6).setCellValue(s.getCompensatoryGranted().doubleValue());
            row.createCell(7).setCellValue(s.getCompensatoryUsed().doubleValue());
            row.createCell(8).setCellValue(s.getCompensatoryGranted().subtract(s.getCompensatoryUsed()).doubleValue());
        }

        wb.write(out);
        wb.close();
    }
}
