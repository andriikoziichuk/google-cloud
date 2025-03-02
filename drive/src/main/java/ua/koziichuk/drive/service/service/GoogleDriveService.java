package ua.koziichuk.drive.service.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public Permission getPermission(String fileId, String permissionId) throws IOException {
        return driveService.permissions()
                .get(fileId, permissionId)
                .execute();
    }

    // Зміна рівня доступу
    public void updatePermission(String fileId, String permissionId, String newRole) throws IOException {
        Permission permission = new Permission()
                .setRole(newRole); // "reader", "writer", "owner"

        driveService.permissions()
                .update(fileId, permissionId, permission)
                .execute();
    }

    // Видалення доступу
    public void deletePermission(String fileId, String permissionId) throws IOException {
        driveService.permissions()
                .delete(fileId, permissionId)
                .execute();
    }
}
