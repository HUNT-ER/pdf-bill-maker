package com.boldyrev.pdfbillcreator.repositories.dao;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

@Repository
public class GoogleDriveDAO {

    private final NetHttpTransport netHttpTransport;
    private final JsonFactory jsonFactory;

    @Value("${google.oauth2.credentials.path}")
    private String SECRET_CREDENTIALS;

    public GoogleDriveDAO(NetHttpTransport netHttpTransport, JsonFactory jsonFactory) {
        this.netHttpTransport = netHttpTransport;
        this.jsonFactory = jsonFactory;
    }

    public String saveAndGetUrl(java.io.File bill) throws IOException {
        Drive drive = getDrive();

        File driveBill = new File().setName(bill.getName());
        Permission permission = new Permission().setType("anyone").setRole("reader");

        FileContent content = new FileContent("application/pdf", bill);

        File storedFile = drive.files().create(driveBill, content)
            .setFields("id, name, webContentLink").execute();
        drive.permissions().create(storedFile.getId(), permission).execute();

        return storedFile.getWebContentLink();
    }

    private HttpRequestInitializer getCredentials() throws IOException {
        GoogleCredentials cred = ServiceAccountCredentials.fromStream(
                new ClassPathResource(SECRET_CREDENTIALS).getInputStream())
            .createScoped(DriveScopes.all());
        AccessToken token = cred.refreshAccessToken();

        HttpRequestInitializer credentials = new HttpCredentialsAdapter(cred);

        return credentials;
    }

    private Drive getDrive() throws IOException {
        Drive drive = new Drive.Builder(netHttpTransport, jsonFactory,
            getCredentials()).setApplicationName("bill-maker").build();
        return drive;
    }
}


