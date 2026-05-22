package com.example.reporting_system.controller;

import com.example.reporting_system.service.GoogleDriveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/drive")
public class DriveController {

    private final GoogleDriveService googleDriveService;

    public DriveController(GoogleDriveService googleDriveService) {
        this.googleDriveService = googleDriveService;
    }

    @PostMapping("/upload")
    public String uploadLatestFile() throws Exception {

        String fileName = "reports/transactions_1779472201097.xlsx";

        return googleDriveService.uploadFile(fileName);
    }

    @GetMapping("/upload")
    public Map<String,String> upload() throws Exception {

        String link =
                googleDriveService.uploadFile(
                        "reports/transactions_1779472201097.xlsx"
                );

        return Map.of(
                "message", "Upload Success",
                "driveLink", link
        );
    }

//    @GetMapping("/upload")
//    public Map<String, String> upload() throws Exception {
//
//        java.io.File folder =
//                new java.io.File("reports");
//
//        java.io.File[] files =
//                folder.listFiles(
//                        (dir, name) ->
//                                name.endsWith(".xlsx")
//                );
//
//        if (files == null || files.length == 0) {
//
//            throw new RuntimeException(
//                    "No Excel files found"
//            );
//        }
//
//        java.io.File latestFile =
//                java.util.Arrays.stream(files)
//                        .max(
//                                java.util.Comparator.comparingLong(
//                                        java.io.File::lastModified
//                                )
//                        )
//                        .get();
//
//        String link =
//                googleDriveService.uploadFile(
//                        latestFile.getPath()
//                );
//
//        return Map.of(
//                "message", "Upload Success",
//                "fileName", latestFile.getName(),
//                "driveLink", link
//        );
//    }
}