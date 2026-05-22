package com.example.reporting_system.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;

@Service
@Slf4j
public class GoogleDriveService {

    private static final String APPLICATION_NAME =
            "Reporting System";

    private static final GsonFactory JSON_FACTORY =
            GsonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY_PATH =
            "tokens";

    private Credential getCredentials() throws Exception {

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JSON_FACTORY,
                        new InputStreamReader(
                                getClass().getResourceAsStream(
                                        "/credentials.json"
                                )
                        )
                );

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JSON_FACTORY,
                        clientSecrets,
                        Collections.singleton(DriveScopes.DRIVE_FILE)
                )
                        .setDataStoreFactory(
                                new FileDataStoreFactory(
                                        Paths.get(TOKENS_DIRECTORY_PATH)
                                                .toFile()
                                )
                        )
                        .setAccessType("offline")
                        .build();

        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder()
                        .setPort(8888)
                        .build();

        return new AuthorizationCodeInstalledApp(
                flow,
                receiver
        ).authorize("user");
    }

    public String uploadFile(String filePath)
            throws Exception {

        Drive driveService =
                new Drive.Builder(
                        GoogleNetHttpTransport
                                .newTrustedTransport(),
                        JSON_FACTORY,
                        getCredentials()
                )
                        .setApplicationName(
                                APPLICATION_NAME
                        )
                        .build();

        com.google.api.services.drive.model.File fileMetadata =
                new com.google.api.services.drive.model.File();

        fileMetadata.setName(
                Paths.get(filePath)
                        .getFileName()
                        .toString()
        );

        java.io.File file =
                new java.io.File(filePath);

        FileContent mediaContent =
                new FileContent(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        file
                );

        com.google.api.services.drive.model.File uploadedFile =
                driveService.files()
                        .create(fileMetadata, mediaContent)
                        .setFields("id, webViewLink")
                        .execute();

        Permission permission =
                new Permission();

        permission.setType("anyone");

        permission.setRole("reader");

        driveService.permissions()
                .create(uploadedFile.getId(), permission)
                .execute();

        log.info("File uploaded to Google Drive");

        return uploadedFile.getWebViewLink();
    }
}