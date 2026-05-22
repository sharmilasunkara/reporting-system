package com.example.reporting_system.scheduler;

import com.example.reporting_system.entity.EmployeeTransaction;
import com.example.reporting_system.repository.EmployeeTransactionRepository;
import com.example.reporting_system.service.EmailService;
import com.example.reporting_system.service.GoogleDriveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ReportScheduler {

    private final EmployeeTransactionRepository repository;
    private final EmailService emailService;
    private final GoogleDriveService googleDriveService;

    public ReportScheduler(
            EmployeeTransactionRepository repository, EmailService emailService, GoogleDriveService googleDriveService) {

        this.repository = repository;
        this.emailService = emailService;
        this.googleDriveService = googleDriveService;
    }

    private LocalDateTime lastRunTime =
            LocalDateTime.now().minusDays(1);

    //   @Scheduled(cron = "*/30 * * * * *")
    @Scheduled(cron = "0 */1 * * * *")
    // @Scheduled(cron = "0 0 */6 * * *")
    public void generateExcelReport() {

        log.info("Scheduler started at: {}", LocalDateTime.now());


        try {

            List<EmployeeTransaction> transactions =
                    repository.findByCreatedAtAfter(lastRunTime);

            if (transactions.isEmpty()) {

                log.info("No new transactions found");

                return;
            }


            XSSFWorkbook workbook = new XSSFWorkbook();

            Sheet sheet =
                    workbook.createSheet("Transactions");

            Row header = sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("ID");

            header.createCell(1)
                    .setCellValue("Employee Name");

            header.createCell(2)
                    .setCellValue("Department");

            header.createCell(3)
                    .setCellValue("Amount");

            int rowNum = 1;

            for (EmployeeTransaction transaction
                    : transactions) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(transaction.getId());

                row.createCell(1)
                        .setCellValue(
                                transaction.getEmployeeName()
                        );

                row.createCell(2)
                        .setCellValue(
                                transaction.getDepartment()
                        );

                row.createCell(3)
                        .setCellValue(
                                transaction.getAmount()
                                        .doubleValue()
                        );
            }

            String fileName =
                    "reports/transactions_"
                            + System.currentTimeMillis()
                            + ".xlsx";

            java.io.File directory =
                    new java.io.File("reports");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            FileOutputStream outputStream =
                    new FileOutputStream(fileName);

            workbook.write(outputStream);

            workbook.close();

            outputStream.close();

            String driveLink =
                    googleDriveService.uploadFile(fileName);

            log.info("Google Drive Link: {}", driveLink);

            lastRunTime = LocalDateTime.now();

            log.info("Excel report generated successfully");

            emailService.sendEmailWithAttachment(
                    "sunkarasharmila519@gmail.com",
                    "Employee Transaction Report",
                    "Please find attached latest report.",
                    fileName
            );

            log.info("Last run time updated to: {}", lastRunTime);

        } catch (Exception ex) {

            log.error("Exception: {}", ex);

        }
    }
}