package ua.koziichuk.drive.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.koziichuk.drive.service.service.LocalFolderSyncService;

import java.io.IOException;

@RestController
@RequestMapping("/drive")
public class DriveSyncController {

    @Autowired
    private LocalFolderSyncService syncService;

    // Синхронізація локальної папки з Google Drive
    @PostMapping("/sync-folder/{localFolderPath}/{folderId}")
    public ResponseEntity<String> syncFolder(@PathVariable String folderId, @PathVariable String localFolderPath) {
        try {
            syncService.syncFolder(folderId, localFolderPath);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error synchronizing folder");
        }
        return ResponseEntity.ok("Folder synchronized successfully");
    }

    @PostMapping("/backup/{localFolderPath}")
    public ResponseEntity<String> backupFiles(@PathVariable String localFolderPath) {
        try {
            syncService.backupFolder(localFolderPath);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error while backuping folder");
        }
        return ResponseEntity.ok("Backup completed successfully");
    }
}
