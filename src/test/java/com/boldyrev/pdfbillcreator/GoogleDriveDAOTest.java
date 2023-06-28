package com.boldyrev.pdfbillcreator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.boldyrev.pdfbillcreator.repositories.dao.GoogleDriveDAO;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GoogleDriveDAOTest {

    @Autowired
    private GoogleDriveDAO drive;

    private File file;

    @BeforeEach
    void createFile() throws IOException {
        File file = new File("test_file");
        file.createNewFile();
        this.file = file;
    }

    @AfterEach
    void deleteFile() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void saveAndGetLink_WithCorrectFilePath_ReturnsDownloadLink() throws IOException {

        String link = drive.saveAndGetUrl(file);

        assertThat(link).startsWith("https://drive.google.com/uc?id=");
    }

    @Test
    void saveAndGetLink_WithEmptyFilePath_ThrowsIOException() {

        assertThatExceptionOfType(IOException.class).isThrownBy(
            () -> drive.saveAndGetUrl(new File("not_existed_file")));
    }

}
