package ua.koziichuk.drive.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class GoogleDriveService {

    @Autowired
    private Drive driveService;

    public String createFolder(String folderName) throws IOException {
        File folderMetadata = new File()
                .setName(folderName)
                .setMimeType("application/vnd.google-apps.folder");

        File folder = driveService.files().create(folderMetadata)
                .setFields("id")
                .execute();

        return folder.getId();
    }

    // Надання доступу користувачам
    public void shareFolder(String folderId, String email, String role) throws IOException {
        Permission permission = new Permission()
                .setType("user")
                .setEmailAddress(email)
                .setRole(role); // "reader", "writer", "owner"

        driveService.permissions().create(folderId, permission)
                .setSendNotificationEmail(false)
                .execute();
    }

    // Отримання списку користувачів з доступом
    public List<Permission> getFolderPermissions(String folderId) throws IOException {
        return driveService.permissions().list(folderId)
                .execute()
                .getPermissions();
    }

    public void uploadFile(File fileMetadata, InputStreamContent mediaContent) throws IOException {
        driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
    }
}
