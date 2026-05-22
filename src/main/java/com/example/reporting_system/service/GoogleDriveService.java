package com.example.reporting_system.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class GoogleDriveService {

    private final OAuth2AuthorizedClientService clientService;

    public GoogleDriveService(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    private String getAccessToken() {

        OAuth2AuthenticationToken authentication =(OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException(
                    "No authenticated user found. Google OAuth login required."
            );
        }
        OAuth2AuthorizedClient client =

                clientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName()
                );

        return client.getAccessToken().getTokenValue();
    }

    public String uploadFile(String filePath) throws Exception {

        String accessToken = getAccessToken();

        File file = new File(filePath);

        String boundary = "----boundary";
        String LINE_FEED = "\r\n";

        URL url = new URL(
                "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart"
        );

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "multipart/related; boundary=" + boundary);

        try (OutputStream outputStream = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)) {

            // Metadata
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Type: application/json; charset=UTF-8").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append("{\"name\":\"" + file.getName() + "\"}").append(LINE_FEED);

            // File content
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Type: application/octet-stream").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();

            writer.append(LINE_FEED);
            writer.append("--" + boundary + "--").append(LINE_FEED);
        }

        int responseCode = conn.getResponseCode();

        if (responseCode == 200 || responseCode == 201) {
            log.info("File uploaded successfully");
            return "Upload Success";
        } else {
            throw new RuntimeException("Upload failed: " + conn.getResponseMessage());
        }
    }
}