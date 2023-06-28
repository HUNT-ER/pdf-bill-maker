package com.boldyrev.pdfbillcreator.config;

import com.boldyrev.pdfbillcreator.exceptions.GoogleDriveConnectionException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
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
