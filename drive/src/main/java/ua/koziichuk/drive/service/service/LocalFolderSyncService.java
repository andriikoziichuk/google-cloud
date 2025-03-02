package ua.koziichuk.drive.service.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

@Slf4j
@Service
public class LocalFolderSyncService {

    @Autowired
    private Drive driveService;

    @Value("${backup.folder.id}")
    private String backupFolderId;

    @Value("${backup.local.folder.path}")
    private String localFolderPath;

    @Scheduled(cron = "0 0 0 1 * *")
    private void backupMyFolder() {
        try {
            syncFolder(backupFolderId, localFolderPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void backupFolder(String localFolderPath) throws IOException {
        syncFolder(backupFolderId, localFolderPath);
    }

    // Синхронізація локальної папки з Google Drive
    public void syncFolder(String folderId, String localFolderPath) throws IOException {
        java.io.File folder = new java.io.File(localFolderPath);
        java.io.File[] files = folder.listFiles();

        if (files != null) {
            for (java.io.File file : files) {
                if (file.isFile()) {
                    uploadFileToDrive(file, folderId);
                }
            }
        }
    }

    // Завантаження файлу на Google Drive
    private void uploadFileToDrive(java.io.File file, String folderId) throws IOException {
        File fileMetadata = new File()
                .setName(file.getName())
                .setParents(Collections.singletonList(folderId));

        InputStreamContent mediaContent = new InputStreamContent(
                Files.probeContentType(file.toPath()),
                new FileInputStream(file)
        );

        driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
    }
}
