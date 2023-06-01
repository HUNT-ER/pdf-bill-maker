package com.boldyrev.pdfbillcreator.config;

import com.boldyrev.pdfbillcreator.exceptions.GoogleDriveConnectionException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Value;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleDriveConfiguration {

    @Bean
    public NetHttpTransport netHttpTransport() {
        NetHttpTransport netHttpTransport = null;
        try {
            netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception e) {
            throw new GoogleDriveConnectionException(e.getMessage());
        }
        return netHttpTransport;
    }

    @Bean
    public JsonFactory jsonFactory() {
        return GsonFactory.getDefaultInstance();
    }
}
