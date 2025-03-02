package ua.koziichuk.drive.service.controller;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.koziichuk.drive.service.service.GoogleDriveService;
import ua.koziichuk.drive.service.service.GoogleFormsService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/drive")
public class DriveController {

    @Autowired
    private GoogleDriveService driveService;
    @Autowired
    private GoogleFormsService formsService;

    @PostMapping("/create-folder")
    public ResponseEntity<String> createFolder(@RequestParam String folderName,
                                               @RequestParam List<String> emails,
                                               @RequestParam(defaultValue = "reader") String role) {
        String folderId = null;
        try {
            folderId = driveService.createFolder(folderName);

            for (String email : emails) {
                driveService.shareFolder(folderId, email, role);
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error creating folder");
        }

        return ResponseEntity.ok("Folder created and shared: " + folderId);
    }

    @GetMapping("/permissions/{folderId}")
    public ResponseEntity<List<Permission>> getPermissions(@PathVariable String folderId) {
        List<Permission> permissions = null;
        try {
            permissions = driveService.getFolderPermissions(folderId);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(permissions);
        }
        return ResponseEntity.ok(permissions);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam String folderId) {
        File fileMetadata = new File()
                .setName(file.getOriginalFilename())
                .setParents(Collections.singletonList(folderId));

        try {
            InputStreamContent mediaContent = new InputStreamContent(
                    file.getContentType(),
                    file.getInputStream()
            );

            driveService.uploadFile(fileMetadata, mediaContent);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error uploading file");
        }

        return ResponseEntity.ok("File uploaded successfully");
    }


    // Збереження відповідей з Google Forms у Google Drive
    @PostMapping("/save-responses/{formId}")
    public ResponseEntity<String> saveResponses(@PathVariable String formId) {
        try {
            formsService.saveResponsesToDrive(formId);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error saving responses");
        }
        return ResponseEntity.ok("Responses saved successfully");
    }

}