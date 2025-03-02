package ua.koziichuk.drive.service.controller;

import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.koziichuk.drive.service.service.GoogleDriveService;

import java.io.IOException;

@RestController
@RequestMapping("/drive")
public class DrivePermissionsController {

    @Autowired
    private GoogleDriveService driveService;

    @GetMapping("/permissions/{fileId}/{permissionId}")
    public ResponseEntity<Permission> getPermission(
            @PathVariable String fileId,
            @PathVariable String permissionId
    ) throws IOException {
        Permission permission = driveService.getPermission(fileId, permissionId);
        return ResponseEntity.ok(permission);
    }

    // Зміна рівня доступу
    @PutMapping("/permissions/{fileId}/{permissionId}")
    public ResponseEntity<String> updatePermission(
            @PathVariable String fileId,
            @PathVariable String permissionId,
            @RequestParam String newRole
    ) throws IOException {
        driveService.updatePermission(fileId, permissionId, newRole);
        return ResponseEntity.ok("Permission updated successfully");
    }

    // Видалення доступу
    @DeleteMapping("/permissions/{fileId}/{permissionId}")
    public ResponseEntity<String> deletePermission(
            @PathVariable String fileId,
            @PathVariable String permissionId
    ) throws IOException {
        driveService.deletePermission(fileId, permissionId);
        return ResponseEntity.ok("Permission deleted successfully");
    }
}